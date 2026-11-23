package net.industrialgrid.breaker;

import com.google.common.collect.ImmutableMap;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.industrialgrid.IndustrialGrid;
import org.patryk3211.powergrid.config.ResistanceValues;
import org.patryk3211.powergrid.electricity.base.*;
import org.patryk3211.powergrid.electricity.base.terminals.BlockStateTerminalCollection;
import org.patryk3211.powergrid.electricity.info.Current;
import org.patryk3211.powergrid.electricity.info.IHaveElectricProperties;
import org.patryk3211.powergrid.electricity.info.Resistance;

import java.util.List;

/** Sibling of PowerGrid's HvBreakerBlock with 5 fixed high-current trip settings instead of a
 * free 0-100 A scroll range - reuses PowerGrid's hv_breaker model/shape/terminal layout. */
public class IndustrialBreakerBlock extends HorizontalKineticBlock implements IElectric, IBE<IndustrialBreakerBlockEntity>, IHaveElectricProperties {
    private BlockStateTerminalCollection terminals = null;
    private ImmutableMap<BlockState, VoxelShape> outlines = null;

    private static final VoxelShape SHAPE = Shapes.or(
            box(0, 0, 0, 3, 16, 16),
            box(3, 0, 0, 13, 16, 10),
            box(13, 0, 0, 16, 16, 16)
    );

    private static final TerminalBoundingBox[] TERMINALS = new TerminalBoundingBox[] {
            new TerminalBoundingBox(IDecoratedTerminal.CONNECTOR, 6, 1, 14, 10, 5, 16),
            new TerminalBoundingBox(IDecoratedTerminal.CONNECTOR, 6, 11, 14, 10, 15, 16)
    };

    public IndustrialBreakerBlock(Properties properties) {
        super(properties);
        setTerminalCollection(HorizontalElectricBlock.horizontalNorthTerminals(this, TERMINALS, SHAPE));
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING).getClockWise().getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == getRotationAxis(state);
    }

    protected void setTerminalCollection(BlockStateTerminalCollection terminals) {
        this.terminals = terminals;
        var mapper = terminals.shapeMapper();
        if (mapper != null)
            outlines = getShapeForEachState(mapper);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        withBlockEntityDo(level, pos, IndustrialBreakerBlockEntity::trigger);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (outlines != null)
            return outlines.get(state);
        return super.getShape(state, world, pos, context);
    }

    @Override
    public ITerminalPlacement terminal(BlockState state, int index) {
        if (terminals != null)
            return terminals.get(state, index);
        return null;
    }

    @Override
    public int terminalCount() {
        if (terminals != null)
            return terminals.count();
        return 0;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return getBlockEntityOptional(level, pos).map(IndustrialBreakerBlockEntity::getSignal).orElse(0);
    }

    @Override
    public boolean accepts(ItemStack wireStack) {
        return true;
    }

    @Override
    public Class<IndustrialBreakerBlockEntity> getBlockEntityClass() {
        return IndustrialBreakerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends IndustrialBreakerBlockEntity> getBlockEntityType() {
        return IndustrialGrid.INDUSTRIAL_BREAKER_BE.get();
    }

    @Override
    public void appendProperties(ItemStack stack, Player player, List<Component> tooltip) {
        Resistance.series(ResistanceValues.get(this), player, tooltip);
        Current.max(stack, player, tooltip);
    }
}
