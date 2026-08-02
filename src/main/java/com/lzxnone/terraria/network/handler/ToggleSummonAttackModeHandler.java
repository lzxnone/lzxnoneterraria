package com.lzxnone.terraria.network.handler;

import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.attachment.PlayerSummon;
import com.lzxnone.terraria.network.payload.ToggleSummonAttackModePayload;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ToggleSummonAttackModeHandler {
    public static void handle(final ToggleSummonAttackModePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if(player.level().isClientSide()) return;

            PlayerSummon summonData = player.getData(ModAttachments.PLAYER_SUMMON);
            PlayerSummon.State mode = summonData.cycleMinionState();
            player.setData(ModAttachments.PLAYER_SUMMON, summonData);
            player.displayClientMessage(
                Component.translatable("message.lzxnoneterraria.summon_attack_mode", mode.getDisplayName()).withStyle(mode.getColor()),
                true
            );
        });
    }
}
