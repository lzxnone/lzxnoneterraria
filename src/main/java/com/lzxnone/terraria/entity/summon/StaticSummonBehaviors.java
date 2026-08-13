package com.lzxnone.terraria.entity.summon;

import com.lzxnone.terraria.item.ammo.*;
import com.lzxnone.terraria.item.normal.FallenStar;
import com.lzxnone.terraria.item.normal.ManaStar;
import com.lzxnone.terraria.item.weapon.magic.BetsyWrath;
import com.lzxnone.terraria.item.weapon.magic.BubbleGun;
import com.lzxnone.terraria.item.weapon.magic.LunarFlare;
import com.lzxnone.terraria.item.weapon.ranged.ElfMelter;
import com.lzxnone.terraria.item.weapon.magic.LastPrism;
import com.lzxnone.terraria.item.weapon.melee.*;
import com.lzxnone.terraria.item.weapon.ranged.Flamethrower;
import com.lzxnone.terraria.item.weapon.ranged.SnowballCannon;
import com.lzxnone.terraria.item.weapon.ranged.StarCannon;
import com.lzxnone.terraria.item.weapon.ranged.SuperStarShooter;
import com.lzxnone.terraria.item.weapon.summon.minion.StardustDragonStaff;
import com.lzxnone.terraria.item.weapon.summon.minion.Terraprisma;
import com.lzxnone.terraria.item.weapon.summon.whip.Possession;
import com.lzxnone.terraria.item.weapon.summon.whip.Whip;

import java.util.HashMap;
import java.util.Map;

public class StaticSummonBehaviors {
    private static final Map<String, IStaticSummonBehavior> BEHAVIORS = new HashMap<>();

    public static final String DEFAULT = "default";

    //掉落实体
    public static final String DROP_FALLEN_STAR = "drop_fallen_star";
    public static final String MANA_STAR = "mana_star";

    //弹射实体
    public static final String SEEDLER_NUT = "seedler_nut";
    public static final String THE_HORSEMANS_BLADE_JACK = "the_horsemans_blade_jack";
    public static final String INFLUX_WAVER_BEAM = "influx_waver_beam";
    public static final String MEOWMERE_PROJECTILE = "meowmere_projectile";
    public static final String TERRA_BLADE_BEAM = "terra_blade_beam";
    public static final String TRUE_NIGHTS_EDGE_BEAM = "true_nights_edge_beam";
    public static final String NIGHTS_EDGE_BEAM = "nights_edge_beam";
    public static final String LIGHTS_BANE_SLASH = "lights_bane_slash";
    public static final String VOLCANO_LIGHT = "volcano_light";
    public static final String TERRAGRIM_BEAM = "terragrim_beam";
    public static final String FIRST_FRACTAL_PROJECTILE = "first_fractal_projectile";
    public static final String MACE_PROJECTILE = "mace_projectile";
    public static final String DEVILS_DEVASTATION_STUCK_PROJECTILE = "devils_devastation_stuck_projectile";
    public static final String DEVILS_DEVASTATION_LIGHTNING = "devils_devastation_lightning";
    public static final String DEVILS_DEVASTATION_MARK = "devils_devastation_mark";
    public static final String DEVILS_DEVASTATION_MARK_LIGHTNING = "devils_devastation_mark_lightning";
    public static final String LAST_PRISM_TRI = "last_prism_tri";
    public static final String LAST_PRISM_BEAM = "last_prism_beam";
    public static final String BETSY_WRATH = "betsys_wrath";
    public static final String LUNAR_FLARE = "lunar_flare";
    public static final String BUBBLE = "bubble";
    public static final String TERRAPRISMA = "terraprisma";
    public static final String STARDUST_DRAGON_STAFF = "stardust_dragon_staff";
    public static final String POSSESSION = "possession";
    public static final String POSSESSION_VISION = "possession_vision";

    //通用子弹实体
    public static final String MUSKET_BALL = "musket_ball";
    public static final String METEOR_SHOT = "meteor_shot";
    public static final String SILVER_BULLET = "silver_bullet";
    public static final String CRYSTAL_BULLET = "crystal_bullet";
    public static final String CURSED_BULLET = "cursed_bullet";
    public static final String CHLOROPHYTE_BULLET = "chlorophyte_bullet";
    public static final String HIGH_VELOCITY_BULLET = "high_velocity_bullet";
    public static final String ICHOR_BULLET = "ichor_bullet";
    public static final String VENOM_BULLET = "venom_bullet";
    public static final String PARTY_BULLET = "party_bullet";
    public static final String NANO_BULLET = "nano_bullet";
    public static final String EXPLODING_BULLET = "exploding_bullet";
    public static final String GOLDEN_BULLET = "golden_bullet";
    public static final String LUMINITE_BULLET = "luminite_bullet";
    public static final String TUNGSTEN_BULLET = "tungsten_bullet";
    public static final String WOODEN_ARROW = "wooden_arrow";

    //特殊子弹实体
    public static final String SNOWBALL = "snowball";
    public static final String FALLEN_STAR = "fallen_star";
    public static final String FALLEN_STAR_SUPER = "fallen_star_super";
    public static final String FLAMETHROWER_FLAME = "flamethrower_flame";
    public static final String ELF_MELTER_FLAME = "elf_melter_flame";

    static {
        BEHAVIORS.put(DEFAULT, new IStaticSummonBehavior() {});

        BEHAVIORS.put(DROP_FALLEN_STAR, FallenStar.SUMMON_BEHAVIOR);
        BEHAVIORS.put(MANA_STAR, ManaStar.SUMMON_BEHAVIOR);

        BEHAVIORS.put(SEEDLER_NUT, Seedler.SUMMON_BEHAVIOR);
        BEHAVIORS.put(THE_HORSEMANS_BLADE_JACK, TheHorsemansBlade.SUMMON_BEHAVIOR);
        BEHAVIORS.put(INFLUX_WAVER_BEAM, InfluxWaver.SUMMON_BEHAVIOR);
        BEHAVIORS.put(MEOWMERE_PROJECTILE, Meowmere.SUMMON_BEHAVIOR);
        BEHAVIORS.put(TERRA_BLADE_BEAM, TerraBlade.SUMMON_BEHAVIOR);
        BEHAVIORS.put(TRUE_NIGHTS_EDGE_BEAM, TrueNightsEdge.SUMMON_BEHAVIOR);
        BEHAVIORS.put(NIGHTS_EDGE_BEAM, NightsEdge.SUMMON_BEHAVIOR);
        BEHAVIORS.put(LIGHTS_BANE_SLASH, LightsBane.SUMMON_BEHAVIOR);
        BEHAVIORS.put(VOLCANO_LIGHT, Volcano.SUMMON_BEHAVIOR);
        BEHAVIORS.put(TERRAGRIM_BEAM, Terragrim.SUMMON_BEHAVIOR);
        BEHAVIORS.put(FIRST_FRACTAL_PROJECTILE, FirstFractal.SUMMON_BEHAVIOR);
        BEHAVIORS.put(MACE_PROJECTILE, Mace.SUMMON_BEHAVIOR);
        BEHAVIORS.put(DEVILS_DEVASTATION_STUCK_PROJECTILE, DevilsDevastation.SUMMON_BEHAVIOR);
        BEHAVIORS.put(DEVILS_DEVASTATION_LIGHTNING, DevilsDevastation.SUMMON_BEHAVIOR2);
        BEHAVIORS.put(DEVILS_DEVASTATION_MARK, DevilsDevastation.SUMMON_BEHAVIOR3);
        BEHAVIORS.put(DEVILS_DEVASTATION_MARK_LIGHTNING, DevilsDevastation.SUMMON_BEHAVIOR4);
        BEHAVIORS.put(LAST_PRISM_TRI, LastPrism.SUMMON_BEHAVIOR);
        BEHAVIORS.put(LAST_PRISM_BEAM, LastPrism.SUMMON_BEHAVIOR2);
        BEHAVIORS.put(BETSY_WRATH, BetsyWrath.SUMMON_BEHAVIOR);
        BEHAVIORS.put(LUNAR_FLARE, LunarFlare.SUMMON_BEHAVIOR);
        BEHAVIORS.put(BUBBLE, BubbleGun.SUMMON_BEHAVIOR);
        BEHAVIORS.put(TERRAPRISMA, Terraprisma.SUMMON_BEHAVIOR);
        BEHAVIORS.put(STARDUST_DRAGON_STAFF, StardustDragonStaff.SUMMON_BEHAVIOR);
        BEHAVIORS.put(POSSESSION, Whip.SUMMON_BEHAVIOR);
        BEHAVIORS.put(POSSESSION_VISION, Possession.SUMMON_BEHAVIOR);

        BEHAVIORS.put(MUSKET_BALL, MusketBall.SUMMON_BEHAVIOR);
        BEHAVIORS.put(METEOR_SHOT, MeteorShot.SUMMON_BEHAVIOR);
        BEHAVIORS.put(SILVER_BULLET, SilverBullet.SUMMON_BEHAVIOR);
        BEHAVIORS.put(CRYSTAL_BULLET, CrystalBullet.SUMMON_BEHAVIOR);
        BEHAVIORS.put(CURSED_BULLET, CursedBullet.SUMMON_BEHAVIOR);
        BEHAVIORS.put(CHLOROPHYTE_BULLET, ChlorophyteBullet.SUMMON_BEHAVIOR);
        BEHAVIORS.put(HIGH_VELOCITY_BULLET, HighVelocityBullet.SUMMON_BEHAVIOR);
        BEHAVIORS.put(ICHOR_BULLET, IchorBullet.SUMMON_BEHAVIOR);
        BEHAVIORS.put(VENOM_BULLET, VenomBullet.SUMMON_BEHAVIOR);
        BEHAVIORS.put(PARTY_BULLET, PartyBullet.SUMMON_BEHAVIOR);
        BEHAVIORS.put(NANO_BULLET, NanoBullet.SUMMON_BEHAVIOR);
        BEHAVIORS.put(EXPLODING_BULLET, ExplodingBullet.SUMMON_BEHAVIOR);
        BEHAVIORS.put(GOLDEN_BULLET, GoldenBullet.SUMMON_BEHAVIOR);
        BEHAVIORS.put(LUMINITE_BULLET, LuminiteBullet.SUMMON_BEHAVIOR);
        BEHAVIORS.put(TUNGSTEN_BULLET, TungstenBullet.SUMMON_BEHAVIOR);
        BEHAVIORS.put(WOODEN_ARROW, WoodenArrow.SUMMON_BEHAVIOR);

        BEHAVIORS.put(SNOWBALL, SnowballCannon.SUMMON_BEHAVIOR);
        BEHAVIORS.put(FALLEN_STAR, StarCannon.SUMMON_BEHAVIOR);
        BEHAVIORS.put(FALLEN_STAR_SUPER, SuperStarShooter.SUMMON_BEHAVIOR);
        BEHAVIORS.put(FLAMETHROWER_FLAME, Flamethrower.SUMMON_BEHAVIOR);
        BEHAVIORS.put(ELF_MELTER_FLAME, ElfMelter.SUMMON_BEHAVIOR);
    }

    public static IStaticSummonBehavior getBehavior(String id) {
        return BEHAVIORS.getOrDefault(id, BEHAVIORS.get(DEFAULT));
    }
}
