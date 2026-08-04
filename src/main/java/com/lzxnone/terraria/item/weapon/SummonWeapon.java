package com.lzxnone.terraria.item.weapon;

import com.lzxnone.terraria.enchantment.ModEnchantmentConfigs;
import com.lzxnone.terraria.enchantment.ModEnchantments;
import com.lzxnone.terraria.item.accessory.AccessoryUtil;
import com.lzxnone.terraria.item.effect.SummonDamageModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;

public class SummonWeapon extends Weapon {
    public SummonWeapon(Tier tier, Properties properties) {
        super(tier, properties);
    }

    public static float applySummonDamageBonus(ItemStack stack, LivingEntity entity, float damage) {
        double finalDamage = damage;
        //召唤增幅
        int amplificationLevel = getEnchantmentLevel(entity, stack, ModEnchantments.SUMMON_AMPLIFICATION);
        finalDamage *= Math.pow(ModEnchantmentConfigs.getSummonAmplificationDamageMultiplier(), amplificationLevel);
        //饰品
        double[] multiplier = {1.0D};
        AccessoryUtil.forEachAccessory(entity, (accessory, accessoryStack) -> {
            if(accessory instanceof SummonDamageModifier modifier) {
                multiplier[0] *= modifier.getSummonDamageMultiplier(accessoryStack, entity);
            }
        });
        finalDamage *= multiplier[0];
        return (float)Math.max(0.0D, finalDamage);
    }
}
