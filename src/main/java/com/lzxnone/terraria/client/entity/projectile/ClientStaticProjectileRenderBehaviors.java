package com.lzxnone.terraria.client.entity.projectile;

import com.lzxnone.terraria.client.item.accessory.ClientAccessoryFallenStar;
import com.lzxnone.terraria.client.item.magic.ClientNebulaArcanum;
import com.lzxnone.terraria.client.item.melee.ClientBladeOfGrass;
import com.lzxnone.terraria.client.item.melee.ClientDevilsDevastation;
import com.lzxnone.terraria.client.item.melee.ClientMace;
import com.lzxnone.terraria.client.item.melee.ClientMuramasa;
import com.lzxnone.terraria.client.item.melee.ClientZenith;
import com.lzxnone.terraria.client.item.ranged.ClientFlamethrower;
import com.lzxnone.terraria.client.item.ranged.ClientSuperStarShooter;
import com.lzxnone.terraria.client.item.summon.ClientPossession;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;

import java.util.HashMap;
import java.util.Map;

public class ClientStaticProjectileRenderBehaviors {
    private static final Map<String, IStaticProjectileRenderBehavior> BEHAVIORS = new HashMap<>();
    private static final IStaticProjectileRenderBehavior DEFAULT_BEHAVIOR = new IStaticProjectileRenderBehavior() {};

    static {
        BEHAVIORS.put(StaticProjectileBehaviors.DEFAULT, DEFAULT_BEHAVIOR);
        BEHAVIORS.put(StaticProjectileBehaviors.ENCHANTED_SWORD_BEAM, DEFAULT_BEHAVIOR);
        BEHAVIORS.put(StaticProjectileBehaviors.STARFURY_STAR, DEFAULT_BEHAVIOR);
        BEHAVIORS.put(StaticProjectileBehaviors.SEEDLER_THORN, DEFAULT_BEHAVIOR);
        BEHAVIORS.put(StaticProjectileBehaviors.STAR_WRATH_STAR, DEFAULT_BEHAVIOR);
        BEHAVIORS.put(StaticProjectileBehaviors.ZENITH_PROJECTILE, ClientZenith.PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(StaticProjectileBehaviors.MURAMASA_PROJECTILE, ClientMuramasa.PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(StaticProjectileBehaviors.LEAF_PROJECTILE, ClientBladeOfGrass.PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(StaticProjectileBehaviors.MACE_PROJECTILE, ClientMace.PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(StaticProjectileBehaviors.DEVILS_DEVASTATION_PROJECTILE, ClientDevilsDevastation.PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(StaticProjectileBehaviors.DEVILS_DEVASTATION_PROJECTILE2, ClientDevilsDevastation.PROJECTILE_BEHAVIOR2);
        BEHAVIORS.put(StaticProjectileBehaviors.DEVILS_DEVASTATION_PROJECTILE3, ClientDevilsDevastation.PROJECTILE_BEHAVIOR3);
        BEHAVIORS.put(StaticProjectileBehaviors.CRYSTAL_FRAGMENT, DEFAULT_BEHAVIOR);
        BEHAVIORS.put(StaticProjectileBehaviors.SUPER_STAR_PRISMATIC_BOLT, ClientSuperStarShooter.PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(StaticProjectileBehaviors.POSSESSION_PROJECTILE, ClientPossession.PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(StaticProjectileBehaviors.ACCESSORY_FALLEN_STAR_PROJECTILE, ClientAccessoryFallenStar.PROJECTILE_BEHAVIOR);
        BEHAVIORS.put(StaticProjectileBehaviors.NEBULA_ARCANUM_ORBIT_PROJECTILE, ClientNebulaArcanum.PROJECTILE_BEHAVIOR);
    }

    public static IStaticProjectileRenderBehavior getBehavior(String id) {
        return BEHAVIORS.getOrDefault(id, BEHAVIORS.get(StaticProjectileBehaviors.DEFAULT));
    }
}
