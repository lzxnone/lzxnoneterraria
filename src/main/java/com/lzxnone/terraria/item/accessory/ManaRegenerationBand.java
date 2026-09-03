package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.LifeRegenerationModifier;
import com.lzxnone.terraria.item.effect.ManaRegenerationModifier;
import com.lzxnone.terraria.item.effect.MaxManaModifier;
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

public class ManaRegenerationBand extends AccessoryItem implements ManaRegenerationModifier, MaxManaModifier, LifeRegenerationModifier {
    //最大魔力加成
    public static final ConfigInt MAX_MANA_BONUS = new ConfigInt(
        "accessory.mana_regeneration_band.max_mana_bonus",
        "max_mana_bonus",
        40,
        0,
        400
    );
    //魔力再生奖励
    public static final ConfigDouble MANA_REGEN_BONUS = new ConfigDouble(
        "accessory.mana_regeneration_band.mana_regen_bonus",
        "mana_regen_bonus",
        60.0D,
        0.0D,
        1000.0D
    );
    //每秒生命回复量
    public static final ConfigDouble HEALTH_PER_SECOND = new ConfigDouble(
        "accessory.mana_regeneration_band.health_per_second",
        "health_per_second",
        0.2D,
        0.0D,
        1000.0D
    );

    public ManaRegenerationBand() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public int getMaxManaBonus(ItemStack stack, LivingEntity entity) {
        return MAX_MANA_BONUS.get();
    }

    @Override
    public double getManaRegenBonus(ItemStack stack, LivingEntity entity) {
        return MANA_REGEN_BONUS.get();
    }

    @Override
    public float getHealthPerSecond(ItemStack stack, LivingEntity entity) {
        return HEALTH_PER_SECOND.get().floatValue();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(MAX_MANA_BONUS, MANA_REGEN_BONUS, HEALTH_PER_SECOND);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "mana_regeneration_band",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/mana_regeneration_band.png"),
        Component.translatable("item.lzxnoneterraria.mana_regeneration_band"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(HEALTH_PER_SECOND.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.life_regen",
                EffectTooltipUtil.formatNumber(HEALTH_PER_SECOND.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        if(MAX_MANA_BONUS.get() > 0) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.max_mana_bonus",
                EffectTooltipUtil.formatNumber(MAX_MANA_BONUS.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        if(MANA_REGEN_BONUS.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.mana_regen_bonus",
                EffectTooltipUtil.formatNumber(MANA_REGEN_BONUS.get())
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
