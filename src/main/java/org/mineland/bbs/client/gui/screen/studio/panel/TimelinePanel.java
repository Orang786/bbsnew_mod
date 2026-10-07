package org.mineland.bbs.client.gui.screen.studio.panel;

import org.mineland.bbs.client.gui.BBSTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public class TimelinePanel {

    private int x, y, width, height;
    private int currentFrame = 8;
    private int totalFrames = 103;
    private boolean draggingPlayhead = false;

    private String[] trackNames = {"x", "y", "z", "pitch", "yaw", "headYaw", "bodyYaw", "sneaking"};
    private int trackLabelWidth = 120;

    public TimelinePanel(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(x, y, x + width, y + height, BBSTheme.TIMELINE_BACKGROUND);
        guiGraphics.fill(x, y, x + width, y + 1, BBSTheme.PANEL_BORDER);

        // Верхняя строка - номера кадров
        renderFrameRuler(guiGraphics);

        // Треки
        renderTracks(guiGraphics, mouseX, mouseY);

        // Playhead (вертикальная линия текущего кадра)
        renderPlayhead(guiGraphics);

        // Нижний инфо-бар
        renderBottomBar(guiGraphics);
    }

    private void renderFrameRuler(GuiGraphics guiGraphics) {
        var font = Minecraft.getInstance().font;
        int rulerY = y + 5;
        int timelineStartX = x + trackLabelWidth;
        int timelineWidth = width - trackLabelWidth;

        float pixelsPerFrame = (float) timelineWidth / totalFrames;

        for (int frame = 0; frame <= totalFrames; frame += 10) {
            int frameX = timelineStartX + (int)(frame * pixelsPerFrame);
            guiGraphics.drawString(font, String.valueOf(frame), frameX, rulerY,
                    BBSTheme.TEXT_GRAY, false);
        }
    }

    private void renderTracks(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int tracksStartY = y + 25;
        int timelineStartX = x + trackLabelWidth;
        int timelineWidth = width - trackLabelWidth;

        for (int i = 0; i < trackNames.length; i++) {
            int trackY = tracksStartY + i * BBSTheme.TRACK_HEIGHT;

            boolean hovering = mouseY >= trackY && mouseY < trackY + BBSTheme.TRACK_HEIGHT;

            // Название трека
            int labelBg = hovering ? BBSTheme.BUTTON_HOVER : BBSTheme.TIMELINE_BACKGROUND;
            guiGraphics.fill(x, trackY, x + trackLabelWidth, trackY + BBSTheme.TRACK_HEIGHT, labelBg);
            guiGraphics.drawString(Minecraft.getInstance().font, trackNames[i],
                    x + 25, trackY + 6, BBSTheme.TEXT_WHITE, false);

            // Иконка типа параметра слева (цветной квадратик)
            int iconColor = getTrackColor(i);
            guiGraphics.fill(x + 5, trackY + 6, x + 15, trackY + 16, iconColor);

            // Фон дорожки
            guiGraphics.fill(timelineStartX, trackY, x + width, trackY + BBSTheme.TRACK_HEIGHT,
                    BBSTheme.TIMELINE_TRACK);

            // Keyframes (тестовые)
            if (i == 1) { // y трек с keyframe
                int kfX = timelineStartX + (int)(8 * ((float) timelineWidth / totalFrames));
                guiGraphics.fill(kfX - 3, trackY + 7, kfX + 3, trackY + 13, BBSTheme.TIMELINE_KEYFRAME);
            }
        }
    }

    private int getTrackColor(int index) {
        int[] colors = {
                0xFFFF4444, 0xFF44FF44, 0xFF4444FF,
                0xFFFFAA44, 0xFFFFFF44, 0xFF44FFFF,
                0xFFFF44FF, 0xFF888888
        };
        return colors[index % colors.length];
    }

    private void renderPlayhead(GuiGraphics guiGraphics) {
        int timelineStartX = x + trackLabelWidth;
        int timelineWidth = width - trackLabelWidth;
        float pixelsPerFrame = (float) timelineWidth / totalFrames;

        int playheadX = timelineStartX + (int)(currentFrame * pixelsPerFrame);

        guiGraphics.vLine(playheadX, y, y + height - 20, BBSTheme.TIMELINE_PLAYHEAD);

        // Треугольник сверху (голова плейхеда)
        guiGraphics.fill(playheadX - 3, y + 15, playheadX + 3, y + 20, BBSTheme.TIMELINE_PLAYHEAD);
    }

    private void renderBottomBar(GuiGraphics guiGraphics) {
        int barY = y + height - 20;
        var font = Minecraft.getInstance().font;

        guiGraphics.fill(x, barY, x + width, y + height, 0xFF0A0A12);

        // Счётчик кадров
        String frameText = currentFrame + "/" + totalFrames;
        guiGraphics.drawString(font, frameText, x + 10, barY + 6,
                BBSTheme.TIMELINE_PLAYHEAD, false);

        // Иконки инструментов
        String[] icons = {"⚙", "📷", "🖌", "🎬", "🔧", "🖼", "🔊", "↓"};
        int iconX = x + 100;
        for (String icon : icons) {
            guiGraphics.drawString(font, icon, iconX, barY + 6, BBSTheme.TEXT_WHITE, false);
            iconX += 20;
        }

        // Логотип в правом нижнем углу
        guiGraphics.drawString(font, "⬡", x + width - 20, barY + 6, BBSTheme.ACCENT_PINK, false);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX < x || mouseX > x + width || mouseY < y || mouseY > y + height) {
            return false;
        }

        // Клик по рулетке кадров - перемещаем playhead
        if (mouseY < y + 20 && mouseX > x + trackLabelWidth) {
            updatePlayheadFromMouse(mouseX);
            draggingPlayhead = true;
            return true;
        }

        return true;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingPlayhead) {
            updatePlayheadFromMouse(mouseX);
            return true;
        }
        return false;
    }

    private void updatePlayheadFromMouse(double mouseX) {
        int timelineStartX = x + trackLabelWidth;
        int timelineWidth = width - trackLabelWidth;
        float pixelsPerFrame = (float) timelineWidth / totalFrames;

        int frame = (int) ((mouseX - timelineStartX) / pixelsPerFrame);
        currentFrame = Math.max(0, Math.min(totalFrames, frame));
    }
}