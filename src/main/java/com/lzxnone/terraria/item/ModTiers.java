package com.lzxnone.terraria.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public enum ModTiers implements Tier {
    COPPER(BlockTags.INCORRECT_FOR_IRON_TOOL, 200, 4.0F, 1.0F, 5, Ingredient.EMPTY),
    ENCHANTED_SWORD(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 500, 6.0F, 3.0F, 14, Ingredient.EMPTY);

    private final TagKey<Block> tag;
    private final int uses;
    private final float speed;
    private final float attackDamageBonus;
    private final int enchantmentValue;
    private final Ingredient repairIngredient;

    ModTiers(TagKey<Block> tag, int uses, float speed, float attackDamageBonus, int enchantmentValue, Ingredient repairIngredient) {
        this.tag = tag;
        this.uses = uses;
        this.speed = speed;
        this.attackDamageBonus = attackDamageBonus;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getUses() {
        return this.uses;
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return this.attackDamageBonus;
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return this.tag;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient;
    }
}
