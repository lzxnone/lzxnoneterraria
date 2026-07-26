package com.lzxnone.terraria.enchantment;

import com.lzxnone.terraria.LzxnoneTerraria;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantments {
    public static final ResourceKey<Enchantment> MANA_LEAK = create("mana_leak");
    public static final ResourceKey<Enchantment> MANA_EFFICIENCY = create("mana_efficiency");
    public static final ResourceKey<Enchantment> MANA_GATHERING = create("mana_gathering");
    public static final ResourceKey<Enchantment> MANA_GATHERING_CURSE = create("mana_gathering_curse");
    public static final ResourceKey<Enchantment> ARCANE_AMPLIFICATION = create("arcane_amplification");
    public static final ResourceKey<Enchantment> SUMMON_AMPLIFICATION = create("summon_amplification");
    public static final ResourceKey<Enchantment> BARREN_LAND = create("barren_land");

    private static ResourceKey<Enchantment> create(String name) {
        return ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, name)
        );
    }
}
