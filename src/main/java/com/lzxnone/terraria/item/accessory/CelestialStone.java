package com.lzxnone.terraria.item.accessory;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.CriticalStrikeModifier;
import com.lzxnone.terraria.item.effect.DamageModifier;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.MinionKnockbackModifier;
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

public class CelestialStone extends AccessoryItem implements DamageModifier, CriticalStrikeModifier, MinionKnockbackModifier {
    public static final ConfigDouble DAMAGE_MULTIPLIER = new ConfigDouble(
        "accessory.celestial_stone.damage_multiplier",
        "damage_multiplier",
        1.10D,
        1.0D,
        10.0D
    );
    public static final ConfigDouble CRIT_CHANCE = new ConfigDouble(
        "accessory.celestial_stone.crit_chance",
        "crit_chance",
        0.02D,
        0.0D,
        1.0D
    );
    public static final ConfigDouble ATTACK_SPEED = new ConfigDouble(
        "accessory.celestial_stone.attack_speed",
        "attack_speed",
        1.10D,
        1.0D,
        10.0D
    );
    public static final ConfigDouble DEFENSE = new ConfigDouble(
        "accessory.celestial_stone.defense",
        "defense",
        4.0D,
        0.0D,
        100.0D
    );
    public static final ConfigDouble MINING_SPEED = new ConfigDouble(
        "accessory.celestial_stone.mining_speed",
        "mining_speed",
        1.15D,
        1.0D,
        10.0D
    );
    public static final ConfigDouble MINION_KNOCKBACK_MULTIPLIER = new ConfigDouble(
        "accessory.celestial_stone.minion_knockback_multiplier",
        "minion_knockback_multiplier",
        1.5D,
        1.0D,
        10.0D
    );

    public CelestialStone() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public double getDamageMultiplier(ItemStack stack, LivingEntity entity) {
        return DAMAGE_MULTIPLIER.get();
    }

    @Override
    public double getCritChance(ItemStack stack, LivingEntity entity) {
        return CRIT_CHANCE.get();
    }

    @Override
    public double getMinionKnockbackMultiplier(ItemStack stack, LivingEntity entity) {
        return MINION_KNOCKBACK_MULTIPLIER.get();
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = MultimapBuilder.hashKeys().arrayListValues().build();
        double attackSpeed = ATTACK_SPEED.get() - 1.0D;
        if(attackSpeed != 0.0D) {
            modifiers.put(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "celestial_stone_attack_speed"),
                    attackSpeed,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
            );
        }
        double defense = DEFENSE.get();
        if(defense != 0.0D) {
            modifiers.put(
                Attributes.ARMOR,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "celestial_stone_armor"),
                    defense,
                    AttributeModifier.Operation.ADD_VALUE
                )
            );
        }
        double miningSpeed = MINING_SPEED.get() - 1.0D;
        if(miningSpeed != 0.0D) {
            modifiers.put(
                Attributes.BLOCK_BREAK_SPEED,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "celestial_stone_mining_speed"),
                    miningSpeed,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
            );
        }
        return modifiers;
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DAMAGE_MULTIPLIER, CRIT_CHANCE, ATTACK_SPEED, DEFENSE, MINING_SPEED, MINION_KNOCKBACK_MULTIPLIER);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "celestial_stone",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/celestial_stone.png"),
        Component.translatable("item.lzxnoneterraria.celestial_stone"),
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
        if(CRIT_CHANCE.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.crit_chance_bonus",
                EffectTooltipUtil.formatNumber(CRIT_CHANCE.get() * 100.0D)
            ).withStyle(ChatFormatting.GRAY));
        }
        if(ATTACK_SPEED.get() != 1.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.attack_speed_bonus",
                EffectTooltipUtil.formatPercent(ATTACK_SPEED.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        if(DEFENSE.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.defense_bonus",
                EffectTooltipUtil.formatNumber(DEFENSE.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        if(MINING_SPEED.get() != 1.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.mining_speed_bonus",
                EffectTooltipUtil.formatPercent(MINING_SPEED.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        if(MINION_KNOCKBACK_MULTIPLIER.get() != 1.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.minion_knockback_increase",
                EffectTooltipUtil.formatPercent(MINION_KNOCKBACK_MULTIPLIER.get())
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
