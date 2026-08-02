package com.lzxnone.terraria.item.weapon.summon.minion;

import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.attachment.PlayerSummon;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.effect.SummonEffect;
import com.lzxnone.terraria.enchantment.ModEnchantments;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.weapon.SummonWeapon;
import com.lzxnone.terraria.utils.CollisionUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class MinionWeapon extends SummonWeapon {
    public static final double MAX_RANGE = 256.0D;

    public MinionWeapon(Tier tier, Properties properties) {
        super(tier, properties);
    }

    public static int getMaxSummonCount(Player player) {
        MobEffectInstance instance = player.getEffect(ModEffects.SUMMON);
        return 1 + (instance == null ? 0 : (instance.getAmplifier() + 1) * SummonEffect.getSummonCountPerLevel());
    }

    public static void addFreshSummon(Player player, String id, List<? extends Entity> slotSummons) {
        if(!(player.level() instanceof ServerLevel serverLevel)) return;
        if(slotSummons.isEmpty()) return;

        PlayerSummon summonData = player.getData(ModAttachments.PLAYER_SUMMON);
        List<PlayerSummon.SummonSlot> slots = cleanSummonSlots(serverLevel, summonData.getMinionSlots(), slotSummons.getFirst().position());

        int maxSummonCount = Math.max(1, getMaxSummonCount(player));
        while(slots.size() >= maxSummonCount) {
            PlayerSummon.SummonSlot removedSlot = slots.removeFirst();
            for(UUID uuid : removedSlot.getSummons()) {
                Entity entity = serverLevel.getEntity(uuid);
                if(entity != null) entity.discard();
            }
        }

        slots.add(new PlayerSummon.SummonSlot(id, slotSummons.stream().map(Entity::getUUID).toList()));
        summonData.setMinionSlots(slots);
        player.setData(ModAttachments.PLAYER_SUMMON, summonData);
        player.displayClientMessage(
            Component.translatable("message.lzxnoneterraria.current_minions", slots.size(), maxSummonCount)
                .withStyle(slots.size() < maxSummonCount ? net.minecraft.ChatFormatting.GREEN : net.minecraft.ChatFormatting.RED),
            true
        );

        for(Entity summon : slotSummons) {
            player.level().addFreshEntity(summon);
        }
    }

    protected static List<PlayerSummon.SummonSlot> cleanSummonSlots(ServerLevel serverLevel, List<PlayerSummon.SummonSlot> slots, Vec3 referencePos) {
        List<PlayerSummon.SummonSlot> cleaned = new ArrayList<>();
        for(PlayerSummon.SummonSlot slot : slots) {
            List<UUID> cleanedSlot = new ArrayList<>();
            boolean removeSlot = false;

            for(UUID uuid : slot.getSummons()) {
                Entity entity = serverLevel.getEntity(uuid);
                if(entity == null || !entity.isAlive()) continue;

                if(entity instanceof StaticSummon staticSummon) {
                    Entity owner = staticSummon.getOwner();
                    if(owner == null || owner.position().subtract(referencePos).length() > MAX_RANGE) {
                        removeSlot = true;
                        break;
                    }
                }
                cleanedSlot.add(uuid);
            }

            if(removeSlot) {
                for(UUID uuid : slot.getSummons()) {
                    Entity entity = serverLevel.getEntity(uuid);
                    if(entity != null) entity.discard();
                }
            }else if(!cleanedSlot.isEmpty()) {
                cleaned.add(new PlayerSummon.SummonSlot(slot.getId(), cleanedSlot));
            }
        }
        return cleaned;
    }

    public static Entity findSummonTarget(StaticSummon summon, Player player, double range) {
        PlayerSummon.State mode = player.getData(ModAttachments.PLAYER_SUMMON).getMinionState();
        if(mode == PlayerSummon.State.PASSIVE) return null;

        if(mode == PlayerSummon.State.NEUTRAL) {
            LivingEntity target = player.getLastHurtByMob();
            return isValidSummonTarget(summon, player, target, range) ? target : null;
        }

        if(mode == PlayerSummon.State.ATTACK_TARGET) {
            return findPlayerRelatedTarget(summon, player, range);
        }

        boolean ignoreBlockOcclusion = hasBarrenLand(summon, player);
        Entity playerRelatedTarget = findPlayerRelatedTarget(summon, player, range);
        if(playerRelatedTarget != null) return playerRelatedTarget;

        AABB searchBox = AABB.ofSize(summon.position(), range * 2.0D, range * 2.0D, range * 2.0D);
        List<Entity> targets = ignoreBlockOcclusion ? CollisionUtil.searchEntities(
            summon.level(),
            searchBox,
            summon,
            player
        ) : CollisionUtil.searchEnemies(
            summon.level(),
            searchBox,
            summon,
            player
        );
        targets.removeIf(target -> !isValidSummonTarget(summon, player, target, range)
            || (!ignoreBlockOcclusion && !isValidEnemySummonTarget(summon, player, target, range))
            || (!ignoreBlockOcclusion && !canPlayerSeeTarget(player, target)));
        targets.sort(Comparator.comparingDouble(target -> target.distanceToSqr(summon.position())));
        return targets.isEmpty() ? null : targets.getFirst();
    }

    public static void storeSummonTarget(StaticSummon summon, Entity target) {
        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA).copy();
        customData.remove("target");
        customData.remove("targetId");
        customData.remove("targetPart");

        if(target instanceof EnderDragonPart dragonPart) {
            customData.putUUID("target", dragonPart.parentMob.getUUID());
            customData.putString("targetPart", dragonPart.name);
        }else if(target != null) {
            customData.putUUID("target", target.getUUID());
        }

        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
    }

    public static Entity getStoredSummonTarget(StaticSummon summon) {
        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
        if(!customData.contains("target") || !(summon.level() instanceof ServerLevel serverLevel)) return null;

        Entity target = serverLevel.getEntity(customData.getUUID("target"));
        if(target instanceof EnderDragon dragon) {
            if(!dragon.isAlive()) return null;

            if(customData.contains("targetPart")) {
                String targetPart = customData.getString("targetPart");
                for(EnderDragonPart part : dragon.getSubEntities()) {
                    if(part.name.equals(targetPart) && CollisionUtil.isEnemySearchTarget(part, summon, summon.getOwner())) {
                        return part;
                    }
                }
            }

            Entity owner = summon.getOwner();
            EnderDragonPart nearestPart = List.of(dragon.getSubEntities()).stream()
                .filter(part -> CollisionUtil.isEnemySearchTarget(part, summon, owner))
                .min(Comparator.comparingDouble(part -> part.distanceToSqr(summon.position())))
                .orElse(null);
            return nearestPart != null ? nearestPart : dragon;
        }
        return target != null && target.isAlive() ? target : null;
    }

    private static Entity findPlayerRelatedTarget(StaticSummon summon, Player player, double range) {
        LivingEntity playerTarget = player.getLastHurtMob();
        if(isValidSummonTarget(summon, player, playerTarget, range)) return playerTarget;

        LivingEntity attacker = player.getLastHurtByMob();
        if(isValidSummonTarget(summon, player, attacker, range)) return attacker;

        return null;
    }

    private static boolean isValidSummonTarget(StaticSummon summon, Player player, Entity target, double range) {
        return target != null
            && target.level() == summon.level()
            && target.distanceToSqr(summon) <= range * range
            && FilterUtil.createTargetFilter(summon, player).test(target);
    }

    private static boolean isValidEnemySummonTarget(StaticSummon summon, Player player, Entity target, double range) {
        return target != null
            && target.level() == summon.level()
            && target.distanceToSqr(summon) <= range * range
            && CollisionUtil.isEnemySearchTarget(target, summon, player);
    }

    private static boolean canPlayerSeeTarget(Player player, Entity target) {
        BlockHitResult hitResult = player.level().clip(new ClipContext(
            player.getEyePosition(),
            target.getEyePosition(),
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            player
        ));
        return hitResult.getType() == HitResult.Type.MISS;
    }

    private static boolean hasBarrenLand(StaticSummon summon, Player player) {
        ItemStack sourceStack = summon.getEntityData().get(StaticSummon.STACK_SOURCE);
        if(sourceStack.isEmpty()) sourceStack = player.getWeaponItem();
        if(sourceStack.isEmpty()) return false;

        return player.registryAccess()
            .lookupOrThrow(Registries.ENCHANTMENT)
            .get(ModEnchantments.BARREN_LAND)
            .map(sourceStack::getEnchantmentLevel)
            .orElse(0) > 0;
    }
}
