package com.lzxnone.terraria.item.food;

import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.attachment.PlayerMana;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ManaCrystal extends Food {
    public static final int USE_DURATION = 10;
    public static final int MANA_INCREASE = 20;

    public ManaCrystal() {
        super(new Item.Properties().rarity(Rarity.UNCOMMON), USE_DURATION);
    }

    @Override
    protected boolean canEat(Level level, Player player, ItemStack stack) {
        return player.getData(ModAttachments.PLAYER_MANA).getBaseMaxMana() < PlayerMana.MAX_BASE_MANA;
    }

    @Override
    protected boolean onEat(Level level, Player player, ItemStack stack) {
        if(!(player instanceof ServerPlayer serverPlayer)) return false;
        PlayerMana mana = player.getData(ModAttachments.PLAYER_MANA);
        int baseMaxMana = mana.getBaseMaxMana();
        if(baseMaxMana >= PlayerMana.MAX_BASE_MANA) return false;
        if(!PlayerMana.setBaseMaxMana(serverPlayer, Math.min(baseMaxMana + MANA_INCREASE, PlayerMana.MAX_BASE_MANA))) return false;

        player.playNotifySound(ModSounds.GET_MANA.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("tooltip.lzxnoneterraria.mana_crystal").withStyle(ChatFormatting.GRAY));
    }
}
