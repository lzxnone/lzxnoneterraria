package com.lzxnone.terraria.item.accessory;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.EmeraldDropOnHitModifier;
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

public class LuckyCoin extends AccessoryItem implements EmeraldDropOnHitModifier {
    //运气加成（属性修饰符）
    public static final ConfigDouble LUCK_BONUS = new ConfigDouble(
        "accessory.lucky_coin.luck_bonus",
        "luck_bonus",
        0.05D,
        0.0D,
        10.0D
    );
    //击中敌人掉落绿宝石概率
    public static final ConfigDouble DROP_CHANCE = new ConfigDouble(
        "accessory.lucky_coin.emerald_drop_chance",
        "emerald_drop_chance",
        0.01D,
        0.0D,
        1.0D
    );

    public LuckyCoin() {
        super(new Item.Properties().stacksTo(1));
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
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "lucky_coin_luck"),
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
            ConfigStruct.loadAll(LUCK_BONUS, DROP_CHANCE);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "lucky_coin",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/lucky_coin.png"),
        Component.translatable("item.lzxnoneterraria.lucky_coin"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
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
