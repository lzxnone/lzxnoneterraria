package com.lzxnone.terraria.attachment;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TargetMarks {
    public static final String PROPHETIC = "prophetic";
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

    public static class Mark {
        public static final Codec<Mark> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.optionalFieldOf("start_time", 0L).forGetter(Mark::getStartTime),
            Codec.INT.optionalFieldOf("duration", 0).forGetter(Mark::getDuration),
            Codec.INT.optionalFieldOf("stacks", 0).forGetter(Mark::getStacks),
            UUID_CODEC.optionalFieldOf("owner", EMPTY_OWNER).forGetter(Mark::getOwner),
            ItemStack.OPTIONAL_CODEC.optionalFieldOf("stack", ItemStack.EMPTY).forGetter(Mark::getStack)
        ).apply(instance, Mark::new));

        private long startTime;
        private int duration;
        private int stacks;
        private UUID owner;
        private ItemStack stack;

        public Mark() {
            this(0L, 0, 0, EMPTY_OWNER, ItemStack.EMPTY);
        }

        public Mark(long startTime, int duration, int stacks) {
            this(startTime, duration, stacks, EMPTY_OWNER, ItemStack.EMPTY);
        }

        public Mark(long startTime, int duration, int stacks, UUID owner, ItemStack stack) {
            this.startTime = startTime;
            this.duration = duration;
            this.stacks = stacks;
            this.owner = owner == null ? EMPTY_OWNER : owner;
            this.stack = stack == null ? ItemStack.EMPTY : stack.copy();
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

        public Mark copy() {
            return new Mark(startTime, duration, stacks, owner, stack);
        }
    }
}
