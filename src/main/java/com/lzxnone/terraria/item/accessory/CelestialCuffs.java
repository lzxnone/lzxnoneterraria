package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.ManaOnHurtModifier;
import com.lzxnone.terraria.item.effect.MaxManaModifier;
import com.lzxnone.terraria.item.effect.StarPickupRangeModifier;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class CelestialCuffs extends AccessoryItem implements StarPickupRangeModifier, MaxManaModifier, ManaOnHurtModifier {
    public static final ConfigDouble PICKUP_RANGE = new ConfigDouble(
        "accessory.celestial_cuffs.pickup_range",
        "star_pickup_range",
        16.0D,
        0.0D,
        100.0D
    );
    public static final ConfigInt MAX_MANA_BONUS = new ConfigInt(
        "accessory.celestial_cuffs.max_mana_bonus",
        "max_mana_bonus",
        20,
        0,
        400
    );
    public static final ConfigDouble MANA_ON_HURT_MULTIPLIER = new ConfigDouble(
        "accessory.celestial_cuffs.mana_on_hurt_multiplier",
        "mana_on_hurt_multiplier",
        5.0D,
        0.0D,
        1000.0D
    );

    public CelestialCuffs() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public double getStarPickupRange(ItemStack stack, LivingEntity entity) {
        return PICKUP_RANGE.get();
    }

    @Override
    public int getMaxManaBonus(ItemStack stack, LivingEntity entity) {
        return MAX_MANA_BONUS.get();
    }

    @Override
    public double getManaOnHurtMultiplier(ItemStack stack, LivingEntity entity) {
        return MANA_ON_HURT_MULTIPLIER.get();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(PICKUP_RANGE, MAX_MANA_BONUS, MANA_ON_HURT_MULTIPLIER);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "celestial_cuffs",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/celestial_cuffs.png"),
        Component.translatable("item.lzxnoneterraria.celestial_cuffs"),
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
        if(MAX_MANA_BONUS.get() > 0) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.max_mana_bonus",
                EffectTooltipUtil.formatNumber(MAX_MANA_BONUS.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        if(MANA_ON_HURT_MULTIPLIER.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.mana_on_hurt"
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
