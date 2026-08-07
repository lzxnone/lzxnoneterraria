package com.lzxnone.terraria.item.accessory;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.ConditionalAttributeModifier;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.LifeRegenerationModifier;
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

import java.util.List;

public class ValentineRing extends AccessoryItem implements LifeRegenerationModifier, ConditionalAttributeModifier {
    //每秒生命回复量
    public static final ConfigDouble LIFE_REGEN = new ConfigDouble(
        "accessory.valentine_ring.life_regen",
        "health_per_second",
        0.25D,
        0.0D,
        1000.0D
    );
    //跳跃高度倍率（同狼人效果，JUMP_STRENGTH 属性）
    public static final ConfigDouble JUMP_HEIGHT = new ConfigDouble(
        "accessory.valentine_ring.jump_height",
        "jump_height",
        1.2D,
        1.0D,
        10.0D
    );

    public ValentineRing() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public float getHealthPerSecond(ItemStack stack, LivingEntity entity) {
        return LIFE_REGEN.get().floatValue();
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getConditionalModifiers(ItemStack stack, LivingEntity entity) {
        return buildModifiers();
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAllConditionalModifiers(ItemStack stack, LivingEntity entity) {
        return buildModifiers();
    }

    //跳跃高度：全天候生效（无条件属性）
    private Multimap<Holder<Attribute>, AttributeModifier> buildModifiers() {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = MultimapBuilder.hashKeys().arrayListValues().build();
        double jumpStrength = JUMP_HEIGHT.get() - 1.0D;
        if(jumpStrength != 0.0D) {
            modifiers.put(
                Attributes.JUMP_STRENGTH,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "valentine_ring_jump_height"),
                    jumpStrength,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
            );
        }
        return modifiers;
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(LIFE_REGEN, JUMP_HEIGHT);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "valentine_ring",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/valentine_ring.png"),
        Component.translatable("item.lzxnoneterraria.valentine_ring"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(LIFE_REGEN.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.life_regen",
                EffectTooltipUtil.formatNumber(LIFE_REGEN.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        if(JUMP_HEIGHT.get() > 1.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.jump_boost"
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
