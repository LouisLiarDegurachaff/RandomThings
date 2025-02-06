package org.louis.randomthings.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ImpalingHandler {
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        // Kiểm tra nếu nguồn sát thương là người chơi hoặc mob sử dụng Trident
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            ItemStack weapon = attacker.getMainHandItem(); // Vũ khí chính
            if (weapon.isEnchanted()) {
                // Kiểm tra enchantment Impaling của Vanilla
                int level = weapon.getEnchantmentLevel(Enchantments.IMPALING);
                if (level > 0) {
                    LivingEntity target = event.getEntity();

                    // Nhóm 1: Aquatic mobs (Luôn áp dụng Impaling)
                    if (target instanceof Guardian || target instanceof Drowned ||
                            target instanceof TropicalFish || target instanceof Axolotl ||
                            target instanceof Squid || target instanceof Turtle) {
                        // Tính sát thương thêm cho mỗi cấp độ Impaling (2.5 tương đương 1.25 tim)
                        float additionalDamage = level * 2.5F;
                        event.setAmount(event.getAmount() + additionalDamage);  // Tăng thêm 2.5 cho mỗi cấp độ của Impaling
                    }
                    // Nhóm 2: Mobs phụ thuộc vào điều kiện (ở dưới nước hoặc trời đang mưa)
                    else if (target instanceof LivingEntity) {
                        boolean isInWater = target.isInWater();
                        boolean isRaining = attacker.level().isRainingAt(target.blockPosition());

                        if (isInWater || isRaining) {
                            // Tính sát thương thêm cho mỗi cấp độ Impaling (2.5 tương đương 1.25 tim)
                            float additionalDamage = level * 2.5F;
                            event.setAmount(event.getAmount() + additionalDamage);  // Tăng thêm 2.5 cho mỗi cấp độ của Impaling
                        }
                    }
                }
            }
        }
    }
}
