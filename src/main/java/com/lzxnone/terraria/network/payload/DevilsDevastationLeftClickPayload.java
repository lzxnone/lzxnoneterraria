package com.lzxnone.terraria.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class DevilsDevastationLeftClickPayload implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<DevilsDevastationLeftClickPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath("lzxnoneterraria", "devils_devastation_left_click_payload")
    );

    public static final StreamCodec<FriendlyByteBuf, DevilsDevastationLeftClickPayload> CODEC =
        StreamCodec.of(
            (buf, payload) -> {},
            buf -> new DevilsDevastationLeftClickPayload()
        );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
