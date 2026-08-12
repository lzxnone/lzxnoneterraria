package com.lzxnone.terraria.entity;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.entity.beam.SwordBeam;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.summon.BeeSummon;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, LzxnoneTerraria.MODID);

    public static final Supplier<EntityType<StaticProjectile>> STATIC_PROJECTILE =
        ENTITY_TYPES.register("static_projectile", () -> EntityType.Builder.of(
            StaticProjectile::new, MobCategory.MISC)
                .sized(1.0f, 1.0f)
                .clientTrackingRange(1024)
                .updateInterval(1)
                .fireImmune()
                .build("static_projectile")
        );
    public static final Supplier<EntityType<StaticSummon>> STATIC_SUMMON =
        ENTITY_TYPES.register("static_summon", () -> EntityType.Builder.of(
            StaticSummon::new, MobCategory.MISC)
                .sized(1.0f, 1.0f)
                .clientTrackingRange(1024)
                .updateInterval(1)
                .build("static_summon")
        );

    public static final Supplier<EntityType<Entity>> SWORD_BEAM =
        ENTITY_TYPES.register("sword_beam", () -> EntityType.Builder.of(
            SwordBeam::new, MobCategory.MISC)
                .sized(0.5f, 0.5f)
                .clientTrackingRange(1024)
                .updateInterval(1)
                .fireImmune()
                .build("sword_beam")
        );

    public static final Supplier<EntityType<BeeSummon>> BEE_SUMMON =
        ENTITY_TYPES.register("bee_summon", () -> EntityType.Builder.of(
            BeeSummon::new, MobCategory.MISC)
                .sized(0.7F, 0.6F)
                .clientTrackingRange(1024)
                .fireImmune()
                .build("bee_summon")
        );
}
