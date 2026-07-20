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
    public static final DeferredHolder<MobEffect, BloodButcheredEffect> BLOOD_BUTCHERED =
            MOB_EFFECTS.register("blood_butchered", BloodButcheredEffect::new);
    public static final DeferredHolder<MobEffect, KillModeEffect> KILL_MODE =
            MOB_EFFECTS.register("kill_mode", KillModeEffect::new);
    public static final DeferredHolder<MobEffect, KillModeCooldownEffect> KILL_MODE_COOLDOWN =
            MOB_EFFECTS.register("kill_mode_cooldown", KillModeCooldownEffect::new);
    public static final DeferredHolder<MobEffect, DemonicFlamesEffect> DEMONIC_FLAMES =
            MOB_EFFECTS.register("demonic_flames", DemonicFlamesEffect::new);
}
