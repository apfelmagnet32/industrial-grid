package net.industrialgrid;

import net.industrialgrid.breaker.IndustrialBreakerBlock;
import net.industrialgrid.breaker.IndustrialBreakerBlockEntity;
import net.industrialgrid.breaker.IndustrialHvSwitchBlock;
import net.industrialgrid.breaker.IndustrialHvSwitchBlockEntity;
import net.industrialgrid.gauge.HighCurrentGaugeBlock;
import net.industrialgrid.gauge.HighCurrentGaugeBlockEntity;
import net.industrialgrid.gauge.HighPowerGaugeBlock;
import net.industrialgrid.gauge.HighPowerGaugeBlockEntity;
import net.industrialgrid.gauge.HighVoltageGaugeBlock;
import net.industrialgrid.gauge.HighVoltageGaugeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.patryk3211.powergrid.config.ResistanceValues;
import org.patryk3211.powergrid.config.ThermalValues;

@Mod(IndustrialGrid.MOD_ID)
public class IndustrialGrid {
    public static final String MOD_ID = "industrialgrid";

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);

    public static final RegistryObject<Item> INDUSTRIAL_CABLE = ITEMS.register(
            "industrial_cable", () -> new IndustrialCableItem(new Item.Properties()));

    private static BlockBehaviour.Properties gaugeProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(2.0f, 6.0f)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops();
    }

    public static final RegistryObject<HighVoltageGaugeBlock> HIGH_VOLTAGE_GAUGE =
            BLOCKS.register("high_voltage_gauge", () -> new HighVoltageGaugeBlock(gaugeProperties()));
    public static final RegistryObject<HighCurrentGaugeBlock> HIGH_CURRENT_GAUGE =
            BLOCKS.register("high_current_gauge", () -> new HighCurrentGaugeBlock(gaugeProperties()));
    public static final RegistryObject<HighPowerGaugeBlock> HIGH_POWER_GAUGE =
            BLOCKS.register("high_power_gauge", () -> new HighPowerGaugeBlock(gaugeProperties()));
    public static final RegistryObject<IndustrialBreakerBlock> INDUSTRIAL_BREAKER =
            BLOCKS.register("industrial_breaker", () -> new IndustrialBreakerBlock(gaugeProperties()));
    public static final RegistryObject<IndustrialHvSwitchBlock> INDUSTRIAL_HV_SWITCH =
            BLOCKS.register("industrial_hv_switch", () -> new IndustrialHvSwitchBlock(gaugeProperties()));

    public static final RegistryObject<BlockItem> HIGH_VOLTAGE_GAUGE_ITEM = ITEMS.register(
            "high_voltage_gauge", () -> new BlockItem(HIGH_VOLTAGE_GAUGE.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> HIGH_CURRENT_GAUGE_ITEM = ITEMS.register(
            "high_current_gauge", () -> new BlockItem(HIGH_CURRENT_GAUGE.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> HIGH_POWER_GAUGE_ITEM = ITEMS.register(
            "high_power_gauge", () -> new BlockItem(HIGH_POWER_GAUGE.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> INDUSTRIAL_BREAKER_ITEM = ITEMS.register(
            "industrial_breaker", () -> new BlockItem(INDUSTRIAL_BREAKER.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> INDUSTRIAL_HV_SWITCH_ITEM = ITEMS.register(
            "industrial_hv_switch", () -> new BlockItem(INDUSTRIAL_HV_SWITCH.get(), new Item.Properties()));

    public static final RegistryObject<BlockEntityType<HighVoltageGaugeBlockEntity>> HIGH_VOLTAGE_GAUGE_BE =
            BLOCK_ENTITIES.register("high_voltage_gauge", () -> BlockEntityType.Builder.of(
                    IndustrialGrid::createHighVoltageGauge, HIGH_VOLTAGE_GAUGE.get()).build(null));
    public static final RegistryObject<BlockEntityType<HighCurrentGaugeBlockEntity>> HIGH_CURRENT_GAUGE_BE =
            BLOCK_ENTITIES.register("high_current_gauge", () -> BlockEntityType.Builder.of(
                    IndustrialGrid::createHighCurrentGauge, HIGH_CURRENT_GAUGE.get()).build(null));
    public static final RegistryObject<BlockEntityType<HighPowerGaugeBlockEntity>> HIGH_POWER_GAUGE_BE =
            BLOCK_ENTITIES.register("high_power_gauge", () -> BlockEntityType.Builder.of(
                    IndustrialGrid::createHighPowerGauge, HIGH_POWER_GAUGE.get()).build(null));
    public static final RegistryObject<BlockEntityType<IndustrialBreakerBlockEntity>> INDUSTRIAL_BREAKER_BE =
            BLOCK_ENTITIES.register("industrial_breaker", () -> BlockEntityType.Builder.of(
                    IndustrialGrid::createIndustrialBreaker, INDUSTRIAL_BREAKER.get()).build(null));
    public static final RegistryObject<BlockEntityType<IndustrialHvSwitchBlockEntity>> INDUSTRIAL_HV_SWITCH_BE =
            BLOCK_ENTITIES.register("industrial_hv_switch", () -> BlockEntityType.Builder.of(
                    IndustrialGrid::createIndustrialHvSwitch, INDUSTRIAL_HV_SWITCH.get()).build(null));

    private static HighVoltageGaugeBlockEntity createHighVoltageGauge(BlockPos pos, BlockState state) {
        return new HighVoltageGaugeBlockEntity(HIGH_VOLTAGE_GAUGE_BE.get(), pos, state);
    }

    private static HighCurrentGaugeBlockEntity createHighCurrentGauge(BlockPos pos, BlockState state) {
        return new HighCurrentGaugeBlockEntity(HIGH_CURRENT_GAUGE_BE.get(), pos, state);
    }

    private static HighPowerGaugeBlockEntity createHighPowerGauge(BlockPos pos, BlockState state) {
        return new HighPowerGaugeBlockEntity(HIGH_POWER_GAUGE_BE.get(), pos, state);
    }

    private static IndustrialBreakerBlockEntity createIndustrialBreaker(BlockPos pos, BlockState state) {
        return new IndustrialBreakerBlockEntity(INDUSTRIAL_BREAKER_BE.get(), pos, state);
    }

    private static IndustrialHvSwitchBlockEntity createIndustrialHvSwitch(BlockPos pos, BlockState state) {
        return new IndustrialHvSwitchBlockEntity(INDUSTRIAL_HV_SWITCH_BE.get(), pos, state);
    }

    public IndustrialGrid() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        modEventBus.addListener(this::addCreative);
        registerElectricalValues();
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(INDUSTRIAL_CABLE.get());
            event.accept(HIGH_VOLTAGE_GAUGE_ITEM.get());
            event.accept(HIGH_CURRENT_GAUGE_ITEM.get());
            event.accept(HIGH_POWER_GAUGE_ITEM.get());
            event.accept(INDUSTRIAL_BREAKER_ITEM.get());
            event.accept(INDUSTRIAL_HV_SWITCH_ITEM.get());
        }
    }

    private void registerElectricalValues() {
        ResistanceValues.register(new ResistanceValues.Provider() {
            @Override
            public java.util.function.DoubleSupplier get(Block block) {
                if (block == HIGH_CURRENT_GAUGE.get())
                    return () -> 0.05;
                if (block == HIGH_POWER_GAUGE.get())
                    return () -> 0.05;
                if (block == INDUSTRIAL_BREAKER.get())
                    return () -> 1e-6;
                if (block == INDUSTRIAL_HV_SWITCH.get())
                    return () -> 1e-9;
                return null;
            }

            @Override
            public java.util.function.DoubleSupplier get(Block block, String suffix) {
                if (block == HIGH_VOLTAGE_GAUGE.get()) {
                    return switch (suffix) {
                        case "range_2kv" -> () -> 2e10;
                        case "range_20kv" -> () -> 2e11;
                        case "range_200kv" -> () -> 2e12;
                        case "range_2000kv" -> () -> 2e13;
                        default -> null;
                    };
                }
                return null;
            }
        });

        ThermalValues.register(new ThermalValues.Provider() {
            @Override
            public java.util.function.DoubleSupplier getPower(Block block) {
                if (block == HIGH_CURRENT_GAUGE.get() || block == HIGH_POWER_GAUGE.get())
                    return () -> 1_000_000_000.0;
                if (block == INDUSTRIAL_BREAKER.get())
                    return () -> 1e9;
                if (block == INDUSTRIAL_HV_SWITCH.get())
                    return () -> 1e9;
                return null;
            }

            @Override
            public java.util.function.DoubleSupplier getMass(Block block) {
                if (block == HIGH_CURRENT_GAUGE.get() || block == HIGH_POWER_GAUGE.get())
                    return () -> 2.0;
                if (block == INDUSTRIAL_BREAKER.get())
                    return () -> 1e12;
                if (block == INDUSTRIAL_HV_SWITCH.get())
                    return () -> 1e6;
                return null;
            }
        });
    }
}
