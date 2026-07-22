package com.lzxnone.terraria.ui.config;

import com.lzxnone.terraria.effect.BloodButcheredEffect;
import com.lzxnone.terraria.effect.DemonicFlamesEffect;
import com.lzxnone.terraria.item.weapon.melee.BeeKeeper;
import com.lzxnone.terraria.item.weapon.melee.BladeOfGrass;
import com.lzxnone.terraria.item.weapon.melee.BloodButcherer;
import com.lzxnone.terraria.item.weapon.melee.DevilsDevastation;
import com.lzxnone.terraria.item.weapon.melee.EnchantedSword;
import com.lzxnone.terraria.item.weapon.melee.Excalibur;
import com.lzxnone.terraria.item.weapon.melee.FirstFractal;
import com.lzxnone.terraria.item.weapon.melee.InfluxWaver;
import com.lzxnone.terraria.item.weapon.melee.LightsBane;
import com.lzxnone.terraria.item.weapon.melee.Mace;
import com.lzxnone.terraria.item.weapon.melee.Meowmere;
import com.lzxnone.terraria.item.weapon.melee.Muramasa;
import com.lzxnone.terraria.item.weapon.melee.NightsEdge;
import com.lzxnone.terraria.item.weapon.melee.Seedler;
import com.lzxnone.terraria.item.weapon.melee.StarWrath;
import com.lzxnone.terraria.item.weapon.melee.Starfury;
import com.lzxnone.terraria.item.weapon.melee.TerraBlade;
import com.lzxnone.terraria.item.weapon.melee.Terragrim;
import com.lzxnone.terraria.item.weapon.melee.TheHorsemansBlade;
import com.lzxnone.terraria.item.weapon.melee.TrueExcalibur;
import com.lzxnone.terraria.item.weapon.melee.TrueNightsEdge;
import com.lzxnone.terraria.item.weapon.melee.Volcano;
import com.lzxnone.terraria.item.weapon.melee.Zenith;

import java.util.HashMap;
import java.util.Map;

public class ConfigList {
    public static final String WEAPON = "weapon";
    public static final String EFFECT = "effect";

    private static final Map<String, ConfigListItem[]> ITEMS = new HashMap<>();

    static {
        ITEMS.put(WEAPON, new ConfigListItem[]{
            EnchantedSword.CONFIG_LIST_ITEM,
            BeeKeeper.CONFIG_LIST_ITEM,
            Starfury.CONFIG_LIST_ITEM,
            Seedler.CONFIG_LIST_ITEM,
            TheHorsemansBlade.CONFIG_LIST_ITEM,
            InfluxWaver.CONFIG_LIST_ITEM,
            StarWrath.CONFIG_LIST_ITEM,
            Meowmere.CONFIG_LIST_ITEM,
            TerraBlade.CONFIG_LIST_ITEM,
            Zenith.CONFIG_LIST_ITEM,
            Excalibur.CONFIG_LIST_ITEM,
            NightsEdge.CONFIG_LIST_ITEM,
            TrueExcalibur.CONFIG_LIST_ITEM,
            TrueNightsEdge.CONFIG_LIST_ITEM,
            LightsBane.CONFIG_LIST_ITEM,
            BloodButcherer.CONFIG_LIST_ITEM,
            Muramasa.CONFIG_LIST_ITEM,
            BladeOfGrass.CONFIG_LIST_ITEM,
            Volcano.CONFIG_LIST_ITEM,
            Terragrim.CONFIG_LIST_ITEM,
            FirstFractal.CONFIG_LIST_ITEM,
            Mace.CONFIG_LIST_ITEM,
            DevilsDevastation.CONFIG_LIST_ITEM
        });
        ITEMS.put(EFFECT, new ConfigListItem[]{
            BloodButcheredEffect.CONFIG_LIST_ITEM,
            DemonicFlamesEffect.CONFIG_LIST_ITEM
        });
    }

    public static ConfigListItem[] getItems(String id) {
        return ITEMS.getOrDefault(id, ITEMS.get(WEAPON));
    }
}
