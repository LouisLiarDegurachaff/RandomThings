package org.louis.randomthings.core.block;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.louis.randomthings.base.BaseEntityBlock;
import org.louis.randomthings.core.block.entity.BlockDrumEntity;
import org.louis.randomthings.core.block.entity.BlockMiniChestEntity;
import org.louis.randomthings.registry.ModBlockEntities;

import java.util.List;

public class BlockDrum extends BaseEntityBlock {
    private static final VoxelShape SHAPE = Shapes.box(
        0.149,
        0.0,
        0.149,
        0.851,
        1.0,
        0.851
    );

    public static final int MAX_CAPACITY = 256000;

    public BlockDrum() {
        super(properties -> properties.sound(SoundType.METAL).strength(2, 6));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        // Kiểm tra nếu block được phá không phải là block của chế độ sáng tạo
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            // Kiểm tra người chơi có phải là chế độ sáng tạo không
            if (pLevel instanceof ServerLevel serverLevel) {
                serverLevel.getServer().getPlayerList().getPlayers().forEach(player -> {
                    if (player instanceof Player && !player.isCreative()) {
                        if (blockEntity instanceof BlockDrumEntity) {
                            ((BlockDrumEntity) blockEntity).drops();
                        }
                    }
                });
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        CompoundTag nbt = stack.getTagElement("BlockEntityTag");

        String fluidName = "Empty";
        int amount = 0;

        if (nbt != null) {
            CompoundTag fluidTag = nbt.getCompound("FluidTank");
            FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(fluidTag);
            fluidName = fluidStack.getDisplayName().getString();
            amount = fluidTag.getInt("Amount");

            tooltip.add(Component.translatable("tooltip.randomthings.drum_fluid", fluidName));
            tooltip.add(Component.translatable("tooltip.randomthings.drum_amount", amount, BlockDrum.MAX_CAPACITY));
        }

        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);

        if (FluidUtil.interactWithFluidHandler(player, hand, world, pos, hit.getDirection()) ||
                held.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()) {
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.BLOCK_DRUM_ENTITY.get().create(pos, state);
    }

}
