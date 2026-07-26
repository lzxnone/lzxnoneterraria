package com.lzxnone.terraria.item.weapon;

import com.lzxnone.terraria.enchantment.ModEnchantments;
import com.lzxnone.terraria.enchantment.ModEnchantmentConfigs;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import java.util.function.IntSupplier;

public class MagicWeapon extends Weapon {
    private static final String MANA_CONSUME_PROGRESS_KEY = "magicManaConsumeProgress";
    private static final String MANA_RECOVER_PROGRESS_KEY = "magicManaRecoverProgress";

    private final IntSupplier manaConsumeRate;
    private final IntSupplier manaRecoverRate;

    public MagicWeapon(Tier tier, Properties properties, int manaConsumeRate, int manaRecoverRate) {
        this(tier, properties, () -> manaConsumeRate, () -> manaRecoverRate);
    }

    public MagicWeapon(Tier tier, Properties properties, IntSupplier manaConsumeRate, IntSupplier manaRecoverRate) {
        super(tier, properties);
        this.manaConsumeRate = manaConsumeRate;
        this.manaRecoverRate = manaRecoverRate;
    }

    public MagicWeapon(Properties properties) {
        super(properties);
        this.manaConsumeRate = () -> 1;
        this.manaRecoverRate = () -> 1;
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

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if(level.isClientSide() || !(entity instanceof LivingEntity livingEntity)) return;
        if(livingEntity.getUseItem() == stack) return;

        recoverMana(stack, getFinalManaRecoverRate(stack, livingEntity));
    }

    protected void onMagicUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
    }

    protected int getManaConsumeRate(ItemStack stack, LivingEntity entity) {
        return Math.max(0, manaConsumeRate.getAsInt());
    }

    protected int getManaRecoverRate(ItemStack stack, LivingEntity entity) {
        return Math.max(0, manaRecoverRate.getAsInt());
    }

    protected double getFinalManaConsumeRate(ItemStack stack, LivingEntity entity) {
        double rate = getManaConsumeRate(stack, entity);
        int leakLevel = getEnchantmentLevel(entity, stack, ModEnchantments.MANA_LEAK);
        int efficiencyLevel = getEnchantmentLevel(entity, stack, ModEnchantments.MANA_EFFICIENCY);

        rate *= Math.pow(ModEnchantmentConfigs.getManaLeakConsumeMultiplier(), leakLevel);
        rate *= Math.pow(ModEnchantmentConfigs.getManaEfficiencyConsumeMultiplier(), efficiencyLevel);
        return Math.max(0.0D, rate);
    }

    protected double getFinalManaRecoverRate(ItemStack stack, LivingEntity entity) {
        double rate = getManaRecoverRate(stack, entity);
        int gatheringLevel = getEnchantmentLevel(entity, stack, ModEnchantments.MANA_GATHERING);
        int curseLevel = getEnchantmentLevel(entity, stack, ModEnchantments.MANA_GATHERING_CURSE);

        rate *= Math.pow(ModEnchantmentConfigs.getManaGatheringRecoverMultiplier(), gatheringLevel);
        rate *= Math.pow(ModEnchantmentConfigs.getManaGatheringCurseRecoverMultiplier(), curseLevel);
        return Math.max(0.0D, rate);
    }

    public static float applyMagicDamageBonus(ItemStack stack, LivingEntity entity, float damage) {
        double finalDamage = damage;
        int amplificationLevel = getEnchantmentLevel(entity, stack, ModEnchantments.ARCANE_AMPLIFICATION);

        finalDamage *= Math.pow(ModEnchantmentConfigs.getArcaneAmplificationDamageMultiplier(), amplificationLevel);
        return (float) Math.max(0.0D, finalDamage);
    }

    protected boolean canUseMagic(ItemStack stack, LivingEntity entity) {
        return !isManaEmpty(stack);
    }

    protected boolean isManaEmpty(ItemStack stack) {
        return stack.isDamageableItem() && stack.getDamageValue() >= stack.getMaxDamage();
    }

    protected boolean tryConsumeMana(Level level, LivingEntity entity, ItemStack stack, double amount) {
        if(amount <= 0.0D || entity.hasInfiniteMaterials()) return true;
        if(!stack.isDamageableItem() || isManaEmpty(stack)) return false;

        int pendingDamage = addManaProgress(stack, MANA_CONSUME_PROGRESS_KEY, amount);
        if(pendingDamage <= 0) return true;

        int damage = pendingDamage;
        if(level instanceof ServerLevel serverLevel) {
            damage = EnchantmentHelper.processDurabilityChange(serverLevel, stack, damage);
        }
        if(damage <= 0) return true;

        int nextDamage = Math.min(stack.getDamageValue() + damage, stack.getMaxDamage());
        stack.setDamageValue(nextDamage);
        return nextDamage < stack.getMaxDamage();
    }

    protected void recoverMana(ItemStack stack, double amount) {
        if(amount <= 0.0D || !stack.isDamageableItem() || stack.getDamageValue() <= 0) return;
        int recoverAmount = addManaProgress(stack, MANA_RECOVER_PROGRESS_KEY, amount);
        if(recoverAmount <= 0) return;

        stack.setDamageValue(Math.max(0, stack.getDamageValue() - recoverAmount));
    }

    private int addManaProgress(ItemStack stack, String key, double amount) {
        double progress = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
            .copyTag()
            .getDouble(key) + amount;
        int wholeAmount = (int) Math.floor(progress);
        double nextProgress = progress - wholeAmount;
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putDouble(key, nextProgress));
        return wholeAmount;
    }

    private static int getEnchantmentLevel(LivingEntity entity, ItemStack stack, ResourceKey<Enchantment> enchantment) {
        return entity.registryAccess()
            .lookupOrThrow(Registries.ENCHANTMENT)
            .get(enchantment)
            .map(stack::getEnchantmentLevel)
            .orElse(0);
    }

}
