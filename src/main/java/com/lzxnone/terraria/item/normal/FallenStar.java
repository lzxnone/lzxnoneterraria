package com.lzxnone.terraria.item.normal;

import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.accessory.AccessoryUtil;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.utils.ParticleUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class FallenStar extends Item {
    public FallenStar() {
        super(new Item.Properties());
    }

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        private static final double GRAVITY = 0.04D;
        private static final double AIR_FRICTION = 0.98D;
        private static final double GROUND_FRICTION = 0.6D;
        private static final double MAX_FALL_SPEED = -1.5D;
        private static final double MIN_ATTRACT_RADIUS = 0D;
        private static final double PICKUP_RADIUS = 0.35D;
        private static final double MAX_PICKUP_RADIUS = 16.0D;
        private static final double ATTRACT_SPEED = 0.25D;

        @Override
        public void tick(StaticSummon summon) {
            Vec3 movement = summon.getDeltaMovement();
            if(summon.onGround()) {
                movement = new Vec3(movement.x * GROUND_FRICTION, 0.0D, movement.z * GROUND_FRICTION);
                CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
                if(!customData.contains("ground")) SoundUtil.playServerSound(summon.level(), ModSounds.STAR_COLLIDE.get(), summon.position(), 4.0f, 1.0f);
                customData.putBoolean("ground", true);
                summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
            }else {
                movement = movement.add(0.0D, -GRAVITY, 0.0D);
                movement = movement.multiply(AIR_FRICTION, AIR_FRICTION, AIR_FRICTION);
                if(movement.y < MAX_FALL_SPEED) movement = new Vec3(movement.x, MAX_FALL_SPEED, movement.z);
            }
            summon.setDeltaMovement(movement);

            if(summon.level().isClientSide()) {
                ParticleUtil.addParticle(
                    summon.level(), ModParticles.STAR_PARTICLE.get(),
                    summon.getBoundingBox().getCenter(), 0,
                    new Vec3(0, 0, 0), 0.2
                );
                return;
            }

            if(tryPickup(summon)) return;
            if(summon.level().isDay()) {
                onDied(summon);
                return;
            }

            int lifetime = summon.getEntityData().get(StaticSummon.LIFETIME);
            int age = summon.getEntityData().get(StaticSummon.AGE);
            if(lifetime != StaticSummon.INFINITE_LIFETIME && age > lifetime) {
                onDied(summon);
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

            ServerPlayer player = players.getFirst();

            double attractRange = Math.max(AccessoryUtil.getStarPickupRange(player), MIN_ATTRACT_RADIUS);
            Vec3 dir = player.getBoundingBox().getCenter().subtract(summon.position());
            double dist = dir.length();
            if(dist <= attractRange) {
                summon.setDeltaMovement(dir.scale(ATTRACT_SPEED));
            }
            if(dist <= PICKUP_RADIUS) {
                ItemStack stack = new ItemStack(ModItems.FALLEN_STAR.get());
                if(!player.getInventory().add(stack)) return false;
                player.take(summon, 1);
                player.containerMenu.broadcastChanges();
                player.playNotifySound(
                    SoundEvents.ITEM_PICKUP,
                    SoundSource.PLAYERS,
                    0.2F,
                    ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
                );
                onDied(summon);
                return true;
            }
            return false;
        }
    };
}
