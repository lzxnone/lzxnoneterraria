package com.lzxnone.terraria.item.armor;

import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.ManaCostModifier;
import com.lzxnone.terraria.item.effect.MaxManaModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class GemRobe extends ArmorItem implements MaxManaModifier, ManaCostModifier {
    private final int maxManaBonus;
    private final double manaCostMultiplier;
    private final int skillMask;

    public GemRobe(Holder<ArmorMaterial> material, int maxManaBonus, double manaCostMultiplier, int skillMask) {
        super(material, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1));
        this.maxManaBonus = maxManaBonus;
        this.manaCostMultiplier = manaCostMultiplier;
        this.skillMask = skillMask;
    }

    public int getSkillMask(ItemStack stack) {
        return this.skillMask;
    }

    public int getSkillMask() {
        return this.skillMask;
    }

    @Override
    public int getMaxManaBonus(ItemStack stack, LivingEntity entity) {
        return this.maxManaBonus;
    }

    @Override
    public double getManaCostMultiplier(ItemStack stack, LivingEntity entity) {
        return this.manaCostMultiplier;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(
            "tooltip.lzxnoneterraria.max_mana_bonus",
            EffectTooltipUtil.formatNumber(this.maxManaBonus)
        ).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable(
            "tooltip.lzxnoneterraria.mana_cost_decrease",
            EffectTooltipUtil.formatPercent(this.manaCostMultiplier)
        ).withStyle(ChatFormatting.GRAY));
    }
}
