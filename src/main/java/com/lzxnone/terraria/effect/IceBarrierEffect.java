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

public class IceBarrierEffect extends MobEffect {
    public static final ConfigFloat DAMAGE_REDUCTION = new ConfigFloat(
        "effect.ice_barrier.damage_reduction",
        "ice_barrier_damage_reduction_per_level",
        0.25f,
        0.0f,
        0.95f
    );

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DAMAGE_REDUCTION);
        }
    };

    public static float getDamageReduction() {
        return DAMAGE_REDUCTION.get();
    }

    public static double getDamageMultiplier(int amplifier) {
        return Math.pow(1.0D - DAMAGE_REDUCTION.get(), amplifier + 1);
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "ice_barrier",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/mob_effect/ice_barrier.png"),
        Component.translatable("effect.lzxnoneterraria.ice_barrier"),
        CONFIG_DATA
    );

    public IceBarrierEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x87CEEB);
    }
}
