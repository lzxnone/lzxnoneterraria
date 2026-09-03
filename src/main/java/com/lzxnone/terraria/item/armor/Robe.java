package com.lzxnone.terraria.item.armor;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

public class Robe extends ArmorItem {

    public Robe() {
        super(ModArmorMaterials.ROBE, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1));
    }
}
