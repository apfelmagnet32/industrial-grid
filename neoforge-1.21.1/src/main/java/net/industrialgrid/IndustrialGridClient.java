package net.industrialgrid;

import com.simibubi.create.content.kinetics.base.ShaftVisual;
import dev.engine_room.flywheel.api.visualization.VisualizerRegistry;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.industrialgrid.breaker.IndustrialBreakerRenderer;
import net.industrialgrid.breaker.IndustrialHvSwitchRenderer;
import net.industrialgrid.gauge.HighGaugeRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = IndustrialGrid.MOD_ID, value = Dist.CLIENT)
public class IndustrialGridClient {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(IndustrialGrid.HIGH_VOLTAGE_GAUGE_BE.get(), HighGaugeRenderer::new);
        event.registerBlockEntityRenderer(IndustrialGrid.HIGH_CURRENT_GAUGE_BE.get(), HighGaugeRenderer::new);
        event.registerBlockEntityRenderer(IndustrialGrid.HIGH_POWER_GAUGE_BE.get(), HighGaugeRenderer::new);
        event.registerBlockEntityRenderer(IndustrialGrid.INDUSTRIAL_BREAKER_BE.get(), IndustrialBreakerRenderer::new);
        event.registerBlockEntityRenderer(IndustrialGrid.INDUSTRIAL_HV_SWITCH_BE.get(), IndustrialHvSwitchRenderer::new);
    }

    /**
     * Registers the Flywheel visuals for our kinetic blocks. Without this, Create's kinetic
     * shaft/cogwheel decoration never appears once Flywheel is active, because it's Flywheel -
     * not the plain BlockEntityRenderer fallback - that normally draws it for every kinetic
     * block. neverSkipVanillaRender() keeps our own renderers running too, since they draw the
     * extra bits (breaker signal lights, switch rod) that ShaftVisual doesn't know about.
     */
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            VisualizerRegistry.setVisualizer(IndustrialGrid.INDUSTRIAL_BREAKER_BE.get(),
                    SimpleBlockEntityVisualizer.builder(IndustrialGrid.INDUSTRIAL_BREAKER_BE.get())
                            .factory(ShaftVisual::new)
                            .neverSkipVanillaRender()
                            .apply());
            VisualizerRegistry.setVisualizer(IndustrialGrid.INDUSTRIAL_HV_SWITCH_BE.get(),
                    SimpleBlockEntityVisualizer.builder(IndustrialGrid.INDUSTRIAL_HV_SWITCH_BE.get())
                            .factory(ShaftVisual::new)
                            .neverSkipVanillaRender()
                            .apply());
        });
    }
}
