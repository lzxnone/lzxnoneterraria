package com.lzxnone.terraria.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.attachment.PlayerMana;
import com.lzxnone.terraria.item.accessory.AccessoryUtil;
import com.lzxnone.terraria.item.weapon.MagicWeapon;
import com.lzxnone.terraria.network.payload.ManaSyncPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID)
public class PlayerManaSyncEventHandler {
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.getEntity() instanceof ServerPlayer player) {
            sync(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if(event.getEntity() instanceof ServerPlayer player) {
            sync(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if(event.getEntity() instanceof ServerPlayer player) {
            sync(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if(!(event.getEntity() instanceof ServerPlayer player)) return;

        //刷新饰品最大魔力加成（基础/加成分离：水晶升级不受饰品影响）
        if(player.tickCount % 20 == 0) {
            PlayerMana mana = player.getData(ModAttachments.PLAYER_MANA);
            int bonus = AccessoryUtil.getMaxManaBonus(player);
            if(mana.getBonusMaxMana() != bonus) {
                mana.setBonusMaxMana(bonus);
                player.setData(ModAttachments.PLAYER_MANA, mana);
                sync(player);
            }
        }

        if(isUsingMagicWeapon(player)) return;

        PlayerMana mana = player.getData(ModAttachments.PLAYER_MANA);
        double delayMultiplier = AccessoryUtil.getManaRecoverDelayMultiplier(player);
        if(mana.tickRecoverDelay(delayMultiplier)) {
            player.setData(ModAttachments.PLAYER_MANA, mana);
            return;
        }

        int oldMana = mana.getMana();
        double regenBonus = AccessoryUtil.getManaRegenBonus(player, mana.getMana(), mana.getMaxMana());
        if(!mana.recoverMana(PlayerMana.DEFAULT_RECOVER_RATE + regenBonus)) return;

        player.setData(ModAttachments.PLAYER_MANA, mana);
        if(mana.getMana() != oldMana) {
            playMaxManaSoundIfRecovered(player, oldMana, mana);
            sync(player);
        }
    }

    public static void sync(ServerPlayer player) {
        PlayerMana mana = player.getData(ModAttachments.PLAYER_MANA);
        PacketDistributor.sendToPlayer(player, new ManaSyncPayload(mana.getMana(), mana.getMaxMana()));
    }

    public static void playMaxManaSoundIfRecovered(ServerPlayer player, int oldMana, PlayerMana mana) {
        if(oldMana < mana.getMaxMana() && mana.getMana() >= mana.getMaxMana()) {
            player.playNotifySound(ModSounds.MAX_MANA.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
        }
    }

    private static boolean isUsingMagicWeapon(ServerPlayer player) {
        if(!player.isUsingItem()) return false;

        ItemStack stack = player.getUseItem();
        return stack.getItem() instanceof MagicWeapon;
    }
}
