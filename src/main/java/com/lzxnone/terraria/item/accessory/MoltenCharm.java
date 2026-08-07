package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.FireBlockImmunityModifier;
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

public class MoltenCharm extends AccessoryItem implements FireBlockImmunityModifier, LavaImmunityModifier {
    public static final ConfigInt IMMUNITY_TICKS = new ConfigInt(
        "accessory.molten_charm.immunity_ticks",
        "lava_immunity_ticks",
        140,
        0,
        1200
    );

    public MoltenCharm() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public boolean isFireBlockImmunity(ItemStack stack, LivingEntity entity) {
        return true;
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
        "molten_charm",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/molten_charm.png"),
        Component.translatable("item.lzxnoneterraria.molten_charm"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(
            "tooltip.lzxnoneterraria.fire_block_immunity"
        ).withStyle(ChatFormatting.GRAY));
        if(IMMUNITY_TICKS.get() > 0) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.lava_immunity",
                EffectTooltipUtil.formatNumber(IMMUNITY_TICKS.get() / 20.0D)
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
