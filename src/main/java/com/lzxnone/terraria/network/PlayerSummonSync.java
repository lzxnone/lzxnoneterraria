package com.lzxnone.terraria.network;

import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.attachment.PlayerSummon;
import com.lzxnone.terraria.network.payload.PlayerSummonSyncPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public class PlayerSummonSync {
    public static void setAndSync(Player player, PlayerSummon summonData) {
        player.setData(ModAttachments.PLAYER_SUMMON, summonData);
        if(player instanceof ServerPlayer serverPlayer) sync(serverPlayer);
    }

    public static void sync(ServerPlayer player) {
        PlayerSummon summonData = player.getData(ModAttachments.PLAYER_SUMMON);
        PacketDistributor.sendToPlayer(player, PlayerSummonSyncPayload.from(summonData));
    }
}
