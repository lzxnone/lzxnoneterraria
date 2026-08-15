package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class CelledEffect extends MobEffect {
    public static final ConfigFloat DAMAGE_PER_LEVEL = new ConfigFloat(
        "effect.celled.damage_per_level",
        "celled_damage_per_level",
        5.0F,
        0.0F,
        8388600.0F
    );

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DAMAGE_PER_LEVEL);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "celled",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/mob_effect/celled.png"),
        Component.translatable("effect.lzxnoneterraria.celled"),
        CONFIG_DATA
    );

    public CelledEffect() {
        super(MobEffectCategory.HARMFUL, 0x318CE7);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if(entity.level() instanceof ServerLevel && entity.tickCount % 20 == 0) {
            if(entity.hurt(entity.damageSources().magic(), DAMAGE_PER_LEVEL.get() * (amplifier + 1))) {
                entity.invulnerableTime = 10;
            }
        }
        return true;
    }
}
