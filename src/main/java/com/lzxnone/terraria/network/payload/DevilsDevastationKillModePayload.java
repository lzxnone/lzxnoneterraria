package com.lzxnone.terraria.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class DevilsDevastationKillModePayload implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<DevilsDevastationKillModePayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath("lzxnoneterraria", "devils_devastation_kill_mode_payload")
    );

    public static final StreamCodec<FriendlyByteBuf, DevilsDevastationKillModePayload> CODEC =
        StreamCodec.of(
            (buf, payload) -> {},
            buf -> new DevilsDevastationKillModePayload()
        );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
