package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.BeeSummoner;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.TargetArmorModifier;
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

public class StingerNecklace extends AccessoryItem implements TargetArmorModifier, BeeSummoner {
    public static final ConfigDouble ARMOR_PIERCE = new ConfigDouble(
        "accessory.stinger_necklace.armor_pierce",
        "armor_pierce",
        2.0D,
        0.0D,
        100.0D
    );
    public static final ConfigInt BEE_SUMMON_MAX_COUNT = new ConfigInt(
        "accessory.stinger_necklace.bee_summon_max_count",
        "bee_summon_max_count",
        3,
        0,
        100
    );
    public static final ConfigDouble BEE_SUMMON_DAMAGE = new ConfigDouble(
        "accessory.stinger_necklace.bee_summon_damage",
        "bee_summon_damage",
        2.0D,
        0.0D,
        1000.0D
    );

    public StingerNecklace() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public double getTargetArmorReduction(ItemStack stack, LivingEntity entity, LivingEntity target) {
        return ARMOR_PIERCE.get();
    }

    @Override
    public int getBeeSummonMaxCount(ItemStack stack, LivingEntity entity) {
        return BEE_SUMMON_MAX_COUNT.get();
    }

    @Override
    public double getBeeSummonDamage(ItemStack stack, LivingEntity entity) {
        return BEE_SUMMON_DAMAGE.get();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(ARMOR_PIERCE, BEE_SUMMON_MAX_COUNT, BEE_SUMMON_DAMAGE);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "stinger_necklace",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/stinger_necklace.png"),
        Component.translatable("item.lzxnoneterraria.stinger_necklace"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(ARMOR_PIERCE.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.armor_pierce_bonus",
                EffectTooltipUtil.formatNumber(ARMOR_PIERCE.get())
            ).withStyle(ChatFormatting.GRAY));
        }
        if(BEE_SUMMON_MAX_COUNT.get() > 0) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.bee_summon"
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
