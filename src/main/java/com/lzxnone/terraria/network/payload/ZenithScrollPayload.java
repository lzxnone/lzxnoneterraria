package com.lzxnone.terraria.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ZenithScrollPayload(double scrollDelta) implements CustomPacketPayload {
    public static final Type<ZenithScrollPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("lzxnoneterraria", "zenith_scroll_payload")
    );

    public static final StreamCodec<FriendlyByteBuf, ZenithScrollPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, ZenithScrollPayload::scrollDelta,
            ZenithScrollPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
