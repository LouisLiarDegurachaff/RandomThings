package org.louis.randomthings.registry;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.louis.randomthings.Randomthings;
import org.louis.randomthings.core.menu.BlockMiniChestMenu;
import org.louis.randomthings.core.menu.ItemBagOfHoldingMenu;
import org.louis.randomthings.core.menu.generator.BlockFurnaceGeneratorMenu;

public class ModMenu {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Randomthings.MODID);

    public static final RegistryObject<MenuType<BlockMiniChestMenu>> MINI_CHEST_MENU = MENU_TYPES.register("mini_chest",
            () -> IForgeMenuType.create(BlockMiniChestMenu::new));

    public static final RegistryObject<MenuType<ItemBagOfHoldingMenu>> BAG_OF_HOLDING_MENU = MENU_TYPES.register("bag_of_holding",
            () -> IForgeMenuType.create(ItemBagOfHoldingMenu::new));

    public static final RegistryObject<MenuType<BlockFurnaceGeneratorMenu>> FURNACE_GENERATOR = MENU_TYPES.register("furnace_generator",
            () -> IForgeMenuType.create(BlockFurnaceGeneratorMenu::new));

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
