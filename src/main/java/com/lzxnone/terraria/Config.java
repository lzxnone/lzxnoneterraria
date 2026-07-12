package com.lzxnone.terraria;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID, value = Dist.CLIENT)
public class Config {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue ENCHANTED_SWORD_DAMAGE;
    public static final ModConfigSpec.IntValue ENCHANTED_SWORD_LIFETIME;
    public static final ModConfigSpec.DoubleValue ENCHANTED_SWORD_SPEED;

    public static final ModConfigSpec.IntValue BEE_KEEPER_MAX_BEES;
    public static final ModConfigSpec.IntValue BEE_KEEPER_CONFUSION_DURATION;
    public static final ModConfigSpec.DoubleValue BEE_KEEPER_BEE_DAMAGE;

    public static final ModConfigSpec.IntValue BLOOD_BUTCHERER_EFFECT_DURATION;
    public static final ModConfigSpec.IntValue BLOOD_BUTCHERER_MAX_LEVEL;
    public static final ModConfigSpec.DoubleValue BLOOD_BUTCHERER_DAMAGE_PER_LEVEL;

    public static final ModConfigSpec.DoubleValue BLADE_OF_GRASS_PROJECTILE_DAMAGE;
    public static final ModConfigSpec.IntValue BLADE_OF_GRASS_EFFECT_DURATION;
    public static final ModConfigSpec.DoubleValue BLADE_OF_GRASS_TARGET_RANGE;
    public static final ModConfigSpec.IntValue BLADE_OF_GRASS_PROJECTILE_LIFETIME;
    public static final ModConfigSpec.IntValue BLADE_OF_GRASS_PROJECTILE_MAX_HIT_COUNT;
    public static final ModConfigSpec.DoubleValue BLADE_OF_GRASS_RADIUS_GROWTH;
    public static final ModConfigSpec.DoubleValue BLADE_OF_GRASS_ROTATION_SPEED;

    public static final ModConfigSpec.DoubleValue VOLCANO_EXPLOSION_RANGE;
    public static final ModConfigSpec.DoubleValue VOLCANO_EXPLOSION_DAMAGE;
    public static final ModConfigSpec.IntValue VOLCANO_EXPLOSION_MAX_HIT_COUNT;
    public static final ModConfigSpec.IntValue VOLCANO_IGNITE_SECONDS;

    public static final ModConfigSpec.DoubleValue STARFURY_DAMAGE;
    public static final ModConfigSpec.DoubleValue STARFURY_MAX_RANGE;
    public static final ModConfigSpec.DoubleValue STARFURY_SPEED;

    public static final ModConfigSpec.DoubleValue SEEDLER_NUT_GRAVITY;
    public static final ModConfigSpec.DoubleValue SEEDLER_NUT_BOUNCE;
    public static final ModConfigSpec.DoubleValue SEEDLER_NUT_INITIAL_SPEED;
    public static final ModConfigSpec.DoubleValue SEEDLER_THORN_DAMAGE;
    public static final ModConfigSpec.IntValue SEEDLER_THORN_LIFETIME;
    public static final ModConfigSpec.DoubleValue SEEDLER_THORN_SPEED;

    public static final ModConfigSpec.IntValue HORSEMANS_BLADE_ROTATE_RANGE;
    public static final ModConfigSpec.IntValue HORSEMANS_BLADE_MAX_HIT_COUNT;
    public static final ModConfigSpec.DoubleValue HORSEMANS_BLADE_DAMAGE;
    public static final ModConfigSpec.DoubleValue HORSEMANS_PUMPKIN_SPAWN_RANGE;
    public static final ModConfigSpec.DoubleValue HORSEMANS_PUMPKIN_SPEED;
    public static final ModConfigSpec.DoubleValue HORSEMANS_PUMPKIN_DAMAGE;
    public static final ModConfigSpec.DoubleValue HORSEMANS_PUMPKIN_MAX_TARGET_RANGE;

    public static final ModConfigSpec.DoubleValue INFLUX_WAVER_DAMAGE;
    public static final ModConfigSpec.IntValue INFLUX_WAVER_MAX_COUNT;
    public static final ModConfigSpec.DoubleValue INFLUX_WAVER_SPEED;
    public static final ModConfigSpec.DoubleValue INFLUX_WAVER_SPAWN_RANGE;
    public static final ModConfigSpec.DoubleValue INFLUX_WAVER_TARGET_RANGE;

    public static final ModConfigSpec.DoubleValue STAR_WRATH_DAMAGE;
    public static final ModConfigSpec.DoubleValue STAR_WRATH_SPEED;
    public static final ModConfigSpec.DoubleValue STAR_WRATH_MAX_RANGE;

    public static final ModConfigSpec.DoubleValue MEOWMERE_DAMAGE;
    public static final ModConfigSpec.DoubleValue MEOWMERE_GRAVITY;
    public static final ModConfigSpec.DoubleValue MEOWMERE_INITIAL_SPEED;

    public static final ModConfigSpec.DoubleValue LIGHTS_BANE_BIG_DAMAGE;
    public static final ModConfigSpec.DoubleValue LIGHTS_BANE_SMALL_DAMAGE;
    public static final ModConfigSpec.DoubleValue LIGHTS_BANE_TARGET_RANGE;

    public static final ModConfigSpec.DoubleValue MURAMASA_BEAM_DAMAGE;

    public static final ModConfigSpec.DoubleValue TERRAGRIM_HIT_RANGE;
    public static final ModConfigSpec.DoubleValue TERRAGRIM_DAMAGE;

    public static final ModConfigSpec.IntValue EXCALIBUR_ROTATE_RANGE;
    public static final ModConfigSpec.IntValue EXCALIBUR_MAX_HIT_COUNT;
    public static final ModConfigSpec.DoubleValue EXCALIBUR_DAMAGE;

    public static final ModConfigSpec.BooleanValue NIGHTS_EDGE_PROJECTILE_ALIGN_TO_BLADE;
    public static final ModConfigSpec.IntValue NIGHTS_EDGE_ROTATE_RANGE;
    public static final ModConfigSpec.IntValue NIGHTS_EDGE_PROJECTILE_ROTATE_RANGE;
    public static final ModConfigSpec.IntValue NIGHTS_EDGE_MAX_HIT_COUNT;
    public static final ModConfigSpec.IntValue NIGHTS_EDGE_PROJECTILE_MAX_HIT_COUNT;
    public static final ModConfigSpec.DoubleValue NIGHTS_EDGE_DAMAGE;
    public static final ModConfigSpec.DoubleValue NIGHTS_EDGE_PROJECTILE_DAMAGE;
    public static final ModConfigSpec.DoubleValue NIGHTS_EDGE_PROJECTILE_SPEED;
    public static final ModConfigSpec.IntValue NIGHTS_EDGE_PROJECTILE_LIFETIME;
    public static final ModConfigSpec.IntValue NIGHTS_EDGE_PROJECTILE_ROTATION_SPEED;

    public static final ModConfigSpec.BooleanValue TERRA_PROJECTILE_ALIGN_TO_BLADE;
    public static final ModConfigSpec.IntValue TERRA_BLADE_ROTATE_RANGE;
    public static final ModConfigSpec.IntValue TERRA_PROJECTILE_ROTATE_RANGE;
    public static final ModConfigSpec.IntValue TERRA_BLADE_MAX_HIT_COUNT;
    public static final ModConfigSpec.DoubleValue TERRA_BLADE_DAMAGE;
    public static final ModConfigSpec.DoubleValue TERRA_PROJECTILE_DAMAGE;
    public static final ModConfigSpec.DoubleValue TERRA_PROJECTILE_SPEED;
    public static final ModConfigSpec.IntValue TERRA_PROJECTILE_LIFETIME;
    public static final ModConfigSpec.DoubleValue TERRA_PROJECTILE_DAMAGE_DECAY;

    public static final ModConfigSpec.DoubleValue ZENITH_DAMAGE;
    public static final ModConfigSpec.DoubleValue ZENITH_MAX_RANGE;
    public static final ModConfigSpec.BooleanValue ZENITH_DISTANCE_MODE;
    public static final ModConfigSpec.IntValue ZENITH_SCROLL_SPEED;
    public static final ModConfigSpec.DoubleValue ZENITH_TRAIL_B;
    public static final ModConfigSpec.IntValue ZENITH_TRAIL_OFFSET;
    public static final ModConfigSpec.IntValue ZENITH_WEAPON_COUNT;
    public static final ModConfigSpec.DoubleValue ZENITH_BOUNDING_BOX_SIZE;
    public static final ModConfigSpec.IntValue ZENITH_CYCLE;
    public static final ModConfigSpec.DoubleValue ZENITH_SCALE;
    public static final ModConfigSpec.DoubleValue ZENITH_TRAIL_ALPHA;
    public static final ModConfigSpec.IntValue ZENITH_TRAIL_MAX_LENGTH;

    public static final ModConfigSpec.IntValue TRUE_EXCALIBUR_ROTATE_RANGE;
    public static final ModConfigSpec.IntValue TRUE_EXCALIBUR_MAX_HIT_COUNT;
    public static final ModConfigSpec.DoubleValue TRUE_EXCALIBUR_DAMAGE;

    public static final ModConfigSpec.BooleanValue TRUE_NIGHTS_EDGE_PROJECTILE_ALIGN_TO_BLADE;
    public static final ModConfigSpec.IntValue TRUE_NIGHTS_EDGE_ROTATE_RANGE;
    public static final ModConfigSpec.IntValue TRUE_NIGHTS_EDGE_PROJECTILE_ROTATE_RANGE;
    public static final ModConfigSpec.IntValue TRUE_NIGHTS_EDGE_MAX_HIT_COUNT;
    public static final ModConfigSpec.IntValue TRUE_NIGHTS_EDGE_PROJECTILE_MAX_HIT_COUNT;
    public static final ModConfigSpec.DoubleValue TRUE_NIGHTS_EDGE_DAMAGE;
    public static final ModConfigSpec.DoubleValue TRUE_NIGHTS_EDGE_PROJECTILE_DAMAGE;
    public static final ModConfigSpec.DoubleValue TRUE_NIGHTS_EDGE_PROJECTILE_MAX_SPEED;
    public static final ModConfigSpec.DoubleValue TRUE_NIGHTS_EDGE_PROJECTILE_ACCELERATION;
    public static final ModConfigSpec.IntValue TRUE_NIGHTS_EDGE_PROJECTILE_LIFETIME;
    public static final ModConfigSpec.IntValue TRUE_NIGHTS_EDGE_PROJECTILE_ROTATION_SPEED;

    public static double enchantedSwordDamage;
    public static int enchantedSwordLifetime;
    public static double enchantedSwordSpeed;

    public static int beeKeeperMaxBees;
    public static int beeKeeperConfusionDuration;
    public static double beeKeeperBeeDamage;

    public static int bloodButchererEffectDuration;
    public static int bloodButchererMaxLevel;
    public static double bloodButchererDamagePerLevel;

    public static double bladeOfGrassProjectileDamage;
    public static int bladeOfGrassEffectDuration;
    public static double bladeOfGrassTargetRange;
    public static int bladeOfGrassProjectileLifetime;
    public static int bladeOfGrassProjectileMaxHitCount;
    public static double bladeOfGrassRadiusGrowth;
    public static double bladeOfGrassRotationSpeed;

    public static double volcanoExplosionRange;
    public static double volcanoExplosionDamage;
    public static int volcanoExplosionMaxHitCount;
    public static int volcanoIgniteSeconds;

    public static double starfuryDamage;
    public static double starfuryMaxRange;
    public static double starfurySpeed;

    public static double seedlerNutGravity;
    public static double seedlerNutBounce;
    public static double seedlerNutInitialSpeed;
    public static double seedlerThornDamage;
    public static int seedlerThornLifetime;
    public static double seedlerThornSpeed;

    public static int horsemansBladeRotateRange;
    public static int horsemansBladeMaxHitCount;
    public static double horsemansBladeDamage;
    public static double horsemansPumpkinSpawnRange;
    public static double horsemansPumpkinSpeed;
    public static double horsemansPumpkinDamage;
    public static double horsemansPumpkinMaxTargetRange;

    public static double influxWaverDamage;
    public static int influxWaverMaxCount;
    public static double influxWaverSpeed;
    public static double influxWaverSpawnRange;
    public static double influxWaverTargetRange;

    public static double starWrathDamage;
    public static double starWrathSpeed;
    public static double starWrathMaxRange;

    public static double meowmereDamage;
    public static double meowmereGravity;
    public static double meowmereInitialSpeed;

    public static double lightsBaneBigDamage;
    public static double lightsBaneSmallDamage;
    public static double lightsBaneTargetRange;

    public static double muramasaBeamDamage;

    public static double terragrimHitRange;
    public static double terragrimDamage;

    public static int excaliburRotateRange;
    public static int excaliburMaxHitCount;
    public static double excaliburDamage;

    public static boolean nightsEdgeProjectileAlignToBlade;
    public static int nightsEdgeRotateRange;
    public static int nightsEdgeProjectileRotateRange;
    public static int nightsEdgeMaxHitCount;
    public static int nightsEdgeProjectileMaxHitCount;
    public static double nightsEdgeDamage;
    public static double nightsEdgeProjectileDamage;
    public static double nightsEdgeProjectileSpeed;
    public static int nightsEdgeProjectileLifetime;
    public static int nightsEdgeProjectileRotationSpeed;

    public static boolean terraProjectileAlignToBlade;
    public static int terraBladeRotateRange;
    public static int terraProjectileRotateRange;
    public static int terraBladeMaxHitCount;
    public static double terraBladeDamage;
    public static double terraProjectileDamage;
    public static double terraProjectileSpeed;
    public static int terraProjectileLifetime;
    public static double terraProjectileDamageDecay;

    public static double zenithDamage;
    public static double zenithMaxRange;
    public static boolean zenithDistanceMode;
    public static int zenithMouseSpeed;
    public static double zenithTrailB;
    public static int zenithTrailOffset;
    public static int zenithWeaponCount;
    public static double zenithBoundingBoxSize;
    public static int zenithCycle;
    public static double zenithScale;
    public static double zenithTrailAlpha;
    public static int zenithTrailMaxLength;

    public static int trueExcaliburRotateRange;
    public static int trueExcaliburMaxHitCount;
    public static double trueExcaliburDamage;

    public static boolean trueNightsEdgeProjectileAlignToBlade;
    public static int trueNightsEdgeRotateRange;
    public static int trueNightsEdgeProjectileRotateRange;
    public static int trueNightsEdgeMaxHitCount;
    public static int trueNightsEdgeProjectileMaxHitCount;
    public static double trueNightsEdgeDamage;
    public static double trueNightsEdgeProjectileDamage;
    public static double trueNightsEdgeProjectileMaxSpeed;
    public static double trueNightsEdgeProjectileAcceleration;
    public static int trueNightsEdgeProjectileLifetime;
    public static int trueNightsEdgeProjectileRotationSpeed;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("enchanted_sword_settings");

        ENCHANTED_SWORD_DAMAGE  = builder
            .comment("弹射伤害")
            .defineInRange("enchanted_sword_damage", 4.0, 0, 2147483647);

        ENCHANTED_SWORD_LIFETIME  = builder
            .comment("弹射持续时间")
            .defineInRange("enchanted_sword_lifetime", 60, 1, 1200);

        ENCHANTED_SWORD_SPEED  = builder
            .comment("弹射速度")
            .defineInRange("enchanted_sword_speed", 1.0, 0, 10);

        builder.pop();


        builder.push("bee_keeper_settings");

        BEE_KEEPER_MAX_BEES = builder
            .comment("最大蜜蜂生成数量")
            .defineInRange("bee_keeper_max_bees", 3, 0, 10);

        BEE_KEEPER_CONFUSION_DURATION = builder
            .comment("困惑持续时间")
            .defineInRange("bee_keeper_confusion_duration", 40, 0, 1200);

        BEE_KEEPER_BEE_DAMAGE = builder
            .comment("蜜蜂的攻击伤害")
            .defineInRange("bee_keeper_bee_damage", 2.0, 0, 2147483647);

        builder.pop();

        builder.push("blood_butcherer_settings");

        BLOOD_BUTCHERER_EFFECT_DURATION = builder
            .comment("血腥屠宰效果持续时间（刻）")
            .defineInRange("blood_butcherer_effect_duration", 180, 0, 72000);

        BLOOD_BUTCHERER_MAX_LEVEL = builder
            .comment("血腥屠宰效果最大等级")
            .defineInRange("blood_butcherer_max_level", 4, 0, 255);

        BLOOD_BUTCHERER_DAMAGE_PER_LEVEL = builder
            .comment("血腥屠宰效果每级伤害")
            .defineInRange("blood_butcherer_damage_per_level", 0.5, 0, 8388600);

        builder.pop();

        builder.push("blade_of_grass_settings");

        BLADE_OF_GRASS_PROJECTILE_DAMAGE = builder
            .comment("弹射伤害")
            .defineInRange("blade_of_grass_projectile_damage", 2.0, 0, 2147483647.0);

        BLADE_OF_GRASS_EFFECT_DURATION = builder
            .comment("效果持续时间（刻）")
            .defineInRange("blade_of_grass_effect_duration", 140, 0, 72000);

        BLADE_OF_GRASS_TARGET_RANGE = builder
            .comment("弹射最大寻敌范围")
            .defineInRange("blade_of_grass_target_range", 8.0, 1.0, 64.0);

        BLADE_OF_GRASS_PROJECTILE_LIFETIME = builder
            .comment("弹射持续时间")
            .defineInRange("blade_of_grass_projectile_lifetime", 60, 1, 600);

        BLADE_OF_GRASS_PROJECTILE_MAX_HIT_COUNT = builder
            .comment("弹射最大击中数")
            .defineInRange("blade_of_grass_projectile_max_hit_count", 2, 0, 100);

        BLADE_OF_GRASS_RADIUS_GROWTH = builder
            .comment("弹射半径增大速度")
            .defineInRange("blade_of_grass_radius_growth", 0.05, 0, 1.0);

        BLADE_OF_GRASS_ROTATION_SPEED = builder
            .comment("弹射旋转速度")
            .defineInRange("blade_of_grass_rotation_speed", 0.2, 0, 1.0);

        builder.pop();

        builder.push("volcano_settings");

        VOLCANO_EXPLOSION_RANGE = builder
            .comment("爆炸范围")
            .defineInRange("volcano_explosion_range", 4.0, 1.0, 64.0);

        VOLCANO_EXPLOSION_DAMAGE = builder
            .comment("爆炸伤害")
            .defineInRange("volcano_explosion_damage", 6.0, 0, 2147483647.0);

        VOLCANO_EXPLOSION_MAX_HIT_COUNT = builder
            .comment("爆炸最大击中数")
            .defineInRange("volcano_explosion_max_hit_count", 2, 0, 100);

        VOLCANO_IGNITE_SECONDS = builder
            .comment("着火时间（秒）")
            .defineInRange("volcano_ignite_seconds", 3, 0, 60);

        builder.pop();

        builder.push("starfury_settings");

        STARFURY_DAMAGE = builder
            .comment("弹射伤害")
            .defineInRange("starfury_damage", 6.0, 0, 2147483647);

        STARFURY_MAX_RANGE = builder
            .comment("最大范围")
            .defineInRange("starfury_max_range", 24.0, 1.0, 1024.0);

        STARFURY_SPEED = builder
            .comment("弹射速度")
            .defineInRange("starfury_speed", 2.0, 0, 10);

        builder.pop();


        builder.push("seedler_settings");

        SEEDLER_NUT_GRAVITY = builder
            .comment("坚果重力")
            .defineInRange("seedler_nut_gravity", 0.075, 0.0, 1.0);

        SEEDLER_NUT_BOUNCE = builder
            .comment("坚果弹力")
            .defineInRange("seedler_nut_bounce", 0.5, 0.0, 1.0);

        SEEDLER_NUT_INITIAL_SPEED = builder
            .comment("坚果初速度")
            .defineInRange("seedler_nut_initial_speed", 0.75, 0, 10.0);

        SEEDLER_THORN_DAMAGE = builder
            .comment("荆棘伤害")
            .defineInRange("seedler_thorn_damage", 2.0, 0, 2147483647);

        SEEDLER_THORN_LIFETIME = builder
            .comment("荆棘持续时间")
            .defineInRange("seedler_thorn_lifetime", 30, 1, 1200);

        SEEDLER_THORN_SPEED = builder
            .comment("荆棘速度")
            .defineInRange("seedler_thorn_speed", 2.0, 0, 10);

        builder.pop();


        builder.push("horsemans_blade_settings");

        HORSEMANS_BLADE_ROTATE_RANGE = builder
            .comment("刀光偏转角范围")
            .defineInRange("horsemans_blade_rotate_range", 45, 0, 90);

        HORSEMANS_BLADE_MAX_HIT_COUNT = builder
            .comment("刀光单次最大击中生物数量")
            .defineInRange("horsemans_blade_max_hit_count", 3, 0, 100);

        HORSEMANS_BLADE_DAMAGE = builder
            .comment("刀光伤害")
            .defineInRange("horsemans_blade_damage", 15.0, 0, 2147483647.0);

        HORSEMANS_PUMPKIN_SPAWN_RANGE = builder
            .comment("南瓜生成范围")
            .defineInRange("horsemans_pumpkin_spawn_range", 16.0, 1.0, 64.0);

        HORSEMANS_PUMPKIN_SPEED = builder
            .comment("南瓜速度")
            .defineInRange("horsemans_pumpkin_speed", 0.5, 0, 10);

        HORSEMANS_PUMPKIN_DAMAGE = builder
            .comment("南瓜伤害")
            .defineInRange("horsemans_pumpkin_damage", 15.0, 0, 2147483647.0);

        HORSEMANS_PUMPKIN_MAX_TARGET_RANGE = builder
            .comment("南瓜最大寻敌范围")
            .defineInRange("horsemans_pumpkin_max_target_range", 32.0, 1.0, 64.0);

        builder.pop();


        builder.push("influx_waver_settings");

        INFLUX_WAVER_DAMAGE = builder
            .comment("弹射伤害")
            .defineInRange("influx_waver_damage", 12.0, 0, 2147483647.0);

        INFLUX_WAVER_MAX_COUNT = builder
            .comment("弹射最大生成数量")
            .defineInRange("influx_waver_max_count", 2, 0, 10);

        INFLUX_WAVER_SPEED = builder
            .comment("弹射速度")
            .defineInRange("influx_waver_speed", 1.0, 0, 10);

        INFLUX_WAVER_SPAWN_RANGE = builder
            .comment("弹射生成范围")
            .defineInRange("influx_waver_spawn_range", 8.0, 1.0, 64.0);

        INFLUX_WAVER_TARGET_RANGE = builder
            .comment("弹射寻敌范围")
            .defineInRange("influx_waver_target_range", 24.0, 1.0, 64.0);

        builder.pop();


        builder.push("star_wrath_settings");

        STAR_WRATH_DAMAGE = builder
            .comment("弹射伤害")
            .defineInRange("star_wrath_damage", 16.0, 0, 2147483647.0);

        STAR_WRATH_SPEED = builder
            .comment("弹射速度")
            .defineInRange("star_wrath_speed", 2.0, 0, 10);

        STAR_WRATH_MAX_RANGE = builder
            .comment("最大范围")
            .defineInRange("star_wrath_max_range", 48.0, 1.0, 1024.0);

        builder.pop();


        builder.push("meowmere_settings");

        MEOWMERE_DAMAGE = builder
            .comment("弹射伤害")
            .defineInRange("meowmere_damage", 20.0, 0, 2147483647.0);

        MEOWMERE_GRAVITY = builder
            .comment("弹射重力")
            .defineInRange("meowmere_gravity", 0.025, 0.0, 1.0);

        MEOWMERE_INITIAL_SPEED = builder
            .comment("弹射初速度")
            .defineInRange("meowmere_initial_speed", 1.5, 0, 10);

        builder.pop();


        builder.push("lights_bane_settings");

        LIGHTS_BANE_BIG_DAMAGE = builder
            .comment("大斩击伤害")
            .defineInRange("lights_bane_big_damage", 4.0, 0, 2147483647.0);

        LIGHTS_BANE_SMALL_DAMAGE = builder
            .comment("小斩击伤害")
            .defineInRange("lights_bane_small_damage", 2.0, 0, 2147483647.0);

        LIGHTS_BANE_TARGET_RANGE = builder
            .comment("寻敌范围")
            .defineInRange("lights_bane_target_range", 2.0, 1.0, 64.0);

        builder.pop();


        builder.push("muramasa_settings");

        MURAMASA_BEAM_DAMAGE = builder
            .comment("蓝色光线伤害")
            .defineInRange("muramasa_beam_damage", 2.5, 0, 2147483647.0);

        builder.pop();


        builder.push("terragrim_settings");

        TERRAGRIM_HIT_RANGE = builder
            .comment("碰撞箱范围")
            .defineInRange("terragrim_hit_range", 2.5, 0.5, 10.0);

        TERRAGRIM_DAMAGE = builder
            .comment("伤害")
            .defineInRange("terragrim_damage", 0.25, 0, 2147483647.0);

        builder.pop();


        builder.push("excalibur_settings");

        EXCALIBUR_ROTATE_RANGE = builder
            .comment("刀光偏转角范围")
            .defineInRange("excalibur_rotate_range", 45, 0, 90);

        EXCALIBUR_MAX_HIT_COUNT = builder
            .comment("刀光单次最大击中生物数量")
            .defineInRange("excalibur_max_hit_count", 3, 0, 100);

        EXCALIBUR_DAMAGE = builder
            .comment("刀光伤害")
            .defineInRange("excalibur_damage", 8.0, 0, 2147483647.0);

        builder.pop();


        builder.push("nights_edge_settings");

        NIGHTS_EDGE_PROJECTILE_ALIGN_TO_BLADE = builder
            .comment("弹射是否匹配刀光方向")
            .define("nights_edge_projectile_align_to_blade", true);

        NIGHTS_EDGE_ROTATE_RANGE = builder
            .comment("刀光偏转角范围")
            .defineInRange("nights_edge_rotate_range", 45, 0, 90);

        NIGHTS_EDGE_PROJECTILE_ROTATE_RANGE = builder
            .comment("弹射偏转角范围")
            .defineInRange("nights_edge_projectile_rotate_range", 40, 0, 90);

        NIGHTS_EDGE_MAX_HIT_COUNT = builder
            .comment("刀光单次最大击中生物数量")
            .defineInRange("nights_edge_max_hit_count", 2, 0, 100);

        NIGHTS_EDGE_PROJECTILE_MAX_HIT_COUNT = builder
            .comment("弹射刀光单次最大击中生物数量")
            .defineInRange("nights_edge_projectile_max_hit_count", 2, 0, 100);

        NIGHTS_EDGE_DAMAGE = builder
            .comment("刀光伤害")
            .defineInRange("nights_edge_damage", 7.0, 0, 2147483647.0);

        NIGHTS_EDGE_PROJECTILE_DAMAGE = builder
            .comment("弹射伤害")
            .defineInRange("nights_edge_projectile_damage", 7.0, 0, 2147483647.0);

        NIGHTS_EDGE_PROJECTILE_SPEED = builder
            .comment("弹射速度")
            .defineInRange("nights_edge_projectile_speed", 0.25, 0, 10);

        NIGHTS_EDGE_PROJECTILE_LIFETIME = builder
            .comment("弹射持续时间")
            .defineInRange("nights_edge_projectile_lifetime", 10, 1, 1200);

        NIGHTS_EDGE_PROJECTILE_ROTATION_SPEED = builder
            .comment("弹射旋转速度")
            .defineInRange("nights_edge_projectile_rotation_speed", 36, 0, 360);

        builder.pop();


        builder.push("terra_blade_settings");

        TERRA_PROJECTILE_ALIGN_TO_BLADE = builder
            .comment("弹射是否匹配刀光方向")
            .define("terra_projectile_align_to_blade", true);

        TERRA_BLADE_ROTATE_RANGE = builder
            .comment("刀光偏转角范围")
            .defineInRange("terra_blade_rotate_range", 45, 0, 90);

        TERRA_PROJECTILE_ROTATE_RANGE = builder
            .comment("弹射偏转角范围")
            .defineInRange("terra_projectile_rotate_range", 45, 0, 90);

        TERRA_BLADE_MAX_HIT_COUNT = builder
            .comment("刀光单次最大击中生物数量")
            .defineInRange("terra_blade_max_hit_count", 3, 0, 100);

        TERRA_BLADE_DAMAGE = builder
            .comment("刀光伤害")
            .defineInRange("terra_blade_damage", 11.0, 0, 2147483647.0);

        TERRA_PROJECTILE_DAMAGE = builder
            .comment("弹射伤害")
            .defineInRange("terra_projectile_damage", 11.0, 0, 2147483647.0);

        TERRA_PROJECTILE_SPEED = builder
            .comment("弹射速度")
            .defineInRange("terra_projectile_speed", 2.0, 0, 10);

        TERRA_PROJECTILE_LIFETIME = builder
            .comment("弹射持续时间")
            .defineInRange("terra_projectile_lifetime", 60, 1, 1200);

        TERRA_PROJECTILE_DAMAGE_DECAY = builder
            .comment("弹射伤害递减系数")
            .defineInRange("terra_projectile_damage_decay", 0.75, 0.0, 1.0);

        builder.pop();


        builder.push("zenith_settings");

        ZENITH_DAMAGE = builder
            .comment("弹射单次伤害x-2x")
            .defineInRange("zenith_damage", 4.0, 1.0, 2147483647);

        ZENITH_MAX_RANGE = builder
            .comment("最大范围(2a)")
            .defineInRange("zenith_max_range", 64.0, 1.0, 1024.0);

        ZENITH_DISTANCE_MODE = builder
            .comment("使用相对距离")
            .define("zenith_distance_mode", true);

        ZENITH_SCROLL_SPEED = builder
            .comment("滚轮调节距离速度")
            .defineInRange("zenith_mouse_speed", 4, 0, 128);

        ZENITH_TRAIL_B = builder
            .comment("椭圆轨道半短轴(b)")
            .defineInRange("zenith_trail_b", 4.0, 0.1, 512.0);

        ZENITH_TRAIL_OFFSET = builder
            .comment("轨道偏转角范围")
            .defineInRange("zenith_trail_offset", 30, 0, 90);

        ZENITH_WEAPON_COUNT = builder
            .comment("单把武器实际数量")
            .defineInRange("zenith_weapon_count", 3, 1, 3);

        ZENITH_BOUNDING_BOX_SIZE = builder
            .comment("弹射碰撞箱大小")
            .defineInRange("zenith_bounding_box_size", 1.5, 0.1, 4.0);

        ZENITH_CYCLE = builder
            .comment("弹射运动周期")
            .defineInRange("zenith_cycle", 20, 10, 100);

        ZENITH_SCALE = builder
            .comment("弹射大小倍数")
            .defineInRange("zenith_scale", 2.0, 0.1, 10.0);

        ZENITH_TRAIL_ALPHA = builder
            .comment("尾迹透明度")
            .defineInRange("zenith_trail_alpha", 0.15, 0.0, 1.0);

        ZENITH_TRAIL_MAX_LENGTH = builder
            .comment("尾迹长度")
            .defineInRange("zenith_trail_max_length", 10, 0, 100);

        builder.pop();

        builder.push("true_excalibur_settings");

        TRUE_EXCALIBUR_ROTATE_RANGE = builder
            .comment("刀光偏转角范围")
            .defineInRange("true_excalibur_rotate_range", 45, 0, 90);

        TRUE_EXCALIBUR_MAX_HIT_COUNT = builder
            .comment("刀光单次最大击中生物数量")
            .defineInRange("true_excalibur_max_hit_count", 6, 0, 100);

        TRUE_EXCALIBUR_DAMAGE = builder
            .comment("刀光伤害")
            .defineInRange("true_excalibur_damage", 8.0, 0, 2147483647.0);

        builder.pop();

        builder.push("true_nights_edge_settings");

        TRUE_NIGHTS_EDGE_PROJECTILE_ALIGN_TO_BLADE = builder
            .comment("弹射是否匹配刀光方向")
            .define("true_nights_edge_projectile_align_to_blade", true);

        TRUE_NIGHTS_EDGE_ROTATE_RANGE = builder
            .comment("刀光偏转角范围")
            .defineInRange("true_nights_edge_rotate_range", 45, 0, 90);

        TRUE_NIGHTS_EDGE_PROJECTILE_ROTATE_RANGE = builder
            .comment("弹射偏转角范围")
            .defineInRange("true_nights_edge_projectile_rotate_range", 45, 0, 90);

        TRUE_NIGHTS_EDGE_MAX_HIT_COUNT = builder
            .comment("刀光单次最大击中生物数量")
            .defineInRange("true_nights_edge_max_hit_count", 2, 0, 100);

        TRUE_NIGHTS_EDGE_PROJECTILE_MAX_HIT_COUNT = builder
            .comment("弹射刀光单次最大击中生物数量")
            .defineInRange("true_nights_edge_projectile_max_hit_count", 3, 0, 100);

        TRUE_NIGHTS_EDGE_DAMAGE = builder
            .comment("刀光伤害")
            .defineInRange("true_nights_edge_damage", 8.0, 0, 2147483647.0);

        TRUE_NIGHTS_EDGE_PROJECTILE_DAMAGE = builder
            .comment("弹射伤害")
            .defineInRange("true_nights_edge_projectile_damage", 4.0, 0, 2147483647.0);

        TRUE_NIGHTS_EDGE_PROJECTILE_MAX_SPEED = builder
            .comment("弹射最大速度")
            .defineInRange("true_nights_edge_projectile_max_speed", 2.0, 0, 10);

        TRUE_NIGHTS_EDGE_PROJECTILE_ACCELERATION = builder
            .comment("弹射加速度")
            .defineInRange("true_nights_edge_projectile_acceleration", 0.03, 0, 10.0);

        TRUE_NIGHTS_EDGE_PROJECTILE_LIFETIME = builder
            .comment("弹射持续时间")
            .defineInRange("true_nights_edge_projectile_lifetime", 60, 1, 1200);

        TRUE_NIGHTS_EDGE_PROJECTILE_ROTATION_SPEED = builder
            .comment("弹射旋转速度")
            .defineInRange("true_nights_edge_projectile_rotation_speed", 30, 0, 360);

        builder.pop();

        SPEC = builder.build();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if(event.getConfig().getSpec() == SPEC) {
            enchantedSwordDamage = ENCHANTED_SWORD_DAMAGE.get();
            enchantedSwordLifetime = ENCHANTED_SWORD_LIFETIME.get();
            enchantedSwordSpeed = ENCHANTED_SWORD_SPEED.get();
            beeKeeperMaxBees = BEE_KEEPER_MAX_BEES.get();
            beeKeeperConfusionDuration = BEE_KEEPER_CONFUSION_DURATION.get();
            beeKeeperBeeDamage = BEE_KEEPER_BEE_DAMAGE.get();
            bloodButchererEffectDuration = BLOOD_BUTCHERER_EFFECT_DURATION.get();
            bloodButchererMaxLevel = BLOOD_BUTCHERER_MAX_LEVEL.get();
            bloodButchererDamagePerLevel = BLOOD_BUTCHERER_DAMAGE_PER_LEVEL.get();
            bladeOfGrassProjectileDamage = BLADE_OF_GRASS_PROJECTILE_DAMAGE.get();
            bladeOfGrassEffectDuration = BLADE_OF_GRASS_EFFECT_DURATION.get();
            bladeOfGrassTargetRange = BLADE_OF_GRASS_TARGET_RANGE.get();
            bladeOfGrassProjectileLifetime = BLADE_OF_GRASS_PROJECTILE_LIFETIME.get();
            bladeOfGrassProjectileMaxHitCount = BLADE_OF_GRASS_PROJECTILE_MAX_HIT_COUNT.get();
            bladeOfGrassRadiusGrowth = BLADE_OF_GRASS_RADIUS_GROWTH.get();
            bladeOfGrassRotationSpeed = BLADE_OF_GRASS_ROTATION_SPEED.get();
            volcanoExplosionRange = VOLCANO_EXPLOSION_RANGE.get();
            volcanoExplosionDamage = VOLCANO_EXPLOSION_DAMAGE.get();
            volcanoExplosionMaxHitCount = VOLCANO_EXPLOSION_MAX_HIT_COUNT.get();
            volcanoIgniteSeconds = VOLCANO_IGNITE_SECONDS.get();
            starfuryDamage = STARFURY_DAMAGE.get();
            starfuryMaxRange = STARFURY_MAX_RANGE.get();
            starfurySpeed = STARFURY_SPEED.get();
            seedlerNutGravity = SEEDLER_NUT_GRAVITY.get();
            seedlerNutBounce = SEEDLER_NUT_BOUNCE.get();
            seedlerNutInitialSpeed = SEEDLER_NUT_INITIAL_SPEED.get();
            seedlerThornDamage = SEEDLER_THORN_DAMAGE.get();
            seedlerThornLifetime = SEEDLER_THORN_LIFETIME.get();
            seedlerThornSpeed = SEEDLER_THORN_SPEED.get();
            horsemansBladeRotateRange = HORSEMANS_BLADE_ROTATE_RANGE.get();
            horsemansBladeMaxHitCount = HORSEMANS_BLADE_MAX_HIT_COUNT.get();
            horsemansBladeDamage = HORSEMANS_BLADE_DAMAGE.get();
            horsemansPumpkinSpawnRange = HORSEMANS_PUMPKIN_SPAWN_RANGE.get();
            horsemansPumpkinSpeed = HORSEMANS_PUMPKIN_SPEED.get();
            horsemansPumpkinDamage = HORSEMANS_PUMPKIN_DAMAGE.get();
            horsemansPumpkinMaxTargetRange = HORSEMANS_PUMPKIN_MAX_TARGET_RANGE.get();
            influxWaverDamage = INFLUX_WAVER_DAMAGE.get();
            influxWaverMaxCount = INFLUX_WAVER_MAX_COUNT.get();
            influxWaverSpeed = INFLUX_WAVER_SPEED.get();
            influxWaverSpawnRange = INFLUX_WAVER_SPAWN_RANGE.get();
            influxWaverTargetRange = INFLUX_WAVER_TARGET_RANGE.get();
            starWrathDamage = STAR_WRATH_DAMAGE.get();
            starWrathSpeed = STAR_WRATH_SPEED.get();
            starWrathMaxRange = STAR_WRATH_MAX_RANGE.get();
            meowmereDamage = MEOWMERE_DAMAGE.get();
            meowmereGravity = MEOWMERE_GRAVITY.get();
            meowmereInitialSpeed = MEOWMERE_INITIAL_SPEED.get();
            lightsBaneBigDamage = LIGHTS_BANE_BIG_DAMAGE.get();
            lightsBaneSmallDamage = LIGHTS_BANE_SMALL_DAMAGE.get();
            lightsBaneTargetRange = LIGHTS_BANE_TARGET_RANGE.get();
            muramasaBeamDamage = MURAMASA_BEAM_DAMAGE.get();
            terragrimHitRange = TERRAGRIM_HIT_RANGE.get();
            terragrimDamage = TERRAGRIM_DAMAGE.get();
            excaliburRotateRange = EXCALIBUR_ROTATE_RANGE.get();
            excaliburMaxHitCount = EXCALIBUR_MAX_HIT_COUNT.get();
            excaliburDamage = EXCALIBUR_DAMAGE.get();
            nightsEdgeProjectileAlignToBlade = NIGHTS_EDGE_PROJECTILE_ALIGN_TO_BLADE.get();
            nightsEdgeRotateRange = NIGHTS_EDGE_ROTATE_RANGE.get();
            nightsEdgeProjectileRotateRange = NIGHTS_EDGE_PROJECTILE_ROTATE_RANGE.get();
            nightsEdgeMaxHitCount = NIGHTS_EDGE_MAX_HIT_COUNT.get();
            nightsEdgeProjectileMaxHitCount = NIGHTS_EDGE_PROJECTILE_MAX_HIT_COUNT.get();
            nightsEdgeDamage = NIGHTS_EDGE_DAMAGE.get();
            nightsEdgeProjectileDamage = NIGHTS_EDGE_PROJECTILE_DAMAGE.get();
            nightsEdgeProjectileSpeed = NIGHTS_EDGE_PROJECTILE_SPEED.get();
            nightsEdgeProjectileLifetime = NIGHTS_EDGE_PROJECTILE_LIFETIME.get();
            nightsEdgeProjectileRotationSpeed = NIGHTS_EDGE_PROJECTILE_ROTATION_SPEED.get();
            terraProjectileAlignToBlade = TERRA_PROJECTILE_ALIGN_TO_BLADE.get();
            terraBladeRotateRange = TERRA_BLADE_ROTATE_RANGE.get();
            terraProjectileRotateRange = TERRA_PROJECTILE_ROTATE_RANGE.get();
            terraBladeMaxHitCount = TERRA_BLADE_MAX_HIT_COUNT.get();
            terraBladeDamage = TERRA_BLADE_DAMAGE.get();
            terraProjectileDamage = TERRA_PROJECTILE_DAMAGE.get();
            terraProjectileSpeed = TERRA_PROJECTILE_SPEED.get();
            terraProjectileLifetime = TERRA_PROJECTILE_LIFETIME.get();
            terraProjectileDamageDecay = TERRA_PROJECTILE_DAMAGE_DECAY.get();
            zenithDamage = ZENITH_DAMAGE.get();
            zenithMaxRange = ZENITH_MAX_RANGE.get();
            zenithDistanceMode = ZENITH_DISTANCE_MODE.get();
            zenithMouseSpeed = ZENITH_SCROLL_SPEED.get();
            zenithTrailB = ZENITH_TRAIL_B.get();
            zenithTrailOffset = ZENITH_TRAIL_OFFSET.get();
            zenithWeaponCount = ZENITH_WEAPON_COUNT.get();
            zenithBoundingBoxSize = ZENITH_BOUNDING_BOX_SIZE.get();
            zenithCycle = ZENITH_CYCLE.get();
            zenithScale = ZENITH_SCALE.get();
            zenithTrailAlpha = ZENITH_TRAIL_ALPHA.get();
            zenithTrailMaxLength = ZENITH_TRAIL_MAX_LENGTH.get();
            trueExcaliburRotateRange = TRUE_EXCALIBUR_ROTATE_RANGE.get();
            trueExcaliburMaxHitCount = TRUE_EXCALIBUR_MAX_HIT_COUNT.get();
            trueExcaliburDamage = TRUE_EXCALIBUR_DAMAGE.get();
            trueNightsEdgeProjectileAlignToBlade = TRUE_NIGHTS_EDGE_PROJECTILE_ALIGN_TO_BLADE.get();
            trueNightsEdgeRotateRange = TRUE_NIGHTS_EDGE_ROTATE_RANGE.get();
            trueNightsEdgeProjectileRotateRange = TRUE_NIGHTS_EDGE_PROJECTILE_ROTATE_RANGE.get();
            trueNightsEdgeMaxHitCount = TRUE_NIGHTS_EDGE_MAX_HIT_COUNT.get();
            trueNightsEdgeProjectileMaxHitCount = TRUE_NIGHTS_EDGE_PROJECTILE_MAX_HIT_COUNT.get();
            trueNightsEdgeDamage = TRUE_NIGHTS_EDGE_DAMAGE.get();
            trueNightsEdgeProjectileDamage = TRUE_NIGHTS_EDGE_PROJECTILE_DAMAGE.get();
            trueNightsEdgeProjectileMaxSpeed = TRUE_NIGHTS_EDGE_PROJECTILE_MAX_SPEED.get();
            trueNightsEdgeProjectileAcceleration = TRUE_NIGHTS_EDGE_PROJECTILE_ACCELERATION.get();
            trueNightsEdgeProjectileLifetime = TRUE_NIGHTS_EDGE_PROJECTILE_LIFETIME.get();
            trueNightsEdgeProjectileRotationSpeed = TRUE_NIGHTS_EDGE_PROJECTILE_ROTATION_SPEED.get();
        }
    }
}
