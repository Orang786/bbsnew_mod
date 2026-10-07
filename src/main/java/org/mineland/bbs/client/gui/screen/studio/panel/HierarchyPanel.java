package org.mineland.bbs.client.gui.screen.studio.panel;

import org.mineland.bbs.client.gui.BBSTheme;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public class HierarchyPanel {

    private int x, y, width, height;
    private List<String> entities = new ArrayList<>();
    private int selectedIndex = -1;
    private String activeTab = "Pick"; // Pick / Edit

    public HierarchyPanel(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // Тестовые данные
        entities.add("player/alex");
        entities.add("player/alex_bends");
        entities.add("block/stone");
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Фон панели
        guiGraphics.fill(x, y, x + width, y + height, BBSTheme.PANEL_BACKGROUND);
        guiGraphics.fill(x + width - 1, y, x + width, y + height, BBSTheme.PANEL_BORDER);

        // Табы (Pick / Edit)
        renderTabs(guiGraphics, mouseX, mouseY);

        // Свойства (если выбран объект)
        int propsY = y + 35;
        renderProperties(guiGraphics, propsY);

        // Список entities
        int listY = propsY + 150;
        renderEntityList(guiGraphics, mouseX, mouseY, listY);
    }

    private void renderTabs(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int tabWidth = width / 2;
        int tabHeight = 25;

        boolean pickActive = activeTab.equals("Pick");

        // Pick tab
        guiGraphics.fill(x, y, x + tabWidth, y + tabHeight,
                pickActive ? BBSTheme.ACCENT_PINK : BBSTheme.PANEL_BACKGROUND);
        guiGraphics.drawCenteredString(net.minecraft.client.Minecraft.getInstance().font,
                "Pick", x + tabWidth / 2, y + 8, BBSTheme.TEXT_WHITE);

        // Edit tab
        guiGraphics.fill(x + tabWidth, y, x + width, y + tabHeight,
                !pickActive ? BBSTheme.ACCENT_PINK : BBSTheme.PANEL_BACKGROUND);
        guiGraphics.drawCenteredString(net.minecraft.client.Minecraft.getInstance().font,
                "Edit", x + tabWidth + tabWidth / 2, y + 8, BBSTheme.TEXT_WHITE);
    }

    private void renderProperties(GuiGraphics guiGraphics, int startY) {
        var font = net.minecraft.client.Minecraft.getInstance().font;
        int lineHeight = 18;
        int currentY = startY;

        // Enabled toggle
        guiGraphics.drawString(font, "Enabled", x + 10, currentY, BBSTheme.TEXT_WHITE, false);
        guiGraphics.fill(x + width - 40, currentY, x + width - 10, currentY + 10, BBSTheme.ACCENT_PINK);
        currentY += lineHeight;

        // Replay display name
        guiGraphics.drawString(font, "Replay display name", x + 10, currentY, BBSTheme.TEXT_GRAY, false);
        currentY += lineHeight;

        // Name tag
        guiGraphics.drawString(font, "Name tag", x + 10, currentY, BBSTheme.TEXT_WHITE, false);
        currentY += lineHeight;

        // Shadow
        guiGraphics.drawString(font, "Shadow", x + 10, currentY, BBSTheme.TEXT_WHITE, false);
        guiGraphics.drawString(font, ">", x + width - 20, currentY, BBSTheme.TEXT_GRAY, false);
        currentY += lineHeight;

        // Miscellaneous
        guiGraphics.drawString(font, "Miscellaneous", x + 10, currentY, BBSTheme.TEXT_WHITE, false);
        guiGraphics.drawString(font, ">", x + width - 20, currentY, BBSTheme.TEXT_GRAY, false);
    }

    private void renderEntityList(GuiGraphics guiGraphics, int mouseX, int mouseY, int startY) {
        var font = net.minecraft.client.Minecraft.getInstance().font;

        // Разделитель с кнопками (+, cube, -, menu)
        guiGraphics.fill(x, startY, x + width, startY + 25, BBSTheme.PANEL_BACKGROUND);
        guiGraphics.drawString(font, "+", x + 10, startY + 8, BBSTheme.TEXT_WHITE, false);
        guiGraphics.drawString(font, "⬡", x + 30, startY + 8, BBSTheme.TEXT_WHITE, false);
        guiGraphics.drawString(font, "-", x + 50, startY + 8, BBSTheme.TEXT_WHITE, false);
        guiGraphics.drawString(font, "☰", x + width - 20, startY + 8, BBSTheme.TEXT_WHITE, false);

        // Список
        int listY = startY + 30;
        int itemHeight = 20;

        for (int i = 0; i < entities.size(); i++) {
            int itemY = listY + i * itemHeight;
            boolean selected = i == selectedIndex;
            boolean hovering = mouseY >= itemY && mouseY < itemY + itemHeight
                    && mouseX >= x && mouseX < x + width;

            int bgColor = selected ? BBSTheme.ACCENT_PINK :
                    (hovering ? BBSTheme.BUTTON_HOVER : BBSTheme.PANEL_BACKGROUND);

            guiGraphics.fill(x, itemY, x + width, itemY + itemHeight, bgColor);
            guiGraphics.drawString(font, entities.get(i), x + 10, itemY + 5,
                    BBSTheme.TEXT_WHITE, false);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX < x || mouseX > x + width || mouseY < y || mouseY > y + height) {
            return false;
        }

        // Клик по табам
        int tabHeight = 25;
        if (mouseY < y + tabHeight) {
            int tabWidth = width / 2;
            if (mouseX < x + tabWidth) {
                activeTab = "Pick";
            } else {
                activeTab = "Edit";
            }
            return true;
        }

        // Клик по списку entities
        int listStartY = y + 35 + 150 + 30;
        int itemHeight = 20;

        for (int i = 0; i < entities.size(); i++) {
            int itemY = listStartY + i * itemHeight;
            if (mouseY >= itemY && mouseY < itemY + itemHeight) {
                selectedIndex = i;
                return true;
            }
        }

        return true; // поглощаем клик в пределах панели
    }
}