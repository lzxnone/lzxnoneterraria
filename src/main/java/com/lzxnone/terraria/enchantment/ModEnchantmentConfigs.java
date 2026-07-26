package com.lzxnone.terraria.enchantment;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.ConfigUtil;
import com.lzxnone.terraria.ui.config.IConfigData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ModEnchantmentConfigs {
    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";
    private static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/ui/nav_enchantment_icon.png");

    public static final String MANA_LEAK_CONSUME_MULTIPLIER_PATH = "enchantment.mana_leak.consume_multiplier";
    public static final double MANA_LEAK_CONSUME_MULTIPLIER_DEFAULT = 1.25D;
    public static final double MANA_LEAK_CONSUME_MULTIPLIER_MIN = 0.0D;
    public static final double MANA_LEAK_CONSUME_MULTIPLIER_MAX = 10.0D;

    public static final String MANA_EFFICIENCY_CONSUME_MULTIPLIER_PATH = "enchantment.mana_efficiency.consume_multiplier";
    public static final double MANA_EFFICIENCY_CONSUME_MULTIPLIER_DEFAULT = 0.85D;
    public static final double MANA_EFFICIENCY_CONSUME_MULTIPLIER_MIN = 0.0D;
    public static final double MANA_EFFICIENCY_CONSUME_MULTIPLIER_MAX = 10.0D;

    public static final String MANA_GATHERING_RECOVER_MULTIPLIER_PATH = "enchantment.mana_gathering.recover_multiplier";
    public static final double MANA_GATHERING_RECOVER_MULTIPLIER_DEFAULT = 1.25D;
    public static final double MANA_GATHERING_RECOVER_MULTIPLIER_MIN = 0.0D;
    public static final double MANA_GATHERING_RECOVER_MULTIPLIER_MAX = 10.0D;

    public static final String MANA_GATHERING_CURSE_RECOVER_MULTIPLIER_PATH = "enchantment.mana_gathering_curse.recover_multiplier";
    public static final double MANA_GATHERING_CURSE_RECOVER_MULTIPLIER_DEFAULT = 0.5D;
    public static final double MANA_GATHERING_CURSE_RECOVER_MULTIPLIER_MIN = 0.0D;
    public static final double MANA_GATHERING_CURSE_RECOVER_MULTIPLIER_MAX = 10.0D;

    public static final String ARCANE_AMPLIFICATION_DAMAGE_MULTIPLIER_PATH = "enchantment.arcane_amplification.damage_multiplier";
    public static final double ARCANE_AMPLIFICATION_DAMAGE_MULTIPLIER_DEFAULT = 1.15D;
    public static final double ARCANE_AMPLIFICATION_DAMAGE_MULTIPLIER_MIN = 0.0D;
    public static final double ARCANE_AMPLIFICATION_DAMAGE_MULTIPLIER_MAX = 10.0D;

    public static final String SUMMON_AMPLIFICATION_DAMAGE_MULTIPLIER_PATH = "enchantment.summon_amplification.damage_multiplier";
    public static final double SUMMON_AMPLIFICATION_DAMAGE_MULTIPLIER_DEFAULT = 1.15D;
    public static final double SUMMON_AMPLIFICATION_DAMAGE_MULTIPLIER_MIN = 0.0D;
    public static final double SUMMON_AMPLIFICATION_DAMAGE_MULTIPLIER_MAX = 10.0D;

    public static final ConfigListItem MANA_LEAK_CONFIG_LIST_ITEM = createConfigListItem(
        "mana_leak",
        "enchantment.lzxnoneterraria.mana_leak",
        () -> ConfigFactory.loadDoubleConfig(MANA_LEAK_CONSUME_MULTIPLIER_PATH, configText("mana_leak_consume_multiplier"), configTooltip("mana_leak_consume_multiplier"), MANA_LEAK_CONSUME_MULTIPLIER_DEFAULT, MANA_LEAK_CONSUME_MULTIPLIER_MIN, MANA_LEAK_CONSUME_MULTIPLIER_MAX)
    );

    public static final ConfigListItem MANA_EFFICIENCY_CONFIG_LIST_ITEM = createConfigListItem(
        "mana_efficiency",
        "enchantment.lzxnoneterraria.mana_efficiency",
        () -> ConfigFactory.loadDoubleConfig(MANA_EFFICIENCY_CONSUME_MULTIPLIER_PATH, configText("mana_efficiency_consume_multiplier"), configTooltip("mana_efficiency_consume_multiplier"), MANA_EFFICIENCY_CONSUME_MULTIPLIER_DEFAULT, MANA_EFFICIENCY_CONSUME_MULTIPLIER_MIN, MANA_EFFICIENCY_CONSUME_MULTIPLIER_MAX)
    );

    public static final ConfigListItem MANA_GATHERING_CONFIG_LIST_ITEM = createConfigListItem(
        "mana_gathering",
        "enchantment.lzxnoneterraria.mana_gathering",
        () -> ConfigFactory.loadDoubleConfig(MANA_GATHERING_RECOVER_MULTIPLIER_PATH, configText("mana_gathering_recover_multiplier"), configTooltip("mana_gathering_recover_multiplier"), MANA_GATHERING_RECOVER_MULTIPLIER_DEFAULT, MANA_GATHERING_RECOVER_MULTIPLIER_MIN, MANA_GATHERING_RECOVER_MULTIPLIER_MAX)
    );

    public static final ConfigListItem MANA_GATHERING_CURSE_CONFIG_LIST_ITEM = createConfigListItem(
        "mana_gathering_curse",
        "enchantment.lzxnoneterraria.mana_gathering_curse",
        () -> ConfigFactory.loadDoubleConfig(MANA_GATHERING_CURSE_RECOVER_MULTIPLIER_PATH, configText("mana_gathering_curse_recover_multiplier"), configTooltip("mana_gathering_curse_recover_multiplier"), MANA_GATHERING_CURSE_RECOVER_MULTIPLIER_DEFAULT, MANA_GATHERING_CURSE_RECOVER_MULTIPLIER_MIN, MANA_GATHERING_CURSE_RECOVER_MULTIPLIER_MAX)
    );

    public static final ConfigListItem ARCANE_AMPLIFICATION_CONFIG_LIST_ITEM = createConfigListItem(
        "arcane_amplification",
        "enchantment.lzxnoneterraria.arcane_amplification",
        () -> ConfigFactory.loadDoubleConfig(ARCANE_AMPLIFICATION_DAMAGE_MULTIPLIER_PATH, configText("arcane_amplification_damage_multiplier"), configTooltip("arcane_amplification_damage_multiplier"), ARCANE_AMPLIFICATION_DAMAGE_MULTIPLIER_DEFAULT, ARCANE_AMPLIFICATION_DAMAGE_MULTIPLIER_MIN, ARCANE_AMPLIFICATION_DAMAGE_MULTIPLIER_MAX)
    );

    public static final ConfigListItem SUMMON_AMPLIFICATION_CONFIG_LIST_ITEM = createConfigListItem(
        "summon_amplification",
        "enchantment.lzxnoneterraria.summon_amplification",
        () -> ConfigFactory.loadDoubleConfig(SUMMON_AMPLIFICATION_DAMAGE_MULTIPLIER_PATH, configText("summon_amplification_damage_multiplier"), configTooltip("summon_amplification_damage_multiplier"), SUMMON_AMPLIFICATION_DAMAGE_MULTIPLIER_DEFAULT, SUMMON_AMPLIFICATION_DAMAGE_MULTIPLIER_MIN, SUMMON_AMPLIFICATION_DAMAGE_MULTIPLIER_MAX)
    );

    public static double getManaLeakConsumeMultiplier() {
        return readDouble(MANA_LEAK_CONSUME_MULTIPLIER_PATH, MANA_LEAK_CONSUME_MULTIPLIER_DEFAULT, MANA_LEAK_CONSUME_MULTIPLIER_MIN, MANA_LEAK_CONSUME_MULTIPLIER_MAX);
    }

    public static double getManaEfficiencyConsumeMultiplier() {
        return readDouble(MANA_EFFICIENCY_CONSUME_MULTIPLIER_PATH, MANA_EFFICIENCY_CONSUME_MULTIPLIER_DEFAULT, MANA_EFFICIENCY_CONSUME_MULTIPLIER_MIN, MANA_EFFICIENCY_CONSUME_MULTIPLIER_MAX);
    }

    public static double getManaGatheringRecoverMultiplier() {
        return readDouble(MANA_GATHERING_RECOVER_MULTIPLIER_PATH, MANA_GATHERING_RECOVER_MULTIPLIER_DEFAULT, MANA_GATHERING_RECOVER_MULTIPLIER_MIN, MANA_GATHERING_RECOVER_MULTIPLIER_MAX);
    }

    public static double getManaGatheringCurseRecoverMultiplier() {
        return readDouble(MANA_GATHERING_CURSE_RECOVER_MULTIPLIER_PATH, MANA_GATHERING_CURSE_RECOVER_MULTIPLIER_DEFAULT, MANA_GATHERING_CURSE_RECOVER_MULTIPLIER_MIN, MANA_GATHERING_CURSE_RECOVER_MULTIPLIER_MAX);
    }

    public static double getArcaneAmplificationDamageMultiplier() {
        return readDouble(ARCANE_AMPLIFICATION_DAMAGE_MULTIPLIER_PATH, ARCANE_AMPLIFICATION_DAMAGE_MULTIPLIER_DEFAULT, ARCANE_AMPLIFICATION_DAMAGE_MULTIPLIER_MIN, ARCANE_AMPLIFICATION_DAMAGE_MULTIPLIER_MAX);
    }

    public static double getSummonAmplificationDamageMultiplier() {
        return readDouble(SUMMON_AMPLIFICATION_DAMAGE_MULTIPLIER_PATH, SUMMON_AMPLIFICATION_DAMAGE_MULTIPLIER_DEFAULT, SUMMON_AMPLIFICATION_DAMAGE_MULTIPLIER_MIN, SUMMON_AMPLIFICATION_DAMAGE_MULTIPLIER_MAX);
    }

    private static ConfigListItem createConfigListItem(String id, String nameKey, Runnable loader) {
        return new ConfigListItem(id, ICON, Component.translatable(nameKey), new IConfigData() {
            @Override
            public void onConfigLoad() {
                loader.run();
            }
        });
    }

    private static Component configText(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key);
    }

    private static Component configTooltip(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key + ".tooltip");
    }

    private static double readDouble(String path, double defaultValue, double minValue, double maxValue) {
        return Math.clamp(ConfigUtil.readDouble(path, defaultValue), minValue, maxValue);
    }
}
