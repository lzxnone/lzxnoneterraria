package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.item.ModItems;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class KillModeEffect extends MobEffect {
    public KillModeEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF00FF);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if(!entity.level().isClientSide()) {
            ItemStack stack = entity.getMainHandItem();
            if(!stack.is(ModItems.DEVILS_DEVASTATION.get())) {
                MobEffectInstance effectInstance = new MobEffectInstance(ModEffects.KILL_MODE_COOLDOWN, 100, 0);
                entity.addEffect(effectInstance);

                MobEffectInstance effectInstance2 = entity.getEffect(ModEffects.KILL_MODE);
                if(effectInstance2 != null) entity.removeEffect(effectInstance2.getEffect());
            }
        }
        return true;
    }
}
