package org.louis.randomthings.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class BaseEntityWaterloggedBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public BaseEntityWaterloggedBlock(Function<Properties, Properties> properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH) // Đặt FACING mặc định là NORTH
                .setValue(WATERLOGGED, Boolean.FALSE)); // WATERLOGGED mặc định là FALSE
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection().getOpposite();
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState()
                .setValue(FACING, direction) // Đặt giá trị FACING
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER); // Đặt WATERLOGGED
    }


    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder); // Kế thừa thuộc tính từ lớp cha, bao gồm FACING
        builder.add(WATERLOGGED); // Chỉ thêm WATERLOGGED, không thêm lại FACING
    }

    @Override
    public void onPlace(BlockState state, Level pLevel, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (state.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        super.onPlace(state, pLevel, pos, oldState, isMoving);
    }

    @Nullable
    @Override
    public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);
}
