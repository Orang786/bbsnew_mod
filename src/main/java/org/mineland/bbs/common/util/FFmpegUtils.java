package org.mineland.bbs.common.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FFmpegUtils {

    private static String ffmpegPath = "";
    private static boolean isInitialized = false;

    public static void initialize() {
        if (isInitialized) return;

        // Пытаемся найти FFmpeg
        String[] paths = {
                "ffmpeg",
                "C:\\ffmpeg\\bin\\ffmpeg.exe",
                "/usr/bin/ffmpeg",
                "/usr/local/bin/ffmpeg",
                "/opt/homebrew/bin/ffmpeg"
        };

        for (String path : paths) {
            if (isFFmpegValid(path)) {
                ffmpegPath = path;
                isInitialized = true;
                return;
            }
        }

        // Если не нашли, загружаем из конфига
        try {
            if (Files.exists(FileUtils.CONFIG_FILE)) {
                String content = Files.readString(FileUtils.CONFIG_FILE);
                JsonObject config = JsonParser.parseString(content).getAsJsonObject();

                if (config.has("ffmpeg_path")) {
                    String configPath = config.get("ffmpeg_path").getAsString();
                    if (isFFmpegValid(configPath)) {
                        ffmpegPath = configPath;
                        isInitialized = true;
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        isInitialized = true;
    }

    public static boolean isFFmpegValid(String path) {
        try {
            ProcessBuilder pb = new ProcessBuilder(path, "-version");
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isFFmpegFound() {
        initialize();
        return !ffmpegPath.isEmpty();
    }

    public static String getFFmpegPath() {
        initialize();
        return ffmpegPath;
    }

    public static void setFFmpegPath(String path) {
        if (isFFmpegValid(path)) {
            ffmpegPath = path;
            saveConfig();
        }
    }

    public static String getFFmpegVersion() {
        if (!isFFmpegFound()) return "Not found";

        try {
            ProcessBuilder pb = new ProcessBuilder(ffmpegPath, "-version");
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
            );

            String firstLine = reader.readLine();
            reader.close();

            return firstLine != null ? firstLine : "Unknown";
        } catch (Exception e) {
            return "Error";
        }
    }

    private static void saveConfig() {
        try {
            JsonObject config = new JsonObject();
            config.addProperty("ffmpeg_path", ffmpegPath);

            Files.createDirectories(FileUtils.BBS_FOLDER);
            Files.writeString(FileUtils.CONFIG_FILE, config.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}