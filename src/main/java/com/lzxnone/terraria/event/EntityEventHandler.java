package com.lzxnone.terraria.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.effect.MidasEffect;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.accessory.AccessoryUtil;
import com.lzxnone.terraria.item.weapon.melee.DevilsDevastation;
import com.lzxnone.terraria.item.weapon.summon.whip.Possession;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;
import java.util.UUID;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID)
public class EntityEventHandler {
    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if(entity.level().isClientSide()) return;
        DamageSource source = event.getSource();
        Entity attacker = event.getSource().getEntity();
        //免疫伤害
        if(Math.random() <= AccessoryUtil.applyDamageImmunity(entity)) {
            event.setCanceled(true);
            return;
        }
        //免疫火块
        if(source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.HOT_FLOOR) || source.is(DamageTypes.CAMPFIRE)) {
            if(AccessoryUtil.isFireBlockImmune(entity)) event.setCanceled(true);
        }
        //进行护甲修饰
        if(!source.is(DamageTypeTags.BYPASSES_ARMOR)) {
            if(attacker instanceof LivingEntity livingEntity) AccessoryUtil.applyTargetArmorModifiers(livingEntity, entity);
        }
    }

    @SubscribeEvent
    public static void onArmorPierceRemove(LivingDamageEvent.Pre event) {
        //移除护甲
        AccessoryUtil.removeTargetArmorModifiers(event.getEntity());
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        //支配之鞭 预兆标记
        Possession.markEvent(event);

        if(event.getEntity() instanceof Player player) {
            AccessoryUtil.applyInvulnerableTimeModifier(player);
            AccessoryUtil.applyFallenStarSummoner(player);
            AccessoryUtil.applyBeeSummoner(player);
        }
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

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if(player.level().isClientSide()) return;
        if(player.getTicksFrozen() > 0 && AccessoryUtil.isFreezeImmune(player)) {
            player.setTicksFrozen(0);
        }
    }

}
