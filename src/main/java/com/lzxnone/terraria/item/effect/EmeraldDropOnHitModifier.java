package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface EmeraldDropOnHitModifier {
    //击中敌人时掉落绿宝石的概率（0~1）
    double getEmeraldDropChance(ItemStack stack, LivingEntity entity);
}
