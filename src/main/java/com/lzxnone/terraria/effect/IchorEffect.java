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

public class IchorEffect extends MobEffect {
    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";

    public static final String DAMAGE_BONUS_PER_LEVEL_PATH = "effect.ichor.damage_bonus_per_level";
    public static final float DAMAGE_BONUS_PER_LEVEL_DEFAULT = 0.15f;
    public static final float DAMAGE_BONUS_PER_LEVEL_MIN = 0.0f;
    public static final float DAMAGE_BONUS_PER_LEVEL_MAX = 10.0f;

    public IchorEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFFF00);
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigFactory.loadFloatConfig(DAMAGE_BONUS_PER_LEVEL_PATH, configText("ichor_damage_bonus_per_level"), configTooltip("ichor_damage_bonus_per_level"), DAMAGE_BONUS_PER_LEVEL_DEFAULT, DAMAGE_BONUS_PER_LEVEL_MIN, DAMAGE_BONUS_PER_LEVEL_MAX);
        }
    };

    private static Component configText(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key);
    }

    private static Component configTooltip(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key + ".tooltip");
    }

    public static float getDamageBonusPerLevel() {
        return Math.clamp(ConfigUtil.readFloat(DAMAGE_BONUS_PER_LEVEL_PATH, DAMAGE_BONUS_PER_LEVEL_DEFAULT), DAMAGE_BONUS_PER_LEVEL_MIN, DAMAGE_BONUS_PER_LEVEL_MAX);
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "ichor",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/mob_effect/ichor.png"),
        Component.translatable("effect.lzxnoneterraria.ichor"),
        CONFIG_DATA
    );
}
