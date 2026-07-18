package com.lzxnone.terraria.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3f;

public class IronSparkParticleOptions implements ParticleOptions {
    public final float initSize;
    public final int initLifetime;
    public final float sparkLength;
    public final Vector3f color;
    public final Vector3f dir;
    public final Vector3f right;

    public IronSparkParticleOptions(float initSize, int initLifetime, float sparkLength, Vector3f color, Vector3f dir, Vector3f right) {
        this.initSize = initSize;
        this.initLifetime = initLifetime;
        this.sparkLength = sparkLength;
        this.color = color;
        this.dir = dir;
        this.right = right;
    }

    public IronSparkParticleOptions() {
        this(0.05f, 30, 4.0f, new Vector3f(1.0F, 0.8F, 0.3F), new Vector3f(0.0F, 1.0F, 0.0F), new Vector3f(1.0F, 0.0F, 0.0F));
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.IRON_SPARK_PARTICLE.get();
    }

    private static final Codec<Vector3f> VEC3_CODEC =
        Codec.FLOAT.listOf().xmap(
            l -> new Vector3f(l.get(0), l.get(1), l.get(2)),
            c -> java.util.List.of(c.x, c.y, c.z)
        );

    public static final MapCodec<IronSparkParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.FLOAT.optionalFieldOf("initSize", 0.05f).forGetter(o -> o.initSize),
            Codec.INT.optionalFieldOf("initLifetime", 40).forGetter(o -> o.initLifetime),
            Codec.FLOAT.optionalFieldOf("sparkLength", 4.0f).forGetter(o -> o.sparkLength),
            VEC3_CODEC.optionalFieldOf("color", new Vector3f(1.0F, 1.0F, 1.0F)).forGetter(o -> o.color),
            VEC3_CODEC.optionalFieldOf("dir", new Vector3f(0.0F, 1.0F, 0.0F)).forGetter(o -> o.dir),
            VEC3_CODEC.optionalFieldOf("right", new Vector3f(1.0F, 0.0F, 0.0F)).forGetter(o -> o.right)
        ).apply(instance, IronSparkParticleOptions::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, IronSparkParticleOptions> STREAM_CODEC =
        new StreamCodec<>() {
            @Override
            public IronSparkParticleOptions decode(RegistryFriendlyByteBuf buf) {
                float size = buf.readFloat();
                int lifetime = buf.readInt();
                float length = buf.readFloat();
                Vector3f col = new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
                Vector3f d = new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
                Vector3f r = new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
                return new IronSparkParticleOptions(size, lifetime, length, col, d, r);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, IronSparkParticleOptions opts) {
                buf.writeFloat(opts.initSize);
                buf.writeInt(opts.initLifetime);
                buf.writeFloat(opts.sparkLength);
                buf.writeFloat(opts.color.x);
                buf.writeFloat(opts.color.y);
                buf.writeFloat(opts.color.z);
                buf.writeFloat(opts.dir.x);
                buf.writeFloat(opts.dir.y);
                buf.writeFloat(opts.dir.z);
                buf.writeFloat(opts.right.x);
                buf.writeFloat(opts.right.y);
                buf.writeFloat(opts.right.z);
            }
        };
}
