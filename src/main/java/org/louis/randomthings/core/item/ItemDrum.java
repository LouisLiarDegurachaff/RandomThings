package org.louis.randomthings.core.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.louis.randomthings.base.BaseItem;
import org.louis.randomthings.core.block.BlockDrum;
import org.louis.randomthings.registry.ModBlocks;

import java.util.List;

public class ItemDrum extends BlockItem {
    public ItemDrum() {
        super(ModBlocks.BLOCK_DRUM.get(), new Properties());
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        // Kiểm tra nếu stack có tag và có BlockEntityTag
        CompoundTag nbt = stack.getTagElement("BlockEntityTag");
        if (nbt != null) {
            // Kiểm tra nếu FluidTank có tồn tại trong BlockEntityTag
            CompoundTag fluidTag = nbt.getCompound("FluidTank");
            return !fluidTag.isEmpty(); // Nếu FluidTank không rỗng, trả về true
        }
        return false;
    }

    @Override
    public Component getName(ItemStack stack) {
        CompoundTag nbt = stack.getTagElement("BlockEntityTag");
        if (nbt != null && nbt.contains("FluidTank", Tag.TAG_COMPOUND)) {
            // Kiểm tra nếu FluidTank có tồn tại trong BlockEntityTag
            CompoundTag fluidTag = nbt.getCompound("FluidTank");
            FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(fluidTag);
            String fluidName = fluidStack.getDisplayName().getString();

            // Trả về tên với màu xanh biển sáng nếu có chất lỏng
            return Component.literal("Drum " + "("+fluidName+")")
                    .setStyle(Style.EMPTY.withColor(0x1E90FF)); // Màu xanh biển sáng
        }
        // Trả về tên "Drum" mà không có màu
        return Component.literal("Drum");
    }


}