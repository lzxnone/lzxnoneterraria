package com.lzxnone.terraria.item.ammo;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.ConfigUtil;
import com.lzxnone.terraria.ui.config.IConfigData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class AmmoConfig {
    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";

    private AmmoConfig() {
    }

    public static String path(String id, String key) {
        return "ammo." + id + "." + key;
    }

    public static String translationKey(String id, String key) {
        return id + "_" + key;
    }

    public static void loadFloat(String id, String key, float defaultValue, float minValue, float maxValue) {
        ConfigFactory.loadFloatConfig(
            path(id, key),
            configText(translationKey(id, key)),
            configTooltip(translationKey(id, key)),
            defaultValue,
            minValue,
            maxValue
        );
    }

    public static void loadDouble(String id, String key, double defaultValue, double minValue, double maxValue) {
        ConfigFactory.loadDoubleConfig(
            path(id, key),
            configText(translationKey(id, key)),
            configTooltip(translationKey(id, key)),
            defaultValue,
            minValue,
            maxValue
        );
    }

    public static void loadInt(String id, String key, int defaultValue, int minValue, int maxValue) {
        ConfigFactory.loadIntConfig(
            path(id, key),
            configText(translationKey(id, key)),
            configTooltip(translationKey(id, key)),
            defaultValue,
            minValue,
            maxValue
        );
    }

    public static float readFloat(String id, String key, float defaultValue, float minValue, float maxValue) {
        return Math.clamp(ConfigUtil.readFloat(path(id, key), defaultValue), minValue, maxValue);
    }

    public static double readDouble(String id, String key, double defaultValue, double minValue, double maxValue) {
        return Math.clamp(ConfigUtil.readDouble(path(id, key), defaultValue), minValue, maxValue);
    }

    public static int readInt(String id, String key, int defaultValue, int minValue, int maxValue) {
        return Math.clamp(ConfigUtil.readInt(path(id, key), defaultValue), minValue, maxValue);
    }

    public static ConfigListItem createListItem(String id, IConfigData configData) {
        return new ConfigListItem(
            id,
            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/" + id + ".png"),
            Component.translatable("item.lzxnoneterraria." + id),
            configData
        );
    }

    private static Component configText(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key);
    }

    private static Component configTooltip(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key + ".tooltip");
    }
}
