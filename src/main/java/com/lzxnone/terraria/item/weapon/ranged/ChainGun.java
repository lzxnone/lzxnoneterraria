package com.lzxnone.terraria.item.weapon.ranged;

import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.utils.AmmoUtil;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class ChainGun extends BasicGun {
    public static final String ID = "chain_gun";
    public static final Vector3f OFFSET = new Vector3f(-0.3f, -0.1f, 1.5f);
    public static final float DAMAGE_DEFAULT = 6.0f;
    public static final float AMMO_NOT_CONSUME_CHANCE = 0.67f;
    public static final float MAX_SPREAD_DEGREES = 15.0f;
    public static final IConfigData CONFIG_DATA = createConfigData(ID, DAMAGE_DEFAULT);
    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);

    public ChainGun() {
        super(Tiers.NETHERITE, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), ID, OFFSET, DAMAGE_DEFAULT, 4, ModSounds.SHOT6, 0.1f, 4);
    }

    @Override
    public int getAmmoConsumeAmount(ItemStack weaponStack, LivingEntity entity) {
        return entity.getRandom().nextFloat() < AMMO_NOT_CONSUME_CHANCE ? 0 : 1;
    }

    @Override
    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        SoundUtil.playClientSound(player, ModSounds.SHOT6.get());
        if(level.isClientSide()) return;

        StaticSummon summon = AmmoUtil.createAmmoSummon(level, player, hand, stack, OFFSET);
        Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
        Vec3 forward = MathUtil.toVec3(dirs[0]).normalize();
        Vec3 up = MathUtil.toVec3(dirs[1]).normalize();
        Vec3 right = MathUtil.toVec3(dirs[2]).normalize();
        float spreadDegrees = getFinalSpreadDegrees(MAX_SPREAD_DEGREES, stack, player);
        double yaw = Math.toRadians((player.getRandom().nextFloat() * 2.0f - 1.0f) * spreadDegrees);
        double pitch = Math.toRadians((player.getRandom().nextFloat() * 2.0f - 1.0f) * spreadDegrees);
        Vec3 spreadDir = forward.add(right.scale(Math.tan(yaw))).add(up.scale(Math.tan(pitch))).normalize();
        float[] xyRot = MathUtil.computeXYRot(spreadDir.toVector3f());
        summon.setXRot(xyRot[0]);
        summon.xRotO = xyRot[0];
        summon.setYRot(xyRot[1]);
        summon.yRotO = xyRot[1];

        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
        customData.putFloat("damage", getDamage());
        customData.putFloat("knockbackScale", 0.1f);
        customData.putInt("invulnerableTime", 4);
        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
        level.addFreshEntity(summon);
    }
}
