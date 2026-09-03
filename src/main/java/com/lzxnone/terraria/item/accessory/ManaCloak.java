package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.AutoManaPotionUser;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.FallenStarSummoner;
import com.lzxnone.terraria.item.effect.ManaCostModifier;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ManaCloak extends AccessoryItem implements FallenStarSummoner, ManaCostModifier, AutoManaPotionUser {
    public static final ConfigDouble FALLEN_STAR_DAMAGE = new ConfigDouble(
        "accessory.mana_cloak.fallen_star_damage",
        "fallen_star_damage",
        7.0D,
        0.0D,
        8388600.0D
    );
    public static final ConfigDouble MANA_COST_MULTIPLIER = new ConfigDouble(
        "accessory.mana_cloak.mana_cost_multiplier",
        "mana_cost_multiplier",
        0.92D,
        0.0D,
        1.0D
    );
    public static final ConfigInt ATTACK_COOLDOWN = new ConfigInt(
        "accessory.mana_cloak.attack_cooldown",
        "attack_cooldown",
        20,
        0,
        1200
    );

    public ManaCloak() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public String getStar(ItemStack stack, LivingEntity entity) {
        return FallenStarSummoner.MANA_CLOAK;
    }

    @Override
    public double getManaCostMultiplier(ItemStack stack, LivingEntity entity) {
        return MANA_COST_MULTIPLIER.get();
    }

    @Override
    public boolean canAutoUseManaPotion(ItemStack stack, LivingEntity entity) {
        return true;
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(FALLEN_STAR_DAMAGE, MANA_COST_MULTIPLIER, ATTACK_COOLDOWN);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "mana_cloak",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/mana_cloak.png"),
        Component.translatable("item.lzxnoneterraria.mana_cloak"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(
            "tooltip.lzxnoneterraria.mana_cloak_fallen_star_summon"
        ).withStyle(ChatFormatting.GRAY));
        if(MANA_COST_MULTIPLIER.get() != 1.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.mana_cost_decrease",
                EffectTooltipUtil.formatPercent(MANA_COST_MULTIPLIER.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        tooltipComponents.add(Component.translatable(
            "tooltip.lzxnoneterraria.auto_mana_potion_use"
        ).withStyle(ChatFormatting.GRAY));
    }
}
