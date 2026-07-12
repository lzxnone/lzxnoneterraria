package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.Config;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.ParticleUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class BloodButcheredEffect extends MobEffect {
    public BloodButcheredEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF0000);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if(entity.level().isClientSide()) {
            Vec3 center = entity.getBoundingBox().getCenter();
            ParticleUtil.addParticles(
                entity.level(), ModParticles.BLOOD_BUTCHERED_PARTICLE.get(),
                center, 0.05,
                new Vec3(0, 0, 0), Math.min((amplifier + 1) * 0.1, 0.5),
                Math.min(amplifier + 1, 5)
            );
        }else {
            MobEffectInstance instance = entity.getEffect(ModEffects.BLOOD_BUTCHERED);
            if(instance != null) {
                if(instance.getDuration() % 20 == 0) {
                    entity.hurt(entity.damageSources().magic(), (float) Config.bloodButchererDamagePerLevel * (amplifier + 1));
                }
            }
        }
        return true;
    }
}
