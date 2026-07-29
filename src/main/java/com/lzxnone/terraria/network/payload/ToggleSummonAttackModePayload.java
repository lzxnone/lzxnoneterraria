package com.lzxnone.terraria.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class ToggleSummonAttackModePayload implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ToggleSummonAttackModePayload> TYPE = new CustomPacketPayload.Type<>(
        ResourceLocation.fromNamespaceAndPath("lzxnoneterraria", "toggle_summon_attack_mode_payload")
    );

    public static final StreamCodec<FriendlyByteBuf, ToggleSummonAttackModePayload> CODEC =
        StreamCodec.of(
            (buf, payload) -> {},
            buf -> new ToggleSummonAttackModePayload()
        );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
