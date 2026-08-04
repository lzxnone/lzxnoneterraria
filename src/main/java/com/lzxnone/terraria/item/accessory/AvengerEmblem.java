package com.lzxnone.terraria.item.accessory;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.DamageModifier;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
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

public class AvengerEmblem extends AccessoryItem implements DamageModifier {
    public static final double DAMAGE_MULTIPLIER = 1.12D;

    public AvengerEmblem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public double getDamageMultiplier(ItemStack stack, LivingEntity entity) {
        return DAMAGE_MULTIPLIER;
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = MultimapBuilder.hashKeys().arrayListValues().build();
        double bonus = DAMAGE_MULTIPLIER - 1.0D;
        if(bonus != 0.0D) {
            modifiers.put(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "avenger_emblem_attack_bonus"),
                    bonus,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
            );
        }
        return modifiers;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(
            "tooltip.lzxnoneterraria.damage_increase",
            EffectTooltipUtil.formatPercent(DAMAGE_MULTIPLIER)
        ).withStyle(ChatFormatting.GRAY));
    }
}
