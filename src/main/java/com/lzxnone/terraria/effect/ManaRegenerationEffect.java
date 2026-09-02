package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class ManaRegenerationEffect extends MobEffect {
    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {}
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "mana_regeneration",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/mob_effect/mana_regeneration.png"),
        Component.translatable("effect.lzxnoneterraria.mana_regeneration"),
        CONFIG_DATA
    );

    public ManaRegenerationEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x0099FF);
    }
}
