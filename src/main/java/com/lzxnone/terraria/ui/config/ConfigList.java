package com.lzxnone.terraria.ui.config;

import com.lzxnone.terraria.effect.BloodButcheredEffect;
import com.lzxnone.terraria.effect.AcidVenomEffect;
import com.lzxnone.terraria.effect.CursedInfernoEffect;
import com.lzxnone.terraria.effect.DemonicFlamesEffect;
import com.lzxnone.terraria.effect.IchorEffect;
import com.lzxnone.terraria.effect.ManaSicknessEffect;
import com.lzxnone.terraria.effect.MidasEffect;
import com.lzxnone.terraria.effect.PaladinsShieldEffect;
import com.lzxnone.terraria.effect.IceBarrierEffect;
import com.lzxnone.terraria.effect.PanicEffect;
import com.lzxnone.terraria.effect.SummonEffect;
import com.lzxnone.terraria.enchantment.ModEnchantmentConfigs;
import com.lzxnone.terraria.client.config.RenderConfigs;
import com.lzxnone.terraria.item.ammo.*;
import com.lzxnone.terraria.item.accessory.ManaFlower;
import com.lzxnone.terraria.item.accessory.NaturesGift;
import com.lzxnone.terraria.item.accessory.ArmorPolish;
import com.lzxnone.terraria.item.accessory.ArmorBracing;
import com.lzxnone.terraria.item.accessory.AnkhCharm;
import com.lzxnone.terraria.item.accessory.AvengerEmblem;
import com.lzxnone.terraria.item.accessory.CobaltShield;
import com.lzxnone.terraria.item.accessory.ObsidianShield;
import com.lzxnone.terraria.item.accessory.AnkhShield;
import com.lzxnone.terraria.item.accessory.StarCloak;
import com.lzxnone.terraria.item.accessory.CrossNecklace;
import com.lzxnone.terraria.item.accessory.StarVeil;
import com.lzxnone.terraria.item.accessory.HerculesBeetle;
import com.lzxnone.terraria.item.accessory.NecromanticScroll;
import com.lzxnone.terraria.item.accessory.PapyrusScarab;
import com.lzxnone.terraria.item.accessory.PygmyNecklace;
import com.lzxnone.terraria.item.accessory.WarriorEmblem;
import com.lzxnone.terraria.item.accessory.SummonerEmblem;
import com.lzxnone.terraria.item.accessory.RangerEmblem;
import com.lzxnone.terraria.item.accessory.SorcererEmblem;
import com.lzxnone.terraria.item.accessory.SharkToothNecklace;
import com.lzxnone.terraria.item.accessory.HoneyComb;
import com.lzxnone.terraria.item.accessory.StingerNecklace;
import com.lzxnone.terraria.item.accessory.BeeCloak;
import com.lzxnone.terraria.item.accessory.BlackBelt;
import com.lzxnone.terraria.item.accessory.TitanGlove;
import com.lzxnone.terraria.item.accessory.FeralClaws;
import com.lzxnone.terraria.item.accessory.PowerGlove;
import com.lzxnone.terraria.item.accessory.MechanicalGlove;
import com.lzxnone.terraria.item.accessory.FleshKnuckles;
import com.lzxnone.terraria.item.accessory.BerserkersGlove;
import com.lzxnone.terraria.item.accessory.PaladinsShield;
import com.lzxnone.terraria.item.accessory.HeroShield;
import com.lzxnone.terraria.item.accessory.FrozenTurtleShell;
import com.lzxnone.terraria.item.accessory.FrozenShield;
import com.lzxnone.terraria.item.accessory.FireGauntlet;
import com.lzxnone.terraria.item.accessory.LavaCharm;
import com.lzxnone.terraria.item.accessory.ObsidianRose;
import com.lzxnone.terraria.item.accessory.MagmaSkull;
import com.lzxnone.terraria.item.accessory.MoltenCharm;
import com.lzxnone.terraria.item.accessory.ObsidianSkullRose;
import com.lzxnone.terraria.item.accessory.MoltenSkullRose;
import com.lzxnone.terraria.item.accessory.PanicNecklace;
import com.lzxnone.terraria.item.accessory.SweetheartNecklace;
import com.lzxnone.terraria.item.accessory.ArcaneFlower;
import com.lzxnone.terraria.item.accessory.RegenerationBand;
import com.lzxnone.terraria.item.accessory.BandOfStarpower;
import com.lzxnone.terraria.item.accessory.ManaRegenerationBand;
import com.lzxnone.terraria.item.accessory.EyeOfGolem;
import com.lzxnone.terraria.item.accessory.DestroyerEmblem;
import com.lzxnone.terraria.item.accessory.MoonStone;
import com.lzxnone.terraria.item.accessory.SunStone;
import com.lzxnone.terraria.item.accessory.CelestialStone;
import com.lzxnone.terraria.item.accessory.MoonCharm;
import com.lzxnone.terraria.item.accessory.NeptunesShell;
import com.lzxnone.terraria.item.accessory.MoonShell;
import com.lzxnone.terraria.item.accessory.CelestialShell;
import com.lzxnone.terraria.item.accessory.PutridScent;
import com.lzxnone.terraria.item.accessory.Shackle;
import com.lzxnone.terraria.item.accessory.SniperScope;
import com.lzxnone.terraria.item.accessory.ReconScope;
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
            SummonEffect.CONFIG_LIST_ITEM,
            PaladinsShieldEffect.CONFIG_LIST_ITEM,
            IceBarrierEffect.CONFIG_LIST_ITEM,
            PanicEffect.CONFIG_LIST_ITEM
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
            ArmorPolish.CONFIG_LIST_ITEM,
            ArmorBracing.CONFIG_LIST_ITEM,
            AnkhCharm.CONFIG_LIST_ITEM,
            CobaltShield.CONFIG_LIST_ITEM,
            ObsidianShield.CONFIG_LIST_ITEM,
            AnkhShield.CONFIG_LIST_ITEM,
            StarCloak.CONFIG_LIST_ITEM,
            CrossNecklace.CONFIG_LIST_ITEM,
            StarVeil.CONFIG_LIST_ITEM,
            ManaFlower.CONFIG_LIST_ITEM,
            HerculesBeetle.CONFIG_LIST_ITEM,
            NecromanticScroll.CONFIG_LIST_ITEM,
            PapyrusScarab.CONFIG_LIST_ITEM,
            PygmyNecklace.CONFIG_LIST_ITEM,
            AvengerEmblem.CONFIG_LIST_ITEM,
            WarriorEmblem.CONFIG_LIST_ITEM,
            SummonerEmblem.CONFIG_LIST_ITEM,
            RangerEmblem.CONFIG_LIST_ITEM,
            SorcererEmblem.CONFIG_LIST_ITEM,
            SharkToothNecklace.CONFIG_LIST_ITEM,
            HoneyComb.CONFIG_LIST_ITEM,
            StingerNecklace.CONFIG_LIST_ITEM,
            BeeCloak.CONFIG_LIST_ITEM,
            BlackBelt.CONFIG_LIST_ITEM,
            TitanGlove.CONFIG_LIST_ITEM,
            FeralClaws.CONFIG_LIST_ITEM,
            PowerGlove.CONFIG_LIST_ITEM,
            MechanicalGlove.CONFIG_LIST_ITEM,
            FleshKnuckles.CONFIG_LIST_ITEM,
            BerserkersGlove.CONFIG_LIST_ITEM,
            PaladinsShield.CONFIG_LIST_ITEM,
            HeroShield.CONFIG_LIST_ITEM,
            FrozenTurtleShell.CONFIG_LIST_ITEM,
            FrozenShield.CONFIG_LIST_ITEM,
            FireGauntlet.CONFIG_LIST_ITEM,
            LavaCharm.CONFIG_LIST_ITEM,
            ObsidianRose.CONFIG_LIST_ITEM,
            MagmaSkull.CONFIG_LIST_ITEM,
            MoltenCharm.CONFIG_LIST_ITEM,
            ObsidianSkullRose.CONFIG_LIST_ITEM,
            MoltenSkullRose.CONFIG_LIST_ITEM,
            PanicNecklace.CONFIG_LIST_ITEM,
            SweetheartNecklace.CONFIG_LIST_ITEM,
            ArcaneFlower.CONFIG_LIST_ITEM,
            RegenerationBand.CONFIG_LIST_ITEM,
            BandOfStarpower.CONFIG_LIST_ITEM,
            ManaRegenerationBand.CONFIG_LIST_ITEM,
            EyeOfGolem.CONFIG_LIST_ITEM,
            DestroyerEmblem.CONFIG_LIST_ITEM,
            MoonStone.CONFIG_LIST_ITEM,
            SunStone.CONFIG_LIST_ITEM,
            CelestialStone.CONFIG_LIST_ITEM,
            MoonCharm.CONFIG_LIST_ITEM,
            NeptunesShell.CONFIG_LIST_ITEM,
            MoonShell.CONFIG_LIST_ITEM,
            CelestialShell.CONFIG_LIST_ITEM,
            PutridScent.CONFIG_LIST_ITEM,
            Shackle.CONFIG_LIST_ITEM,
            SniperScope.CONFIG_LIST_ITEM,
            ReconScope.CONFIG_LIST_ITEM
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
