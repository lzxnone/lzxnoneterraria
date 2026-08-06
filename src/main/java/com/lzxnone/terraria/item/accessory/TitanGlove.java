package com.lzxnone.terraria.item.accessory;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.MeleeKnockbackModifier;
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

public class TitanGlove extends AccessoryItem implements MeleeKnockbackModifier {
    public static final ConfigDouble MELEE_KNOCKBACK = new ConfigDouble(
        "accessory.titan_glove.melee_knockback",
        "melee_knockback",
        0.4D,
        0.0D,
        100.0D
    );

    public TitanGlove() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public double getMeleeKnockbackBonus(ItemStack stack, LivingEntity entity) {
        return MELEE_KNOCKBACK.get();
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = MultimapBuilder.hashKeys().arrayListValues().build();
        double bonus = MELEE_KNOCKBACK.get();
        if(bonus != 0.0D) {
            modifiers.put(
                Attributes.ATTACK_KNOCKBACK,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "titan_glove_knockback"),
                    bonus,
                    AttributeModifier.Operation.ADD_VALUE
                )
            );
        }
        return modifiers;
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(MELEE_KNOCKBACK);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "titan_glove",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/titan_glove.png"),
        Component.translatable("item.lzxnoneterraria.titan_glove"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(MELEE_KNOCKBACK.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.melee_knockback_bonus",
                EffectTooltipUtil.formatNumber(MELEE_KNOCKBACK.get())
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
