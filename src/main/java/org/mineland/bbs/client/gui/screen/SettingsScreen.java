package org.mineland.bbs.client.gui.screen;

import org.mineland.bbs.common.config.BBSConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;

public class SettingsScreen extends Screen {

    private Screen lastScreen;
    private BBSConfig config;

    public SettingsScreen(Screen lastScreen) {
        super(Component.literal("Settings"));
        this.lastScreen = lastScreen;
        this.config = BBSConfig.getInstance();
    }

    @Override
    protected void init() {
        this.clearWidgets();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Заголовок
        this.addRenderableWidget(new StringWidget(
                centerX - 50, 40,
                100, 20,
                Component.literal("Settings"),
                this.font
        ));

        // Тема (Dark)
        this.addRenderableWidget(new StringWidget(
                centerX - 150, centerY - 60,
                100, 20,
                Component.literal("Theme:"),
                this.font
        ));

        this.addRenderableWidget(Button.builder(
                Component.literal(config.theme.equals("dark") ? "Dark ✓" : "Dark"),
                button -> {
                    config.theme = "dark";
                    this.init();
                }
        ).pos(centerX - 40, centerY - 60).width(60).build());

        this.addRenderableWidget(Button.builder(
                Component.literal(config.theme.equals("light") ? "Light ✓" : "Light"),
                button -> {
                    config.theme = "light";
                    this.init();
                }
        ).pos(centerX + 30, centerY - 60).width(60).build());

        // UI Scale
        this.addRenderableWidget(new StringWidget(
                centerX - 150, centerY - 20,
                100, 20,
                Component.literal("UI Scale: " + config.uiScale + "%"),
                this.font
        ));

        this.addRenderableWidget(Button.builder(
                Component.literal("-"),
                button -> {
                    if (config.uiScale > 50) config.uiScale -= 10;
                    this.init();
                }
        ).pos(centerX - 40, centerY - 20).width(30).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("+"),
                button -> {
                    if (config.uiScale < 200) config.uiScale += 10;
                    this.init();
                }
        ).pos(centerX + 10, centerY - 20).width(30).build());

        // Сохранить
        this.addRenderableWidget(Button.builder(
                Component.literal("Save"),
                button -> this.saveSettings()
        ).pos(centerX - 75, centerY + 40).width(150).build());

        // Back
        this.addRenderableWidget(Button.builder(
                Component.literal("Back"),
                button -> this.onClose()
        ).pos(centerX - 75, centerY + 70).width(150).build());
    }

    private void saveSettings() {
        config.save();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(lastScreen);
    }
}