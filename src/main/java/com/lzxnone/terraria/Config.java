package com.lzxnone.terraria;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID, value = Dist.CLIENT)
public class Config {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue ZENITH_DAMAGE;
    public static final ModConfigSpec.DoubleValue ZENITH_MAX_RANGE;
    public static final ModConfigSpec.DoubleValue ZENITH_TRAIL_B;
    public static final ModConfigSpec.DoubleValue ZENITH_BOUNDING_BOX_SIZE;
    public static final ModConfigSpec.IntValue ZENITH_CYCLE;
    public static final ModConfigSpec.DoubleValue ZENITH_SCALE;
    public static final ModConfigSpec.DoubleValue ZENITH_TRAIL_ALPHA;
    public static final ModConfigSpec.IntValue ZENITH_TRAIL_MAX_LENGTH;

    public static double zenithDamage;
    public static double zenithMaxRange;
    public static double zenithTrailB;
    public static double zenithBoundingBoxSize;
    public static int zenithCycle;
    public static double zenithScale;
    public static double zenithTrailAlpha;
    public static int zenithTrailMaxLength;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("zenith_settings");

        ZENITH_DAMAGE = builder
            .comment("弹射单次伤害x-2x")
            .defineInRange("zenith_damage", 4.0, 1.0, 114514.0);

        ZENITH_MAX_RANGE = builder
            .comment("最大范围(2a)")
            .defineInRange("zenith_max_range", 64.0, 1.0, 1024.0);

        ZENITH_TRAIL_B = builder
            .comment("椭圆轨道半短轴(b)")
            .defineInRange("zenith_trail_b", 4.0, 0.1, 512.0);

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
            zenithDamage = ZENITH_DAMAGE.get();
            zenithMaxRange = ZENITH_MAX_RANGE.get();
            zenithTrailB = ZENITH_TRAIL_B.get();
            zenithBoundingBoxSize = ZENITH_BOUNDING_BOX_SIZE.get();
            zenithCycle = ZENITH_CYCLE.get();
            zenithScale = ZENITH_SCALE.get();
            zenithTrailAlpha = ZENITH_TRAIL_ALPHA.get();
            zenithTrailMaxLength = ZENITH_TRAIL_MAX_LENGTH.get();
        }
    }
}
