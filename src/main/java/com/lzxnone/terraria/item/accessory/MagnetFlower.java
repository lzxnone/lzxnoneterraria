package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.AutoManaPotionUser;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.ManaCostModifier;
import com.lzxnone.terraria.item.effect.StarPickupRangeModifier;
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

public class MagnetFlower extends AccessoryItem implements StarPickupRangeModifier, ManaCostModifier, AutoManaPotionUser {
    public static final ConfigDouble PICKUP_RANGE = new ConfigDouble(
        "accessory.magnet_flower.pickup_range",
        "star_pickup_range",
        16.0D,
        0.0D,
        100.0D
    );
    public static final ConfigDouble MANA_COST_MULTIPLIER = new ConfigDouble(
        "accessory.magnet_flower.mana_cost_multiplier",
        "mana_cost_multiplier",
        0.92D,
        0.0D,
        1.0D
    );

    public MagnetFlower() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public double getStarPickupRange(ItemStack stack, LivingEntity entity) {
        return PICKUP_RANGE.get();
    }

    @Override
    public double getManaCostMultiplier(ItemStack stack, LivingEntity entity) {
        return MANA_COST_MULTIPLIER.get();
    }

    @Override
    public boolean canAutoUseManaPotion(ItemStack stack, LivingEntity entity) {
        return true;
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(PICKUP_RANGE, MANA_COST_MULTIPLIER);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "magnet_flower",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/magnet_flower.png"),
        Component.translatable("item.lzxnoneterraria.magnet_flower"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(PICKUP_RANGE.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.star_pickup_range",
                EffectTooltipUtil.formatNumber(PICKUP_RANGE.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        if(MANA_COST_MULTIPLIER.get() != 1.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.mana_cost_decrease",
                EffectTooltipUtil.formatPercent(MANA_COST_MULTIPLIER.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        tooltipComponents.add(Component.translatable(
            "tooltip.lzxnoneterraria.auto_mana_potion_use"
        ).withStyle(ChatFormatting.GRAY));
    }
}
