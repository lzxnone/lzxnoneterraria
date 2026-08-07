package com.lzxnone.terraria.item.accessory;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.ConditionalAttributeModifier;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.WaterBreathingModifier;
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

public class NeptunesShell extends AccessoryItem implements WaterBreathingModifier, ConditionalAttributeModifier {
    public static final ConfigDouble MOVE_SPEED = new ConfigDouble(
        "accessory.neptunes_shell.move_speed",
        "move_speed",
        1.5D,
        1.0D,
        10.0D
    );

    public NeptunesShell() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public boolean canBreatheUnderwater(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        return getConditionalModifiers(stack, slotContext.entity());
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getConditionalModifiers(ItemStack stack, LivingEntity entity) {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = MultimapBuilder.hashKeys().arrayListValues().build();
        if(entity.isInWater()) buildModifiers(modifiers);
        return modifiers;
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAllConditionalModifiers(ItemStack stack, LivingEntity entity) {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = MultimapBuilder.hashKeys().arrayListValues().build();
        buildModifiers(modifiers);
        return modifiers;
    }

    private void buildModifiers(Multimap<Holder<Attribute>, AttributeModifier> modifiers) {
        double moveSpeed = MOVE_SPEED.get() - 1.0D;
        if(moveSpeed != 0.0D) {
            modifiers.put(
                Attributes.MOVEMENT_SPEED,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "neptunes_shell_move_speed"),
                    moveSpeed,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
            );
        }
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(MOVE_SPEED);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "neptunes_shell",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/neptunes_shell.png"),
        Component.translatable("item.lzxnoneterraria.neptunes_shell"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(
            "tooltip.lzxnoneterraria.active_in_water"
        ).withStyle(ChatFormatting.GREEN));
        if(MOVE_SPEED.get() != 1.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.move_speed_bonus",
                EffectTooltipUtil.formatPercent(MOVE_SPEED.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        tooltipComponents.add(Component.translatable(
            "tooltip.lzxnoneterraria.water_breathing"
        ).withStyle(ChatFormatting.GRAY));
    }
}
