package net.industrialgrid.gauge;

import net.industrialgrid.IndustrialGrid;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.patryk3211.powergrid.electricity.gauge.GaugeBlock;
import org.patryk3211.powergrid.electricity.info.Current;
import org.patryk3211.powergrid.electricity.info.IHaveElectricProperties;

import java.util.List;

public class HighCurrentGaugeBlock extends GaugeBlock<HighCurrentGaugeBlockEntity> implements IHaveElectricProperties {
    public HighCurrentGaugeBlock(Properties settings) {
        super(settings);
    }

    @Override
    public Class<HighCurrentGaugeBlockEntity> getBlockEntityClass() {
        return HighCurrentGaugeBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends HighCurrentGaugeBlockEntity> getBlockEntityType() {
        return IndustrialGrid.HIGH_CURRENT_GAUGE_BE.get();
    }

    @Override
    public void appendProperties(ItemStack stack, Player player, List<Component> tooltip) {
        Current.max(200_000, player, tooltip);
    }
}
