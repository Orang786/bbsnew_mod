package org.mineland.bbs.client.gui.screen.dashboard;

import org.mineland.bbs.common.util.FileUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DashboardScreen extends Screen {

    // Дизайн-холст - ВСЁ рисуется в этих координатах, потом масштабируется
    private static final int DESIGN_WIDTH = 1246;
    private static final int DESIGN_HEIGHT = 655;

    private float uiScale;
    private int offsetX, offsetY;

    private List<File> recentProjects = new ArrayList<>();

    // Onboarding state
    private boolean onboardActive = true;
    private int onboardStep = 0;

    public DashboardScreen() {
        super(Component.literal("Block Buster Studio"));
    }

    @Override
    protected void init() {
        this.clearWidgets();

        uiScale = Math.min((float) this.width / DESIGN_WIDTH, (float) this.height / DESIGN_HEIGHT);
        offsetX = (int) ((this.width - DESIGN_WIDTH * uiScale) / 2f);
        offsetY = (int) ((this.height - DESIGN_HEIGHT * uiScale) / 2f);

        loadRecentProjects();
    }

    private void loadRecentProjects() {
        recentProjects.clear();
        File[] projects = FileUtils.listProjects();
        if (projects != null) {
            for (File p : projects) recentProjects.add(p);
        }
    }

    // ========== COORDINATE CONVERSION ==========

    private int toDesignX(double screenX) {
        return (int) ((screenX - offsetX) / uiScale);
    }

    private int toDesignY(double screenY) {
        return (int) ((screenY - offsetY) / uiScale);
    }

    // ========== RENDER ==========

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        int dmx = toDesignX(mouseX);
        int dmy = toDesignY(mouseY);

        // Фон на весь реальный экран (не масштабируется, покрывает все окно)
        renderFullBackground(g);

        // Letterbox-рамки если пропорции экрана не совпадают с дизайном
        if (offsetX > 0) {
            g.fill(0, 0, offsetX, this.height, 0xFF000000);
            g.fill(this.width - offsetX, 0, this.width, this.height, 0xFF000000);
        }
        if (offsetY > 0) {
            g.fill(0, 0, this.width, offsetY, 0xFF000000);
            g.fill(0, this.height - offsetY, this.width, this.height, 0xFF000000);
        }

        g.pose().pushPose();
        g.pose().translate(offsetX, offsetY, 0);
        g.pose().scale(uiScale, uiScale, 1f);

        renderTabBar(g, dmx, dmy);
        renderHeroBanner(g);
        renderFilmsAndRecent(g, dmx, dmy);
        renderBottomToolbar(g, dmx, dmy);

        if (onboardActive) {
            renderOnboarding(g, dmx, dmy);
        }

        g.pose().popPose();

        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        // Отключаем стандартный фон
    }

    private void renderFullBackground(GuiGraphics g) {
        g.fillGradient(0, 0, this.width, this.height, 0xFF18111A, 0xFF0A0810);
    }

    private void renderGlow(GuiGraphics g, float cx, float cy, float radius, int color) {
        int steps = 5;
        int baseAlpha = (color >> 24) & 0xFF;
        for (int i = steps; i > 0; i--) {
            float r = radius * i / steps;
            int alpha = (int) (baseAlpha * (1.0f - (float) i / steps));
            int c = (alpha << 24) | (color & 0xFFFFFF);
            g.fill((int) (cx - r), (int) (cy - r), (int) (cx + r), (int) (cy + r), c);
        }
    }

    // ========== TAB BAR (design y: 0-24) ==========

    private void renderTabBar(GuiGraphics g, int dmx, int dmy) {
        g.fill(0, 0, DESIGN_WIDTH, 24, 0xFF14141A);

        // Tab "New tab"
        boolean hoveringTab = dmx >= 0 && dmx <= 140 && dmy >= 0 && dmy <= 24;
        g.fill(0, 0, 140, 24, hoveringTab ? 0xFF2A2A32 : 0xFF1E1E26);

        g.fill(10, 11, 13, 14, 0xFF888888); // маленькая иконка-точка вместо лупы
        g.drawString(this.font, "New tab", 22, 8, 0xFFCCCCCC, false);
        g.drawString(this.font, "x", 123, 8, 0xFF888888, false);

        // Правые иконки
        int[] iconX = {1122, 1148, 1189, 1213, 1235};
        String[] icons = {"|>", "[-]", "=", "[]", "x"};
        for (int i = 0; i < icons.length; i++) {
            int color = i == 0 ? 0xFFE84545 : 0xFFCCCCCC;
            g.drawString(this.font, icons[i], iconX[i], 8, color, false);
        }
    }

    // ========== HERO BANNER (design: x337-910, y90-325) ==========

    private void renderHeroBanner(GuiGraphics g) {
        int x1 = 337, y1 = 90, x2 = 910, y2 = 325;

        g.fillGradient(x1, y1, x2, y2, 0xFF231828, 0xFF14101C);

        renderGlow(g, x1 + 170, y1 + 110, 95, 0x55CC3333);
        renderGlow(g, x1 + 290, y1 + 130, 80, 0x5533AACC);

        // Нижняя подпись-бар
        g.fill(x1, y2 - 30, x2, y2 - 29, 0xFF3A2E40);
        g.fill(x1 + 10, y2 - 22, x1 + 12, y2 - 10, 0xFFE84545);
        g.drawString(this.font, "BBS FS | 2.6", x1 + 20, y2 - 20, 0xFFFFFFFF, false);

        String renderBy = "render by Xavin";
        int tw = this.font.width(renderBy);
        g.drawString(this.font, renderBy, x2 - tw - 10, y2 - 20, 0xFF999999, false);
    }

    // ========== FILMS / RECENT (design: start y=350) ==========

    private void renderFilmsAndRecent(GuiGraphics g, int dmx, int dmy) {
        int leftX = 347;
        int rightX = 580;
        int headerY = 350;

        g.drawString(this.font, "Films", leftX, headerY, 0xFF777777, false);
        g.drawString(this.font, "Recent", rightX, headerY, 0xFF777777, false);

        int y = headerY + 32;
        renderMenuItem(g, dmx, dmy, leftX, y, "+", "New film", true);
        y += 26;
        renderMenuItem(g, dmx, dmy, leftX, y, "=", "Film list", false);
        y += 26;
        renderMenuItem(g, dmx, dmy, leftX, y, "o", "Open folder", false);
        y += 38;
        renderMenuItem(g, dmx, dmy, leftX, y, "D", "Discord", false);
        y += 27;
        renderMenuItem(g, dmx, dmy, leftX, y, ">", "Tutorials", false);
        y += 26;
        renderMenuItem(g, dmx, dmy, leftX, y, "?", "Wiki", false);

        int recentY = headerY + 32;
        if (recentProjects.isEmpty()) {
            g.drawString(this.font, "No recent films", rightX, recentY, 0xFF555555, false);
        } else {
            for (File project : recentProjects) {
                renderRecentItem(g, dmx, dmy, rightX, recentY, project);
                recentY += 26;
            }
        }
    }

    private void renderMenuItem(GuiGraphics g, int dmx, int dmy, int x, int y,
                                String icon, String text, boolean accent) {
        int tw = this.font.width(text) + 16;
        boolean hovering = dmx >= x && dmx <= x + tw && dmy >= y - 2 && dmy <= y + 10;

        int color = accent ? 0xFFE84545 : (hovering ? 0xFFFFFFFF : 0xFFBBBBBB);

        g.drawString(this.font, icon, x, y, color, false);
        g.drawString(this.font, text, x + 15, y, color, false);
    }

    private void renderRecentItem(GuiGraphics g, int dmx, int dmy, int x, int y, File project) {
        int itemWidth = 290;
        boolean hovering = dmx >= x && dmx <= x + itemWidth && dmy >= y - 2 && dmy <= y + 10;

        int color = hovering ? 0xFFFFFFFF : 0xFFBBBBBB;

        g.drawString(this.font, "#", x, y, color, false);

        String name = trimText(project.getName(), 180);
        g.drawString(this.font, name, x + 16, y, color, false);

        String timeAgo = formatTimeAgo(project.lastModified());
        int tw = this.font.width(timeAgo);
        g.drawString(this.font, timeAgo, x + itemWidth - tw, y, 0xFF666666, false);
    }

    private String trimText(String text, int maxWidth) {
        if (this.font.width(text) <= maxWidth) return text;
        while (this.font.width(text + "...") > maxWidth && text.length() > 1) {
            text = text.substring(0, text.length() - 1);
        }
        return text + "...";
    }

    private String formatTimeAgo(long modTime) {
        long diff = System.currentTimeMillis() - modTime;
        long minutes = diff / 60000;
        if (minutes < 1) return "now";
        if (minutes < 60) return minutes + " min";
        long hours = minutes / 60;
        if (hours < 24) return hours + " h";
        return (hours / 24) + " d";
    }

    // ========== BOTTOM TOOLBAR (design y: 627-655) ==========

    private void renderBottomToolbar(GuiGraphics g, int dmx, int dmy) {
        g.fill(0, 627, DESIGN_WIDTH, 629, 0xFFE84545);
        g.fill(0, 629, DESIGN_WIDTH, 655, 0xFF14141A);

        String[] icons = {"@", "[]", "/", "=", "o", "O", "T", "im", "))"};
        int activeIndex = 3;
        int x = 10;

        for (int i = 0; i < icons.length; i++) {
            boolean active = i == activeIndex;
            if (active) {
                g.fill(x - 5, 631, x + 17, 651, 0xFFE84545);
            }
            g.drawString(this.font, icons[i], x, 634, 0xFFCCCCCC, false);
            x += 24;
        }

        g.drawString(this.font, "W", DESIGN_WIDTH - 20, 634, 0xFFFFFFFF, false);
    }

    // ========== ONBOARDING (design: x480-765, y497-610) ==========

    private void renderOnboarding(GuiGraphics g, int dmx, int dmy) {
        int bx = 480, by = 497, bw = 285, bh = 113;

        g.fill(bx, by, bx + 3, by + bh, 0xFFE84545);
        g.fill(bx + 3, by, bx + bw, by + bh, 0xEE1A1A1A);

        String title = onboardStep == 0 ? "This is where the editors live" : "Create your first film";
        g.drawString(this.font, title, bx + 12, by + 12, 0xFFFFFFFF, false);

        // Крестик
        boolean hoveringClose = dmx >= bx + bw - 18 && dmx <= bx + bw - 5
                && dmy >= by + 8 && dmy <= by + 20;
        g.drawString(this.font, "x", bx + bw - 16, by + 10, hoveringClose ? 0xFFFFFFFF : 0xFFAAAAAA, false);

        String desc = onboardStep == 0
                ? "Films, models, textures, particles —\neverything opens from here. The bar\ncan be dragged to any edge."
                : "Click \"New film\" to start a new\nproject, or open an existing one\nfrom the list.";

        int descY = by + 30;
        for (String line : desc.split("\n")) {
            g.drawString(this.font, line, bx + 12, descY, 0xFFAAAAAA, false);
            descY += 11;
        }

        g.drawString(this.font, (onboardStep + 1) + " of 2", bx + 12, by + bh - 20, 0xFF888888, false);

        int btnX = bx + bw - 88;
        int btnY = by + bh - 24;
        boolean hoveringBtn = dmx >= btnX && dmx <= btnX + 76 && dmy >= btnY && dmy <= btnY + 20;

        g.fill(btnX, btnY, btnX + 76, btnY + 20, hoveringBtn ? 0xFFFF6666 : 0xFFE84545);
        g.drawCenteredString(this.font, "Continue", btnX + 38, btnY + 6, 0xFFFFFFFF);
    }

    // ========== INPUT ==========

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int dmx = toDesignX(mouseX);
        int dmy = toDesignY(mouseY);

        if (onboardActive && handleOnboardingClick(dmx, dmy)) return true;
        if (handleMenuClicks(dmx, dmy)) return true;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean handleOnboardingClick(int dmx, int dmy) {
        int bx = 480, by = 497, bw = 285, bh = 113;

        if (dmx >= bx + bw - 18 && dmx <= bx + bw - 5 && dmy >= by + 8 && dmy <= by + 20) {
            onboardActive = false;
            return true;
        }

        int btnX = bx + bw - 88;
        int btnY = by + bh - 24;
        if (dmx >= btnX && dmx <= btnX + 76 && dmy >= btnY && dmy <= btnY + 20) {
            onboardStep++;
            if (onboardStep >= 2) onboardActive = false;
            return true;
        }

        return false;
    }

    private boolean handleMenuClicks(int dmx, int dmy) {
        int leftX = 347;
        int y = 382;

        if (isHovering(dmx, dmy, leftX, y, "+New film")) {
            createNewFilm();
            return true;
        }
        y += 26;
        if (isHovering(dmx, dmy, leftX, y, "=Film list")) return true;
        y += 26;
        if (isHovering(dmx, dmy, leftX, y, "oOpen folder")) {
            openProjectsFolder();
            return true;
        }

        return false;
    }

    private boolean isHovering(int dmx, int dmy, int x, int y, String text) {
        int w = this.font.width(text) + 16;
        return dmx >= x && dmx <= x + w && dmy >= y - 2 && dmy <= y + 10;
    }

    private void createNewFilm() {
        String newName = "Film " + (recentProjects.size() + 1);
        FileUtils.createProject(newName);
        loadRecentProjects();
    }

    private void openProjectsFolder() {
        try {
            java.awt.Desktop.getDesktop().open(FileUtils.PROJECTS_FOLDER.toFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}