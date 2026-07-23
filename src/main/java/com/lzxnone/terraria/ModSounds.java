package com.lzxnone.terraria;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, LzxnoneTerraria.MODID);

    public static final Supplier<SoundEvent> BEAM =
            SOUND_EVENTS.register("beam",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "beam")));
    public static final Supplier<SoundEvent> MAX_MANA =
            SOUND_EVENTS.register("max_mana",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "max_mana")));
    public static final Supplier<SoundEvent> WAVE =
            SOUND_EVENTS.register("wave",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "wave")));
    public static final Supplier<SoundEvent> WAVE2 =
            SOUND_EVENTS.register("wave2",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "wave2")));
    public static final Supplier<SoundEvent> WAVE3 =
            SOUND_EVENTS.register("wave3",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "wave3")));
    public static final Supplier<SoundEvent> STAR_FALL =
            SOUND_EVENTS.register("star_fall",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "star_fall")));
    public static final Supplier<SoundEvent> STAR_COLLIDE =
            SOUND_EVENTS.register("star_collide",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "star_collide")));
    public static final Supplier<SoundEvent> BOOM =
            SOUND_EVENTS.register("boom",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "boom")));
    public static final Supplier<SoundEvent> CAT =
            SOUND_EVENTS.register("cat",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "cat")));
    public static final Supplier<SoundEvent> CAT2 =
            SOUND_EVENTS.register("cat2",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "cat2")));
    public static final Supplier<SoundEvent> EXPLOSIVE_TRAP_EXPLODE0 =
            SOUND_EVENTS.register("explosive_trap_explode0",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "explosive_trap_explode0")));
    public static final Supplier<SoundEvent> EXPLOSIVE_TRAP_EXPLODE1 =
            SOUND_EVENTS.register("explosive_trap_explode1",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "explosive_trap_explode1")));
    public static final Supplier<SoundEvent> EXPLOSIVE_TRAP_EXPLODE2 =
            SOUND_EVENTS.register("explosive_trap_explode2",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "explosive_trap_explode2")));
    public static final Supplier<SoundEvent> DIG =
            SOUND_EVENTS.register("dig",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "dig")));
    public static final Supplier<SoundEvent> DEMON_SWORD_SWING =
            SOUND_EVENTS.register("demon_sword_swing",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "demon_sword_swing")));
    public static final Supplier<SoundEvent> DEMON_SWORD_IMPACT =
            SOUND_EVENTS.register("demon_sword_impact",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "demon_sword_impact")));
    public static final Supplier<SoundEvent> DEMON_SWORD_INSANE_IMPACT =
            SOUND_EVENTS.register("demon_sword_insane_impact",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "demon_sword_insane_impact")));
    public static final Supplier<SoundEvent> DEMON_SWORD_KILL_MODE =
            SOUND_EVENTS.register("demon_sword_kill_mode",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "demon_sword_kill_mode")));
    public static final Supplier<SoundEvent> DEMON_SWORD_KILL_MODE_OFF_COOLDOWN =
            SOUND_EVENTS.register("demon_sword_kill_mode_off_cooldown",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "demon_sword_kill_mode_off_cooldown")));
    public static final Supplier<SoundEvent> DEMON_SWORD_FINAL_STRIKE =
            SOUND_EVENTS.register("demon_sword_final_strike",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "demon_sword_final_strike")));
    public static final Supplier<SoundEvent> DEMON_SWORD_STRONG_IMPACT =
            SOUND_EVENTS.register("demon_sword_strong_impact",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "demon_sword_strong_impact")));
}
