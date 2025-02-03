package org.louis.randomthings.core.item.tool;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import org.louis.randomthings.base.tool.BasePickaxeItem;

import java.util.function.Function;


public class ItemDestructionPickaxe extends BasePickaxeItem {

    public ItemDestructionPickaxe(Tier tier) {
        super(tier);
    }

    public ItemDestructionPickaxe(Tier tier, int attackDamage, float attackSpeed, Function<Properties, Properties> properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }
}
