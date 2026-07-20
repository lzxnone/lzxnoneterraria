package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.Config;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.weapon.melee.DevilsDevastation;
import com.lzxnone.terraria.particle.CircleParticleOptions;
import com.lzxnone.terraria.utils.ParticleUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class KillModeEffect extends MobEffect {
    public KillModeEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xCC2DC7);
    }

    public static final CircleParticleOptions PARTICLE = new CircleParticleOptions(0.075f, 20, new Vector3f(0.8f, 0.176f, 0.78f));

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if(entity.level() instanceof ServerLevel serverLevel) {
            ItemStack stack = entity.getMainHandItem();
            if(stack.is(ModItems.DEVILS_DEVASTATION.get())) {
                if(entity.getDeltaMovement().length() > 0.001) {
                    ParticleUtil.addParticles(
                        serverLevel, PARTICLE,
                        entity.getBoundingBox().getCenter().add(new Vec3(0, Math.sin(entity.tickCount / 5.0), 0)), new Vec3(0, 0, 0),
                        0, 1
                    );
                    ParticleUtil.addParticles(
                        serverLevel, PARTICLE,
                        entity.getBoundingBox().getCenter().add(new Vec3(0, -Math.sin(entity.tickCount / 5.0), 0)), new Vec3(0, 0, 0),
                        0, 1
                    );
                }
            }else {
                MobEffectInstance effectInstance = new MobEffectInstance(ModEffects.KILL_MODE_COOLDOWN, Config.devilsDevastationKillModeCooldownTime, 0);
                entity.addEffect(effectInstance);

                MobEffectInstance effectInstance2 = entity.getEffect(ModEffects.KILL_MODE);
                if(effectInstance2 != null) entity.removeEffect(effectInstance2.getEffect());
            }
        }
        return true;
    }
}
