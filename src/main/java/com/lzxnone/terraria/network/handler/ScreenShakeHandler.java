package com.lzxnone.terraria.network.handler;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.network.payload.ScreenShakePayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ScreenShakeHandler {
    public static void handle(final ScreenShakePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if(FMLEnvironment.dist != Dist.CLIENT) return;
            try {
                Class<?> handler = Class.forName("com.lzxnone.terraria.client.event.ScreenShakeHandler");
                handler.getMethod("startShake", int.class, float.class).invoke(null, payload.duration(), payload.power());
            }catch (ReflectiveOperationException e) {
                LzxnoneTerraria.LOGGER.warn("Failed to start screen shake", e);
            }
        });
    }
}
