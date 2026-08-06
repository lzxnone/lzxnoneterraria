package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.item.effect.IgniteOnHitModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class MagmaStone extends AccessoryItem implements IgniteOnHitModifier {
    public MagmaStone() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public boolean canApplyIgnite(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public int getIgniteTicks(ItemStack stack, LivingEntity entity) {
        int roll = entity.getRandom().nextInt(8);
        if(roll < 2) return 120; //6 秒 2/8 (25%)
        if(roll < 5) return 80;  //4 秒 3/8 (37.5%)
        return 40;               //2 秒 3/8 (37.5%)
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(
            "tooltip.lzxnoneterraria.ignite_on_hit"
        ).withStyle(ChatFormatting.GRAY));
    }
}
