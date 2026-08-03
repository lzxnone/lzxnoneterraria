package com.lzxnone.terraria.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.attachment.PlayerSummon;
import com.lzxnone.terraria.network.PlayerSummonSync;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.List;
import java.util.UUID;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID)
public class PlayerSummonClearEventHandler {
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.getEntity() instanceof ServerPlayer player) {
            clearPlayerSummons(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if(event.getEntity() instanceof ServerPlayer player) {
            clearPlayerSummons(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if(event.getEntity() instanceof ServerPlayer player) {
            clearPlayerSummons(player);
        }
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if(livingEntity.level() instanceof ServerLevel serverLevel) {
            if(livingEntity instanceof ServerPlayer player) {
                clearPlayerSummons(player);
            }
        }
    }

    private static void clearPlayerSummons(ServerPlayer player) {
        PlayerSummon playerSummon = player.getData(ModAttachments.PLAYER_SUMMON);
        discardSummonSlots(player.serverLevel(), playerSummon.getMinionSlots());
        discardSummonSlots(player.serverLevel(), playerSummon.getSentrySlots());
        playerSummon.getMinionSlots().clear();
        playerSummon.getSentrySlots().clear();
        PlayerSummonSync.setAndSync(player, playerSummon);
    }

    private static void discardSummonSlots(ServerLevel serverLevel, List<PlayerSummon.SummonSlot> slots) {
        for(PlayerSummon.SummonSlot slot : slots) {
            for(UUID uuid : slot.getSummons()) {
                Entity entity = serverLevel.getEntity(uuid);
                if(entity != null) entity.discard();
            }
        }
    }
}
