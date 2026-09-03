package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.DamageImmunityModifier;
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
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class MysticArtsSash extends AccessoryItem implements MaxManaModifier, ManaRegenerationModifier, LifeRegenerationModifier, DamageImmunityModifier {
    public static final ConfigDouble DAMAGE_IMMUNITY_CHANCE = new ConfigDouble(
        "accessory.mystic_arts_sash.damage_immunity",
        "damage_immunity",
        0.1D,
        0.0D,
        1.0D
    );
    public static final ConfigInt MAX_MANA_BONUS = new ConfigInt(
        "accessory.mystic_arts_sash.max_mana_bonus",
        "max_mana_bonus",
        40,
        0,
        400
    );
    public static final ConfigDouble MANA_REGEN_BONUS = new ConfigDouble(
        "accessory.mystic_arts_sash.mana_regen_bonus",
        "mana_regen_bonus",
        60.0D,
        0.0D,
        1000.0D
    );
    public static final ConfigDouble HEALTH_PER_SECOND = new ConfigDouble(
        "accessory.mystic_arts_sash.health_per_second",
        "health_per_second",
        0.2D,
        0.0D,
        1000.0D
    );

    public MysticArtsSash() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public double getDamageImmunityChance(ItemStack stack, LivingEntity entity) {
        return DAMAGE_IMMUNITY_CHANCE.get();
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
            ConfigStruct.loadAll(DAMAGE_IMMUNITY_CHANCE, MAX_MANA_BONUS, MANA_REGEN_BONUS, HEALTH_PER_SECOND);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "mystic_arts_sash",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/mystic_arts_sash.png"),
        Component.translatable("item.lzxnoneterraria.mystic_arts_sash"),
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
        if(DAMAGE_IMMUNITY_CHANCE.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.damage_immunity",
                EffectTooltipUtil.formatNumber(DAMAGE_IMMUNITY_CHANCE.get() * 100.0D)
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
