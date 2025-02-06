package org.louis.randomthings.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.louis.randomthings.Randomthings;
import org.louis.randomthings.core.block.*;
import org.louis.randomthings.core.block.generator.BlockFurnaceGenerator;

import java.util.function.Supplier;


public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Randomthings.MODID);

    public static final RegistryObject<Block> BLOCK_ANGEL_BLOCK = BLOCKS.register("angel_block", BlockAngelBlock::new);
    public static final RegistryObject<Block> BLOCK_MINI_CHEST = registerBlock("mini_chest",BlockMiniChest::new);
    public static final RegistryObject<Block> BLOCK_DRUM = BLOCKS.register("drum", BlockDrum::new);
    public static final RegistryObject<Block> BLOCK_MACHINE_BLOCK = registerBlock("machine_block", BlockMachineBlock::new);
    public static final RegistryObject<Block> BLOCK_FURNACE_GENERATOR = registerBlock("furnace_generator", BlockFurnaceGenerator::new);
    public static final RegistryObject<Block> BLOCK_COBBLEGEN = registerBlock("cobblegen_block", BlockCobbelGen::new);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}