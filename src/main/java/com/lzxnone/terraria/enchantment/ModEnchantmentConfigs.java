package com.lzxnone.terraria.enchantment;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ModEnchantmentConfigs {
    private static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/ui/nav_enchantment_icon.png");

    public static final ConfigDouble MANA_LEAK_CONSUME_MULTIPLIER = new ConfigDouble("enchantment.mana_leak.consume_multiplier", "mana_leak_consume_multiplier", 1.25D, 0.0D, 10.0D);

    public static final ConfigDouble MANA_EFFICIENCY_CONSUME_MULTIPLIER = new ConfigDouble("enchantment.mana_efficiency.consume_multiplier", "mana_efficiency_consume_multiplier", 0.85D, 0.0D, 10.0D);

    public static final ConfigDouble ARCANE_AMPLIFICATION_DAMAGE_MULTIPLIER = new ConfigDouble("enchantment.arcane_amplification.damage_multiplier", "arcane_amplification_damage_multiplier", 1.15D, 0.0D, 10.0D);

    public static final ConfigDouble SUMMON_AMPLIFICATION_DAMAGE_MULTIPLIER = new ConfigDouble("enchantment.summon_amplification.damage_multiplier", "summon_amplification_damage_multiplier", 1.15D, 0.0D, 10.0D);

    public static final ConfigInt AMMO_EXHAUSTION_CONSUME_BONUS = new ConfigInt("enchantment.ammo_exhaustion.consume_bonus", "ammo_exhaustion_consume_bonus", 1, 0, 1024);

    public static final ConfigDouble BULLET_HELL_NOT_CONSUME_CHANCE = new ConfigDouble("enchantment.bullet_hell.not_consume_chance", "bullet_hell_not_consume_chance", 0.5D, 0.0D, 1.0D);

    public static final ConfigDouble GUNPOWDER_DAMAGE_MULTIPLIER = new ConfigDouble("enchantment.gunpowder.damage_multiplier", "gunpowder_damage_multiplier", 1.15D, 0.0D, 10.0D);

    public static final ConfigDouble STEADY_BREATH_SPREAD_MULTIPLIER = new ConfigDouble("enchantment.steady_breath.spread_multiplier", "steady_breath_spread_multiplier", 0.5D, 0.0D, 10.0D);

    public static final ConfigListItem MANA_LEAK_CONFIG_LIST_ITEM = createConfigListItem(
        "mana_leak",
        "enchantment.lzxnoneterraria.mana_leak",
        () -> MANA_LEAK_CONSUME_MULTIPLIER.load()
    );

    public static final ConfigListItem MANA_EFFICIENCY_CONFIG_LIST_ITEM = createConfigListItem(
        "mana_efficiency",
        "enchantment.lzxnoneterraria.mana_efficiency",
        () -> MANA_EFFICIENCY_CONSUME_MULTIPLIER.load()
    );

    public static final ConfigListItem ARCANE_AMPLIFICATION_CONFIG_LIST_ITEM = createConfigListItem(
        "arcane_amplification",
        "enchantment.lzxnoneterraria.arcane_amplification",
        () -> ARCANE_AMPLIFICATION_DAMAGE_MULTIPLIER.load()
    );

    public static final ConfigListItem SUMMON_AMPLIFICATION_CONFIG_LIST_ITEM = createConfigListItem(
        "summon_amplification",
        "enchantment.lzxnoneterraria.summon_amplification",
        () -> SUMMON_AMPLIFICATION_DAMAGE_MULTIPLIER.load()
    );

    public static final ConfigListItem AMMO_EXHAUSTION_CONFIG_LIST_ITEM = createConfigListItem(
        "ammo_exhaustion",
        "enchantment.lzxnoneterraria.ammo_exhaustion",
        () -> AMMO_EXHAUSTION_CONSUME_BONUS.load()
    );

    public static final ConfigListItem BULLET_HELL_CONFIG_LIST_ITEM = createConfigListItem(
        "bullet_hell",
        "enchantment.lzxnoneterraria.bullet_hell",
        () -> BULLET_HELL_NOT_CONSUME_CHANCE.load()
    );

    public static final ConfigListItem GUNPOWDER_CONFIG_LIST_ITEM = createConfigListItem(
        "gunpowder",
        "enchantment.lzxnoneterraria.gunpowder",
        () -> GUNPOWDER_DAMAGE_MULTIPLIER.load()
    );

    public static final ConfigListItem STEADY_BREATH_CONFIG_LIST_ITEM = createConfigListItem(
        "steady_breath",
        "enchantment.lzxnoneterraria.steady_breath",
        () -> STEADY_BREATH_SPREAD_MULTIPLIER.load()
    );

    public static double getManaLeakConsumeMultiplier() {
        return MANA_LEAK_CONSUME_MULTIPLIER.get();
    }

    public static double getManaEfficiencyConsumeMultiplier() {
        return MANA_EFFICIENCY_CONSUME_MULTIPLIER.get();
    }

    public static double getArcaneAmplificationDamageMultiplier() {
        return ARCANE_AMPLIFICATION_DAMAGE_MULTIPLIER.get();
    }

    public static double getSummonAmplificationDamageMultiplier() {
        return SUMMON_AMPLIFICATION_DAMAGE_MULTIPLIER.get();
    }

    public static int getAmmoExhaustionConsumeBonus() {
        return AMMO_EXHAUSTION_CONSUME_BONUS.get();
    }

    public static double getBulletHellNotConsumeChance() {
        return BULLET_HELL_NOT_CONSUME_CHANCE.get();
    }

    public static double getGunpowderDamageMultiplier() {
        return GUNPOWDER_DAMAGE_MULTIPLIER.get();
    }

    public static double getSteadyBreathSpreadMultiplier() {
        return STEADY_BREATH_SPREAD_MULTIPLIER.get();
    }

    private static ConfigListItem createConfigListItem(String id, String nameKey, Runnable loader) {
        return new ConfigListItem(id, ICON, Component.translatable(nameKey), new IConfigData() {
            @Override
            public void onConfigLoad() {
                loader.run();
            }
        });
    }


}
