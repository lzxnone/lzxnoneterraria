package com.lzxnone.terraria.item.accessory;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class ArmorPolish extends AccessoryItem {
    public static final ConfigDouble ARMOR_TOUGHNESS = new ConfigDouble(
        "accessory.armor_polish.armor_toughness",
        "armor_toughness",
        2.0D,
        0.0D,
        100.0D
    );

    public ArmorPolish() {
        super(new Item.Properties().stacksTo(1));
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
        "armor_polish",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/armor_polish.png"),
        Component.translatable("item.lzxnoneterraria.armor_polish"),
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
    }

}
