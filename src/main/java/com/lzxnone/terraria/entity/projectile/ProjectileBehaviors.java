package com.lzxnone.terraria.entity.projectile;

import com.lzxnone.terraria.item.weapon.melee.EnchantedSword;
import com.lzxnone.terraria.item.weapon.melee.Seedler;
import com.lzxnone.terraria.item.weapon.melee.Starfury;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import java.util.HashMap;
import java.util.Map;

public class ProjectileBehaviors {
    private static final Map<String, IProjectileBehavior> BEHAVIORS = new HashMap<>();

    public static final String DEFAULT = "default";
    public static final String ENCHANTED_SWORD_BEAM = "enchanted_sword_beam";
    public static final String STARFURY_STAR = "starfury_star";
    public static final String SEEDLER_NUT = "seedler_nut";
    public static final String SEEDLER_THORN = "seedler_thorn";

    static {
        BEHAVIORS.put(DEFAULT, new IProjectileBehavior() {
            @Override
            public void onHitEntity(TextureProjectile projectile, EntityHitResult result) {
                if(projectile.level().isClientSide()) return;
                projectile.discard();
            }
            @Override
            public void onHitBlock(TextureProjectile projectile, BlockHitResult result) {
                if(projectile.level().isClientSide()) return;
                if(!projectile.level().getBlockState(result.getBlockPos()).getCollisionShape(projectile.level(), result.getBlockPos()).isEmpty()) {
                    projectile.discard();
                }
            }
            @Override
            public void onDied(TextureProjectile projectile) {
                projectile.discard();
            }
        });
        BEHAVIORS.put(ENCHANTED_SWORD_BEAM, EnchantedSword.PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(STARFURY_STAR, Starfury.PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(SEEDLER_NUT, Seedler.NUT_PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(SEEDLER_THORN, Seedler.THORN_PROJECTILE_BEHAVIOR);
    }

    public static IProjectileBehavior getBehavior(String id) {
        return BEHAVIORS.getOrDefault(id, BEHAVIORS.get(DEFAULT));
    }
}