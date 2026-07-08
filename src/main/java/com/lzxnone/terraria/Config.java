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
    public static final ModConfigSpec.DoubleValue ZENITH_TRAIL_B;
    public static final ModConfigSpec.IntValue ZENITH_TRAIL_OFFSET;
    public static final ModConfigSpec.IntValue ZENITH_WEAPON_COUNT;
    public static final ModConfigSpec.DoubleValue ZENITH_BOUNDING_BOX_SIZE;
    public static final ModConfigSpec.IntValue ZENITH_CYCLE;
    public static final ModConfigSpec.DoubleValue ZENITH_SCALE;
    public static final ModConfigSpec.DoubleValue ZENITH_TRAIL_ALPHA;
    public static final ModConfigSpec.IntValue ZENITH_TRAIL_MAX_LENGTH;

    public static double enchantedSwordDamage;
    public static int enchantedSwordLifetime;
    public static double enchantedSwordSpeed;

    public static int beeKeeperMaxBees;
    public static int beeKeeperConfusionDuration;
    public static double beeKeeperBeeDamage;

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
    public static double zenithTrailB;
    public static int zenithTrailOffset;
    public static int zenithWeaponCount;
    public static double zenithBoundingBoxSize;
    public static int zenithCycle;
    public static double zenithScale;
    public static double zenithTrailAlpha;
    public static int zenithTrailMaxLength;

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


        builder.push("terra_blade_settings");

        TERRA_PROJECTILE_ALIGN_TO_BLADE = builder
            .comment("弹射是否匹配刀光方向")
            .define("terra_projectile_align_to_blade", true);

        TERRA_BLADE_ROTATE_RANGE = builder
            .comment("刀光偏转角范围")
            .defineInRange("terra_blade_rotate_range", 30, 0, 90);

        TERRA_PROJECTILE_ROTATE_RANGE = builder
            .comment("弹射偏转角范围")
            .defineInRange("terra_projectile_rotate_range", 30, 0, 90);

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
            zenithTrailB = ZENITH_TRAIL_B.get();
            zenithTrailOffset = ZENITH_TRAIL_OFFSET.get();
            zenithWeaponCount = ZENITH_WEAPON_COUNT.get();
            zenithBoundingBoxSize = ZENITH_BOUNDING_BOX_SIZE.get();
            zenithCycle = ZENITH_CYCLE.get();
            zenithScale = ZENITH_SCALE.get();
            zenithTrailAlpha = ZENITH_TRAIL_ALPHA.get();
            zenithTrailMaxLength = ZENITH_TRAIL_MAX_LENGTH.get();
        }
    }
}
