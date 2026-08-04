package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.item.effect.MobEffectImmunityModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ThePlan extends AccessoryItem implements MobEffectImmunityModifier {
    public ThePlan() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public boolean isSlownessImmunity(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean isConfusedImmunity(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(
            "tooltip.lzxnoneterraria.slowness_immunity"
        ).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable(
            "tooltip.lzxnoneterraria.confused_immunity"
        ).withStyle(ChatFormatting.GRAY));
    }
}
