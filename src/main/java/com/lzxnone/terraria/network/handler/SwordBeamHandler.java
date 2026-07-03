package com.lzxnone.terraria.network.handler;

import com.lzxnone.terraria.entity.beam.SwordBeamBehaviors;
import com.lzxnone.terraria.network.payload.SwordBeamPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SwordBeamHandler {
    public static void handle(final SwordBeamPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            if(level.isClientSide()) return;
            SwordBeamBehaviors.getBehavior(payload.id()).generate(context.player(), payload.beamData());
        });
    }
}
