package com.lzxnone.terraria.item;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.block.ModBlocks;
import com.lzxnone.terraria.item.accessory.*;
import com.lzxnone.terraria.item.ammo.*;
import com.lzxnone.terraria.item.food.ManaCrystal;
import com.lzxnone.terraria.item.normal.FallenStar;
import com.lzxnone.terraria.item.potion.*;
import com.lzxnone.terraria.item.weapon.ranged.ElfMelter;
import com.lzxnone.terraria.item.weapon.magic.*;
import com.lzxnone.terraria.item.weapon.melee.*;
import com.lzxnone.terraria.item.weapon.ranged.Flamethrower;
import com.lzxnone.terraria.item.weapon.ranged.SnowballCannon;
import com.lzxnone.terraria.item.weapon.ranged.StarCannon;
import com.lzxnone.terraria.item.weapon.ranged.SuperStarShooter;
import com.lzxnone.terraria.item.weapon.ranged.bow.DaedalusStormbow;
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
    public static final DeferredItem<Item> DAEDALUS_STORMBOW = ITEMS.register("daedalus_stormbow", DaedalusStormbow::new);

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
    public static final DeferredItem<Item> WOODEN_ARROW = ITEMS.register("wooden_arrow", WoodenArrow::new);
    public static final DeferredItem<Item> FALLEN_STAR = ITEMS.register("fallen_star", FallenStar::new);

    //饰品
    public static final DeferredItem<Item> ARMOR_POLISH = ITEMS.register("armor_polish", ArmorPolish::new);
    public static final DeferredItem<Item> MEGAPHONE = ITEMS.register("megaphone", Megaphone::new);
    public static final DeferredItem<Item> NAZAR = ITEMS.register("nazar", Nazar::new);
    public static final DeferredItem<Item> POCKET_MIRROR = ITEMS.register("pocket_mirror", PocketMirror::new);
    public static final DeferredItem<Item> REFLECTIVE_SHADES = ITEMS.register("reflective_shades", ReflectiveShades::new);
    public static final DeferredItem<Item> ARMOR_BRACING = ITEMS.register("armor_bracing", ArmorBracing::new);
    public static final DeferredItem<Item> COUNTERCURSE_MANTRA = ITEMS.register("countercurse_mantra", CountercurseMantra::new);
    public static final DeferredItem<Item> ANKH_CHARM = ITEMS.register("ankh_charm", AnkhCharm::new);
    public static final DeferredItem<Item> OBSIDIAN_SKULL = ITEMS.register("obsidian_skull", ObsidianSkull::new);
    public static final DeferredItem<Item> COBALT_SHIELD = ITEMS.register("cobalt_shield", CobaltShield::new);
    public static final DeferredItem<Item> OBSIDIAN_SHIELD = ITEMS.register("obsidian_shield", ObsidianShield::new);
    public static final DeferredItem<Item> ANKH_SHIELD = ITEMS.register("ankh_shield", AnkhShield::new);
    public static final DeferredItem<Item> AVENGER_EMBLEM = ITEMS.register("avenger_emblem", AvengerEmblem::new);
    public static final DeferredItem<Item> CROSS_NECKLACE = ITEMS.register("cross_necklace", CrossNecklace::new);
    public static final DeferredItem<Item> STAR_CLOAK = ITEMS.register("star_cloak", StarCloak::new);
    public static final DeferredItem<Item> STAR_VEIL = ITEMS.register("star_veil", StarVeil::new);
    public static final DeferredItem<Item> HAND_WARMER = ITEMS.register("hand_warmer", HandWarmer::new);
    public static final DeferredItem<Item> BEZOAR = ITEMS.register("bezoar", Bezoar::new);
    public static final DeferredItem<Item> BLINDFOLD = ITEMS.register("blindfold", Blindfold::new);
    public static final DeferredItem<Item> FAST_CLOCK = ITEMS.register("fast_clock", FastClock::new);
    public static final DeferredItem<Item> VITAMINS = ITEMS.register("vitamins", Vitamins::new);
    public static final DeferredItem<Item> ADHESIVE_BANDAGE = ITEMS.register("adhesive_bandage", AdhesiveBandage::new);
    public static final DeferredItem<Item> MEDICATED_BANDAGE = ITEMS.register("medicated_bandage", MedicatedBandage::new);
    public static final DeferredItem<Item> TRIFOLD_MAP = ITEMS.register("trifold_map", TrifoldMap::new);
    public static final DeferredItem<Item> THE_PLAN = ITEMS.register("the_plan", ThePlan::new);
    public static final DeferredItem<Item> NATURES_GIFT = ITEMS.register("natures_gift", NaturesGift::new);
    public static final DeferredItem<Item> MANA_FLOWER = ITEMS.register("mana_flower", ManaFlower::new);
    public static final DeferredItem<Item> HERCULES_BEETLE = ITEMS.register("hercules_beetle", HerculesBeetle::new);
    public static final DeferredItem<Item> NECROMANTIC_SCROLL = ITEMS.register("necromantic_scroll", NecromanticScroll::new);
    public static final DeferredItem<Item> PAPYRUS_SCARAB = ITEMS.register("papyrus_scarab", PapyrusScarab::new);
    public static final DeferredItem<Item> PYGMY_NECKLACE = ITEMS.register("pygmy_necklace", PygmyNecklace::new);
    public static final DeferredItem<Item> WARRIOR_EMBLEM = ITEMS.register("warrior_emblem", WarriorEmblem::new);
    public static final DeferredItem<Item> SUMMONER_EMBLEM = ITEMS.register("summoner_emblem", SummonerEmblem::new);
    public static final DeferredItem<Item> RANGER_EMBLEM = ITEMS.register("ranger_emblem", RangerEmblem::new);
    public static final DeferredItem<Item> SORCERER_EMBLEM = ITEMS.register("sorcerer_emblem", SorcererEmblem::new);
    public static final DeferredItem<Item> SHARK_TOOTH_NECKLACE = ITEMS.register("shark_tooth_necklace", SharkToothNecklace::new);
    public static final DeferredItem<Item> HONEY_COMB = ITEMS.register("honey_comb", HoneyComb::new);
    public static final DeferredItem<Item> STINGER_NECKLACE = ITEMS.register("stinger_necklace", StingerNecklace::new);
    public static final DeferredItem<Item> BEE_CLOAK = ITEMS.register("bee_cloak", BeeCloak::new);
    public static final DeferredItem<Item> BLACK_BELT = ITEMS.register("black_belt", BlackBelt::new);
    public static final DeferredItem<Item> TITAN_GLOVE = ITEMS.register("titan_glove", TitanGlove::new);
    public static final DeferredItem<Item> FERAL_CLAWS = ITEMS.register("feral_claws", FeralClaws::new);
    public static final DeferredItem<Item> POWER_GLOVE = ITEMS.register("power_glove", PowerGlove::new);
    public static final DeferredItem<Item> MECHANICAL_GLOVE = ITEMS.register("mechanical_glove", MechanicalGlove::new);
    public static final DeferredItem<Item> FLESH_KNUCKLES = ITEMS.register("flesh_knuckles", FleshKnuckles::new);
    public static final DeferredItem<Item> BERSERKERS_GLOVE = ITEMS.register("berserkers_glove", BerserkersGlove::new);
    public static final DeferredItem<Item> PALADINS_SHIELD = ITEMS.register("paladins_shield", PaladinsShield::new);
    public static final DeferredItem<Item> HERO_SHIELD = ITEMS.register("hero_shield", HeroShield::new);
    public static final DeferredItem<Item> FROZEN_TURTLE_SHELL = ITEMS.register("frozen_turtle_shell", FrozenTurtleShell::new);
    public static final DeferredItem<Item> FROZEN_SHIELD = ITEMS.register("frozen_shield", FrozenShield::new);
    public static final DeferredItem<Item> MAGMA_STONE = ITEMS.register("magma_stone", MagmaStone::new);
    public static final DeferredItem<Item> FIRE_GAUNTLET = ITEMS.register("fire_gauntlet", FireGauntlet::new);
    public static final DeferredItem<Item> LAVA_CHARM = ITEMS.register("lava_charm", LavaCharm::new);
    public static final DeferredItem<Item> OBSIDIAN_ROSE = ITEMS.register("obsidian_rose", ObsidianRose::new);
    public static final DeferredItem<Item> MAGMA_SKULL = ITEMS.register("magma_skull", MagmaSkull::new);
    public static final DeferredItem<Item> MOLTEN_CHARM = ITEMS.register("molten_charm", MoltenCharm::new);
    public static final DeferredItem<Item> OBSIDIAN_SKULL_ROSE = ITEMS.register("obsidian_skull_rose", ObsidianSkullRose::new);
    public static final DeferredItem<Item> MOLTEN_SKULL_ROSE = ITEMS.register("molten_skull_rose", MoltenSkullRose::new);
    public static final DeferredItem<Item> PANIC_NECKLACE = ITEMS.register("panic_necklace", PanicNecklace::new);
    public static final DeferredItem<Item> SWEETHEART_NECKLACE = ITEMS.register("sweetheart_necklace", SweetheartNecklace::new);
    public static final DeferredItem<Item> ARCANE_FLOWER = ITEMS.register("arcane_flower", ArcaneFlower::new);
    public static final DeferredItem<Item> REGENERATION_BAND = ITEMS.register("band_of_regeneration", RegenerationBand::new);
    public static final DeferredItem<Item> BAND_OF_STARPOWER = ITEMS.register("band_of_starpower", BandOfStarpower::new);
    public static final DeferredItem<Item> MANA_REGENERATION_BAND = ITEMS.register("mana_regeneration_band", ManaRegenerationBand::new);
    public static final DeferredItem<Item> MAGIC_CUFF = ITEMS.register("magic_cuffs", MagicCuff::new);
    public static final DeferredItem<Item> CELESTIAL_MAGNET = ITEMS.register("celestial_magnet", CelestialMagnet::new);
    public static final DeferredItem<Item> CELESTIAL_EMBLEM = ITEMS.register("celestial_emblem", CelestialEmblem::new);
    public static final DeferredItem<Item> CELESTIAL_CUFFS = ITEMS.register("celestial_cuffs", CelestialCuffs::new);
    public static final DeferredItem<Item> MAGNET_FLOWER = ITEMS.register("magnet_flower", MagnetFlower::new);
    public static final DeferredItem<Item> PHILOSOPHERS_STONE = ITEMS.register("philosophers_stone", PhilosophersStone::new);
    public static final DeferredItem<Item> VALENTINE_RING = ITEMS.register("valentine_ring", ValentineRing::new);
    public static final DeferredItem<Item> CHARM_OF_MYTHS = ITEMS.register("charm_of_myths", CharmOfMyths::new);
    public static final DeferredItem<Item> MANA_CLOAK = ITEMS.register("mana_cloak", ManaCloak::new);
    public static final DeferredItem<Item> LUCKY_COIN = ITEMS.register("lucky_coin", LuckyCoin::new);
    public static final DeferredItem<Item> GOLD_RING = ITEMS.register("gold_ring", GoldRing::new);
    public static final DeferredItem<Item> COIN_RING = ITEMS.register("coin_ring", CoinRing::new);
    public static final DeferredItem<Item> DISCOUNT_CARD = ITEMS.register("discount_card", DiscountCard::new);
    public static final DeferredItem<Item> GREEDY_RING = ITEMS.register("greedy_ring", GreedyRing::new);
    public static final DeferredItem<Item> TREASURE_MAGNET = ITEMS.register("treasure_magnet", TreasureMagnet::new);
    public static final DeferredItem<Item> TOOLBELT = ITEMS.register("toolbelt", Toolbelt::new);
    public static final DeferredItem<Item> ANCIENT_CHISEL = ITEMS.register("ancient_chisel", AncientChisel::new);
    public static final DeferredItem<Item> EYE_OF_GOLEM = ITEMS.register("eye_of_the_golem", EyeOfGolem::new);
    public static final DeferredItem<Item> DESTROYER_EMBLEM = ITEMS.register("destroyer_emblem", DestroyerEmblem::new);
    public static final DeferredItem<Item> MOON_STONE = ITEMS.register("moon_stone", MoonStone::new);
    public static final DeferredItem<Item> SUN_STONE = ITEMS.register("sun_stone", SunStone::new);
    public static final DeferredItem<Item> CELESTIAL_STONE = ITEMS.register("celestial_stone", CelestialStone::new);
    public static final DeferredItem<Item> MOON_CHARM = ITEMS.register("moon_charm", MoonCharm::new);
    public static final DeferredItem<Item> NEPTUNES_SHELL = ITEMS.register("neptunes_shell", NeptunesShell::new);
    public static final DeferredItem<Item> MOON_SHELL = ITEMS.register("moon_shell", MoonShell::new);
    public static final DeferredItem<Item> CELESTIAL_SHELL = ITEMS.register("celestial_shell", CelestialShell::new);
    public static final DeferredItem<Item> PUTRID_SCENT = ITEMS.register("putrid_scent", PutridScent::new);
    public static final DeferredItem<Item> SHACKLE = ITEMS.register("shackle", Shackle::new);
    public static final DeferredItem<Item> RIFLE_SCOPE = ITEMS.register("rifle_scope", RifleScope::new);
    public static final DeferredItem<Item> SNIPER_SCOPE = ITEMS.register("sniper_scope", SniperScope::new);
    public static final DeferredItem<Item> RECON_SCOPE = ITEMS.register("recon_scope", ReconScope::new);

    //食物
    public static final DeferredItem<Item> MANA_CRYSTAL = ITEMS.register("mana_crystal", ManaCrystal::new);

    //药水
    public static final DeferredItem<Item> SUMMONING_POTION = ITEMS.register("summoning_potion", SummoningPotion::new);
    public static final DeferredItem<Item> LESSER_MANA_POTION = ITEMS.register("lesser_mana_potion", LesserManaPotion::new);
    public static final DeferredItem<Item> MANA_POTION = ITEMS.register("mana_potion", ManaPotion::new);
    public static final DeferredItem<Item> GREATER_MANA_POTION = ITEMS.register("greater_mana_potion", GreaterManaPotion::new);
    public static final DeferredItem<Item> SUPER_MANA_POTION = ITEMS.register("super_mana_potion", SuperManaPotion::new);

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
    public static final DeferredItem<Item> MANA_STAR = ITEMS.register("mana_star", () -> new Item(new Item.Properties()));

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
