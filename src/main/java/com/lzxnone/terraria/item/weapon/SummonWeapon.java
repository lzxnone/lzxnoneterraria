package com.lzxnone.terraria.item.weapon;

import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SummonWeapon extends Item {
    private final int enchantmentValue;

    public SummonWeapon(Tier tier, Properties properties) {
        super(properties.durability(tier.getUses()));
        this.enchantmentValue = tier.getEnchantmentValue();
    }

    public SummonWeapon(Properties properties) {
        super(properties);
        this.enchantmentValue = 0;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getMaxStackSize() == 1;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return enchantmentValue;
    }

    public static int getMaxSummonCount(Player player) {
        MobEffectInstance instance = player.getEffect(ModEffects.SUMMON);
        return 1 + (instance == null ? 0 : instance.getAmplifier() + 1);
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
                    if(dist > 256) return true;
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
}
