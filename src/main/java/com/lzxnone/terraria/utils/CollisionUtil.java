package com.lzxnone.terraria.utils;

import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollisionUtil {
    public static EntityHitResult checkEntityHit(StaticProjectile projectile, double inflate) {
         EntityHitResult result = ProjectileUtil.getEntityHitResult(
            projectile.level(),
            projectile,
            projectile.position(),
            projectile.position().add(projectile.getDeltaMovement()),
            projectile.getBoundingBox()
                .expandTowards(projectile.getDeltaMovement())
                .inflate(inflate),
            FilterUtil.createTargetFilter(projectile, projectile.getOwner())
        );
        return result;
    }

    public static EntityHitResult checkEntityHit(StaticSummon summon, Vec3 nextPos, double inflate) {
         EntityHitResult result = ProjectileUtil.getEntityHitResult(
            summon.level(),
            summon,
            summon.position(),
            nextPos,
            summon.getBoundingBox()
                .expandTowards(nextPos)
                .inflate(inflate),
            FilterUtil.createTargetFilter(summon, summon.getOwner())
        );
        return result;
    }

    public static BlockHitResult checkBlockHit(Entity entity, Vec3 nextPos) {
        return entity.level().clip(new ClipContext(
            entity.position(), nextPos,
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            entity
        ));
    }

    public static List<Entity> searchEntities(Level level, AABB searchBox, @Nullable Entity attacker, @Nullable Entity owner) {
        return level.getEntitiesOfClass(
            Entity.class,
            searchBox,
            FilterUtil.createTargetFilter(attacker, owner)
        );
    }

    public static List<Entity> searchEnemies(Level level, AABB searchBox, @Nullable Entity attacker, @Nullable Entity owner) {
        List<Entity> enemies = new ArrayList<>();
        Set<Integer> addedIds = new HashSet<>();

        for(Entity entity : level.getEntitiesOfClass(Entity.class, searchBox, entity -> isEnemySearchTarget(entity, attacker, owner))) {
            if(entity instanceof EnderDragon dragon) {
                addEnderDragonParts(enemies, addedIds, dragon, searchBox, attacker, owner);
            }else if(addedIds.add(entity.getId())) {
                enemies.add(entity);
            }
        }

        for(EnderDragon dragon : level.getEntitiesOfClass(
            EnderDragon.class,
            searchBox.inflate(16.0D),
            dragon -> isEnemySearchTarget(dragon, attacker, owner)
        )) {
            addEnderDragonParts(enemies, addedIds, dragon, searchBox, attacker, owner);
        }

        return enemies;
    }

    public static boolean isEnemySearchTarget(Entity entity, @Nullable Entity attacker, @Nullable Entity owner) {
        if(entity instanceof EnderDragonPart dragonPart) {
            return isSearchableTarget(dragonPart, attacker, owner, true)
                && isSearchableTarget(dragonPart.parentMob, attacker, owner, false);
        }
        if(entity instanceof EnderDragon dragon) {
            return isSearchableTarget(dragon, attacker, owner, false);
        }
        return entity instanceof LivingEntity livingTarget
            && FilterUtil.createEnemyFilter(attacker, owner).test(livingTarget);
    }

    private static boolean isSearchableTarget(Entity entity, @Nullable Entity attacker, @Nullable Entity owner, boolean requirePickable) {
        return entity != null
            && entity.isAlive()
            && (!requirePickable || entity.isPickable())
            && !entity.isInvulnerable()
            && !entity.isSpectator()
            && entity != attacker
            && !(entity instanceof PartEntity<?> part && part.getParent() == attacker)
            && (owner == null || (entity != owner && !entity.isAlliedTo(owner)));
    }

    private static void addEnderDragonParts(List<Entity> enemies, Set<Integer> addedIds, EnderDragon dragon,
                                            AABB searchBox, @Nullable Entity attacker, @Nullable Entity owner) {
        for(EnderDragonPart part : dragon.getSubEntities()) {
            if(part.getBoundingBox().intersects(searchBox)
                && isEnemySearchTarget(part, attacker, owner)
                && addedIds.add(part.getId())) {
                enemies.add(part);
            }
        }
    }
}
