package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.CriticalStrikeModifier;
import com.lzxnone.terraria.item.effect.DamageModifier;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class DestroyerEmblem extends AccessoryItem implements DamageModifier, CriticalStrikeModifier {
    public static final ConfigDouble DAMAGE_MULTIPLIER = new ConfigDouble(
        "accessory.destroyer_emblem.damage_multiplier",
        "damage_multiplier",
        1.10D,
        1.0D,
        10.0D
    );
    public static final ConfigDouble CRIT_CHANCE = new ConfigDouble(
        "accessory.destroyer_emblem.crit_chance",
        "crit_chance",
        0.08D,
        0.0D,
        1.0D
    );

    public DestroyerEmblem() {
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

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DAMAGE_MULTIPLIER, CRIT_CHANCE);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "destroyer_emblem",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/destroyer_emblem.png"),
        Component.translatable("item.lzxnoneterraria.destroyer_emblem"),
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
    }
}
