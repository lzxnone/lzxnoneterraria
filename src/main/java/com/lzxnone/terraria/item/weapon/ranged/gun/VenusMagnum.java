package com.lzxnone.terraria.item.weapon.ranged.gun;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.weapon.RangedWeapon;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;
public class VenusMagnum extends Gun {
    public static final String ID = "venus_magnum";
    public static final Vector3f OFFSET = new Vector3f(-0.3f, -0.05f, 1.5f);
    public static final float DAMAGE_DEFAULT = 5.0f;
    public static final ConfigFloat DAMAGE = createDamageConfig(ID, DAMAGE_DEFAULT);
    public static final IConfigData CONFIG_DATA = createConfigData(DAMAGE);
    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);
    public VenusMagnum() {
        super(Tiers.DIAMOND, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }
    @Override
    protected Vector3f getOffset(ItemStack stack, Player player) {
        return OFFSET;
    }
    @Override
    protected float getDamage(ItemStack stack, Player player) {
        return DAMAGE.get();
    }
    @Override
    public int getUseTime(ItemStack stack, LivingEntity entity) {
        return 9;
    }
    @Override
    protected SoundEvent getShootSound(ItemStack stack, Player player) {
        return ModSounds.SHOT6.get();
    }
    @Override
    protected float getKnockbackScale(ItemStack stack, Player player) {
        return 0.1f;
    }
    @Override
    protected int getInvulnerableTime(ItemStack stack, Player player) {
        return 5;
    }

    @Override
    protected ItemStack getBulletAmmoStack(Level level, Player player, InteractionHand hand, ItemStack weaponStack) {
        ItemStack ammoStack = RangedWeapon.getAmmoStack(weaponStack);
        if(ammoStack.is(ModItems.MUSKET_BALL.get()) || ammoStack.is(ModItems.TUNGSTEN_BULLET.get()) || ammoStack.is(ModItems.ENDLESS_MUSKET_POUCH.get())) {
            return ModItems.HIGH_VELOCITY_BULLET.get().getDefaultInstance();
        }
        return ammoStack;
    }
}

