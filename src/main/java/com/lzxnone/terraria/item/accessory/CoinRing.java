package com.lzxnone.terraria.item.accessory;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.EmeraldDropOnHitModifier;
import com.lzxnone.terraria.item.effect.EmeraldPickupRangeModifier;
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

public class CoinRing extends AccessoryItem implements EmeraldPickupRangeModifier, EmeraldDropOnHitModifier {
    //绿宝石吸附范围（金戒指效果）
    public static final ConfigDouble PICKUP_RANGE = new ConfigDouble(
        "accessory.coin_ring.pickup_range",
        "emerald_pickup_range",
        16.0D,
        0.0D,
        16.0D
    );
    //运气加成（幸运币效果）
    public static final ConfigDouble LUCK_BONUS = new ConfigDouble(
        "accessory.coin_ring.luck_bonus",
        "luck_bonus",
        0.05D,
        0.0D,
        10.0D
    );
    //击中敌人掉落绿宝石概率（幸运币效果）
    public static final ConfigDouble DROP_CHANCE = new ConfigDouble(
        "accessory.coin_ring.emerald_drop_chance",
        "emerald_drop_chance",
        0.01D,
        0.0D,
        1.0D
    );

    public CoinRing() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public double getEmeraldPickupRange(ItemStack stack, LivingEntity entity) {
        return PICKUP_RANGE.get();
    }

    @Override
    public double getEmeraldDropChance(ItemStack stack, LivingEntity entity) {
        return DROP_CHANCE.get();
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = MultimapBuilder.hashKeys().arrayListValues().build();
        double luck = LUCK_BONUS.get();
        if(luck != 0.0D) {
            modifiers.put(
                Attributes.LUCK,
                new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "coin_ring_luck"),
                    luck,
                    AttributeModifier.Operation.ADD_VALUE
                )
            );
        }
        return modifiers;
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(PICKUP_RANGE, LUCK_BONUS, DROP_CHANCE);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "coin_ring",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/coin_ring.png"),
        Component.translatable("item.lzxnoneterraria.coin_ring"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(PICKUP_RANGE.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.emerald_pickup_range",
                EffectTooltipUtil.formatNumber(PICKUP_RANGE.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        if(LUCK_BONUS.get() != 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.luck_bonus",
                EffectTooltipUtil.formatNumber(LUCK_BONUS.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        if(DROP_CHANCE.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.emerald_drop"
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
