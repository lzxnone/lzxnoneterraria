package com.lzxnone.terraria.attachment;

import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "lzxnoneterraria");

    public static final Supplier<AttachmentType<List<UUID>>> STUCK_DEVILS_DEVASTATION_PROJECTILE =
        ATTACHMENT_TYPES.register("stuck_devils_devastation_projectile", () -> AttachmentType.<List<UUID>>builder((Supplier<List<UUID>>) ArrayList::new).serialize(Codec.list(Codec.STRING.xmap(UUID::fromString, UUID::toString))).build());

    public static final Supplier<AttachmentType<Optional<UUID>>> DEVILS_DEVASTATION_MARK =
        ATTACHMENT_TYPES.register("devils_devastation_mark", () -> AttachmentType.<Optional<UUID>>builder(Optional::empty).serialize(Codec.STRING.xmap(UUID::fromString, UUID::toString).optionalFieldOf("mark").codec()).build());

    public static final Supplier<AttachmentType<Integer>> DEVILS_DEVASTATION_HIT_COUNT =
        ATTACHMENT_TYPES.register("devils_devastation_hit_count", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());
}
