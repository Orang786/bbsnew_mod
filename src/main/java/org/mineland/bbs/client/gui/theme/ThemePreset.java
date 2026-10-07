package org.mineland.bbs.client.gui.theme;

public class ThemePreset {

    public final String name;
    public final int primaryColor;
    public final int secondaryColor;
    public final int iconColor;
    public final int glowLeftColor;
    public final int glowRightColor;

    public ThemePreset(String name, int primaryColor, int secondaryColor, int iconColor,
                       int glowLeftColor, int glowRightColor) {
        this.name = name;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.iconColor = iconColor;
        this.glowLeftColor = glowLeftColor;
        this.glowRightColor = glowRightColor;
    }

    public static String toHex(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }
}