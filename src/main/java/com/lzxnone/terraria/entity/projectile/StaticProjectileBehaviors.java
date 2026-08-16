package com.lzxnone.terraria.entity.projectile;

import com.lzxnone.terraria.item.ammo.CrystalBullet;
import com.lzxnone.terraria.item.effect.FallenStarSummoner;
import com.lzxnone.terraria.item.weapon.magic.NebulaArcanum;
import com.lzxnone.terraria.item.weapon.magic.StellarTune;
import com.lzxnone.terraria.item.weapon.melee.*;
import com.lzxnone.terraria.item.weapon.melee.flail.Flail;
import com.lzxnone.terraria.item.weapon.ranged.Flamethrower;
import com.lzxnone.terraria.item.weapon.ranged.SuperStarShooter;
import com.lzxnone.terraria.item.weapon.summon.whip.Possession;
import com.lzxnone.terraria.item.weapon.summon.minion.StardustCellStaff;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import java.util.HashMap;
import java.util.Map;

public class StaticProjectileBehaviors {
    private static final Map<String, IStaticProjectileBehavior> BEHAVIORS = new HashMap<>();

    public static final String DEFAULT = "default";
    public static final String ENCHANTED_SWORD_BEAM = "enchanted_sword_beam";
    public static final String STARFURY_STAR = "starfury_star";
    public static final String SEEDLER_THORN = "seedler_thorn";
    public static final String STAR_WRATH_STAR = "star_wrath_star";
    public static final String ZENITH_PROJECTILE = "zenith_projectile";
    public static final String MURAMASA_PROJECTILE = "muramasa_projectile";
    public static final String LEAF_PROJECTILE = "leaf_projectile";
    public static final String FLAIL_PROJECTILE = "flail_projectile";
    public static final String DEVILS_DEVASTATION_PROJECTILE = "devils_devastation_projectile";
    public static final String DEVILS_DEVASTATION_PROJECTILE2 = "devils_devastation_projectile2";
    public static final String DEVILS_DEVASTATION_PROJECTILE3 = "devils_devastation_projectile3";
    public static final String CRYSTAL_FRAGMENT = "crystal_fragment";
    public static final String SUPER_STAR_PRISMATIC_BOLT = "super_star_prismatic_blot";
    public static final String POSSESSION_PROJECTILE = "possession_projectile";
    public static final String ACCESSORY_FALLEN_STAR_PROJECTILE = "accessory_fallen_star_projectile";
    public static final String NEBULA_ARCANUM_ORBIT_PROJECTILE = "nebula_arcanum_orbit_projectile";
    public static final String STELLAR_TUNE_PROJECTILE = "stellar_tune_projectile";
    public static final String STARDUST_CELL_PROJECTILE = "stardust_cell_projectile";

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
        BEHAVIORS.put(ZENITH_PROJECTILE, Zenith.PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(MURAMASA_PROJECTILE, Muramasa.PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(LEAF_PROJECTILE, BladeOfGrass.PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(FLAIL_PROJECTILE, Flail.PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(DEVILS_DEVASTATION_PROJECTILE, DevilsDevastation.PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(DEVILS_DEVASTATION_PROJECTILE2, DevilsDevastation.PROJECTILE_BEHAVIOR2);
        BEHAVIORS.put(DEVILS_DEVASTATION_PROJECTILE3, DevilsDevastation.PROJECTILE_BEHAVIOR3);
        BEHAVIORS.put(CRYSTAL_FRAGMENT, CrystalBullet.PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(SUPER_STAR_PRISMATIC_BOLT, SuperStarShooter.PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(POSSESSION_PROJECTILE, Possession.PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(ACCESSORY_FALLEN_STAR_PROJECTILE, FallenStarSummoner.PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(NEBULA_ARCANUM_ORBIT_PROJECTILE, NebulaArcanum.PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(STELLAR_TUNE_PROJECTILE, StellarTune.PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(STARDUST_CELL_PROJECTILE, StardustCellStaff.PROJECTILE_BEHAVIOR);
    }

    public static IStaticProjectileBehavior getBehavior(String id) {
        return BEHAVIORS.getOrDefault(id, BEHAVIORS.get(DEFAULT));
    }
}
