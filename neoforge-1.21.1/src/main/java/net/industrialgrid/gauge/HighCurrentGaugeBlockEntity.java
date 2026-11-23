package net.industrialgrid.gauge;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.patryk3211.powergrid.electricity.base.ThermalBehaviour;
import org.patryk3211.powergrid.electricity.gauge.CurrentGaugeBlockEntity;
import org.patryk3211.powergrid.electricity.gauge.GaugeBlockEntity;
import org.patryk3211.powergrid.electricity.gauge.GaugeValueBehaviour;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;
import org.patryk3211.powergrid.utility.Unit;

import java.util.List;

/** High-range sibling of PowerGrid's CurrentGaugeBlockEntity - see HighVoltageGaugeBlockEntity for why it's not a subclass. */
public class HighCurrentGaugeBlockEntity extends GaugeBlockEntity {
    private static final float[] MAX_VALUES = new float[] { 200f, 2_000f, 20_000f, 200_000f, 2_000_000f, 20_000_000f, 200_000_000f };
    private ElectricWire wire;

    public HighCurrentGaugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        maxValue = MAX_VALUES[0];
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        gaugeValue = new GaugeValueBehaviour(Component.translatable("industrialgrid.devices.gauge.high_current"),
                Unit.CURRENT.get().component(), MAX_VALUES, this, new BoxTransform());
        gaugeValue.withCallback(i -> {
            maxValue = MAX_VALUES[i];
            sendData();
        });
        behaviours.add(gaugeValue);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        maxValue = MAX_VALUES[gaugeValue.getValue()];
    }

    @Override
    public void tick() {
        var current = Math.abs(getValue());
        if(current > maxValue) {
            dialTarget = 1.125f;
        } else {
            dialTarget = current / maxValue;
        }
        applyPower(wire);
        super.tick();
    }

    @Override
    public @Nullable ThermalBehaviour specifyThermalBehaviour() {
        return ThermalBehaviour.fromConfig(this);
    }

    @Override
    public void buildCircuit(CircuitBuilder builder) {
        float resistance = resistance();
        builder.setTerminalCount(2);
        wire = builder.connect(resistance, builder.terminalNode(0), builder.terminalNode(1));
    }

    @Override
    public float getMaxValue() {
        return maxValue;
    }

    @Override
    public ChatFormatting getColor(float value) {
        return CurrentGaugeBlockEntity.measurementColor(value, maxValue);
    }

    @Override
    public float getValue() {
        return (float) wire.current();
    }

    @Override
    public Unit getUnit() {
        return Unit.CURRENT;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        CurrentGaugeBlockEntity.addTooltip(tooltip, getValue(), maxValue, false);
        return true;
    }
}
