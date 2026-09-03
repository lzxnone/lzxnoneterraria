package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.FallenStarSummoner;
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

public class StarVeil extends AccessoryItem implements FallenStarSummoner, InvulnerableTimeModifier {
    public static final ConfigDouble FALLEN_STAR_DAMAGE = new ConfigDouble(
        "accessory.star_veil.fallen_star_damage",
        "fallen_star_damage",
        7.0D,
        0.0D,
        8388600.0D
    );
    public static final ConfigDouble INVULNERABLE_TIME_MULTIPLIER = new ConfigDouble(
        "accessory.star_veil.invulnerable_time_multiplier",
        "invulnerable_time_multiplier",
        2.0D,
        1.0D,
        10.0D
    );

    public StarVeil() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public String getStar(ItemStack stack, LivingEntity entity) {
        return FallenStarSummoner.STAR_VEIL;
    }

    @Override
    public double getInvulnerableTimeMultiplier(ItemStack stack, LivingEntity entity) {
        return INVULNERABLE_TIME_MULTIPLIER.get();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(FALLEN_STAR_DAMAGE, INVULNERABLE_TIME_MULTIPLIER);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "star_veil",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/star_veil.png"),
        Component.translatable("item.lzxnoneterraria.star_veil"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(
            "tooltip.lzxnoneterraria.fallen_star_summon"
        ).withStyle(ChatFormatting.GRAY));
        if(INVULNERABLE_TIME_MULTIPLIER.get() != 1.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.invulnerable_time_bonus",
                EffectTooltipUtil.formatNumber(INVULNERABLE_TIME_MULTIPLIER.get())
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
