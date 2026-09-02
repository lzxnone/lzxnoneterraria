package com.lzxnone.terraria.utils;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.damage.ModDamageTypes;
import com.lzxnone.terraria.entity.beam.SwordBeam;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.summon.BeeSummon;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.accessory.AccessoryUtil;
import com.lzxnone.terraria.item.weapon.*;
import com.lzxnone.terraria.item.weapon.summon.minion.MinionWeapon;
import com.lzxnone.terraria.item.weapon.summon.whip.Whip;
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
    private static final int SHORT_INVULNERABLE_TIME = 2;
    private static final int MAX_SOURCE_COOLDOWNS = 64;

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

        //仆从/哨兵 标记伤害（来自鞭子标记的点伤加成）
        if(category == DamageCategory.MINION || category == DamageCategory.SENTRY) {
            finalDamage = Whip.applyMarkDamageBonus(target, finalDamage);
        }

        finalDamage = Math.max(0.0F, finalDamage);

        //近战暴击（近战专属暴击 + 全职业通用饰品暴击）
        if(category == DamageCategory.MELEE) {
            double critChance = AccessoryUtil.getMeleeCritChance(player) + AccessoryUtil.getCritChance(player);
            if(critChance > 0.0D && player.getRandom().nextDouble() < critChance) {
                finalDamage *= 1.5F;
            }
        }else if(category == DamageCategory.RANGED || category == DamageCategory.MAGIC || category == DamageCategory.SUMMON || category == DamageCategory.NORMAL) {
            //远程、魔法、召唤鞭打、普通攻击：享受通用饰品暴击
            double critChance = AccessoryUtil.getCritChance(player);
            if(critChance > 0.0D && player.getRandom().nextDouble() < critChance) {
                finalDamage *= 1.5F;
            }
        }else if(category == DamageCategory.MINION || category == DamageCategory.SENTRY) {
            //仆从/哨兵 标记暴击（严格仅来自鞭子标记的暴击率加成，不享受玩家自身饰品暴击）
            int tagCrit = Whip.applyMarkCritBonus(target, 0);
            if(tagCrit > 0 && player.getRandom().nextInt(100) < tagCrit) {
                finalDamage *= 1.5F;
            }
        }

        //伤害浮动：最终伤害乘以 0.8~1.2 随机系数
        finalDamage *= 0.8F + (float) Math.random() * 0.4F;

        Vec3 beforeHurtMovement = target instanceof LivingEntity livingTarget ? livingTarget.getDeltaMovement() : Vec3.ZERO;
        String dragonInvulnerableKey = getDragonInvulnerableKey(target);
        //按来源隔离的冷却：同一来源（UUID）在 invulnerableTime 内不重复命中同一目标
        if(invulnerableTime > 0 && target instanceof LivingEntity livingTarget) {
            long gameTime = serverLevel.getGameTime();
            Map<UUID, Long> sourceCooldowns = livingTarget.getData(ModAttachments.SOURCE_HIT_COOLDOWNS);
            if(sourceCooldowns.size() > MAX_SOURCE_COOLDOWNS) {
                sourceCooldowns.entrySet().removeIf(entry -> entry.getValue() <= gameTime);
            }
            Long nextAllowed = sourceCooldowns.get(attackEntity.getUUID());
            if(nextAllowed != null && gameTime < nextAllowed) return false;
        }
        if(dragonInvulnerableKey != null && invulnerableTime > 0) {
            long gameTime = serverLevel.getGameTime();
            Long invulnerableUntil = DRAGON_INVULNERABLE_UNTIL.get(dragonInvulnerableKey);
            if(invulnerableUntil != null && invulnerableUntil > gameTime) return false;
        }
        boolean hasHurt = target.hurt(source, finalDamage);

        if(hasHurt) {
            if(invulnerableTime > 0 && target instanceof LivingEntity livingTarget) {
                livingTarget.getData(ModAttachments.SOURCE_HIT_COOLDOWNS)
                    .put(attackEntity.getUUID(), serverLevel.getGameTime() + invulnerableTime);
            }
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
        //实体全局无敌帧固定缩短；同来源防刷已由 SOURCE_HIT_COOLDOWNS 承担
        target.invulnerableTime = SHORT_INVULNERABLE_TIME;
        if(target instanceof EnderDragonPart dragonPart) {
            dragonPart.parentMob.invulnerableTime = SHORT_INVULNERABLE_TIME;
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
