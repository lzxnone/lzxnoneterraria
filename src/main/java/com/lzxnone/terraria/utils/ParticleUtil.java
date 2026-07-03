package com.lzxnone.terraria.utils;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;


public class ParticleUtil {
    public static void addParticle(Level level, ParticleOptions particleData, Vec3 pos, double randomRadius, Vec3 speed, double randomSpeed) {
        if(level.isClientSide()) {
            level.addParticle(particleData, true,
            pos.x + (Math.random() * 2 - 1) * randomRadius, pos.y + (Math.random() * 2 - 1) * randomRadius, pos.z + (Math.random() * 2 - 1) * randomRadius,
            speed.x + (Math.random() * 2 - 1) * randomSpeed, speed.y + (Math.random() * 2 - 1) * randomSpeed, speed.z + (Math.random() * 2 - 1) * randomSpeed);
        }
    }

    public static void addParticles(Level level, ParticleOptions particleData, Vec3 pos, double randomRadius, Vec3 speed, double randomSpeed, int count) {
        if(level.isClientSide()) {
            while(count-- > 0) addParticle(level, particleData, pos, randomRadius, speed, randomSpeed);
        }
    }

    public static void addParticles(ServerLevel level, ParticleOptions particleData, Vec3 pos, Vec3 offset, double speed, int count) {
        level.sendParticles(particleData, pos.x, pos.y, pos.z, count, offset.x, offset.y, offset.z, speed);
    }
}
