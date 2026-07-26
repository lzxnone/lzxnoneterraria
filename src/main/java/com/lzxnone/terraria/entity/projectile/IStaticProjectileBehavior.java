package com.lzxnone.terraria.entity.projectile;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public interface IStaticProjectileBehavior {
    default void onMoving(StaticProjectile projectile) {}
    default void onHitEntity(StaticProjectile projectile, EntityHitResult result) {}
    default void onHitBlock(StaticProjectile projectile, BlockHitResult result) {}
    default void onDied(StaticProjectile projectile) {
        if(!projectile.level().isClientSide()) {
            projectile.discard();
        }
    }
}
