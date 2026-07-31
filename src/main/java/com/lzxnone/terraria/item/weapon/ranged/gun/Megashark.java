package com.lzxnone.terraria.item.weapon.ranged.gun;

import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import org.joml.Vector3f;

public class Megashark extends Gun {
    public static final String ID = "megashark";
    public static final Vector3f OFFSET = new Vector3f(-0.3f, -0.15f, 1.5f);
    public static final float DAMAGE_DEFAULT = 3.0f;
    public static final float AMMO_NOT_CONSUME_CHANCE_DEFAULT = 0.5f;
    public static final IConfigData CONFIG_DATA = createConfigData(ID, DAMAGE_DEFAULT, AMMO_NOT_CONSUME_CHANCE_DEFAULT);
    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);

    public Megashark() {
        super(Tiers.DIAMOND, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON), ID, OFFSET, DAMAGE_DEFAULT, AMMO_NOT_CONSUME_CHANCE_DEFAULT, 7, ModSounds.SHOT2, 0.1f, 5);
    }
}
