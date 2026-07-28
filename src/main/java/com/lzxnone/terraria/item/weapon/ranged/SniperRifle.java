package com.lzxnone.terraria.item.weapon.ranged;

import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.weapon.RangedWeapon;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.utils.AmmoUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public class SniperRifle extends BasicGun {
    public static final String ID = "sniper_rifle";
    public static final Vector3f OFFSET = new Vector3f(-0.3f, -0.1f, 1.5f);
    public static final float DAMAGE_DEFAULT = 35.0f;
    public static final IConfigData CONFIG_DATA = createConfigData(ID, DAMAGE_DEFAULT);
    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);

    public SniperRifle() {
        super(Tiers.DIAMOND, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC), ID, OFFSET, DAMAGE_DEFAULT, 36, ModSounds.SHOT, 2.0f, 10);
    }

    @Override
    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        SoundUtil.playClientSound(player, ModSounds.SHOT.get());
        if(level.isClientSide()) return;

        StaticSummon summon = AmmoUtil.createAmmoSummon(level, player, hand, stack, OFFSET);
        ItemStack ammoStack = RangedWeapon.getAmmoStack(stack);
        if(ammoStack.is(ModItems.MUSKET_BALL.get()) || ammoStack.is(ModItems.TUNGSTEN_BULLET.get())) {
            AmmoUtil.setHighVelocityBullet(summon);
        }

        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
        customData.putFloat("damage", getDamage());
        customData.putFloat("knockbackScale", 2.0f);
        customData.putInt("invulnerableTime", 10);
        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
        level.addFreshEntity(summon);
    }
}
