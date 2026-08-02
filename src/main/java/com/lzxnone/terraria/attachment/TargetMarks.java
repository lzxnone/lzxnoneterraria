package com.lzxnone.terraria.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TargetMarks {
    public static final String PROPHETIC = "prophetic";
    private static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(UUID::fromString, UUID::toString);
    private static final UUID EMPTY_OWNER = new UUID(0L, 0L);

    public static final Codec<TargetMarks> CODEC = Codec.unboundedMap(Codec.STRING, Mark.CODEC).xmap(TargetMarks::new, TargetMarks::getMarks);

    private final Map<String, Mark> marks;

    public TargetMarks() {
        this(Map.of());
    }

    public TargetMarks(Map<String, Mark> marks) {
        this.marks = new HashMap<>();
        if(marks != null) this.marks.putAll(marks);
    }

    public Map<String, Mark> getMarks() {
        return marks;
    }

    public TargetMarks copy() {
        return new TargetMarks(marks);
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
