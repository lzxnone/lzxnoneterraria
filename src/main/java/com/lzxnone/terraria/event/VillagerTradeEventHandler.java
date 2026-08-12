package com.lzxnone.terraria.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.ModItems;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.BasicItemListing;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID)
public class VillagerTradeEventHandler {
    private static final float PRICE_MULTIPLIER = 0.05F;

    @SubscribeEvent
    public static void onVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.CLERIC) {
            event.getTrades().get(1).add(new BasicItemListing(
                1,
                new ItemStack(ModItems.LESSER_MANA_POTION.get(), 2),
                16,
                2,
                PRICE_MULTIPLIER
            ));
            event.getTrades().get(2).add(new BasicItemListing(
                2,
                new ItemStack(ModItems.MANA_POTION.get()),
                12,
                5,
                PRICE_MULTIPLIER
            ));
        }

        if (event.getType() == VillagerProfession.TOOLSMITH) {
            event.getTrades().get(3).add(new BasicItemListing(
                16,
                new ItemStack(ModItems.TOOLBELT.get()),
                3,
                10,
                PRICE_MULTIPLIER
            ));
        }
    }
}
