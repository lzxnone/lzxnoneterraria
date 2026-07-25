package com.lzxnone.terraria.damage;

import com.lzxnone.terraria.LzxnoneTerraria;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> PLAYER_NORMAL_ATTACK = create("player_normal_attack");
    public static final ResourceKey<DamageType> PLAYER_REAL_ATTACK = create("player_real_attack");

    private static ResourceKey<DamageType> create(String name) {
        return ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, name)
        );
    }
}
