package com.lzxnone.terraria.network.payload;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.attachment.PlayerSummon;
import com.mojang.serialization.DataResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PlayerSummonSyncPayload(CompoundTag summonData) implements CustomPacketPayload {
    public static final Type<PlayerSummonSyncPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "player_summon_sync_payload")
    );

    public static final StreamCodec<FriendlyByteBuf, PlayerSummonSyncPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.COMPOUND_TAG, PlayerSummonSyncPayload::summonData,
        PlayerSummonSyncPayload::new
    );

    public PlayerSummonSyncPayload {
        summonData = summonData == null ? new CompoundTag() : summonData.copy();
    }

    public static PlayerSummonSyncPayload from(PlayerSummon summonData) {
        DataResult<Tag> result = PlayerSummon.CODEC.encodeStart(NbtOps.INSTANCE, summonData);
        Tag tag = result.result().orElseGet(CompoundTag::new);
        return new PlayerSummonSyncPayload(tag instanceof CompoundTag compoundTag ? compoundTag : new CompoundTag());
    }

    public PlayerSummon toPlayerSummon() {
        return PlayerSummon.CODEC.parse(NbtOps.INSTANCE, summonData)
            .result()
            .orElseGet(PlayerSummon::new);
    }

    @Override
    public CompoundTag summonData() {
        return summonData.copy();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
