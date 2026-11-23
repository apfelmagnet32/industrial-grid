package net.industrialgrid.transformer;

import com.simibubi.create.foundation.block.IBE;
import net.industrialgrid.IndustrialGrid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.patryk3211.powergrid.electricity.base.IDecoratedTerminal;
import org.patryk3211.powergrid.electricity.base.ITerminalPlacement;
import org.patryk3211.powergrid.electricity.base.TerminalBoundingBox;
import org.patryk3211.powergrid.electricity.transformer.TransformerBlock;
import org.patryk3211.powergrid.electricity.transformer.TransformerBlockEntity;

import java.util.Optional;

/**
 * Sibling of PowerGrid's TransformerSmallBlock (single-block transformer, same terminal/shape
 * layout) - see IndustrialTransformerBlockEntity for why it's rated so much higher. The entire
 * coil-winding interaction (right-click with a wire, the turns-count screen, the network packet)
 * is inherited unmodified from TransformerBlock - PowerGrid's winding flow is block-agnostic, it
 * never references a concrete transformer class.
 */
public class IndustrialTransformerBlock extends TransformerBlock implements IBE<IndustrialTransformerBlockEntity> {
    public static final EnumProperty<Direction.Axis> HORIZONTAL_AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    private static final TerminalBoundingBox Z_TERMINAL_1 = new TerminalBoundingBox(IDecoratedTerminal.CONNECTOR, 1, 13, 2, 4, 16, 5);
    private static final TerminalBoundingBox Z_TERMINAL_2 = new TerminalBoundingBox(IDecoratedTerminal.CONNECTOR, 1, 13, 11, 4, 16, 14);
    private static final TerminalBoundingBox Z_TERMINAL_3 = new TerminalBoundingBox(IDecoratedTerminal.CONNECTOR, 12, 13, 2, 15, 16, 5);
    private static final TerminalBoundingBox Z_TERMINAL_4 = new TerminalBoundingBox(IDecoratedTerminal.CONNECTOR, 12, 13, 11, 15, 16, 14);

    private static final TerminalBoundingBox X_TERMINAL_1 = Z_TERMINAL_1.rotateAroundY(Rotation.CLOCKWISE_90);
    private static final TerminalBoundingBox X_TERMINAL_2 = Z_TERMINAL_2.rotateAroundY(Rotation.CLOCKWISE_90);
    private static final TerminalBoundingBox X_TERMINAL_3 = Z_TERMINAL_3.rotateAroundY(Rotation.CLOCKWISE_90);
    private static final TerminalBoundingBox X_TERMINAL_4 = Z_TERMINAL_4.rotateAroundY(Rotation.CLOCKWISE_90);

    private static final VoxelShape SHAPE_Z = Shapes.or(
            box(2, 0, 0, 14, 14, 16),
            Z_TERMINAL_1.getShape(),
            Z_TERMINAL_2.getShape(),
            Z_TERMINAL_3.getShape(),
            Z_TERMINAL_4.getShape()
    );

    private static final VoxelShape SHAPE_X = Shapes.or(
            box(0, 0, 2, 16, 14, 14),
            X_TERMINAL_1.getShape(),
            X_TERMINAL_2.getShape(),
            X_TERMINAL_3.getShape(),
            X_TERMINAL_4.getShape()
    );

    public IndustrialTransformerBlock(Properties settings) {
        super(settings, 1_000_000);
        registerDefaultState(defaultBlockState().setValue(COILS, 0));
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state) {
        return new ItemStack(this);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HORIZONTAL_AXIS, COILS);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(HORIZONTAL_AXIS)) {
            case X -> SHAPE_X;
            case Z -> SHAPE_Z;
            default -> throw new IllegalStateException();
        };
    }

    @Override
    public Optional<TransformerBlockEntity> getBlockEntity(Level world, BlockPos pos, BlockState state) {
        return Optional.ofNullable(getBlockEntity(world, pos));
    }

    @Override
    protected boolean isInitiator(BlockPos pos, BlockState state, BlockPos initiator) {
        return pos.equals(initiator);
    }

    @Override
    public int terminalCount() {
        return 4;
    }

    @Override
    public ITerminalPlacement terminal(BlockState state, int index) {
        return switch (state.getValue(HORIZONTAL_AXIS)) {
            case X -> switch (index) {
                case 0 -> X_TERMINAL_1;
                case 1 -> X_TERMINAL_2;
                case 2 -> X_TERMINAL_3;
                case 3 -> X_TERMINAL_4;
                default -> null;
            };
            case Z -> switch (index) {
                case 0 -> Z_TERMINAL_1;
                case 1 -> Z_TERMINAL_2;
                case 2 -> Z_TERMINAL_3;
                case 3 -> Z_TERMINAL_4;
                default -> null;
            };
            default -> null;
        };
    }

    @Override
    public Class<IndustrialTransformerBlockEntity> getBlockEntityClass() {
        return IndustrialTransformerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends IndustrialTransformerBlockEntity> getBlockEntityType() {
        return IndustrialGrid.INDUSTRIAL_TRANSFORMER_BE.get();
    }
}
