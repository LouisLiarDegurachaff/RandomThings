package org.louis.randomthings.base;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import org.louis.randomthings.registry.ModTooltips;

import java.util.List;
import java.util.function.Function;

public class BaseReusableItem extends BaseItem {
    private static final RegistryObject<Enchantment> UNBREAKING_ENCHANTMENT = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, "minecraft")
            .register("unbreaking", () -> Enchantments.UNBREAKING);
    private final boolean damage;
    private final boolean tooltip;

    // Constructor mặc định khi không có tooltip
    public BaseReusableItem(int uses) {
        this(uses, p -> p);  // Truyền một Function<Item.Properties, Item.Properties> đơn giản
    }

    // Constructor mặc định với Function<Item.Properties, Item.Properties>
    public BaseReusableItem(Function<Properties, Properties> properties) {
        this(true, properties);
    }

    // Constructor với tooltip flag và Function<Item.Properties, Item.Properties>
    public BaseReusableItem(boolean tooltip, Function<Properties, Properties> properties) {
        this(0, tooltip, properties);
    }

    // Constructor với số lượng sử dụng và Function<Item.Properties, Item.Properties>
    public BaseReusableItem(int uses, Function<Properties, Properties> properties) {
        this(uses, true, properties);
    }

    // Constructor chính, nơi ta sẽ truyền Function<Item.Properties, Item.Properties> vào
    public BaseReusableItem(int uses, boolean tooltip, Function<Properties, Properties> properties) {
        super(p -> properties.apply(new Properties()));  // Truyền Function<Item.Properties, Item.Properties> vào
        this.damage = uses > 0;
        this.tooltip = tooltip;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        var copy = stack.copyWithCount(1);

        if (!this.damage) return copy;

        var unbreaking = stack.getEnchantmentLevel(UNBREAKING_ENCHANTMENT.get());
        if (Math.random() > (1.0F / (unbreaking + 1)))
            return copy;

        copy.setDamageValue(stack.getDamageValue() + 1);

        if (copy.getDamageValue() >= stack.getMaxDamage())
            return ItemStack.EMPTY;

        return copy;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
        if (this.tooltip) {
            if (this.damage) {
                // Tính số lần sử dụng còn lại
                int damage = pStack.getMaxDamage() - pStack.getDamageValue() + 1;

                if (damage == 1) {
                    tooltip.add(ModTooltips.ONE_USE_LEFT.getTextComponent());
                } else {
                    tooltip.add(ModTooltips.USES_LEFT.args(damage).getTextComponent());
                }
            } else {
                tooltip.add(ModTooltips.UNLIMITED_USES.getTextComponent());
            }
        }
    }
}
