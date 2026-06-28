package com.lzxnone.terraria.entity;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.entity.projectile.TextureProjectile;
import com.lzxnone.terraria.entity.summon.BeeSummon;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, LzxnoneTerraria.MODID);

    public static final Supplier<EntityType<TextureProjectile>> TEXTURE_PROJECTILE =
        ENTITY_TYPES.register("texture_projectile", () -> EntityType.Builder.of(
            TextureProjectile::new, MobCategory.MISC)
                .sized(0.5f, 0.5f)
                .clientTrackingRange(1024)
                .updateInterval(1)
                .build("texture_projectile")
        );

    public static final Supplier<EntityType<BeeSummon>> BEE_SUMMON =
        ENTITY_TYPES.register("bee_summon", () -> EntityType.Builder.of(
            BeeSummon::new, MobCategory.MISC)
                .sized(0.7F, 0.6F)
                .clientTrackingRange(8)
                .build("bee_summon")
        );
}
