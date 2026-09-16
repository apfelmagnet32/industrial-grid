package net.industrialgrid.gauge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import org.patryk3211.powergrid.collections.ModdedPartialModels;
import org.patryk3211.powergrid.electricity.gauge.GaugeBlockEntity;
import org.patryk3211.powergrid.electricity.gauge.IGaugeBlock;

/**
 * Copy of PowerGrid's own GaugeRenderer with the head-model lookup pointed
 * at our own block-entity classes instead of theirs (their version's
 * instanceof checks only recognise their own classes). Reuses PowerGrid's
 * and Create's existing partial models directly - no new 3D assets needed.
 */
public class HighGaugeRenderer extends SafeBlockEntityRenderer<GaugeBlockEntity> {
    public HighGaugeRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(GaugeBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        var gaugeState = be.getBlockState();

        var headBuffer = CachedBuffers.partial(getHeadModel(be), gaugeState);
        var dialBuffer = CachedBuffers.partial(AllPartialModels.GAUGE_DIAL, gaugeState);

        float progress = Mth.lerp(partialTicks, be.prevDialState, be.dialState);

        for (Direction facing : Iterate.directions) {
            if (!((IGaugeBlock) gaugeState.getBlock()).shouldRenderHeadOnFace(be.getLevel(), be.getBlockPos(), gaugeState, facing))
                continue;

            float dialPivot = 5.75f / 16;
            VertexConsumer vb = buffer.getBuffer(RenderType.solid());
            rotateBufferTowards(dialBuffer, facing).translate(0, dialPivot, dialPivot)
                    .rotate(Direction.EAST.getAxis(), (float) (Math.PI / 2 * -progress))
                    .translate(0, -dialPivot, -dialPivot)
                    .light(light)
                    .renderInto(ms, vb);
            rotateBufferTowards(headBuffer, facing).light(light)
                    .renderInto(ms, vb);
        }
    }

    protected SuperByteBuffer rotateBufferTowards(SuperByteBuffer buffer, Direction target) {
        return buffer.rotateCentered((float) ((-target.toYRot() - 90) / 180 * Math.PI), Direction.UP);
    }

    public static PartialModel getHeadModel(GaugeBlockEntity entity) {
        if(entity instanceof HighPowerGaugeBlockEntity)
            return ModdedPartialModels.CONDUCTIVE_POWER_HEAD;
        else if(entity instanceof HighCurrentGaugeBlockEntity)
            return ModdedPartialModels.CONDUCTIVE_CURRENT_HEAD;
        else
            return ModdedPartialModels.CONDUCTIVE_VOLTAGE_HEAD;
    }
}
