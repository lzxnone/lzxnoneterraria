package com.lzxnone.terraria.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class PlayerSummon {
    private static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(UUID::fromString, UUID::toString);
    private static final Codec<List<SummonSlot>> SUMMON_SLOTS_CODEC = Codec.list(SummonSlot.CODEC);
    private static final Codec<State> STATE_CODEC = Codec.STRING.xmap(State::byName, State::getSerializedName);

    public static final Codec<PlayerSummon> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        SUMMON_SLOTS_CODEC.optionalFieldOf("minions", List.of()).forGetter(PlayerSummon::getMinionSlots),
        STATE_CODEC.optionalFieldOf("minion_state", State.AGGRESSIVE).forGetter(PlayerSummon::getMinionState),
        SUMMON_SLOTS_CODEC.optionalFieldOf("sentries", List.of()).forGetter(PlayerSummon::getSentrySlots),
        STATE_CODEC.optionalFieldOf("sentry_state", State.AGGRESSIVE).forGetter(PlayerSummon::getSentryState)
    ).apply(instance, PlayerSummon::new));

    private List<SummonSlot> minionSlots;
    private State minionState;
    private List<SummonSlot> sentrySlots;
    private State sentryState;

    public PlayerSummon() {
        this(List.of(), State.AGGRESSIVE, List.of(), State.AGGRESSIVE);
    }

    public PlayerSummon(List<SummonSlot> minionSlots, State minionState, List<SummonSlot> sentrySlots, State sentryState) {
        this.minionSlots = copySlots(minionSlots);
        this.minionState = minionState == null ? State.AGGRESSIVE : minionState;
        this.sentrySlots = copySlots(sentrySlots);
        this.sentryState = sentryState == null ? State.AGGRESSIVE : sentryState;
    }

    public List<SummonSlot> getMinionSlots() {
        return minionSlots;
    }

    public void setMinionSlots(List<SummonSlot> minionSlots) {
        this.minionSlots = copySlots(minionSlots);
    }

    public State getMinionState() {
        return minionState;
    }

    public void setMinionState(State minionState) {
        this.minionState = minionState == null ? State.AGGRESSIVE : minionState;
    }

    public State cycleMinionState() {
        State[] states = State.values();
        minionState = states[(minionState.ordinal() + 1) % states.length];
        return minionState;
    }

    public List<SummonSlot> getSentrySlots() {
        return sentrySlots;
    }

    public void setSentrySlots(List<SummonSlot> sentrySlots) {
        this.sentrySlots = copySlots(sentrySlots);
    }

    public State getSentryState() {
        return sentryState;
    }

    public void setSentryState(State sentryState) {
        this.sentryState = sentryState == null ? State.AGGRESSIVE : sentryState;
    }

    public State cycleSentryState() {
        State[] states = State.values();
        sentryState = states[(sentryState.ordinal() + 1) % states.length];
        return sentryState;
    }

    private static List<SummonSlot> copySlots(List<SummonSlot> slots) {
        List<SummonSlot> copied = new ArrayList<>();
        for(SummonSlot slot : slots) {
            if(slot == null || slot.getSummons().isEmpty()) continue;
            copied.add(slot.copy());
        }
        return copied;
    }

    public static class SummonSlot {
        public static final Codec<SummonSlot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("id", "").forGetter(SummonSlot::getId),
            Codec.list(UUID_CODEC).optionalFieldOf("summons", List.of()).forGetter(SummonSlot::getSummons)
        ).apply(instance, SummonSlot::new));

        private String id;
        private final List<UUID> summons;

        public SummonSlot(String id, List<UUID> summons) {
            this.id = id == null ? "" : id;
            this.summons = summons == null ? new ArrayList<>() : new ArrayList<>(summons);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id == null ? "" : id;
        }

        public List<UUID> getSummons() {
            return summons;
        }

        public void addSummon(UUID uuid) {
            if(uuid != null) summons.add(uuid);
        }

        public void removeSummon(UUID uuid) {
            summons.remove(uuid);
        }

        public void setSummons(List<UUID> summons) {
            this.summons.clear();
            if(summons != null) this.summons.addAll(summons);
        }

        public SummonSlot copy() {
            return new SummonSlot(id, summons);
        }
    }

    public enum State {
        PASSIVE("message.lzxnoneterraria.summon_attack_mode.passive", ChatFormatting.GREEN),
        NEUTRAL("message.lzxnoneterraria.summon_attack_mode.neutral", ChatFormatting.YELLOW),
        AGGRESSIVE("message.lzxnoneterraria.summon_attack_mode.aggressive", ChatFormatting.RED),
        ATTACK_TARGET("message.lzxnoneterraria.summon_attack_mode.attack_target", ChatFormatting.BLUE);

        private final String translationKey;
        private final ChatFormatting color;

        State(String translationKey, ChatFormatting color) {
            this.translationKey = translationKey;
            this.color = color;
        }

        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public Component getDisplayName() {
            return Component.translatable(translationKey);
        }

        public ChatFormatting getColor() {
            return color;
        }

        private static State byName(String name) {
            for(State state : values()) {
                if(state.getSerializedName().equals(name)) return state;
            }
            return AGGRESSIVE;
        }
    }
}
