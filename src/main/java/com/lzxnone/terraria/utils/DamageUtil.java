package com.lzxnone.terraria.utils;

import com.lzxnone.terraria.damage.ModDamageTypes;
import com.lzxnone.terraria.entity.beam.SwordBeam;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.summon.BeeSummon;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

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

    public static boolean normalAttack(Entity attackEntity, Entity target, float damage, float knockbackScale) {
        return entityAttack(attackEntity, target, damage, knockbackScale, ModDamageTypes.PLAYER_NORMAL_ATTACK, false);
    }

    public static boolean realAttack(Entity attackEntity, Entity target, float damage) {
        return realAttack(attackEntity, target, damage, 0.0F);
    }

    public static boolean realAttack(Entity attackEntity, Entity target, float damage, float knockbackScale) {
        return entityAttack(attackEntity, target, damage, knockbackScale, ModDamageTypes.PLAYER_REAL_ATTACK, true);
    }

    private static boolean entityAttack(
        Entity attackEntity,
        Entity target,
        float damage,
        float knockbackScale,
        ResourceKey<DamageType> damageType,
        boolean realDamage
    ) {
        Player player = getAttackOwner(attackEntity);
        if(player == null || !(attackEntity.level() instanceof ServerLevel serverLevel)) return false;

        DamageSource source = attackEntity.level().damageSources().source(damageType, attackEntity, player);
        float finalDamage = realDamage ? Math.max(0.0F, damage) : applyPlayerDamageEffects(player, damage);
        float knockbackLevel = (float) player.getAttributeValue(Attributes.ATTACK_KNOCKBACK);

        if(!realDamage) {
            finalDamage = EnchantmentHelper.modifyDamage(serverLevel, player.getWeaponItem(), target, source, finalDamage);
            knockbackLevel = EnchantmentHelper.modifyKnockback(serverLevel, player.getWeaponItem(), target, source, knockbackLevel);
        }
        finalDamage = Math.max(0.0F, finalDamage);

        boolean hasHurt = target.hurt(source, finalDamage);

        if(hasHurt) {
            float finalKnockback = knockbackLevel * knockbackScale;
            if(finalKnockback > 0.0F && target instanceof LivingEntity livingTarget) {
                Vec3 knockbackDirection = getAttackKnockbackDirection(attackEntity, target);
                livingTarget.knockback(finalKnockback * 0.5F, knockbackDirection.x, knockbackDirection.z);
            }
            if(!realDamage) {
                EnchantmentHelper.doPostAttackEffects(serverLevel, target, source);
            }
        }

        return hasHurt;
    }

    private static float applyPlayerDamageEffects(Player player, float damage) {
        float finalDamage = damage;
        if(player.hasEffect(MobEffects.DAMAGE_BOOST) && player.getEffect(MobEffects.DAMAGE_BOOST) != null) {
            int amplifier = Objects.requireNonNull(player.getEffect(MobEffects.DAMAGE_BOOST)).getAmplifier();
            finalDamage += (amplifier + 1) * 3.0F;
        }
        if(player.hasEffect(MobEffects.WEAKNESS) && player.getEffect(MobEffects.WEAKNESS) != null) {
            int amplifier = Objects.requireNonNull(player.getEffect(MobEffects.WEAKNESS)).getAmplifier();
            finalDamage -= (amplifier + 1) * 4.0F;
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
