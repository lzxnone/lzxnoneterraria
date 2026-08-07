package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.LavaImmunityModifier;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
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

public class LavaCharm extends AccessoryItem implements LavaImmunityModifier {
    public static final ConfigInt IMMUNITY_TICKS = new ConfigInt(
        "accessory.lava_charm.immunity_ticks",
        "lava_immunity_ticks",
        140,
        0,
        1200
    );

    public LavaCharm() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public int getLavaImmunityTicks(ItemStack stack, LivingEntity entity) {
        return IMMUNITY_TICKS.get();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(IMMUNITY_TICKS);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "lava_charm",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/lava_charm.png"),
        Component.translatable("item.lzxnoneterraria.lava_charm"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(IMMUNITY_TICKS.get() > 0) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.lava_immunity",
                EffectTooltipUtil.formatNumber(IMMUNITY_TICKS.get() / 20.0D)
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
