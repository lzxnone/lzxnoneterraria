package com.lzxnone.terraria.network.handler;

import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.network.payload.ToggleSummonFreeTargetingPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ToggleSummonFreeTargetingHandler {
    public static void handle(final ToggleSummonFreeTargetingPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if(player.level().isClientSide()) return;

            boolean enabled = !player.getData(ModAttachments.SUMMON_FREE_TARGETING);
            player.setData(ModAttachments.SUMMON_FREE_TARGETING, enabled);
            player.displayClientMessage(
                Component.translatable(enabled
                    ? "message.lzxnoneterraria.summon_free_targeting_enabled"
                    : "message.lzxnoneterraria.summon_free_targeting_disabled"),
                true
            );
        });
    }
}
