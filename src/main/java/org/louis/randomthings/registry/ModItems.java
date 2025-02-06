package org.louis.randomthings.registry;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.louis.randomthings.Randomthings;
import org.louis.randomthings.core.item.*;
import org.louis.randomthings.core.item.tool.ItemDefoliageAxe;
import org.louis.randomthings.core.item.tool.ItemDestructionPickaxe;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Randomthings.MODID);

    public static final RegistryObject<Item> BAG_OF_HOLDING = ITEMS.register("bag_of_holding", ItemBagOfHolding::new);

    public static final RegistryObject<Item> DIVISION_SIGIL = ITEMS.register("division_sigil", ItemDivisionSigil::new);
    public static final RegistryObject<Item> ACTIVATED_DIVISION_SIGIL = ITEMS.register("activated_division_sigil", ItemActivatedDivisionSigil::new);
    public static final RegistryObject<Item> UNSTABLE_INGOT = ITEMS.register("unstable_ingot", ItemUnstableIngot::new);
    public static final RegistryObject<Item> ANGEL_RING = ITEMS.register("angel_ring", ItemAngelRing::new);

    public static final RegistryObject<Item> ITEM_ANGEL_BLOCK = ITEMS.register("angel_block", ItemAngelBlock::new);
    public static final RegistryObject<Item> ITEM_DRUM = ITEMS.register("drum", ItemDrum::new);

    public static final RegistryObject<Item> DESTRUCTION_PICKAXE = ITEMS.register("destruction_pickaxe",
            () -> new ItemDestructionPickaxe(ModTiers.UNSTABLE_TIER));
    public static final RegistryObject<Item> DEFOLIAGE_AXE = ITEMS.register("defoliage_axe",
            () -> new ItemDefoliageAxe(ModTiers.UNSTABLE_TIER));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}