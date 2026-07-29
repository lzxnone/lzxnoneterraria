package com.lzxnone.terraria.network.handler;

import com.lzxnone.terraria.item.weapon.SummonWeapon;
import com.lzxnone.terraria.network.payload.ToggleSummonAttackModePayload;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ToggleSummonAttackModeHandler {
    public static void handle(final ToggleSummonAttackModePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if(player.level().isClientSide()) return;

            SummonWeapon.SummonAttackMode mode = SummonWeapon.cycleAttackMode(player);
            player.displayClientMessage(
                Component.translatable("message.lzxnoneterraria.summon_attack_mode", mode.getDisplayName()).withStyle(mode.getColor()),
                true
            );
        });
    }
}
