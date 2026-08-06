package com.lzxnone.terraria.item.accessory;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.ConditionalAttributeModifier;
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

public class SunStone extends AccessoryItem implements DamageModifier, CriticalStrikeModifier, MinionKnockbackModifier, ConditionalAttributeModifier {
    public static final ConfigDouble DAMAGE_MULTIPLIER = new ConfigDouble(
        "accessory.sun_stone.damage_multiplier",
        "damage_multiplier",
        1.10D,
        1.0D,
        10.0D
    );
    public static final ConfigDouble CRIT_CHANCE = new ConfigDouble(
        "accessory.sun_stone.crit_chance",
        "crit_chance",
        0.02D,
        0.0D,
        1.0D
    );
    public static final ConfigDouble ATTACK_SPEED = new ConfigDouble(
        "accessory.sun_stone.attack_speed",
        "attack_speed",
        1.10D,
        1.0D,
        10.0D
    );
    public static final ConfigDouble DEFENSE = new ConfigDouble(
        "accessory.sun_stone.defense",
        "defense",
        4.0D,
        0.0D,
        100.0D
    );
    public static final ConfigDouble MINING_SPEED = new ConfigDouble(
        "accessory.sun_stone.mining_speed",
        "mining_speed",
        1.15D,
        1.0D,
        10.0D
    );
    public static final ConfigDouble MINION_KNOCKBACK_MULTIPLIER = new ConfigDouble(
        "accessory.sun_stone.minion_knockback_multiplier",
        "minion_knockback_multiplier",
        1.5D,
        1.0D,
        10.0D
    );

    public SunStone() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public double getDamageMultiplier(ItemStack stack, LivingEntity entity) {
        return !entity.level().isNight() ? DAMAGE_MULTIPLIER.get() : 1.0D;
    }

    @Override
    public double getCritChance(ItemStack stack, LivingEntity entity) {
        return !entity.level().isNight() ? CRIT_CHANCE.get() : 0.0D;
    }

    @Override
    public double getMinionKnockbackMultiplier(ItemStack stack, LivingEntity entity) {
        return !entity.level().isNight() ? MINION_KNOCKBACK_MULTIPLIER.get() : 1.0D;
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        //条件属性由 AccessoryUtil.syncConditionalModifiers 动态管理，不走 curios 自动应用
        return MultimapBuilder.hashKeys().arrayListValues().build();
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getConditionalModifiers(ItemStack stack, LivingEntity entity) {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = MultimapBuilder.hashKeys().arrayListValues().build();
        if(!entity.level().isNight()) buildModifiers(modifiers);
        return modifiers;
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAllConditionalModifiers(ItemStack stack, LivingEntity entity) {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = MultimapBuilder.hashKeys().arrayListValues().build();
        buildModifiers(modifiers);
        return modifiers;
    }

    private void buildModifiers(Multimap<Holder<Attribute>, AttributeModifier> modifiers) {
        double attackSpeed = ATTACK_SPEED.get() - 1.0D;
        if(attackSpeed != 0.0D) {
            modifiers.put(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "sun_stone_attack_speed"),
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
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "sun_stone_armor"),
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
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "sun_stone_mining_speed"),
                    miningSpeed,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
            );
        }
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DAMAGE_MULTIPLIER, CRIT_CHANCE, ATTACK_SPEED, DEFENSE, MINING_SPEED, MINION_KNOCKBACK_MULTIPLIER);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "sun_stone",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/sun_stone.png"),
        Component.translatable("item.lzxnoneterraria.sun_stone"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(
            "tooltip.lzxnoneterraria.active_at_day"
        ).withStyle(ChatFormatting.GRAY));
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
