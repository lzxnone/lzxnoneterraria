package com.lzxnone.terraria.item.weapon;

import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.effect.SummonEffect;
import com.lzxnone.terraria.enchantment.ModEnchantments;
import com.lzxnone.terraria.enchantment.ModEnchantmentConfigs;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SummonWeapon extends Weapon {
    private static final double MAX_RANGE = 256;

    public SummonWeapon(Tier tier, Properties properties) {
        super(tier, properties);
    }

    public SummonWeapon(Properties properties) {
        super(properties);
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

    public static void removeSummon(Player player, Entity summon) {
        List<UUID> summons = getSummons(player);
        if(summons.remove(summon.getUUID())) {
            player.setData(ModAttachments.SUMMON_WEAPON_SUMMONS, summons);
        }
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
}
