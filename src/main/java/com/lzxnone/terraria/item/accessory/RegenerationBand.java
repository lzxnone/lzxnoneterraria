package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.LifeRegenerationModifier;
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

public class RegenerationBand extends AccessoryItem implements LifeRegenerationModifier {
    //每秒生命回复量
    public static final ConfigDouble HEALTH_PER_SECOND = new ConfigDouble(
        "accessory.band_of_regeneration.health_per_second",
        "health_per_second",
        0.2D,
        0.0D,
        1000.0D
    );

    public RegenerationBand() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public float getHealthPerSecond(ItemStack stack, LivingEntity entity) {
        return HEALTH_PER_SECOND.get().floatValue();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(HEALTH_PER_SECOND);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "band_of_regeneration",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/band_of_regeneration.png"),
        Component.translatable("item.lzxnoneterraria.band_of_regeneration"),
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
    }
}
