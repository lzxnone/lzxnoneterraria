package com.lzxnone.terraria.utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
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

        float knockbackLevel = (float) player.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        boolean isSprinting = player.isSprinting();
        if(isSprinting) {
            knockbackLevel += 1.0F;
        }

        if(player.level() instanceof ServerLevel serverLevel) {
            finalDamage = EnchantmentHelper.modifyDamage(serverLevel, player.getWeaponItem(), target, source, finalDamage);
            knockbackLevel = EnchantmentHelper.modifyKnockback(serverLevel, player.getWeaponItem(), target, source, knockbackLevel);
        }
        finalDamage = Math.max(0.0F, finalDamage);

        boolean hasHurt = target.hurt(source, finalDamage);

        if(hasHurt) {
            if(knockbackLevel > 0.0F && target instanceof LivingEntity livingTarget) {
                livingTarget.knockback(
                    knockbackLevel * 0.5F,
                    Mth.sin(player.getYRot() * ((float) Math.PI / 180F)),
                    -Mth.cos(player.getYRot() * ((float) Math.PI / 180F))
                );

                if(isSprinting) {
                    player.setDeltaMovement(player.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                    player.setSprinting(false);
                }
            }
            if(player.level() instanceof ServerLevel serverLevel) {
                EnchantmentHelper.doPostAttackEffects(serverLevel, target, source);
            }
        }

        return hasHurt;
    }
}
