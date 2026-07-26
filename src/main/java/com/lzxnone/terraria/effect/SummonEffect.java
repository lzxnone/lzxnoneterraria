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

public class SummonEffect extends MobEffect {
    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";

    public static final String SUMMON_COUNT_PER_LEVEL_PATH = "effect.summon.summon_count_per_level";
    public static final int SUMMON_COUNT_PER_LEVEL_DEFAULT = 1;
    public static final int SUMMON_COUNT_PER_LEVEL_MIN = 0;
    public static final int SUMMON_COUNT_PER_LEVEL_MAX = 10;

    public SummonEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x34D95A);
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigFactory.loadIntConfig(SUMMON_COUNT_PER_LEVEL_PATH, configText("summon_summon_count_per_level"), configTooltip("summon_summon_count_per_level"), SUMMON_COUNT_PER_LEVEL_DEFAULT, SUMMON_COUNT_PER_LEVEL_MIN, SUMMON_COUNT_PER_LEVEL_MAX);
        }
    };

    private static Component configText(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key);
    }

    private static Component configTooltip(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key + ".tooltip");
    }

    public static int getSummonCountPerLevel() {
        return Math.clamp(ConfigUtil.readInt(SUMMON_COUNT_PER_LEVEL_PATH, SUMMON_COUNT_PER_LEVEL_DEFAULT), SUMMON_COUNT_PER_LEVEL_MIN, SUMMON_COUNT_PER_LEVEL_MAX);
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "summon",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/mob_effect/summon.png"),
        Component.translatable("effect.lzxnoneterraria.summon"),
        CONFIG_DATA
    );
}
