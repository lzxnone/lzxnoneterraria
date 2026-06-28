package com.lzxnone.terraria.entity.projectile;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public interface IProjectileBehavior {
    default void onMoving(TextureProjectile projectile) {}
    default void onHitEntity(TextureProjectile projectile, EntityHitResult result) {}
    default void onHitBlock(TextureProjectile projectile, BlockHitResult result) {}
    default void onDied(TextureProjectile projectile) {
        if(!projectile.level().isClientSide()) {
            projectile.discard();
        }
    }
}
