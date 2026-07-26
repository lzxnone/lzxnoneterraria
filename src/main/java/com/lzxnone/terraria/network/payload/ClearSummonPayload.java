package com.lzxnone.terraria.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClearSummonPayload(String id) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClearSummonPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath("lzxnoneterraria", "clear_summon_payload")
    );

    public static final StreamCodec<FriendlyByteBuf, ClearSummonPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ClearSummonPayload::id,
            ClearSummonPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
