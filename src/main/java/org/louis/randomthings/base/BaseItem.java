package org.louis.randomthings.base;

import net.minecraft.world.item.Item;

import java.util.function.Function;

public class BaseItem extends Item {
    public BaseItem() {
        super(new Properties());
    }

    public BaseItem(Function<Properties, Properties> properties) {
        super(properties.apply(new Properties()));
    }
}
