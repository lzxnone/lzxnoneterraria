package com.lzxnone.terraria.entity.projectile;

import com.lzxnone.terraria.item.weapon.melee.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import java.util.HashMap;
import java.util.Map;

public class StaticProjectileBehaviors {
    private static final Map<String, IStaticProjectileBehavior> BEHAVIORS = new HashMap<>();

    public static final String DEFAULT = "default";
    public static final String ENCHANTED_SWORD_BEAM = "enchanted_sword_beam";
    public static final String STARFURY_STAR = "starfury_star";
    public static final String SEEDLER_NUT = "seedler_nut";
    public static final String SEEDLER_THORN = "seedler_thorn";
    public static final String INFLUX_WAVER_BEAM = "influx_waver";
    public static final String STAR_WRATH_STAR = "star_wrath_star";

    static {
        BEHAVIORS.put(DEFAULT, new IStaticProjectileBehavior() {
            @Override
            public void onHitEntity(StaticProjectile projectile, EntityHitResult result) {
                if(projectile.level().isClientSide()) return;
                projectile.discard();
            }
            @Override
            public void onHitBlock(StaticProjectile projectile, BlockHitResult result) {
                if(projectile.level().isClientSide()) return;
                if(!projectile.level().getBlockState(result.getBlockPos()).getCollisionShape(projectile.level(), result.getBlockPos()).isEmpty()) {
                    projectile.discard();
                }
            }
        });
        BEHAVIORS.put(ENCHANTED_SWORD_BEAM, EnchantedSword.PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(STARFURY_STAR, Starfury.PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(SEEDLER_THORN, Seedler.PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(STAR_WRATH_STAR, StarWrath.PROJECTILE_BEHAVIOR);
    }

    public static IStaticProjectileBehavior getBehavior(String id) {
        return BEHAVIORS.getOrDefault(id, BEHAVIORS.get(DEFAULT));
    }
}