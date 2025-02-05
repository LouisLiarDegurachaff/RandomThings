package org.louis.randomthings.core.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.louis.randomthings.base.BaseItem;
import org.louis.randomthings.registry.ModTooltips;

import java.util.List;

public class ItemUnstableIngot extends BaseItem {

    private static final int EXPLOSION_TIME = 200;  // Thời gian nổ (200 tick = 10 giây)

    public ItemUnstableIngot() {
        super(properties -> properties);
        // Đăng ký sự kiện
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.register(this);
    }

    @Override
    public Component getName(ItemStack stack) {
        // Kiểm tra nếu item có NBT "isStable"
        if (stack.hasTag() && stack.getTag().contains("isStable")) {
            boolean isStable = stack.getTag().getBoolean("isStable");

            // Nếu isStable là true, tên sẽ là "Stable Ingot" với màu trắng
            if (isStable) {
                return Component.literal("Stable-'Unstable Ingot'")
                        .setStyle(Style.EMPTY.withColor(16777215)); // Màu trắng
            }
        }
        // Nếu không có "isStable" hoặc "isStable" là false, tên là "Unstable Ingot" với màu vàng
        return Component.literal("Unstable Ingot")
                .setStyle(Style.EMPTY.withColor(16776960)); // Màu vàng
    }

    // Khi item được craft (trong crafting table)
    @SubscribeEvent
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        ItemStack craftedItem = event.getCrafting();
            // Thêm NBT "ExplosionTime" và bắt đầu bộ đếm
            if (!craftedItem.hasTag()) {
                craftedItem.getOrCreateTag().putInt("ExplosionTime", EXPLOSION_TIME);  // Đặt thời gian nổ
                craftedItem.getOrCreateTag().putBoolean("isStable", false);  // Đánh dấu là không ổn định
            }

    }

    // Khi item được nhặt lên
    @SubscribeEvent
    public void onItemPickedUp(EntityItemPickupEvent event) {
        // Kiểm tra nếu item là Unstable Ingot
        ItemEntity itemEntity = event.getItem();
        ItemStack pickedUpItem = itemEntity.getItem();

        if (pickedUpItem.getItem() instanceof ItemUnstableIngot) {
            Player player = event.getEntity();

            // Kiểm tra nếu người chơi không ở gần Crafting Table
            if (player.level().getBlockState(player.blockPosition()).getBlock() != Blocks.CRAFTING_TABLE) {
                // Nếu không phải Crafting Table, hủy sự kiện và không cho người chơi lấy item
                event.setCanceled(true);
            }
        }
    }


    @SubscribeEvent
    public void onItemPlaceInContainer(PlayerInteractEvent.RightClickBlock event) {
        if (event.getItemStack().getItem() instanceof ItemUnstableIngot) {
            ItemStack itemStack = event.getItemStack();

            if (!itemStack.hasTag()) {
                // If item does not have NBT tags, add them
                itemStack.getOrCreateTag().putInt("ExplosionTime", EXPLOSION_TIME);  // Set explosion time
                itemStack.getOrCreateTag().putBoolean("isStable", false);  // Mark as unstable
            }
        }
    }

    // Cập nhật bộ đếm mỗi tick
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            for (ItemStack stack : player.getInventory().items) {
                if (stack.getItem() instanceof ItemUnstableIngot && stack.hasTag() && stack.getTag().contains("ExplosionTime")) {
                    // Giảm thời gian còn lại mỗi tick
                    int timeLeft = stack.getTag().getInt("ExplosionTime");
                    if (timeLeft > 0) {
                        stack.getTag().putInt("ExplosionTime", timeLeft - 1);
                    } else {
                        // Kích hoạt nổ khi hết thời gian
                        triggerExplosionEffects(player.level(), player, stack);
                    }
                }
            }
        }
    }

    // Gây nổ và sát thương
    private void triggerExplosionEffects(Level world, Player player, ItemStack stack) {
        if (!world.isClientSide()) {
            // Xóa tất cả các item "Unstable Ingot" trong kho của người chơi
            removeUnstableIngotsFromInventory(player);

            // Phát âm thanh nổ
            world.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE, player.getSoundSource(), 1.0F, 1.0F);

            // Tạo particle nổ
            world.addParticle(ParticleTypes.EXPLOSION, player.getX(), player.getY(), player.getZ(), 0, 0, 0);

            // Tạo sát thương
            if (!player.isDeadOrDying()) {
                player.hurt(player.damageSources().magic(), 20.0F);  // Sát thương
            }
        }
    }

    // Xóa tất cả các item "Unstable Ingot" trong kho của người chơi
    private void removeUnstableIngotsFromInventory(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof ItemUnstableIngot) {
                player.getInventory().setItem(i, ItemStack.EMPTY);
            }
        }
    }


    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

        tooltip.add(ModTooltips.UNSTABLE_INGOT.getTextComponent());
        // Kiểm tra nếu item là Unstable và có NBT "ExplosionTime"
        if (stack.hasTag() && stack.getTag().contains("ExplosionTime")) {
            int timeLeft = stack.getTag().getInt("ExplosionTime");

            // Thêm thông tin về thời gian còn lại của Unstable Ingot và thay đổi màu theo thời gian
            int color = timeLeft > 40 ? 16776960 : (16711680 + ((40 - timeLeft) * 128)); // Tạo màu chuyển từ vàng sang đỏ
            tooltip.add(Component.literal("Explosion Time Left: " + timeLeft + " ticks")
                    .setStyle(Style.EMPTY.withColor(color))); // Đổi màu khi gần hết thời gian
        }
    }
}
