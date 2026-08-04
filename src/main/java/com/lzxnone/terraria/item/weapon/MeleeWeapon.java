package com.lzxnone.terraria.item.weapon;

import com.lzxnone.terraria.item.accessory.AccessoryUtil;
import com.lzxnone.terraria.item.effect.MeleeDamageModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;

public class MeleeWeapon extends Weapon {
    public MeleeWeapon(Tier tier, Properties properties) {
        super(tier, properties);
    }

    public MeleeWeapon(Properties properties) {
        super(properties);
    }

    public static float applyMeleeDamageBonus(ItemStack stack, LivingEntity entity, float damage) {
        double finalDamage = damage;
        //饰品
        double[] multiplier = {1.0D};
        AccessoryUtil.forEachAccessory(entity, (accessory, accessoryStack) -> {
            if(accessory instanceof MeleeDamageModifier modifier) {
                multiplier[0] *= modifier.getMeleeDamageModifier(accessoryStack, entity);
            }
        });
        finalDamage *= multiplier[0];
        return (float)Math.max(0.0D, finalDamage);
    }
}
