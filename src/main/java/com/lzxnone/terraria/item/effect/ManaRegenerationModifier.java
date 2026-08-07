package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ManaRegenerationModifier {
    //魔力恢复延迟倍率（0.5 = 减半）
    double getManaRecoverDelayMultiplier(ItemStack stack, LivingEntity entity);
    //额外魔力恢复速率（点/秒），按当前魔力水平计算
    double getManaRegenBonus(ItemStack stack, LivingEntity entity, int currentMana, int maxMana);
}
