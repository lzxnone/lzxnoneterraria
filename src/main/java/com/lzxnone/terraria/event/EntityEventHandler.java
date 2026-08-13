package com.lzxnone.terraria.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.attachment.LavaImmunity;
import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.attachment.PlayerMana;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.effect.BetsysCurseEffect;
import com.lzxnone.terraria.effect.IchorEffect;
import com.lzxnone.terraria.effect.MidasEffect;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.effect.PaladinsShieldEffect;
import com.lzxnone.terraria.effect.IceBarrierEffect;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.accessory.PanicNecklace;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.accessory.AccessoryUtil;
import com.lzxnone.terraria.item.weapon.RangedWeapon;
import com.lzxnone.terraria.item.weapon.Weapon;
import com.lzxnone.terraria.item.weapon.melee.DevilsDevastation;
import com.lzxnone.terraria.item.weapon.summon.whip.Possession;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID)
public class EntityEventHandler {
    //魔力星掉落概率
    private static final double MANA_STAR_DROP_CHANCE = 1.0D / 9.0D;

    //吸引掉落物
    private static final double ITEM_PICKUP_PROBE_RANGE = 16.0D;
    private static final double ITEM_ATTRACT_MIN_SPEED = 0.08D;
    private static final double ITEM_ATTRACT_MAX_SPEED = 0.6D;
    private static final double ITEM_ATTRACT_SPEED_SCALE = 0.18D;
    private static final double ITEM_ATTRACT_EXISTING_MOTION_SCALE = 0.6D;
    private static final double ITEM_PICKUP_TOUCH_RANGE_SQR = 1.2D * 1.2D;

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if(!(event.getEntity() instanceof ItemEntity itemEntity)) return;
        if(!(itemEntity.level() instanceof ServerLevel serverLevel)) return;
        if(!itemEntity.isAlive() || itemEntity.getItem().isEmpty() || itemEntity.hasPickUpDelay()) return;

        ServerPlayer target = null;
        double bestScore = Double.MAX_VALUE;
        List<ServerPlayer> players = serverLevel.getEntitiesOfClass(
            ServerPlayer.class,
            itemEntity.getBoundingBox().inflate(ITEM_PICKUP_PROBE_RANGE),
            player -> player.isAlive() && !player.isSpectator()
        );
        for(ServerPlayer player : players) {
            double range = AccessoryUtil.getEffectivePickupRange(player, itemEntity.getItem().getItem());
            if(range <= 0.0D) continue;
            double distanceSqr = itemEntity.distanceToSqr(player);
            if(distanceSqr > range * range) continue;
            if(distanceSqr < bestScore) {
                bestScore = distanceSqr;
                target = player;
            }
        }
        if(target == null) return;

        if(itemEntity.distanceToSqr(target) <= ITEM_PICKUP_TOUCH_RANGE_SQR) {
            itemEntity.playerTouch(target);
            return;
        }

        Vec3 toPlayer = target.getEyePosition().subtract(itemEntity.position());
        double distance = toPlayer.length();
        if(distance < 1.0E-4D) return;
        double speed = Mth.clamp(distance * ITEM_ATTRACT_SPEED_SCALE, ITEM_ATTRACT_MIN_SPEED, ITEM_ATTRACT_MAX_SPEED);
        Vec3 attractMotion = toPlayer.scale(speed / distance);
        itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().scale(ITEM_ATTRACT_EXISTING_MOTION_SCALE).add(attractMotion));
        itemEntity.hasImpulse = true;
    }

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
        //岩浆免疫（仅岩浆，不免疫岩浆块）
        if(source.is(DamageTypes.LAVA) && entity instanceof Player player) {
            LavaImmunity lavaImmunity = player.getData(ModAttachments.LAVA_IMMUNITY);
            if(lavaImmunity.getCurrentTicks() > 0) {
                event.setCanceled(true);
                return;
            }
            double reduction = AccessoryUtil.getLavaDamageReduction(player);
            if(reduction > 0.0D) {
                event.setAmount(event.getAmount() * (float) (1.0D - reduction));
            }
        }
        //进行护甲穿透
        if(!source.is(DamageTypeTags.BYPASSES_ARMOR)) {
            if(attacker instanceof LivingEntity livingEntity) AccessoryUtil.applyTargetArmorModifiers(livingEntity, entity);
            IchorEffect.applyArmorReduction(entity);
            BetsysCurseEffect.applyArmorReduction(entity);
        }
        //近战攻击命中时点燃目标
        if(attacker instanceof LivingEntity livingAttacker && livingAttacker != entity) {
            if(source.is(DamageTypes.PLAYER_ATTACK) || source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.MOB_ATTACK_NO_AGGRO)) {
                if(AccessoryUtil.canApplyIgnite(livingAttacker)) entity.igniteForTicks(AccessoryUtil.getIgniteTicks(livingAttacker));
            }
        }
        //受到伤害时恢复魔力
        if(entity instanceof Player player) {
            boolean isDebuffDamage = source.is(DamageTypes.DROWN) || source.is(DamageTypes.MAGIC)
                || source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.WITHER);
            double manaMultiplier = AccessoryUtil.getManaOnHurtMultiplier(player);
            if(!isDebuffDamage && manaMultiplier > 0.0D) {
                int recover = (int) Math.round(event.getAmount() * manaMultiplier);
                if(recover > 0 && player instanceof ServerPlayer serverPlayer) PlayerMana.recoverMana(serverPlayer, recover);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        if(!(target.level() instanceof ServerLevel serverLevel)) return;
        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();
        //原版的箭伤害
        if(source.is(DamageTypes.ARROW) && attacker instanceof LivingEntity livingAttacker) {
            ItemStack weaponStack = source.getWeaponItem();
            if(weaponStack != null && !weaponStack.isEmpty()) {
                float finalDamage = Weapon.applyDamageBonus(weaponStack, livingAttacker, event.getNewDamage());
                finalDamage = RangedWeapon.applyRangedDamageBonus(weaponStack, livingAttacker, finalDamage);
                event.setNewDamage(finalDamage);
            }
        }
        //移除饰品加成护甲穿透
        AccessoryUtil.removeTargetArmorModifiers(event.getEntity());
        IchorEffect.removeArmorReduction(event.getEntity());
        BetsysCurseEffect.removeArmorReduction(event.getEntity());
        //暴击
        if(attacker instanceof LivingEntity livingEntity && attacker != target) {
            double critChance = AccessoryUtil.getCritChance(livingEntity);
            if(critChance > 0.0D && attacker.getRandom().nextDouble() < critChance) {
                event.setNewDamage(event.getNewDamage() * 1.5F);
            }
            //击中敌人概率掉落绿宝石（敌对生物、末影龙或凋灵）
            double emeraldDropChance = AccessoryUtil.getEmeraldDropChance(livingEntity);
            if(emeraldDropChance > 0.0D
                && (target instanceof Enemy || target instanceof EnderDragon || target instanceof WitherBoss)
                && livingEntity.getRandom().nextDouble() < emeraldDropChance) {
                target.spawnAtLocation(new ItemStack(Items.EMERALD));
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
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        //支配之鞭 预兆标记
        Possession.markEvent(event);

        if(event.getEntity() instanceof Player player) {
            AccessoryUtil.applyInvulnerableTimeModifier(player);
            AccessoryUtil.applyFallenStarSummoner(player);
            AccessoryUtil.applyBeeSummoner(player);
            //受到伤害后触发恐慌加速
            if(AccessoryUtil.canApplyPanic(player) && PanicNecklace.DURATION.get() > 0) {
                player.addEffect(new MobEffectInstance(ModEffects.PANIC, PanicNecklace.DURATION.get(), 0, false, false));
            }
        }
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();
        Entity killer = event.getSource().getEntity();
        if(killer instanceof Projectile projectile) {
            Entity owner = projectile.getOwner();
            if(owner instanceof Player) killer = owner;
        }
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

            //生成魔力星
            if(killer instanceof Player player
                    && livingEntity instanceof Monster
                    && player.getData(ModAttachments.PLAYER_MANA).getMana() < player.getData(ModAttachments.PLAYER_MANA).getMaxMana()
                    && livingEntity.getRandom().nextFloat() < MANA_STAR_DROP_CHANCE) {
                StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), livingEntity.level());
                summon.setPos(livingEntity.position());
                summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.MANA_STAR);
                summon.getEntityData().set(StaticSummon.RENDER_MODE, "item");
                summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.MANA_STAR.get()));
                summon.getEntityData().set(StaticSummon.LIFETIME, 1200);
                summon.getEntityData().set(StaticSummon.GLOW, true);
                CompoundTag customData = new CompoundTag();
                customData.putInt("mana", 100);
                summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
                livingEntity.level().addFreshEntity(summon);
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
        //岩浆免疫计时：岩浆中递减，离开后逐步恢复（每 tick +1）
        LavaImmunity lavaImmunity = player.getData(ModAttachments.LAVA_IMMUNITY);
        if(lavaImmunity.getMaxTicks() > 0) {
            if(player.isInLava()) {
                lavaImmunity.setCurrentTicks(Math.max(0, lavaImmunity.getCurrentTicks() - 1));
            }else {
                lavaImmunity.setCurrentTicks(Math.min(lavaImmunity.getMaxTicks(), lavaImmunity.getCurrentTicks() + 1));
            }
        }
        //生命再生：每秒统计累加值并回复
        if(player.tickCount % 20 == 0) {
            float regen = AccessoryUtil.getHealthPerSecond(player);
            if(regen > 0.0F) {
                player.heal(regen);
            }
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
