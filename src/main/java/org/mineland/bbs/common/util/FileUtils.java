package org.mineland.bbs.common.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    public static final Path BBS_FOLDER = Paths.get(System.getProperty("user.home"), ".bbs");
    public static final Path PROJECTS_FOLDER = BBS_FOLDER.resolve("projects");
    public static final Path CONFIG_FILE = BBS_FOLDER.resolve("config.json");

    static {
        try {
            Files.createDirectories(PROJECTS_FOLDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean fileExists(String path) {
        return Files.exists(Paths.get(path));
    }

    public static File[] listProjects() {
        return PROJECTS_FOLDER.toFile().listFiles(File::isDirectory);
    }

    public static Path createProject(String name) {
        Path projectPath = PROJECTS_FOLDER.resolve(name);
        try {
            Files.createDirectories(projectPath);
            Files.createDirectories(projectPath.resolve("scenes"));
            Files.createDirectories(projectPath.resolve("exports"));
            return projectPath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deleteProject(String name) {
        Path projectPath = PROJECTS_FOLDER.resolve(name);
        try {
            Files.walk(projectPath)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}