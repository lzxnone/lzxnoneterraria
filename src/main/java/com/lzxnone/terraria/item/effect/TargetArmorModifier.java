package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface TargetArmorModifier {
    //对目标护甲的减少修饰（正数 = 减少的目标护甲值）
    default double getTargetArmorReduction(ItemStack stack, LivingEntity entity, LivingEntity target) { return 0.0D; }

    //对目标护甲韧性的减少修饰（正数 = 减少的目标护甲韧性值）
    default double getTargetArmorToughnessReduction(ItemStack stack, LivingEntity entity, LivingEntity target) { return 0.0D; }
}
