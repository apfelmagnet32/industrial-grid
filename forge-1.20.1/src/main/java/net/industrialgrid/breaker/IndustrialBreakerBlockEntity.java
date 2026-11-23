package net.industrialgrid.breaker;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.patryk3211.powergrid.collections.ModdedSoundEvents;
import org.patryk3211.powergrid.electricity.GlobalElectricNetworks;
import org.patryk3211.powergrid.electricity.base.ThermalBehaviour;
import org.patryk3211.powergrid.electricity.sim.SwitchedWire;
import org.patryk3211.powergrid.kinetics.base.ElectricKineticBlockEntity;

import java.util.List;
import java.util.Locale;

/**
 * Sibling of PowerGrid's HvBreakerBlockEntity (see HighVoltageGaugeBlockEntity for why it's not
 * a subclass) with a fixed set of high-current trip thresholds instead of a free 0-100 A scroll:
 * charge it up to speed via a shaft, then a redstone pulse closes/opens it. While closed, it trips
 * open the instant the current through it exceeds the selected setting.
 */
public class IndustrialBreakerBlockEntity extends ElectricKineticBlockEntity {
    public static final float[] SETTINGS = new float[] { 1_000f, 5_000f, 10_000f, 100_000f, 1_000_000f };

    public static String formatSetting(int index) {
        float amps = SETTINGS[index - 1];
        if (amps >= 1_000_000f) {
            return String.format(Locale.ROOT, "%.0fM A", amps / 1_000_000f);
        } else if (amps >= 1_000f) {
            return String.format(Locale.ROOT, "%.0fk A", amps / 1_000f);
        }
        return String.format(Locale.ROOT, "%.0f A", amps);
    }

    @Nullable
    private SwitchedWire wire;
    private IndustrialBreakerScrollBehaviour setting;

    protected LerpedFloat charge;
    protected boolean prevState, state, wasCharged;
    private boolean redstoneState;
    private int splitCooldown;

    public IndustrialBreakerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        charge = LerpedFloat.linear().startWithValue(0).chase(0, 0, LerpedFloat.Chaser.LINEAR);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        setting = new IndustrialBreakerScrollBehaviour(
                Component.translatable("industrialgrid.devices.industrial_breaker.setting"), this, new Box());
        setting.setValue(0);
        behaviours.add(setting);
    }

    @Override
    public @Nullable ThermalBehaviour specifyThermalBehaviour() {
        return ThermalBehaviour.fromConfig(this);
    }

    @Override
    public void onSpeedChanged(float previousSpeed) {
        super.onSpeedChanged(previousSpeed);
        charge.chase(1, getChaseSpeed(), LerpedFloat.Chaser.LINEAR);
        if (state)
            return;
        sendData();
    }

    private float getChaseSpeed() {
        return Mth.clamp(Math.abs(getSpeed()) / 80 / 20, 0, 1);
    }

    @Override
    public void initialize() {
        assert level != null;
        super.initialize();
        redstoneState = level.getBestNeighborSignal(worldPosition) > 0;
    }

    public void trigger() {
        assert level != null;
        var redstone = level.getBestNeighborSignal(worldPosition) > 0;
        if (redstone && !redstoneState && (charge.getValue() == 1 || state)) {
            state = !state;
            if (wire != null) {
                wire.setState(state);
            } else {
                electricBehaviour.rebuildCircuit(false);
            }
            charge.setValueNoUpdate(state ? 1 : 0);
            (state ? ModdedSoundEvents.BREAKER_ON : ModdedSoundEvents.BREAKER_OFF)
                    .playOnServer(level, worldPosition);
            notifyUpdate();
        }
        redstoneState = redstone;
    }

    @Override
    public void tick() {
        assert level != null;
        applyPower(wire);

        super.tick();
        prevState = state;
        charge.tickChaser();

        if (getChaseSpeed() != 0 && !charge.settled()) {
            int soundRate = (int) (1 / (getChaseSpeed() * 10)) + 1;
            float volume = .25f;
            float pitch = charge.getValue() + 0.5f;
            if (((int) level.getGameTime()) % soundRate == 0)
                level.playSound(null, worldPosition, SoundEvents.WOODEN_BUTTON_CLICK_OFF, SoundSource.BLOCKS,
                        volume, pitch);
            wasCharged = false;
        } else if (charge.getValue() == 1 && !wasCharged) {
            level.playSound(null, worldPosition, SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.BLOCKS,
                    .25f, 1.5f);
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
            wasCharged = true;
        }

        if (wire != null) {
            int index = setting.getValue();
            if (state && index != 0 && !level.isClientSide && wire.isConverged()) {
                if (Math.abs(wire.current()) > SETTINGS[index - 1]) {
                    state = false;
                    wire.setState(false);
                    charge.setValueNoUpdate(0);
                    ModdedSoundEvents.BREAKER_OFF.playOnServer(level, worldPosition);
                    notifyUpdate();
                }
            }
            if (!state && splitCooldown++ >= 100) {
                GlobalElectricNetworks.getWorldNetworks(level).scheduleIslandDiscovery(wire.getNetwork());
                electricBehaviour.rebuildCircuit(false);
            }
        }
    }

    public int getSignal() {
        if (state)
            return 15;
        if (charge.getValue() == 1)
            return 1;
        return 0;
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.put("Charge", charge.writeNBT());
        compound.putBoolean("State", state);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        charge.readNBT(compound.getCompound("Charge"), clientPacket);
        if (compound.contains("Charge"))
            charge.setValueNoUpdate(compound.getCompound("Charge").getFloat("Value"));
        state = compound.getBoolean("State");
        if (wire != null) {
            wire.setState(state);
        } else {
            electricBehaviour.rebuildCircuit(false);
        }
    }

    @Override
    public void buildCircuit(CircuitBuilder builder) {
        builder.setTerminalCount(2);
        if (state) {
            splitCooldown = 0;
            wire = builder.connectSwitch(resistance(), builder.terminalNode(0), builder.terminalNode(1), state);
        } else {
            wire = null;
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        tooltip.add(Component.translatable("industrialgrid.devices.industrial_breaker.status")
                .withStyle(ChatFormatting.GRAY));

        Component line;
        if (state) {
            line = Component.translatable("industrialgrid.devices.industrial_breaker.closed")
                    .withStyle(ChatFormatting.RED);
        } else {
            line = Component.translatable("industrialgrid.devices.industrial_breaker.open")
                    .withStyle(ChatFormatting.GREEN);
        }
        if (charge.getValue() == 1) {
            line = Component.empty().append(line)
                    .append(Component.literal(" - ").withStyle(ChatFormatting.DARK_GRAY))
                    .append(Component.translatable("industrialgrid.devices.industrial_breaker.charged")
                            .withStyle(ChatFormatting.YELLOW));
        }
        tooltip.add(Component.literal(" ").append(line));
        return true;
    }

    public static class Box extends CenteredSideValueBoxTransform {
        public Box() {
            super((state, dir) -> dir == state.getValue(IndustrialBreakerBlock.HORIZONTAL_FACING));
        }

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8.0f, 7.0f, 15.5f);
        }
    }
}
