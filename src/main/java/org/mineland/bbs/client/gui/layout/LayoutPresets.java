package org.mineland.bbs.client.gui.layout;

import java.util.ArrayList;
import java.util.List;

public class LayoutPresets {

    public static final LayoutPreset DEFAULT = new LayoutPreset("default", "Default")
            .addPanel(0f, 0f, 0.12f, 0.75f, PanelType.HIERARCHY, false)
            .addPanel(0.12f, 0f, 0.88f, 0.75f, PanelType.VIEWPORT, true)
            .addPanel(0f, 0.75f, 1f, 0.25f, PanelType.TIMELINE, false);

    public static final LayoutPreset BBS_OLD = new LayoutPreset("bbs_old", "BBS (old)")
            .addPanel(0f, 0f, 0.25f, 0.75f, PanelType.HIERARCHY, false)
            .addPanel(0.25f, 0f, 0.75f, 0.75f, PanelType.VIEWPORT, true)
            .addPanel(0f, 0.75f, 1f, 0.25f, PanelType.TIMELINE, false);

    public static final LayoutPreset MINI = new LayoutPreset("mini", "Mini")
            .addPanel(0f, 0f, 1f, 0.82f, PanelType.VIEWPORT, true)
            .addPanel(0f, 0.82f, 1f, 0.18f, PanelType.TIMELINE, false);

    public static final LayoutPreset SHORTS = new LayoutPreset("shorts", "Shorts")
            .addPanel(0f, 0f, 0.15f, 1f, PanelType.HIERARCHY, false)
            .addPanel(0.15f, 0f, 0.65f, 0.85f, PanelType.VIEWPORT, true)
            .addPanel(0.15f, 0.85f, 0.65f, 0.15f, PanelType.TIMELINE, false)
            .addPanel(0.8f, 0f, 0.2f, 1f, PanelType.PROPERTIES, false);

    public static final List<LayoutPreset> ALL = new ArrayList<>();

    static {
        ALL.add(DEFAULT);
        ALL.add(BBS_OLD);
        ALL.add(MINI);
        ALL.add(SHORTS);
    }
}