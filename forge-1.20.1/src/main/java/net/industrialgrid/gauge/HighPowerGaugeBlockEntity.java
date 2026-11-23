package net.industrialgrid.gauge;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.patryk3211.powergrid.electricity.base.ThermalBehaviour;
import org.patryk3211.powergrid.electricity.gauge.GaugeBlockEntity;
import org.patryk3211.powergrid.electricity.gauge.GaugeValueBehaviour;
import org.patryk3211.powergrid.electricity.gauge.PowerGaugeBlockEntity;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;
import org.patryk3211.powergrid.electricity.sim.node.IElectricNode;
import org.patryk3211.powergrid.utility.Unit;

import java.util.List;

/**
 * High-range sibling of PowerGrid's PowerGaugeBlockEntity. Uses a single
 * probe resistor (like the current gauge) instead of PowerGrid's separate
 * series+shunt setup, and derives power as |V| x |I| across it - simpler
 * circuit, same physics, much lower risk to get right without an in-game test.
 */
public class HighPowerGaugeBlockEntity extends GaugeBlockEntity {
    private static final float[] MAX_VALUES = new float[] { 20_000f, 200_000f, 2_000_000f, 20_000_000f, 200_000_000f };
    private IElectricNode node1;
    private IElectricNode node2;
    private ElectricWire wire;

    public HighPowerGaugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        maxValue = MAX_VALUES[0];
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        gaugeValue = new GaugeValueBehaviour(Component.translatable("industrialgrid.devices.gauge.high_power"),
                Unit.POWER.get().component(), MAX_VALUES, this, new BoxTransform());
        gaugeValue.withCallback(i -> {
            maxValue = MAX_VALUES[i];
            sendData();
        });
        behaviours.add(gaugeValue);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        maxValue = MAX_VALUES[gaugeValue.getValue()];
    }

    @Override
    public void tick() {
        var power = Math.abs(getValue());
        if(power > maxValue) {
            dialTarget = 1.125f;
        } else {
            dialTarget = power / maxValue;
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
        builder.setTerminalCount(2);
        node1 = builder.terminalNode(0);
        node2 = builder.terminalNode(1);
        wire = builder.connect(resistance(), node1, node2);
    }

    @Override
    public float getMaxValue() {
        return maxValue;
    }

    @Override
    public ChatFormatting getColor(float value) {
        return PowerGaugeBlockEntity.measurementColor(value, maxValue);
    }

    @Override
    public float getValue() {
        var voltage = Math.abs(node1.getVoltage() - node2.getVoltage());
        var current = Math.abs(wire.current());
        return (float) (voltage * current);
    }

    @Override
    public Unit getUnit() {
        return Unit.POWER;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        PowerGaugeBlockEntity.addTooltip(tooltip, getValue(), maxValue);
        return true;
    }
}
