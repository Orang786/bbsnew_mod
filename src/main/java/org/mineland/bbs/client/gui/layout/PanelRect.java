package org.mineland.bbs.client.gui.layout;

public class PanelRect {

    public final float x, y, width, height; // относительные координаты 0..1
    public final PanelType type;
    public final boolean hasIcon;

    public PanelRect(float x, float y, float width, float height, PanelType type, boolean hasIcon) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
        this.hasIcon = hasIcon;
    }
}