package org.louis.randomthings.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.louis.randomthings.Randomthings;

public class ModTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Randomthings.MODID);

    public static final RegistryObject<CreativeModeTab> Random_Things_Tab = CREATIVE_MODE_TABS.register("random_things_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModBlocks.BLOCK_ANGEL_BLOCK.get()))
                    .title(Component.translatable("creativetab.random_things_tab"))
                    .displayItems((parameters, output) -> {
                        ModItems.ITEMS.getEntries().forEach(item -> {
                            output.accept(item.get());
                        });
                        ModBlocks.BLOCKS.getEntries().forEach(block-> {
                            output.accept(block.get());
                        });
                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
