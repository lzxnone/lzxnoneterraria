package com.lzxnone.terraria.item.armor;

import com.lzxnone.terraria.LzxnoneTerraria;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class ModArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
        DeferredRegister.create(Registries.ARMOR_MATERIAL, LzxnoneTerraria.MODID);

    private static DeferredHolder<ArmorMaterial, ArmorMaterial> registerRobe(String name, int defense, Supplier<Ingredient> repairIngredient) {
        return ARMOR_MATERIALS.register(name, () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 0);
                map.put(ArmorItem.Type.LEGGINGS, 0);
                map.put(ArmorItem.Type.CHESTPLATE, defense);
                map.put(ArmorItem.Type.HELMET, 0);
                map.put(ArmorItem.Type.BODY, defense);
            }),
            15,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            repairIngredient,
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, name))),
            0.0F,
            0.0F
        ));
    }

    // 0 防御
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ROBE = registerRobe("robe", 0, () -> Ingredient.of(Items.STRING));
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> AMETHYST_ROBE = registerRobe("amethyst_robe", 0, () -> Ingredient.of(Items.AMETHYST_SHARD));

    // 1 防御
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> TOPAZ_ROBE = registerRobe("topaz_robe", 1, () -> Ingredient.of(Items.COPPER_INGOT));
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SAPPHIRE_ROBE = registerRobe("sapphire_robe", 1, () -> Ingredient.of(Items.LAPIS_BLOCK));

    // 2 防御
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> EMERALD_ROBE = registerRobe("emerald_robe", 2, () -> Ingredient.of(Items.EMERALD));
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> RUBY_ROBE = registerRobe("ruby_robe", 2, () -> Ingredient.of(Items.REDSTONE_BLOCK));

    // 3 防御
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> AMBER_ROBE = registerRobe("amber_robe", 3, () -> Ingredient.of(Items.GOLD_INGOT));
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> DIAMOND_ROBE = registerRobe("diamond_robe", 3, () -> Ingredient.of(Items.DIAMOND));
}
