package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.MagicDamageModifier;
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

public class CelestialEmblem extends AccessoryItem implements MagicDamageModifier, StarPickupRangeModifier {
    public static final ConfigDouble MAGIC_DAMAGE_MULTIPLIER = new ConfigDouble(
        "accessory.celestial_emblem.magic_damage_multiplier",
        "magic_damage_multiplier",
        1.15D,
        1.0D,
        10.0D
    );
    public static final ConfigDouble PICKUP_RANGE = new ConfigDouble(
        "accessory.celestial_emblem.pickup_range",
        "star_pickup_range",
        16.0D,
        0.0D,
        100.0D
    );

    public CelestialEmblem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public double getMagicDamageMultiplier(ItemStack stack, LivingEntity entity) {
        return MAGIC_DAMAGE_MULTIPLIER.get();
    }

    @Override
    public double getStarPickupRange(ItemStack stack, LivingEntity entity) {
        return PICKUP_RANGE.get();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(MAGIC_DAMAGE_MULTIPLIER, PICKUP_RANGE);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "celestial_emblem",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/celestial_emblem.png"),
        Component.translatable("item.lzxnoneterraria.celestial_emblem"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(MAGIC_DAMAGE_MULTIPLIER.get() != 1.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.magic_damage_increase",
                EffectTooltipUtil.formatPercent(MAGIC_DAMAGE_MULTIPLIER.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        if(PICKUP_RANGE.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.star_pickup_range",
                EffectTooltipUtil.formatNumber(PICKUP_RANGE.get())
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
