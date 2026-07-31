package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class MidasEffect extends MobEffect {
    public static final ConfigFloat DROP_CHANCE = new ConfigFloat("effect.midas.drop_chance", "midas_drop_chance", 0.25f, 0.0f, 1.0f);

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DROP_CHANCE);
        }
    };

    public static float getDropChance() {
        return DROP_CHANCE.get();
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "midas",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/mob_effect/midas.png"),
        Component.translatable("effect.lzxnoneterraria.midas"),
        CONFIG_DATA
    );

    public MidasEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFD700);
    }

}
