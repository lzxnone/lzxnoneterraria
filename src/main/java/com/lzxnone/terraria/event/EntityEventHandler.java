package com.lzxnone.terraria.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.attachment.PlayerSummon;
import com.lzxnone.terraria.effect.MidasEffect;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.weapon.melee.DevilsDevastation;
import com.lzxnone.terraria.item.weapon.summon.whip.Possession;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.List;
import java.util.UUID;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID)
public class EntityEventHandler {
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
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        //支配之鞭 预兆标记
        Possession.markEvent(event);
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if(livingEntity.level() instanceof ServerLevel serverLevel) {
            if(livingEntity instanceof ServerPlayer player) {
                clearPlayerSummons(player);
            }

            //迈达斯生成绿宝石
            if(livingEntity.hasEffect(ModEffects.MIDAS) && livingEntity.getRandom().nextFloat() < MidasEffect.getDropChance()) {
                livingEntity.spawnAtLocation(new ItemStack(Items.EMERALD));
            }

            //消耗插入的破灭魔王剑
            List<UUID> stuckList = livingEntity.getData(ModAttachments.STUCK_DEVILS_DEVASTATION_PROJECTILE);
            while(!stuckList.isEmpty()) {
                UUID uuid = stuckList.getLast();
                stuckList.removeLast();
                Entity stuck = serverLevel.getEntity(uuid);
                if(stuck instanceof StaticSummon stuckProjectile) DevilsDevastation.summonStuckProjectile(stuckProjectile);
            }
        }
    }

    private static void clearPlayerSummons(ServerPlayer player) {
        PlayerSummon playerSummon = player.getData(ModAttachments.PLAYER_SUMMON);
        discardSummonSlots(player.serverLevel(), playerSummon.getMinionSlots());
        discardSummonSlots(player.serverLevel(), playerSummon.getSentrySlots());
        playerSummon.getMinionSlots().clear();
        playerSummon.getSentrySlots().clear();
        player.setData(ModAttachments.PLAYER_SUMMON, playerSummon);
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
