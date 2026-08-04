package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.MinionCountModifier;
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
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class PygmyNecklace extends AccessoryItem implements MinionCountModifier {
    public static final ConfigInt MINION_COUNT_BONUS = new ConfigInt(
        "accessory.pygmy_necklace.minion_count_bonus",
        "minion_count_bonus",
        1,
        0,
        100
    );

    public PygmyNecklace() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public int getMinionCountBonus(ItemStack stack, LivingEntity entity) {
        return MINION_COUNT_BONUS.get();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(MINION_COUNT_BONUS);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "pygmy_necklace",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/pygmy_necklace.png"),
        Component.translatable("item.lzxnoneterraria.pygmy_necklace"),
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
    }
}