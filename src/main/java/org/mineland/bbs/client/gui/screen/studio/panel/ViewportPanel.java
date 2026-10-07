package org.mineland.bbs.client.gui.screen.studio.panel;

import org.mineland.bbs.client.gui.BBSTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class ViewportPanel {

    private int x, y, width, height;

    public ViewportPanel(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Фон viewport (позже здесь будет рендериться 3D мир)
        guiGraphics.fill(x, y, x + width, y + height, 0xFF87CEEB); // Небо

        // Рисуем перекрестие камеры (как на скрине)
        int centerX = x + width / 2;
        int centerY = y + height / 2;

        guiGraphics.hLine(x, x + width - 1, centerY, 0x88FF0000);
        guiGraphics.vLine(centerX, y, y + height - 1, 0x88FF0000);

        // Разрешение внизу справа
        var font = Minecraft.getInstance().font;
        String resolution = width + " x " + height;
        guiGraphics.drawString(font, resolution,
                x + width - 80, y + height - 40, BBSTheme.TEXT_WHITE, false);

        // Панель управления viewport снизу
        renderViewportControls(guiGraphics, mouseX, mouseY);
    }

    private void renderViewportControls(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int controlsHeight = 30;
        int controlsY = y + height - controlsHeight - 10;

        guiGraphics.fill(x + width / 2 - 100, controlsY, x + width / 2 + 100,
                controlsY + controlsHeight, 0xCC000000);

        var font = Minecraft.getInstance().font;
        String[] icons = {"🖐", "✂", "↔", "✈", "🖐", "📷", "👁", "⚙"};
        int iconSpacing = 25;
        int startX = x + width / 2 - (icons.length * iconSpacing) / 2;

        for (int i = 0; i < icons.length; i++) {
            guiGraphics.drawString(font, icons[i],
                    startX + i * iconSpacing, controlsY + 10, BBSTheme.TEXT_WHITE, false);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        // TODO: вращение камеры
        return false;
    }
}