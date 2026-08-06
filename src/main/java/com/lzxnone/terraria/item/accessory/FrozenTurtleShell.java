package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.effect.IceBarrierEffect;
import com.lzxnone.terraria.item.effect.EffectTooltipUtil;
import com.lzxnone.terraria.item.effect.IceBarrierModifier;
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

public class FrozenTurtleShell extends AccessoryItem implements IceBarrierModifier {
    public static final ConfigDouble MAX_HEALTH_RATIO = new ConfigDouble(
        "accessory.frozen_turtle_shell.max_health_ratio",
        "max_health_ratio",
        0.5D,
        0.0D,
        1.0D
    );

    public FrozenTurtleShell() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public boolean applyIceBarrier(ItemStack stack, LivingEntity entity) {
        return entity.getHealth() / entity.getMaxHealth() < MAX_HEALTH_RATIO.get();
    }

    @Override
    public double getMaxHealthRatio(ItemStack stack, LivingEntity entity) {
        return MAX_HEALTH_RATIO.get();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(MAX_HEALTH_RATIO);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "frozen_turtle_shell",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/frozen_turtle_shell.png"),
        Component.translatable("item.lzxnoneterraria.frozen_turtle_shell"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(MAX_HEALTH_RATIO.get() > 0.0D) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.ice_barrier",
                EffectTooltipUtil.formatNumber(MAX_HEALTH_RATIO.get() * 100.0D),
                EffectTooltipUtil.formatNumber(IceBarrierEffect.getDamageReduction() * 100.0D)
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
