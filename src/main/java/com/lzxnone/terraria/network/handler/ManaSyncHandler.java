package com.lzxnone.terraria.network.handler;

import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.attachment.PlayerMana;
import com.lzxnone.terraria.network.payload.ManaSyncPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ManaSyncHandler {
    public static void handle(final ManaSyncPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            PlayerMana mana = context.player().getData(ModAttachments.PLAYER_MANA);
            mana.setBaseMaxMana(payload.baseMaxMana());
            mana.setBonusMaxMana(payload.bonusMaxMana());
            mana.setMana(payload.mana());
            context.player().setData(ModAttachments.PLAYER_MANA, mana);
        });
    }
}
