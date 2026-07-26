package com.lzxnone.terraria.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ToggleSummonFreeTargetingPayload(boolean ignored) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ToggleSummonFreeTargetingPayload> TYPE = new CustomPacketPayload.Type<>(
        ResourceLocation.fromNamespaceAndPath("lzxnoneterraria", "toggle_summon_free_targeting_payload")
    );

    public static final StreamCodec<FriendlyByteBuf, ToggleSummonFreeTargetingPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, ToggleSummonFreeTargetingPayload::ignored,
        ToggleSummonFreeTargetingPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
