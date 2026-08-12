package com.lzxnone.terraria.item;

import com.lzxnone.terraria.LzxnoneTerraria;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {
    public static final TagKey<Item> AMMO = create("ammo");
    public static final TagKey<Item> BULLET_AMMO = create("bullet_ammo");
    public static final TagKey<Item> ARROW_AMMO = create("arrow_ammo");
    public static final TagKey<Item> GUNPOWDER_WEAPONS = create("gunpowder_weapons");
    public static final TagKey<Item> SPREAD_RANGED_WEAPONS = create("spread_ranged_weapons");

    private static TagKey<Item> create(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, name));
    }
}
