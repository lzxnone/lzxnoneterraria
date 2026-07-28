package com.lzxnone.terraria.item;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.ammo.*;
import com.lzxnone.terraria.item.projectile.*;
import com.lzxnone.terraria.item.projectile.first_fractal.*;
import com.lzxnone.terraria.item.weapon.magic.*;
import com.lzxnone.terraria.item.weapon.melee.*;
import com.lzxnone.terraria.item.weapon.ranged.*;
import com.lzxnone.terraria.item.weapon.summon.*;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LzxnoneTerraria.MODID);

    //近战武器
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
    public static final DeferredItem<Item> TRUE_COPPER_SHORTSWORD = ITEMS.register("true_copper_shortsword", TrueCopperShortsword::new);
    public static final DeferredItem<Item> FIRST_FRACTAL = ITEMS.register("first_fractal", FirstFractal::new);
    public static final DeferredItem<Item> MACE = ITEMS.register("mace", Mace::new);

    //魔法武器
    public static final DeferredItem<Item> LAST_PRISM = ITEMS.register("last_prism", LastPrism::new);

    //远程武器
    public static final DeferredItem<Item> MUSKET = ITEMS.register("musket", Musket::new);
    public static final DeferredItem<Item> MINISHARK = ITEMS.register("minishark", Minishark::new);
    public static final DeferredItem<Item> FLINTLOCK_PISTOL = ITEMS.register("flintlock_pistol", FlintlockPistol::new);
    public static final DeferredItem<Item> BOOMSTICK = ITEMS.register("boomstick", Boomstick::new);
    public static final DeferredItem<Item> REVOLVER = ITEMS.register("revolver", Revolver::new);
    public static final DeferredItem<Item> RED_RYDER = ITEMS.register("red_ryder", RedRyder::new);
    public static final DeferredItem<Item> THE_UNDERTAKER = ITEMS.register("the_undertaker", TheUndertaker::new);
    public static final DeferredItem<Item> QUAD_BARREL_SHOTGUN = ITEMS.register("quad_barrel_shotgun", QuadBarrelShotgun::new);
    public static final DeferredItem<Item> HANDGUN = ITEMS.register("handgun", Handgun::new);
    public static final DeferredItem<Item> PHOENIX_BLASTER = ITEMS.register("phoenix_blaster", PhoenixBlaster::new);
    public static final DeferredItem<Item> MEGASHARK = ITEMS.register("megashark", Megashark::new);
    public static final DeferredItem<Item> SDMG = ITEMS.register("sdmg", SDMG::new);

    //召唤武器
    public static final DeferredItem<Item> TERRAPRISMA = ITEMS.register("terraprisma", Terraprisma::new);

    //近战武器 灾厄
    public static final DeferredItem<Item> DEVILS_DEVASTATION = ITEMS.register("devils_devastation", DevilsDevastation::new);

    //弹药
    public static final DeferredItem<Item> MUSKET_BALL = ITEMS.register("musket_ball", MusketBall::new);
    public static final DeferredItem<Item> METEOR_SHOT = ITEMS.register("meteor_shot", MeteorShot::new);
    public static final DeferredItem<Item> SILVER_BULLET = ITEMS.register("silver_bullet", SilverBullet::new);
    public static final DeferredItem<Item> CRYSTAL_BULLET = ITEMS.register("crystal_bullet", CrystalBullet::new);
    public static final DeferredItem<Item> CURSED_BULLET = ITEMS.register("cursed_bullet", CursedBullet::new);
    public static final DeferredItem<Item> CHLOROPHYTE_BULLET = ITEMS.register("chlorophyte_bullet", ChlorophyteBullet::new);
    public static final DeferredItem<Item> HIGH_VELOCITY_BULLET = ITEMS.register("high_velocity_bullet", HighVelocityBullet::new);
    public static final DeferredItem<Item> ICHOR_BULLET = ITEMS.register("ichor_bullet", IchorBullet::new);
    public static final DeferredItem<Item> VENOM_BULLET = ITEMS.register("venom_bullet", VenomBullet::new);
    public static final DeferredItem<Item> PARTY_BULLET = ITEMS.register("party_bullet", PartyBullet::new);
    public static final DeferredItem<Item> NANO_BULLET = ITEMS.register("nano_bullet", NanoBullet::new);
    public static final DeferredItem<Item> EXPLODING_BULLET = ITEMS.register("exploding_bullet", ExplodingBullet::new);
    public static final DeferredItem<Item> GOLDEN_BULLET = ITEMS.register("golden_bullet", GoldenBullet::new);
    public static final DeferredItem<Item> ENDLESS_MUSKET_POUCH = ITEMS.register("endless_musket_pouch", EndlessMusketPouch::new);
    public static final DeferredItem<Item> LUMINITE_BULLET = ITEMS.register("luminite_bullet", LuminiteBullet::new);
    public static final DeferredItem<Item> TUNGSTEN_BULLET = ITEMS.register("tungsten_bullet", TungstenBullet::new);

    //弹射物
    public static final DeferredItem<Item> BULLET_PROJECTILE = ITEMS.register("bullet_projectile", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> METEOR_SHOT_PROJECTILE = ITEMS.register("meteor_shot_projectile", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SILVER_BULLET_PROJECTILE = ITEMS.register("silver_bullet_projectile", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CRYSTAL_BULLET_PROJECTILE = ITEMS.register("crystal_bullet_projectile", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CRYSTAL_FRAGMENT = ITEMS.register("crystal_fragment", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CURSED_BULLET_PROJECTILE = ITEMS.register("cursed_bullet_projectile", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CHLOROPHYTE_BULLET_PROJECTILE = ITEMS.register("chlorophyte_bullet_projectile", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> HIGH_VELOCITY_BULLET_PROJECTILE = ITEMS.register("high_velocity_bullet_projectile", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ICHOR_BULLET_PROJECTILE = ITEMS.register("ichor_bullet_projectile", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> VENOM_BULLET_PROJECTILE = ITEMS.register("venom_bullet_projectile", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> PARTY_BULLET_PROJECTILE = ITEMS.register("party_bullet_projectile", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> NANO_BULLET_PROJECTILE = ITEMS.register("nano_bullet_projectile", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> EXPLODING_BULLET_PROJECTILE = ITEMS.register("exploding_bullet_projectile", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GOLDEN_BULLET_PROJECTILE = ITEMS.register("golden_bullet_projectile", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LUMINITE_BULLET_PROJECTILE = ITEMS.register("luminite_bullet_projectile", () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> ENCHANTED_SWORD_BEAM = ITEMS.register("enchanted_sword_beam", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STARFURY_STAR = ITEMS.register("starfury_star", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SEEDLER_NUT = ITEMS.register("seedler_nut", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SEEDLER_THORN = ITEMS.register("seedler_thorn", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INFLUX_WAVER_BEAM = ITEMS.register("influx_waver_beam", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STAR_WRATH_STAR = ITEMS.register("star_wrath_star", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MEOWMERE_PROJECTILE = ITEMS.register("meowmere_projectile", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LEAF_PROJECTILE = ITEMS.register("leaf_projectile", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FIRST_FRACTAL_PROJECTILE0 = ITEMS.register("first_fractal_projectile0", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FIRST_FRACTAL_PROJECTILE1 = ITEMS.register("first_fractal_projectile1", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FIRST_FRACTAL_PROJECTILE2 = ITEMS.register("first_fractal_projectile2", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FIRST_FRACTAL_PROJECTILE3 = ITEMS.register("first_fractal_projectile3", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FIRST_FRACTAL_PROJECTILE4 = ITEMS.register("first_fractal_projectile4", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FIRST_FRACTAL_PROJECTILE5 = ITEMS.register("first_fractal_projectile5", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FIRST_FRACTAL_PROJECTILE6 = ITEMS.register("first_fractal_projectile6", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FIRST_FRACTAL_PROJECTILE7 = ITEMS.register("first_fractal_projectile7", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FIRST_FRACTAL_PROJECTILE8 = ITEMS.register("first_fractal_projectile8", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FIRST_FRACTAL_PROJECTILE9 = ITEMS.register("first_fractal_projectile9", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FIRST_FRACTAL_PROJECTILE10 = ITEMS.register("first_fractal_projectile10", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FIRST_FRACTAL_PROJECTILE11 = ITEMS.register("first_fractal_projectile11", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FIRST_FRACTAL_PROJECTILE12 = ITEMS.register("first_fractal_projectile12", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FIRST_FRACTAL_PROJECTILE13 = ITEMS.register("first_fractal_projectile13", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FIRST_FRACTAL_PROJECTILE14 = ITEMS.register("first_fractal_projectile14", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MACE_PROJECTILE = ITEMS.register("mace_projectile", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TERRAPRISMA_SUMMON = ITEMS.register("terraprisma_summon", () -> new Item(new Item.Properties()));

}
