package com.lzxnone.terraria.network.handler;

import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.network.payload.PlayerSummonSyncPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PlayerSummonSyncHandler {
    public static void handle(final PlayerSummonSyncPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> context.player().setData(ModAttachments.PLAYER_SUMMON, payload.toPlayerSummon()));
    }
}
