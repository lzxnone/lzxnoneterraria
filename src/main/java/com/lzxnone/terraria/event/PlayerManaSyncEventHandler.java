package com.lzxnone.terraria.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.attachment.PlayerMana;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.item.accessory.AccessoryUtil;
import com.lzxnone.terraria.item.armor.ArmorUtil;
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

        //刷新饰品与防具最大魔力加成
        if(player.tickCount % 20 == 0) {
            PlayerMana mana = player.getData(ModAttachments.PLAYER_MANA);
            int bonus = AccessoryUtil.getMaxManaBonus(player) + ArmorUtil.getMaxManaBonus(player);
            if(mana.getBonusMaxMana() != bonus) {
                PlayerMana.setBonusMaxMana(player, bonus);
            }
        }

        PlayerMana mana = player.getData(ModAttachments.PLAYER_MANA);

        /*double delayMultiplier = AccessoryUtil.getManaRecoverDelayMultiplier(player);
        if(mana.tickRecoverDelay(delayMultiplier)) {
            player.setData(ModAttachments.PLAYER_MANA, mana);
            return;
        }*/

        boolean hasManaRegenBuff = player.hasEffect(ModEffects.MANA_REGENERATION);
        double manaRegenBonus = AccessoryUtil.getManaRegenBonus(player);
        double standingBonus = (hasManaRegenBuff || !isPlayerMoving(player)) ? 2.0D : 1.0D;
        double regenCoeff = hasManaRegenBuff ? 1.0D : (mana.getMaxMana() > 0 ? ((double) mana.getMana() / mana.getMaxMana()) * 0.5D + 0.5D : 1.0D);
        double itemUseCoeff = isUsingMagicWeapon(player) ? 0.05D : 1.0D;

        PlayerMana.recoverMana(player, manaRegenBonus, standingBonus, regenCoeff, itemUseCoeff);
    }

    public static void sync(ServerPlayer player) {
        PlayerMana mana = player.getData(ModAttachments.PLAYER_MANA);
        PacketDistributor.sendToPlayer(player, new ManaSyncPayload(mana.getMana(), mana.getBaseMaxMana(), mana.getBonusMaxMana()));
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

    private static boolean isPlayerMoving(ServerPlayer player) {
        return Math.abs(player.getX() - player.xo) > 0.001D
            || Math.abs(player.getY() - player.yo) > 0.001D
            || Math.abs(player.getZ() - player.zo) > 0.001D
            || player.getDeltaMovement().horizontalDistanceSqr() > 1.0E-4D;
    }
}
