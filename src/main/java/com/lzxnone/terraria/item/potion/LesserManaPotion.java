package com.lzxnone.terraria.item.potion;

public class LesserManaPotion extends AbstractManaPotion {
    public static final int RECOVER_AMOUNT = 50;

    @Override
    public int getRecoverAmount() {
        return RECOVER_AMOUNT;
    }
}
