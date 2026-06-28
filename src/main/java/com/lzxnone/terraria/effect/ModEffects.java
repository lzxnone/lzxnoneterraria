package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.LzxnoneTerraria;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, LzxnoneTerraria.MODID);

    public static final DeferredHolder<MobEffect, ConfusedEffect> CONFUSED =
            MOB_EFFECTS.register("confused", ConfusedEffect::new);
}
