package com.lzxnone.terraria.item.potion;

import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.attachment.PlayerMana;
import com.lzxnone.terraria.effect.ManaSicknessEffect;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class AbstractManaPotion extends Potion {
    public abstract int getRecoverAmount();

    @Override
    protected boolean canDrink(Level level, Player player, ItemStack stack) {
        PlayerMana mana = player.getData(ModAttachments.PLAYER_MANA);
        return mana.getMana() < mana.getMaxMana();
    }

    @Override
    protected boolean onDrink(ServerPlayer player, ItemStack stack) {
        if(!PlayerMana.recoverMana(player, getRecoverAmount())) return false;

        ManaSicknessEffect.applyTo(player);
        return true;
    }

    @Override
    protected String getTooltipKey() {
        return "tooltip.lzxnoneterraria.mana_potion";
    }

    @Override
    protected Object[] getTooltipArgs() {
        return new Object[]{getRecoverAmount()};
    }
}
