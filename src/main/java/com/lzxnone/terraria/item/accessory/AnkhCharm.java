package com.lzxnone.terraria.item.accessory;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
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

public class AnkhCharm extends AccessoryItem implements MobEffectImmunityModifier {
    public static final ConfigDouble ARMOR_TOUGHNESS = new ConfigDouble(
        "accessory.ankh_charm.armor_toughness",
        "ankh_charm_armor_toughness",
        2.0D,
        0.0D,
        100.0D
    );

    public AnkhCharm() {
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
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = MultimapBuilder.hashKeys().arrayListValues().build();
        double toughness = ARMOR_TOUGHNESS.get();
        if(toughness != 0.0D) {
            modifiers.put(
                Attributes.ARMOR_TOUGHNESS,
                new AttributeModifier(id, toughness, AttributeModifier.Operation.ADD_VALUE)
            );
        }
        return modifiers;
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(ARMOR_TOUGHNESS);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "ankh_charm",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/ankh_charm.png"),
        Component.translatable("item.lzxnoneterraria.ankh_charm"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(ARMOR_TOUGHNESS.get() != 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.armor_toughness_bonus",
                EffectTooltipUtil.formatNumber(ARMOR_TOUGHNESS.get())
            ).withStyle(ChatFormatting.GRAY));
        }
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
