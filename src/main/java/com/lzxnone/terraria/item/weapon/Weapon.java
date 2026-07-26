package com.lzxnone.terraria.item.weapon;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class Weapon extends Item {
    private final int enchantmentValue;

    public Weapon(Tier tier, Properties properties) {
        super(properties.durability(tier.getUses()));
        this.enchantmentValue = tier.getEnchantmentValue();
    }

    public Weapon(Properties properties) {
        super(properties);
        this.enchantmentValue = 0;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return false;
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
