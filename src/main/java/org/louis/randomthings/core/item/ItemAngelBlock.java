package org.louis.randomthings.core.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.NotNull;
import org.louis.randomthings.registry.ModBlocks;
import net.minecraft.world.item.Item;

public class ItemAngelBlock extends BlockItem { // Kế thừa từ BlockItem
    public ItemAngelBlock() {
        super(ModBlocks.BLOCK_ANGEL_BLOCK.get(), new Properties());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        if (!pLevel.isClientSide) {
            double x = pPlayer.getX() + pPlayer.getLookAngle().x * 4.5;
            double y = pPlayer.getEyeY() + pPlayer.getLookAngle().y * 4.5;
            double z = pPlayer.getZ() + pPlayer.getLookAngle().z * 4.5;
            BlockPos pos = new BlockPos((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));

            if (pLevel.isInWorldBounds(pos) && pLevel.getBlockState(pos).canBeReplaced()) {
                pLevel.setBlock(pos, ModBlocks.BLOCK_ANGEL_BLOCK.get().defaultBlockState(), 3); // Đặt block

                pLevel.playSound(null, pos, SoundType.STONE.getPlaceSound(),
                        net.minecraft.sounds.SoundSource.BLOCKS,
                        SoundType.STONE.getVolume(),
                        SoundType.STONE.getPitch());

                if (!pPlayer.isCreative()) {
                    if (pUsedHand == InteractionHand.MAIN_HAND) {
                        pPlayer.getInventory().removeFromSelected(false);
                    } else {
                        pPlayer.getInventory().removeItem(Inventory.SLOT_OFFHAND, 1);
                    }
                }
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
