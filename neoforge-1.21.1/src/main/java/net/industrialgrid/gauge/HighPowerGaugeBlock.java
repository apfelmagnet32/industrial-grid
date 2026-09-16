package net.industrialgrid.gauge;

import net.industrialgrid.IndustrialGrid;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.patryk3211.powergrid.electricity.gauge.GaugeBlock;
import org.patryk3211.powergrid.electricity.info.IHaveElectricProperties;
import org.patryk3211.powergrid.electricity.info.Power;

import java.util.List;

public class HighPowerGaugeBlock extends GaugeBlock<HighPowerGaugeBlockEntity> implements IHaveElectricProperties {
    public HighPowerGaugeBlock(Properties settings) {
        super(settings);
    }

    @Override
    public Class<HighPowerGaugeBlockEntity> getBlockEntityClass() {
        return HighPowerGaugeBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends HighPowerGaugeBlockEntity> getBlockEntityType() {
        return IndustrialGrid.HIGH_POWER_GAUGE_BE.get();
    }

    @Override
    public void appendProperties(ItemStack stack, Player player, List<Component> tooltip) {
        Power.max(200_000_000, player, tooltip);
    }
}
