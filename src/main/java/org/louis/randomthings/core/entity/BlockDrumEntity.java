package org.louis.randomthings.core.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.louis.randomthings.core.block.BlockDrum;
import org.louis.randomthings.registry.ModBlockEntities;

public class BlockDrumEntity extends BlockEntity {
    private FluidTank drum;
    private final LazyOptional<IFluidHandler> fluidOptional = LazyOptional.of(() -> drum);

    public BlockDrumEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLOCK_DRUM_ENTITY.get(), pos, state);
        this.drum = new FluidTank(BlockDrum.MAX_CAPACITY) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                BlockDrumEntity.this.sendUpdate();
            }
        };
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.drum.readFromNBT(pTag.getCompound("FluidTank"));
    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("FluidTank", this.drum.writeToNBT(new CompoundTag()));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction direction) {
        if (cap == ForgeCapabilities.FLUID_HANDLER)
            return fluidOptional.cast();
        return super.getCapability(cap, direction);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fluidOptional.invalidate();
    }

    private void sendUpdate() {
        setChanged(); // Đánh dấu rằng block đã thay đổi

        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public FluidTank getFluidTank() {
        return this.drum;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        this.saveAdditional(tag); // Lưu trạng thái của FluidTank
        return tag;
    }

    public void drops() {
        if (level != null && !level.isClientSide()) {
            // Tạo ItemStack cho block drum
            ItemStack itemStack = new ItemStack(this.getBlockState().getBlock().asItem());

            // Lưu dữ liệu BlockEntity vào NBT nếu có
            CompoundTag entityData = new CompoundTag();
            this.saveAdditional(entityData);

            // Kiểm tra nếu có dữ liệu NBT thì gắn vào ItemStack
            if (!entityData.isEmpty()) {
                itemStack.getOrCreateTag().put("BlockEntityTag", entityData);
            }

            // Thả item drum (có hoặc không có NBT)
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), itemStack);
        }
    }
}
