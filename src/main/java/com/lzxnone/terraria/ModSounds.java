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
}
