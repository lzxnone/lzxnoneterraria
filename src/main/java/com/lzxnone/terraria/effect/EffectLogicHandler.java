package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.item.weapon.melee.DevilsDevastation;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

public class EffectLogicHandler {

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance instance = event.getEffectInstance();
        Holder<MobEffect> currentEffect = null;
        if(instance != null) {
            currentEffect = instance.getEffect();
        }
        if(checkEffect(currentEffect)) {
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
        if(checkEffect(currentEffect)) {
            if(entity.level() instanceof ServerLevel serverLevel) {
                if(event.getEffectInstance() != null) {
                    serverLevel.getChunkSource().broadcast(entity,
                            new ClientboundRemoveMobEffectPacket(entity.getId(), currentEffect));
                }
                if(currentEffect == ModEffects.KILL_MODE) {
                    MobEffectInstance effectInstance = new MobEffectInstance(ModEffects.KILL_MODE_COOLDOWN, DevilsDevastation.getKillModeCooldownTime(), 0);
                    entity.addEffect(effectInstance);
                }else if(currentEffect == ModEffects.KILL_MODE_COOLDOWN) {
                    SoundUtil.playServerSound(entity, ModSounds.DEMON_SWORD_KILL_MODE_OFF_COOLDOWN.get(), 0.5f, 1.0f);
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
        if(checkEffect(currentEffect)) {
            if(entity.level() instanceof ServerLevel serverLevel) {
                if(event.getEffectInstance() != null) {
                    serverLevel.getChunkSource().broadcast(entity,
                            new ClientboundRemoveMobEffectPacket(entity.getId(), currentEffect));
                }
                if(currentEffect == ModEffects.KILL_MODE) {
                    MobEffectInstance effectInstance = new MobEffectInstance(ModEffects.KILL_MODE_COOLDOWN, DevilsDevastation.getKillModeCooldownTime(), 0);
                    entity.addEffect(effectInstance);
                }else if(currentEffect == ModEffects.KILL_MODE_COOLDOWN) {
                    SoundUtil.playServerSound(entity, ModSounds.DEMON_SWORD_KILL_MODE_OFF_COOLDOWN.get(), 0.5f, 1.0f);
                }
            }
        }
    }

    public static boolean checkEffect(Holder<MobEffect> effect) {
        if(effect == null) return false;
        return
            effect == ModEffects.CONFUSED
            || effect == ModEffects.KILL_MODE
            || effect == ModEffects.KILL_MODE_COOLDOWN
            || effect == ModEffects.MIDAS
            || effect == ModEffects.CURSED_INFERNO
            || effect == ModEffects.ICHOR
            || effect == ModEffects.ACID_VENOM;
    }
}
