package com.lzxnone.terraria.network.payload;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SwordBeamPayload (String id, CompoundTag beamData) implements CustomPacketPayload {
    public static final Type<SwordBeamPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("lzxnoneterraria", "sword_beam_payload")
    );

    public static final StreamCodec<FriendlyByteBuf, SwordBeamPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SwordBeamPayload::id,
            ByteBufCodecs.COMPOUND_TAG, SwordBeamPayload::beamData,
            SwordBeamPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
