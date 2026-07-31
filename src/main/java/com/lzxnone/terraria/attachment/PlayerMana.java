package com.lzxnone.terraria.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;

public class PlayerMana {
    public static final int DEFAULT_MAX_MANA = 20;
    public static final double DEFAULT_RECOVER_RATE = 1.0D;

    public static final Codec<PlayerMana> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.optionalFieldOf("mana", DEFAULT_MAX_MANA).forGetter(PlayerMana::getMana),
        Codec.INT.optionalFieldOf("max_mana", DEFAULT_MAX_MANA).forGetter(PlayerMana::getMaxMana),
        Codec.DOUBLE.optionalFieldOf("consume_progress", 0.0D).forGetter(PlayerMana::getConsumeProgress),
        Codec.DOUBLE.optionalFieldOf("recover_progress", 0.0D).forGetter(PlayerMana::getRecoverProgress),
        Codec.INT.optionalFieldOf("recover_delay", 0).forGetter(PlayerMana::getRecoverDelay)
    ).apply(instance, PlayerMana::new));

    private int mana;
    private int maxMana;
    private double consumeProgress;
    private double recoverProgress;
    private int recoverDelay;

    public PlayerMana() {
        this(DEFAULT_MAX_MANA, DEFAULT_MAX_MANA, 0.0D, 0.0D, 0);
    }

    public PlayerMana(int mana, int maxMana) {
        this(mana, maxMana, 0.0D, 0.0D, 0);
    }

    public PlayerMana(int mana, int maxMana, double consumeProgress, double recoverProgress) {
        this(mana, maxMana, consumeProgress, recoverProgress, 0);
    }

    public PlayerMana(int mana, int maxMana, double consumeProgress, double recoverProgress, int recoverDelay) {
        this.maxMana = Math.max(0, maxMana);
        this.mana = Mth.clamp(mana, 0, this.maxMana);
        this.consumeProgress = Math.max(0.0D, consumeProgress);
        this.recoverProgress = Math.max(0.0D, recoverProgress);
        this.recoverDelay = Math.max(0, recoverDelay);
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = Mth.clamp(mana, 0, maxMana);
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = Math.max(0, maxMana);
        this.mana = Math.min(mana, this.maxMana);
    }

    public double getConsumeProgress() {
        return consumeProgress;
    }

    public double getRecoverProgress() {
        return recoverProgress;
    }

    public int getRecoverDelay() {
        return recoverDelay;
    }

    public boolean hasMana() {
        return mana > 0;
    }

    public boolean recoverMana(double amount) {
        if(amount <= 0.0D || mana >= maxMana) return false;

        int recoverAmount = addManaProgress(false, amount);
        if(recoverAmount <= 0) return true;

        int oldMana = mana;
        setMana(mana + recoverAmount);
        return mana != oldMana;
    }

    public boolean recoverManaImmediately(int amount) {
        if(amount <= 0 || mana >= maxMana) return false;

        int oldMana = mana;
        setMana(mana + amount);
        return mana != oldMana;
    }

    public boolean consumeMana(double amount) {
        if(amount <= 0.0D) return true;
        if(mana <= 0) return false;

        int consumeAmount = addManaProgress(true, amount);
        if(consumeAmount <= 0) return mana > 0;

        if(consumeAmount >= mana) {
            mana = 0;
            return false;
        }

        mana -= consumeAmount;
        return true;
    }

    public void applyRecoverDelay() {
        if(maxMana <= 0) return;

        double emptyRatio = 1.0D - (double) mana / maxMana;
        recoverDelay = Math.max(recoverDelay, (int) Math.ceil(0.7D * (emptyRatio * 240.0D + 45.0D)));
        recoverProgress = 0.0D;
    }

    public boolean tickRecoverDelay() {
        if(recoverDelay <= 0) return false;

        recoverDelay--;
        return true;
    }

    private int addManaProgress(boolean consume, double amount) {
        double progress = (consume ? consumeProgress : recoverProgress) + amount;
        int wholeAmount = (int)Math.floor(progress);
        double nextProgress = progress - wholeAmount;

        if(consume) {
            consumeProgress = nextProgress;
        }else {
            recoverProgress = nextProgress;
        }
        return wholeAmount;
    }
}
