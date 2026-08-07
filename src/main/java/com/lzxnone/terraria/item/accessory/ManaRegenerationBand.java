package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
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

public class ManaRegenerationBand extends AccessoryItem implements ManaRegenerationModifier, MaxManaModifier {
    //最大魔力加成
    public static final ConfigInt MAX_MANA_BONUS = new ConfigInt(
        "accessory.mana_regeneration_band.max_mana_bonus",
        "max_mana_bonus",
        20,
        0,
        400
    );
    public static final ConfigDouble RECOVER_DELAY_MULTIPLIER = new ConfigDouble(
        "accessory.mana_regeneration_band.recover_delay_multiplier",
        "mana_regen_delay_multiplier",
        0.5D,
        0.1D,
        1.0D
    );
    public static final ConfigDouble REGEN_BONUS_SCALE = new ConfigDouble(
        "accessory.mana_regeneration_band.regen_bonus_scale",
        "mana_regen_bonus_scale",
        11.5D,
        0.0D,
        400.0D
    );
    public static final ConfigDouble REGEN_BONUS_BASE = new ConfigDouble(
        "accessory.mana_regeneration_band.regen_bonus_base",
        "mana_regen_bonus_base",
        2.875D,
        0.0D,
        400.0D
    );

    public ManaRegenerationBand() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public int getMaxManaBonus(ItemStack stack, LivingEntity entity) {
        return MAX_MANA_BONUS.get();
    }

    @Override
    public double getManaRecoverDelayMultiplier(ItemStack stack, LivingEntity entity) {
        return RECOVER_DELAY_MULTIPLIER.get();
    }

    @Override
    public double getManaRegenBonus(ItemStack stack, LivingEntity entity, int currentMana, int maxMana) {
        double ratio = maxMana > 0 ? (double) currentMana / maxMana : 0.0D;
        return REGEN_BONUS_SCALE.get() * ratio + REGEN_BONUS_BASE.get();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(MAX_MANA_BONUS, RECOVER_DELAY_MULTIPLIER, REGEN_BONUS_SCALE, REGEN_BONUS_BASE);
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
        if(MAX_MANA_BONUS.get() > 0) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.max_mana_bonus",
                EffectTooltipUtil.formatNumber(MAX_MANA_BONUS.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        if(RECOVER_DELAY_MULTIPLIER.get() < 1.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.mana_regen_delay",
                EffectTooltipUtil.formatPercent(RECOVER_DELAY_MULTIPLIER.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        if(REGEN_BONUS_SCALE.get() > 0.0D || REGEN_BONUS_BASE.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.mana_regen_boost"
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
