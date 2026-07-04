package com.lzxnone.terraria.particle;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, LzxnoneTerraria.MODID);

    public static final DustParticleOptions MAX_MANA_PARTICLE = new DustParticleOptions(
        0.25f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(0.5F, 0.35F, 0.9F),
        }
    );

    public static final DustParticleOptions SMOKE_PARTICLE = new DustParticleOptions(
        0.025f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(0.7F, 0.7F, 0.7F),
        }
    );

    public static final DustParticleOptions EXPLODE_FLAME_PARTICLE = new DustParticleOptions(
        0.05f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(1.0F, 0.8F, 0.5F),
            new Vector3f(1.0F, 0.5F, 0.0F),
            new Vector3f(1.0F, 0.9F, 0.0F),
        }
    );

    public static final DustParticleOptions COLORFUL_PARTICLE = new DustParticleOptions(
        0.025f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(0.0F, 1.0F, 1.0F), // 0: 青色 (Cyan)
            new Vector3f(0.1F, 0.4F, 1.0F), // 1: 蓝色 (Blue)
            new Vector3f(1.0F, 1.0F, 0.6F), // 2: 淡黄 (Light Yellow)
            new Vector3f(1.0F, 0.9F, 0.0F), // 3: 黄色 (Yellow)
            new Vector3f(0.5F, 1.0F, 0.5F), // 4: 淡绿 (Light Green)
            new Vector3f(0.0F, 1.0F, 0.2F), // 5: 绿色 (Green)
            new Vector3f(1.0F, 0.6F, 0.8F), // 6: 粉红 (Pink)
            new Vector3f(1.0F, 0.0F, 1.0F), // 7: 品红 (Magenta)
            new Vector3f(1.0F, 0.8F, 0.5F), // 8: 淡橙 (Light Orange)
            new Vector3f(1.0F, 0.5F, 0.0F), // 9: 橙色 (Orange)
            new Vector3f(0.7F, 0.7F, 0.7F)  // 10: 淡灰 (Light Gray)
        }
    );

    public static final Supplier<ParticleType<DustParticleOptions>> DUST_PARTICLE =
            PARTICLE_TYPES.register("dust_particle", () -> new ParticleType<>(false) {
                @Override
                public MapCodec<DustParticleOptions> codec() {
                    return DustParticleOptions.CODEC;
                }
                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, DustParticleOptions> streamCodec() {
                    return DustParticleOptions.STREAM_CODEC;
                }
            });
    public static final Supplier<SimpleParticleType> STAR_PARTICLE =
            PARTICLE_TYPES.register("star_particle", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> EXPLODE_PARTICLE =
            PARTICLE_TYPES.register("explode_particle", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> SEEDLER_THORN_PARTICLE =
            PARTICLE_TYPES.register("seedler_thorn_particle", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> INFLUX_WAVER_DROP_PARTICLE =
            PARTICLE_TYPES.register("influx_waver_drop_particle", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> TERRA_BEAM_HIT_PARTICLE =
            PARTICLE_TYPES.register("terra_beam_hit_particle", () -> new SimpleParticleType(true));
}