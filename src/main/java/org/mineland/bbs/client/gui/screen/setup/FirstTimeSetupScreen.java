package org.mineland.bbs.client.gui.screen.setup;

import org.mineland.bbs.client.gui.render.StarfieldBackground;
import org.mineland.bbs.client.gui.theme.ThemePreset;
import org.mineland.bbs.client.gui.theme.ThemePresets;
import org.mineland.bbs.common.config.BBSConfig;
import org.mineland.bbs.common.util.FFmpegUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.mineland.bbs.client.gui.screen.dashboard.DashboardScreen;
import org.mineland.bbs.client.gui.layout.LayoutPreset;
import org.mineland.bbs.client.gui.layout.LayoutPresets;
import org.mineland.bbs.client.gui.layout.PanelRect;

import javax.swing.*;
import java.io.File;
import java.util.List;

public class FirstTimeSetupScreen extends Screen {

    private final StarfieldBackground background = new StarfieldBackground();
    private ThemePreset selectedTheme = ThemePresets.LIGHT;

    private int currentPage = 0;
    private final int totalPages = 4;

    // Общие координаты
    private int centerX, centerY;
    private int contentWidth;
    private int titleY;
    private int contentY;

    // Page 0: Theme
    private int colorBarX, colorBarWidth, colorBarHeight;
    private int presetsY, presetButtonSize, presetButtonSpacing, presetsStartX;

    // Page 1: FFmpeg
    private boolean ffmpegFound = false;
    private String ffmpegVersion = "";
    private int pickFileButtonX, pickFileButtonY, pickFileButtonWidth, pickFileButtonHeight;

    private LayoutPreset selectedLayout = LayoutPresets.DEFAULT;
    private int layoutPreviewSize = 56;
    private int layoutPreviewSpacing = 24;
    private int layoutsStartX, layoutsY;

    public FirstTimeSetupScreen() {
        super(Component.literal("First Time Setup"));
    }

    public FirstTimeSetupScreen(int startPage) {
        super(Component.literal("First Time Setup"));
        this.currentPage = startPage;
    }

    // Page 3: Links & Start
    private int linksX, linksY, linkSpacing;
    private int startButtonX, startButtonY, startButtonWidth, startButtonHeight;

    @Override
    protected void init() {
        this.clearWidgets();

        centerX = this.width / 2;
        centerY = this.height / 2;
        contentWidth = Math.min(420, (int) (this.width * 0.35f));
        titleY = centerY - 130;
        contentY = centerY - 30;

        // Page 0 - Theme setup
        colorBarWidth = contentWidth;
        colorBarHeight = 32;
        colorBarX = centerX - colorBarWidth / 2;

        int presetsGap = 10;
        presetButtonSize = (colorBarWidth - presetsGap * 3) / 4;
        presetButtonSpacing = presetsGap;
        presetsY = contentY + colorBarHeight + 20;

        int totalPresetsWidth = 4 * presetButtonSize + 3 * presetButtonSpacing;
        presetsStartX = centerX - totalPresetsWidth / 2;

        // Page 1 - FFmpeg setup
        FFmpegUtils.initialize();
        ffmpegFound = FFmpegUtils.isFFmpegFound();
        ffmpegVersion = FFmpegUtils.getFFmpegVersion();

        pickFileButtonWidth = 160;
        pickFileButtonHeight = 24;
        pickFileButtonX = centerX - pickFileButtonWidth / 2;
        pickFileButtonY = contentY + 50;

        // Page 2 - Layout setup
        int totalLayoutsWidth = LayoutPresets.ALL.size() * layoutPreviewSize
                + (LayoutPresets.ALL.size() - 1) * layoutPreviewSpacing;
        layoutsStartX = centerX - totalLayoutsWidth / 2;
        layoutsY = contentY;

        // Page 3 - Links & Start button
        linksX = colorBarX; // выравниваем по левому краю как color bar
        linksY = contentY;
        linkSpacing = 20;

        startButtonWidth = contentWidth;
        startButtonHeight = 28;
        startButtonX = centerX - startButtonWidth / 2;
        startButtonY = linksY + linkSpacing * 3 + 35;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Обновляем цвета фона в соответствии с выбранной темой
        background.setGlowColors(selectedTheme.glowLeftColor, selectedTheme.glowRightColor);
        background.render(guiGraphics, this.width, this.height);

        renderTitle(guiGraphics);

        switch (currentPage) {
            case 0 -> renderThemePage(guiGraphics, mouseX, mouseY);
            case 1 -> renderFFmpegPage(guiGraphics, mouseX, mouseY);
            case 2 -> renderLayoutPage(guiGraphics, mouseX, mouseY);
            case 3 -> renderFinalPage(guiGraphics, mouseX, mouseY);
        }

        renderNavigationArrows(guiGraphics, mouseX, mouseY);
        renderPageDots(guiGraphics);
        renderLogo(guiGraphics);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Отключаем стандартный блюр
    }

    private void renderTitle(GuiGraphics guiGraphics) {
        String title = "First time in BBS NEW 2.3?";
        String subtitle = switch (currentPage) {
            case 0 -> "Let's make it look the way you like";
            case 1 -> "Without it BBS can't save video";
            case 2 -> "Where the editor's panels go. You can drag them around later";
            case 3 -> "Tutorials, the wiki and the community";
            default -> "";
        };

        guiGraphics.drawCenteredString(this.font, title, centerX, titleY, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, subtitle, centerX, titleY + 22, 0xFFAAAAAA);
    }

    // ========== PAGE 0: THEME ==========

    private void renderThemePage(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderColorBar(guiGraphics);
        renderThemePresets(guiGraphics, mouseX, mouseY);
    }

    private void renderColorBar(GuiGraphics guiGraphics) {
        int halfWidth = colorBarWidth / 2;

        guiGraphics.fill(colorBarX, contentY, colorBarX + halfWidth, contentY + colorBarHeight,
                selectedTheme.primaryColor | 0xFF000000);

        String primaryHex = ThemePreset.toHex(selectedTheme.primaryColor);
        int primaryTextColor = isLightColor(selectedTheme.primaryColor) ? 0xFF000000 : 0xFFFFFFFF;
        guiGraphics.drawCenteredString(this.font, primaryHex,
                colorBarX + halfWidth / 2, contentY + colorBarHeight / 2 - 4, primaryTextColor);

        guiGraphics.fill(colorBarX + halfWidth, contentY, colorBarX + colorBarWidth, contentY + colorBarHeight,
                selectedTheme.secondaryColor | 0xFF000000);

        String secondaryHex = ThemePreset.toHex(selectedTheme.secondaryColor);
        int secondaryTextColor = isLightColor(selectedTheme.secondaryColor) ? 0xFF000000 : 0xFFFFFFFF;
        guiGraphics.drawCenteredString(this.font, secondaryHex,
                colorBarX + halfWidth + halfWidth / 2, contentY + colorBarHeight / 2 - 4, secondaryTextColor);
    }

    private void renderThemePresets(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        List<ThemePreset> presets = ThemePresets.ALL;

        for (int i = 0; i < presets.size(); i++) {
            ThemePreset preset = presets.get(i);
            int btnX = presetsStartX + i * (presetButtonSize + presetButtonSpacing);
            int btnY = presetsY;

            boolean selected = preset == selectedTheme;
            boolean hovering = mouseX >= btnX && mouseX <= btnX + presetButtonSize
                    && mouseY >= btnY && mouseY <= btnY + presetButtonSize;

            renderThemeButton(guiGraphics, preset, btnX, btnY, selected, hovering);
        }
    }

    private void renderThemeButton(GuiGraphics guiGraphics, ThemePreset preset,
                                   int x, int y, boolean selected, boolean hovering) {
        int size = presetButtonSize;

        int bgColor = preset.primaryColor | 0xFF000000;
        guiGraphics.fill(x, y, x + size, y + size, bgColor);

        if (selected) {
            int borderColor = 0xFFFFFFFF;
            guiGraphics.fill(x - 1, y - 1, x + size + 1, y, borderColor);
            guiGraphics.fill(x - 1, y + size, x + size + 1, y + size + 1, borderColor);
            guiGraphics.fill(x - 1, y, x, y + size, borderColor);
            guiGraphics.fill(x + size, y, x + size + 1, y + size, borderColor);
        } else if (hovering) {
            int borderColor = 0x88FFFFFF;
            guiGraphics.fill(x - 1, y - 1, x + size + 1, y, borderColor);
            guiGraphics.fill(x - 1, y + size, x + size + 1, y + size + 1, borderColor);
        }

        int iconSize = Math.max(4, size / 8);
        int iconX = x + size / 2 - iconSize / 2;
        int iconY = y + size / 2 - iconSize / 2;
        guiGraphics.fill(iconX, iconY, iconX + iconSize, iconY + iconSize,
                preset.iconColor | 0xFF000000);

        int textColor = selected ? 0xFFFFFFFF : 0xFFAAAAAA;
        guiGraphics.drawCenteredString(this.font, preset.name,
                x + size / 2, y + size + 5, textColor);
    }

    // ========== PAGE 1: FFMPEG ==========

    private void renderFFmpegPage(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Статус найден/не найден
        String statusText;
        int statusColor;

        if (ffmpegFound) {
            String shortVersion = extractShortVersion(ffmpegVersion);
            statusText = "✓ Found: " + shortVersion;
            statusColor = 0xFF55FF55;
        } else {
            statusText = "✗ Not found";
            statusColor = 0xFFFF5555;
        }

        guiGraphics.drawCenteredString(this.font, statusText, centerX, contentY, statusColor);

        // Путь/название
        String pathText = ffmpegFound ? "ffmpeg" : "ffmpeg not configured";
        guiGraphics.drawCenteredString(this.font, pathText, centerX, contentY + 18, 0xFF888888);

        // Кнопка Pick file
        renderPickFileButton(guiGraphics, mouseX, mouseY);
    }

    private void renderPickFileButton(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        boolean hovering = mouseX >= pickFileButtonX && mouseX <= pickFileButtonX + pickFileButtonWidth
                && mouseY >= pickFileButtonY && mouseY <= pickFileButtonY + pickFileButtonHeight;

        int bgColor = hovering ? 0xFFFF6666 : 0xFFE84545;
        guiGraphics.fill(pickFileButtonX, pickFileButtonY,
                pickFileButtonX + pickFileButtonWidth, pickFileButtonY + pickFileButtonHeight, bgColor);

        guiGraphics.drawCenteredString(this.font, "Pick file...",
                centerX, pickFileButtonY + pickFileButtonHeight / 2 - 4, 0xFFFFFFFF);
    }

    private String extractShortVersion(String fullVersion) {
        // "ffmpeg version 4.4-xxxx..." -> "ffmpeg 4.4"
        try {
            String[] parts = fullVersion.split(" ");
            if (parts.length >= 3) {
                String version = parts[2];
                // Берём только числа до первого не-цифрового/точки символа
                StringBuilder sb = new StringBuilder();
                for (char c : version.toCharArray()) {
                    if (Character.isDigit(c) || c == '.') {
                        sb.append(c);
                    } else {
                        break;
                    }
                }
                return "ffmpeg " + sb;
            }
        } catch (Exception ignored) {}
        return "ffmpeg";
    }

    // ========== PAGE 2: LAYOUT ==========

    private void renderLayoutPage(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        List<LayoutPreset> layouts = LayoutPresets.ALL;

        for (int i = 0; i < layouts.size(); i++) {
            LayoutPreset layout = layouts.get(i);
            int btnX = layoutsStartX + i * (layoutPreviewSize + layoutPreviewSpacing);
            int btnY = layoutsY;

            boolean selected = layout == selectedLayout;
            boolean hovering = mouseX >= btnX && mouseX <= btnX + layoutPreviewSize
                    && mouseY >= btnY && mouseY <= btnY + layoutPreviewSize;

            renderLayoutPreview(guiGraphics, layout, btnX, btnY, selected, hovering);
        }
    }

    private void renderLayoutPreview(GuiGraphics guiGraphics, LayoutPreset layout,
                                     int x, int y, boolean selected, boolean hovering) {
        int size = layoutPreviewSize;

        // Фон превью
        guiGraphics.fill(x, y, x + size, y + size, 0xFF1A1A1A);

        // Рисуем панели макета
        for (PanelRect panel : layout.panels) {
            int px = x + (int) (panel.x * size);
            int py = y + (int) (panel.y * size);
            int pw = (int) (panel.width * size);
            int ph = (int) (panel.height * size);

            // Небольшой отступ между панелями
            int gap = 2;
            guiGraphics.fill(px + gap, py + gap, px + pw - gap, py + ph - gap, panel.type.color);

            // Иконка камеры в viewport
            if (panel.hasIcon && pw > 15 && ph > 12) {
                int iconSize = 6;
                int iconX = px + pw / 2 - iconSize / 2;
                int iconY = py + ph / 2 - iconSize / 2;
                guiGraphics.fill(iconX, iconY, iconX + iconSize, iconY + iconSize, 0xFFFFFFFF);
            }
        }

        // Рамка выделения
        if (selected) {
            int borderColor = 0xFFFFFFFF;
            guiGraphics.fill(x - 1, y - 1, x + size + 1, y, borderColor);
            guiGraphics.fill(x - 1, y + size, x + size + 1, y + size + 1, borderColor);
            guiGraphics.fill(x - 1, y, x, y + size, borderColor);
            guiGraphics.fill(x + size, y, x + size + 1, y + size, borderColor);
        } else if (hovering) {
            int borderColor = 0x88FFFFFF;
            guiGraphics.fill(x - 1, y - 1, x + size + 1, y, borderColor);
            guiGraphics.fill(x - 1, y + size, x + size + 1, y + size + 1, borderColor);
        }

        // Название под превью
        int textColor = selected ? 0xFFFFFFFF : 0xFFAAAAAA;
        guiGraphics.drawCenteredString(this.font, layout.name,
                x + size / 2, y + size + 8, textColor);
    }

    // ========== PAGE 3: LINKS & START ==========

    private void renderFinalPage(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int y = linksY;

        // Tutorials
        renderLinkItem(guiGraphics, mouseX, mouseY, linksX, y, "▶", "Tutorials", 0);
        y += linkSpacing;

        // Wiki
        renderLinkItem(guiGraphics, mouseX, mouseY, linksX, y, "?", "Wiki", 1);
        y += linkSpacing;

        // Discord
        renderLinkItem(guiGraphics, mouseX, mouseY, linksX, y, "◉", "Discord", 2);
        y += linkSpacing;

        // F9 hint (не кликабельно, просто текст)
        guiGraphics.drawString(this.font, "F9 — all keybinds, in any editor",
                linksX, y, 0xFF888888, false);

        // Start button
        renderStartButton(guiGraphics, mouseX, mouseY);
    }

    private void renderLinkItem(GuiGraphics guiGraphics, int mouseX, int mouseY,
                                int x, int y, String icon, String text, int linkIndex) {
        int textWidth = this.font.width(icon + " " + text);
        boolean hovering = mouseX >= x && mouseX <= x + textWidth + 10
                && mouseY >= y - 2 && mouseY <= y + 10;

        int color = hovering ? 0xFFFFFFFF : 0xFFCCCCCC;

        guiGraphics.drawString(this.font, icon, x, y, color, false);
        guiGraphics.drawString(this.font, text, x + 16, y, color, false);
    }

    private void renderStartButton(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        boolean hovering = mouseX >= startButtonX && mouseX <= startButtonX + startButtonWidth
                && mouseY >= startButtonY && mouseY <= startButtonY + startButtonHeight;

        int bgColor = hovering ? 0xFFFF6666 : 0xFFE84545;
        guiGraphics.fill(startButtonX, startButtonY,
                startButtonX + startButtonWidth, startButtonY + startButtonHeight, bgColor);

        guiGraphics.drawCenteredString(this.font, "Start",
                centerX, startButtonY + startButtonHeight / 2 - 4, 0xFFFFFFFF);
    }

    // ========== PLACEHOLDER PAGES ==========

    private void renderPlaceholderPage(GuiGraphics guiGraphics, String text) {
        guiGraphics.drawCenteredString(this.font, text, centerX, contentY + 20, 0xFFFFFFFF);
    }

    // ========== NAVIGATION ==========

    private void renderNavigationArrows(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        boolean isLastPage = currentPage == totalPages - 1;

        int arrowY = getArrowY();
        int[] arrowXs = getArrowXPositions();
        int leftArrowX = arrowXs[0];
        int rightArrowX = arrowXs[1];

        if (currentPage > 0) {
            boolean hoveringLeft = isHoveringArrow(mouseX, mouseY, leftArrowX, arrowY);
            int leftColor = hoveringLeft ? 0xFFFFFFFF : 0xFFAAAAAA;
            guiGraphics.drawString(this.font, "<-", leftArrowX, arrowY, leftColor, false);
        }

        // На последней странице правая стрелка не нужна - есть кнопка Start
        if (!isLastPage) {
            boolean hoveringRight = isHoveringArrow(mouseX, mouseY, rightArrowX, arrowY);
            int rightColor = hoveringRight ? 0xFFFFFFFF : 0xFFAAAAAA;
            guiGraphics.drawString(this.font, "->", rightArrowX, arrowY, rightColor, false);
        }
    }

    private int getArrowY() {
        return switch (currentPage) {
            case 2 -> layoutsY + layoutPreviewSize / 2 - 4;
            default -> contentY + 10;
        };
    }

    private int[] getArrowXPositions() {
        return switch (currentPage) {
            case 2 -> {
                int totalLayoutsWidth = LayoutPresets.ALL.size() * layoutPreviewSize
                        + (LayoutPresets.ALL.size() - 1) * layoutPreviewSpacing;
                int left = centerX - totalLayoutsWidth / 2 - 50;
                int right = centerX + totalLayoutsWidth / 2 + 35;
                yield new int[]{left, right};
            }
            default -> {
                int left = centerX - contentWidth / 2 - 60;
                int right = centerX + contentWidth / 2 + 45;
                yield new int[]{left, right};
            }
        };
    }

    private boolean isHoveringArrow(int mouseX, int mouseY, int arrowX, int arrowY) {
        return mouseX >= arrowX - 10 && mouseX <= arrowX + 20
                && mouseY >= arrowY - 10 && mouseY <= arrowY + 15;
    }

    private void renderPageDots(GuiGraphics guiGraphics) {
        int dotSize = 6;
        int dotSpacing = 14;
        int totalWidth = totalPages * dotSpacing;
        int startX = centerX - totalWidth / 2;
        int dotsY = this.height / 2 + 150;

        for (int i = 0; i < totalPages; i++) {
            int dotX = startX + i * dotSpacing;
            boolean active = i == currentPage;

            int color = active ? 0xFFE91E8C : 0xFF555555;
            guiGraphics.fill(dotX, dotsY, dotX + dotSize, dotsY + dotSize, color);
        }
    }

    private void renderLogo(GuiGraphics guiGraphics) {
        guiGraphics.drawString(this.font, "",
                this.width - 20, this.height - 20, 0x88FFFFFF, false);
    }

    private boolean isLightColor(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        double brightness = (r * 0.299 + g * 0.587 + b * 0.114);
        return brightness > 150;
    }

    private boolean handleLayoutPageClick(double mouseX, double mouseY) {
        List<LayoutPreset> layouts = LayoutPresets.ALL;

        for (int i = 0; i < layouts.size(); i++) {
            LayoutPreset layout = layouts.get(i);
            int btnX = layoutsStartX + i * (layoutPreviewSize + layoutPreviewSpacing);
            int btnY = layoutsY;

            if (mouseX >= btnX && mouseX <= btnX + layoutPreviewSize
                    && mouseY >= btnY && mouseY <= btnY + layoutPreviewSize) {
                selectedLayout = layout;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean isLastPage = currentPage == totalPages - 1;

        int arrowY = getArrowY();
        int[] arrowXs = getArrowXPositions();
        int leftArrowX = arrowXs[0];
        int rightArrowX = arrowXs[1];

        if (currentPage > 0 && isHoveringArrow((int) mouseX, (int) mouseY, leftArrowX, arrowY)) {
            currentPage--;
            init();
            return true;
        }

        if (!isLastPage && isHoveringArrow((int) mouseX, (int) mouseY, rightArrowX, arrowY)) {
            goToNextPage();
            return true;
        }

        switch (currentPage) {
            case 0 -> {
                if (handleThemePageClick(mouseX, mouseY)) return true;
            }
            case 1 -> {
                if (handleFFmpegPageClick(mouseX, mouseY)) return true;
            }
            case 2 -> {
                if (handleLayoutPageClick(mouseX, mouseY)) return true;
            }
            case 3 -> {
                if (handleFinalPageClick(mouseX, mouseY)) return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean handleFinalPageClick(double mouseX, double mouseY) {
        // Клик по Start button
        if (mouseX >= startButtonX && mouseX <= startButtonX + startButtonWidth
                && mouseY >= startButtonY && mouseY <= startButtonY + startButtonHeight) {
            finishSetup();
            return true;
        }

        // Клики по ссылкам (открываем браузер)
        int y = linksY;
        if (isHoveringLink(mouseX, mouseY, linksX, y, "▶ Tutorials")) {
            openLink("https://github.com/Orang786/");
            return true;
        }
        y += linkSpacing;

        if (isHoveringLink(mouseX, mouseY, linksX, y, "? Wiki")) {
            openLink("https://github.com/Orang786");
            return true;
        }
        y += linkSpacing;

        if (isHoveringLink(mouseX, mouseY, linksX, y, "◉ Discord")) {
            openLink("https://discord.gg/3BdVQBXch");
            return true;
        }

        return false;
    }

    private boolean isHoveringLink(double mouseX, double mouseY, int x, int y, String fullText) {
        int textWidth = this.font.width(fullText);
        return mouseX >= x && mouseX <= x + textWidth + 10
                && mouseY >= y - 2 && mouseY <= y + 10;
    }

    private void openLink(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean handleThemePageClick(double mouseX, double mouseY) {
        List<ThemePreset> presets = ThemePresets.ALL;

        for (int i = 0; i < presets.size(); i++) {
            ThemePreset preset = presets.get(i);
            int btnX = presetsStartX + i * (presetButtonSize + presetButtonSpacing);
            int btnY = presetsY;

            if (mouseX >= btnX && mouseX <= btnX + presetButtonSize
                    && mouseY >= btnY && mouseY <= btnY + presetButtonSize) {
                selectedTheme = preset;
                return true;
            }
        }
        return false;
    }

    private boolean handleFFmpegPageClick(double mouseX, double mouseY) {
        if (mouseX >= pickFileButtonX && mouseX <= pickFileButtonX + pickFileButtonWidth
                && mouseY >= pickFileButtonY && mouseY <= pickFileButtonY + pickFileButtonHeight) {

            openFilePicker();
            return true;
        }
        return false;
    }

    private void openFilePicker() {
        new Thread(() -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select FFmpeg executable");

            int result = chooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                String path = selectedFile.getAbsolutePath();

                if (FFmpegUtils.isFFmpegValid(path)) {
                    FFmpegUtils.setFFmpegPath(path);
                    ffmpegFound = true;
                    ffmpegVersion = FFmpegUtils.getFFmpegVersion();
                }
            }
        }).start();
    }

    private void goToNextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            init();
        } else {
            finishSetup();
        }
    }

    private void finishSetup() {
        BBSConfig config = BBSConfig.getInstance();
        config.theme = selectedTheme.name.toLowerCase();
        config.layoutPreset = selectedLayout.id;
        config.firstTimeSetupDone = true;
        config.save();

        Minecraft.getInstance().setScreen(new DashboardScreen());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}