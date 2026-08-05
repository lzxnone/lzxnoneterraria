package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.BeeSummoner;
import com.lzxnone.terraria.item.effect.FallenStarSummoner;
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

public class BeeCloak extends AccessoryItem implements FallenStarSummoner, BeeSummoner {
    public static final ConfigDouble FALLEN_STAR_DAMAGE = new ConfigDouble(
        "accessory.bee_cloak.fallen_star_damage",
        "fallen_star_damage",
        7.0D,
        0.0D,
        8388600.0D
    );
    public static final ConfigInt BEE_SUMMON_MAX_COUNT = new ConfigInt(
        "accessory.bee_cloak.bee_summon_max_count",
        "bee_summon_max_count",
        3,
        0,
        100
    );
    public static final ConfigDouble BEE_SUMMON_DAMAGE = new ConfigDouble(
        "accessory.bee_cloak.bee_summon_damage",
        "bee_summon_damage",
        2.0D,
        0.0D,
        1000.0D
    );

    public BeeCloak() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public boolean summonThreeFallenStar(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public double getFallenStarDamage(ItemStack stack, LivingEntity entity) {
        return FALLEN_STAR_DAMAGE.get();
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
            ConfigStruct.loadAll(FALLEN_STAR_DAMAGE, BEE_SUMMON_MAX_COUNT, BEE_SUMMON_DAMAGE);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "bee_cloak",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/bee_cloak.png"),
        Component.translatable("item.lzxnoneterraria.bee_cloak"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(
            "tooltip.lzxnoneterraria.fallen_star_summon"
        ).withStyle(ChatFormatting.GRAY));
        if(BEE_SUMMON_MAX_COUNT.get() > 0) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.bee_summon"
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
