package org.louis.randomthings.registry;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class ModTiers {
    public static final Tier UNSTABLE_TIER = new Tier() {
        @Override
        public int getUses() {
            return 2031; // Số lần sử dụng, giống Netherite
        }

        @Override
        public float getSpeed() {
            return 9.0F; // Tốc độ đào, giống Netherite
        }

        @Override
        public float getAttackDamageBonus() {
            return 4.0F; // Damage, giống Netherite
        }

        @Override
        public int getLevel() {
            return 4; // Cấp độ, giống Netherite
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY; // Không thể sửa chữa, không có nguyên liệu sửa chữa
        }

        @Override
        public int getEnchantmentValue() {
            return 22; // Giá trị ma thuật của Unstable Netherite, bạn có thể thay đổi giá trị này theo ý muốn
        }
    };
}
