package org.louis.randomthings.core.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.louis.randomthings.Randomthings;
import org.louis.randomthings.base.BaseItem;
import org.louis.randomthings.core.menu.ItemBagOfHoldingMenu;

import java.util.List;

public class ItemBagOfHolding extends BaseItem {
    public ItemBagOfHolding() {
        super(properties -> properties.stacksTo(1)); // Chỉ có thể có 1 item
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide && pUsedHand == InteractionHand.MAIN_HAND) { // Kiểm tra nếu không phải client và sử dụng tay chính
            if (pPlayer instanceof ServerPlayer serverPlayer) {
                // Mở menu cho người chơi trên server
                NetworkHooks.openScreen(serverPlayer, new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("item." + Randomthings.MODID + ".bag_of_holding"); // Tên menu
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
                        // Tạo và trả về menu
                        return new ItemBagOfHoldingMenu(pContainerId, pPlayerInventory, pPlayer.getMainHandItem());
                    }
                }, packetBuffer -> {
                    // Gửi vật phẩm từ tay chính tới server
                    packetBuffer.writeItem(pPlayer.getItemInHand(pUsedHand));
                });
            }
            return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // Kiểm tra nếu Bag of Holding có chứa dữ liệu inventory
        if (stack.hasTag() && stack.getTag().contains("Inventory")) {
            ItemStackHandler inventoryHandler = new ItemStackHandler(54);
            inventoryHandler.deserializeNBT(stack.getTag().getCompound("Inventory"));

            // Số lượng tối đa hiển thị trong tooltip
            final int MAX_DISPLAY = 10;

            // Duyệt qua các slot để hiển thị item
            int displayedCount = 0;
            for (int i = 0; i < inventoryHandler.getSlots() && displayedCount < MAX_DISPLAY; i++) {
                ItemStack itemStack = inventoryHandler.getStackInSlot(i);
                if (!itemStack.isEmpty()) {
                    tooltip.add(Component.literal(itemStack.getCount() + " x " + itemStack.getDisplayName().getString()));
                    displayedCount++;
                }
            }

            // Nếu có nhiều hơn MAX_DISPLAY item, hiển thị thông báo rằng còn nhiều hơn
            if (displayedCount >= MAX_DISPLAY) {
                tooltip.add(Component.literal("...and more"));
            }

        } else {
            tooltip.clear();
        }
    }

}
