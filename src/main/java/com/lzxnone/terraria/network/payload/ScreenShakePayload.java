package com.lzxnone.terraria.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ScreenShakePayload(int duration, float power) implements CustomPacketPayload {
    public static final Type<ScreenShakePayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath("lzxnoneterraria", "screen_shake_payload")
    );

    public static final StreamCodec<FriendlyByteBuf, ScreenShakePayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, ScreenShakePayload::duration,
        ByteBufCodecs.FLOAT, ScreenShakePayload::power,
        ScreenShakePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
