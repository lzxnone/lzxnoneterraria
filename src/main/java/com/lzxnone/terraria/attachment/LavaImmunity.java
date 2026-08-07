package com.lzxnone.terraria.attachment;

//岩浆免疫计时（不序列化，客户端本地状态）
public class LavaImmunity {
    private int maxTicks;
    private int currentTicks;

    public LavaImmunity() {
        this(0, 0);
    }

    public LavaImmunity(int maxTicks, int currentTicks) {
        this.maxTicks = maxTicks;
        this.currentTicks = currentTicks;
    }

    public int getMaxTicks() {
        return maxTicks;
    }

    public void setMaxTicks(int maxTicks) {
        this.maxTicks = maxTicks;
    }

    public int getCurrentTicks() {
        return currentTicks;
    }

    public void setCurrentTicks(int currentTicks) {
        this.currentTicks = currentTicks;
    }
}
