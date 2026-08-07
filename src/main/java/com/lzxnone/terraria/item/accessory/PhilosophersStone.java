package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.ManaSicknessDurationModifier;
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

public class PhilosophersStone extends AccessoryItem implements ManaSicknessDurationModifier {
    //耐魔性持续时间比例（0.75 = 降低 25%）
    public static final ConfigDouble DURATION_MULTIPLIER = new ConfigDouble(
        "accessory.philosophers_stone.duration_multiplier",
        "mana_sickness_duration_multiplier",
        0.75D,
        0.01D,
        1.0D
    );

    public PhilosophersStone() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public double getManaSicknessDurationMultiplier(ItemStack stack, LivingEntity entity) {
        return DURATION_MULTIPLIER.get();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DURATION_MULTIPLIER);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "philosophers_stone",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/philosophers_stone.png"),
        Component.translatable("item.lzxnoneterraria.philosophers_stone"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(DURATION_MULTIPLIER.get() < 1.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.mana_sickness_duration",
                EffectTooltipUtil.formatPercent(DURATION_MULTIPLIER.get())
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
