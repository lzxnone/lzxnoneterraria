package com.lzxnone.terraria.utils;

import com.lzxnone.terraria.damage.ModDamageTypes;
import com.lzxnone.terraria.effect.IchorEffect;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.entity.beam.SwordBeam;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.summon.BeeSummon;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.weapon.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class DamageUtil {
    public enum DamageCategory {
        NORMAL,
        MELEE,
        RANGED,
        SUMMON,
        MAGIC,
        REAL
    }

    public static boolean normalAttack(Entity attackEntity, Entity target, ItemStack sourceStack, float damage, float knockbackScale) {
        return entityAttack(attackEntity, target, sourceStack, damage, knockbackScale, ModDamageTypes.PLAYER_NORMAL_ATTACK, DamageCategory.NORMAL);
    }

    public static boolean meleeAttack(Entity attackEntity, Entity target, ItemStack sourceStack, float damage, float knockbackScale) {
        return entityAttack(attackEntity, target, sourceStack, damage, knockbackScale, ModDamageTypes.PLAYER_NORMAL_ATTACK, DamageCategory.MELEE);
    }

    public static boolean rangedAttack(Entity attackEntity, Entity target, ItemStack sourceStack, float damage, float knockbackScale) {
        return entityAttack(attackEntity, target, sourceStack, damage, knockbackScale, ModDamageTypes.PLAYER_NORMAL_ATTACK, DamageCategory.RANGED);
    }

    public static boolean summonAttack(Entity attackEntity, Entity target, ItemStack sourceStack, float damage, float knockbackScale) {
        return entityAttack(attackEntity, target, sourceStack, damage, knockbackScale, ModDamageTypes.PLAYER_NORMAL_ATTACK, DamageCategory.SUMMON);
    }

    public static boolean magicAttack(Entity attackEntity, Entity target, ItemStack sourceStack, float damage, float knockbackScale) {
        return entityAttack(attackEntity, target, sourceStack, damage, knockbackScale, ModDamageTypes.PLAYER_NORMAL_ATTACK, DamageCategory.MAGIC);
    }

    public static boolean realAttack(Entity attackEntity, Entity target, float damage, float knockbackScale) {
        return entityAttack(attackEntity, target, ItemStack.EMPTY, damage, knockbackScale, ModDamageTypes.PLAYER_REAL_ATTACK, DamageCategory.REAL);
    }

    private static boolean entityAttack(
        Entity attackEntity,
        Entity target,
        ItemStack sourceStack,
        float damage,
        float knockbackScale,
        ResourceKey<DamageType> damageType,
        DamageCategory category
    ) {
        Player player = getAttackOwner(attackEntity);
        if(player == null || !(attackEntity.level() instanceof ServerLevel serverLevel)) return false;

        DamageSource source = attackEntity.level().damageSources().source(damageType, attackEntity, player);
        ItemStack itemSource = sourceStack == null ? ItemStack.EMPTY : sourceStack;

        //应用药水
        float finalDamage = category == DamageCategory.REAL ? Math.max(0.0F, damage) : applyPlayerDamageEffects(player, damage);
        float knockbackLevel = (float) player.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        //
        //应用
        finalDamage = Weapon.applyDamageBonus(itemSource, player, finalDamage);
        if(category == DamageCategory.MELEE && !itemSource.isEmpty()) {
            finalDamage = EnchantmentHelper.modifyDamage(serverLevel, itemSource, target, source, finalDamage);
            finalDamage = MeleeWeapon.applyMeleeDamageBonus(itemSource, player, finalDamage);
            knockbackLevel = EnchantmentHelper.modifyKnockback(serverLevel, itemSource, target, source, knockbackLevel);
        }else if(category == DamageCategory.SUMMON && !itemSource.isEmpty()) {
            finalDamage = SummonWeapon.applySummonDamageBonus(itemSource, player, finalDamage);
        }else if(category == DamageCategory.MAGIC && !itemSource.isEmpty()) {
            finalDamage = MagicWeapon.applyMagicDamageBonus(itemSource, player, finalDamage);
        }else if(category == DamageCategory.RANGED && !itemSource.isEmpty()) {
            finalDamage = RangedWeapon.applyRangedDamageBonus(itemSource, player, finalDamage);
        }
        if(category != DamageCategory.REAL) {
            finalDamage = applyTargetDamageEffects(target, finalDamage);
        }
        finalDamage = Math.max(0.0F, finalDamage);

        Vec3 beforeHurtMovement = target instanceof LivingEntity livingTarget ? livingTarget.getDeltaMovement() : Vec3.ZERO;
        boolean hasHurt = target.hurt(source, finalDamage);

        if(hasHurt) {
            if(category != DamageCategory.SUMMON && target instanceof LivingEntity livingTarget) {
                player.setLastHurtMob(livingTarget);
            }
            if(target instanceof LivingEntity livingTarget) {
                Vec3 hurtKnockback = livingTarget.getDeltaMovement().subtract(beforeHurtMovement);
                Vec3 extraKnockback = Vec3.ZERO;

                if(knockbackLevel > 0.0F) {
                    livingTarget.setDeltaMovement(beforeHurtMovement);
                    Vec3 knockbackDirection = getAttackKnockbackDirection(attackEntity, target);
                    livingTarget.knockback(knockbackLevel * 0.5F, -knockbackDirection.x, -knockbackDirection.z);
                    extraKnockback = livingTarget.getDeltaMovement().subtract(beforeHurtMovement);
                }

                livingTarget.setDeltaMovement(beforeHurtMovement.add(hurtKnockback.add(extraKnockback).scale(knockbackScale)));
            }
            DamageSource postSource = attackEntity == player
                ? source
                : attackEntity.level().damageSources().source(damageType, player, player);
            EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, target, postSource, itemSource.isEmpty() ? null : itemSource);
        }

        return hasHurt;
    }

    private static float applyPlayerDamageEffects(Player player, float damage) {
        float finalDamage = damage;
        //力量
        if(player.hasEffect(MobEffects.DAMAGE_BOOST) && player.getEffect(MobEffects.DAMAGE_BOOST) != null) {
            int amplifier = Objects.requireNonNull(player.getEffect(MobEffects.DAMAGE_BOOST)).getAmplifier();
            finalDamage += (amplifier + 1) * 3.0F;
        }
        //虚弱
        if(player.hasEffect(MobEffects.WEAKNESS) && player.getEffect(MobEffects.WEAKNESS) != null) {
            int amplifier = Objects.requireNonNull(player.getEffect(MobEffects.WEAKNESS)).getAmplifier();
            finalDamage -= (amplifier + 1) * 4.0F;
        }
        return Math.max(0.0F, finalDamage);
    }

    private static float applyTargetDamageEffects(Entity target, float damage) {
        if(!(target instanceof LivingEntity livingTarget)) return damage;

        float finalDamage = damage;
        if(livingTarget.hasEffect(ModEffects.ICHOR) && livingTarget.getEffect(ModEffects.ICHOR) != null) {
            int amplifier = Objects.requireNonNull(livingTarget.getEffect(ModEffects.ICHOR)).getAmplifier();
            finalDamage *= 1.0f + IchorEffect.getDamageBonusPerLevel() * (amplifier + 1);
        }
        return Math.max(0.0F, finalDamage);
    }

    private static Player getAttackOwner(Entity attackEntity) {
        if(attackEntity instanceof Player player) return player;
        if(attackEntity instanceof StaticProjectile projectile) return getAttackOwner(projectile.getOwner());
        if(attackEntity instanceof StaticSummon summon) return getAttackOwner(summon.getOwner());
        if(attackEntity instanceof SwordBeam beam) return getAttackOwner(beam.getOwner());
        if(attackEntity instanceof BeeSummon beeSummon) return getAttackOwner(beeSummon.owner);
        return null;
    }

    private static Vec3 getAttackKnockbackDirection(Entity attackEntity, Entity target) {
        Vec3 direction = attackEntity.getDeltaMovement();
        if(direction.lengthSqr() < 1.0E-7D) {
            direction = getStoredAttackDirection(attackEntity);
        }
        if(direction.lengthSqr() < 1.0E-7D) {
            direction = target.position().subtract(attackEntity.position());
        }
        if(direction.lengthSqr() < 1.0E-7D) {
            Player owner = getAttackOwner(attackEntity);
            direction = target.position().subtract(owner != null ? owner.position() : attackEntity.position());
        }
        if(direction.lengthSqr() < 1.0E-7D) {
            return new Vec3(0.0D, 0.0D, 1.0D);
        }
        return direction.normalize();
    }

    private static Vec3 getStoredAttackDirection(Entity attackEntity) {
        if(attackEntity instanceof StaticProjectile projectile) {
            return MathUtil.toVec3(projectile.getEntityData().get(StaticProjectile.DIRECTION));
        }
        if(attackEntity instanceof SwordBeam beam && beam.currentPosition != null && beam.prevPosition != null) {
            return beam.currentPosition.subtract(beam.prevPosition);
        }
        return Vec3.ZERO;
    }
}
