package com.lzxnone.terraria.item;

import com.lzxnone.terraria.LzxnoneTerraria;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {
    public static final TagKey<Item> AMMO = create("ammo");
    public static final TagKey<Item> BULLET_AMMO = create("bullet_ammo");

    private static TagKey<Item> create(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, name));
    }
}
