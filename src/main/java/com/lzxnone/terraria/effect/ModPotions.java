package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.LzxnoneTerraria;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS =
        DeferredRegister.create(BuiltInRegistries.POTION, LzxnoneTerraria.MODID);

    public static final DeferredHolder<Potion, Potion> SHORT_SUMMON_0 =
        registerSummonPotion("short_summon_0", 6000, 0);
    public static final DeferredHolder<Potion, Potion> MEDIUM_SUMMON_0 =
        registerSummonPotion("medium_summon_0", 36000, 0);
    public static final DeferredHolder<Potion, Potion> LONG_SUMMON_0 =
        registerSummonPotion("long_summon_0", 216000, 0);
    public static final DeferredHolder<Potion, Potion> SHORT_SUMMON_1 =
        registerSummonPotion("short_summon_1", 6000, 1);
    public static final DeferredHolder<Potion, Potion> MEDIUM_SUMMON_1 =
        registerSummonPotion("medium_summon_1", 36000, 1);
    public static final DeferredHolder<Potion, Potion> LONG_SUMMON_1 =
        registerSummonPotion("long_summon_1", 216000, 1);
    public static final DeferredHolder<Potion, Potion> SHORT_SUMMON_2 =
        registerSummonPotion("short_summon_2", 6000, 2);
    public static final DeferredHolder<Potion, Potion> MEDIUM_SUMMON_2 =
        registerSummonPotion("medium_summon_2", 36000, 2);
    public static final DeferredHolder<Potion, Potion> LONG_SUMMON_2 =
        registerSummonPotion("long_summon_2", 216000, 2);
    public static final DeferredHolder<Potion, Potion> SHORT_SUMMON_3 =
        registerSummonPotion("short_summon_3", 6000, 3);
    public static final DeferredHolder<Potion, Potion> MEDIUM_SUMMON_3 =
        registerSummonPotion("medium_summon_3", 36000, 3);
    public static final DeferredHolder<Potion, Potion> LONG_SUMMON_3 =
        registerSummonPotion("long_summon_3", 216000, 3);
    public static final DeferredHolder<Potion, Potion> SHORT_SUMMON_4 =
        registerSummonPotion("short_summon_4", 6000, 4);
    public static final DeferredHolder<Potion, Potion> MEDIUM_SUMMON_4 =
        registerSummonPotion("medium_summon_4", 36000, 4);
    public static final DeferredHolder<Potion, Potion> LONG_SUMMON_4 =
        registerSummonPotion("long_summon_4", 216000, 4);

    private static DeferredHolder<Potion, Potion> registerSummonPotion(String name, int duration, int amplifier) {
        return POTIONS.register(name, () -> new Potion("summon", new MobEffectInstance(ModEffects.SUMMON, duration, amplifier)));
    }
}
