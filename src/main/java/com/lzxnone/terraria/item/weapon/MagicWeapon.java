package com.lzxnone.terraria.item.weapon;

import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.attachment.PlayerMana;
import com.lzxnone.terraria.enchantment.ModEnchantments;
import com.lzxnone.terraria.enchantment.ModEnchantmentConfigs;
import com.lzxnone.terraria.event.PlayerManaSyncEventHandler;
import com.lzxnone.terraria.effect.ManaSicknessEffect;
import com.lzxnone.terraria.item.accessory.AccessoryUtil;
import com.lzxnone.terraria.item.accessory.effect.AutoManaPotionUser;
import com.lzxnone.terraria.item.accessory.effect.ManaCostModifier;
import com.lzxnone.terraria.utils.ManaPotionUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public class MagicWeapon extends Weapon {
    public MagicWeapon(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
        if(!canUseMagic(stack, entity)) {
            entity.stopUsingItem();
            return;
        }

        if(!level.isClientSide()) {
            if(!tryConsumeMana(level, entity, stack, getFinalManaConsumeRate(stack, entity))) {
                entity.stopUsingItem();
                return;
            }
        }

        onMagicUseTick(level, entity, stack, count);
    }

    protected void onMagicUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
    }

    protected double getManaConsumeRate(ItemStack stack, LivingEntity entity) {
        return 0.0D;
    }

    protected double getFinalManaConsumeRate(ItemStack stack, LivingEntity entity) {
        double rate = getManaConsumeRate(stack, entity);
        int leakLevel = getEnchantmentLevel(entity, stack, ModEnchantments.MANA_LEAK);
        int efficiencyLevel = getEnchantmentLevel(entity, stack, ModEnchantments.MANA_EFFICIENCY);

        rate *= Math.pow(ModEnchantmentConfigs.getManaLeakConsumeMultiplier(), leakLevel);
        rate *= Math.pow(ModEnchantmentConfigs.getManaEfficiencyConsumeMultiplier(), efficiencyLevel);
        rate *= getAccessoryManaCostMultiplier(entity);
        return Math.max(0.0D, rate);
    }

    public static float applyMagicDamageBonus(ItemStack stack, LivingEntity entity, float damage) {
        double finalDamage = damage;
        int amplificationLevel = getEnchantmentLevel(entity, stack, ModEnchantments.ARCANE_AMPLIFICATION);

        finalDamage *= Math.pow(ModEnchantmentConfigs.getArcaneAmplificationDamageMultiplier(), amplificationLevel);
        finalDamage *= ManaSicknessEffect.getMagicDamageMultiplier(entity);
        return (float) Math.max(0.0D, finalDamage);
    }

    protected boolean canUseMagic(ItemStack stack, LivingEntity entity) {
        if(entity.hasInfiniteMaterials()) return true;
        if(!isManaEmpty(entity)) return true;
        if(entity instanceof ServerPlayer player) {
            return tryAutoUseManaPotion(player);
        }
        return entity instanceof Player player && canAutoUseManaPotion(player) && ManaPotionUtil.hasManaPotion(player);
    }

    protected boolean isManaEmpty(LivingEntity entity) {
        if(!(entity instanceof Player player)) return false;
        return !player.getData(ModAttachments.PLAYER_MANA).hasMana();
    }

    protected boolean tryConsumeMana(Level level, LivingEntity entity, ItemStack stack, double amount) {
        if(amount <= 0.0D || entity.hasInfiniteMaterials()) return true;
        if(!(entity instanceof ServerPlayer player)) return true;

        PlayerMana mana = player.getData(ModAttachments.PLAYER_MANA);
        int oldMana = mana.getMana();
        boolean canContinue = mana.consumeMana(amount);
        mana.applyRecoverDelay();
        player.setData(ModAttachments.PLAYER_MANA, mana);
        if(mana.getMana() != oldMana) {
            PlayerManaSyncEventHandler.sync(player);
        }
        if(canContinue) return true;
        return tryAutoUseManaPotion(player);
    }

    private static double getAccessoryManaCostMultiplier(LivingEntity entity) {
        double[] multiplier = {1.0D};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(accessory instanceof ManaCostModifier modifier) {
                multiplier[0] *= modifier.getManaCostMultiplier(stack, entity);
            }
        });
        return multiplier[0];
    }

    private static boolean canAutoUseManaPotion(LivingEntity entity) {
        boolean[] result = {false};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(accessory instanceof AutoManaPotionUser autoUser && autoUser.canAutoUseManaPotion(stack, entity)) {
                result[0] = true;
            }
        });
        return result[0];
    }

    private static boolean tryAutoUseManaPotion(ServerPlayer player) {
        if(!canAutoUseManaPotion(player)) return false;
        return ManaPotionUtil.tryUseManaPotion(player);
    }

}
