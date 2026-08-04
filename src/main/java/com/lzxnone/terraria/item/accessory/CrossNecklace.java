package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.InvulnerableTimeModifier;
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

public class CrossNecklace extends AccessoryItem implements InvulnerableTimeModifier {
    public static final ConfigDouble INVULNERABLE_TIME_MULTIPLIER = new ConfigDouble(
        "accessory.cross_necklace.invulnerable_time_multiplier",
        "invulnerable_time_multiplier",
        2.0D,
        1.0D,
        10.0D
    );

    public CrossNecklace() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public double getInvulnerableTimeMultiplier(ItemStack stack, LivingEntity entity) {
        return INVULNERABLE_TIME_MULTIPLIER.get();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(INVULNERABLE_TIME_MULTIPLIER);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "cross_necklace",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/cross_necklace.png"),
        Component.translatable("item.lzxnoneterraria.cross_necklace"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(INVULNERABLE_TIME_MULTIPLIER.get() != 1.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.invulnerable_time_bonus",
                EffectTooltipUtil.formatNumber(INVULNERABLE_TIME_MULTIPLIER.get())
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
