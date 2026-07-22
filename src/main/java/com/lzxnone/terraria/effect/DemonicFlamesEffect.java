package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.particle.CircleParticleOptions;
import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.ConfigUtil;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.utils.ParticleUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class DemonicFlamesEffect extends MobEffect {
    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";

    public static final String DAMAGE_PER_LEVEL_PATH = "effect.demonic_flames.damage_per_level";
    public static final double DAMAGE_PER_LEVEL_DEFAULT = 2.5;
    public static final double DAMAGE_PER_LEVEL_MIN = 0.0;
    public static final double DAMAGE_PER_LEVEL_MAX = 8388600.0;

    public DemonicFlamesEffect() {
        super(MobEffectCategory.HARMFUL, 0xCC2DC7);
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigFactory.loadDoubleConfig(DAMAGE_PER_LEVEL_PATH, configText("demonic_flames_damage_per_level"), configTooltip("demonic_flames_damage_per_level"), DAMAGE_PER_LEVEL_DEFAULT, DAMAGE_PER_LEVEL_MIN, DAMAGE_PER_LEVEL_MAX);
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
        "demonic_flames",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/mob_effect/demonic_flames.png"),
        Component.translatable("effect.lzxnoneterraria.demonic_flames"),
        CONFIG_DATA
    );

    public static final CircleParticleOptions PARTICLE = new CircleParticleOptions(0.075f, 20, new Vector3f(0.8f, 0.176f, 0.78f));

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if(entity.level() instanceof ServerLevel serverLevel) {
            MobEffectInstance instance = entity.getEffect(ModEffects.DEMONIC_FLAMES);
            if(instance != null) {
                if(entity.tickCount % 4 == 0) {
                    entity.invulnerableTime = 5;
                    entity.hurt(entity.damageSources().magic(), (float) getDamagePerLevel() * (amplifier + 1));
                }
            }

            ParticleUtil.addParticles(
                serverLevel, PARTICLE,
                entity.getBoundingBox().getCenter(), new Vec3(0, 0, 0),
                0.2, 1
            );
        }else {
            LzxnoneTerraria.LOGGER.info("11");
        }
        return true;
    }
}
