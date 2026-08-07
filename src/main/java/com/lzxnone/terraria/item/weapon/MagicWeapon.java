package com.lzxnone.terraria.item.weapon;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.attachment.PlayerMana;
import com.lzxnone.terraria.enchantment.ModEnchantments;
import com.lzxnone.terraria.enchantment.ModEnchantmentConfigs;
import com.lzxnone.terraria.event.PlayerManaSyncEventHandler;
import com.lzxnone.terraria.effect.ManaSicknessEffect;
import com.lzxnone.terraria.item.accessory.AccessoryUtil;
import com.lzxnone.terraria.item.effect.AutoManaPotionUser;
import com.lzxnone.terraria.item.effect.MagicDamageModifier;
import com.lzxnone.terraria.item.effect.ManaCostModifier;
import com.lzxnone.terraria.item.effect.SummonDamageModifier;
import com.lzxnone.terraria.item.potion.AbstractManaPotion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
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

    //使用魔法武器时
    protected void onMagicUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {}

    //提供基础魔力消耗
    protected double getManaConsumeRate(ItemStack stack, LivingEntity entity) { return 0.0D; }

    //获取最终魔力消耗
    protected double getFinalManaConsumeRate(ItemStack stack, LivingEntity entity) {
        double rate = getManaConsumeRate(stack, entity);
        //魔力泄漏
        int leakLevel = getEnchantmentLevel(entity, stack, ModEnchantments.MANA_LEAK);
        rate *= Math.pow(ModEnchantmentConfigs.getManaLeakConsumeMultiplier(), leakLevel);
        //魔力效率
        int efficiencyLevel = getEnchantmentLevel(entity, stack, ModEnchantments.MANA_EFFICIENCY);
        rate *= Math.pow(ModEnchantmentConfigs.getManaEfficiencyConsumeMultiplier(), efficiencyLevel);
        //饰品
        double[] multiplier = {1.0D};
        AccessoryUtil.forEachAccessory(entity, (accessory, itemStack) -> {
            if(accessory instanceof ManaCostModifier modifier) {
                multiplier[0] *= modifier.getManaCostMultiplier(itemStack, entity);
            }
        });
        rate *= multiplier[0];
        return Math.max(0.0D, rate);
    }

    //获取最终造成的伤害
    public static float applyMagicDamageBonus(ItemStack stack, LivingEntity entity, float damage) {
        double finalDamage = damage;
        //奥术增幅
        int amplificationLevel = getEnchantmentLevel(entity, stack, ModEnchantments.ARCANE_AMPLIFICATION);
        finalDamage *= Math.pow(ModEnchantmentConfigs.getArcaneAmplificationDamageMultiplier(), amplificationLevel);
        //耐魔性
        finalDamage *= ManaSicknessEffect.getMagicDamageMultiplier(entity);
        //饰品
        double[] multiplier = {1.0D};
        AccessoryUtil.forEachAccessory(entity, (accessory, accessoryStack) -> {
            if(accessory instanceof MagicDamageModifier modifier) {
                multiplier[0] *= modifier.getMagicDamageMultiplier(accessoryStack, entity);
            }
        });
        finalDamage *= multiplier[0];
        return (float) Math.max(0.0D, finalDamage);
    }

    //是否可以继续使用魔法武器
    protected boolean canUseMagic(ItemStack stack, LivingEntity entity) {
        if(entity.hasInfiniteMaterials()) return true;
        if(entity instanceof Player player) {
            if(player.getData(ModAttachments.PLAYER_MANA).hasMana()) return true;
            if(player instanceof ServerPlayer serverPlayer) return tryAutoUseManaPotion(serverPlayer);
            return canAutoUseManaPotion(player) && findManaPotionSlot(player) >= 0;
        }
        return false;
    }

    //进行魔力消耗
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

    private static int findManaPotionSlot(Player player) {
        for(int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if(stack.getItem() instanceof AbstractManaPotion potion) {
                if(potion.getRecoverAmount() > 0) return i;
            }
        }
        return -1;
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
        int slot = findManaPotionSlot(player);
        if(slot < 0) return false;
        ItemStack stack = player.getInventory().getItem(slot);
        if(!(stack.getItem() instanceof AbstractManaPotion potion)) return false;
        return potion.tryDrink(player, stack);
    }

}
