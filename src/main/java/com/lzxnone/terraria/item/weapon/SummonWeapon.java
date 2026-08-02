package com.lzxnone.terraria.item.weapon;

import com.lzxnone.terraria.enchantment.ModEnchantmentConfigs;
import com.lzxnone.terraria.enchantment.ModEnchantments;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;

public class SummonWeapon extends Weapon {
    public SummonWeapon(Tier tier, Properties properties) {
        super(tier, properties);
    }

    public static float applySummonDamageBonus(ItemStack stack, LivingEntity entity, float damage) {
        double finalDamage = damage;
        int amplificationLevel = getEnchantmentLevel(entity, stack, ModEnchantments.SUMMON_AMPLIFICATION);

        finalDamage *= Math.pow(ModEnchantmentConfigs.getSummonAmplificationDamageMultiplier(), amplificationLevel);
        return (float)Math.max(0.0D, finalDamage);
    }
}
