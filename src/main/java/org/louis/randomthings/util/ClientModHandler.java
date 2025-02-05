package org.louis.randomthings.util;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.louis.randomthings.Randomthings;
import org.louis.randomthings.registry.ModBlocks;
import org.louis.randomthings.registry.ModRenderers;
import org.louis.randomthings.registry.ModScreens;

@Mod.EventBusSubscriber(modid = Randomthings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModHandler {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ModScreens.register();

        ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLOCK_COBBLEGEN.get(), RenderType.translucent());
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        ModRenderers.register(event);
    }
}
