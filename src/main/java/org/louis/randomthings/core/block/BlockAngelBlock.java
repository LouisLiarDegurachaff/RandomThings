package org.louis.randomthings.core.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.louis.randomthings.registry.ModBlocks;
import org.louis.randomthings.registry.ModItems;

public class BlockAngelBlock extends Block {
    public BlockAngelBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE).sound(SoundType.STONE));
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (!player.isCreative()) {
            player.getInventory().placeItemBackInInventory(ModItems.ITEM_ANGEL_BLOCK.get().getDefaultInstance(), true);
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }
}
