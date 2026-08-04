package com.lzxnone.terraria.item.accessory;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.FireBlockImmunityModifier;
import com.lzxnone.terraria.item.effect.MobEffectImmunityModifier;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class AnkhShield extends AccessoryItem implements FireBlockImmunityModifier, MobEffectImmunityModifier {
    public static final ConfigDouble KNOCKBACK_RESISTANCE = new ConfigDouble(
        "accessory.ankh_shield.knockback_resistance",
        "knockback_resistance",
        1.0D,
        0.0D,
        1.0D
    );
    public static final ConfigDouble DEFENSE = new ConfigDouble(
        "accessory.ankh_shield.defense",
        "defense",
        4.0D,
        0.0D,
        100.0D
    );
    public static final ConfigDouble ARMOR_TOUGHNESS = new ConfigDouble(
        "accessory.ankh_shield.armor_toughness",
        "armor_toughness",
        2.0D,
        0.0D,
        100.0D
    );

    public AnkhShield() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public boolean isPoisonImmunity(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean isWitherImmunity(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean isDarknessImmunity(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean isSlownessImmunity(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean isWeaknessImmunity(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean isBleedImmunity(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean isConfusedImmunity(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean isLevitationImmunity(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean isMiningFatigueImmunity(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean isFireBlockImmunity(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = MultimapBuilder.hashKeys().arrayListValues().build();
        double knockback = KNOCKBACK_RESISTANCE.get();
        if(knockback != 0.0D) {
            modifiers.put(
                Attributes.KNOCKBACK_RESISTANCE,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "ankh_shield_knockback_resistance"),
                    knockback,
                    AttributeModifier.Operation.ADD_VALUE
                )
            );
        }
        double defense = DEFENSE.get();
        if(defense != 0.0D) {
            modifiers.put(
                Attributes.ARMOR,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "ankh_shield_defense"),
                    defense,
                    AttributeModifier.Operation.ADD_VALUE
                )
            );
        }
        double toughness = ARMOR_TOUGHNESS.get();
        if(toughness != 0.0D) {
            modifiers.put(
                Attributes.ARMOR_TOUGHNESS,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "ankh_shield_armor_toughness"),
                    toughness,
                    AttributeModifier.Operation.ADD_VALUE
                )
            );
        }
        return modifiers;
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(KNOCKBACK_RESISTANCE, DEFENSE, ARMOR_TOUGHNESS);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "ankh_shield",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/ankh_shield.png"),
        Component.translatable("item.lzxnoneterraria.ankh_shield"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(KNOCKBACK_RESISTANCE.get() != 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.knockback_resistance_bonus",
                EffectTooltipUtil.formatNumber(KNOCKBACK_RESISTANCE.get() * 100.0D)
            ).withStyle(ChatFormatting.GRAY));
        }
        if(DEFENSE.get() != 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.defense_bonus",
                EffectTooltipUtil.formatNumber(DEFENSE.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        if(ARMOR_TOUGHNESS.get() != 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.armor_toughness_bonus",
                EffectTooltipUtil.formatNumber(ARMOR_TOUGHNESS.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        tooltipComponents.add(Component.translatable("tooltip.lzxnoneterraria.fire_block_immunity").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.lzxnoneterraria.poison_immunity").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.lzxnoneterraria.bleed_immunity").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.lzxnoneterraria.slowness_immunity").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.lzxnoneterraria.confused_immunity").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.lzxnoneterraria.wither_immunity").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.lzxnoneterraria.mining_fatigue_immunity").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.lzxnoneterraria.darkness_immunity").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.lzxnoneterraria.levitation_immunity").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.lzxnoneterraria.weakness_immunity").withStyle(ChatFormatting.GRAY));
    }
}
