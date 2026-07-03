package com.lzxnone.terraria.utils;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SoundUtil {
    public static void playClientSound(Player player, SoundEvent event) {
        if(player.level().isClientSide()) {
            player.playSound(event, 4.0f, 1.0f);
        }
    }

    public static void playClientSound(Player player, SoundEvent event, float volume, float pitch) {
        if(player.level().isClientSide()) {
            player.playSound(event, volume, pitch);
        }
    }

    public static void playServerSound(Level level, SoundEvent event, Vec3 pos) {
        if(!level.isClientSide()) {
            level.playSound(
                null,
                pos.x, pos.y, pos.z,
                event,
                SoundSource.PLAYERS,
                4.0f,
                1.0f
            );
        }
    }

    public static void playServerSound(Level level, SoundEvent event, Vec3 pos, float volume, float pitch) {
        if(!level.isClientSide()) {
            level.playSound(
                null,
                pos.x, pos.y, pos.z,
                event,
                SoundSource.PLAYERS,
                volume,
                pitch
            );
        }
    }

    public static void playServerSound(Entity entity, SoundEvent event, float volume, float pitch) {
        if(!entity.level().isClientSide()) {
            entity.level().playSound(
                null,
                entity,
                event,
                SoundSource.PLAYERS,
                volume,
                pitch
            );
        }
    }
}
