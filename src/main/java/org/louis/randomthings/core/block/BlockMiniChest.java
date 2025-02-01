package org.louis.randomthings.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import org.louis.randomthings.base.BaseBlock;
import org.louis.randomthings.core.block.entity.BlockMiniChestEntity;
import org.louis.randomthings.registry.ModBlockEntities;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

@SuppressWarnings("deprecation")
public class BlockMiniChest extends BaseBlock implements EntityBlock,SimpleWaterloggedBlock {
    // Thuộc tính hướng (FACING) để chest có thể xoay ngang
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    // Hình dạng VoxelShape của block, dựa trên JSON model
    private static final VoxelShape SHAPE = Block.box(5, 0, 5, 11, 6, 11);

    public BlockMiniChest() {
        super(properties -> properties.copy(Blocks.CHEST).sound(SoundType.STONE).strength(2).noOcclusion());
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(WATERLOGGED, Boolean.FALSE)
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction direction = pContext.getHorizontalDirection().getOpposite();
        FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());

        return this.defaultBlockState()
                .setValue(FACING, direction)
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER)
        ;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.BLOCK_MINI_CHEST_ENTITY.get().create(pos, state);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof BlockMiniChestEntity && pPlayer instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openScreen(serverPlayer, (BlockMiniChestEntity) entity, pPos);
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof BlockMiniChestEntity) {
                ((BlockMiniChestEntity) blockEntity).drops();
            }
            // Custom particle effects (using block texture)
            if (!pLevel.isClientSide()) {
                BlockParticleOption particleData = new BlockParticleOption(ParticleTypes.BLOCK, pState);
                pLevel.addParticle(particleData, pPos.getX() + 0.5, pPos.getY() + 0.5, pPos.getZ() + 0.5, 0, 0, 0);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public void onPlace(BlockState state, Level pLevel, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (state.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        super.onPlace(state, pLevel, pos, oldState, isMoving);
    }
}