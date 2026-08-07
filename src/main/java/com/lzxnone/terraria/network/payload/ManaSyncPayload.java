package com.lzxnone.terraria.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ManaSyncPayload(int mana, int baseMaxMana, int bonusMaxMana) implements CustomPacketPayload {
    public static final Type<ManaSyncPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath("lzxnoneterraria", "mana_sync_payload")
    );

    public static final StreamCodec<FriendlyByteBuf, ManaSyncPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, ManaSyncPayload::mana,
        ByteBufCodecs.INT, ManaSyncPayload::baseMaxMana,
        ByteBufCodecs.INT, ManaSyncPayload::bonusMaxMana,
        ManaSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
