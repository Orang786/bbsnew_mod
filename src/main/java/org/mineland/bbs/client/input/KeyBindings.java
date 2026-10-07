package org.mineland.bbs.client.input;

import org.mineland.bbs.Bbs;
import org.mineland.bbs.client.gui.screen.dashboard.DashboardScreen;
import org.mineland.bbs.client.gui.screen.setup.FirstTimeSetupScreen;
import org.mineland.bbs.common.config.BBSConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = Bbs.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class KeyBindings {

    public static final KeyMapping OPEN_BBS = new KeyMapping(
            "key.bbs.open",
            GLFW.GLFW_KEY_0,
            "key.categories.misc"
    );

    @SuppressWarnings("DataFlowIssue")
    @net.minecraftforge.eventbus.api.SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (OPEN_BBS.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.screen == null) {
                BBSConfig config = BBSConfig.getInstance();

                if (!config.firstTimeSetupDone) {
                    mc.setScreen(new FirstTimeSetupScreen());
                } else {
                    mc.setScreen(new DashboardScreen());
                }
            }
        }
    }
}