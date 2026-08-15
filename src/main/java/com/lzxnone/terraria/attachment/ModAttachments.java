package com.lzxnone.terraria.attachment;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static final Supplier<AttachmentType<PlayerSummon>> PLAYER_SUMMON =
        ATTACHMENT_TYPES.register("player_summon", () -> AttachmentType.builder(PlayerSummon::new).serialize(PlayerSummon.CODEC).copyOnDeath().build());

    public static final Supplier<AttachmentType<PlayerMana>> PLAYER_MANA =
        ATTACHMENT_TYPES.register("player_mana", () -> AttachmentType.builder(PlayerMana::new).serialize(PlayerMana.CODEC).copyOnDeath().build());

    public static final Supplier<AttachmentType<TargetMarks>> TARGET_MARKS =
        ATTACHMENT_TYPES.register("target_marks", () -> AttachmentType.builder((Supplier<TargetMarks>) TargetMarks::new).serialize(TargetMarks.CODEC).build());

    public static final Supplier<AttachmentType<Optional<UUID>>> PALADIN_GUARDIAN_SOURCE =
        ATTACHMENT_TYPES.register("paladin_guardian_source", () -> AttachmentType.<Optional<UUID>>builder(Optional::empty).serialize(Codec.STRING.xmap(UUID::fromString, UUID::toString).optionalFieldOf("guardian").codec()).build());

    public static final Supplier<AttachmentType<LavaImmunity>> LAVA_IMMUNITY =
        ATTACHMENT_TYPES.register("lava_immunity", () -> AttachmentType.builder(LavaImmunity::new).build());

    //按攻击来源隔离的命中冷却：key = 攻击来源实体 UUID，value = 下次最早允许命中的服务器时间（刻）
    public static final Supplier<AttachmentType<Map<UUID, Long>>> SOURCE_HIT_COOLDOWNS =
        ATTACHMENT_TYPES.register("source_hit_cooldowns", () -> AttachmentType.<Map<UUID, Long>>builder((Supplier<Map<UUID, Long>>) HashMap::new).build());

    //玩家当前活跃的刀光实体引用列表（双端各自维护，供渲染层判断刀光是否在场，行为名从实体 entityData 获取）
    public static final Supplier<AttachmentType<List<Entity>>> ACTIVE_BEAMS =
        ATTACHMENT_TYPES.register("active_beams", () -> AttachmentType.<List<Entity>>builder((Supplier<List<Entity>>) ArrayList::new).build());

}
