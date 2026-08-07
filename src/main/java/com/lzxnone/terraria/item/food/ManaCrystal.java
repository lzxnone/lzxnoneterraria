package com.lzxnone.terraria.item.food;

import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.attachment.PlayerMana;
import com.lzxnone.terraria.event.PlayerManaSyncEventHandler;
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
    public static final int MAX_MANA = 200;

    public ManaCrystal() {
        super(new Item.Properties().rarity(Rarity.UNCOMMON), USE_DURATION);
    }

    @Override
    protected boolean canEat(Level level, Player player, ItemStack stack) {
        //只看基础最大魔力（水晶升级部分），饰品加成不会顶掉水晶使用次数
        return player.getData(ModAttachments.PLAYER_MANA).getBaseMaxMana() < MAX_MANA;
    }

    @Override
    protected boolean onEat(Level level, Player player, ItemStack stack) {
        PlayerMana mana = player.getData(ModAttachments.PLAYER_MANA);
        if(mana.increaseMaxMana(MANA_INCREASE, MAX_MANA) <= 0) return false;

        player.setData(ModAttachments.PLAYER_MANA, mana);
        if(player instanceof ServerPlayer serverPlayer) {
            PlayerManaSyncEventHandler.sync(serverPlayer);
            SoundUtil.playServerSound(player, ModSounds.GET_MANA.get(), 0.5f, 1.0f);
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("tooltip.lzxnoneterraria.mana_crystal").withStyle(ChatFormatting.GRAY));
    }
}
