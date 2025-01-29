package org.louis.randomthings.core.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.louis.randomthings.Randomthings;
import org.louis.randomthings.core.menu.BlockMiniChestMenu;

public class BlockMiniChestScreen extends AbstractContainerScreen<BlockMiniChestMenu> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Randomthings.MODID, "textures/gui/mini_chest_menu.png");
    public BlockMiniChestScreen(BlockMiniChestMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        renderBackground(guiGraphics);
        guiGraphics.blit( TEXTURE, this.leftPos, this.topPos, 0,0,this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics,pMouseX,pMouseY);

    }
}
