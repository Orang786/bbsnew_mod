package org.mineland.bbs.client.gui.layout;

public enum PanelType {
    VIEWPORT(0xFFD03838),   // красный - камера/вьюпорт
    TIMELINE(0xFF3A3A3A),   // тёмно-серый
    HIERARCHY(0xFF2A2A2A),  // серый
    PROPERTIES(0xFF4A4A4A); // светло-серый

    public final int color;

    PanelType(int color) {
        this.color = color;
    }
}