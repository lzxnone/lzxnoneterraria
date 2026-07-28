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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class Boomstick extends BasicGun {
    public static final String ID = "boomstick";
    public static final Vector3f OFFSET = new Vector3f(-0.3f, -0.05f, 1.5f);
    public static final float DAMAGE_DEFAULT = 2.0f;
    public static final int PROJECTILE_COUNT = 3;
    public static final float MAX_SPREAD_DEGREES = 8.0f;
    public static final IConfigData CONFIG_DATA = createConfigData(ID, DAMAGE_DEFAULT);
    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);

    public Boomstick() {
        super(Tiers.IRON, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON), ID, OFFSET, DAMAGE_DEFAULT, 40, ModSounds.SHOT5, 1.0f, 10);
    }

    @Override
    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        SoundUtil.playClientSound(player, ModSounds.SHOT5.get());
        if(level.isClientSide()) return;

        for(int i = 0; i < PROJECTILE_COUNT; i++) {
            StaticSummon summon = AmmoUtil.createAmmoSummon(level, player, hand, stack, OFFSET);
            applySpread(player, summon);

            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            customData.putFloat("damage", getDamage());
            customData.putFloat("knockbackScale", 0.8f);
            customData.putInt("invulnerableTime", 10);
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
            level.addFreshEntity(summon);
        }
    }

    private static void applySpread(Player player, StaticSummon summon) {
        Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
        Vec3 forward = MathUtil.toVec3(dirs[0]).normalize();
        Vec3 up = MathUtil.toVec3(dirs[1]).normalize();
        Vec3 right = MathUtil.toVec3(dirs[2]).normalize();

        double yaw = Math.toRadians((player.getRandom().nextFloat() * 2.0f - 1.0f) * MAX_SPREAD_DEGREES);
        double pitch = Math.toRadians((player.getRandom().nextFloat() * 2.0f - 1.0f) * MAX_SPREAD_DEGREES);
        Vec3 spreadDir = forward
            .add(right.scale(Math.tan(yaw)))
            .add(up.scale(Math.tan(pitch)))
            .normalize();

        float[] xyRot = MathUtil.computeXYRot(spreadDir.toVector3f());
        summon.setXRot(xyRot[0]);
        summon.xRotO = xyRot[0];
        summon.setYRot(xyRot[1]);
        summon.yRotO = xyRot[1];
    }
}
