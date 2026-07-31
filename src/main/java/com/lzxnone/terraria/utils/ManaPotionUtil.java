package com.lzxnone.terraria.utils;

import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.attachment.PlayerMana;
import com.lzxnone.terraria.effect.ManaEffect;
import com.lzxnone.terraria.effect.ManaSicknessEffect;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.event.PlayerManaSyncEventHandler;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

public class ManaPotionUtil {
    public static boolean hasManaPotion(Player player) {
        return findManaPotionSlot(player) >= 0;
    }

    public static boolean tryUseManaPotion(ServerPlayer player) {
        int slot = findManaPotionSlot(player);
        if(slot < 0) return false;

        ItemStack stack = player.getInventory().getItem(slot);
        int recoverAmount = getManaRecoverAmount(stack);
        if(recoverAmount <= 0) return false;

        PlayerMana mana = player.getData(ModAttachments.PLAYER_MANA);
        int oldMana = mana.getMana();
        if(!mana.recoverManaImmediately(recoverAmount)) return false;
        ManaSicknessEffect.applyTo(player);

        stack.shrink(1);
        ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
        if(stack.isEmpty()) {
            player.getInventory().setItem(slot, bottle);
        }else if(!player.getInventory().add(bottle)) {
            player.drop(bottle, false);
        }

        player.setData(ModAttachments.PLAYER_MANA, mana);
        if(mana.getMana() != oldMana) {
            PlayerManaSyncEventHandler.sync(player);
        }
        return true;
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
        if(!stack.is(Items.POTION)) return 0;

        PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        for(MobEffectInstance effect : contents.getAllEffects()) {
            if(effect.getEffect().is(ModEffects.MANA)) {
                return ManaEffect.getRecoverAmount(effect.getAmplifier());
            }
        }
        return 0;
    }
}
