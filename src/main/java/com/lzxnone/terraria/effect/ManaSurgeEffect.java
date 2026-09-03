package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class ManaSurgeEffect extends MobEffect {
    public static final ConfigDouble DAMAGE_MULTIPLIER = new ConfigDouble(
        "effect.mana_surge.damage_multiplier",
        "mana_surge_damage_multiplier",
        1.2D,
        0.0D,
        100.0D
    );

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DAMAGE_MULTIPLIER);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "mana_surge",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/mob_effect/mana_surge.png"),
        Component.translatable("effect.lzxnoneterraria.mana_surge"),
        CONFIG_DATA
    );

    public ManaSurgeEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x00E5FF);
    }

    public static float getMagicDamageMultiplier(LivingEntity entity) {
        if(entity == null) return 1.0F;
        MobEffectInstance instance = entity.getEffect(ModEffects.MANA_SURGE);
        if(instance == null) return 1.0F;
        int level = instance.getAmplifier() + 1;
        return (float) Math.pow(DAMAGE_MULTIPLIER.get(), level);
    }
}
