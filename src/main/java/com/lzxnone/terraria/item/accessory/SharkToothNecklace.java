package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.TargetArmorModifier;
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

public class SharkToothNecklace extends AccessoryItem implements TargetArmorModifier {
    public static final ConfigDouble ARMOR_PIERCE = new ConfigDouble(
        "accessory.shark_tooth_necklace.armor_pierce",
        "armor_pierce",
        2.0D,
        0.0D,
        100.0D
    );

    public SharkToothNecklace() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public double getTargetArmorReduction(ItemStack stack, LivingEntity entity, LivingEntity target) {
        return ARMOR_PIERCE.get();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(ARMOR_PIERCE);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "shark_tooth_necklace",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/shark_tooth_necklace.png"),
        Component.translatable("item.lzxnoneterraria.shark_tooth_necklace"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(ARMOR_PIERCE.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.armor_pierce_bonus",
                EffectTooltipUtil.formatNumber(ARMOR_PIERCE.get())
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
