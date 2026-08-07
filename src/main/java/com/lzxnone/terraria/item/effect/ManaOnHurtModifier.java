package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ManaOnHurtModifier {
    //受到伤害时恢复魔力倍率（受 1 点伤害恢复 multiplier 点魔力）
    double getManaOnHurtMultiplier(ItemStack stack, LivingEntity entity);
}
