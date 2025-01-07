package dev.lemonnik.melodeon;

import com.google.gson.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class ResourcePackGenerator {
    public static void generateResourcePack() {
        File currentRunDir = new File(System.getProperty("user.dir"));
        File resourcePackDir = new File(currentRunDir, "resourcepacks/Melodeon/");
        File musicDir = new File(currentRunDir, "MelodeonSounds");

        if (!resourcePackDir.exists() && !resourcePackDir.mkdirs()) {
            Melodeon.LOGGER.error("Failed to create resource pack directory: " + resourcePackDir.getAbsolutePath());
            return;
        }

        copyMusicFiles(musicDir, resourcePackDir);
        generateSoundsJson(resourcePackDir);
        createPackMeta(resourcePackDir);
        createPackPng(resourcePackDir);
    }

    private static void copyMusicFiles(File musicDir, File resourcePackDir) {
        File soundsDir = new File(resourcePackDir, "assets/" + Melodeon.MOD_ID + "/sounds/");

        if (!soundsDir.exists() && !soundsDir.mkdirs()) {
            Melodeon.LOGGER.error("Failed to create sounds directory: " + resourcePackDir.getAbsolutePath());
            return;
        }

        if (!musicDir.exists() || !musicDir.isDirectory()) {
            Melodeon.LOGGER.warn("Music directory not found, creating...");
            if (musicDir.mkdir()) {
                Melodeon.LOGGER.info("Created music directory: " + musicDir.getAbsolutePath());
            } else {
                Melodeon.LOGGER.error("Failed to create music directory: " + musicDir.getAbsolutePath());
                return;
            }
        }

        if (resourcePackDir.exists() && resourcePackDir.isDirectory()) {
            File[] files = resourcePackDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        boolean deleted = file.delete();
                        if (deleted) {
                            Melodeon.LOGGER.info("Deleted file: " + file.getAbsolutePath());
                        } else {
                            Melodeon.LOGGER.warn("Failed to delete file: " + file.getAbsolutePath());
                        }
                    }
                }
            }
        } else {
            Melodeon.LOGGER.error("Resource pack directory not found or is not a directory.");
            return;
        }

        File[] musicFiles = musicDir.listFiles();
        if (musicFiles != null) {
            for (File musicFile : musicFiles) {
                if (musicFile.isFile() && musicFile.getName().endsWith(".ogg")) {
                    File destinationFile = new File(soundsDir, musicFile.getName());
                    try {
                        Files.copy(musicFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        Melodeon.LOGGER.info("Copied music file: " + musicFile.getAbsolutePath() + " to " + destinationFile.getAbsolutePath());
                    } catch (IOException e) {
                        Melodeon.LOGGER.error("Failed to copy file: " + musicFile.getAbsolutePath(), e);
                    }
                }
            }
        } else {
            Melodeon.LOGGER.warn("No music files found in the directory: " + musicDir.getAbsolutePath());
        }
    }


    private static void generateSoundsJson(File resourcePackDir) {
        File soundsDir = new File(resourcePackDir, "assets/melodeon/sounds/");

        if (!soundsDir.exists() || !soundsDir.isDirectory()) {
            Melodeon.LOGGER.warn("Sounds directory not found: " + soundsDir.getAbsolutePath());
            return;
        }

        JsonObject soundsJson = new JsonObject();

        for (File soundFile : Objects.requireNonNull(soundsDir.listFiles())) {
            if (soundFile.isFile() && (soundFile.getName().endsWith(".ogg") || soundFile.getName().endsWith(".wav"))) {
                String soundName = soundFile.getName().substring(0, soundFile.getName().lastIndexOf('.'));

                JsonObject soundEntry = new JsonObject();
                JsonArray soundPaths = new JsonArray();

                soundPaths.add(new JsonPrimitive("melodeon:" + soundName));
                soundEntry.add("sounds", soundPaths);

                soundsJson.add(soundName, soundEntry);
            }
        }

        File assetsDir = new File(resourcePackDir, "assets/melodeon");
        if (!assetsDir.exists() && !assetsDir.mkdirs()) {
            Melodeon.LOGGER.error("Failed to create assets directory: " + assetsDir.getAbsolutePath());
            return;
        }

        File soundsJsonFile = new File(assetsDir, "sounds.json");

        try (FileWriter writer = new FileWriter(soundsJsonFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(soundsJson, writer);
            Melodeon.LOGGER.info("Generated sounds.json at: " + soundsJsonFile.getAbsolutePath());
        } catch (IOException e) {
            Melodeon.LOGGER.error("Failed to write sounds.json", e);
        }
    }

    private static void createPackMeta(File resourcePackDir) {
        File packMetaFile = new File(resourcePackDir, "pack.mcmeta");

        JsonObject packMeta = new JsonObject();
        JsonObject pack = new JsonObject();
        pack.addProperty("pack_format", 15); // Adjust the format number if necessary
        pack.addProperty("description", "Melodeon Resource Pack\nIMPORTANT");
        packMeta.add("pack", pack);

        try (FileWriter writer = new FileWriter(packMetaFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(packMeta, writer);
            Melodeon.LOGGER.info("Generated pack.mcmeta at: " + packMetaFile.getAbsolutePath());
        } catch (IOException e) {
            Melodeon.LOGGER.error("Failed to write pack.mcmeta", e);
        }
    }

    private static void createPackPng(File resourcePackDir) {
        File packPngFile = new File(resourcePackDir, "pack.png");

        try {
            if (!packPngFile.exists()) {
                try (InputStream inputStream = SoundRegisterer.class.getResourceAsStream("/assets/melodeon/icon.png")) {
                    if (inputStream != null) {
                        Files.copy(inputStream, packPngFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        Melodeon.LOGGER.info("Generated pack.png at: " + packPngFile.getAbsolutePath());
                    } else {
                        Melodeon.LOGGER.warn("Default pack.png not found in resources, skipping");
                    }
                }
            }
        } catch (IOException e) {
            Melodeon.LOGGER.error("Failed to create pack.png", e);
        }
    }

}
