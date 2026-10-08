package com.lzxnone.terraria.attachment;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TargetMarks {
    public static final String POSSESSION = "possession";
    public static final String KALEIDOSCOPE = "kaleidoscope";
    private static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(UUID::fromString, UUID::toString);
    private static final UUID EMPTY_OWNER = new UUID(0L, 0L);
    private static final Codec<CompoundTag> COMPOUND_TAG_CODEC = Codec.PASSTHROUGH.xmap(
        dynamic -> {
            Tag tag = dynamic.convert(NbtOps.INSTANCE).getValue();
            return tag instanceof CompoundTag compoundTag ? compoundTag.copy() : new CompoundTag();
        },
        tag -> new Dynamic<>(NbtOps.INSTANCE, tag.copy())
    );
    private static final Codec<Map<String, Mark>> MARKS_CODEC = Codec.unboundedMap(Codec.STRING, Mark.CODEC);
    private static final Codec<TargetMarks> LEGACY_CODEC = MARKS_CODEC.xmap(TargetMarks::new, TargetMarks::getMarks);
    private static final Codec<TargetMarks> RECORD_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        MARKS_CODEC.fieldOf("marks").forGetter(TargetMarks::getMarks),
        COMPOUND_TAG_CODEC.optionalFieldOf("custom_data", new CompoundTag()).forGetter(TargetMarks::getCustomData)
    ).apply(instance, TargetMarks::new));

    public static final Codec<TargetMarks> CODEC = Codec.either(RECORD_CODEC, LEGACY_CODEC).xmap(
        either -> either.map(targetMarks -> targetMarks, targetMarks -> targetMarks),
        targetMarks -> Either.left(targetMarks)
    );

    private final Map<String, Mark> marks;
    private CompoundTag customData;

    public TargetMarks() {
        this(Map.of(), new CompoundTag());
    }

    public TargetMarks(Map<String, Mark> marks) {
        this(marks, new CompoundTag());
    }

    public TargetMarks(Map<String, Mark> marks, CompoundTag customData) {
        this.marks = new HashMap<>();
        if(marks != null) {
            marks.forEach((id, mark) -> {
                if(id != null && mark != null) this.marks.put(id, mark.copy());
            });
        }
        this.customData = customData == null ? new CompoundTag() : customData.copy();
    }

    public Map<String, Mark> getMarks() {
        return marks;
    }

    public CompoundTag getCustomData() {
        return customData.copy();
    }

    public void setCustomData(CompoundTag customData) {
        this.customData = customData == null ? new CompoundTag() : customData.copy();
    }

    public TargetMarks copy() {
        return new TargetMarks(marks, customData);
    }

    public void addMark(String id, Mark mark, int maxSlots, long gameTime) {
        if(id == null || mark == null) return;

        // 1. 同一标记直接刷新
        if(marks.containsKey(id)) {
            marks.put(id, mark);
            return;
        }

        // 2. 清理已过期的标记
        if(gameTime > 0) {
            marks.entrySet().removeIf(entry -> {
                Mark m = entry.getValue();
                return m != null && m.getDuration() > 0 && gameTime - m.getStartTime() >= m.getDuration();
            });
        }

        // 3. 统计该施加者的有效标记
        UUID owner = mark.getOwner();
        List<Map.Entry<String, Mark>> ownerMarks = new ArrayList<>();
        for(Map.Entry<String, Mark> entry : marks.entrySet()) {
            Mark m = entry.getValue();
            if(m != null && (owner == null || owner.equals(m.getOwner()))) {
                ownerMarks.add(entry);
            }
        }

        // 4. 若超出最大槽位上限，按 startTime 剔除最老的标记（FIFO）
        while(ownerMarks.size() >= maxSlots && !ownerMarks.isEmpty()) {
            Map.Entry<String, Mark> oldest = Collections.min(
                ownerMarks,
                Comparator.comparingLong(e -> e.getValue().getStartTime())
            );
            marks.remove(oldest.getKey());
            ownerMarks.remove(oldest);
        }

        // 5. 存入新标记
        marks.put(id, mark);
    }

    public static class Mark {
        public static final Codec<Mark> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.optionalFieldOf("start_time", 0L).forGetter(Mark::getStartTime),
            Codec.INT.optionalFieldOf("duration", 0).forGetter(Mark::getDuration),
            Codec.INT.optionalFieldOf("stacks", 0).forGetter(Mark::getStacks),
            UUID_CODEC.optionalFieldOf("owner", EMPTY_OWNER).forGetter(Mark::getOwner),
            ItemStack.OPTIONAL_CODEC.optionalFieldOf("stack", ItemStack.EMPTY).forGetter(Mark::getStack),
            Codec.FLOAT.optionalFieldOf("tag_damage", 0.0F).forGetter(Mark::getTagDamage),
            Codec.INT.optionalFieldOf("tag_crit", 0).forGetter(Mark::getTagCrit)
        ).apply(instance, Mark::new));

        private long startTime;
        private int duration;
        private int stacks;
        private UUID owner;
        private ItemStack stack;
        private float tagDamage;
        private int tagCrit;

        public Mark() {
            this(0L, 0, 0, EMPTY_OWNER, ItemStack.EMPTY, 0.0F, 0);
        }

        public Mark(long startTime, int duration, int stacks) {
            this(startTime, duration, stacks, EMPTY_OWNER, ItemStack.EMPTY, 0.0F, 0);
        }

        public Mark(long startTime, int duration, int stacks, UUID owner, ItemStack stack) {
            this(startTime, duration, stacks, owner, stack, 0.0F, 0);
        }

        public Mark(long startTime, int duration, int stacks, UUID owner, ItemStack stack, float tagDamage, int tagCrit) {
            this.startTime = startTime;
            this.duration = duration;
            this.stacks = stacks;
            this.owner = owner == null ? EMPTY_OWNER : owner;
            this.stack = stack == null ? ItemStack.EMPTY : stack.copy();
            this.tagDamage = tagDamage;
            this.tagCrit = tagCrit;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getStacks() {
            return stacks;
        }

        public void setStacks(int stacks) {
            this.stacks = stacks;
        }

        public UUID getOwner() {
            return owner;
        }

        public void setOwner(UUID owner) {
            this.owner = owner == null ? EMPTY_OWNER : owner;
        }

        public ItemStack getStack() {
            return stack;
        }

        public void setStack(ItemStack stack) {
            this.stack = stack == null ? ItemStack.EMPTY : stack.copy();
        }

        public float getTagDamage() {
            return tagDamage;
        }

        public void setTagDamage(float tagDamage) {
            this.tagDamage = tagDamage;
        }

        public int getTagCrit() {
            return tagCrit;
        }

        public void setTagCrit(int tagCrit) {
            this.tagCrit = tagCrit;
        }

        public Mark copy() {
            return new Mark(startTime, duration, stacks, owner, stack, tagDamage, tagCrit);
        }
    }
}
