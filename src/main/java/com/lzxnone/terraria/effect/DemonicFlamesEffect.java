package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.Config;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.particle.CircleParticleOptions;
import com.lzxnone.terraria.utils.ParticleUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class DemonicFlamesEffect extends MobEffect {
    public DemonicFlamesEffect() {
        super(MobEffectCategory.HARMFUL, 0xCC2DC7);
    }

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
                    entity.hurt(entity.damageSources().magic(), (float) Config.demonicFlamesDamagePerLevel * (amplifier + 1));
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
