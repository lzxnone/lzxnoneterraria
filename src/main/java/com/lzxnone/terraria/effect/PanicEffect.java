package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class PanicEffect extends MobEffect {
    public static final ConfigDouble SPEED_BOOST = new ConfigDouble(
        "effect.panic.speed_boost",
        "panic_speed_boost",
        1.0D,
        0.0D,
        10.0D
    );

    public PanicEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF6B4A);
        addAttributeModifier(
            Attributes.MOVEMENT_SPEED,
            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "panic_effect_movement_speed"),
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
            level -> SPEED_BOOST.get()
        );
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(SPEED_BOOST);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "panic",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/mob_effect/panic.png"),
        Component.translatable("effect.lzxnoneterraria.panic"),
        CONFIG_DATA
    );
}
