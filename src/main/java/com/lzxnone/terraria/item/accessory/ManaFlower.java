package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.item.accessory.effect.AutoManaPotionUser;
import com.lzxnone.terraria.item.accessory.effect.ManaCostModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ManaFlower extends AccessoryItem implements ManaCostModifier, AutoManaPotionUser {
    private static final double MANA_COST_MULTIPLIER = 0.92D;

    public ManaFlower() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public double getManaCostMultiplier(ItemStack stack, LivingEntity entity) {
        return MANA_COST_MULTIPLIER;
    }

    @Override
    public boolean canAutoUseManaPotion(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("tooltip.lzxnoneterraria.mana_flower.0").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.lzxnoneterraria.mana_flower.1").withStyle(ChatFormatting.GRAY));
    }
}
