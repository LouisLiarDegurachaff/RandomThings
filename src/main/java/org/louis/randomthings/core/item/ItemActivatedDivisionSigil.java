package org.louis.randomthings.core.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import org.louis.randomthings.base.BaseReusableItem;

public class ItemActivatedDivisionSigil extends BaseReusableItem {

    public ItemActivatedDivisionSigil() {
        super(0, properties -> properties.stacksTo(1)); // Item mới sẽ không thể stack
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal("Activated Division Sigil")
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)); // Màu xanh lá
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // Không cần check foil nữa, vì item sẽ thay đổi sau khi kích hoạt
    }

}
