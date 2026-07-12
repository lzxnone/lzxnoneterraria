package com.lzxnone.terraria.item;

import com.lzxnone.terraria.item.weapon.melee.*;

import java.util.HashMap;
import java.util.Map;

public class ItemWaveBehaviors {
    private static final Map<String, IItemWaveBehavior> BEHAVIORS = new HashMap<>();

    public static final String DEFAULT = "default";
    public static final String BEE_KEEPER = "bee_keeper";
    public static final String THE_HORSEMANS_BLADE = "the_horsemans_blade";
    public static final String TERRA_BLADE = "terra_blade";
    public static final String TRUE_EXCALIBUR = "true_excalibur";
    public static final String TRUE_NIGHTS_EDGE = "true_nights_edge";
    public static final String EXCALIBUR = "excalibur";
    public static final String NIGHTS_EDGE = "nights_edge";
    public static final String LIGHTS_BANE = "lights_bane";
    public static final String BLOOD_BUTCHERER = "blood_butcherer";
    public static final String MURAMASA = "muramasa";
    public static final String VOLCANO = "volcano";

    static {
        BEHAVIORS.put(DEFAULT, new IItemWaveBehavior() {});
        BEHAVIORS.put(BEE_KEEPER, BeeKeeper.ITEM_WAVE_BEHAVIOR);
        BEHAVIORS.put(THE_HORSEMANS_BLADE, TheHorsemansBlade.ITEM_WAVE_BEHAVIOR);
        BEHAVIORS.put(TERRA_BLADE, TerraBlade.ITEM_WAVE_BEHAVIOR);
        BEHAVIORS.put(TRUE_EXCALIBUR, TrueExcalibur.ITEM_WAVE_BEHAVIOR);
        BEHAVIORS.put(TRUE_NIGHTS_EDGE, TrueNightsEdge.ITEM_WAVE_BEHAVIOR);
        BEHAVIORS.put(EXCALIBUR, Excalibur.ITEM_WAVE_BEHAVIOR);
        BEHAVIORS.put(NIGHTS_EDGE, NightsEdge.ITEM_WAVE_BEHAVIOR);
        BEHAVIORS.put(LIGHTS_BANE, LightsBane.ITEM_WAVE_BEHAVIOR);
        BEHAVIORS.put(BLOOD_BUTCHERER, BloodButcherer.ITEM_WAVE_BEHAVIOR);
        BEHAVIORS.put(MURAMASA, Muramasa.ITEM_WAVE_BEHAVIOR);
        BEHAVIORS.put(VOLCANO, Volcano.ITEM_WAVE_BEHAVIOR);
    }

    public static IItemWaveBehavior getBehavior(String id) {
        return BEHAVIORS.getOrDefault(id, BEHAVIORS.get(DEFAULT));
    }
}
