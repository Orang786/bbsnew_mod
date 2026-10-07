package org.mineland.bbs.common.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.mineland.bbs.common.util.FileUtils;

import java.nio.file.Files;

public class BBSConfig {

    private static BBSConfig instance;

    public String ffmpegPath = "";
    public String lastProject = "";
    public String theme = "dark"; // dark, light
    public int uiScale = 100;
    public boolean showTips = true;
    public boolean firstTimeSetupDone = false;
    public String layoutPreset = "default";

    public static BBSConfig getInstance() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    public static BBSConfig load() {
        try {
            if (Files.exists(FileUtils.CONFIG_FILE)) {
                String content = Files.readString(FileUtils.CONFIG_FILE);
                JsonObject json = com.google.gson.JsonParser.parseString(content).getAsJsonObject();

                BBSConfig config = new BBSConfig();
                if (json.has("ffmpeg_path")) config.ffmpegPath = json.get("ffmpeg_path").getAsString();
                if (json.has("last_project")) config.lastProject = json.get("last_project").getAsString();
                if (json.has("theme")) config.theme = json.get("theme").getAsString();
                if (json.has("ui_scale")) config.uiScale = json.get("ui_scale").getAsInt();
                if (json.has("show_tips")) config.showTips = json.get("show_tips").getAsBoolean();
                if (json.has("first_time_setup_done"))
                    config.firstTimeSetupDone = json.get("first_time_setup_done").getAsBoolean();
                if (json.has("layout_preset"))
                    config.layoutPreset = json.get("layout_preset").getAsString();

                return config;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new BBSConfig();
    }

    public void save() {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("ffmpeg_path", ffmpegPath);
            json.addProperty("last_project", lastProject);
            json.addProperty("theme", theme);
            json.addProperty("ui_scale", uiScale);
            json.addProperty("show_tips", showTips);
            json.addProperty("first_time_setup_done", firstTimeSetupDone);
            json.addProperty("layout_preset", layoutPreset);

            Files.createDirectories(FileUtils.BBS_FOLDER);
            Files.writeString(FileUtils.CONFIG_FILE, json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}