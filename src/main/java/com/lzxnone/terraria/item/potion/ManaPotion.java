package com.lzxnone.terraria.item.potion;

public class ManaPotion extends AbstractManaPotion {
    public static final int RECOVER_AMOUNT = 100;

    @Override
    public int getRecoverAmount() {
        return RECOVER_AMOUNT;
    }
}
