package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.MerchantDiscountModifier;
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

public class DiscountCard extends AccessoryItem implements MerchantDiscountModifier {
    //商人交易折扣比例（0.2 = 价格减 20%）
    public static final ConfigDouble DISCOUNT_RATIO = new ConfigDouble(
        "accessory.discount_card.discount_ratio",
        "merchant_discount_ratio",
        0.2D,
        0.0D,
        1.0D
    );

    public DiscountCard() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public double getMerchantDiscountRatio(ItemStack stack, LivingEntity entity) {
        return DISCOUNT_RATIO.get();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DISCOUNT_RATIO);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "discount_card",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/discount_card.png"),
        Component.translatable("item.lzxnoneterraria.discount_card"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(DISCOUNT_RATIO.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.merchant_discount",
                EffectTooltipUtil.formatNumber(DISCOUNT_RATIO.get() * 100.0D) + "%"
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
