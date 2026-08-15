package com.lzxnone.terraria.utils;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;


public class ParticleUtil {
    private static final double DEFAULT_PARTICLE_BROADCAST_RADIUS = 128.0D;

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
        addParticles(level, particleData, pos, offset, speed, count, DEFAULT_PARTICLE_BROADCAST_RADIUS);
    }

    public static void addParticles(ServerLevel level, ParticleOptions particleData, Vec3 pos, Vec3 offset, double speed, int count, double radius) {
        ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(
            particleData, false, pos.x, pos.y, pos.z,
            (float) offset.x, (float) offset.y, (float) offset.z, (float) speed, count
        );
        level.getServer().getPlayerList().broadcast(null, pos.x, pos.y, pos.z, radius, level.dimension(), packet);
    }
}
