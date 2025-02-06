package org.louis.randomthings.core.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.louis.randomthings.registry.ModBlockEntities;
import org.louis.randomthings.util.TickableBlockEntity;

public class BlockCobbleGenEntity extends BlockEntity implements TickableBlockEntity {
    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            BlockCobbleGenEntity.this.setChanged();
        }
    };

    private final LazyOptional<ItemStackHandler> optional = LazyOptional.of(() -> this.inventory);
    private int tickCounter = 0;

    public BlockCobbleGenEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLOCK_COBBLEGEN_ENTITY.get(), pos, state); // Sửa lại tên entity block
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
            // Cho phép kết nối nhưng chỉ xuất item
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
    public void tick() {
        if (!level.isClientSide()) {
            tickCounter++; // Tăng tickCounter mỗi lần tick

            // Sau 40 tick, thêm 1 cobblestone vào inventory
            if (tickCounter >= 40) {
                ItemStack stackInSlot = this.inventory.getStackInSlot(0);

                if (stackInSlot.isEmpty()) {
                    // Nếu slot trống, thêm 1 cobblestone
                    this.inventory.setStackInSlot(0, new ItemStack(Items.COBBLESTONE, 1));
                } else if (stackInSlot.getCount() < 32) {
                    // Nếu số lượng cobblestone ít hơn 32, tăng số lượng
                    stackInSlot.grow(1);
                }
                pushItemToTop();
                tickCounter = 0; // Reset lại đếm tick
                setChanged(); // Đánh dấu thay đổi
            }
        }
    }

    private void pushItemToTop() {
        ItemStack stack = this.inventory.getStackInSlot(0);
        if (stack.isEmpty()) {
            // Nếu không có item, không làm gì
            return;
        }

        // Lấy BlockEntity của block phía trên
        BlockEntity blockAbove = level.getBlockEntity(worldPosition.above());
        if (blockAbove != null) {
            // Kiểm tra xem block phía trên có hỗ trợ IItemHandler không
            LazyOptional<IItemHandler> handler = blockAbove.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.DOWN);
            if (handler.isPresent()) {
                // Cố gắng đẩy item lên block phía trên
                IItemHandler itemHandler = handler.orElse(null);
                ItemStack result = ItemHandlerHelper.insertItemStacked(itemHandler, stack, false);

                // Kiểm tra nếu item đã được chuyển đi
                if (result.isEmpty()) {
                    // Nếu item đã được chấp nhận, reset slot
                    this.inventory.setStackInSlot(0, ItemStack.EMPTY);
                } else if (result.getCount() != stack.getCount()) {
                    // Nếu có sự thay đổi số lượng, cập nhật lại inventory
                    this.inventory.setStackInSlot(0, result);
                }
            }
        }
    }
}
