package org.louis.randomthings.core.item;

import net.minecraft.world.item.Item;

public class ItemBagOfHolding extends Item {
    public ItemBagOfHolding() {
        super(new Item.Properties().stacksTo(1)); // Chỉ có thể có 1 item
    }

}
