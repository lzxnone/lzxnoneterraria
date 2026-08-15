package com.lzxnone.terraria.ui.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.lzxnone.terraria.LzxnoneTerraria;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

public final class ConfigUtil {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve(LzxnoneTerraria.MODID + "-config.json");
    private static JsonObject configCache;
    private static boolean loaded;
    private static boolean dirty;

    private ConfigUtil() {
    }

    public static Path getConfigPath() {
        return CONFIG_PATH;
    }

    public static <T> T read(String path, T defaultValue, Function<JsonElement, T> parser) {
        ensureLoaded();
        JsonElement element = findElement(configCache, path);
        if(element == null || element.isJsonNull()) {
            return defaultValue;
        }

        try {
            T value = parser.apply(element);
            return value == null ? defaultValue : value;
        } catch(RuntimeException exception) {
            return defaultValue;
        }
    }

    public static int readInt(String path, int defaultValue) {
        return read(path, defaultValue, JsonElement::getAsInt);
    }

    public static float readFloat(String path, float defaultValue) {
        return read(path, defaultValue, JsonElement::getAsFloat);
    }

    public static double readDouble(String path, double defaultValue) {
        return read(path, defaultValue, JsonElement::getAsDouble);
    }

    public static boolean readBoolean(String path, boolean defaultValue) {
        return read(path, defaultValue, JsonElement::getAsBoolean);
    }

    public static String readString(String path, String defaultValue) {
        return read(path, defaultValue, JsonElement::getAsString);
    }

    public static void setInt(String path, int value) {
        set(path, new JsonPrimitive(value));
    }

    public static void setFloat(String path, float value) {
        set(path, new JsonPrimitive(value));
    }

    public static void setDouble(String path, double value) {
        set(path, new JsonPrimitive(value));
    }

    public static void setBoolean(String path, boolean value) {
        set(path, new JsonPrimitive(value));
    }

    public static void setString(String path, String value) {
        set(path, new JsonPrimitive(value));
    }

    public static void remove(String path) {
        ensureLoaded();

        String[] parts = splitPath(path);
        if(parts.length == 0) {
            return;
        }

        JsonObject current = configCache;
        for(int i = 0; i < parts.length - 1; i++) {
            JsonElement child = current.get(parts[i]);
            if(child == null || !child.isJsonObject()) {
                return;
            }
            current = child.getAsJsonObject();
        }

        if(current.has(parts[parts.length - 1])) {
            current.remove(parts[parts.length - 1]);
            dirty = true;
        }
    }

    public static void save() {
        save(false);
    }

    public static void save(boolean force) {
        ensureLoaded();
        if(!dirty && !force) {
            return;
        }

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try(Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(configCache, writer);
            }
            dirty = false;
        } catch(IOException exception) {
            LzxnoneTerraria.LOGGER.warn("Failed to save config to {}", CONFIG_PATH, exception);
        }
    }

    public static void reload() {
        configCache = null;
        loaded = false;
        dirty = false;
        ensureLoaded();
    }

    private static void ensureLoaded() {
        if(loaded) {
            return;
        }
        configCache = loadRoot();
        loaded = true;
    }

    private static JsonObject loadRoot() {
        if(!Files.exists(CONFIG_PATH)) {
            return new JsonObject();
        }

        try(Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            JsonElement element = JsonParser.parseReader(reader);
            return element != null && element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
        } catch(IOException | RuntimeException exception) {
            LzxnoneTerraria.LOGGER.warn("Failed to load config from {}", CONFIG_PATH, exception);
            return new JsonObject();
        }
    }

    private static void set(String path, JsonElement value) {
        ensureLoaded();

        String[] parts = splitPath(path);
        if(parts.length == 0) {
            LzxnoneTerraria.LOGGER.warn("Ignored config write with invalid path '{}'", path);
            return;
        }

        JsonObject current = configCache;
        for(int i = 0; i < parts.length - 1; i++) {
            JsonElement child = current.get(parts[i]);
            if(child == null || !child.isJsonObject()) {
                JsonObject childObject = new JsonObject();
                current.add(parts[i], childObject);
                current = childObject;
            } else {
                current = child.getAsJsonObject();
            }
        }

        current.add(parts[parts.length - 1], value);
        dirty = true;
    }

    private static JsonElement findElement(JsonObject root, String path) {
        String[] parts = splitPath(path);
        if(parts.length == 0) {
            return null;
        }

        JsonElement current = root;
        for(String part : parts) {
            if(current == null || !current.isJsonObject()) {
                return null;
            }
            current = current.getAsJsonObject().get(part);
        }
        return current;
    }

    private static String[] splitPath(String path) {
        if(path == null || path.isBlank()) {
            return new String[0];
        }

        String[] parts = path.split("\\.");
        for(String part : parts) {
            if(part.isBlank()) {
                return new String[0];
            }
        }
        return parts;
    }
}
