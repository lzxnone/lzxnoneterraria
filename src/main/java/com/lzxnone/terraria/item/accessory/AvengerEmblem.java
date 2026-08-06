package com.lzxnone.terraria.item.accessory;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.DamageModifier;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
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

public class AvengerEmblem extends AccessoryItem implements DamageModifier {
    public static final ConfigDouble DAMAGE_MULTIPLIER = new ConfigDouble(
        "accessory.avenger_emblem.damage_multiplier",
        "damage_multiplier",
        1.12D,
        1.0D,
        10.0D
    );

    public AvengerEmblem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public double getDamageMultiplier(ItemStack stack, LivingEntity entity) {
        return DAMAGE_MULTIPLIER.get();
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = MultimapBuilder.hashKeys().arrayListValues().build();
        double bonus = DAMAGE_MULTIPLIER.get() - 1.0D;
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

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DAMAGE_MULTIPLIER);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "avenger_emblem",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/avenger_emblem.png"),
        Component.translatable("item.lzxnoneterraria.avenger_emblem"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(DAMAGE_MULTIPLIER.get() != 1.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.damage_increase",
                EffectTooltipUtil.formatPercent(DAMAGE_MULTIPLIER.get())
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
