package com.lzxnone.terraria.item.accessory;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.ConditionalAttributeModifier;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.LifeRegenerationModifier;
import com.lzxnone.terraria.item.effect.MeleeCritChanceModifier;
import com.lzxnone.terraria.item.effect.MeleeDamageModifier;
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

public class MoonCharm extends AccessoryItem implements MeleeDamageModifier, MeleeCritChanceModifier, LifeRegenerationModifier, ConditionalAttributeModifier {
    public static final ConfigDouble MELEE_DAMAGE_MULTIPLIER = new ConfigDouble(
        "accessory.moon_charm.melee_damage_multiplier",
        "melee_damage_multiplier",
        1.051D,
        1.0D,
        10.0D
    );
    public static final ConfigDouble MELEE_CRIT_CHANCE = new ConfigDouble(
        "accessory.moon_charm.melee_crit_chance",
        "melee_crit_chance",
        0.02D,
        0.0D,
        1.0D
    );
    public static final ConfigDouble ATTACK_SPEED = new ConfigDouble(
        "accessory.moon_charm.attack_speed",
        "attack_speed",
        1.051D,
        1.0D,
        10.0D
    );
    public static final ConfigDouble MOVE_SPEED = new ConfigDouble(
        "accessory.moon_charm.move_speed",
        "move_speed",
        1.05D,
        1.0D,
        10.0D
    );
    public static final ConfigDouble DEFENSE = new ConfigDouble(
        "accessory.moon_charm.defense",
        "defense",
        3.0D,
        0.0D,
        100.0D
    );
    public static final ConfigDouble JUMP_HEIGHT = new ConfigDouble(
        "accessory.moon_charm.jump_height",
        "jump_height",
        1.2D,
        1.0D,
        10.0D
    );
    public static final ConfigDouble LIFE_REGEN = new ConfigDouble(
        "accessory.moon_charm.life_regen",
        "health_per_second",
        0.1D,
        0.0D,
        1000.0D
    );

    public MoonCharm() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public double getMeleeDamageMultiplier(ItemStack stack, LivingEntity entity) {
        return entity.level().isNight() ? MELEE_DAMAGE_MULTIPLIER.get() : 1.0D;
    }

    @Override
    public double getMeleeCritChance(ItemStack stack, LivingEntity entity) {
        return entity.level().isNight() ? MELEE_CRIT_CHANCE.get() : 0.0D;
    }

    @Override
    public float getHealthPerSecond(ItemStack stack, LivingEntity entity) {
        return entity.level().isNight() ? LIFE_REGEN.get().floatValue() : 0.0F;
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        return getConditionalModifiers(stack, slotContext.entity());
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getConditionalModifiers(ItemStack stack, LivingEntity entity) {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = MultimapBuilder.hashKeys().arrayListValues().build();
        if(entity.level().isNight()) buildModifiers(modifiers);
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
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "moon_charm_attack_speed"),
                    attackSpeed,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
            );
        }
        double moveSpeed = MOVE_SPEED.get() - 1.0D;
        if(moveSpeed != 0.0D) {
            modifiers.put(
                Attributes.MOVEMENT_SPEED,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "moon_charm_move_speed"),
                    moveSpeed,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
            );
        }
        double defense = DEFENSE.get();
        if(defense != 0.0D) {
            modifiers.put(
                Attributes.ARMOR,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "moon_charm_armor"),
                    defense,
                    AttributeModifier.Operation.ADD_VALUE
                )
            );
        }
        double jumpStrength = JUMP_HEIGHT.get() - 1.0D;
        if(jumpStrength != 0.0D) {
            modifiers.put(
                Attributes.JUMP_STRENGTH,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "moon_charm_jump_height"),
                    jumpStrength,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
            );
        }
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(MELEE_DAMAGE_MULTIPLIER, MELEE_CRIT_CHANCE, ATTACK_SPEED, MOVE_SPEED, DEFENSE, JUMP_HEIGHT, LIFE_REGEN);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "moon_charm",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/moon_charm.png"),
        Component.translatable("item.lzxnoneterraria.moon_charm"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(
            "tooltip.lzxnoneterraria.active_at_night"
        ).withStyle(ChatFormatting.GREEN));
        if(MELEE_CRIT_CHANCE.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.melee_crit_chance_bonus",
                EffectTooltipUtil.formatNumber(MELEE_CRIT_CHANCE.get() * 100.0D)
            ).withStyle(ChatFormatting.GRAY));
        }
        if(MELEE_DAMAGE_MULTIPLIER.get() != 1.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.melee_damage_increase",
                EffectTooltipUtil.formatPercent(MELEE_DAMAGE_MULTIPLIER.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        if(ATTACK_SPEED.get() != 1.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.attack_speed_bonus",
                EffectTooltipUtil.formatPercent(ATTACK_SPEED.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        if(MOVE_SPEED.get() != 1.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.move_speed_bonus",
                EffectTooltipUtil.formatPercent(MOVE_SPEED.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        if(DEFENSE.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.defense_bonus",
                EffectTooltipUtil.formatNumber(DEFENSE.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        if(JUMP_HEIGHT.get() > 1.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.jump_boost"
            ).withStyle(ChatFormatting.GRAY));
        }
        if(LIFE_REGEN.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.life_regen",
                EffectTooltipUtil.formatNumber(LIFE_REGEN.get())
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
