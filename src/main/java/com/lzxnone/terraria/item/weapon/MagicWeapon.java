package com.lzxnone.terraria.item.weapon;

import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.attachment.PlayerMana;
import com.lzxnone.terraria.enchantment.ModEnchantments;
import com.lzxnone.terraria.enchantment.ModEnchantmentConfigs;
import com.lzxnone.terraria.effect.ManaSicknessEffect;
import com.lzxnone.terraria.item.accessory.AccessoryUtil;
import com.lzxnone.terraria.item.effect.AutoManaPotionUser;
import com.lzxnone.terraria.item.effect.MagicDamageModifier;
import com.lzxnone.terraria.item.effect.ManaCostModifier;
import com.lzxnone.terraria.item.potion.AbstractManaPotion;
import com.lzxnone.terraria.utils.DamageUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class MagicWeapon extends Weapon {
    public MagicWeapon(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(player.getCooldowns().isOnCooldown(stack.getItem())) return InteractionResultHolder.fail(stack);
        player.startUsingItem(hand);

        if(level.isClientSide()) return InteractionResultHolder.consume(stack);
        if(!tryShoot(level, player, hand, stack)) return InteractionResultHolder.fail(stack);
        player.getCooldowns().addCooldown(stack.getItem(), Math.max(1, getUseTime(stack, player) / 3));
        return InteractionResultHolder.consume(stack);
    }

    public boolean shouldShootThisTick(ItemStack weaponStack, LivingEntity entity, int remainingUseTicks) {
        int useTime = Math.max(1, getUseTime(weaponStack, entity));
        int elapsedMinecraftTicks = getUseDuration(weaponStack, entity) - remainingUseTicks;
        if(elapsedMinecraftTicks <= 0) return false;

        int currentShot = elapsedMinecraftTicks * 3 / useTime;
        int previousShot = (elapsedMinecraftTicks - 1) * 3 / useTime;
        return currentShot > previousShot;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
        if(level.isClientSide()) return;
        if(!(entity instanceof Player player)) return;
        if(!shouldShootThisTick(stack, entity, count)) return;

        if(!tryShoot(level, player, player.getUsedItemHand(), stack)) {
            player.stopUsingItem();
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    public float getTooltipDamage(ItemStack weaponStack, LivingEntity entity) {
        float damage = entity instanceof Player player ? DamageUtil.applyPlayerDamageEffects(player, 0.0F) : 0.0F;
        return applyMagicDamageBonus(weaponStack, entity, damage);
    }

    @Override
    public @NonNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    public int getUseTime(ItemStack weaponStack, LivingEntity entity) {
        return computeUseTime(1, weaponStack, entity);
    }

    public int computeUseTime(int useTime, ItemStack weaponStack, LivingEntity entity) {
        if(entity instanceof Player player) {
            double manaConsume = getFinalManaConsumeRate(weaponStack, player);
            if(!testConsumeMana(player, manaConsume)) {
                return (int) Math.ceil(useTime / 0.4D);
            }
        }
        return useTime;
    }

    public boolean tryShoot(Level level, Player player, InteractionHand hand, ItemStack weaponStack) {
        double manaConsume = getFinalManaConsumeRate(weaponStack, player);
        tryConsumeMana(player, manaConsume);
        //if(!tryConsumeMana(player, manaConsume)) return false;

        shoot(level, player, hand, weaponStack);
        return true;
    }

    //测试魔力是否充足（类比 tryConsumeMana）
    public boolean testConsumeMana(Player player, double amount) {
        if(amount <= 0.0D || player.hasInfiniteMaterials()) return true;
        if(PlayerMana.testMana(player, amount)) return true;

        PlayerMana mana = player.getData(ModAttachments.PLAYER_MANA);
        double targetMana = Math.max(1.0D, Math.floor(mana.getConsumeProgress() + amount));
        return canAutoUseManaPotionToReachTarget(player, targetMana);
    }

    //尝试消耗魔力
    protected boolean tryConsumeMana(Player player, double amount) {
        //创造模式
        if(amount <= 0.0D || player.hasInfiniteMaterials()) return true;
        if(!(player instanceof ServerPlayer serverPlayer)) return false;
        //直接消耗
        if(PlayerMana.consumeMana(serverPlayer, amount)) return true;

        //自动喝药
        PlayerMana mana = player.getData(ModAttachments.PLAYER_MANA);
        double targetMana = Math.max(1.0D, Math.floor(mana.getConsumeProgress() + amount));
        if(!tryAutoUseManaPotionToReachTarget(player, targetMana)) return false;
        return PlayerMana.consumeMana(serverPlayer, amount);
    }


    //使用魔法武器时
    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack stack) {}

    //提供基础魔力消耗
    protected double getManaConsumeRate(ItemStack stack, LivingEntity entity) { return 0.0D; }

    protected double getManaTooltipValue(ItemStack stack) {
        return getManaConsumeRate(stack, null);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        double mana = getManaTooltipValue(stack);
        if(mana > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.magic_weapon_mana",
                mana == Math.rint(mana) ? String.valueOf((int) mana) : String.format("%.2f", mana)
            ).withStyle(ChatFormatting.BLUE));
        }
    }

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

    protected static boolean canAutoUseManaPotionToReachTarget(Player player, double targetMana) {
        PlayerMana mana = player.getData(ModAttachments.PLAYER_MANA);
        if(targetMana <= 0.0D || mana.getMana() >= targetMana) return true;
        if(mana.getMaxMana() < targetMana) return false;

        boolean[] result = {false};
        AccessoryUtil.forEachAccessory(player, (accessory, stack) -> {
            if(accessory instanceof AutoManaPotionUser autoUser && autoUser.canAutoUseManaPotion(stack, player)) {
                result[0] = true;
            }
        });
        if(!result[0]) return false;

        int simulatedMana = mana.getMana();
        for(int i = 0; i < player.getInventory().getContainerSize() && simulatedMana < targetMana; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if(!(stack.getItem() instanceof AbstractManaPotion potion)) continue;
            int recoverAmount = potion.getRecoverAmount();
            if(recoverAmount <= 0) continue;
            for(int j = 0; j < stack.getCount() && simulatedMana < targetMana; j++) {
                simulatedMana = Math.min(mana.getMaxMana(), simulatedMana + recoverAmount);
            }
        }
        return simulatedMana >= targetMana;
    }

    protected static boolean tryAutoUseManaPotionToReachTarget(Player player, double targetMana) {
        if(!canAutoUseManaPotionToReachTarget(player, targetMana)) return false;
        if(!(player instanceof ServerPlayer serverPlayer)) return true;

        while(serverPlayer.getData(ModAttachments.PLAYER_MANA).getMana() < targetMana) {
            PlayerMana mana = serverPlayer.getData(ModAttachments.PLAYER_MANA);
            int bestSlot = -1;
            int bestRecoverAmount = 0;
            int smallestSufficientRecoverAmount = Integer.MAX_VALUE;
            for(int i = 0; i < serverPlayer.getInventory().getContainerSize(); i++) {
                ItemStack stack = serverPlayer.getInventory().getItem(i);
                if(!(stack.getItem() instanceof AbstractManaPotion potion)) continue;
                int recoverAmount = potion.getRecoverAmount();
                if(recoverAmount <= 0) continue;
                int manaAfterDrink = Math.min(mana.getMaxMana(), mana.getMana() + recoverAmount);
                if(manaAfterDrink <= mana.getMana()) continue;
                if(manaAfterDrink >= targetMana) {
                    if(recoverAmount < smallestSufficientRecoverAmount) {
                        bestSlot = i;
                        bestRecoverAmount = recoverAmount;
                        smallestSufficientRecoverAmount = recoverAmount;
                    }
                }else if(smallestSufficientRecoverAmount == Integer.MAX_VALUE && recoverAmount > bestRecoverAmount) {
                    bestSlot = i;
                    bestRecoverAmount = recoverAmount;
                }
            }
            if(bestSlot < 0) return false;

            ItemStack stack = serverPlayer.getInventory().getItem(bestSlot);
            if(!(stack.getItem() instanceof AbstractManaPotion potion)) return false;
            int oldMana = mana.getMana();
            if(!potion.tryDrink(serverPlayer, stack)) return false;
            if(serverPlayer.getData(ModAttachments.PLAYER_MANA).getMana() <= oldMana) return false;
        }
        return true;
    }

}
