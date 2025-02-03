package org.louis.randomthings.registry;

import net.minecraft.ChatFormatting;
import org.louis.randomthings.util.Tooltip;

public class ModTooltips {
    // Các tooltip mặc định
    public static final Tooltip USES_LEFT = new Tooltip("tooltip.randomthings.uses_left", ChatFormatting.GREEN);
    public static final Tooltip ONE_USE_LEFT = new Tooltip("tooltip.randomthings.one_use_left", ChatFormatting.RED);
    public static final Tooltip UNLIMITED_USES = new Tooltip("tooltip.randomthings.unlimited_uses", ChatFormatting.GOLD);
    public static final Tooltip UNSTABLE_INGOT = new Tooltip("tooltip.randomthings.unstable_ingot", ChatFormatting.GOLD);
    public static final Tooltip AUTO_REPAIR = new Tooltip("tooltip.randomthings.auto_repair", ChatFormatting.GREEN);
}
