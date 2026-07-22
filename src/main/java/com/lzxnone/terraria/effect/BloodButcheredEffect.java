package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.ConfigUtil;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.ParticleUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class BloodButcheredEffect extends MobEffect {
    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";

    public static final String DAMAGE_PER_LEVEL_PATH = "effect.blood_butchered.damage_per_level";
    public static final double DAMAGE_PER_LEVEL_DEFAULT = 0.5;
    public static final double DAMAGE_PER_LEVEL_MIN = 0.0;
    public static final double DAMAGE_PER_LEVEL_MAX = 8388600.0;

    public BloodButcheredEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF0000);
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigFactory.loadDoubleConfig(DAMAGE_PER_LEVEL_PATH, configText("blood_butcherer_damage_per_level"), configTooltip("blood_butcherer_damage_per_level"), DAMAGE_PER_LEVEL_DEFAULT, DAMAGE_PER_LEVEL_MIN, DAMAGE_PER_LEVEL_MAX);
        }
    };

    private static Component configText(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key);
    }

    private static Component configTooltip(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key + ".tooltip");
    }

    public static double getDamagePerLevel() {
        return Math.clamp(ConfigUtil.readDouble(DAMAGE_PER_LEVEL_PATH, DAMAGE_PER_LEVEL_DEFAULT), DAMAGE_PER_LEVEL_MIN, DAMAGE_PER_LEVEL_MAX);
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "blood_butchered",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/mob_effect/blood_butchered.png"),
        Component.translatable("effect.lzxnoneterraria.blood_butchered"),
        CONFIG_DATA
    );

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if(entity.level() instanceof ServerLevel serverLevel) {
            MobEffectInstance instance = entity.getEffect(ModEffects.BLOOD_BUTCHERED);
            if(instance != null) {
                if(instance.getDuration() % 20 == 0) {
                    entity.hurt(entity.damageSources().magic(), (float) getDamagePerLevel() * (amplifier + 1));
                }
            }

            ParticleUtil.addParticles(
                serverLevel, ModParticles.BLOOD_BUTCHERED_PARTICLE.get(),
                entity.getBoundingBox().getCenter(), new Vec3(0, 0, 0),
                Math.min((amplifier + 1) * 0.1, 0.5), Math.min(amplifier + 1, 5)
            );
        }
        return true;
    }
}
