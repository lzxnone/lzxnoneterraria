package com.lzxnone.terraria.entity.beam;

import com.lzxnone.terraria.item.weapon.melee.*;

import java.util.HashMap;
import java.util.Map;

public class SwordBeamBehaviors {
    private static final Map<String, ISwordBeamBehavior> BEHAVIORS = new HashMap<>();

    public static final String DEFAULT = "default";
    public static final String THE_HORSEMANS_BLADE = "the_horsemans_blade";
    public static final String TERRA_BLADE = "terra_blade";
    public static final String TRUE_EXCALIBUR = "true_excalibur";
    public static final String TRUE_NIGHTS_EDGE = "true_nights_edge";
    public static final String EXCALIBUR = "excalibur";
    public static final String NIGHTS_EDGE = "nights_edge";

    static {
        BEHAVIORS.put(DEFAULT, new ISwordBeamBehavior() {});
        BEHAVIORS.put(THE_HORSEMANS_BLADE, TheHorsemansBlade.SWORD_BEAM_BEHAVIOR);
        BEHAVIORS.put(TERRA_BLADE, TerraBlade.SWORD_BEAM_BEHAVIOR);
        BEHAVIORS.put(TRUE_EXCALIBUR, TrueExcalibur.SWORD_BEAM_BEHAVIOR);
        BEHAVIORS.put(TRUE_NIGHTS_EDGE, TrueNightsEdge.SWORD_BEAM_BEHAVIOR);
        BEHAVIORS.put(EXCALIBUR, Excalibur.SWORD_BEAM_BEHAVIOR);
        BEHAVIORS.put(NIGHTS_EDGE, NightsEdge.SWORD_BEAM_BEHAVIOR);
    }

    public static ISwordBeamBehavior getBehavior(String id) {
        return BEHAVIORS.getOrDefault(id, BEHAVIORS.get(DEFAULT));
    }
}
