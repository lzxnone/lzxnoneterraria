package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.item.weapon.MeleeWeapon;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.ItemStack;

public class CopperShortsword extends MeleeWeapon {
    public static final String ID = "copper_shortsword";
    public static final ConfigFloat BASE_MELEE_DAMAGE = createBaseMeleeDamageConfig(ID, 2F);
    public static final ConfigFloat BASE_MELEE_ATTACK_SPEED = createBaseMeleeAttackSpeedConfig(ID, -1F);

    public static final IConfigData CONFIG_DATA = createConfigData(BASE_MELEE_DAMAGE, BASE_MELEE_ATTACK_SPEED);
    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);
    public CopperShortsword() {
        super(Tiers.STONE, new Item.Properties());
    }

    @Override
    protected float getBaseMeleeDamage(ItemStack stack) {
        return BASE_MELEE_DAMAGE.get();
    }

    @Override
    protected float getBaseMeleeAttackSpeed(ItemStack stack) {
        return BASE_MELEE_ATTACK_SPEED.get();
    }
}
