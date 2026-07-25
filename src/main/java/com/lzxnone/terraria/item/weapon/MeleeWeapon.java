package com.lzxnone.terraria.item.weapon;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;

public class MeleeWeapon extends Item {
    private final int enchantmentValue;

    public MeleeWeapon(Tier tier, Properties properties) {
        super(properties.durability(tier.getUses()));
        this.enchantmentValue = tier.getEnchantmentValue();
    }

    public MeleeWeapon(Properties properties) {
        super(properties);
        this.enchantmentValue = 0;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getMaxStackSize() == 1;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return enchantmentValue;
    }
}
