package com.lzxnone.terraria.network;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.network.handler.*;
import com.lzxnone.terraria.network.payload.*;
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
        registrar.playToServer(
                ZenithScrollPayload.TYPE,
                ZenithScrollPayload.CODEC,
                ZenithScrollHandler::handle
        );
        registrar.playToServer(
                FirstFractalScrollPayload.TYPE,
                FirstFractalScrollPayload.CODEC,
                FirstFractalScrollHandler::handle
        );
        registrar.playToServer(
                DevilsDevastationKillModePayload.TYPE,
                DevilsDevastationKillModePayload.CODEC,
                DevilsDevastationKillModeHandler::handle
        );
        registrar.playToServer(
                DevilsDevastationLeftClickPayload.TYPE,
                DevilsDevastationLeftClickPayload.CODEC,
                DevilsDevastationLeftClickHandler::handle
        );
        registrar.playToServer(
                ClearSummonPayload.TYPE,
                ClearSummonPayload.CODEC,
                ClearSummonHandler::handle
        );
        registrar.playToServer(
                ToggleSummonAttackModePayload.TYPE,
                ToggleSummonAttackModePayload.CODEC,
                ToggleSummonAttackModeHandler::handle
        );
        registrar.playToClient(
                ScreenShakePayload.TYPE,
                ScreenShakePayload.CODEC,
                ScreenShakeHandler::handle
        );
    }
}
