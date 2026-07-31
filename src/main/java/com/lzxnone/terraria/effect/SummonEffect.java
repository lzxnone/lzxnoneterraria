package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SummonEffect extends MobEffect {
    public static final ConfigInt SUMMON_COUNT_PER_LEVEL = new ConfigInt("effect.summon.summon_count_per_level", "summon_summon_count_per_level", 1, 0, 10);

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(SUMMON_COUNT_PER_LEVEL);
        }
    };

    public static int getSummonCountPerLevel() {
        return SUMMON_COUNT_PER_LEVEL.get();
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "summon",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/mob_effect/summon.png"),
        Component.translatable("effect.lzxnoneterraria.summon"),
        CONFIG_DATA
    );

    public SummonEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x34D95A);
    }
}
