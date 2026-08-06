package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class PaladinsShieldEffect extends MobEffect {
    public static final ConfigFloat DAMAGE_ABSORPTION_RATIO = new ConfigFloat(
        "effect.paladins_shield.damage_absorption_ratio",
        "paladins_shield_damage_absorption_ratio",
        0.25f,
        0.0f,
        1.0f
    );

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DAMAGE_ABSORPTION_RATIO);
        }
    };

    public static float getDamageAbsorptionRatio() {
        return DAMAGE_ABSORPTION_RATIO.get();
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "paladins_shield",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/mob_effect/paladins_shield.png"),
        Component.translatable("effect.lzxnoneterraria.paladins_shield"),
        CONFIG_DATA
    );

    public PaladinsShieldEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x4FC3F7);
    }
}
