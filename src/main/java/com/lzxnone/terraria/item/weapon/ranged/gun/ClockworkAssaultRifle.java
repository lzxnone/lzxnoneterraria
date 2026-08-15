package com.lzxnone.terraria.item.weapon.ranged.gun;

import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.summon.StaticSummon;
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

public class ClockworkAssaultRifle extends Gun {
    public static final String ID = "clockwork_assault_rifle";
    public static final Vector3f OFFSET = new Vector3f(-0.3f, -0.15f, 1.5f);
    public static final float DAMAGE_DEFAULT = 2.4f;
    public static final ConfigFloat DAMAGE = createDamageConfig(ID, DAMAGE_DEFAULT);
    public static final int PROJECTILE_COUNT = 3;
    public static final double PROJECTILE_OFFSET = -1.0;
    public static final IConfigData CONFIG_DATA = createConfigData(DAMAGE);
    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);

    public ClockworkAssaultRifle() {
        super(Tiers.DIAMOND, new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
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
        return 24;
    }

    @Override
    protected SoundEvent getShootSound(ItemStack stack, Player player) {
        return ModSounds.SHOT7.get();
    }

    @Override
    protected float getKnockbackScale(ItemStack stack, Player player) {
        return 0.2f;
    }

    @Override
    protected int getInvulnerableTime(ItemStack stack, Player player) {
        return 10;
    }

    @Override
    protected int getProjectileCount(ItemStack stack, Player player) {
        return PROJECTILE_COUNT;
    }

    @Override
    protected void postprocessBulletSummon(Level level, Player player, InteractionHand hand, ItemStack weaponStack,
                                           StaticSummon summon, int index) {
        summon.setPos(summon.position().add(player.getLookAngle().normalize().scale(PROJECTILE_OFFSET * index)));
    }
}
