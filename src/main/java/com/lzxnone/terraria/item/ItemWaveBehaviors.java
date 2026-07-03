package com.lzxnone.terraria.item;

import com.lzxnone.terraria.item.weapon.melee.BeeKeeper;
import com.lzxnone.terraria.item.weapon.melee.TerraBlade;
import com.lzxnone.terraria.item.weapon.melee.TheHorsemansBlade;

import java.util.HashMap;
import java.util.Map;

public class ItemWaveBehaviors {
    private static final Map<String, IItemWaveBehavior> BEHAVIORS = new HashMap<>();

    public static final String DEFAULT = "default";
    public static final String BEE_KEEPER = "bee_keeper";
    public static final String THE_HORSEMANS_BLADE = "the_horsemans_blade";
    public static final String TERRA_BLADE = "terra_blade";

    static {
        BEHAVIORS.put(DEFAULT, new IItemWaveBehavior() {});
        BEHAVIORS.put(BEE_KEEPER, BeeKeeper.ITEM_WAVE_BEHAVIOR);
        BEHAVIORS.put(THE_HORSEMANS_BLADE, TheHorsemansBlade.ITEM_WAVE_BEHAVIOR);
        BEHAVIORS.put(TERRA_BLADE, TerraBlade.ITEM_WAVE_BEHAVIOR);
    }

    public static IItemWaveBehavior getBehavior(String id) {
        return BEHAVIORS.getOrDefault(id, BEHAVIORS.get(DEFAULT));
    }
}
