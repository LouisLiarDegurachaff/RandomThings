package org.louis.randomthings.core.menu.generator;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.louis.randomthings.core.block.generator.entity.BlockFurnaceGeneratorEntity;
import org.louis.randomthings.registry.ModBlocks;
import org.louis.randomthings.registry.ModMenu;
import org.louis.randomthings.util.generator.FurnaceFuelSlot;

public class BlockFurnaceGeneratorMenu extends AbstractContainerMenu {
    private final BlockFurnaceGeneratorEntity blockEntity;
    private final ContainerLevelAccess levelAccess;
    private final ContainerData data;

    public BlockFurnaceGeneratorMenu(int containerId, Inventory playerInv, FriendlyByteBuf additionalData) {
        this(containerId,
                playerInv,
                playerInv.player.level().getBlockEntity(additionalData.readBlockPos()),
                new SimpleContainerData(4));
    }

    public BlockFurnaceGeneratorMenu(int containerId, Inventory playerInv, BlockEntity blockEntity, ContainerData data) {
        super(ModMenu.FURNACE_GENERATOR.get(), containerId);
        if(blockEntity instanceof BlockFurnaceGeneratorEntity be) {
            this.blockEntity = be;
        } else {
            throw new IllegalStateException("Incorrect block entity class (%s) passed into ExampleEnergyGeneratorMenu!"
                    .formatted(blockEntity.getClass().getCanonicalName()));
        }

        this.levelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.data = data;

        createPlayerHotbar(playerInv);
        createPlayerInventory(playerInv);
        createBlockEntityInventory(be);

        addDataSlots(data);
    }

    private void createBlockEntityInventory(BlockFurnaceGeneratorEntity be) {
        be.getInventoryOptional().ifPresent(inventory ->
                addSlot(new FurnaceFuelSlot(inventory, 0, 62, 36)));
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

        if(fromStack.getCount() <= 0)
            fromSlot.set(ItemStack.EMPTY);

        if(!fromSlot.hasItem())
            return ItemStack.EMPTY;

        ItemStack copyFromStack = fromStack.copy();

        if(pIndex < 36) {
            // We are inside of the player's inventory
            if(!moveItemStackTo(fromStack, 36, 37, false))
                return ItemStack.EMPTY;
        } else if (pIndex < 37) {
            // We are inside of the block entity inventory
            if(!moveItemStackTo(fromStack, 0, 36, false))
                return ItemStack.EMPTY;
        } else {
            System.err.println("Invalid slot index: " + pIndex);
            return ItemStack.EMPTY;
        }

        fromSlot.setChanged();
        fromSlot.onTake(pPlayer, fromStack);

        return copyFromStack;
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return stillValid(this.levelAccess, pPlayer, ModBlocks.BLOCK_FURNACE_GENERATOR.get());
    }

    public BlockFurnaceGeneratorEntity getBlockEntity() {
        return this.blockEntity;
    }

    public int getEnergy() {
        return this.data.get(0);
    }

    public int getMaxEnergy() {
        return this.data.get(1);
    }

    public int getBurnTime() {
        return this.data.get(2);
    }

    public int getMaxBurnTime() {
        return this.data.get(3);
    }

    public int getEnergyStoredScaled() {
        return (int) (((float) getEnergy() / (float) getMaxEnergy()) * 38);
    }
}
