package org.louis.randomthings.core.block.generator.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.louis.randomthings.Randomthings;
import org.louis.randomthings.core.menu.generator.BlockFurnaceGeneratorMenu;
import org.louis.randomthings.registry.ModBlockEntities;
import org.louis.randomthings.util.CustomEnergyStorage;
import org.louis.randomthings.util.TickableBlockEntity;

public class BlockFurnaceGeneratorEntity extends BlockEntity implements TickableBlockEntity, MenuProvider {
    private static final Component TITLE =
            Component.translatable("block." + Randomthings.MODID + ".furnace_generator");

    private final ItemStackHandler inventory = new ItemStackHandler(1);
    private final LazyOptional<ItemStackHandler> inventoryOptional = LazyOptional.of(() -> this.inventory);

    private final CustomEnergyStorage energy = new CustomEnergyStorage(10000, 0, 10000, 0);
    private final LazyOptional<CustomEnergyStorage> energyOptional = LazyOptional.of(() -> this.energy);

    private int burnTime = 0, maxBurnTime = 0;

    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex) {
                case 0 -> BlockFurnaceGeneratorEntity.this.energy.getEnergyStored();
                case 1 -> BlockFurnaceGeneratorEntity.this.energy.getMaxEnergyStored();
                case 2 -> BlockFurnaceGeneratorEntity.this.burnTime;
                case 3 -> BlockFurnaceGeneratorEntity.this.maxBurnTime;
                default -> throw new UnsupportedOperationException("Unexpected value: " + pIndex);
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
            switch (pIndex) {
                case 0 -> BlockFurnaceGeneratorEntity.this.energy.setEnergy(pValue);
                case 2 -> BlockFurnaceGeneratorEntity.this.burnTime = pValue;
                case 3 -> BlockFurnaceGeneratorEntity.this.maxBurnTime = pValue;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public BlockFurnaceGeneratorEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.BLOCK_FURNACE_GENERATOR_ENTITY.get(), pPos, pBlockState);
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide())
            return;

        // Nếu năng lượng chưa đầy
        if (this.energy.getEnergyStored() < this.energy.getMaxEnergyStored()) {
            // Nếu không có nhiên liệu đang cháy
            if (this.burnTime <= 0) {
                if (canBurn(this.inventory.getStackInSlot(0))) {
                    // Lấy tổng burn time của nhiên liệu
                    int fuelBurnTime = getBurnTime(this.inventory.getStackInSlot(0));

                    // Đặt lại burnTime và maxBurnTime theo 0.25 × t_fuel
                    this.burnTime = this.maxBurnTime = (int) (0.25 * fuelBurnTime);

                    // Tiêu thụ 1 item nhiên liệu
                    this.inventory.getStackInSlot(0).shrink(1);

                    sendUpdate();
                }
            } else {
                // Đốt nhiên liệu, giảm burnTime
                this.burnTime--;

                // Thêm năng lượng (40 RF/tick)
                this.energy.addEnergy(40);

                sendUpdate();
            }
        }
    }


    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);

        var randomthingsData = new CompoundTag();
        randomthingsData.put("Inventory", this.inventory.serializeNBT());
        randomthingsData.put("Energy", this.energy.serializeNBT());
        randomthingsData.putInt("BurnTime", this.burnTime);
        randomthingsData.putInt("MaxBurnTime", this.maxBurnTime);
        nbt.put(Randomthings.MODID, randomthingsData);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        CompoundTag randomthingsData = nbt.getCompound(Randomthings.MODID);
        if(randomthingsData.isEmpty())
            return;

        if (randomthingsData.contains("Inventory", Tag.TAG_COMPOUND)) {
            this.inventory.deserializeNBT(randomthingsData.getCompound("Inventory"));
        }

        if(randomthingsData.contains("Energy", Tag.TAG_INT)) {
            this.energy.deserializeNBT(randomthingsData.get("Energy"));
        }

        if (randomthingsData.contains("BurnTime", Tag.TAG_INT)) {
            this.burnTime = randomthingsData.getInt("BurnTime");
        }

        if (randomthingsData.contains("MaxBurnTime", Tag.TAG_INT)) {
            this.maxBurnTime = randomthingsData.getInt("MaxBurnTime");
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        saveAdditional(nbt);
        return nbt;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.inventoryOptional.cast();
        } else if (cap == ForgeCapabilities.ENERGY) {
            return this.energyOptional.cast();
        } else {
            return super.getCapability(cap);
        }
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.inventoryOptional.invalidate();
        this.energyOptional.invalidate();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return TITLE;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        return new BlockFurnaceGeneratorMenu(pContainerId, pPlayerInventory, this, this.containerData);
    }

    public LazyOptional<ItemStackHandler> getInventoryOptional() {
        return this.inventoryOptional;
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }

    public LazyOptional<CustomEnergyStorage> getEnergyOptional() {
        return this.energyOptional;
    }

    public CustomEnergyStorage getEnergy() {
        return this.energy;
    }

    private void sendUpdate() {
        setChanged();

        if(this.level != null)
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public int getBurnTime(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
    }

    public boolean canBurn(ItemStack stack) {
        return getBurnTime(stack) > 0;
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
}
