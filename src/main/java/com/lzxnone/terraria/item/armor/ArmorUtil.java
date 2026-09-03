package com.lzxnone.terraria.item.armor;

import com.lzxnone.terraria.item.effect.ManaCostModifier;
import com.lzxnone.terraria.item.effect.MaxManaModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;

public class ArmorUtil {

    public static void forEachArmor(LivingEntity entity, BiConsumer<Object, ItemStack> consumer) {
        if(entity == null) return;
        for(ItemStack stack : entity.getArmorSlots()) {
            if(!stack.isEmpty()) {
                consumer.accept(stack.getItem(), stack);
            }
        }
    }

    // 最大魔力加成：累加所有防具的加成
    public static int getMaxManaBonus(LivingEntity entity) {
        int[] bonus = {0};
        forEachArmor(entity, (armor, stack) -> {
            if(armor instanceof MaxManaModifier modifier) {
                bonus[0] += modifier.getMaxManaBonus(stack, entity);
            }
        });
        return bonus[0];
    }

    // 魔力消耗倍率：累乘所有防具的倍率
    public static double getManaCostMultiplier(LivingEntity entity) {
        double[] multiplier = {1.0D};
        forEachArmor(entity, (armor, stack) -> {
            if(armor instanceof ManaCostModifier modifier) {
                multiplier[0] *= modifier.getManaCostMultiplier(stack, entity);
            }
        });
        return multiplier[0];
    }
}
