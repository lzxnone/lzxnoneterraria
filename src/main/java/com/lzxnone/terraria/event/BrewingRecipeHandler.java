package com.lzxnone.terraria.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.effect.ModPotions;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID)
public class BrewingRecipeHandler {
    @SubscribeEvent
    public static void onRegisterBrewingRecipes(RegisterBrewingRecipesEvent event) {
        event.getBuilder().addMix(Potions.AWKWARD, Items.NETHERITE_SCRAP, ModPotions.SHORT_SUMMON_0);
        event.getBuilder().addMix(ModPotions.SHORT_SUMMON_0, Items.NETHERITE_SCRAP, ModPotions.MEDIUM_SUMMON_0);
        event.getBuilder().addMix(ModPotions.MEDIUM_SUMMON_0, Items.NETHERITE_SCRAP, ModPotions.LONG_SUMMON_0);

        event.getBuilder().addMix(ModPotions.LONG_SUMMON_0, Items.NETHERITE_SCRAP, ModPotions.SHORT_SUMMON_1);
        event.getBuilder().addMix(ModPotions.SHORT_SUMMON_1, Items.NETHERITE_SCRAP, ModPotions.MEDIUM_SUMMON_1);
        event.getBuilder().addMix(ModPotions.MEDIUM_SUMMON_1, Items.NETHERITE_SCRAP, ModPotions.LONG_SUMMON_1);

        event.getBuilder().addMix(ModPotions.LONG_SUMMON_1, Items.NETHERITE_INGOT, ModPotions.SHORT_SUMMON_2);
        event.getBuilder().addMix(ModPotions.SHORT_SUMMON_2, Items.NETHERITE_INGOT, ModPotions.MEDIUM_SUMMON_2);
        event.getBuilder().addMix(ModPotions.MEDIUM_SUMMON_2, Items.NETHERITE_INGOT, ModPotions.LONG_SUMMON_2);

        event.getBuilder().addMix(ModPotions.LONG_SUMMON_2, Items.NETHERITE_INGOT, ModPotions.SHORT_SUMMON_3);
        event.getBuilder().addMix(ModPotions.SHORT_SUMMON_3, Items.NETHERITE_INGOT, ModPotions.MEDIUM_SUMMON_3);
        event.getBuilder().addMix(ModPotions.MEDIUM_SUMMON_3, Items.NETHERITE_INGOT, ModPotions.LONG_SUMMON_3);

        event.getBuilder().addMix(ModPotions.LONG_SUMMON_3, Items.NETHERITE_BLOCK, ModPotions.SHORT_SUMMON_4);
        event.getBuilder().addMix(ModPotions.SHORT_SUMMON_4, Items.NETHERITE_BLOCK, ModPotions.MEDIUM_SUMMON_4);
        event.getBuilder().addMix(ModPotions.MEDIUM_SUMMON_4, Items.NETHERITE_BLOCK, ModPotions.LONG_SUMMON_4);
    }
}
