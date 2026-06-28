package com.lzxnone.terraria.item;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.projectile.EnchantedSwordBeam;
import com.lzxnone.terraria.item.projectile.SeedlerNut;
import com.lzxnone.terraria.item.projectile.SeedlerThorn;
import com.lzxnone.terraria.item.projectile.StarfuryStar;
import com.lzxnone.terraria.item.weapon.melee.*;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LzxnoneTerraria.MODID);

    public static final DeferredItem<CopperShortsword> COPPER_SHORTSWORD = ITEMS.register("copper_shortsword", CopperShortsword::new);
    public static final DeferredItem<EnchantedSword> ENCHANTED_SWORD = ITEMS.register("enchanted_sword", EnchantedSword::new);
    public static final DeferredItem<Item> BEE_KEEPER = ITEMS.register("bee_keeper", BeeKeeper::new);
    public static final DeferredItem<Item> STARFURY = ITEMS.register("starfury", Starfury::new);
    public static final DeferredItem<Item> SEEDLER = ITEMS.register("seedler", Seedler::new);

    public static final DeferredItem<Item> INFLUX_WAVER = ITEMS.register("influx_waver",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MEOWMERE = ITEMS.register("meowmere",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STAR_WRATH = ITEMS.register("star_wrath",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TERRA_BLADE = ITEMS.register("terra_blade",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> THE_HORSEMANS_BLADE = ITEMS.register("the_horsemans_blade",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ZENITH = ITEMS.register("zenith",
            () -> new Item(new Item.Properties()));

    //弹射物
    public static final DeferredItem<EnchantedSwordBeam> ENCHANTED_SWORD_BEAM = ITEMS.register("enchanted_sword_beam", EnchantedSwordBeam::new);
    public static final DeferredItem<StarfuryStar> STARFURY_STAR = ITEMS.register("starfury_star", StarfuryStar::new);
    public static final DeferredItem<SeedlerNut> SEEDLER_NUT = ITEMS.register("seedler_nut", SeedlerNut::new);
    public static final DeferredItem<SeedlerThorn> SEEDLER_THORN = ITEMS.register("seedler_thorn", SeedlerThorn::new);
}
