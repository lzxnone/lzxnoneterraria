package com.lzxnone.terraria.utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Objects;

public class DamageUtil {
    public static boolean attack(Player player, Entity target, float damage) {
        float finalDamage = damage;
        if(player.hasEffect(MobEffects.DAMAGE_BOOST) && player.getEffect(MobEffects.DAMAGE_BOOST) != null) {
            int amplifier = Objects.requireNonNull(player.getEffect(MobEffects.DAMAGE_BOOST)).getAmplifier();
            finalDamage += (amplifier + 1) * 3.0F;
        }
        if(player.hasEffect(MobEffects.WEAKNESS) && player.getEffect(MobEffects.WEAKNESS) != null) {
            int amplifier = Objects.requireNonNull(player.getEffect(MobEffects.WEAKNESS)).getAmplifier();
            finalDamage -= (amplifier + 1) * 4.0F;
        }
        finalDamage = Math.max(0.0F, finalDamage);

        DamageSource source = player.level().damageSources().playerAttack(player);
        if(player.level() instanceof ServerLevel serverLevel) {
            finalDamage = EnchantmentHelper.modifyDamage(serverLevel, player.getWeaponItem(), target, source, finalDamage);
        }
        finalDamage = Math.max(0.0F, finalDamage);

        boolean hasHurt = target.hurt(source, finalDamage);

        if(hasHurt && player.level() instanceof ServerLevel serverLevel) {
            EnchantmentHelper.doPostAttackEffects(serverLevel, target, source);
        }

        return hasHurt;
    }
}
