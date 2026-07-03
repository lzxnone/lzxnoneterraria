package com.lzxnone.terraria.utils;

import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

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

    public static EntityHitResult checkEntityHit(StaticProjectile projectile) {
        return checkEntityHit(projectile, 1.0);
    }

    public static BlockHitResult checkBlockHit(StaticProjectile projectile) {
        BlockHitResult result = projectile.level().clip(new ClipContext(
            projectile.position(), projectile.position().add(projectile.getDeltaMovement()),
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            projectile
        ));
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

    public static EntityHitResult checkEntityHit(StaticSummon summon, Vec3 nextPos) {
        return checkEntityHit(summon, nextPos, 1.0);
    }

    public static BlockHitResult checkBlockHit(StaticSummon summon, Vec3 nextPos) {
        BlockHitResult result = summon.level().clip(new ClipContext(
            summon.position(), nextPos,
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            summon
        ));
        return result;
    }
}
