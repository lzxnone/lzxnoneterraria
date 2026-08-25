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

public class StarlightParticleOptions implements ParticleOptions {
    public final float initSize;
    public final float initRotSpeed;
    public final int initLifetime;
    public final Vector3f[] colors;

    public StarlightParticleOptions(float initSize, float initRotSpeed, int initLifetime,
                                    Vector3f[] colors) {
        this.initSize = initSize;
        this.initRotSpeed = initRotSpeed;
        this.initLifetime = initLifetime;
        this.colors = colors;
    }

    public StarlightParticleOptions() {
        this(0.05f, 0.5f, 40, null);
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.STARLIGHT_PARTICLE.get();
    }

    private static final Codec<Vector3f[]> COLORS_CODEC =
        Codec.FLOAT.listOf().listOf().xmap(
            lists -> lists.stream()
                .map(l -> new Vector3f(l.get(0), l.get(1), l.get(2)))
                .toArray(Vector3f[]::new),
            arr -> Arrays.stream(arr).map(c -> java.util.List.of(c.x, c.y, c.z)).toList()
        );

    public static final MapCodec<StarlightParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.FLOAT.optionalFieldOf("initSize", 0.05f).forGetter(o -> o.initSize),
            Codec.FLOAT.optionalFieldOf("initRotSpeed", 0.5f).forGetter(o -> o.initRotSpeed),
            Codec.INT.optionalFieldOf("initLifetime", 40).forGetter(o -> o.initLifetime),
            COLORS_CODEC.optionalFieldOf("colors", null).forGetter(o -> o.colors)
        ).apply(instance, StarlightParticleOptions::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, StarlightParticleOptions> STREAM_CODEC =
        new StreamCodec<>() {
            @Override
            public StarlightParticleOptions decode(RegistryFriendlyByteBuf buf) {
                float size = buf.readFloat();
                float rotSpeed = buf.readFloat();
                int lifetime = buf.readInt();
                int colorCount = buf.readVarInt();
                Vector3f[] colors = colorCount > 0 ? new Vector3f[colorCount] : null;
                for(int i = 0; i < colorCount; i++) {
                    colors[i] = new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
                }
                return new StarlightParticleOptions(size, rotSpeed, lifetime, colors);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, StarlightParticleOptions options) {
                buf.writeFloat(options.initSize);
                buf.writeFloat(options.initRotSpeed);
                buf.writeInt(options.initLifetime);
                if(options.colors != null) {
                    buf.writeVarInt(options.colors.length);
                    for(Vector3f color : options.colors) {
                        buf.writeFloat(color.x);
                        buf.writeFloat(color.y);
                        buf.writeFloat(color.z);
                    }
                }else {
                    buf.writeVarInt(0);
                }
            }
        };
}
