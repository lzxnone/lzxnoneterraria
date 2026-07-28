package com.lzxnone.terraria.item.weapon.ranged;

import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import org.joml.Vector3f;

public class Musket extends BasicGun {
    public static final String ID = "musket";
    public static final Vector3f OFFSET = new Vector3f(-0.3f, -0.1f, 1.5f);
    public static final float DAMAGE_DEFAULT = 3.5f;
    public static final IConfigData CONFIG_DATA = createConfigData(ID, DAMAGE_DEFAULT);
    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);

    public Musket() {
        super(Tiers.IRON, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON), ID, OFFSET, DAMAGE_DEFAULT, 32, ModSounds.SHOT3, 1.0f, 10);
    }

    @Override
    public boolean canUseAmmo(ItemStack weaponStack, ItemStack ammoStack) {
        return ammoStack.is(ModItems.MUSKET_BALL.get()) || ammoStack.is(ModItems.ENDLESS_MUSKET_POUCH.get());
    }
}
