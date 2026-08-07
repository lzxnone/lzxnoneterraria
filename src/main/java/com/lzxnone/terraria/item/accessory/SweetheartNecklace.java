package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.effect.BeeSummoner;
import com.lzxnone.terraria.item.effect.PanicModifier;
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

public class SweetheartNecklace extends AccessoryItem implements PanicModifier, BeeSummoner {
    //恐慌持续时间（刻）
    public static final ConfigInt DURATION = new ConfigInt(
        "accessory.sweetheart_necklace.duration",
        "panic_duration",
        160,
        0,
        1200
    );
    //受击释放的蜜蜂数量
    public static final ConfigInt BEE_SUMMON_MAX_COUNT = new ConfigInt(
        "accessory.sweetheart_necklace.bee_summon_max_count",
        "bee_summon_max_count",
        3,
        0,
        100
    );
    public static final ConfigDouble BEE_SUMMON_DAMAGE = new ConfigDouble(
        "accessory.sweetheart_necklace.bee_summon_damage",
        "bee_summon_damage",
        2.0D,
        0.0D,
        1000.0D
    );

    public SweetheartNecklace() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public boolean canApplyPanic(ItemStack stack, LivingEntity entity) {
        return true;
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
            ConfigStruct.loadAll(DURATION, BEE_SUMMON_MAX_COUNT, BEE_SUMMON_DAMAGE);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "sweetheart_necklace",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/sweetheart_necklace.png"),
        Component.translatable("item.lzxnoneterraria.sweetheart_necklace"),
        CONFIG_DATA
    );

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(BEE_SUMMON_MAX_COUNT.get() > 0) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.bee_summon"
            ).withStyle(ChatFormatting.GRAY));
        }
        tooltipComponents.add(Component.translatable(
            "tooltip.lzxnoneterraria.panic"
        ).withStyle(ChatFormatting.GRAY));
    }
}
