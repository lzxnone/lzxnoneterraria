package com.lzxnone.terraria.entity.summon;

import com.lzxnone.terraria.item.weapon.melee.*;

import java.util.HashMap;
import java.util.Map;

public class StaticSummonBehaviors {
    private static final Map<String, IStaticSummonBehavior> BEHAVIORS = new HashMap<>();

    public static final String DEFAULT = "default";
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
    public static final String DEVILS_DEVASTATION_PROJECTILE = "devils_devastation_projectile";
    public static final String DEVILS_DEVASTATION_LIGHTNING = "devils_devastation_lightning";

    static {
        BEHAVIORS.put(DEFAULT, new IStaticSummonBehavior() {});
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
        BEHAVIORS.put(DEVILS_DEVASTATION_PROJECTILE, DevilsDevastation.SUMMON_BEHAVIOR);
        BEHAVIORS.put(DEVILS_DEVASTATION_LIGHTNING, DevilsDevastation.SUMMON_BEHAVIOR2);
    }

    public static IStaticSummonBehavior getBehavior(String id) {
        return BEHAVIORS.getOrDefault(id, BEHAVIORS.get(DEFAULT));
    }
}
