package com.lzxnone.terraria.item.potion;

public class GreaterManaPotion extends AbstractManaPotion {
    public static final int RECOVER_AMOUNT = 200;

    @Override
    public int getRecoverAmount() {
        return RECOVER_AMOUNT;
    }
}
