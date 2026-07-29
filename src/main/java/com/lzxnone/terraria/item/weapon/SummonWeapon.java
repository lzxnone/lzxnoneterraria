package com.lzxnone.terraria.item.weapon;

import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.effect.SummonEffect;
import com.lzxnone.terraria.enchantment.ModEnchantments;
import com.lzxnone.terraria.enchantment.ModEnchantmentConfigs;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.utils.CollisionUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class SummonWeapon extends Weapon {
    private static final double MAX_RANGE = 256;

    public enum SummonAttackMode {
        PASSIVE("message.lzxnoneterraria.summon_attack_mode.passive", ChatFormatting.GREEN),
        NEUTRAL("message.lzxnoneterraria.summon_attack_mode.neutral", ChatFormatting.YELLOW),
        AGGRESSIVE("message.lzxnoneterraria.summon_attack_mode.aggressive", ChatFormatting.RED),
        ATTACK_TARGET("message.lzxnoneterraria.summon_attack_mode.attack_target", ChatFormatting.BLUE);

        private final String translationKey;
        private final ChatFormatting color;

        SummonAttackMode(String translationKey, ChatFormatting color) {
            this.translationKey = translationKey;
            this.color = color;
        }

        public Component getDisplayName() {
            return Component.translatable(translationKey);
        }

        public ChatFormatting getColor() {
            return color;
        }
    }

    public SummonWeapon(Tier tier, Properties properties) {
        super(tier, properties);
    }

    public static int getMaxSummonCount(Player player) {
        MobEffectInstance instance = player.getEffect(ModEffects.SUMMON);
        return 1 + (instance == null ? 0 : (instance.getAmplifier() + 1) * SummonEffect.getSummonCountPerLevel());
    }

    public static float applySummonDamageBonus(ItemStack stack, LivingEntity entity, float damage) {
        double finalDamage = damage;
        int amplificationLevel = getEnchantmentLevel(entity, stack, ModEnchantments.SUMMON_AMPLIFICATION);

        finalDamage *= Math.pow(ModEnchantmentConfigs.getSummonAmplificationDamageMultiplier(), amplificationLevel);
        return (float)Math.max(0.0D, finalDamage);
    }

    public static List<UUID> getSummons(Player player) {
        return new ArrayList<>(player.getData(ModAttachments.SUMMON_WEAPON_SUMMONS));
    }

    public static SummonAttackMode getAttackMode(Player player) {
        int mode = player.getData(ModAttachments.SUMMON_ATTACK_MODE);
        SummonAttackMode[] modes = SummonAttackMode.values();
        if(mode < 0 || mode >= modes.length) return SummonAttackMode.AGGRESSIVE;
        return modes[mode];
    }

    public static SummonAttackMode cycleAttackMode(Player player) {
        SummonAttackMode[] modes = SummonAttackMode.values();
        int next = (getAttackMode(player).ordinal() + 1) % modes.length;
        player.setData(ModAttachments.SUMMON_ATTACK_MODE, next);
        return modes[next];
    }

    public static Entity findSummonTarget(StaticSummon summon, Player player, double range) {
        SummonAttackMode mode = getAttackMode(player);
        if(mode == SummonAttackMode.PASSIVE) return null;

        if(mode == SummonAttackMode.NEUTRAL) {
            LivingEntity target = player.getLastHurtByMob();
            return isValidSummonTarget(summon, player, target, range) ? target : null;
        }

        if(mode == SummonAttackMode.ATTACK_TARGET) {
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

    public static void registerSummon(Player player, Entity summon) {
        if(!(player.level() instanceof ServerLevel serverLevel)) return;

        List<UUID> summons = getSummons(player);
        summons.removeIf(uuid -> {
            Entity entity = serverLevel.getEntity(uuid);
            if(entity instanceof StaticSummon summon1) {
                Entity owner = summon1.getOwner();
                if(owner != null) {
                    double dist = owner.position().subtract(summon.position()).length();
                    if(dist > MAX_RANGE) return true;
                }else return true;
            }
            return entity == null || !entity.isAlive();
        });

        int maxSummonCount = Math.max(1, getMaxSummonCount(player));
        while(summons.size() >= maxSummonCount) {
            UUID uuid = summons.remove(0);
            Entity entity = serverLevel.getEntity(uuid);
            if(entity != null) entity.discard();
        }

        summons.add(summon.getUUID());
        player.setData(ModAttachments.SUMMON_WEAPON_SUMMONS, summons);
        player.displayClientMessage(
            Component.translatable("message.lzxnoneterraria.current_summons", summons.size(), maxSummonCount)
                .withStyle(summons.size() < maxSummonCount ? ChatFormatting.GREEN : ChatFormatting.RED),
            true
        );
    }

    public static void addFreshSummon(Player player, Entity summon) {
        registerSummon(player, summon);
        player.level().addFreshEntity(summon);
    }

    public String getSummonId() {
        return "";
    }

    private static int getEnchantmentLevel(LivingEntity entity, ItemStack stack, ResourceKey<Enchantment> enchantment) {
        return entity.registryAccess()
            .lookupOrThrow(Registries.ENCHANTMENT)
            .get(enchantment)
            .map(stack::getEnchantmentLevel)
            .orElse(0);
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
