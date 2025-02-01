package org.louis.randomthings.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;

public class ModConfigs {
    public static final ForgeConfigSpec CLIENT;
    public static final ForgeConfigSpec COMMON;

    public static final ForgeConfigSpec.BooleanValue ANIMATED_GROWTH_ACCELERATORS;

    static {
        final var client = new ForgeConfigSpec.Builder();

        client.comment("General configuration options.").push("General");
        ANIMATED_GROWTH_ACCELERATORS = client
                .comment("Should Growth Accelerators use animated textures?")
                .define("animatedGrowthAccelerators", true);
        client.pop();

        CLIENT = client.build();
    }

    // Common
    static {
        final var common = new ForgeConfigSpec.Builder();


        COMMON = common.build();
    }

    public static boolean isTheOneProbeInstalled() {
        return ModList.get().isLoaded("theoneprobe");
    }
}
