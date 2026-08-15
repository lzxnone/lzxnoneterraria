package com.lzxnone.terraria.item.effect;

import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.IStaticProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.accessory.AccessoryUtil;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.utils.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public interface FallenStarSummoner {
    boolean summonThreeFallenStar(ItemStack stack, LivingEntity entity);
    double getFallenStarDamage(ItemStack stack, LivingEntity entity);

    //星星落地时是否变为回复魔力的拾取物（魔力斗篷等）
    default boolean summonManaStarOnLanding(ItemStack stack, LivingEntity entity) {
        return false;
    }

    IStaticProjectileBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileBehavior() {
        @Override
        public void onMoving(StaticProjectile projectile) {
            ParticleUtil.addParticle(
                projectile.level(), ModParticles.STAR_PARTICLE.get(),
                projectile.position(), 1.0,
                new Vec3(0, 0, 0), 0.1
            );
        }
        @Override
        public void onHitEntity(StaticProjectile projectile, EntityHitResult result) {
            if(!projectile.level().isClientSide()) {
                Entity target = result.getEntity();
                Entity owner = projectile.getOwner();
                if(owner == null) return;
                if(!FilterUtil.createTargetFilter(owner).test(target) || !(owner instanceof Player player)) return;
                ItemStack stack = projectile.getEntityData().get(StaticProjectile.STACK_SOURCE);
                double[] fallenStarDamage = {0.0D};
                AccessoryUtil.forEachAccessory(player, (accessory, accessoryStack) -> {
                    if(accessory instanceof FallenStarSummoner summoner) {
                        fallenStarDamage[0] = Math.max(summoner.getFallenStarDamage(accessoryStack, player), fallenStarDamage[0]);
                    }
                });
                if(DamageUtil.meleeAttack(projectile, target, stack, (float) fallenStarDamage[0], 1.0f, 10)) {
                }
            }
        }
        @Override
        public void onHitBlock(StaticProjectile projectile, BlockHitResult result) {
            CompoundTag customData = projectile.getEntityData().get(StaticProjectile.CUSTOM_DATA);
            if(!customData.contains("targetLifetime")) return;

            int targetLifetime = customData.getInt("targetLifetime");
            int age = projectile.getEntityData().get(StaticProjectile.AGE);

            if(!projectile.level().isClientSide()) {
                if(age < targetLifetime) {
                    EntityHitResult entityHit = CollisionUtil.checkEntityHit(projectile, 3.0);
                    if(entityHit != null) {
                        this.onHitEntity(projectile, entityHit);
                    }
                }else {
                    if(!projectile.level().getBlockState(result.getBlockPos()).getCollisionShape(projectile.level(), result.getBlockPos()).isEmpty()) {
                        onDied(projectile);
                    }
                }
            }
        }
        @Override
        public void onDied(StaticProjectile projectile) {
            if(!projectile.level().isClientSide()) {
                CompoundTag customData = projectile.getEntityData().get(StaticProjectile.CUSTOM_DATA);
                if(customData.contains("summonManaStar")) {
                    StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), projectile.level());
                    summon.setPos(projectile.position());
                    summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.MANA_STAR);
                    summon.getEntityData().set(StaticSummon.RENDER_MODE, "item");
                    summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.MANA_STAR.get()));
                    summon.getEntityData().set(StaticSummon.LIFETIME, 100);
                    summon.getEntityData().set(StaticSummon.GLOW, true);
                    CompoundTag summonCustomData = new CompoundTag();
                    summonCustomData.putInt("mana", 50);
                    summon.getEntityData().set(StaticSummon.CUSTOM_DATA, summonCustomData);
                    projectile.level().addFreshEntity(summon);
                }
                ParticleUtil.addParticles(
                    (ServerLevel) projectile.level(), ModParticles.STAR_PARTICLE.get(),
                    projectile.position(), new Vec3(0.5, 0.5, 0.5),
                    0.2, 25
                );
                SoundUtil.playServerSound(projectile.level(), ModSounds.STAR_COLLIDE.get(), projectile.position());
                projectile.discard();
            }
        }
    };
}
