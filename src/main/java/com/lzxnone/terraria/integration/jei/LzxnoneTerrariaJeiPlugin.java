package com.lzxnone.terraria.integration.jei;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.NonNull;

import java.util.List;

@JeiPlugin
public class LzxnoneTerrariaJeiPlugin implements IModPlugin {
    @Override
    public @NonNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IVanillaRecipeFactory factory = registration.getVanillaRecipeFactory();
        List<IJeiBrewingRecipe> recipes = List.of(
            factory.createBrewingRecipe(
                List.of(new ItemStack(Items.BROWN_MUSHROOM), new ItemStack(Items.RED_MUSHROOM)),
                List.of(new ItemStack(ModItems.LESSER_MANA_POTION.get())),
                new ItemStack(ModItems.MANA_POTION.get()),
                ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "brewing/mana_potion")
            ),
            factory.createBrewingRecipe(
                List.of(new ItemStack(Items.GLOWSTONE_DUST)),
                List.of(new ItemStack(ModItems.MANA_POTION.get())),
                new ItemStack(ModItems.MANA_REGENERATION_POTION.get()),
                ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "brewing/mana_regeneration_potion")
            )
        );
        registration.addRecipes(RecipeTypes.BREWING, recipes);

        registration.addItemStackInfo(
            new ItemStack(ModItems.FALLEN_STAR.get()),
            Component.translatable("jei.lzxnoneterraria.info.fallen_star")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.LESSER_MANA_POTION.get()),
            Component.translatable("jei.lzxnoneterraria.info.lesser_mana_potion")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.MANA_POTION.get()),
            Component.translatable("jei.lzxnoneterraria.info.mana_potion")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.NATURES_GIFT.get()),
            Component.translatable("jei.lzxnoneterraria.info.natures_gift")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.ARMOR_POLISH.get()),
            Component.translatable("jei.lzxnoneterraria.info.armor_polish")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.MEGAPHONE.get()),
            Component.translatable("jei.lzxnoneterraria.info.megaphone")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.NAZAR.get()),
            Component.translatable("jei.lzxnoneterraria.info.nazar")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.POCKET_MIRROR.get()),
            Component.translatable("jei.lzxnoneterraria.info.pocket_mirror")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.BLINDFOLD.get()),
            Component.translatable("jei.lzxnoneterraria.info.blindfold")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.VITAMINS.get()),
            Component.translatable("jei.lzxnoneterraria.info.vitamins")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.ADHESIVE_BANDAGE.get()),
            Component.translatable("jei.lzxnoneterraria.info.adhesive_bandage")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.TRIFOLD_MAP.get()),
            Component.translatable("jei.lzxnoneterraria.info.trifold_map")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.BEZOAR.get()),
            Component.translatable("jei.lzxnoneterraria.info.bezoar")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.FAST_CLOCK.get()),
            Component.translatable("jei.lzxnoneterraria.info.fast_clock")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.COBALT_SHIELD.get()),
            Component.translatable("jei.lzxnoneterraria.info.cobalt_shield")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.MOON_CHARM.get()),
            Component.translatable("jei.lzxnoneterraria.info.moon_charm")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.NEPTUNES_SHELL.get()),
            Component.translatable("jei.lzxnoneterraria.info.neptunes_shell")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.MOON_STONE.get()),
            Component.translatable("jei.lzxnoneterraria.info.moon_stone")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.SUN_STONE.get()),
            Component.translatable("jei.lzxnoneterraria.info.sun_stone")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.BLACK_BELT.get()),
            Component.translatable("jei.lzxnoneterraria.info.black_belt")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.FLESH_KNUCKLES.get()),
            Component.translatable("jei.lzxnoneterraria.info.village_accessory")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.PUTRID_SCENT.get()),
            Component.translatable("jei.lzxnoneterraria.info.village_accessory")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.BAND_OF_STARPOWER.get()),
            Component.translatable("jei.lzxnoneterraria.info.village_accessory")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.PANIC_NECKLACE.get()),
            Component.translatable("jei.lzxnoneterraria.info.village_accessory")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.REGENERATION_BAND.get()),
            Component.translatable("jei.lzxnoneterraria.info.village_accessory")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.FERAL_CLAWS.get()),
            Component.translatable("jei.lzxnoneterraria.info.jungle_accessory")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.STAR_CLOAK.get()),
            Component.translatable("jei.lzxnoneterraria.info.trial_reward_accessory")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.TITAN_GLOVE.get()),
            Component.translatable("jei.lzxnoneterraria.info.trial_reward_accessory")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.CROSS_NECKLACE.get()),
            Component.translatable("jei.lzxnoneterraria.info.trial_reward_accessory")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.LAVA_CHARM.get()),
            Component.translatable("jei.lzxnoneterraria.info.nether_accessory")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.OBSIDIAN_ROSE.get()),
            Component.translatable("jei.lzxnoneterraria.info.nether_accessory")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.MAGMA_STONE.get()),
            Component.translatable("jei.lzxnoneterraria.info.nether_accessory")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.FROZEN_TURTLE_SHELL.get()),
            Component.translatable("jei.lzxnoneterraria.info.frozen_turtle_shell")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.SHACKLE.get()),
            Component.translatable("jei.lzxnoneterraria.info.shackle")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.SHARK_TOOTH_NECKLACE.get()),
            Component.translatable("jei.lzxnoneterraria.info.shark_tooth_necklace")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.RIFLE_SCOPE.get()),
            Component.translatable("jei.lzxnoneterraria.info.rifle_scope")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.HERCULES_BEETLE.get()),
            Component.translatable("jei.lzxnoneterraria.info.hercules_beetle")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.ANCIENT_CHISEL.get()),
            Component.translatable("jei.lzxnoneterraria.info.desert_accessory")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.PYGMY_NECKLACE.get()),
            Component.translatable("jei.lzxnoneterraria.info.pygmy_necklace")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.NECROMANTIC_SCROLL.get()),
            Component.translatable("jei.lzxnoneterraria.info.necromantic_scroll")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.EYE_OF_GOLEM.get()),
            Component.translatable("jei.lzxnoneterraria.info.eye_of_the_golem")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.PHILOSOPHERS_STONE.get()),
            Component.translatable("jei.lzxnoneterraria.info.ocean_accessory")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.VALENTINE_RING.get()),
            Component.translatable("jei.lzxnoneterraria.info.snowy_accessory")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.HAND_WARMER.get()),
            Component.translatable("jei.lzxnoneterraria.info.snowy_accessory")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.CELESTIAL_MAGNET.get()),
            Component.translatable("jei.lzxnoneterraria.info.end_accessory")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.TREASURE_MAGNET.get()),
            Component.translatable("jei.lzxnoneterraria.info.trial_chambers_accessory")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.TOOLBELT.get()),
            Component.translatable("jei.lzxnoneterraria.info.toolbelt")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.GOLD_RING.get()),
            Component.translatable("jei.lzxnoneterraria.info.pillager_outpost_accessory")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.LUCKY_COIN.get()),
            Component.translatable("jei.lzxnoneterraria.info.pillager_outpost_accessory")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.DISCOUNT_CARD.get()),
            Component.translatable("jei.lzxnoneterraria.info.pillager_outpost_accessory")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.WARRIOR_EMBLEM.get()),
            Component.translatable("jei.lzxnoneterraria.info.warrior_emblem")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.SUMMONER_EMBLEM.get()),
            Component.translatable("jei.lzxnoneterraria.info.summoner_emblem")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.RANGER_EMBLEM.get()),
            Component.translatable("jei.lzxnoneterraria.info.ranger_emblem")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.SORCERER_EMBLEM.get()),
            Component.translatable("jei.lzxnoneterraria.info.sorcerer_emblem")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.DAEDALUS_STORMBOW.get()),
            Component.translatable("jei.lzxnoneterraria.info.daedalus_stormbow")
        );
    }
}
