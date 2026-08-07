package com.lzxnone.terraria.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;

public class PlayerMana {
    public static final int DEFAULT_BASE_MAX_MANA = 20;
    public static final double DEFAULT_RECOVER_RATE = 1.0D;
    public static final int MAX_BASE_MANA = 200;
    public static final int MAX_MANA = 400;

    public static final Codec<PlayerMana> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.optionalFieldOf("mana", DEFAULT_BASE_MAX_MANA).forGetter(PlayerMana::getMana),
        Codec.INT.optionalFieldOf("base_max_mana", DEFAULT_BASE_MAX_MANA).forGetter(PlayerMana::getBaseMaxMana),
        Codec.DOUBLE.optionalFieldOf("consume_progress", 0.0D).forGetter(PlayerMana::getConsumeProgress),
        Codec.DOUBLE.optionalFieldOf("recover_progress", 0.0D).forGetter(PlayerMana::getRecoverProgress),
        Codec.INT.optionalFieldOf("recover_delay", 0).forGetter(PlayerMana::getRecoverDelay)
    ).apply(instance, PlayerMana::new));

    private int mana;
    private int baseMaxMana;
    private int bonusMaxMana;
    private double consumeProgress;
    private double recoverProgress;
    private int recoverDelay;

    public PlayerMana() {
        this(DEFAULT_BASE_MAX_MANA, DEFAULT_BASE_MAX_MANA, 0.0D, 0.0D, 0);
    }

    public PlayerMana(int mana, int baseMaxMana, double consumeProgress, double recoverProgress, int recoverDelay) {
        this.baseMaxMana = Math.max(0, baseMaxMana);
        this.mana = Mth.clamp(mana, 0, getMaxMana());
        this.consumeProgress = Math.max(0.0D, consumeProgress);
        this.recoverProgress = Math.max(0.0D, recoverProgress);
        this.recoverDelay = Math.max(0, recoverDelay);
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = Mth.clamp(mana, 0, getMaxMana());
    }

    public int getMaxMana() {
        return Math.min(baseMaxMana + bonusMaxMana, MAX_MANA);
    }

    public int getBaseMaxMana() {
        return baseMaxMana;
    }

    public void setBaseMaxMana(int baseMaxMana) {
        this.baseMaxMana = Math.max(0, baseMaxMana);
        this.mana = Math.min(mana, getMaxMana());
    }

    public int getBonusMaxMana() {
        return bonusMaxMana;
    }

    public void setBonusMaxMana(int bonusMaxMana) {
        this.bonusMaxMana = Math.max(0, bonusMaxMana);
        this.mana = Math.min(mana, getMaxMana());
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

    //通过魔力水晶增加基础最大魔力
    public int increaseBaseMaxMana(int amount) {
        if(amount <= 0 || baseMaxMana >= MAX_BASE_MANA) return 0;

        int oldBaseMaxMana = baseMaxMana;
        setBaseMaxMana(Math.min(baseMaxMana + amount, MAX_BASE_MANA));
        int increase = baseMaxMana - oldBaseMaxMana;
        setMana(mana + increase);
        return increase;
    }

    //通过累加计数器恢复魔力
    public boolean recoverMana(double amount) {
        if(amount <= 0.0D || mana >= getMaxMana()) return false;

        int recoverAmount = addManaProgress(false, amount);
        if(recoverAmount <= 0) return true;

        int oldMana = mana;
        setMana(mana + recoverAmount);
        return mana != oldMana;
    }

    //直接恢复指定值的魔力
    public boolean recoverManaImmediately(int amount) {
        if(amount <= 0 || mana >= getMaxMana()) return false;

        int oldMana = mana;
        setMana(mana + amount);
        return mana != oldMana;
    }

    //通过累加计数器消耗魔力
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

    //设置恢复延迟
    public void applyRecoverDelay() {
        if(getMaxMana() <= 0) return;

        double emptyRatio = 1.0D - (double) mana / getMaxMana();
        recoverDelay = Math.max(recoverDelay, (int) Math.ceil(0.7D * (emptyRatio * 240.0D + 45.0D)));
        recoverProgress = 0.0D;
    }

    //减少恢复延迟，返回延迟是否完成
    public boolean tickRecoverDelay(double delayMultiplier) {
        if(recoverDelay <= 0) return false;

        int step = Math.max(1, (int) Math.ceil(1.0D / Math.max(0.01D, delayMultiplier)));
        recoverDelay = Math.max(0, recoverDelay - step);
        return recoverDelay > 0;
    }

    //累计计数器
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
