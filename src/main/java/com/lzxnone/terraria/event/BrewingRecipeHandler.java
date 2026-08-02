package com.lzxnone.terraria.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID)
public class BrewingRecipeHandler {
    @SubscribeEvent
    public static void onRegisterBrewingRecipes(RegisterBrewingRecipesEvent event) {
        event.getBuilder().addRecipe(
            DataComponentIngredient.of(false, PotionContents.createItemStack(Items.POTION, Potions.AWKWARD)),
            Ingredient.of(Items.SADDLE),
            new ItemStack(ModItems.SUMMONING_POTION.get())
        );
        event.getBuilder().addRecipe(
            Ingredient.of(ModItems.LESSER_MANA_POTION.get()),
            Ingredient.of(Items.BROWN_MUSHROOM, Items.RED_MUSHROOM),
            new ItemStack(ModItems.MANA_POTION.get())
        );
    }
}
