package com.lzxnone.terraria.ui.config.struct;

import net.minecraft.network.chat.Component;

public abstract class ConfigStruct<T> {
    public static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";

    public final String path;
    public final String translationKey;

    protected ConfigStruct(String path) {
        this(path, defaultTranslationKey(path));
    }

    protected ConfigStruct(String path, String translationKey) {
        if(path == null || path.isBlank()) {
            throw new IllegalArgumentException("Config path cannot be blank.");
        }
        if(translationKey == null || translationKey.isBlank()) {
            throw new IllegalArgumentException("Config translation key cannot be blank.");
        }
        this.path = path;
        this.translationKey = translationKey;
    }

    public Component configText() {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + translationKey);
    }

    public Component configTooltip() {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + translationKey + ".tooltip");
    }

    public abstract T get();

    public abstract void load();

    public static void loadAll(ConfigStruct<?>... configs) {
        for(ConfigStruct<?> config : configs) {
            config.load();
        }
    }

    protected static String defaultTranslationKey(String path) {
        String key = path;
        int firstDotIndex = path.indexOf('.');
        if(firstDotIndex >= 0 && firstDotIndex < path.length() - 1) {
            key = path.substring(firstDotIndex + 1);
        }
        return key.replace('.', '_');
    }
}
