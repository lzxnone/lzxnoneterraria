package com.lzxnone.terraria.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.effect.MidasEffect;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.effect.PaladinsShieldEffect;
import com.lzxnone.terraria.effect.IceBarrierEffect;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.accessory.AccessoryUtil;
import com.lzxnone.terraria.item.weapon.melee.DevilsDevastation;
import com.lzxnone.terraria.item.weapon.summon.whip.Possession;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;
import java.util.Optional;
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
            if(AccessoryUtil.isFireBlockImmune(entity)) {
                event.setCanceled(true);
                return;
            }
        }
        //进行护甲穿透
        if(!source.is(DamageTypeTags.BYPASSES_ARMOR)) {
            if(attacker instanceof LivingEntity livingEntity) AccessoryUtil.applyTargetArmorModifiers(livingEntity, entity);
        }
        //近战攻击命中时点燃目标
        if(attacker instanceof LivingEntity livingAttacker && livingAttacker != entity) {
            if(source.is(DamageTypes.PLAYER_ATTACK) || source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.MOB_ATTACK_NO_AGGRO)) {
                if(AccessoryUtil.canApplyIgnite(livingAttacker)) entity.igniteForTicks(AccessoryUtil.getIgniteTicks(livingAttacker));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        if(!(target.level() instanceof ServerLevel serverLevel)) return;
        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();
        //移除饰品加成护甲穿透
        AccessoryUtil.removeTargetArmorModifiers(event.getEntity());
        //暴击
        if(attacker instanceof LivingEntity livingEntity && attacker != target) {
            double critChance = AccessoryUtil.getCritChance(livingEntity);
            if(critChance > 0.0D && attacker.getRandom().nextDouble() < critChance) {
                event.setNewDamage(event.getNewDamage() * 1.5F);
            }
        }
        //冰障
        if(target.hasEffect(ModEffects.ICE_BARRIER)) {
            MobEffectInstance instance = target.getEffect(ModEffects.ICE_BARRIER);
            if(instance != null) event.setNewDamage(event.getNewDamage() * (float) IceBarrierEffect.getDamageMultiplier(instance.getAmplifier()));
        }
        //圣骑士护盾伤害转移
        if(target.hasEffect(ModEffects.PALADINS_GUARDIAN)) {
            UUID guardianId = target.getData(ModAttachments.PALADIN_GUARDIAN_SOURCE).orElse(null);
            if(guardianId != null && !guardianId.equals(target.getUUID())) {
                if(serverLevel.getEntity(guardianId) instanceof LivingEntity paladin
                    && paladin.isAlive() && paladin.getHealth() / paladin.getMaxHealth() > AccessoryUtil.getGuardianMinHealthRatio(paladin)) {
                    float ratio = PaladinsShieldEffect.getDamageAbsorptionRatio();
                    if(ratio > 0.0F) {
                        float transferred = event.getNewDamage() * ratio;
                        event.setNewDamage(event.getNewDamage() - transferred);
                        paladin.hurt(event.getSource(), transferred);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntity();
        if(entity.level().isClientSide()) return;
        double heightMultiplier = AccessoryUtil.getJumpHeightMultiplier(entity);
        double speedMultiplier = AccessoryUtil.getJumpSpeedMultiplier(entity);
        if(heightMultiplier <= 1.0D && speedMultiplier <= 1.0D) return;
        Vec3 motion = entity.getDeltaMovement();
        entity.setDeltaMovement(
            motion.x * speedMultiplier,
            motion.y * heightMultiplier,
            motion.z * speedMultiplier
        );
    }

    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
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
        //水下呼吸
        if(player.isInWater() && player.getAirSupply() < player.getMaxAirSupply() && AccessoryUtil.isWaterBreathing(player)) {
            player.setAirSupply(player.getMaxAirSupply());
        }
        //同步昼夜限定饰品的动态属性
        if(player.tickCount % 20 == 0) AccessoryUtil.syncConditionalModifiers(player);
        //免疫冰冻
        if(player.getTicksFrozen() > 0 && AccessoryUtil.isFreezeImmune(player)) {
            player.setTicksFrozen(0);
        }
        //圣骑士护盾效果
        if(player.tickCount % 30 == 0) {
            if(AccessoryUtil.canApplyGuardianShield(player)) {
                double range = AccessoryUtil.getGuardianRange(player);
                player.addEffect(new MobEffectInstance(ModEffects.PALADINS_GUARDIAN, 40, 0, false, false));
                player.setData(ModAttachments.PALADIN_GUARDIAN_SOURCE, Optional.of(player.getUUID()));
                if(range > 0) {
                    List<Player> allies = player.level().getEntitiesOfClass(
                        Player.class, player.getBoundingBox().inflate(range),
                        ally -> ally != player && ally.isAlive() && player.isAlliedTo(ally)
                    );
                    for(Player ally : allies) {
                        UUID writeUuid;
                        if(AccessoryUtil.canApplyGuardianShield(ally)) {
                            writeUuid = ally.getUUID();
                        }else {
                            UUID current = ally.getData(ModAttachments.PALADIN_GUARDIAN_SOURCE).orElse(null);
                            writeUuid = (current != null && (((ServerLevel) player.level()).getEntity(current) instanceof LivingEntity living && living.isAlive())) ? current : player.getUUID();
                        }
                        ally.addEffect(new MobEffectInstance(ModEffects.PALADINS_GUARDIAN, 40, 0, false, false));
                        ally.setData(ModAttachments.PALADIN_GUARDIAN_SOURCE, Optional.of(writeUuid));
                    }
                }
            }
        }
        //冰障效果
        if(player.tickCount % 30 == 0 && AccessoryUtil.canApplyIceBarrier(player)) {
            double threshold = AccessoryUtil.getIceBarrierMaxHealthRatio(player);
            if(threshold > 0.0D && player.getHealth() / player.getMaxHealth() <= threshold) {
                player.addEffect(new MobEffectInstance(ModEffects.ICE_BARRIER, 40, 0, false, false));
            }
        }
    }
}
