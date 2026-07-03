package com.lzxnone.terraria.network;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.network.handler.SwordBeamHandler;
import com.lzxnone.terraria.network.payload.SwordBeamPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID)
public class ModNetwork {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1.0.0");
        registrar.playToServer(
                SwordBeamPayload.TYPE,
                SwordBeamPayload.CODEC,
                SwordBeamHandler::handle
        );
    }
}
