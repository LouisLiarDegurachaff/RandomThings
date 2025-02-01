package org.louis.randomthings.core.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.louis.randomthings.core.menu.ItemBagOfHoldingMenu;

public class ItemBagOfHoldingScreen extends AbstractContainerScreen<ItemBagOfHoldingMenu> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    public ItemBagOfHoldingScreen(ItemBagOfHoldingMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        renderBackground(guiGraphics);
        guiGraphics.blit( TEXTURE, this.leftPos, this.topPos, 0,0,this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        // Hiển thị tiêu đề của túi Bag of Holding
        pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);

        // Không vẽ tiêu đề "Inventory" của người chơi
        pGuiGraphics.drawString(this.font, this.playerInventoryTitle, this.titleLabelX, 128, 4210752, false);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics,pMouseX,pMouseY);

    }
}
