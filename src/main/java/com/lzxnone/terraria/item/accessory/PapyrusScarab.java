package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.MinionCountModifier;
import com.lzxnone.terraria.item.effect.MinionKnockbackModifier;
import com.lzxnone.terraria.item.effect.SummonDamageModifier;
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
import java.util.Locale;

public class PapyrusScarab extends AccessoryItem implements MinionCountModifier, SummonDamageModifier, MinionKnockbackModifier {
    public static final ConfigInt MINION_COUNT_BONUS = new ConfigInt(
        "accessory.papyrus_scarab.minion_count_bonus",
        "minion_count_bonus",
        1,
        0,
        100
    );
    public static final ConfigDouble SUMMON_DAMAGE_MULTIPLIER = new ConfigDouble(
        "accessory.papyrus_scarab.summon_damage_multiplier",
        "summon_damage_multiplier",
        1.15D,
        1.0D,
        10.0D
    );
    public static final ConfigDouble MINION_KNOCKBACK_MULTIPLIER = new ConfigDouble(
        "accessory.papyrus_scarab.minion_knockback_multiplier",
        "minion_knockback_multiplier",
        1.25D,
        1.0D,
        10.0D
    );

    public PapyrusScarab() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public int getMinionCountBonus(ItemStack stack, LivingEntity entity) {
        return MINION_COUNT_BONUS.get();
    }

    @Override
    public double getSummonDamageMultiplier(ItemStack stack, LivingEntity entity) {
        return SUMMON_DAMAGE_MULTIPLIER.get();
    }

    @Override
    public double getMinionKnockbackMultiplier(ItemStack stack, LivingEntity entity) {
        return MINION_KNOCKBACK_MULTIPLIER.get();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(MINION_COUNT_BONUS, SUMMON_DAMAGE_MULTIPLIER, MINION_KNOCKBACK_MULTIPLIER);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "papyrus_scarab",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/papyrus_scarab.png"),
        Component.translatable("item.lzxnoneterraria.papyrus_scarab"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(MINION_COUNT_BONUS.get() != 0) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.minion_slot_add",
                MINION_COUNT_BONUS.get()
            ).withStyle(ChatFormatting.GRAY));
        }
        if(SUMMON_DAMAGE_MULTIPLIER.get() != 1.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.summon_damage_increase",
                EffectTooltipUtil.formatPercent(SUMMON_DAMAGE_MULTIPLIER.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        if(MINION_KNOCKBACK_MULTIPLIER.get() != 1.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.minion_knockback_increase",
                EffectTooltipUtil.formatPercent(MINION_KNOCKBACK_MULTIPLIER.get())
            ).withStyle(ChatFormatting.GRAY));
        }
    }

    private static String formatPercentBonus(double multiplier) {
        double bonus = Math.max(0.0D, (multiplier - 1.0D) * 100.0D);
        if(Math.abs(bonus - Math.rint(bonus)) < 1.0E-6D) return Integer.toString((int)Math.rint(bonus));
        String formatted = String.format(Locale.ROOT, "%.2f", bonus);
        while(formatted.endsWith("0")) formatted = formatted.substring(0, formatted.length() - 1);
        if(formatted.endsWith(".")) formatted = formatted.substring(0, formatted.length() - 1);
        return formatted;
    }
}