package org.mineland.bbs.client.gui.screen;

import org.mineland.bbs.common.config.BBSConfig;
import org.mineland.bbs.common.util.FFmpegUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;

public class FFmpegConfigScreen extends Screen {

    private Screen lastScreen;
    private EditBox ffmpegPathInput;
    private String statusMessage = "";
    private int statusColor = 0xFFFFFF;

    public FFmpegConfigScreen(Screen lastScreen) {
        super(Component.literal("Configure FFmpeg"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        this.clearWidgets();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Заголовок
        this.addRenderableWidget(new StringWidget(
                centerX - 100, 40,
                200, 20,
                Component.literal("FFmpeg Configuration"),
                this.font
        ));

        // Инструкция
        this.addRenderableWidget(new StringWidget(
                centerX - 150, centerY - 80,
                300, 40,
                Component.literal("Enter the full path to FFmpeg executable"),
                this.font
        ));

        // Input поле
        ffmpegPathInput = new EditBox(this.font, centerX - 150, centerY - 20, 300, 20,
                Component.literal("FFmpeg Path"));
        ffmpegPathInput.setValue(FFmpegUtils.getFFmpegPath());
        this.addRenderableWidget(ffmpegPathInput);

        // Проверить кнопка
        this.addRenderableWidget(Button.builder(
                Component.literal("Test"),
                button -> this.testFFmpeg()
        ).pos(centerX - 75, centerY + 20).width(150).build());

        // Сохранить кнопка
        this.addRenderableWidget(Button.builder(
                Component.literal("Save"),
                button -> this.saveFFmpeg()
        ).pos(centerX - 75, centerY + 50).width(150).build());

        // Назад
        this.addRenderableWidget(Button.builder(
                Component.literal("Back"),
                button -> this.onClose()
        ).pos(centerX - 75, centerY + 80).width(150).build());
    }

    private void testFFmpeg() {
        String path = ffmpegPathInput.getValue();
        if (FFmpegUtils.isFFmpegValid(path)) {
            statusMessage = "✓ FFmpeg is valid!";
            statusColor = 0x00FF00;
        } else {
            statusMessage = "✗ FFmpeg not found or invalid";
            statusColor = 0xFF0000;
        }
    }

    private void saveFFmpeg() {
        String path = ffmpegPathInput.getValue();
        if (FFmpegUtils.isFFmpegValid(path)) {
            FFmpegUtils.setFFmpegPath(path);
            BBSConfig.getInstance().ffmpegPath = path;
            BBSConfig.getInstance().save();

            statusMessage = "✓ FFmpeg saved!";
            statusColor = 0x00FF00;
        } else {
            statusMessage = "✗ Invalid FFmpeg path";
            statusColor = 0xFF0000;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        // Статус сообщение
        if (!statusMessage.isEmpty()) {
            guiGraphics.drawString(this.font, statusMessage,
                    this.width / 2 - 50, this.height / 2 + 100, statusColor, false);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(lastScreen);
    }
}