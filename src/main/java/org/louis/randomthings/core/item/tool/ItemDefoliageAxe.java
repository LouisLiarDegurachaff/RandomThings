package org.louis.randomthings.core.item.tool;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.louis.randomthings.base.tool.BaseAxeItem;

import java.util.function.Function;

public class ItemDefoliageAxe extends BaseAxeItem {

    private boolean isUndead(LivingEntity entity) {
        return entity instanceof Zombie ||
                entity instanceof Husk ||
                entity instanceof Drowned ||
                entity instanceof WitherSkeleton ||
                entity instanceof ZombieVillager ||
                entity instanceof ZombieHorse ||
                entity instanceof ZombifiedPiglin ||
                entity instanceof Phantom ||
                entity instanceof Stray ||
                entity instanceof Skeleton ||
                entity instanceof SkeletonHorse ||
                entity instanceof Warden ||
                entity instanceof Zoglin;
    }


    public ItemDefoliageAxe(Tier tier) {
        super(tier);
    }

    public ItemDefoliageAxe(Tier tier, int attackDamage, float attackSpeed, Function<Properties, Properties> properties) {
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

    @Override
    public void inventoryTick(@NotNull ItemStack stack, Level level, @NotNull Entity entity, int invSlot, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player) {
            FoodData foodData = player.getFoodData();
            if (foodData.getSaturationLevel() < 5 ) {
                foodData.eat(1, 0.2F);
            }
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (entity instanceof LivingEntity target) {
            // Người chơi sẽ mất một phần năng lượng (exhaustion) mỗi lần nhấp chuột trái
            player.getFoodData().addExhaustion(10.0F);
            // Nếu mục tiêu không phải undead, chặn hoàn toàn sát thương
            if (!isUndead(target)) {
                if (target.isAlive() && target.getHealth() > 0.0F && target.getHealth() < target.getMaxHealth()) {
                    target.heal(2.0F); // Hồi máu cho đối tượng sống

                    // Hiển thị particle hồi máu (HEART)
                    if (player.level().isClientSide) {
                        for (int i = 0; i < 5; i++) {
                            player.level().addParticle(ParticleTypes.HEART,
                                    target.getX() + (player.level().random.nextDouble() - 0.5) * target.getBbWidth(),
                                    target.getY() + player.level().random.nextDouble() * target.getBbHeight() - target.getBbHeight() / 2,
                                    target.getZ() + (player.level().random.nextDouble() - 0.5) * target.getBbWidth(),
                                    1.0F, 0.0F, 0.0F); // Màu đỏ
                        }
                    }
                }
                return true; // Ngăn chặn Minecraft gây sát thương mặc định
            }

            // Nếu là undead, gây sát thương
            float k = 2.0F; // Sát thương cơ bản
            k *= 2.0F; // Tăng sát thương

            // Lấy đối tượng DamageType cho sát thương người chơi
            var damageSource = player.damageSources().playerAttack(player);
            target.hurt(damageSource, k * 4.0F); // Gây sát thương cho đối tượng undead

            return true; // Đối tượng undead bị tấn công và nhận sát thương
        }

        return super.onLeftClickEntity(stack, player, entity);
    }



}
