package org.louis.randomthings.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.louis.randomthings.core.block.entity.BlockDrumEntity;

public class BlockDrumRenderer implements BlockEntityRenderer<BlockDrumEntity> {
    private final BlockEntityRendererProvider.Context context;

    public BlockDrumRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
    }

    @Override
    public void render(@NotNull BlockDrumEntity pBlockEntity, float pPartialTick,
                       @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight,
                       int pPackedOverlay) {
        FluidStack fluidStack = pBlockEntity.getFluidTank().getFluid();
        // Kiểm tra nếu tank rỗng thì không render
        if (fluidStack.isEmpty()) return;

        Level level = pBlockEntity.getLevel();
        if (level == null) return;

        BlockPos pos = pBlockEntity.getBlockPos();

        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        ResourceLocation stillTexture = fluidTypeExtensions.getStillTexture(fluidStack);
        if (stillTexture == null) return;

        // Lấy sprite từ texture tĩnh của chất lỏng
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);

        // Lấy các chỉ số UV cố định của sprite
        float u0 = sprite.getU0();
        float v0 = sprite.getV0();
        float u1 = sprite.getU1();
        float v1 = sprite.getV1();

        // Nếu là nước, sử dụng tintColor. Nếu không, giữ tintColor là 0xFFFFFF.
        int tintColor = (fluidStack.getFluid() == Fluids.WATER) ? fluidTypeExtensions.getTintColor(fluidStack.getFluid().defaultFluidState(), level, pos) : 0xFFFFFF;

        // Sử dụng RenderType không trong suốt
        VertexConsumer builder = pBuffer.getBuffer(RenderType.solid());

        // Vẽ các mặt của block drum chứa chất lỏng với các chỉ số UV cố định
        drawQuad(builder, pPoseStack, 0.15f, 0f, 0.15f, 0.85f, 1f, 0.15f, u0, v0, u1, v1, pPackedLight, tintColor);

        pPoseStack.pushPose();
        drawQuad(builder, pPoseStack, 0.85f, 0f, 0.85f, 0.15f, 1f, 0.85f, u0, v0, u1, v1, pPackedLight, tintColor);
        pPoseStack.popPose();

        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YP.rotationDegrees(90));
        pPoseStack.translate(-1f, 0, 0);
        drawQuad(builder, pPoseStack, 0.15f, 0f, 0.15f, 0.85f, 1f, 0.15f, u0, v0, u1, v1, pPackedLight, tintColor);
        pPoseStack.popPose();

        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YN.rotationDegrees(90));
        pPoseStack.translate(0, 0, -1f);
        drawQuad(builder, pPoseStack, 0.15f, 0f, 0.15f, 0.85f, 1f, 0.15f, u0, v0, u1, v1, pPackedLight, tintColor);
        pPoseStack.popPose();
    }




    private static void drawVertex(VertexConsumer builder, PoseStack poseStack, float x, float y, float z, float u, float v, int packedLight, int color) {
        builder.vertex(poseStack.last().pose(), x, y, z)
                .color(color)
                .uv(u, v)
                .uv2(packedLight)
                .normal(1, 0, 0)
                .endVertex();
    }

    private static void drawQuad(VertexConsumer builder, PoseStack poseStack, float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1, int packedLight, int color) {
        drawVertex(builder, poseStack, x0, y0, z0, u0, v0, packedLight, color);
        drawVertex(builder, poseStack, x0, y1, z1, u0, v1, packedLight, color);
        drawVertex(builder, poseStack, x1, y1, z1, u1, v1, packedLight, color);
        drawVertex(builder, poseStack, x1, y0, z0, u1, v0, packedLight, color);
    }
}
