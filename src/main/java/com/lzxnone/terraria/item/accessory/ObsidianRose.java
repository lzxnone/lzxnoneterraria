package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.LavaDamageReductionModifier;
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

public class ObsidianRose extends AccessoryItem implements LavaDamageReductionModifier {
    public static final ConfigDouble DAMAGE_REDUCTION = new ConfigDouble(
        "accessory.obsidian_rose.damage_reduction",
        "lava_damage_reduction",
        0.5D,
        0.0D,
        1.0D
    );

    public ObsidianRose() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public double getLavaDamageReduction(ItemStack stack, LivingEntity entity) {
        return DAMAGE_REDUCTION.get();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DAMAGE_REDUCTION);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "obsidian_rose",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/obsidian_rose.png"),
        Component.translatable("item.lzxnoneterraria.obsidian_rose"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(DAMAGE_REDUCTION.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.lava_damage_reduction",
                EffectTooltipUtil.formatPercent(DAMAGE_REDUCTION.get())
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
