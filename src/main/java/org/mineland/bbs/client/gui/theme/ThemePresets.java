package org.mineland.bbs.client.gui.theme;

import java.util.ArrayList;
import java.util.List;

public class ThemePresets {

    public static final ThemePreset DARK = new ThemePreset(
            "Dark", 0xFF1A1A1A, 0xFF0D0D0D, 0xFFFF4444,
            0xFF8800FF, 0xFF4400AA  // фиолетовое свечение
    );

    public static final ThemePreset LIGHT = new ThemePreset(
            "Light", 0xFFFFFFFF, 0xFFE0E0E0, 0xFFFF4444,
            0xFFE91E8C, 0xFFFF8800  // розово-оранжевое (по умолчанию)
    );

    public static final ThemePreset WARM = new ThemePreset(
            "Warm", 0xFF2A1F1A, 0xFF1A120D, 0xFFFFAA33,
            0xFFFF6600, 0xFFFFCC00  // оранжево-жёлтое
    );

    public static final ThemePreset COOL = new ThemePreset(
            "Cool", 0xFF1A222A, 0xFF0D1319, 0xFF33AAFF,
            0xFF0066FF, 0xFF00CCFF  // синее свечение
    );

    public static final List<ThemePreset> ALL = new ArrayList<>();

    static {
        ALL.add(DARK);
        ALL.add(LIGHT);
        ALL.add(WARM);
        ALL.add(COOL);
    }
}