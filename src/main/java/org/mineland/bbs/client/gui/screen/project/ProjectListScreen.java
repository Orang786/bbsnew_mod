package org.mineland.bbs.client.gui.screen.project;

import org.mineland.bbs.Bbs;
import org.mineland.bbs.common.config.BBSConfig;
import org.mineland.bbs.common.util.FileUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;
import org.mineland.bbs.client.gui.screen.studio.MainStudioScreen;

import java.io.File;

public class ProjectListScreen extends Screen {

    private Screen lastScreen;
    private ProjectList projectList;
    private EditBox newProjectInput;
    private String selectedProject = "";

    public ProjectListScreen(Screen lastScreen) {
        super(Component.literal("Projects"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        this.clearWidgets();

        int centerX = this.width / 2;

        // Заголовок
        this.addRenderableWidget(new StringWidget(
                centerX - 50, 20,
                100, 20,
                Component.literal("Your Projects"),
                this.font
        ));

        // Список проектов - БЕЗ последнего параметра
        projectList = new ProjectList(this.minecraft, this.width, this.height - 120, 60, 50);
        this.addRenderableWidget(projectList);

        // Новый проект input
        newProjectInput = new EditBox(this.font, centerX - 150, this.height - 100, 250, 20,
                Component.literal("Project Name"));
        this.addRenderableWidget(newProjectInput);

        // Кнопки
        this.addRenderableWidget(Button.builder(
                Component.literal("Create"),
                button -> this.createProject()
        ).pos(centerX + 110, this.height - 100).width(60).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Open"),
                button -> this.openProject()
        ).pos(centerX - 150, this.height - 60).width(70).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Delete"),
                button -> this.deleteProject()
        ).pos(centerX - 70, this.height - 60).width(70).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Back"),
                button -> this.onClose()
        ).pos(centerX + 10, this.height - 60).width(70).build());
    }

    private void createProject() {
        String projectName = newProjectInput.getValue().trim();
        if (!projectName.isEmpty()) {
            FileUtils.createProject(projectName);
            newProjectInput.setValue("");
            this.init();
        }
    }

    private void openProject() {
        if (projectList.getSelected() != null) {
            String projectName = projectList.getSelected().name;
            BBSConfig.getInstance().lastProject = projectName;
            BBSConfig.getInstance().save();

            this.minecraft.setScreen(new MainStudioScreen(projectName));
        }
    }

    private void deleteProject() {
        if (projectList.getSelected() != null) {
            String projectName = projectList.getSelected().name;
            FileUtils.deleteProject(projectName);
            this.init();
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        if (lastScreen != null) {
            this.minecraft.setScreen(lastScreen);
        } else {
            this.minecraft.setScreen(null); // просто закрываем GUI
        }
    }

    // Внутренний класс для списка проектов
    private static class ProjectList extends ObjectSelectionList<ProjectList.ProjectEntry> {

        public ProjectList(net.minecraft.client.Minecraft minecraft, int width, int height, int top, int bottom) {
            super(minecraft, width, height, top, bottom);

            File[] projects = FileUtils.listProjects();
            if (projects != null) {
                for (File project : projects) {
                    this.addEntry(new ProjectEntry(project.getName()));
                }
            }
        }

        @Override
        public int getRowWidth() {
            return this.width - 20;
        }

        static class ProjectEntry extends ObjectSelectionList.Entry<ProjectEntry> {

            private final String name;

            ProjectEntry(String name) {
                this.name = name;
            }

            @Override
            public void render(GuiGraphics guiGraphics, int index, int top, int left, int width,
                               int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
                guiGraphics.drawString(
                        net.minecraft.client.Minecraft.getInstance().font,
                        this.name, left + 5, top + 5, 0xFFFFFF, false
                );
            }

            @Override
            public Component getNarration() {
                return Component.literal(this.name);
            }
        }
    }
}