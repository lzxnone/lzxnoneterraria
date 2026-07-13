package com.lzxnone.terraria.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record FirstFractalScrollPayload(double scrollDelta) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<FirstFractalScrollPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath("lzxnoneterraria", "first_fractal_scroll_payload")
    );

    public static final StreamCodec<FriendlyByteBuf, FirstFractalScrollPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, FirstFractalScrollPayload::scrollDelta,
            FirstFractalScrollPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
