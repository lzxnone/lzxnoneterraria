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
    public static final Supplier<ParticleType<ZenithTrailParticleOptions>> ZENITH_TRAIL_PARTICLE =
            PARTICLE_TYPES.register("zenith_trail_particle", () -> new ParticleType<>(false) {
                @Override
                public MapCodec<ZenithTrailParticleOptions> codec() {
                    return ZenithTrailParticleOptions.CODEC;
                }
                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, ZenithTrailParticleOptions> streamCodec() {
                    return ZenithTrailParticleOptions.STREAM_CODEC;
                }
            });
    public static final Supplier<ParticleType<IronSparkParticleOptions>> IRON_SPARK_PARTICLE =
            PARTICLE_TYPES.register("iron_spark_particle", () -> new ParticleType<>(false) {
                @Override
                public MapCodec<IronSparkParticleOptions> codec() {
                    return IronSparkParticleOptions.CODEC;
                }
                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, IronSparkParticleOptions> streamCodec() {
                    return IronSparkParticleOptions.STREAM_CODEC;
                }
            });
    public static final Supplier<ParticleType<CircleParticleOptions>> CIRCLE_PARTICLE =
            PARTICLE_TYPES.register("circle_particle", () -> new ParticleType<>(false) {
                @Override
                public MapCodec<CircleParticleOptions> codec() {
                    return CircleParticleOptions.CODEC;
                }
                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, CircleParticleOptions> streamCodec() {
                    return CircleParticleOptions.STREAM_CODEC;
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
    public static final Supplier<SimpleParticleType> EXCALIBUR_HIT_PARTICLE =
            PARTICLE_TYPES.register("excalibur_hit_particle", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> TRUE_EXCALIBUR_HIT_PARTICLE =
            PARTICLE_TYPES.register("true_excalibur_hit_particle", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> NIGHTS_EDGE_HIT_PARTICLE =
            PARTICLE_TYPES.register("nights_edge_hit_particle", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> TRUE_NIGHTS_EDGE_HIT_PARTICLE =
            PARTICLE_TYPES.register("true_nights_edge_hit_particle", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> BLOOD_BUTCHERED_PARTICLE =
            PARTICLE_TYPES.register("blood_butchered_particle", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> LEAF_PARTICLE =
            PARTICLE_TYPES.register("leaf_particle", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> SOLAR_EXPLOSION_PARTICLE =
            PARTICLE_TYPES.register("solar_explosion_particle", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> CRYSTAL_FRAGMENT_PARTICLE =
            PARTICLE_TYPES.register("crystal_fragment_particle", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> DEVILS_DEVASTATION_RUNE_PARTICLE =
            PARTICLE_TYPES.register("devils_devastation_rune_particle", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> DEVILS_DEVASTATION_RUNE_PARTICLE2 =
            PARTICLE_TYPES.register("devils_devastation_rune_particle2", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> BLACK_DUST_PARTICLE =
            PARTICLE_TYPES.register("black_dust_particle", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> PARTY_PARTICLE =
            PARTICLE_TYPES.register("party_particle", () -> new SimpleParticleType(true));
}
