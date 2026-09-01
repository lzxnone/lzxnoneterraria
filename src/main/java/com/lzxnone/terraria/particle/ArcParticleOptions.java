package com.lzxnone.terraria.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3f;

public class ArcParticleOptions implements ParticleOptions {
    public final float size;
    public final int lifetime;
    public final Vector3f color;
    public final float length;

    public ArcParticleOptions(float size, int lifetime, Vector3f color, float length) {
        this.size = size;
        this.lifetime = lifetime;
        this.color = color;
        this.length = length;
    }

    public ArcParticleOptions() {
        this(0.08f, 15, new Vector3f(1.0F, 0.2F, 0.2F), 1.0f);
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.ARC_PARTICLE.get();
    }

    private static final Codec<Vector3f> COLOR_CODEC = Codec.FLOAT.listOf().xmap(
        list -> new Vector3f(list.get(0), list.get(1), list.get(2)),
        vec -> java.util.List.of(vec.x, vec.y, vec.z)
    );

    public static final MapCodec<ArcParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.FLOAT.optionalFieldOf("size", 0.08f).forGetter(o -> o.size),
            Codec.INT.optionalFieldOf("lifetime", 15).forGetter(o -> o.lifetime),
            COLOR_CODEC.optionalFieldOf("color", new Vector3f(1.0f, 0.2f, 0.2f)).forGetter(o -> o.color),
            Codec.FLOAT.optionalFieldOf("length", 1.0f).forGetter(o -> o.length)
        ).apply(instance, ArcParticleOptions::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ArcParticleOptions> STREAM_CODEC =
        new StreamCodec<>() {
            @Override
            public ArcParticleOptions decode(RegistryFriendlyByteBuf buf) {
                float size = buf.readFloat();
                int lifetime = buf.readInt();
                Vector3f color = new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
                float length = buf.readFloat();
                return new ArcParticleOptions(size, lifetime, color, length);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, ArcParticleOptions opts) {
                buf.writeFloat(opts.size);
                buf.writeInt(opts.lifetime);
                buf.writeFloat(opts.color.x);
                buf.writeFloat(opts.color.y);
                buf.writeFloat(opts.color.z);
                buf.writeFloat(opts.length);
            }
        };
}
