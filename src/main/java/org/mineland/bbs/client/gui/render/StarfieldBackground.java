package org.mineland.bbs.client.gui.render;

import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StarfieldBackground {

    private static class Star {
        float x, y;
        float size;
        float brightness;

        Star(float x, float y, float size, float brightness) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.brightness = brightness;
        }
    }

    private final List<Star> stars = new ArrayList<>();
    private final Random random = new Random(12345);
    private boolean generated = false;
    private int lastWidth, lastHeight;

    // Цвета свечения (по умолчанию розовый и оранжевый)
    private int leftGlowColor = 0xFFE91E8C;
    private int rightGlowColor = 0xFFFF8800;

    // Плавный переход между цветами
    private int targetLeftColor = leftGlowColor;
    private int targetRightColor = rightGlowColor;
    private float transitionProgress = 1.0f;
    private static final float TRANSITION_SPEED = 0.08f;

    public void setGlowColors(int leftColor, int rightColor) {
        if (targetLeftColor != leftColor || targetRightColor != rightColor) {
            // Начинаем новый переход от текущего отображаемого цвета
            this.leftGlowColor = getCurrentLeftColor();
            this.rightGlowColor = getCurrentRightColor();

            this.targetLeftColor = leftColor;
            this.targetRightColor = rightColor;
            this.transitionProgress = 0.0f;
        }
    }

    private int getCurrentLeftColor() {
        return lerpColor(leftGlowColor, targetLeftColor, transitionProgress);
    }

    private int getCurrentRightColor() {
        return lerpColor(rightGlowColor, targetRightColor, transitionProgress);
    }

    private int lerpColor(int colorA, int colorB, float t) {
        t = Math.max(0, Math.min(1, t));

        int aR = (colorA >> 16) & 0xFF;
        int aG = (colorA >> 8) & 0xFF;
        int aB = colorA & 0xFF;

        int bR = (colorB >> 16) & 0xFF;
        int bG = (colorB >> 8) & 0xFF;
        int bB = colorB & 0xFF;

        int r = (int) (aR + (bR - aR) * t);
        int g = (int) (aG + (bG - aG) * t);
        int b = (int) (aB + (bB - aB) * t);

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    public void render(GuiGraphics guiGraphics, int width, int height) {
        if (!generated || lastWidth != width || lastHeight != height) {
            generateStars(width, height);
            lastWidth = width;
            lastHeight = height;
            generated = true;
        }

        // Обновляем плавный переход цвета
        if (transitionProgress < 1.0f) {
            transitionProgress = Math.min(1.0f, transitionProgress + TRANSITION_SPEED);
        }

        renderGradientBackground(guiGraphics, width, height);
        renderNebulaGlow(guiGraphics, width, height);

        for (Star star : stars) {
            int alpha = (int) (star.brightness * 255);
            int color = (alpha << 24) | 0xFFFFFF;
            int size = Math.max(1, (int) star.size);
            guiGraphics.fill((int) star.x, (int) star.y,
                    (int) star.x + size, (int) star.y + size, color);
        }
    }

    private void generateStars(int width, int height) {
        stars.clear();
        int starCount = (width * height) / 8000;

        for (int i = 0; i < starCount; i++) {
            float x = random.nextFloat() * width;
            float y = random.nextFloat() * height;
            float size = 1 + random.nextFloat() * 1.5f;
            float brightness = 0.3f + random.nextFloat() * 0.7f;
            stars.add(new Star(x, y, size, brightness));
        }
    }

    private void renderGradientBackground(GuiGraphics guiGraphics, int width, int height) {
        int topColor = 0xFF1A1225;
        int bottomColor = 0xFF0A0812;
        guiGraphics.fillGradient(0, 0, width, height, topColor, bottomColor);
    }

    private void renderNebulaGlow(GuiGraphics guiGraphics, int width, int height) {
        int currentLeft = getCurrentLeftColor();
        int currentRight = getCurrentRightColor();

        // Делаем полупрозрачными для свечения
        int leftGlow = (0x33 << 24) | (currentLeft & 0xFFFFFF);
        int rightGlow = (0x33 << 24) | (currentRight & 0xFFFFFF);

        renderGlowBlob(guiGraphics, width * 0.15f, height * 0.6f, width * 0.4f, leftGlow);
        renderGlowBlob(guiGraphics, width * 0.85f, height * 0.4f, width * 0.35f, rightGlow);
    }

    private void renderGlowBlob(GuiGraphics guiGraphics, float centerX, float centerY,
                                float radius, int color) {
        int steps = 8;
        int baseAlpha = (color >> 24) & 0xFF;

        for (int i = steps; i > 0; i--) {
            float currentRadius = radius * i / steps;
            int alpha = (int) (baseAlpha * (1.0f - (float) i / steps) * 0.9f);
            int blobColor = (alpha << 24) | (color & 0xFFFFFF);

            int x1 = (int) (centerX - currentRadius);
            int y1 = (int) (centerY - currentRadius);
            int x2 = (int) (centerX + currentRadius);
            int y2 = (int) (centerY + currentRadius);

            guiGraphics.fill(x1, y1, x2, y2, blobColor);
        }
    }
}