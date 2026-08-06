package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.item.effect.ScopeZoomModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class RifleScope extends AccessoryItem implements ScopeZoomModifier {
    public RifleScope() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public boolean canZoom(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<net.minecraft.network.chat.Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(net.minecraft.network.chat.Component.translatable(
            "tooltip.lzxnoneterraria.rifle_scope"
        ).withStyle(net.minecraft.ChatFormatting.GRAY));
    }
}
