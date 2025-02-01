package org.louis.randomthings.core.menu;

import net.minecraft.nbt.CompoundTag;
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
import org.louis.randomthings.registry.ModMenu;

public class ItemBagOfHoldingMenu extends AbstractContainerMenu {
    private final ItemStackHandler inventoryHandler;

    // Client Constructor
    public ItemBagOfHoldingMenu(int pContainerId, Inventory pPlayerInventory, FriendlyByteBuf pBuf) {
        this(pContainerId, pPlayerInventory, pBuf.readItem());
    }

    /// Server Constructor
    public ItemBagOfHoldingMenu(int pContainerId, Inventory pPlayerInventory, ItemStack backpackItem) {
        super(ModMenu.BAG_OF_HOLDING_MENU.get(), pContainerId);
        this.inventoryHandler = new ItemStackHandler(54); // 54 slot giống Double Chest

        // Deserialize NBT để lấy item trong backpack
        this.inventoryHandler.deserializeNBT(backpackItem.getOrCreateTag().getCompound("Inventory"));

        // Tạo các slot cho backpack (54 slot, giống Double Chest)
        createBackpackSlots();

        // Điều chỉnh vị trí của inventory và hotbar
        createPlayerInventory(pPlayerInventory);
        createPlayerHotbar(pPlayerInventory);
    }

    private void createBackpackSlots() {
        for (int row = 0; row < 6; row++) { // 6 hàng cho backpack
            for (int column = 0; column < 9; column++) { // 9 cột cho mỗi hàng
                addSlot(new SlotItemHandler(inventoryHandler, column + row * 9, 8 + column * 18, 18 + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack pStack) {
                        return !(pStack.getItem() instanceof ItemBagOfHolding) && super.mayPlace(pStack); // Cấm đặt BagOfHolding vào các slot
                    }
                });
            }
        }
    }

    private void createPlayerInventory(Inventory playerInv) {
        int inventoryStartY = 140; // Dời xuống dưới backpack
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInv, 9 + column + (row * 9), 8 + (column * 18), inventoryStartY + (row * 18)) {
                    @Override
                    public boolean mayPlace(ItemStack pStack) {
                        ItemStack heldItem = playerInv.player.getMainHandItem();
                        if (pStack == heldItem && pStack.getItem() instanceof ItemBagOfHolding) {
                            return false;
                        }
                        return super.mayPlace(pStack);
                    }
                });
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
                    // Không cho phép lấy BagOfHolding đang cầm khỏi hotbar
                    ItemStack stackInSlot = this.getItem();
                    ItemStack heldItem = pPlayer.getMainHandItem();
                    if (stackInSlot == heldItem && stackInSlot.getItem() instanceof ItemBagOfHolding) {
                        return false;
                    }
                    return super.mayPickup(pPlayer);
                }

                @Override
                public boolean mayPlace(ItemStack pStack) {
                    // Không cho phép đặt lại BagOfHolding đang cầm vào hotbar
                    ItemStack heldItem = playerInv.player.getMainHandItem();
                    if (pStack == heldItem && pStack.getItem() instanceof ItemBagOfHolding) {
                        return false;
                    }
                    return super.mayPlace(pStack);
                }
            });
        }
    }



    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
        Slot fromSlot = getSlot(pIndex);
        ItemStack fromStack = fromSlot.getItem();
        ItemStack heldItem = pPlayer.getMainHandItem();
        CompoundTag heldItemTag = heldItem.getTag();

        // Nếu slot rỗng hoặc là BagOfHolding mà đang cầm trên tay, không làm gì cả
        if (fromStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (heldItemTag != null && heldItemTag.contains("type") && "BagOfHolding".equals(heldItemTag.getString("type"))) {
            return ItemStack.EMPTY;
        }


        ItemStack copyFromStack = fromStack.copy();

        // Di chuyển item giữa backpack, inventory và hotbar
        if (pIndex < 54) {
            // Nếu không thể di chuyển vào 81-90, thử di chuyển vào 54-81
            if (!moveItemStackTo(fromStack, 80, 89, false)) {
                if (!moveItemStackTo(fromStack, 54, 79, false)) {
                    return ItemStack.EMPTY;
                }
            }
        } else if (pIndex < 90) {
            // Nếu không thể di chuyển vào 0-54, thử di chuyển vào 54-81
            if (!moveItemStackTo(fromStack, 0, 53, false)) {
                if (!moveItemStackTo(fromStack, 54, 80, false)) {
                    return ItemStack.EMPTY;
                }
            }
        } else {
            return ItemStack.EMPTY; // Không có slot hợp lệ nào
        }


        // Lưu các thay đổi vào NBT sau khi di chuyển item vào backpack
        saveInventoryToNBT(pPlayer);

        fromSlot.setChanged();
        fromSlot.onTake(pPlayer, fromStack);

        return copyFromStack;
    }

    // Phương thức lưu lại inventory vào NBT
    private void saveInventoryToNBT(Player pPlayer) {
        ItemStack backpackItem = getCurrentPlayerBackpack(pPlayer);
        if (!backpackItem.isEmpty()) {
            backpackItem.getOrCreateTag().put("Inventory", this.inventoryHandler.serializeNBT());
        }
    }

    // Lấy item hiện tại trong tay của người chơi (backpack)
    private ItemStack getCurrentPlayerBackpack(Player pPlayer) {
        return pPlayer.getMainHandItem(); // Hoặc lấy từ nơi khác nếu cần
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        // Lưu lại các item trong backpack vào NBT khi người chơi đóng menu
        ItemStack backpackItem = getCurrentPlayerBackpack(player);
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
