package org.louis.randomthings.base;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class BaseBlockItem extends BlockItem {

    public BaseBlockItem(Block block, Function<Item.Properties, Item.Properties> properties) {
        super(block, properties.apply(new Item.Properties()));
    }

}
