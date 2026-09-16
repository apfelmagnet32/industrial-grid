package net.industrialgrid.gauge;

import net.industrialgrid.IndustrialGrid;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.patryk3211.powergrid.electricity.gauge.GaugeBlock;
import org.patryk3211.powergrid.electricity.info.IHaveElectricProperties;
import org.patryk3211.powergrid.electricity.info.Voltage;

import java.util.List;

public class HighVoltageGaugeBlock extends GaugeBlock<HighVoltageGaugeBlockEntity> implements IHaveElectricProperties {
    public HighVoltageGaugeBlock(Properties settings) {
        super(settings);
    }

    @Override
    public Class<HighVoltageGaugeBlockEntity> getBlockEntityClass() {
        return HighVoltageGaugeBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends HighVoltageGaugeBlockEntity> getBlockEntityType() {
        return IndustrialGrid.HIGH_VOLTAGE_GAUGE_BE.get();
    }

    @Override
    public void appendProperties(ItemStack stack, Player player, List<Component> tooltip) {
        Voltage.max(2_000_000, player, tooltip);
    }
}
