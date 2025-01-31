package org.louis.randomthings.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.ChatFormatting;

public class Tooltip {
    private final String key;
    private final ChatFormatting color;

    public Tooltip(String key) {
        this(key, ChatFormatting.GRAY);
    }

    public Tooltip(String key, ChatFormatting color) {
        this.key = key;
        this.color = color;
    }

    // Thêm tham số vào tooltip (ví dụ: số lần sử dụng)
    public Tooltip args(Object... args) {
        // Xử lý args nếu cần, ví dụ thay thế các tham số trong chuỗi
        return this;
    }

    // Tạo TextComponent từ key và màu sắc
    public Component getTextComponent() {
        return Component.translatable(this.key).setStyle(Style.EMPTY.withColor(this.color));
    }
}
