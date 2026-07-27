package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.particle.DustParticleOptions;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class AcidVenomEffect extends MobEffect {
    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";

    public static final String DAMAGE_PER_LEVEL_PATH = "effect.acid_venom.damage_per_level";
    public static final float DAMAGE_PER_LEVEL_DEFAULT = 2.0f;
    public static final float DAMAGE_PER_LEVEL_MIN = 0.0f;
    public static final float DAMAGE_PER_LEVEL_MAX = 8388600.0f;

    public AcidVenomEffect() {
        super(MobEffectCategory.HARMFUL, 0x8A2BE2);
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigFactory.loadFloatConfig(DAMAGE_PER_LEVEL_PATH, configText("acid_venom_damage_per_level"), configTooltip("acid_venom_damage_per_level"), DAMAGE_PER_LEVEL_DEFAULT, DAMAGE_PER_LEVEL_MIN, DAMAGE_PER_LEVEL_MAX);
        }
    };

    private static Component configText(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key);
    }

    private static Component configTooltip(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key + ".tooltip");
    }

    public static float getDamagePerLevel() {
        return Math.clamp(ConfigUtil.readFloat(DAMAGE_PER_LEVEL_PATH, DAMAGE_PER_LEVEL_DEFAULT), DAMAGE_PER_LEVEL_MIN, DAMAGE_PER_LEVEL_MAX);
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "acid_venom",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/mob_effect/acid_venom.png"),
        Component.translatable("effect.lzxnoneterraria.acid_venom"),
        CONFIG_DATA
    );

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.075f, 0.5f, 40, false, new Vector3f[]{
            new Vector3f(0.0F, 0.0F, 0.0F),
            new Vector3f(0.06F, 0.06F, 0.06F)
        }
    );

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if(entity.level() instanceof ServerLevel serverLevel) {
            if(entity.tickCount % 20 == 0) {
                if(entity.hurt(entity.damageSources().magic(), getDamagePerLevel() * (amplifier + 1))) entity.invulnerableTime = 10;
            }
            ParticleUtil.addParticles(
                serverLevel, PARTICLE,
                entity.getBoundingBox().getCenter(), new Vec3(0, 0, 0),
                0.1, 1
            );
        }
        return true;
    }
}
