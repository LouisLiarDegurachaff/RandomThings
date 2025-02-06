package org.louis.randomthings.core.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.louis.randomthings.core.entity.BlockMiniChestEntity;
import org.louis.randomthings.registry.ModBlocks;
import org.louis.randomthings.registry.ModMenu;

public class BlockMiniChestMenu extends AbstractContainerMenu {
    private final BlockMiniChestEntity blockEntity;
    private final ContainerLevelAccess levelAccess;

    // Client Constructor
    public BlockMiniChestMenu(int containerId, Inventory playerInv, FriendlyByteBuf additionalData) {
        this(containerId, playerInv, playerInv.player.level().getBlockEntity(additionalData.readBlockPos()));
    }

    // Server Constructor
    public BlockMiniChestMenu(int containerId, Inventory playerInv, BlockEntity blockEntity) {
        super(ModMenu.MINI_CHEST_MENU.get(), containerId);
        if(blockEntity instanceof BlockMiniChestEntity be) {
            this.blockEntity = be;
        } else {
            throw new IllegalStateException("Incorrect block entity class (%s) passed into ExampleMenu!"
                    .formatted(blockEntity.getClass().getCanonicalName()));
        }

        this.levelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        createPlayerHotbar(playerInv);
        createPlayerInventory(playerInv);
        createBlockEntityInventory(be);
    }

    private void createBlockEntityInventory(BlockMiniChestEntity be) {
        be.getOptional().ifPresent(inventory -> {
            addSlot(new SlotItemHandler(inventory, 0, 80, 35)); // Tạo đúng 1 slot ở vị trí trung tâm
        });
    }

    private void createPlayerInventory(Inventory playerInv) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInv,
                        9 + column + (row * 9),
                        8 + (column * 18),
                        84 + (row * 18)));
            }
        }
    }

    private void createPlayerHotbar(Inventory playerInv) {
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInv,
                    column,
                    8 + (column * 18),
                    142));
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

        // Nếu chúng ta đang ở trong kho của người chơi (slot < 36)
        if (pIndex < 36) {
            // Di chuyển item vào slot mini chest (slot 36)
            if (!moveItemStackTo(fromStack, 36, 37, false)) {
                return ItemStack.EMPTY;
            }
        } else if (pIndex < 37) {
            // Nếu chúng ta đang ở trong slot mini chest (slot 36), di chuyển item vào kho người chơi
            if (!moveItemStackTo(fromStack, 0, 35, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            // Trường hợp này không bao giờ xảy ra vì mini chest chỉ có một slot (36)
            return ItemStack.EMPTY;
        }

        // Cập nhật slot và thông báo rằng item đã được lấy
        fromSlot.setChanged();
        fromSlot.onTake(pPlayer, fromStack);

        return copyFromStack;
    }


    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return stillValid(this.levelAccess, pPlayer, ModBlocks.BLOCK_MINI_CHEST.get());
    }

    public BlockMiniChestEntity getBlockEntity() {
        return this.blockEntity;
    }
}
