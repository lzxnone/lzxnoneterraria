package com.lzxnone.terraria.ui.config;

import com.lzxnone.terraria.effect.BloodButcheredEffect;
import com.lzxnone.terraria.effect.AcidVenomEffect;
import com.lzxnone.terraria.effect.CursedInfernoEffect;
import com.lzxnone.terraria.effect.DemonicFlamesEffect;
import com.lzxnone.terraria.effect.IchorEffect;
import com.lzxnone.terraria.effect.ManaSicknessEffect;
import com.lzxnone.terraria.effect.MidasEffect;
import com.lzxnone.terraria.effect.SummonEffect;
import com.lzxnone.terraria.enchantment.ModEnchantmentConfigs;
import com.lzxnone.terraria.client.config.RenderConfigs;
import com.lzxnone.terraria.item.ammo.*;
import com.lzxnone.terraria.item.accessory.ManaFlower;
import com.lzxnone.terraria.item.accessory.NaturesGift;
import com.lzxnone.terraria.item.weapon.magic.LastPrism;
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
import com.lzxnone.terraria.item.weapon.ranged.ElfMelter;
import com.lzxnone.terraria.item.weapon.ranged.Flamethrower;
import com.lzxnone.terraria.item.weapon.ranged.SnowballCannon;
import com.lzxnone.terraria.item.weapon.ranged.StarCannon;
import com.lzxnone.terraria.item.weapon.ranged.SuperStarShooter;
import com.lzxnone.terraria.item.weapon.ranged.gun.*;
import com.lzxnone.terraria.item.weapon.summon.minion.StardustDragonStaff;
import com.lzxnone.terraria.item.weapon.summon.minion.Terraprisma;
import com.lzxnone.terraria.item.weapon.summon.whip.Possession;

import java.util.HashMap;
import java.util.Map;

public class ConfigList {
    public static final String WEAPON = "weapon";
    public static final String AMMO = "ammo";
    public static final String ACCESSORY = "accessory";
    public static final String EFFECT = "effect";
    public static final String ENCHANTMENT = "enchantment";
    public static final String RENDER = "render";

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
            DevilsDevastation.CONFIG_LIST_ITEM,
            Terraprisma.CONFIG_LIST_ITEM,
            StardustDragonStaff.CONFIG_LIST_ITEM,
            Possession.CONFIG_LIST_ITEM,
            LastPrism.CONFIG_LIST_ITEM,
            Minishark.CONFIG_LIST_ITEM,
            FlintlockPistol.CONFIG_LIST_ITEM,
            Boomstick.CONFIG_LIST_ITEM,
            Revolver.CONFIG_LIST_ITEM,
            RedRyder.CONFIG_LIST_ITEM,
            Musket.CONFIG_LIST_ITEM,
            TheUndertaker.CONFIG_LIST_ITEM,
            QuadBarrelShotgun.CONFIG_LIST_ITEM,
            Handgun.CONFIG_LIST_ITEM,
            PhoenixBlaster.CONFIG_LIST_ITEM,
            Megashark.CONFIG_LIST_ITEM,
            ClockworkAssaultRifle.CONFIG_LIST_ITEM,
            Gatligator.CONFIG_LIST_ITEM,
            Shotgun.CONFIG_LIST_ITEM,
            Uzi.CONFIG_LIST_ITEM,
            VenusMagnum.CONFIG_LIST_ITEM,
            TacticalShotgun.CONFIG_LIST_ITEM,
            SniperRifle.CONFIG_LIST_ITEM,
            ChainGun.CONFIG_LIST_ITEM,
            SDMG.CONFIG_LIST_ITEM,
            SnowballCannon.CONFIG_LIST_ITEM,
            StarCannon.CONFIG_LIST_ITEM,
            SuperStarShooter.CONFIG_LIST_ITEM,
            Flamethrower.CONFIG_LIST_ITEM,
            ElfMelter.CONFIG_LIST_ITEM
        });
        ITEMS.put(EFFECT, new ConfigListItem[]{
            BloodButcheredEffect.CONFIG_LIST_ITEM,
            DemonicFlamesEffect.CONFIG_LIST_ITEM,
            CursedInfernoEffect.CONFIG_LIST_ITEM,
            IchorEffect.CONFIG_LIST_ITEM,
            AcidVenomEffect.CONFIG_LIST_ITEM,
            MidasEffect.CONFIG_LIST_ITEM,
            ManaSicknessEffect.CONFIG_LIST_ITEM,
            SummonEffect.CONFIG_LIST_ITEM
        });
        ITEMS.put(AMMO, new ConfigListItem[]{
            MusketBall.CONFIG_LIST_ITEM,
            MeteorShot.CONFIG_LIST_ITEM,
            SilverBullet.CONFIG_LIST_ITEM,
            CrystalBullet.CONFIG_LIST_ITEM,
            CursedBullet.CONFIG_LIST_ITEM,
            ChlorophyteBullet.CONFIG_LIST_ITEM,
            HighVelocityBullet.CONFIG_LIST_ITEM,
            IchorBullet.CONFIG_LIST_ITEM,
            VenomBullet.CONFIG_LIST_ITEM,
            PartyBullet.CONFIG_LIST_ITEM,
            NanoBullet.CONFIG_LIST_ITEM,
            ExplodingBullet.CONFIG_LIST_ITEM,
            GoldenBullet.CONFIG_LIST_ITEM,
            LuminiteBullet.CONFIG_LIST_ITEM,
            TungstenBullet.CONFIG_LIST_ITEM
        });
        ITEMS.put(ACCESSORY, new ConfigListItem[]{
            NaturesGift.CONFIG_LIST_ITEM,
            ManaFlower.CONFIG_LIST_ITEM
        });
        ITEMS.put(ENCHANTMENT, new ConfigListItem[]{
            ModEnchantmentConfigs.MANA_LEAK_CONFIG_LIST_ITEM,
            ModEnchantmentConfigs.MANA_EFFICIENCY_CONFIG_LIST_ITEM,
            ModEnchantmentConfigs.ARCANE_AMPLIFICATION_CONFIG_LIST_ITEM,
            ModEnchantmentConfigs.SUMMON_AMPLIFICATION_CONFIG_LIST_ITEM,
            ModEnchantmentConfigs.AMMO_EXHAUSTION_CONFIG_LIST_ITEM,
            ModEnchantmentConfigs.BULLET_HELL_CONFIG_LIST_ITEM,
            ModEnchantmentConfigs.GUNPOWDER_CONFIG_LIST_ITEM,
            ModEnchantmentConfigs.STEADY_BREATH_CONFIG_LIST_ITEM
        });
        ITEMS.put(RENDER, new ConfigListItem[]{
            RenderConfigs.CONFIG_LIST_ITEM
        });
    }

    public static ConfigListItem[] getItems(String id) {
        return ITEMS.getOrDefault(id, ITEMS.get(WEAPON));
    }
}
