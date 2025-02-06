package org.louis.randomthings.core.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.louis.randomthings.Randomthings;
import org.louis.randomthings.core.menu.BlockMiniChestMenu;
import org.louis.randomthings.registry.ModBlockEntities;

public class BlockMiniChestEntity extends BlockEntity implements MenuProvider {
    private static final Component TITLE =
            Component.translatable("block." + Randomthings.MODID + ".mini_chest");

    private final ItemStackHandler inventory = new ItemStackHandler(1) { // Mini chest có 9 slot
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            BlockMiniChestEntity.this.setChanged();
        }
    };

    private final LazyOptional<ItemStackHandler> optional = LazyOptional.of(() -> this.inventory);

    public BlockMiniChestEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLOCK_MINI_CHEST_ENTITY.get(), pos, state);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("Inventory")) {
            this.inventory.deserializeNBT(nbt.getCompound("Inventory"));
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("Inventory", this.inventory.serializeNBT());
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction direction) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            // Cho phép kết nối với Thermal và các mod tương tự
            return this.optional.cast();
        }
        return super.getCapability(cap, direction);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.optional.invalidate();
    }

    public LazyOptional<ItemStackHandler> getOptional() {
        return this.optional;
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }

    public void drops() {
        if (level != null && !level.isClientSide()) {
            SimpleContainer inventoryContainer = new SimpleContainer(this.inventory.getSlots());
            for (int i = 0; i < this.inventory.getSlots(); i++) {
                inventoryContainer.setItem(i, this.inventory.getStackInSlot(i));
            }
            Containers.dropContents(level, worldPosition, inventoryContainer);
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return TITLE;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        return new BlockMiniChestMenu(pContainerId, pPlayerInventory, this);
    }
}
