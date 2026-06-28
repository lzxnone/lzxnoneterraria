package com.lzxnone.terraria.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3f;

import java.util.Arrays;

public class DustParticleOptions implements ParticleOptions {
    public final float initSize;
    public final float initRotSpeed;
    public final int initLifetime;
    public final boolean glow;
    public final Vector3f[] colors;

    public DustParticleOptions(float initSize, float initRotSpeed, int initLifetime,
                                boolean glow, Vector3f[] colors) {
        this.initSize = initSize;
        this.initRotSpeed = initRotSpeed;
        this.initLifetime = initLifetime;
        this.glow = glow;
        this.colors = colors;
    }

    public DustParticleOptions() {
        this(0.05f, 0.5f, 40, true, null);
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.DUST_PARTICLE.get();
    }

    private static final Codec<Vector3f[]> COLORS_CODEC =
        Codec.FLOAT.listOf().listOf().xmap(
            lists -> lists.stream().map(l -> new Vector3f(l.get(0), l.get(1), l.get(2))).toArray(Vector3f[]::new),
            arr -> Arrays.stream(arr).map(c -> java.util.List.of(c.x, c.y, c.z)).toList()
        );

    public static final MapCodec<DustParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.FLOAT.optionalFieldOf("initSize", 0.05f).forGetter(o -> o.initSize),
            Codec.FLOAT.optionalFieldOf("initRotSpeed", 0.5f).forGetter(o -> o.initRotSpeed),
            Codec.INT.optionalFieldOf("initLifetime", 40).forGetter(o -> o.initLifetime),
            Codec.BOOL.optionalFieldOf("glow", true).forGetter(o -> o.glow),
            COLORS_CODEC.optionalFieldOf("colors", null).forGetter(o -> o.colors)
        ).apply(instance, DustParticleOptions::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, DustParticleOptions> STREAM_CODEC =
        new StreamCodec<>() {
            @Override
            public DustParticleOptions decode(RegistryFriendlyByteBuf buf) {
                float size = buf.readFloat();
                float rotSpeed = buf.readFloat();
                int lifetime = buf.readInt();
                boolean glowFlag = buf.readBoolean();
                int colorCount = buf.readVarInt();
                Vector3f[] cols = colorCount > 0 ? new Vector3f[colorCount] : null;
                for (int i = 0; i < colorCount; i++) {
                    cols[i] = new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
                }
                return new DustParticleOptions(size, rotSpeed, lifetime, glowFlag, cols);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, DustParticleOptions opts) {
                buf.writeFloat(opts.initSize);
                buf.writeFloat(opts.initRotSpeed);
                buf.writeInt(opts.initLifetime);
                buf.writeBoolean(opts.glow);
                if (opts.colors != null) {
                    buf.writeVarInt(opts.colors.length);
                    for (Vector3f c : opts.colors) {
                        buf.writeFloat(c.x);
                        buf.writeFloat(c.y);
                        buf.writeFloat(c.z);
                    }
                } else {
                    buf.writeVarInt(0);
                }
            }
        };
}
