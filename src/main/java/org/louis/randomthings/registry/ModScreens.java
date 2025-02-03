package org.louis.randomthings.registry;

import net.minecraft.client.gui.screens.MenuScreens;
import org.louis.randomthings.core.screen.BlockMiniChestScreen;
import org.louis.randomthings.core.screen.ItemBagOfHoldingScreen;

public class ModScreens {
    public static void register() {
        // Đăng ký tất cả các màn hình ở đây
        MenuScreens.register(ModMenu.MINI_CHEST_MENU.get(), BlockMiniChestScreen::new);
        MenuScreens.register(ModMenu.BAG_OF_HOLDING_MENU.get(), ItemBagOfHoldingScreen::new);
    }
}
