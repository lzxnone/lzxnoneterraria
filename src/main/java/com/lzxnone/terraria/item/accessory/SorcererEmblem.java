package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.MagicDamageModifier;
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

public class SorcererEmblem extends AccessoryItem implements MagicDamageModifier {
    public static final ConfigDouble MAGIC_DAMAGE_MULTIPLIER = new ConfigDouble(
        "accessory.sorcerer_emblem.magic_damage_multiplier",
        "magic_damage_multiplier",
        1.15D,
        1.0D,
        10.0D
    );

    public SorcererEmblem() {
        super(new Item.Properties().stacksTo(1));
    }

    public double getMagicDamageModifier(ItemStack stack, LivingEntity entity) {
        return MAGIC_DAMAGE_MULTIPLIER.get();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(MAGIC_DAMAGE_MULTIPLIER);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "sorcerer_emblem",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/sorcerer_emblem.png"),
        Component.translatable("item.lzxnoneterraria.sorcerer_emblem"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(MAGIC_DAMAGE_MULTIPLIER.get() != 1.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.magic_damage_increase",
                EffectTooltipUtil.formatPercent(MAGIC_DAMAGE_MULTIPLIER.get())
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
