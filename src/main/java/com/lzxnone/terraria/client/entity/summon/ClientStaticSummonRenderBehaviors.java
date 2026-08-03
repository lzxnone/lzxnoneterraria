package com.lzxnone.terraria.client.entity.summon;

import com.lzxnone.terraria.client.item.ClientDropFallenStar;
import com.lzxnone.terraria.client.item.ranged.ClientBulletAmmo;
import com.lzxnone.terraria.client.item.magic.ClientLastPrism;
import com.lzxnone.terraria.client.item.melee.ClientDevilsDevastation;
import com.lzxnone.terraria.client.item.melee.ClientFirstFractal;
import com.lzxnone.terraria.client.item.melee.ClientLightsBane;
import com.lzxnone.terraria.client.item.melee.ClientMace;
import com.lzxnone.terraria.client.item.melee.ClientMeowmere;
import com.lzxnone.terraria.client.item.melee.ClientNightsEdge;
import com.lzxnone.terraria.client.item.melee.ClientTerraBlade;
import com.lzxnone.terraria.client.item.melee.ClientTerragrim;
import com.lzxnone.terraria.client.item.melee.ClientTrueNightsEdge;
import com.lzxnone.terraria.client.item.ranged.ClientFlamethrower;
import com.lzxnone.terraria.client.item.ranged.ClientStarCannon;
import com.lzxnone.terraria.client.item.ranged.ClientSuperStarShooter;
import com.lzxnone.terraria.client.item.summon.ClientStardustDragonStaff;
import com.lzxnone.terraria.client.item.summon.ClientTerraprisma;
import com.lzxnone.terraria.client.item.summon.ClientWhip;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;

import java.util.HashMap;
import java.util.Map;

public class ClientStaticSummonRenderBehaviors {
    private static final Map<String, IStaticSummonRenderBehavior> BEHAVIORS = new HashMap<>();
    private static final IStaticSummonRenderBehavior DEFAULT_BEHAVIOR = new IStaticSummonRenderBehavior() {};

    static {
        BEHAVIORS.put(StaticSummonBehaviors.DEFAULT, DEFAULT_BEHAVIOR);

        BEHAVIORS.put(StaticSummonBehaviors.DROP_FALLEN_STAR, ClientDropFallenStar.SUMMON_BEHAVIOR);

        BEHAVIORS.put(StaticSummonBehaviors.SEEDLER_NUT, DEFAULT_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.THE_HORSEMANS_BLADE_JACK, DEFAULT_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.INFLUX_WAVER_BEAM, DEFAULT_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.MEOWMERE_PROJECTILE, ClientMeowmere.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.TERRA_BLADE_BEAM, ClientTerraBlade.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.TRUE_NIGHTS_EDGE_BEAM, ClientTrueNightsEdge.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.NIGHTS_EDGE_BEAM, ClientNightsEdge.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.LIGHTS_BANE_SLASH, ClientLightsBane.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.VOLCANO_LIGHT, DEFAULT_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.TERRAGRIM_BEAM, ClientTerragrim.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.FIRST_FRACTAL_PROJECTILE, ClientFirstFractal.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.MACE_PROJECTILE, ClientMace.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.DEVILS_DEVASTATION_STUCK_PROJECTILE, DEFAULT_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.DEVILS_DEVASTATION_LIGHTNING, ClientDevilsDevastation.SUMMON_BEHAVIOR2);
        BEHAVIORS.put(StaticSummonBehaviors.DEVILS_DEVASTATION_MARK, ClientDevilsDevastation.SUMMON_BEHAVIOR3);
        BEHAVIORS.put(StaticSummonBehaviors.DEVILS_DEVASTATION_MARK_LIGHTNING, ClientDevilsDevastation.SUMMON_BEHAVIOR4);
        BEHAVIORS.put(StaticSummonBehaviors.LAST_PRISM_TRI, ClientLastPrism.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.LAST_PRISM_BEAM, ClientLastPrism.SUMMON_BEHAVIOR2);
        BEHAVIORS.put(StaticSummonBehaviors.TERRAPRISMA, ClientTerraprisma.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.POSSESSION, ClientWhip.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.POSSESSION_VISION, DEFAULT_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.STARDUST_DRAGON_STAFF, ClientStardustDragonStaff.SUMMON_BEHAVIOR);

        BEHAVIORS.put(StaticSummonBehaviors.MUSKET_BALL, ClientBulletAmmo.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.METEOR_SHOT, ClientBulletAmmo.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.SILVER_BULLET, ClientBulletAmmo.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.CRYSTAL_BULLET, ClientBulletAmmo.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.CURSED_BULLET, ClientBulletAmmo.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.CHLOROPHYTE_BULLET, ClientBulletAmmo.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.HIGH_VELOCITY_BULLET, ClientBulletAmmo.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.ICHOR_BULLET, ClientBulletAmmo.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.VENOM_BULLET, ClientBulletAmmo.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.PARTY_BULLET, ClientBulletAmmo.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.NANO_BULLET, ClientBulletAmmo.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.EXPLODING_BULLET, ClientBulletAmmo.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.GOLDEN_BULLET, ClientBulletAmmo.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.LUMINITE_BULLET, ClientBulletAmmo.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.TUNGSTEN_BULLET, ClientBulletAmmo.SUMMON_BEHAVIOR);

        BEHAVIORS.put(StaticSummonBehaviors.SNOWBALL, DEFAULT_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.FALLEN_STAR, ClientStarCannon.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.FALLEN_STAR_SUPER, ClientSuperStarShooter.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.FLAMETHROWER_FLAME, ClientFlamethrower.SUMMON_BEHAVIOR);
        BEHAVIORS.put(StaticSummonBehaviors.ELF_MELTER_FLAME, ClientFlamethrower.SUMMON_BEHAVIOR);
    }

    public static IStaticSummonRenderBehavior getBehavior(String id) {
        return BEHAVIORS.getOrDefault(id, BEHAVIORS.get(StaticSummonBehaviors.DEFAULT));
    }
}
