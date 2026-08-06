package com.lzxnone.terraria.item.accessory;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.effect.IceBarrierEffect;
import com.lzxnone.terraria.effect.PaladinsShieldEffect;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.GuardianShieldModifier;
import com.lzxnone.terraria.item.effect.IceBarrierModifier;
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

public class FrozenShield extends AccessoryItem implements GuardianShieldModifier, IceBarrierModifier {
    public static final ConfigDouble MIN_HEALTH_RATIO = new ConfigDouble(
        "accessory.frozen_shield.min_health_ratio",
        "min_health_ratio",
        0.25D,
        0.0D,
        1.0D
    );
    public static final ConfigDouble MAX_HEALTH_RATIO = new ConfigDouble(
        "accessory.frozen_shield.max_health_ratio",
        "max_health_ratio",
        0.5D,
        0.0D,
        1.0D
    );
    public static final ConfigDouble DEFENSE = new ConfigDouble(
        "accessory.frozen_shield.defense",
        "defense",
        6.0D,
        0.0D,
        100.0D
    );
    public static final ConfigDouble KNOCKBACK_RESISTANCE = new ConfigDouble(
        "accessory.frozen_shield.knockback_resistance",
        "knockback_resistance",
        1.0D,
        0.0D,
        1.0D
    );
    public static final ConfigDouble GUARDIAN_RANGE = new ConfigDouble(
        "accessory.frozen_shield.guardian_range",
        "paladins_shield_range",
        32.0D,
        0.0D,
        128.0D
    );

    public FrozenShield() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public boolean applyGuardianShield(ItemStack stack, LivingEntity entity) {
        return entity.getHealth() / entity.getMaxHealth() > MIN_HEALTH_RATIO.get();
    }

    @Override
    public double getGuardianRange(ItemStack stack, LivingEntity entity) {
        return GUARDIAN_RANGE.get();
    }

    @Override
    public double getMinHealthRatio(ItemStack stack, LivingEntity entity) {
        return MIN_HEALTH_RATIO.get();
    }

    @Override
    public boolean applyIceBarrier(ItemStack stack, LivingEntity entity) {
        return entity.getHealth() / entity.getMaxHealth() < MAX_HEALTH_RATIO.get();
    }

    @Override
    public double getMaxHealthRatio(ItemStack stack, LivingEntity entity) {
        return MAX_HEALTH_RATIO.get();
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = MultimapBuilder.hashKeys().arrayListValues().build();
        double defense = DEFENSE.get();
        if(defense != 0.0D) {
            modifiers.put(
                Attributes.ARMOR,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "frozen_shield_armor"),
                    defense,
                    AttributeModifier.Operation.ADD_VALUE
                )
            );
        }
        double knockbackResistance = KNOCKBACK_RESISTANCE.get();
        if(knockbackResistance != 0.0D) {
            modifiers.put(
                Attributes.KNOCKBACK_RESISTANCE,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "frozen_shield_knockback_resistance"),
                    knockbackResistance,
                    AttributeModifier.Operation.ADD_VALUE
                )
            );
        }
        return modifiers;
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DEFENSE, KNOCKBACK_RESISTANCE, GUARDIAN_RANGE, MIN_HEALTH_RATIO, MAX_HEALTH_RATIO);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "frozen_shield",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/frozen_shield.png"),
        Component.translatable("item.lzxnoneterraria.frozen_shield"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(DEFENSE.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.defense_bonus",
                EffectTooltipUtil.formatNumber(DEFENSE.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        if(KNOCKBACK_RESISTANCE.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.knockback_resistance_bonus",
                EffectTooltipUtil.formatNumber(KNOCKBACK_RESISTANCE.get() * 100.0D)
            ).withStyle(ChatFormatting.GRAY));
        }
        if(GUARDIAN_RANGE.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.paladins_shield",
                EffectTooltipUtil.formatNumber(MIN_HEALTH_RATIO.get() * 100.0D),
                EffectTooltipUtil.formatNumber(PaladinsShieldEffect.getDamageAbsorptionRatio() * 100.0D)
            ).withStyle(ChatFormatting.GRAY));
        }
        if(MAX_HEALTH_RATIO.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.ice_barrier",
                EffectTooltipUtil.formatNumber(MAX_HEALTH_RATIO.get() * 100.0D),
                EffectTooltipUtil.formatNumber(IceBarrierEffect.getDamageReduction() * 100.0D)
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
