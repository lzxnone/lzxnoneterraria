package com.lzxnone.terraria.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3f;

public class CircleParticleOptions implements ParticleOptions {
    public final float initSize;
    public final int initLifetime;
    public final Vector3f color;

    public CircleParticleOptions(float initSize, int initLifetime, Vector3f color) {
        this.initSize = initSize;
        this.initLifetime = initLifetime;
        this.color = color;
    }

    public CircleParticleOptions() {
        this(0.25f, 20, new Vector3f(1.0F, 1.0F, 1.0F));
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.CIRCLE_PARTICLE.get();
    }

    private static final Codec<Vector3f> VEC3_CODEC =
        Codec.FLOAT.listOf().xmap(
            l -> new Vector3f(l.get(0), l.get(1), l.get(2)),
            c -> java.util.List.of(c.x, c.y, c.z)
        );

    public static final MapCodec<CircleParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.FLOAT.optionalFieldOf("initSize", 0.25f).forGetter(o -> o.initSize),
            Codec.INT.optionalFieldOf("initLifetime", 20).forGetter(o -> o.initLifetime),
            VEC3_CODEC.optionalFieldOf("color", new Vector3f(1.0F, 1.0F, 1.0F)).forGetter(o -> o.color)
        ).apply(instance, CircleParticleOptions::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CircleParticleOptions> STREAM_CODEC =
        new StreamCodec<>() {
            @Override
            public CircleParticleOptions decode(RegistryFriendlyByteBuf buf) {
                float size = buf.readFloat();
                int lifetime = buf.readInt();
                Vector3f col = new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
                return new CircleParticleOptions(size, lifetime, col);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, CircleParticleOptions opts) {
                buf.writeFloat(opts.initSize);
                buf.writeInt(opts.initLifetime);
                buf.writeFloat(opts.color.x);
                buf.writeFloat(opts.color.y);
                buf.writeFloat(opts.color.z);
            }
        };
}
