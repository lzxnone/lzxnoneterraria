package com.lzxnone.terraria.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.effect.MidasEffect;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.weapon.melee.DevilsDevastation;
import com.lzxnone.terraria.item.weapon.summon.whip.Possession;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.List;
import java.util.UUID;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID)
public class EntityEventHandler {
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        //支配之鞭 预兆标记
        Possession.markEvent(event);
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if(livingEntity.level() instanceof ServerLevel serverLevel) {
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
}
