package com.lzxnone.terraria.creative_mode_tab;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LzxnoneTerraria.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MELEE_TAB = CREATIVE_MODE_TABS.register("lzxnoneterraria_melee_0", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.lzxnoneterraria.melee"))
            .icon(() -> ModItems.TERRA_BLADE.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.COPPER_SHORTSWORD.get());
                output.accept(ModItems.ENCHANTED_SWORD.get());
                output.accept(ModItems.BEE_KEEPER.get());
                output.accept(ModItems.STARFURY.get());
                output.accept(ModItems.SEEDLER.get());
                output.accept(ModItems.THE_HORSEMANS_BLADE.get());
                output.accept(ModItems.INFLUX_WAVER.get());
                output.accept(ModItems.STAR_WRATH.get());
                output.accept(ModItems.MEOWMERE.get());
                output.accept(ModItems.TERRA_BLADE.get());
                output.accept(ModItems.ZENITH.get());
                output.accept(ModItems.TRUE_COPPER_SHORTSWORD.get());
                output.accept(ModItems.EXCALIBUR.get());
                output.accept(ModItems.NIGHTS_EDGE.get());
                output.accept(ModItems.TRUE_EXCALIBUR.get());
                output.accept(ModItems.TRUE_NIGHTS_EDGE.get());
                output.accept(ModItems.LIGHTS_BANE.get());
                output.accept(ModItems.BLOOD_BUTCHERER.get());
                output.accept(ModItems.MURAMASA.get());
                output.accept(ModItems.BLADE_OF_GRASS.get());
                output.accept(ModItems.VOLCANO.get());
                output.accept(ModItems.TERRAGRIM.get());
                output.accept(ModItems.FIRST_FRACTAL.get());
                output.accept(ModItems.MACE.get());
            }).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CALAMITY_MELEE_TAB = CREATIVE_MODE_TABS.register("lzxnoneterraria_melee_1", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.lzxnoneterraria.calamity_melee"))
            .icon(() -> ModItems.DEVILS_DEVASTATION.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.DEVILS_DEVASTATION.get());
            }).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAGIC_TAB = CREATIVE_MODE_TABS.register("lzxnoneterraria_magic_0", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.lzxnoneterraria.magic"))
            .icon(() -> ModItems.LAST_PRISM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.LAST_PRISM.get());
                output.accept(ModItems.BETSY_WRATH.get());
                output.accept(ModItems.LUNAR_FLARE.get());
                output.accept(ModItems.NEBULA_BLAZE.get());
                output.accept(ModItems.NEBULA_ARCANUM.get());
                output.accept(ModItems.STELLAR_TUNE.get());
                output.accept(ModItems.BUBBLE_GUN.get());
                output.accept(ModItems.NIGHTGLOW.get());
                output.accept(ModItems.MANA_CRYSTAL.get());
                output.accept(ModItems.LESSER_MANA_POTION.get());
                output.accept(ModItems.MANA_POTION.get());
                output.accept(ModItems.GREATER_MANA_POTION.get());
                output.accept(ModItems.SUPER_MANA_POTION.get());
            }).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ACCESSORY_TAB = CREATIVE_MODE_TABS.register("lzxnoneterraria_accessory_0", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.lzxnoneterraria.accessory"))
            .icon(() -> ModItems.MANA_FLOWER.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.ARMOR_POLISH.get());
                output.accept(ModItems.MEGAPHONE.get());
                output.accept(ModItems.NAZAR.get());
                output.accept(ModItems.POCKET_MIRROR.get());
                output.accept(ModItems.REFLECTIVE_SHADES.get());
                output.accept(ModItems.ARMOR_BRACING.get());
                output.accept(ModItems.COUNTERCURSE_MANTRA.get());
                output.accept(ModItems.ANKH_CHARM.get());
                output.accept(ModItems.OBSIDIAN_SKULL.get());
                output.accept(ModItems.COBALT_SHIELD.get());
                output.accept(ModItems.OBSIDIAN_SHIELD.get());
                output.accept(ModItems.ANKH_SHIELD.get());
                output.accept(ModItems.AVENGER_EMBLEM.get());
                output.accept(ModItems.CROSS_NECKLACE.get());
                output.accept(ModItems.STAR_CLOAK.get());
                output.accept(ModItems.STAR_VEIL.get());
                output.accept(ModItems.HAND_WARMER.get());
                output.accept(ModItems.BEZOAR.get());
                output.accept(ModItems.BLINDFOLD.get());
                output.accept(ModItems.FAST_CLOCK.get());
                output.accept(ModItems.VITAMINS.get());
                output.accept(ModItems.ADHESIVE_BANDAGE.get());
                output.accept(ModItems.MEDICATED_BANDAGE.get());
                output.accept(ModItems.TRIFOLD_MAP.get());
                output.accept(ModItems.THE_PLAN.get());
                output.accept(ModItems.NATURES_GIFT.get());
                output.accept(ModItems.MANA_FLOWER.get());
                output.accept(ModItems.HERCULES_BEETLE.get());
                output.accept(ModItems.NECROMANTIC_SCROLL.get());
                output.accept(ModItems.PAPYRUS_SCARAB.get());
                output.accept(ModItems.PYGMY_NECKLACE.get());
                output.accept(ModItems.WARRIOR_EMBLEM.get());
                output.accept(ModItems.SUMMONER_EMBLEM.get());
                output.accept(ModItems.RANGER_EMBLEM.get());
                output.accept(ModItems.SORCERER_EMBLEM.get());
                output.accept(ModItems.SHARK_TOOTH_NECKLACE.get());
                output.accept(ModItems.HONEY_COMB.get());
                output.accept(ModItems.STINGER_NECKLACE.get());
                output.accept(ModItems.BEE_CLOAK.get());
                output.accept(ModItems.BLACK_BELT.get());
                output.accept(ModItems.TITAN_GLOVE.get());
                output.accept(ModItems.FERAL_CLAWS.get());
                output.accept(ModItems.POWER_GLOVE.get());
                output.accept(ModItems.MECHANICAL_GLOVE.get());
                output.accept(ModItems.FLESH_KNUCKLES.get());
                output.accept(ModItems.BERSERKERS_GLOVE.get());
                output.accept(ModItems.PALADINS_SHIELD.get());
                output.accept(ModItems.HERO_SHIELD.get());
                output.accept(ModItems.FROZEN_SHIELD.get());
                output.accept(ModItems.FROZEN_TURTLE_SHELL.get());
                output.accept(ModItems.MAGMA_STONE.get());
                output.accept(ModItems.FIRE_GAUNTLET.get());
                output.accept(ModItems.LAVA_CHARM.get());
                output.accept(ModItems.OBSIDIAN_ROSE.get());
                output.accept(ModItems.MAGMA_SKULL.get());
                output.accept(ModItems.MOLTEN_CHARM.get());
                output.accept(ModItems.OBSIDIAN_SKULL_ROSE.get());
                output.accept(ModItems.MOLTEN_SKULL_ROSE.get());
                output.accept(ModItems.PANIC_NECKLACE.get());
                output.accept(ModItems.SWEETHEART_NECKLACE.get());
                output.accept(ModItems.ARCANE_FLOWER.get());
                output.accept(ModItems.REGENERATION_BAND.get());
                output.accept(ModItems.BAND_OF_STARPOWER.get());
                output.accept(ModItems.MANA_REGENERATION_BAND.get());
                output.accept(ModItems.MAGIC_CUFF.get());
                output.accept(ModItems.CELESTIAL_MAGNET.get());
                output.accept(ModItems.CELESTIAL_EMBLEM.get());
                output.accept(ModItems.CELESTIAL_CUFFS.get());
                output.accept(ModItems.MAGNET_FLOWER.get());
                output.accept(ModItems.PHILOSOPHERS_STONE.get());
                output.accept(ModItems.VALENTINE_RING.get());
                output.accept(ModItems.CHARM_OF_MYTHS.get());
                output.accept(ModItems.MANA_CLOAK.get());
                output.accept(ModItems.LUCKY_COIN.get());
                output.accept(ModItems.GOLD_RING.get());
                output.accept(ModItems.COIN_RING.get());
                output.accept(ModItems.DISCOUNT_CARD.get());
                output.accept(ModItems.GREEDY_RING.get());
                output.accept(ModItems.TREASURE_MAGNET.get());
                output.accept(ModItems.TOOLBELT.get());
                output.accept(ModItems.ANCIENT_CHISEL.get());
                output.accept(ModItems.EYE_OF_GOLEM.get());
                output.accept(ModItems.DESTROYER_EMBLEM.get());
                output.accept(ModItems.MOON_STONE.get());
                output.accept(ModItems.SUN_STONE.get());
                output.accept(ModItems.CELESTIAL_STONE.get());
                output.accept(ModItems.MOON_CHARM.get());
                output.accept(ModItems.NEPTUNES_SHELL.get());
                output.accept(ModItems.MOON_SHELL.get());
                output.accept(ModItems.CELESTIAL_SHELL.get());
                output.accept(ModItems.PUTRID_SCENT.get());
                output.accept(ModItems.SHACKLE.get());
                output.accept(ModItems.RIFLE_SCOPE.get());
                output.accept(ModItems.SNIPER_SCOPE.get());
                output.accept(ModItems.RECON_SCOPE.get());
            }).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BLOCK_TAB = CREATIVE_MODE_TABS.register("lzxnoneterraria_block_0", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.lzxnoneterraria.block"))
            .icon(() -> ModItems.VISION.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.VISION.get());
                output.accept(ModItems.STARDUST_DRAGON_HEAD.get());
                output.accept(ModItems.STARDUST_DRAGON_BODY_A.get());
                output.accept(ModItems.STARDUST_DRAGON_BODY_B.get());
                output.accept(ModItems.STARDUST_DRAGON_TAIL.get());
            }).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> RANGED_TAB = CREATIVE_MODE_TABS.register("lzxnoneterraria_ranged_0", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.lzxnoneterraria.ranged"))
            .icon(() -> ModItems.SDMG.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.MINISHARK.get());
                output.accept(ModItems.FLINTLOCK_PISTOL.get());
                output.accept(ModItems.BOOMSTICK.get());
                output.accept(ModItems.REVOLVER.get());
                output.accept(ModItems.RED_RYDER.get());
                output.accept(ModItems.MUSKET.get());
                output.accept(ModItems.THE_UNDERTAKER.get());
                output.accept(ModItems.QUAD_BARREL_SHOTGUN.get());
                output.accept(ModItems.HANDGUN.get());
                output.accept(ModItems.PHOENIX_BLASTER.get());
                output.accept(ModItems.MEGASHARK.get());
                output.accept(ModItems.CLOCKWORK_ASSAULT_RIFLE.get());
                output.accept(ModItems.GATLIGATOR.get());
                output.accept(ModItems.SHOTGUN.get());
                output.accept(ModItems.UZI.get());
                output.accept(ModItems.VENUS_MAGNUM.get());
                output.accept(ModItems.TACTICAL_SHOTGUN.get());
                output.accept(ModItems.SNIPER_RIFLE.get());
                output.accept(ModItems.CHAIN_GUN.get());
                output.accept(ModItems.SDMG.get());
                output.accept(ModItems.SNOWBALL_CANNON.get());
                output.accept(ModItems.STAR_CANNON.get());
                output.accept(ModItems.SUPER_STAR_SHOOTER.get());
                output.accept(ModItems.FLAMETHROWER.get());
                output.accept(ModItems.ELF_MELTER.get());
                output.accept(ModItems.DAEDALUS_STORMBOW.get());
                output.accept(ModItems.MUSKET_BALL.get());
                output.accept(ModItems.METEOR_SHOT.get());
                output.accept(ModItems.SILVER_BULLET.get());
                output.accept(ModItems.CRYSTAL_BULLET.get());
                output.accept(ModItems.CURSED_BULLET.get());
                output.accept(ModItems.CHLOROPHYTE_BULLET.get());
                output.accept(ModItems.HIGH_VELOCITY_BULLET.get());
                output.accept(ModItems.ICHOR_BULLET.get());
                output.accept(ModItems.VENOM_BULLET.get());
                output.accept(ModItems.PARTY_BULLET.get());
                output.accept(ModItems.NANO_BULLET.get());
                output.accept(ModItems.EXPLODING_BULLET.get());
                output.accept(ModItems.GOLDEN_BULLET.get());
                output.accept(ModItems.ENDLESS_MUSKET_POUCH.get());
                output.accept(ModItems.LUMINITE_BULLET.get());
                output.accept(ModItems.TUNGSTEN_BULLET.get());
                output.accept(ModItems.WOODEN_ARROW.get());
                output.accept(ModItems.FALLEN_STAR.get());
            }).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SUMMON_TAB = CREATIVE_MODE_TABS.register("lzxnoneterraria_summon_0", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.lzxnoneterraria.summon"))
            .icon(() -> ModItems.TERRAPRISMA.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.TERRAPRISMA.get());
                output.accept(ModItems.STARDUST_DRAGON_STAFF.get());
                output.accept(ModItems.STARDUST_CELL_STAFF.get());
                output.accept(ModItems.POSSESSION.get());
                output.accept(ModItems.SUMMONING_POTION.get());
            }).build());
}
