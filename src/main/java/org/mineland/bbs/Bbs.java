package org.mineland.bbs;

import org.mineland.bbs.common.config.BBSConfig;
import org.mineland.bbs.common.util.FFmpegUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("bbs")
public class Bbs {

    public static final String MOD_ID = "bbs";
    public static final Logger LOGGER = LogManager.getLogger();

    public Bbs() {
        LOGGER.info("Block Buster Studio - Constructor called");
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class CommonEvents {

        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void onCommonSetup(FMLCommonSetupEvent event) {
            LOGGER.info("Block Buster Studio - Common Setup");
            BBSConfig.getInstance();
            FFmpegUtils.initialize();
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {

        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("Block Buster Studio - Client Setup");
        }
    }
}