package org.louis.randomthings.registry;

import net.minecraftforge.client.event.EntityRenderersEvent;
import org.louis.randomthings.client.renderer.BlockDrumRenderer;

public class ModRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        // Đăng ký renderer cho BlockEntity
        event.registerBlockEntityRenderer(ModBlockEntities.BLOCK_DRUM_ENTITY.get(), BlockDrumRenderer::new);
    }
}
