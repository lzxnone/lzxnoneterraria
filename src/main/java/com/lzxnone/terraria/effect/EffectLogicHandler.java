package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.LzxnoneTerraria;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.Objects;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID)
public class EffectLogicHandler {

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance instance = event.getEffectInstance();
        Holder<MobEffect> currentEffect = null;
        if(instance != null) {
            currentEffect = instance.getEffect();
        }
        if(checkEffect(currentEffect) && !entity.level().isClientSide() && !(entity instanceof Player)) {
            if(entity.level() instanceof ServerLevel serverLevel) {
                serverLevel.getChunkSource().broadcast(entity,
                        new ClientboundUpdateMobEffectPacket(entity.getId(), instance, false));
            }
        }
    }

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance instance = event.getEffectInstance();
        Holder<MobEffect> currentEffect = null;
        if(instance != null) {
            currentEffect = instance.getEffect();
        }
        if(checkEffect(currentEffect) && !entity.level().isClientSide() && !(entity instanceof Player)) {
            if(entity.level() instanceof ServerLevel serverLevel) {
                if(event.getEffectInstance() != null) {
                    serverLevel.getChunkSource().broadcast(entity,
                            new ClientboundRemoveMobEffectPacket(entity.getId(), currentEffect));
                }
                if(currentEffect == ModEffects.KILL_MODE) {
                    MobEffectInstance effectInstance = new MobEffectInstance(ModEffects.KILL_MODE_COOLDOWN, 100, 0);
                    entity.addEffect(effectInstance);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance instance = event.getEffectInstance();
        Holder<MobEffect> currentEffect = null;
        if(instance != null) {
            currentEffect = instance.getEffect();
        }
        if(checkEffect(currentEffect) && !entity.level().isClientSide() && !(entity instanceof Player)) {
            if(entity.level() instanceof ServerLevel serverLevel) {
                if(event.getEffectInstance() != null) {
                    serverLevel.getChunkSource().broadcast(entity,
                            new ClientboundRemoveMobEffectPacket(entity.getId(), currentEffect));
                }
                if(currentEffect == ModEffects.KILL_MODE) {
                    MobEffectInstance effectInstance = new MobEffectInstance(ModEffects.KILL_MODE_COOLDOWN, 100, 0);
                    entity.addEffect(effectInstance);
                }
            }
        }
    }

    public static boolean checkEffect(Holder<MobEffect> effect) {
        if(effect == null) return false;
        return
            effect == ModEffects.CONFUSED
            || effect == ModEffects.KILL_MODE;
    }
}
