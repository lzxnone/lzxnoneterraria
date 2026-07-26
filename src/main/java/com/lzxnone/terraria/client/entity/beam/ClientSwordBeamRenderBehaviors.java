package com.lzxnone.terraria.client.entity.beam;

import com.lzxnone.terraria.client.item.melee.ClientNightsEdge;
import com.lzxnone.terraria.client.item.melee.ClientTrueExcalibur;
import com.lzxnone.terraria.client.item.melee.ClientTrueNightsEdge;
import com.lzxnone.terraria.entity.beam.SwordBeamBehaviors;

import java.util.HashMap;
import java.util.Map;

public class ClientSwordBeamRenderBehaviors {
    private static final Map<String, ISwordBeamRenderBehavior> BEHAVIORS = new HashMap<>();
    private static final ISwordBeamRenderBehavior DEFAULT_BEHAVIOR = new ISwordBeamRenderBehavior() {};

    static {
        BEHAVIORS.put(SwordBeamBehaviors.DEFAULT, DEFAULT_BEHAVIOR);
        BEHAVIORS.put(SwordBeamBehaviors.THE_HORSEMANS_BLADE, DEFAULT_BEHAVIOR);
        BEHAVIORS.put(SwordBeamBehaviors.TERRA_BLADE, DEFAULT_BEHAVIOR);
        BEHAVIORS.put(SwordBeamBehaviors.TRUE_EXCALIBUR, ClientTrueExcalibur.SWORD_BEAM_BEHAVIOR);
        BEHAVIORS.put(SwordBeamBehaviors.TRUE_NIGHTS_EDGE, ClientTrueNightsEdge.SWORD_BEAM_BEHAVIOR);
        BEHAVIORS.put(SwordBeamBehaviors.EXCALIBUR, DEFAULT_BEHAVIOR);
        BEHAVIORS.put(SwordBeamBehaviors.NIGHTS_EDGE, ClientNightsEdge.SWORD_BEAM_BEHAVIOR);
    }

    public static ISwordBeamRenderBehavior getBehavior(String id) {
        return BEHAVIORS.getOrDefault(id, BEHAVIORS.get(SwordBeamBehaviors.DEFAULT));
    }
}
