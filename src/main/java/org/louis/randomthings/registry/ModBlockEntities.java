package org.louis.randomthings.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.louis.randomthings.Randomthings;
import org.louis.randomthings.core.block.entity.BlockMiniChestEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Randomthings.MODID);

    public static final RegistryObject<BlockEntityType<BlockMiniChestEntity>> BLOCK_MINI_CHEST_ENTITY =
            BLOCK_ENTITIES.register("mini_chest_entity",
                    () -> BlockEntityType.Builder.of(BlockMiniChestEntity::new, ModBlocks.BLOCK_MINI_CHEST.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
