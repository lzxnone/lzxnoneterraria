package com.lzxnone.terraria.item.weapon.ranged;

import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import org.joml.Vector3f;

public class Handgun extends BasicGun {
    public static final String ID = "handgun";
    public static final Vector3f OFFSET = new Vector3f(-0.3f, 0, 1.5f);
    public static final float DAMAGE_DEFAULT = 4.0f;
    public static final IConfigData CONFIG_DATA = createConfigData(ID, DAMAGE_DEFAULT);
    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);

    public Handgun() {
        super(Tiers.IRON, new Item.Properties().stacksTo(1), ID, OFFSET, DAMAGE_DEFAULT, 15, ModSounds.SHOT6, 0.25f, 10);
    }
}
