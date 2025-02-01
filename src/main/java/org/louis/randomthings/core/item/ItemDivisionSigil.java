package org.louis.randomthings.core.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.louis.randomthings.base.BaseItem;
import org.louis.randomthings.registry.ModItems;

public class ItemDivisionSigil extends BaseItem {
    public ItemDivisionSigil() {
        super(properties -> properties.stacksTo(1)); // Giữ giá trị mặc định
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();

        if (player != null && player.isShiftKeyDown()) {
            BlockState state = world.getBlockState(pos);

            if (state.is(Blocks.ENCHANTING_TABLE)) {
                if (isSurroundedByRedstone(world, pos)) {
                    if (!world.isClientSide) {
                        activateSigil(player, stack); // Kích hoạt sigil
                    }
                    return InteractionResult.sidedSuccess(world.isClientSide);
                } else {
                    if (!world.isClientSide) {
                        player.sendSystemMessage(Component.literal("You need 8 Redstone Dust around the Enchantment Table!"));
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    private void activateSigil(Player player, ItemStack stack) {
        // Thay thế item cũ bằng item mới (Activated Division Sigil)
        ItemStack newItemStack = new ItemStack(ModItems.ACTIVATED_DIVISION_SIGIL.get());
        player.setItemInHand(player.getUsedItemHand(), newItemStack); // Cập nhật item trong tay người chơi

        // Gửi thông báo
        player.sendSystemMessage(Component.literal("Division Sigil has been activated!"));
    }

    private boolean isSurroundedByRedstone(Level level, BlockPos pos) {
        int redstoneCount = 0;
        BlockPos[] positions = {
                pos.north(), pos.south(), pos.east(), pos.west(),
                pos.north().east(), pos.north().west(), pos.south().east(), pos.south().west()
        };

        for (BlockPos checkPos : positions) {
            if (level.getBlockState(checkPos).is(Blocks.REDSTONE_WIRE)) {
                redstoneCount++;
            }
        }

        return redstoneCount == 8;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal("Division Sigil")
                .setStyle(Style.EMPTY.withColor(0xFFFFFF)); // Màu trắng
    }
}
