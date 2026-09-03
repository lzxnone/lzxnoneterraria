package com.lzxnone.terraria.item.accessory;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.LifeRegenerationModifier;
import com.lzxnone.terraria.item.effect.ManaRegenerationModifier;
import com.lzxnone.terraria.item.effect.MaxManaModifier;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
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
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class RestorationShield extends AccessoryItem implements MaxManaModifier, ManaRegenerationModifier, LifeRegenerationModifier {
    public static final ConfigDouble KNOCKBACK_RESISTANCE = new ConfigDouble(
        "accessory.restoration_shield.knockback_resistance",
        "knockback_resistance",
        1.0D,
        0.0D,
        1.0D
    );
    public static final ConfigDouble DEFENSE = new ConfigDouble(
        "accessory.restoration_shield.defense",
        "defense",
        1.0D,
        0.0D,
        100.0D
    );
    public static final ConfigInt MAX_MANA_BONUS = new ConfigInt(
        "accessory.restoration_shield.max_mana_bonus",
        "max_mana_bonus",
        40,
        0,
        400
    );
    public static final ConfigDouble MANA_REGEN_BONUS = new ConfigDouble(
        "accessory.restoration_shield.mana_regen_bonus",
        "mana_regen_bonus",
        60.0D,
        0.0D,
        1000.0D
    );
    public static final ConfigDouble HEALTH_PER_SECOND = new ConfigDouble(
        "accessory.restoration_shield.health_per_second",
        "health_per_second",
        0.2D,
        0.0D,
        1000.0D
    );

    public RestorationShield() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = MultimapBuilder.hashKeys().arrayListValues().build();
        double knockback = KNOCKBACK_RESISTANCE.get();
        if(knockback != 0.0D) {
            modifiers.put(
                Attributes.KNOCKBACK_RESISTANCE,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "restoration_shield_knockback_resistance"),
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
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "restoration_shield_defense"),
                    defense,
                    AttributeModifier.Operation.ADD_VALUE
                )
            );
        }
        return modifiers;
    }

    @Override
    public int getMaxManaBonus(ItemStack stack, LivingEntity entity) {
        return MAX_MANA_BONUS.get();
    }

    @Override
    public double getManaRegenBonus(ItemStack stack, LivingEntity entity) {
        return MANA_REGEN_BONUS.get();
    }

    @Override
    public float getHealthPerSecond(ItemStack stack, LivingEntity entity) {
        return HEALTH_PER_SECOND.get().floatValue();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(KNOCKBACK_RESISTANCE, DEFENSE, MAX_MANA_BONUS, MANA_REGEN_BONUS, HEALTH_PER_SECOND);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "restoration_shield",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/restoration_shield.png"),
        Component.translatable("item.lzxnoneterraria.restoration_shield"),
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
        if(HEALTH_PER_SECOND.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.life_regen",
                EffectTooltipUtil.formatNumber(HEALTH_PER_SECOND.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        if(MAX_MANA_BONUS.get() > 0) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.max_mana_bonus",
                EffectTooltipUtil.formatNumber(MAX_MANA_BONUS.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        if(MANA_REGEN_BONUS.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.mana_regen_bonus",
                EffectTooltipUtil.formatNumber(MANA_REGEN_BONUS.get())
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
