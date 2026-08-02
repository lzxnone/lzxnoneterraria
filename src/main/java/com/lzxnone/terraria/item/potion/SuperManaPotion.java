package com.lzxnone.terraria.item.potion;

public class SuperManaPotion extends AbstractManaPotion {
    public static final int RECOVER_AMOUNT = 300;

    @Override
    public int getRecoverAmount() {
        return RECOVER_AMOUNT;
    }
}
