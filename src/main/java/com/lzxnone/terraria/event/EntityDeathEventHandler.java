package com.lzxnone.terraria.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.weapon.melee.DevilsDevastation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.List;
import java.util.UUID;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID)
public class EntityDeathEventHandler {
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();

        //消耗插入的破灭魔王剑
        if(livingEntity.level() instanceof ServerLevel serverLevel) {
            List<UUID> stuckList = livingEntity.getData(ModAttachments.STUCK_DEVILS_DEVASTATION_PROJECTILE);
            while(!stuckList.isEmpty()) {
                UUID uuid = stuckList.getLast();
                stuckList.removeLast();
                Entity stuck = serverLevel.getEntity(uuid);
                if(stuck instanceof StaticSummon stuckProjectile) DevilsDevastation.summonStuckProjectile(stuckProjectile);
            }
        }
    }
}
