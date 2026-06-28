package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.LzxnoneTerraria;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID)
public class EffectLogicHandler {

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        if(!entity.level().isClientSide() && !(entity instanceof Player)) {
            if(entity.level() instanceof ServerLevel serverLevel) {
                serverLevel.getChunkSource().broadcast(entity,
                        new ClientboundUpdateMobEffectPacket(entity.getId(), event.getEffectInstance(), false));
            }
        }
    }

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        LivingEntity entity = event.getEntity();
        if(!entity.level().isClientSide() && !(entity instanceof Player)) {
            if(entity.level() instanceof ServerLevel serverLevel) {
                serverLevel.getChunkSource().broadcast(entity,
                        new ClientboundRemoveMobEffectPacket(entity.getId(), event.getEffectInstance().getEffect()));
            }
        }
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide() && !(entity instanceof Player)) {
            if (entity.level() instanceof ServerLevel serverLevel) {
                serverLevel.getChunkSource().broadcast(entity,
                        new ClientboundRemoveMobEffectPacket(entity.getId(), event.getEffectInstance().getEffect()));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Post event) {
        if(!event.getEntity().level().isClientSide() && event.getEntity() instanceof PathfinderMob mob) {

        }
    }

}
