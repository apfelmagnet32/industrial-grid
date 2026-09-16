package net.industrialgrid.breaker;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import org.patryk3211.powergrid.collections.ModdedPartialModels;

/**
 * Sibling of PowerGrid's HvSwitchRenderer, reusing the same partial models. Always renders
 * through the BER path - no Flywheel Visual is registered for this block entity, so unlike
 * PowerGrid's own version we don't skip rendering when Flywheel is active.
 */
public class IndustrialHvSwitchRenderer extends KineticBlockEntityRenderer<IndustrialHvSwitchBlockEntity> {
    public IndustrialHvSwitchRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(IndustrialHvSwitchBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        var state = be.getBlockState();
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        var facing = state.getValue(IndustrialHvSwitchBlock.HORIZONTAL_FACING);
        var rod = CachedBuffers.partialFacing(ModdedPartialModels.HV_SWITCH_ROD, state, facing);
        float angle = (1.0f - be.rod.getValue(partialTicks)) * (float) Math.PI * 0.5f;
        rod
                .rotateCentered(angle, facing.getClockWise())
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }

    @Override
    protected SuperByteBuffer getRotatedModel(IndustrialHvSwitchBlockEntity be, BlockState state) {
        return CachedBuffers.partialFacingVertical(AllPartialModels.COGWHEEL_SHAFT, state, state.getValue(IndustrialHvSwitchBlock.HORIZONTAL_FACING).getClockWise());
    }
}
