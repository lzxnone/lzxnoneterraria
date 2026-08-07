package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface LifeRegenerationModifier {
    //每秒生命回复量（HP/s）
    float getHealthPerSecond(ItemStack stack, LivingEntity entity);
}
