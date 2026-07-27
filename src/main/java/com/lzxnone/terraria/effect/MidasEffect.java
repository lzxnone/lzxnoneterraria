package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.ConfigUtil;
import com.lzxnone.terraria.ui.config.IConfigData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class MidasEffect extends MobEffect {
    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";

    public static final String DROP_CHANCE_PATH = "effect.midas.drop_chance";
    public static final float DROP_CHANCE_DEFAULT = 0.25f;
    public static final float DROP_CHANCE_MIN = 0.0f;
    public static final float DROP_CHANCE_MAX = 1.0f;

    public MidasEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFD700);
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigFactory.loadFloatConfig(DROP_CHANCE_PATH, configText("midas_drop_chance"), configTooltip("midas_drop_chance"), DROP_CHANCE_DEFAULT, DROP_CHANCE_MIN, DROP_CHANCE_MAX);
        }
    };

    private static Component configText(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key);
    }

    private static Component configTooltip(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key + ".tooltip");
    }

    public static float getDropChance() {
        return Math.clamp(ConfigUtil.readFloat(DROP_CHANCE_PATH, DROP_CHANCE_DEFAULT), DROP_CHANCE_MIN, DROP_CHANCE_MAX);
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "midas",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/mob_effect/midas.png"),
        Component.translatable("effect.lzxnoneterraria.midas"),
        CONFIG_DATA
    );
}
