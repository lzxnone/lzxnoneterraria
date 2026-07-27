package com.lzxnone.terraria.item.weapon.ranged;

import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.ModItemTags;
import com.lzxnone.terraria.item.weapon.RangedWeapon;
import com.lzxnone.terraria.utils.AmmoUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;

public class SDMG extends RangedWeapon {
    public SDMG() {
        super(Tiers.NETHERITE, new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(!tryShoot(level, player, stack)) return InteractionResultHolder.fail(stack);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public boolean canUseAmmo(ItemStack weaponStack, ItemStack ammoStack) {
        return ammoStack.is(ModItemTags.BULLET_AMMO);
    }

    @Override
    public int getUseTime(ItemStack weaponStack, LivingEntity entity) {
        return 5;
    }

    @Override
    protected void shoot(Level level, Player player, ItemStack stack) {
        SoundUtil.playClientSound(player, ModSounds.SHOT.get());
        if(!level.isClientSide()) {
            StaticSummon summon = AmmoUtil.createAmmoSummon(level, player, stack);
            level.addFreshEntity(summon);
        }
    }
}
