package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface MobEffectImmunityModifier {
    //中毒
    default boolean isPoisonImmunity(ItemStack stack, LivingEntity entity) { return false; }

    //凋零
    default boolean isWitherImmunity(ItemStack stack, LivingEntity entity) { return false; }

    //黑暗
    default boolean isDarknessImmunity(ItemStack stack, LivingEntity entity) { return false; }

    //缓慢
    default boolean isSlownessImmunity(ItemStack stack, LivingEntity entity) { return false; }

    //缓慢
    default boolean isWeaknessImmunity(ItemStack stack, LivingEntity entity) { return false; }

    //流血
    default boolean isBleedImmunity(ItemStack stack, LivingEntity entity) { return false; }

    //困惑
    default boolean isConfusedImmunity(ItemStack stack, LivingEntity entity) { return false; }

    //漂浮
    default boolean isLevitationImmunity(ItemStack stack, LivingEntity entity) { return false; }

    //挖掘疲劳
    default boolean isMiningFatigueImmunity(ItemStack stack, LivingEntity entity) { return false; }
}
