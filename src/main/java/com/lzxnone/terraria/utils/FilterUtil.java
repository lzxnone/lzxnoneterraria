package com.lzxnone.terraria.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class FilterUtil {
    public static final Predicate<Entity> BASE_FILTER = entity ->
        entity != null
        && entity.isAlive()
        && entity.isPickable()
        && !entity.isInvulnerable()
        && !entity.isSpectator();


    public static Predicate<LivingEntity> createLivingTargetFilter(@Nullable Entity owner) {
        return target ->
            BASE_FILTER.test(target)
            && (owner == null || (target != owner && !target.isAlliedTo(owner)));
    }

    public static Predicate<LivingEntity> createLivingTargetFilter(Entity attacker,  @Nullable Entity owner) {
        return target ->
            createLivingTargetFilter(owner).test(target)
            && target != attacker;
    }

    public static Predicate<Entity> createTargetFilter(@Nullable Entity owner) {
        return target ->
            BASE_FILTER.test(target)
            && (owner == null || (target != owner && !target.isAlliedTo(owner)));
    }

    public static Predicate<Entity> createTargetFilter(Entity attacker,  @Nullable Entity owner) {
        return target ->
            createTargetFilter(owner).test(target)
            && target != attacker;
    }

    public static Predicate<Monster> createMonsterFilter(@Nullable Entity owner) {
        return monster ->
            BASE_FILTER.test(monster)
            && (owner == null || (monster != owner && !monster.isAlliedTo(owner)));
    }
}
