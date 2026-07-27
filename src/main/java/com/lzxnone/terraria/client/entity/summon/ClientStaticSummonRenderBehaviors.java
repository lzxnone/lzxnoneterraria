package com.lzxnone.terraria.client.entity.summon;

import com.lzxnone.terraria.client.item.ammo.ClientBulletAmmo;
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
import com.lzxnone.terraria.client.item.summon.ClientTerraprisma;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;

import java.util.HashMap;
import java.util.Map;

public class ClientStaticSummonRenderBehaviors {
    private static final Map<String, IStaticSummonRenderBehavior> BEHAVIORS = new HashMap<>();
    private static final IStaticSummonRenderBehavior DEFAULT_BEHAVIOR = new IStaticSummonRenderBehavior() {};

    static {
        BEHAVIORS.put(StaticSummonBehaviors.DEFAULT, DEFAULT_BEHAVIOR);

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
    }

    public static IStaticSummonRenderBehavior getBehavior(String id) {
        return BEHAVIORS.getOrDefault(id, BEHAVIORS.get(StaticSummonBehaviors.DEFAULT));
    }
}
