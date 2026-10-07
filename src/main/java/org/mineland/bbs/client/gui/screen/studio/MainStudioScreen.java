package org.mineland.bbs.client.gui.screen.studio;

import org.mineland.bbs.client.gui.BBSTheme;
import org.mineland.bbs.client.gui.screen.studio.panel.HierarchyPanel;
import org.mineland.bbs.client.gui.screen.studio.panel.TimelinePanel;
import org.mineland.bbs.client.gui.screen.studio.panel.ViewportPanel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MainStudioScreen extends Screen {

    private String projectName;

    private HierarchyPanel hierarchyPanel;
    private ViewportPanel viewportPanel;
    private TimelinePanel timelinePanel;

    public MainStudioScreen(String projectName) {
        super(Component.literal("Block Buster Studio - " + projectName));
        this.projectName = projectName;
    }

    @Override
    protected void init() {
        this.clearWidgets();

        int hierarchyWidth = BBSTheme.HIERARCHY_WIDTH;
        int timelineHeight = BBSTheme.TIMELINE_HEIGHT;
        int toolbarHeight = BBSTheme.TOOLBAR_HEIGHT;

        // Левая панель - Hierarchy
        hierarchyPanel = new HierarchyPanel(
                0, toolbarHeight,
                hierarchyWidth, this.height - toolbarHeight - timelineHeight
        );

        // Центральная панель - Viewport (3D сцена)
        viewportPanel = new ViewportPanel(
                hierarchyWidth, toolbarHeight,
                this.width - hierarchyWidth, this.height - toolbarHeight - timelineHeight
        );

        // Нижняя панель - Timeline
        timelinePanel = new TimelinePanel(
                0, this.height - timelineHeight,
                this.width, timelineHeight
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // НЕ рендерим фон игры - рендерим мир напрямую (позже добавим)
        guiGraphics.fill(0, 0, this.width, this.height, BBSTheme.BACKGROUND_DARK);

        // Верхний toolbar
        renderTopToolbar(guiGraphics, mouseX, mouseY);

        // Панели
        hierarchyPanel.render(guiGraphics, mouseX, mouseY, partialTick);
        viewportPanel.render(guiGraphics, mouseX, mouseY, partialTick);
        timelinePanel.render(guiGraphics, mouseX, mouseY, partialTick);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderTopToolbar(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int toolbarHeight = BBSTheme.TOOLBAR_HEIGHT;

        guiGraphics.fill(0, 0, this.width, toolbarHeight, BBSTheme.PANEL_BACKGROUND);
        guiGraphics.fill(0, toolbarHeight - 1, this.width, toolbarHeight, BBSTheme.PANEL_BORDER);

        // Название проекта
        guiGraphics.drawString(this.font, projectName, 10, 10, BBSTheme.TEXT_WHITE, false);

        // Кнопки справа (play, save, menu)
        int rightX = this.width - 80;
        guiGraphics.drawString(this.font, "▶", rightX, 10, BBSTheme.ACCENT_PINK, false);
        guiGraphics.drawString(this.font, "💾", rightX + 25, 10, BBSTheme.TEXT_WHITE, false);
        guiGraphics.drawString(this.font, "☰", rightX + 50, 10, BBSTheme.TEXT_WHITE, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hierarchyPanel.mouseClicked(mouseX, mouseY, button)) return true;
        if (viewportPanel.mouseClicked(mouseX, mouseY, button)) return true;
        if (timelinePanel.mouseClicked(mouseX, mouseY, button)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (timelinePanel.mouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;
        if (viewportPanel.mouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false; // не закрываем по ESC, у нас своя логика
    }
}