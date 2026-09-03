package com.lzxnone.terraria.item.weapon;

import com.lzxnone.terraria.item.accessory.AccessoryUtil;
import com.lzxnone.terraria.item.effect.DamageModifier;
import com.lzxnone.terraria.item.ModItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

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

    public static float applyDamageBonus(ItemStack stack, LivingEntity entity, float damage) {
        double finalDamage = damage;
        //饰品
        double[] multiplier = {1.0D};
        AccessoryUtil.forEachAccessory(entity, (accessory, accessoryStack) -> {
            if(accessory instanceof DamageModifier modifier) {
                multiplier[0] *= modifier.getDamageMultiplier(accessoryStack, entity);
            }
        });
        finalDamage *= multiplier[0];
        return (float)Math.max(0.0D, finalDamage);
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

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(stack.is(ModItemTags.SPREAD_RANGED_WEAPONS)) {
            tooltipComponents.add(Component.translatable(
                "tooltip.lzxnoneterraria.spread_ranged_weapon"
            ).withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    public static int getEnchantmentLevel(LivingEntity entity, ItemStack stack, ResourceKey<Enchantment> enchantment) {
        if(entity == null) return 0;
        return entity.registryAccess()
            .lookupOrThrow(Registries.ENCHANTMENT)
            .get(enchantment)
            .map(stack::getEnchantmentLevel)
            .orElse(0);
    }
}
