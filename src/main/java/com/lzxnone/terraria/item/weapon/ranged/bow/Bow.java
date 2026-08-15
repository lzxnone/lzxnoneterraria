package com.lzxnone.terraria.item.weapon.ranged.bow;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.ModItemTags;
import com.lzxnone.terraria.item.weapon.RangedWeapon;
import com.lzxnone.terraria.utils.AmmoUtil;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

public class Bow extends RangedWeapon {
    public static final Vector3f OFFSET = new Vector3f(-0.3f, -0.15f, 1.5f);
    public static final int USE_TIME = 20;
    public static final int FLAME_ARROW_FIRE_TICKS = 20 * 60 * 60;
    public static final int FLAME_TARGET_IGNITE_TICKS = 100;

    public Bow(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public boolean canUseAmmo(ItemStack weaponStack, ItemStack ammoStack) {
        return ammoStack.is(ModItemTags.ARROW_AMMO);
    }

    @Override
    protected ResourceLocation getDefaultAmmo(ItemStack weaponStack) {
        return ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "wooden_arrow");
    }

    @Override
    public int getUseTime(ItemStack weaponStack, LivingEntity entity) {
        return USE_TIME;
    }

    @Override
    public @NonNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public boolean consumeAmmo(ItemStack weaponStack, Player player, int amount) {
        if(hasInfinity(weaponStack, player) && getAmmoCount(weaponStack, player) > 0) return true;
        return super.consumeAmmo(weaponStack, player, amount);
    }

    @Override
    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack weaponStack) {
        SoundUtil.playClientSound(player, ModSounds.BOW_SHOOT.get());
        if(level.isClientSide()) return;

        StaticSummon summon = createArrowSummon(level, player, hand, weaponStack);
        level.addFreshEntity(summon);
    }

    protected float getDamage(ItemStack weaponStack, LivingEntity entity, ItemStack ammoStack) {
        return 0;
    }

    @Override
    public float getTooltipDamage(ItemStack weaponStack, LivingEntity entity) {
        ItemStack ammoStack = getTooltipAmmoStack(weaponStack, entity);
        float damage = getDamage(weaponStack, entity, ammoStack) + getAmmoBaseDamage(ammoStack);
        if(entity instanceof Player player) damage = DamageUtil.applyPlayerDamageEffects(player, damage);
        return applyRangedDamageBonus(weaponStack, entity, damage);
    }

    protected ItemStack getTooltipAmmoStack(ItemStack weaponStack, LivingEntity entity) {
        return getAmmoStack(weaponStack);
    }

    protected float getKnockbackScale(ItemStack weaponStack, LivingEntity entity, ItemStack ammoStack) {
        return 1.0f;
    }

    protected int getInvulnerableTime(ItemStack weaponStack, LivingEntity entity, ItemStack ammoStack) {
        return 10;
    }

    protected boolean hasInfinity(ItemStack weaponStack, LivingEntity entity) {
        return getEnchantmentLevel(entity, weaponStack, Enchantments.INFINITY) > 0;
    }

    protected StaticSummon createArrowSummon(Level level, Player player, InteractionHand hand, ItemStack weaponStack) {
        StaticSummon summon = AmmoUtil.createAmmoSummon(level, player, hand, weaponStack, OFFSET);
        ItemStack ammoStack = getAmmoStack(weaponStack);
        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA).copy();
        customData.putFloat("damage", getDamage(weaponStack, player, ammoStack));
        customData.putFloat("knockbackScale", getKnockbackScale(weaponStack, player, ammoStack));
        customData.putInt("invulnerableTime", getInvulnerableTime(weaponStack, player, ammoStack));
        if(getEnchantmentLevel(player, weaponStack, Enchantments.FLAME) > 0) {
            customData.putInt(StaticSummon.HIT_TARGET_IGNITE_TICKS_KEY, FLAME_TARGET_IGNITE_TICKS);
            summon.igniteForTicks(FLAME_ARROW_FIRE_TICKS);
        }
        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
        return summon;
    }
}
