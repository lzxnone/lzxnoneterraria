package com.lzxnone.terraria.utils;

import com.lzxnone.terraria.item.potion.AbstractManaPotion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ManaPotionUtil {
    public static boolean hasManaPotion(Player player) {
        return findManaPotionSlot(player) >= 0;
    }

    public static boolean tryUseManaPotion(ServerPlayer player) {
        int slot = findManaPotionSlot(player);
        if(slot < 0) return false;

        ItemStack stack = player.getInventory().getItem(slot);
        if(!(stack.getItem() instanceof AbstractManaPotion potion)) return false;
        return potion.tryDrink(player, stack);
    }

    private static int findManaPotionSlot(Player player) {
        for(int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if(getManaRecoverAmount(stack) > 0) {
                return i;
            }
        }
        return -1;
    }

    private static int getManaRecoverAmount(ItemStack stack) {
        if(stack.getItem() instanceof AbstractManaPotion potion) return potion.getRecoverAmount();
        return 0;
    }
}
