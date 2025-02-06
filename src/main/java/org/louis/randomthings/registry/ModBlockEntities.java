package org.louis.randomthings.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.louis.randomthings.Randomthings;
import org.louis.randomthings.core.entity.BlockCobbleGenEntity;
import org.louis.randomthings.core.entity.BlockDrumEntity;
import org.louis.randomthings.core.entity.BlockMiniChestEntity;
import org.louis.randomthings.core.block.generator.entity.BlockFurnaceGeneratorEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Randomthings.MODID);

    public static final RegistryObject<BlockEntityType<BlockMiniChestEntity>> BLOCK_MINI_CHEST_ENTITY =
            BLOCK_ENTITIES.register("mini_chest",
                    () -> BlockEntityType.Builder.of(BlockMiniChestEntity::new, ModBlocks.BLOCK_MINI_CHEST.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockDrumEntity>> BLOCK_DRUM_ENTITY =
            BLOCK_ENTITIES.register("drum",
                    () -> BlockEntityType.Builder.of(BlockDrumEntity::new, ModBlocks.BLOCK_DRUM.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockFurnaceGeneratorEntity>> BLOCK_FURNACE_GENERATOR_ENTITY =
            BLOCK_ENTITIES.register("furnace_generator",
                    () -> BlockEntityType.Builder.of(BlockFurnaceGeneratorEntity::new, ModBlocks.BLOCK_FURNACE_GENERATOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockCobbleGenEntity>> BLOCK_COBBLEGEN_ENTITY =
            BLOCK_ENTITIES.register("cobblegen_block",
                    () -> BlockEntityType.Builder.of(BlockCobbleGenEntity::new, ModBlocks.BLOCK_COBBLEGEN.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
