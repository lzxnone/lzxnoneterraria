package com.lzxnone.terraria.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3f;

public class ZenithTrailParticleOptions implements ParticleOptions {
    public final float initSize;
    public final int initLifetime;
    public final boolean glow;
    public final Vector3f color;
    public final Vector3f up;
    public final Vector3f right;
    public final int angle;

    public ZenithTrailParticleOptions(float initSize, int initLifetime, boolean glow, Vector3f color, Vector3f up, Vector3f right, int angle) {
        this.initSize = initSize;
        this.initLifetime = initLifetime;
        this.glow = glow;
        this.color = color;
        this.up = up;
        this.right = right;
        this.angle = angle;
    }

    public ZenithTrailParticleOptions() {
        this(0.05f, 40, true, new Vector3f(1.0F, 1.0F, 1.0F), new Vector3f(1.0F, 0.0F, 0.0F), new Vector3f(0.0F, 1.0F, 0.0F), 0);
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.ZENITH_TRAIL_PARTICLE.get();
    }

    private static final Codec<Vector3f> COLOR_CODEC =
        Codec.FLOAT.listOf().xmap(
            l -> new Vector3f(l.get(0), l.get(1), l.get(2)),
            c -> java.util.List.of(c.x, c.y, c.z)
        );

    public static final MapCodec<ZenithTrailParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.FLOAT.optionalFieldOf("initSize", 0.05f).forGetter(o -> o.initSize),
            Codec.INT.optionalFieldOf("initLifetime", 40).forGetter(o -> o.initLifetime),
            Codec.BOOL.optionalFieldOf("glow", true).forGetter(o -> o.glow),
            COLOR_CODEC.optionalFieldOf("color", new Vector3f(1.0F, 1.0F, 1.0F)).forGetter(o -> o.color),
            COLOR_CODEC.optionalFieldOf("up", new Vector3f(0.0F, 1.0F, 0.0F)).forGetter(o -> o.up),
            COLOR_CODEC.optionalFieldOf("right", new Vector3f(1.0F, 0.0F, 0.0F)).forGetter(o -> o.right),
            Codec.INT.optionalFieldOf("angle", 0).forGetter(o -> o.angle)
        ).apply(instance, ZenithTrailParticleOptions::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ZenithTrailParticleOptions> STREAM_CODEC =
        new StreamCodec<>() {
            @Override
            public ZenithTrailParticleOptions decode(RegistryFriendlyByteBuf buf) {
                float size = buf.readFloat();
                int lifetime = buf.readInt();
                boolean glowFlag = buf.readBoolean();
                Vector3f col = new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
                Vector3f up = new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
                Vector3f right = new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
                int angle = buf.readInt();
                return new ZenithTrailParticleOptions(size, lifetime, glowFlag, col, up, right, angle);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, ZenithTrailParticleOptions opts) {
                buf.writeFloat(opts.initSize);
                buf.writeInt(opts.initLifetime);
                buf.writeBoolean(opts.glow);
                buf.writeFloat(opts.color.x);
                buf.writeFloat(opts.color.y);
                buf.writeFloat(opts.color.z);
                buf.writeFloat(opts.up.x);
                buf.writeFloat(opts.up.y);
                buf.writeFloat(opts.up.z);
                buf.writeFloat(opts.right.x);
                buf.writeFloat(opts.right.y);
                buf.writeFloat(opts.right.z);
                buf.writeInt(opts.angle);
            }
        };
}
