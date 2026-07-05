package com.lzxnone.terraria.item;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.projectile.*;
import com.lzxnone.terraria.item.weapon.melee.*;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LzxnoneTerraria.MODID);

    public static final DeferredItem<Item> COPPER_SHORTSWORD = ITEMS.register("copper_shortsword", CopperShortsword::new);
    public static final DeferredItem<Item> ENCHANTED_SWORD = ITEMS.register("enchanted_sword", EnchantedSword::new);
    public static final DeferredItem<Item> BEE_KEEPER = ITEMS.register("bee_keeper", BeeKeeper::new);
    public static final DeferredItem<Item> STARFURY = ITEMS.register("starfury", Starfury::new);
    public static final DeferredItem<Item> SEEDLER = ITEMS.register("seedler", Seedler::new);
    public static final DeferredItem<Item> THE_HORSEMANS_BLADE = ITEMS.register("the_horsemans_blade", TheHorsemansBlade::new);
    public static final DeferredItem<Item> INFLUX_WAVER = ITEMS.register("influx_waver", InfluxWaver::new);
    public static final DeferredItem<Item> STAR_WRATH = ITEMS.register("star_wrath", StarWrath::new);
    public static final DeferredItem<Item> MEOWMERE = ITEMS.register("meowmere", Meowmere::new);
    public static final DeferredItem<Item> TERRA_BLADE = ITEMS.register("terra_blade", TerraBlade::new);
    public static final DeferredItem<Item> ZENITH = ITEMS.register("zenith", Zenith::new);
    public static final DeferredItem<Item> LIGHTS_BANE = ITEMS.register("lights_bane", LightsBane::new);
    public static final DeferredItem<Item> BLOOD_BUTCHERER = ITEMS.register("blood_butcherer", BloodButcherer::new);
    public static final DeferredItem<Item> MURAMASA = ITEMS.register("muramasa", Muramasa::new);
    public static final DeferredItem<Item> TERRAGRIM = ITEMS.register("terragrim", Terragrim::new);
    public static final DeferredItem<Item> BLADE_OF_GRASS = ITEMS.register("blade_of_grass", BladeOfGrass::new);
    public static final DeferredItem<Item> VOLCANO = ITEMS.register("volcano", Volcano::new);
    public static final DeferredItem<Item> NIGHTS_EDGE = ITEMS.register("nights_edge", NightsEdge::new);
    public static final DeferredItem<Item> TRUE_NIGHTS_EDGE = ITEMS.register("true_nights_edge", TrueNightsEdge::new);
    public static final DeferredItem<Item> EXCALIBUR = ITEMS.register("excalibur", Excalibur::new);
    public static final DeferredItem<Item> TRUE_EXCALIBUR = ITEMS.register("true_excalibur", TrueExcalibur::new);

    //弹射物
    public static final DeferredItem<Item> ENCHANTED_SWORD_BEAM = ITEMS.register("enchanted_sword_beam", EnchantedSwordBeam::new);
    public static final DeferredItem<Item> STARFURY_STAR = ITEMS.register("starfury_star", StarfuryStar::new);
    public static final DeferredItem<Item> SEEDLER_NUT = ITEMS.register("seedler_nut", SeedlerNut::new);
    public static final DeferredItem<Item> SEEDLER_THORN = ITEMS.register("seedler_thorn", SeedlerThorn::new);
    public static final DeferredItem<Item> INFLUX_WAVER_BEAM = ITEMS.register("influx_waver_beam", InfluxWaverBeam::new);
    public static final DeferredItem<Item> STAR_WRATH_STAR = ITEMS.register("star_wrath_star", StarWrathStar::new);
    public static final DeferredItem<Item> MEOWMERE_PROJECTILE = ITEMS.register("meowmere_projectile", MeowmereProjectile::new);
}
