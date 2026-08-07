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
            )
        );
        registration.addRecipes(RecipeTypes.BREWING, recipes);

        registration.addItemStackInfo(
            new ItemStack(ModItems.FALLEN_STAR.get()),
            Component.translatable("jei.lzxnoneterraria.info.fallen_star")
        );
        registration.addItemStackInfo(
            new ItemStack(ModItems.NATURES_GIFT.get()),
            Component.translatable("jei.lzxnoneterraria.info.natures_gift")
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
    }
}
