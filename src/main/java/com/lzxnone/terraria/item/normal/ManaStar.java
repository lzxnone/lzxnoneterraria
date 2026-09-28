package com.lzxnone.terraria.item.normal;

import com.lzxnone.terraria.attachment.PlayerMana;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.accessory.AccessoryUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ManaStar extends Item {
    public static final int MANA_SURGE_DURATION_TICKS = 40;

    public ManaStar() {
        super(new Item.Properties());
    }

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        private static final double GRAVITY = 0.04D;
        private static final double AIR_FRICTION = 0.98D;
        private static final double GROUND_FRICTION = 0.6D;
        private static final double MAX_FALL_SPEED = -1.5D;
        private static final double PICKUP_RADIUS = 0.5D;
        private static final double MAX_PICKUP_RADIUS = 16.0D;
        private static final double ATTRACT_SPEED = 0.25D;

        @Override
        public void tick(StaticSummon summon) {
            Vec3 movement = summon.getDeltaMovement();
            if(summon.onGround()) {
                movement = new Vec3(movement.x * GROUND_FRICTION, 0.0D, movement.z * GROUND_FRICTION);
            }else {
                movement = movement.add(0.0D, -GRAVITY, 0.0D);
                movement = movement.multiply(AIR_FRICTION, AIR_FRICTION, AIR_FRICTION);
                if(movement.y < MAX_FALL_SPEED) movement = new Vec3(movement.x, MAX_FALL_SPEED, movement.z);
            }
            summon.setDeltaMovement(movement);

            if(summon.level().isClientSide()) return;

            if(tryPickup(summon)) return;

            int lifetime = summon.getEntityData().get(StaticSummon.LIFETIME);
            int age = summon.getEntityData().get(StaticSummon.AGE);
            if(lifetime != StaticSummon.INFINITE_LIFETIME && age > lifetime) {
                summon.discard();
                return;
            }
            summon.getEntityData().set(StaticSummon.AGE, age + 1);
        }

        private boolean tryPickup(StaticSummon summon) {
            List<ServerPlayer> players = summon.level().getEntitiesOfClass(
                ServerPlayer.class,
                summon.getBoundingBox().inflate(MAX_PICKUP_RADIUS),
                player -> !player.isSpectator()
            );
            if(players.isEmpty()) return false;

            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);

            // 1. 标准包围盒碰撞检测捡起
            for (ServerPlayer player : players) {
                if (player.getBoundingBox().intersects(summon.getBoundingBox())) {
                    int amount = customData.contains("mana") ? customData.getInt("mana") : 0;
                    PlayerMana.recoverMana(player, amount);
                    player.addEffect(new MobEffectInstance(ModEffects.MANA_SURGE, MANA_SURGE_DURATION_TICKS, 0));
                    onDied(summon);
                    return true;
                }
            }

            // 2. 吸引逻辑（保持原有基础吸引范围不变，寻找最近的可吸引玩家）
            ServerPlayer closestPlayer = null;
            double closestDistSq = Double.MAX_VALUE;
            for (ServerPlayer player : players) {
                double distSq = summon.distanceToSqr(player);
                if (distSq < closestDistSq) {
                    closestDistSq = distSq;
                    closestPlayer = player;
                }
            }

            if (closestPlayer != null) {
                double attractRange = Math.max(AccessoryUtil.getStarPickupRange(closestPlayer), customData.contains("min_attract_range") ? customData.getDouble("min_attract_range") : 4.0D);
                Vec3 dir = closestPlayer.getBoundingBox().getCenter().subtract(summon.position());
                double dist = dir.length();
                if (dist <= attractRange) {
                    summon.setDeltaMovement(dir.scale(ATTRACT_SPEED));
                }
            }

            return false;
        }
    };
}
