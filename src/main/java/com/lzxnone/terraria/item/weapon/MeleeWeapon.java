package com.lzxnone.terraria.item.weapon;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.accessory.AccessoryUtil;
import com.lzxnone.terraria.item.effect.MeleeDamageModifier;
import com.lzxnone.terraria.item.effect.MeleeKnockbackModifier;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.DamageUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public class MeleeWeapon extends Weapon {
    private static final float BASE_MELEE_DAMAGE_MIN = 0.0F;
    private static final float BASE_MELEE_DAMAGE_MAX = 8388600.0F;
    private static final float BASE_MELEE_ATTACK_SPEED_MIN = -100.0F;
    private static final float BASE_MELEE_ATTACK_SPEED_MAX = 100.0F;
    private static final ResourceLocation BASE_MELEE_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_melee_damage");
    private static final ResourceLocation BASE_MELEE_ATTACK_SPEED_ID = ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_melee_attack_speed");

    public MeleeWeapon(Tier tier, Properties properties) {
        super(tier, properties);
    }

    public MeleeWeapon(Properties properties) {
        super(properties);
    }

    public static ConfigFloat createBaseMeleeDamageConfig(String id, float damageDefault) {
        return new ConfigFloat(baseMeleeDamagePath(id), "base_melee_damage", damageDefault, BASE_MELEE_DAMAGE_MIN, BASE_MELEE_DAMAGE_MAX);
    }

    public static ConfigFloat createBaseMeleeAttackSpeedConfig(String id, float attackSpeedDefault) {
        return new ConfigFloat(baseMeleeAttackSpeedPath(id), "base_melee_attack_speed", attackSpeedDefault, BASE_MELEE_ATTACK_SPEED_MIN, BASE_MELEE_ATTACK_SPEED_MAX);
    }

    public static IConfigData createConfigData(ConfigStruct... configs) {
        return new IConfigData() {
            @Override
            public void onConfigLoad() {
                ConfigStruct.loadAll(configs);
            }
        };
    }

    public static ConfigListItem createConfigListItem(String id, IConfigData configData) {
        return new ConfigListItem(
            id,
            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/" + id + ".png"),
            Component.translatable("item.lzxnoneterraria." + id),
            configData
        );
    }

    private static String baseMeleeDamagePath(String id) {
        return "weapon." + id + ".base_melee_damage";
    }

    private static String baseMeleeAttackSpeedPath(String id) {
        return "weapon." + id + ".base_melee_attack_speed";
    }

    protected float getBaseMeleeDamage(ItemStack stack) {
        return 0.0F;
    }

    protected float getBaseMeleeAttackSpeed(ItemStack stack) {
        return -2.4F;
    }

    public float getTooltipDamage(ItemStack weaponStack, LivingEntity entity) {
        float damage = getBaseMeleeDamage(weaponStack);
        //锋利附魔
        int sharpnessLevel = getEnchantmentLevel(entity, weaponStack, Enchantments.SHARPNESS);
        if(sharpnessLevel > 0) {
            damage += 1.0F + Math.max(0, sharpnessLevel - 1) * 0.5F;
        }
        //药水
        if(entity instanceof Player player) damage = DamageUtil.applyPlayerDamageEffects(player, damage);
        //近战加成
        return applyMeleeDamageBonus(weaponStack, entity, damage);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(BASE_MELEE_DAMAGE_ID, getBaseMeleeDamage(stack), AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(BASE_MELEE_ATTACK_SPEED_ID, getBaseMeleeAttackSpeed(stack), AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build();
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return itemAbility == ItemAbilities.SWORD_SWEEP || super.canPerformAction(stack, itemAbility);
    }

    public static float applyMeleeDamageBonus(ItemStack stack, LivingEntity entity, float damage) {        double finalDamage = damage;
        //饰品
        double[] multiplier = {1.0D};
        AccessoryUtil.forEachAccessory(entity, (accessory, accessoryStack) -> {
            if(accessory instanceof MeleeDamageModifier modifier) {
                multiplier[0] *= modifier.getMeleeDamageMultiplier(accessoryStack, entity);
            }
        });
        finalDamage *= multiplier[0];
        return (float)Math.max(0.0D, finalDamage);
    }

    public static float applyMeleeKnockbackBonus(ItemStack stack, LivingEntity entity, float knockback) {
        //饰品
        double[] bonus = {0.0D};
        AccessoryUtil.forEachAccessory(entity, (accessory, accessoryStack) -> {
            if(accessory instanceof MeleeKnockbackModifier modifier) {
                bonus[0] += modifier.getMeleeKnockbackBonus(accessoryStack, entity);
            }
        });
        return knockback + (float) bonus[0];
    }
}
