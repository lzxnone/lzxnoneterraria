package com.lzxnone.terraria.item.accessory;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.ConditionalAttributeModifier;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.MeleeCritChanceModifier;
import com.lzxnone.terraria.item.effect.MeleeDamageModifier;
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

public class MoonShell extends AccessoryItem implements MeleeDamageModifier, MeleeCritChanceModifier, WaterBreathingModifier, ConditionalAttributeModifier {
    public static final ConfigDouble MELEE_DAMAGE_MULTIPLIER = new ConfigDouble(
        "accessory.moon_shell.melee_damage_multiplier",
        "melee_damage_multiplier",
        1.051D,
        1.0D,
        10.0D
    );
    public static final ConfigDouble MELEE_CRIT_CHANCE = new ConfigDouble(
        "accessory.moon_shell.melee_crit_chance",
        "melee_crit_chance",
        0.02D,
        0.0D,
        1.0D
    );
    public static final ConfigDouble ATTACK_SPEED = new ConfigDouble(
        "accessory.moon_shell.attack_speed",
        "attack_speed",
        1.051D,
        1.0D,
        10.0D
    );
    public static final ConfigDouble DEFENSE = new ConfigDouble(
        "accessory.moon_shell.defense",
        "defense",
        3.0D,
        0.0D,
        100.0D
    );
    public static final ConfigDouble JUMP_HEIGHT = new ConfigDouble(
        "accessory.moon_shell.jump_height",
        "jump_height",
        1.2D,
        1.0D,
        10.0D
    );
    public static final ConfigDouble WOLF_MOVE_SPEED = new ConfigDouble(
        "accessory.moon_shell.wolf_move_speed",
        "move_speed",
        1.05D,
        1.0D,
        10.0D
    );
    public static final ConfigDouble MOVE_SPEED = new ConfigDouble(
        "accessory.moon_shell.move_speed",
        "move_speed",
        1.5D,
        1.0D,
        10.0D
    );

    public MoonShell() {
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
    public boolean canBreatheUnderwater(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        return MultimapBuilder.hashKeys().arrayListValues().build();
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getConditionalModifiers(ItemStack stack, LivingEntity entity) {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = MultimapBuilder.hashKeys().arrayListValues().build();
        if(entity.level().isNight()) {
            buildWolfModifiers(modifiers);
        }
        if(entity.isInWater()) {
            buildMerfolkModifiers(modifiers);
        }
        return modifiers;
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAllConditionalModifiers(ItemStack stack, LivingEntity entity) {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = MultimapBuilder.hashKeys().arrayListValues().build();
        buildWolfModifiers(modifiers);
        buildMerfolkModifiers(modifiers);
        return modifiers;
    }

    //狼人（夜晚）：近战攻速、防御、移动速度
    private void buildWolfModifiers(Multimap<Holder<Attribute>, AttributeModifier> modifiers) {
        double attackSpeed = ATTACK_SPEED.get() - 1.0D;
        if(attackSpeed != 0.0D) {
            modifiers.put(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "moon_shell_wolf_attack_speed"),
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
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "moon_shell_wolf_armor"),
                    defense,
                    AttributeModifier.Operation.ADD_VALUE
                )
            );
        }
        double wolfMoveSpeed = WOLF_MOVE_SPEED.get() - 1.0D;
        if(wolfMoveSpeed != 0.0D) {
            modifiers.put(
                Attributes.MOVEMENT_SPEED,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "moon_shell_wolf_move_speed"),
                    wolfMoveSpeed,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
            );
        }
        double jumpStrength = JUMP_HEIGHT.get() - 1.0D;
        if(jumpStrength != 0.0D) {
            modifiers.put(
                Attributes.JUMP_STRENGTH,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "moon_shell_wolf_jump_height"),
                    jumpStrength,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
            );
        }
    }

    //人鱼（水中）：移动速度
    private void buildMerfolkModifiers(Multimap<Holder<Attribute>, AttributeModifier> modifiers) {
        double moveSpeed = MOVE_SPEED.get() - 1.0D;
        if(moveSpeed != 0.0D) {
            modifiers.put(
                Attributes.MOVEMENT_SPEED,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "moon_shell_merfolk_move_speed"),
                    moveSpeed,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
            );
        }
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(MELEE_DAMAGE_MULTIPLIER, MELEE_CRIT_CHANCE, ATTACK_SPEED, DEFENSE, JUMP_HEIGHT, WOLF_MOVE_SPEED, MOVE_SPEED);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "moon_shell",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/moon_shell.png"),
        Component.translatable("item.lzxnoneterraria.moon_shell"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        //狼人（夜晚）
        tooltipComponents.add(Component.translatable(
            "tooltip.lzxnoneterraria.active_at_night"
        ).withStyle(ChatFormatting.GRAY));
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
        if(WOLF_MOVE_SPEED.get() != 1.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.move_speed_bonus",
                EffectTooltipUtil.formatPercent(WOLF_MOVE_SPEED.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        //人鱼（水中）
        tooltipComponents.add(Component.translatable(
            "tooltip.lzxnoneterraria.active_in_water"
        ).withStyle(ChatFormatting.GRAY));
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
