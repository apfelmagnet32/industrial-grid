package net.industrialgrid.gauge;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.patryk3211.powergrid.electricity.gauge.GaugeBlockEntity;
import org.patryk3211.powergrid.electricity.gauge.GaugeValueBehaviour;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;
import org.patryk3211.powergrid.electricity.sim.node.IElectricNode;
import org.patryk3211.powergrid.utility.Unit;

import java.util.List;

/**
 * Same idea as PowerGrid's own VoltageGaugeBlockEntity, just with a much
 * bigger dial range so it can read creative-source-scale voltages without
 * pegging out. Not a subclass of VoltageGaugeBlockEntity on purpose - its
 * range table and resistance keys are private, so reusing it safely would
 * require duplicating its exact array length; a clean sibling class avoids
 * that mismatch risk entirely.
 */
public class HighVoltageGaugeBlockEntity extends GaugeBlockEntity {
    private static final float[] MAX_VALUES = new float[] { 2_000f, 20_000f, 200_000f, 2_000_000f };
    private static final String[] RESISTANCE_KEYS = new String[] { "range_2kv", "range_20kv", "range_200kv", "range_2000kv" };

    private IElectricNode node1;
    private IElectricNode node2;
    private ElectricWire connection;

    public HighVoltageGaugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        maxValue = MAX_VALUES[0];
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        gaugeValue = new GaugeValueBehaviour(Component.translatable("industrial-grid.devices.gauge.high_voltage"),
                Unit.VOLTAGE.get().component(), MAX_VALUES, this, new BoxTransform());
        gaugeValue.withCallback(i -> {
            setMaxValue(i);
            sendData();
        });
        behaviours.add(gaugeValue);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        setMaxValue(gaugeValue.getValue());
    }

    @Override
    public void tick() {
        var potential = Math.abs(getValue());
        if(potential > maxValue) {
            dialTarget = 1.125f;
        } else {
            dialTarget = potential / maxValue;
        }
        super.tick();
    }

    @Override
    public void buildCircuit(CircuitBuilder builder) {
        builder.setTerminalCount(2);
        node1 = builder.terminalNode(0);
        node2 = builder.terminalNode(1);
        connection = builder.connect(resistance(RESISTANCE_KEYS[0]), node1, node2);
    }

    @Override
    public float getMaxValue() {
        return maxValue;
    }

    private void setMaxValue(int index) {
        maxValue = MAX_VALUES[index];
        if(connection != null) {
            connection.setResistance(resistance(RESISTANCE_KEYS[index]));
        }
    }

    @Override
    public ChatFormatting getColor(float value) {
        return org.patryk3211.powergrid.electricity.gauge.VoltageGaugeBlockEntity.measurementColor(value, maxValue);
    }

    @Override
    public float getValue() {
        return (float) (node1.getVoltage() - node2.getVoltage());
    }

    @Override
    public Unit getUnit() {
        return Unit.VOLTAGE;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        org.patryk3211.powergrid.electricity.gauge.VoltageGaugeBlockEntity.addTooltip(tooltip, getValue(), maxValue);
        return true;
    }
}
