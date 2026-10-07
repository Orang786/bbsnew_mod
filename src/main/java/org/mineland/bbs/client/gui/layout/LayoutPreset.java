package org.mineland.bbs.client.gui.layout;

import java.util.ArrayList;
import java.util.List;

public class LayoutPreset {

    public final String name;
    public final String id;
    public final List<PanelRect> panels = new ArrayList<>();

    public LayoutPreset(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public LayoutPreset addPanel(float x, float y, float width, float height,
                                 PanelType type, boolean hasIcon) {
        panels.add(new PanelRect(x, y, width, height, type, hasIcon));
        return this;
    }
}