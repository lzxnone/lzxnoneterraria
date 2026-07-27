package com.lzxnone.terraria.item.ammo;

import net.minecraft.world.item.Item;

public class BulletAmmo extends Item {
    public static final int MAX_STACK_SIZE = 99;

    public BulletAmmo() {
        super(new Item.Properties().stacksTo(MAX_STACK_SIZE));
    }
}
