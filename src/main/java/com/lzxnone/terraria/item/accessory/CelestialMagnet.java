package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
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

public class CelestialMagnet extends AccessoryItem implements StarPickupRangeModifier {
    public static final ConfigDouble PICKUP_RANGE = new ConfigDouble(
        "accessory.celestial_magnet.pickup_range",
        "star_pickup_range",
        16.0D,
        0.0D,
        16.0D
    );

    public CelestialMagnet() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public double getStarPickupRange(ItemStack stack, LivingEntity entity) {
        return PICKUP_RANGE.get();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(PICKUP_RANGE);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "celestial_magnet",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/celestial_magnet.png"),
        Component.translatable("item.lzxnoneterraria.celestial_magnet"),
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
    }
}
