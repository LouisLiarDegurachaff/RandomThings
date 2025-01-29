package org.louis.randomthings.core.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import org.louis.randomthings.Randomthings;
import org.louis.randomthings.core.menu.ItemBagOfHoldingMenu;
import org.louis.randomthings.registry.ModMenuTypes;

public class ItemBagOfHolding extends Item {
    public ItemBagOfHolding() {
        super(new Item.Properties().stacksTo(1)); // Chỉ có thể có 1 item
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
}
