package net.industrialgrid.transformer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.patryk3211.powergrid.electricity.transformer.TransformerBlock;
import org.patryk3211.powergrid.electricity.transformer.TransformerBlockEntity;

/**
 * Sibling of PowerGrid's TransformerSmallBlockEntity - see HighVoltageGaugeBlockEntity for why
 * it's not a subclass. PowerGrid's own transformers physically model voltage step-up via a
 * primary/secondary turns ratio, limited to 60 turns per coil and a modest thermal rating
 * (1000 W / 4.0 kg), which is exactly why a normal transformer dies trying to reach extreme
 * voltages: the stray/mutual inductance loss heats it past the overheat temperature long before
 * you get anywhere near a million volts. This sibling keeps the same physics (coreAl, i.e. the
 * inductance-per-turn^2 constant) but allows vastly more turns per coil (so a huge step-up ratio
 * fits in a single block) and uses a near-ideal coupling factor plus the mod's usual "industrial"
 * thermal headroom (ResistanceValues/ThermalValues) so it doesn't overheat doing it.
 */
public class IndustrialTransformerBlockEntity extends TransformerBlockEntity {
    public IndustrialTransformerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public double coreAl() {
        return 1e-4;
    }

    @Override
    public double couplingFactor() {
        return 0.999999;
    }

    @Override
    public void updateCoilBlockState() {
        assert level != null;
        int coilCount = secondaryCoil.isDefined() ? 2 : primaryCoil.isDefined() ? 1 : 0;
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(TransformerBlock.COILS, coilCount));
    }
}
