package org.louis.randomthings.core.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.louis.randomthings.core.item.ItemBagOfHolding;
import org.louis.randomthings.registry.ModMenuTypes;

public class ItemBagOfHoldingMenu extends AbstractContainerMenu {
    private final ItemStackHandler inventoryHandler;

    // Client Constructor
    public ItemBagOfHoldingMenu(int pContainerId, Inventory pPlayerInventory, FriendlyByteBuf pBuf) {
        this(pContainerId, pPlayerInventory, pBuf.readItem());
    }

    // Server Constructor
    public ItemBagOfHoldingMenu(int pContainerId, Inventory pPlayerInventory, ItemStack backpackItem) {
        super(ModMenuTypes.BAG_OF_HOLDING_MENU.get(), pContainerId);
        this.inventoryHandler = new ItemStackHandler(54); // 54 slot giống Double Chest

        // Deserialize NBT để lấy item trong backpack
        this.inventoryHandler.deserializeNBT(backpackItem.getOrCreateTag().getCompound("Inventory"));

        // Tạo các slot cho backpack (54 slot, giống Double Chest)
        for (int row = 0; row < 6; row++) { // 6 hàng cho backpack
            for (int column = 0; column < 9; column++) { // 9 cột cho mỗi hàng
                this.addSlot(new SlotItemHandler(inventoryHandler, column + row * 9, 8 + column * 18, 18 + row * 18));
            }
        }

        // Điều chỉnh vị trí của inventory và hotbar để tránh chồng lấn
        createPlayerInventory(pPlayerInventory);
        createPlayerHotbar(pPlayerInventory);
    }

    private void createPlayerInventory(Inventory playerInv) {
        int inventoryStartY = 140; // Dời xuống dưới backpack
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInv,
                        9 + column + (row * 9),
                        8 + (column * 18),
                        inventoryStartY + (row * 18))); // Điều chỉnh Y
            }
        }
    }

    private void createPlayerHotbar(Inventory playerInv) {
        int hotbarStartY = 198; // Dời xuống dưới inventory
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInv,
                    column,
                    8 + (column * 18),
                    hotbarStartY) {
                @Override
                public boolean mayPickup(Player pPlayer) {
                    // Nếu item trong hotbar là BagOfHolding, không cho phép lấy ra
                    ItemStack stackInSlot = this.getItem();
                    if (stackInSlot.getItem() instanceof ItemBagOfHolding) {
                        return false; // Không cho phép lấy ra
                    }
                    return super.mayPickup(pPlayer); // Cho phép như bình thường với các item khác
                }

                @Override
                public boolean mayPlace(ItemStack pStack) {
                    // Không cho phép đặt BagOfHolding vào slot hotbar
                    if (pStack.getItem() instanceof ItemBagOfHolding) {
                        return false; // Không cho phép đặt vào hotbar
                    }
                    return super.mayPlace(pStack); // Cho phép đặt các item khác vào
                }
            });
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
        Slot fromSlot = getSlot(pIndex);
        ItemStack fromStack = fromSlot.getItem();

        // Nếu slot rỗng, không làm gì cả
        if (fromStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack copyFromStack = fromStack.copy();

        // Kiểm tra nếu item là BagOfHolding, ngừng di chuyển nó
        if (fromStack.getItem() instanceof ItemBagOfHolding) {
            return ItemStack.EMPTY; // Không cho phép di chuyển BagOfHolding
        }

        if (pIndex < 36) { // Nếu đang trong kho của người chơi
            // Di chuyển item vào slot backpack (slot 36-89)
            if (!moveItemStackTo(fromStack, 36, 90, false)) {
                return ItemStack.EMPTY;
            }
        } else if (pIndex < 90) { // Nếu đang trong slot backpack (slot 36-89)
            // Di chuyển item vào kho người chơi
            if (!moveItemStackTo(fromStack, 0, 36, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY; // Không có slot hợp lệ nào
        }

        // Cập nhật slot và thông báo rằng item đã được lấy
        fromSlot.setChanged();
        fromSlot.onTake(pPlayer, fromStack);

        return copyFromStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        // Lưu lại các item trong backpack vào NBT khi người chơi đóng menu
        ItemStack backpackItem = player.getMainHandItem();
        if (!backpackItem.isEmpty()) {
            // Ghi lại inventory vào NBT
            backpackItem.getOrCreateTag().put("Inventory", this.inventoryHandler.serializeNBT());
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        // Kiểm tra nếu người chơi đang sử dụng ItemBagOfHolding
        ItemStack currentItem = pPlayer.getMainHandItem();
        return !currentItem.isEmpty() && currentItem.getItem() instanceof ItemBagOfHolding;
    }

    public ItemStackHandler getInventory() {
        return this.inventoryHandler;
    }
}
