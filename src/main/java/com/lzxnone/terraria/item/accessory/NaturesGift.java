package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.accessory.effect.ManaCostModifier;
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
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Locale;

public class NaturesGift extends AccessoryItem implements ManaCostModifier {
    public static final ConfigDouble MANA_COST_MULTIPLIER = new ConfigDouble(
        "accessory.natures_gift.mana_cost_multiplier",
        "natures_gift_mana_cost_multiplier",
        0.94D,
        0.0D,
        1.0D
    );

    public NaturesGift() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public double getManaCostMultiplier(ItemStack stack, LivingEntity entity) {
        return MANA_COST_MULTIPLIER.get();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(MANA_COST_MULTIPLIER);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "natures_gift",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/natures_gift.png"),
        Component.translatable("item.lzxnoneterraria.natures_gift"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(
            "tooltip.lzxnoneterraria.natures_gift.0",
            formatManaCostReductionPercent(MANA_COST_MULTIPLIER.get())
        ).withStyle(ChatFormatting.GRAY));
    }

    private static String formatManaCostReductionPercent(double multiplier) {
        double reduction = Math.max(0.0D, (1.0D - multiplier) * 100.0D);
        if(Math.abs(reduction - Math.rint(reduction)) < 1.0E-6D) return Integer.toString((int)Math.rint(reduction));
        String formatted = String.format(Locale.ROOT, "%.2f", reduction);
        while(formatted.endsWith("0")) formatted = formatted.substring(0, formatted.length() - 1);
        if(formatted.endsWith(".")) formatted = formatted.substring(0, formatted.length() - 1);
        return formatted;
    }
}
