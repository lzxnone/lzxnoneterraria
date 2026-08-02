package com.lzxnone.terraria.item;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.block.ModBlocks;
import com.lzxnone.terraria.item.ammo.*;
import com.lzxnone.terraria.item.accessory.ManaFlower;
import com.lzxnone.terraria.item.accessory.NaturesGift;
import com.lzxnone.terraria.item.food.ManaCrystal;
import com.lzxnone.terraria.item.weapon.ranged.ElfMelter;
import com.lzxnone.terraria.item.weapon.magic.*;
import com.lzxnone.terraria.item.weapon.melee.*;
import com.lzxnone.terraria.item.weapon.ranged.Flamethrower;
import com.lzxnone.terraria.item.weapon.ranged.SnowballCannon;
import com.lzxnone.terraria.item.weapon.ranged.StarCannon;
import com.lzxnone.terraria.item.weapon.ranged.SuperStarShooter;
import com.lzxnone.terraria.item.weapon.ranged.gun.*;
import com.lzxnone.terraria.item.weapon.summon.minion.StardustDragonStaff;
import com.lzxnone.terraria.item.weapon.summon.minion.Terraprisma;
import com.lzxnone.terraria.item.weapon.summon.whip.Possession;
import net.minecraft.world.item.BlockItem;
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
    public static final DeferredItem<Item> CLOCKWORK_ASSAULT_RIFLE = ITEMS.register("clockwork_assault_rifle", ClockworkAssaultRifle::new);
    public static final DeferredItem<Item> GATLIGATOR = ITEMS.register("gatligator", Gatligator::new);
    public static final DeferredItem<Item> SHOTGUN = ITEMS.register("shotgun", Shotgun::new);
    public static final DeferredItem<Item> UZI = ITEMS.register("uzi", Uzi::new);
    public static final DeferredItem<Item> VENUS_MAGNUM = ITEMS.register("venus_magnum", VenusMagnum::new);
    public static final DeferredItem<Item> TACTICAL_SHOTGUN = ITEMS.register("tactical_shotgun", TacticalShotgun::new);
    public static final DeferredItem<Item> SNIPER_RIFLE = ITEMS.register("sniper_rifle", SniperRifle::new);
    public static final DeferredItem<Item> CHAIN_GUN = ITEMS.register("chain_gun", ChainGun::new);
    public static final DeferredItem<Item> SDMG = ITEMS.register("sdmg", SDMG::new);
    public static final DeferredItem<Item> SNOWBALL_CANNON = ITEMS.register("snowball_cannon", SnowballCannon::new);
    public static final DeferredItem<Item> STAR_CANNON = ITEMS.register("star_cannon", StarCannon::new);
    public static final DeferredItem<Item> SUPER_STAR_SHOOTER = ITEMS.register("super_star_shooter", SuperStarShooter::new);
    public static final DeferredItem<Item> FLAMETHROWER = ITEMS.register("flamethrower", Flamethrower::new);
    public static final DeferredItem<Item> ELF_MELTER = ITEMS.register("elf_melter", ElfMelter::new);

    //召唤武器
    public static final DeferredItem<Item> TERRAPRISMA = ITEMS.register("terraprisma", Terraprisma::new);
    public static final DeferredItem<Item> STARDUST_DRAGON_STAFF = ITEMS.register("stardust_dragon_staff", StardustDragonStaff::new);
    public static final DeferredItem<Item> POSSESSION = ITEMS.register("possession", Possession::new);

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
    public static final DeferredItem<Item> FALLEN_STAR = ITEMS.register("fallen_star", FallenStar::new);

    //饰品
    public static final DeferredItem<Item> NATURES_GIFT = ITEMS.register("natures_gift", NaturesGift::new);
    public static final DeferredItem<Item> MANA_FLOWER = ITEMS.register("mana_flower", ManaFlower::new);

    //食物
    public static final DeferredItem<Item> MANA_CRYSTAL = ITEMS.register("mana_crystal", ManaCrystal::new);

    //方块
    public static final DeferredItem<Item> VISION = ITEMS.register("vision", () -> new BlockItem(ModBlocks.VISION.get(), new Item.Properties()));
    public static final DeferredItem<Item> STARDUST_DRAGON_HEAD = ITEMS.register("stardust_dragon_head", () -> new BlockItem(ModBlocks.STARDUST_DRAGON_HEAD.get(), new Item.Properties()));
    public static final DeferredItem<Item> STARDUST_DRAGON_BODY_A = ITEMS.register("stardust_dragon_body_a", () -> new BlockItem(ModBlocks.STARDUST_DRAGON_BODY_A.get(), new Item.Properties()));
    public static final DeferredItem<Item> STARDUST_DRAGON_BODY_B = ITEMS.register("stardust_dragon_body_b", () -> new BlockItem(ModBlocks.STARDUST_DRAGON_BODY_B.get(), new Item.Properties()));
    public static final DeferredItem<Item> STARDUST_DRAGON_TAIL = ITEMS.register("stardust_dragon_tail", () -> new BlockItem(ModBlocks.STARDUST_DRAGON_TAIL.get(), new Item.Properties()));

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
    public static final DeferredItem<Item> WHITE_STAR = ITEMS.register("white_star", () -> new Item(new Item.Properties()));

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
