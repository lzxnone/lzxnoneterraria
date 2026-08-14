package com.lzxnone.terraria.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.entity.PartEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class SearchUtil {
    private SearchUtil() {}

    public static List<Entity> searchEntities(Entity entity, @Nullable Entity owner, AABB searchBox) {
        return entity.level().getEntitiesOfClass(
            Entity.class,
            searchBox,
            FilterUtil.createTargetFilter(entity, owner)
        );
    }

    public static List<Entity> searchEnemies(Entity entity, @Nullable Entity owner, AABB searchBox) {
        List<Entity> enemies = new ArrayList<>();
        Set<Integer> addedIds = new HashSet<>();
        Predicate<Entity> enemyFilter = target -> {
            if(target instanceof EnderDragonPart dragonPart) {
                EnderDragon dragon = dragonPart.parentMob;
                return dragonPart.isAlive()
                    && dragonPart.isPickable()
                    && !dragonPart.isInvulnerable()
                    && !dragonPart.isSpectator()
                    && dragonPart != entity
                    && !(dragonPart instanceof PartEntity<?> part && part.getParent() == entity)
                    && (owner == null || (dragonPart != owner && !dragonPart.isAlliedTo(owner)))
                    && dragon != null
                    && dragon.isAlive()
                    && !dragon.isInvulnerable()
                    && !dragon.isSpectator()
                    && dragon != entity
                    && (owner == null || (dragon != owner && !dragon.isAlliedTo(owner)));
            }
            if(target instanceof EnderDragon dragon) {
                return dragon.isAlive()
                    && !dragon.isInvulnerable()
                    && !dragon.isSpectator()
                    && dragon != entity
                    && (owner == null || (dragon != owner && !dragon.isAlliedTo(owner)));
            }
            return target instanceof LivingEntity livingTarget
                && FilterUtil.createEnemyFilter(entity, owner).test(livingTarget);
        };

        for(Entity target : entity.level().getEntitiesOfClass(Entity.class, searchBox, enemyFilter)) {
            if(target instanceof EnderDragon dragon) {
                for(EnderDragonPart part : dragon.getSubEntities()) {
                    if(part.getBoundingBox().intersects(searchBox)
                        && enemyFilter.test(part)
                        && addedIds.add(part.getId())) {
                        enemies.add(part);
                    }
                }
            }else if(addedIds.add(target.getId())) {
                enemies.add(target);
            }
        }

        for(EnderDragon dragon : entity.level().getEntitiesOfClass(EnderDragon.class, searchBox.inflate(16.0D), enemyFilter)) {
            for(EnderDragonPart part : dragon.getSubEntities()) {
                if(part.getBoundingBox().intersects(searchBox)
                    && enemyFilter.test(part)
                    && addedIds.add(part.getId())) {
                    enemies.add(part);
                }
            }
        }

        return enemies;
    }

    public static List<Entity> searchNearestEntities(Entity entity, @Nullable Entity owner, AABB searchBox, int count) {
        List<Entity> targets = searchEntities(entity, owner, searchBox);
        if(count <= 0 || targets.isEmpty()) return List.of();
        targets.sort(Comparator.comparingDouble(target -> target.distanceToSqr(searchBox.getCenter())));
        if(targets.size() <= count) return targets;
        return new ArrayList<>(targets.subList(0, count));
    }

    public static List<Entity> searchNearestEnemies(Entity entity, @Nullable Entity owner, AABB searchBox, int count) {
        List<Entity> targets = searchEnemies(entity, owner, searchBox);
        if(count <= 0 || targets.isEmpty()) return List.of();
        targets.sort(Comparator.comparingDouble(target -> target.distanceToSqr(searchBox.getCenter())));
        if(targets.size() <= count) return targets;
        return new ArrayList<>(targets.subList(0, count));
    }
}
