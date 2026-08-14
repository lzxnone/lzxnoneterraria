package com.lzxnone.terraria.utils;

import com.lzxnone.terraria.damage.ModDamageTypes;
import com.lzxnone.terraria.entity.beam.SwordBeam;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.summon.BeeSummon;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.accessory.AccessoryUtil;
import com.lzxnone.terraria.item.weapon.*;
import com.lzxnone.terraria.item.weapon.summon.minion.MinionWeapon;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class DamageUtil {
    private static final Map<String, Long> DRAGON_INVULNERABLE_UNTIL = new HashMap<>();

    public enum DamageCategory {
        NORMAL,
        MELEE,
        RANGED,
        SUMMON,
        MAGIC,
        MINION,
        SENTRY,
        REAL
    }

    public static boolean normalAttack(Entity attackEntity, Entity target, ItemStack sourceStack, float damage, float knockbackScale, int invulnerableTime) {
        return entityAttack(attackEntity, target, sourceStack, damage, knockbackScale, invulnerableTime, ModDamageTypes.PLAYER_NORMAL_ATTACK, DamageCategory.NORMAL);
    }

    public static boolean meleeAttack(Entity attackEntity, Entity target, ItemStack sourceStack, float damage, float knockbackScale, int invulnerableTime) {
        return entityAttack(attackEntity, target, sourceStack, damage, knockbackScale, invulnerableTime, ModDamageTypes.PLAYER_NORMAL_ATTACK, DamageCategory.MELEE);
    }

    public static boolean rangedAttack(Entity attackEntity, Entity target, ItemStack sourceStack, float damage, float knockbackScale, int invulnerableTime) {
        return entityAttack(attackEntity, target, sourceStack, damage, knockbackScale, invulnerableTime, ModDamageTypes.PLAYER_NORMAL_ATTACK, DamageCategory.RANGED);
    }

    public static boolean summonAttack(Entity attackEntity, Entity target, ItemStack sourceStack, float damage, float knockbackScale, int invulnerableTime) {
        return entityAttack(attackEntity, target, sourceStack, damage, knockbackScale, invulnerableTime, ModDamageTypes.PLAYER_NORMAL_ATTACK, DamageCategory.SUMMON);
    }

    public static boolean minionAttack(Entity attackEntity, Entity target, ItemStack sourceStack, float damage, float knockbackScale, int invulnerableTime) {
        return entityAttack(attackEntity, target, sourceStack, damage, knockbackScale, invulnerableTime, ModDamageTypes.PLAYER_NORMAL_ATTACK, DamageCategory.MINION);
    }

    public static boolean sentryAttack(Entity attackEntity, Entity target, ItemStack sourceStack, float damage, float knockbackScale, int invulnerableTime) {
        return entityAttack(attackEntity, target, sourceStack, damage, knockbackScale, invulnerableTime, ModDamageTypes.PLAYER_NORMAL_ATTACK, DamageCategory.SENTRY);
    }

    public static boolean magicAttack(Entity attackEntity, Entity target, ItemStack sourceStack, float damage, float knockbackScale, int invulnerableTime) {
        return entityAttack(attackEntity, target, sourceStack, damage, knockbackScale, invulnerableTime, ModDamageTypes.PLAYER_NORMAL_ATTACK, DamageCategory.MAGIC);
    }

    public static boolean realAttack(Entity attackEntity, Entity target, float damage, float knockbackScale, int invulnerableTime) {
        return entityAttack(attackEntity, target, ItemStack.EMPTY, damage, knockbackScale, invulnerableTime, ModDamageTypes.PLAYER_REAL_ATTACK, DamageCategory.REAL);
    }

    private static boolean entityAttack(
        Entity attackEntity,
        Entity target,
        ItemStack sourceStack,
        float damage,
        float knockbackScale,
        int invulnerableTime,
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
        knockbackLevel -= MeleeWeapon.applyMeleeKnockbackBonus(itemSource, player, 0.0F);

        //应用
        finalDamage = Weapon.applyDamageBonus(itemSource, player, finalDamage);
        if(category == DamageCategory.MELEE && !itemSource.isEmpty()) {
            finalDamage = EnchantmentHelper.modifyDamage(serverLevel, itemSource, target, source, finalDamage);
            finalDamage = MeleeWeapon.applyMeleeDamageBonus(itemSource, player, finalDamage);
            knockbackLevel = EnchantmentHelper.modifyKnockback(serverLevel, itemSource, target, source, knockbackLevel);
            knockbackLevel = MeleeWeapon.applyMeleeKnockbackBonus(itemSource, player, knockbackLevel);
        }else if(category == DamageCategory.SUMMON && !itemSource.isEmpty()) {
            finalDamage = SummonWeapon.applySummonDamageBonus(itemSource, player, finalDamage);
        }else if(category == DamageCategory.MAGIC && !itemSource.isEmpty()) {
            finalDamage = MagicWeapon.applyMagicDamageBonus(itemSource, player, finalDamage);
        }else if(category == DamageCategory.RANGED && !itemSource.isEmpty()) {
            finalDamage = RangedWeapon.applyRangedDamageBonus(itemSource, player, finalDamage);
            knockbackLevel += Weapon.getEnchantmentLevel(player, itemSource, Enchantments.PUNCH);
        }else if(category == DamageCategory.MINION && !itemSource.isEmpty()) {
            finalDamage = SummonWeapon.applySummonDamageBonus(itemSource, player, finalDamage);
            knockbackLevel = MinionWeapon.applyMinionKnockback(player, knockbackLevel);
        }else if(category == DamageCategory.SENTRY && !itemSource.isEmpty()) {
            finalDamage = SummonWeapon.applySummonDamageBonus(itemSource, player, finalDamage);
        }
        finalDamage = Math.max(0.0F, finalDamage);

        //近战暴击（月光护身符等饰品的近战暴击率，触发后伤害提升 1.5 倍）
        if(category == DamageCategory.MELEE) {
            double meleeCritChance = AccessoryUtil.getMeleeCritChance(player);
            if(meleeCritChance > 0.0D && player.getRandom().nextDouble() < meleeCritChance) {
                finalDamage *= 1.5F;
            }
        }

        Vec3 beforeHurtMovement = target instanceof LivingEntity livingTarget ? livingTarget.getDeltaMovement() : Vec3.ZERO;
        String dragonInvulnerableKey = getDragonInvulnerableKey(target);
        if(dragonInvulnerableKey != null && invulnerableTime > 0) {
            long gameTime = serverLevel.getGameTime();
            Long invulnerableUntil = DRAGON_INVULNERABLE_UNTIL.get(dragonInvulnerableKey);
            if(invulnerableUntil != null && invulnerableUntil > gameTime) return false;
        }
        boolean hasHurt = target.hurt(source, finalDamage);

        if(hasHurt) {
            applyInvulnerableTime(serverLevel, target, invulnerableTime, dragonInvulnerableKey);
            if(category != DamageCategory.MINION && category != DamageCategory.SENTRY && target instanceof LivingEntity livingTarget) {
                player.setLastHurtMob(livingTarget);
            }
            if(category == DamageCategory.MELEE) {
                //近战着火
                if(AccessoryUtil.canApplyIgnite(player)) target.igniteForTicks(AccessoryUtil.getIgniteTicks(player));
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

                Vec3 knockback = hurtKnockback.add(extraKnockback);
                double scaledY = knockback.y * Math.min(knockbackScale, 1.0F);
                Vec3 scaledKnockback = new Vec3(knockback.x * knockbackScale, scaledY, knockback.z * knockbackScale);
                livingTarget.setDeltaMovement(beforeHurtMovement.add(scaledKnockback));
            }
            DamageSource postSource = attackEntity == player
                ? source
                : attackEntity.level().damageSources().source(damageType, player, player);
            EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, target, postSource, itemSource.isEmpty() ? null : itemSource);
        }

        return hasHurt;
    }

    private static void applyInvulnerableTime(ServerLevel serverLevel, Entity target, int invulnerableTime, String dragonInvulnerableKey) {
        if(invulnerableTime < 0) return;
        target.invulnerableTime = invulnerableTime;
        if(target instanceof EnderDragonPart dragonPart) {
            dragonPart.parentMob.invulnerableTime = invulnerableTime;
        }
        if(dragonInvulnerableKey != null && invulnerableTime > 0) {
            DRAGON_INVULNERABLE_UNTIL.put(dragonInvulnerableKey, serverLevel.getGameTime() + invulnerableTime);
            if(DRAGON_INVULNERABLE_UNTIL.size() > 1024) {
                long gameTime = serverLevel.getGameTime();
                DRAGON_INVULNERABLE_UNTIL.entrySet().removeIf(entry -> entry.getValue() <= gameTime);
            }
        }
    }

    private static String getDragonInvulnerableKey(Entity target) {
        UUID targetUuid = null;
        String partName = "";
        if(target instanceof EnderDragonPart dragonPart) {
            targetUuid = dragonPart.parentMob.getUUID();
            partName = ":" + dragonPart.name;
        }else if(target instanceof EnderDragon dragon) {
            targetUuid = dragon.getUUID();
        }
        return targetUuid == null ? null : targetUuid + partName;
    }

    public static float applyPlayerDamageEffects(Player player, float damage) {
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
