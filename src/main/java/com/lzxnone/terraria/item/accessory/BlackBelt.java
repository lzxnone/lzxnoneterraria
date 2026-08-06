package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.DamageImmunityModifier;
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

public class BlackBelt extends AccessoryItem implements DamageImmunityModifier {
    public static final ConfigDouble DAMAGE_IMMUNITY_CHANCE = new ConfigDouble(
        "accessory.black_belt.damage_immunity",
        "damage_immunity",
        0.1D,
        0.0D,
        1.0D
    );

    public BlackBelt() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public double getDamageImmunityChance(ItemStack stack, LivingEntity entity) {
        return DAMAGE_IMMUNITY_CHANCE.get();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DAMAGE_IMMUNITY_CHANCE);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "black_belt",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/black_belt.png"),
        Component.translatable("item.lzxnoneterraria.black_belt"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(DAMAGE_IMMUNITY_CHANCE.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.damage_immunity",
                EffectTooltipUtil.formatNumber(DAMAGE_IMMUNITY_CHANCE.get() * 100.0D)
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
