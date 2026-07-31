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
    public static final Supplier<SoundEvent> BEAM2 =
            SOUND_EVENTS.register("beam2",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "beam2")));
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
    public static final Supplier<SoundEvent> SNOW_BREAK =
            SOUND_EVENTS.register("snow_break",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "snow_break")));
    public static final Supplier<SoundEvent> SUMMON_TERRAPRISMA =
            SOUND_EVENTS.register("summon_terraprisma",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "summon_terraprisma")));
    public static final Supplier<SoundEvent> SHOT =
            SOUND_EVENTS.register("shot",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "shot")));
    public static final Supplier<SoundEvent> SHOT2 =
            SOUND_EVENTS.register("shot2",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "shot2")));
    public static final Supplier<SoundEvent> SHOT3 =
            SOUND_EVENTS.register("shot3",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "shot3")));
    public static final Supplier<SoundEvent> SHOT4 =
            SOUND_EVENTS.register("shot4",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "shot4")));
    public static final Supplier<SoundEvent> SHOT5 =
            SOUND_EVENTS.register("shot5",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "shot5")));
    public static final Supplier<SoundEvent> SHOT6 =
            SOUND_EVENTS.register("shot6",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "shot6")));
    public static final Supplier<SoundEvent> SHOT7 =
            SOUND_EVENTS.register("shot7",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "shot7")));
    public static final Supplier<SoundEvent> SHOT8 =
            SOUND_EVENTS.register("shot8",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "shot8")));
    public static final Supplier<SoundEvent> SHOT9 =
            SOUND_EVENTS.register("shot9",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "shot9")));
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
