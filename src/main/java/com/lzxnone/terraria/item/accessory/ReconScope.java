package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.CriticalStrikeModifier;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.RangedDamageModifier;
import com.lzxnone.terraria.item.effect.ScopeZoomModifier;
import com.lzxnone.terraria.item.effect.VisibilityModifier;
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

public class ReconScope extends AccessoryItem implements ScopeZoomModifier, RangedDamageModifier, CriticalStrikeModifier, VisibilityModifier {
    public static final ConfigDouble VISIBILITY_MULTIPLIER = new ConfigDouble(
        "accessory.recon_scope.visibility_multiplier",
        "visibility_multiplier",
        0.5D,
        0.0D,
        10.0D
    );
    public static final ConfigDouble RANGED_DAMAGE_MULTIPLIER = new ConfigDouble(
        "accessory.recon_scope.ranged_damage_multiplier",
        "ranged_damage_multiplier",
        1.10D,
        1.0D,
        10.0D
    );
    public static final ConfigDouble CRIT_CHANCE = new ConfigDouble(
        "accessory.recon_scope.crit_chance",
        "crit_chance",
        0.10D,
        0.0D,
        1.0D
    );

    public ReconScope() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public boolean canZoom(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public double getVisibilityMultiplier(ItemStack stack, LivingEntity entity) {
        return VISIBILITY_MULTIPLIER.get();
    }

    @Override
    public double getRangedDamageMultiplier(ItemStack stack, LivingEntity entity) {
        return RANGED_DAMAGE_MULTIPLIER.get();
    }

    @Override
    public double getCritChance(ItemStack stack, LivingEntity entity) {
        return CRIT_CHANCE.get();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(VISIBILITY_MULTIPLIER, RANGED_DAMAGE_MULTIPLIER, CRIT_CHANCE);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "recon_scope",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/recon_scope.png"),
        Component.translatable("item.lzxnoneterraria.recon_scope"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(
            "tooltip.lzxnoneterraria.rifle_scope"
        ).withStyle(ChatFormatting.GRAY));
        if(RANGED_DAMAGE_MULTIPLIER.get() != 1.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.ranged_damage_increase",
                EffectTooltipUtil.formatPercent(RANGED_DAMAGE_MULTIPLIER.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        if(CRIT_CHANCE.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.crit_chance_bonus",
                EffectTooltipUtil.formatNumber(CRIT_CHANCE.get() * 100.0D)
            ).withStyle(ChatFormatting.GRAY));
        }
        if(VISIBILITY_MULTIPLIER.get() < 1.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.aggro_decrease"
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
