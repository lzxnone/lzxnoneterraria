package com.lzxnone.terraria.utils;

import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.weapon.RangedWeapon;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class AmmoUtil {
    public static final Vector3f OFFSET = new Vector3f(-0.3f, -0.15f, 1.5f);

    public static StaticSummon createAmmoSummon(Level level, Player player, InteractionHand hand, ItemStack stack) {
        Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
        Vec3 eyePos = player.getEyePosition();
        Vector3f offset = getOffset(hand);
        Vec3 pos = new Vec3(
            eyePos.x + dirs[0].x * offset.z + dirs[1].x * offset.y + dirs[2].x * offset.x,
            eyePos.y + dirs[0].y * offset.z + dirs[1].y * offset.y + dirs[2].y * offset.x,
            eyePos.z + dirs[0].z * offset.z + dirs[1].z * offset.y + dirs[2].z * offset.x
        );

        StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), level);
        summon.setOwner(player);
        summon.setPos(pos);
        summon.getEntityData().set(StaticSummon.STACK_SOURCE, stack.copy());
        summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");

        float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);
        summon.setXRot(xyRot[0]);
        summon.setYRot(xyRot[1]);
        summon.xRotO = xyRot[0];
        summon.yRotO = xyRot[1];

        String ammo = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getString(RangedWeapon.AMMO_KEY);
        if(ammo.equals("lzxnoneterraria:musket_ball")) setMusketBall(summon);
        else if(ammo.equals("lzxnoneterraria:silver_bullet")) setSilverBullet(summon);
        else if(ammo.equals("lzxnoneterraria:crystal_bullet")) setCrystalBullet(summon);
        else if(ammo.equals("lzxnoneterraria:cursed_bullet")) setCursedBullet(summon);
        else if(ammo.equals("lzxnoneterraria:chlorophyte_bullet")) setChlorophyteBullet(summon);
        else if(ammo.equals("lzxnoneterraria:high_velocity_bullet")) setHighVelocityBullet(summon);
        else if(ammo.equals("lzxnoneterraria:ichor_bullet")) setIchorBullet(summon);
        else if(ammo.equals("lzxnoneterraria:venom_bullet")) setVenomBullet(summon);
        else if(ammo.equals("lzxnoneterraria:party_bullet")) setPartyBullet(summon);
        else if(ammo.equals("lzxnoneterraria:nano_bullet")) setNanoBullet(summon);
        else if(ammo.equals("lzxnoneterraria:exploding_bullet")) setExplodingBullet(summon);
        else if(ammo.equals("lzxnoneterraria:golden_bullet")) setGoldenBullet(summon);
        else if(ammo.equals("lzxnoneterraria:endless_musket_pouch")) setMusketBall(summon);
        else if(ammo.equals("lzxnoneterraria:luminite_bullet")) setLuminiteBullet(summon);
        else if(ammo.equals("lzxnoneterraria:tungsten_bullet")) setTungstenBullet(summon);
        else if(ammo.equals("lzxnoneterraria:meteor_shot")) setMeteorShot(summon);
        return summon;
    }

    public static Vector3f getOffset(InteractionHand hand) {
        return new Vector3f(
            hand == InteractionHand.OFF_HAND ? -OFFSET.x : OFFSET.x,
            OFFSET.y,
            OFFSET.z
        );
    }

    public static void setMusketBall(StaticSummon summon) {
        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.MUSKET_BALL);
        summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.BULLET_PROJECTILE.get()));
        summon.getEntityData().set(StaticSummon.LIFETIME, 100);
        summon.getEntityData().set(StaticSummon.SCALE_X, 1.0f);
        summon.getEntityData().set(StaticSummon.SCALE_Y, 0.05f);
        summon.getEntityData().set(StaticSummon.SCALE_Z, 0.01f);
        summon.setNoGravity(true);
    }

    public static void setSilverBullet(StaticSummon summon) {
        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.SILVER_BULLET);
        summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.SILVER_BULLET_PROJECTILE.get()));
        summon.getEntityData().set(StaticSummon.LIFETIME, 100);
        summon.getEntityData().set(StaticSummon.SCALE_X, 1.0f);
        summon.getEntityData().set(StaticSummon.SCALE_Y, 0.05f);
        summon.getEntityData().set(StaticSummon.SCALE_Z, 0.01f);
        summon.setNoGravity(true);
    }

    public static void setCrystalBullet(StaticSummon summon) {
        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.CRYSTAL_BULLET);
        summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.CRYSTAL_BULLET_PROJECTILE.get()));
        summon.getEntityData().set(StaticSummon.LIFETIME, 100);
        summon.getEntityData().set(StaticSummon.SCALE_X, 1.0f);
        summon.getEntityData().set(StaticSummon.SCALE_Y, 0.05f);
        summon.getEntityData().set(StaticSummon.SCALE_Z, 0.01f);
        summon.setNoGravity(true);
    }

    public static void setCursedBullet(StaticSummon summon) {
        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.CURSED_BULLET);
        summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.CURSED_BULLET_PROJECTILE.get()));
        summon.getEntityData().set(StaticSummon.LIFETIME, 100);
        summon.getEntityData().set(StaticSummon.SCALE_X, 1.0f);
        summon.getEntityData().set(StaticSummon.SCALE_Y, 0.05f);
        summon.getEntityData().set(StaticSummon.SCALE_Z, 0.01f);
        summon.setNoGravity(true);
    }

    public static void setChlorophyteBullet(StaticSummon summon) {
        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.CHLOROPHYTE_BULLET);
        summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.CHLOROPHYTE_BULLET_PROJECTILE.get()));
        summon.getEntityData().set(StaticSummon.LIFETIME, 100);
        summon.getEntityData().set(StaticSummon.SCALE_X, 1.0f);
        summon.getEntityData().set(StaticSummon.SCALE_Y, 0.05f);
        summon.getEntityData().set(StaticSummon.SCALE_Z, 0.01f);
        summon.getEntityData().set(StaticSummon.GLOW, true);
        summon.setNoGravity(true);
    }

    public static void setHighVelocityBullet(StaticSummon summon) {
        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.HIGH_VELOCITY_BULLET);
        summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.HIGH_VELOCITY_BULLET_PROJECTILE.get()));
        summon.getEntityData().set(StaticSummon.LIFETIME, 100);
        summon.getEntityData().set(StaticSummon.SCALE_X, 1.0f);
        summon.getEntityData().set(StaticSummon.SCALE_Y, 0.05f);
        summon.getEntityData().set(StaticSummon.SCALE_Z, 0.01f);
        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
        customData.putInt("hit", 0);
        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
        summon.setNoGravity(true);
    }

    public static void setIchorBullet(StaticSummon summon) {
        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.ICHOR_BULLET);
        summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.ICHOR_BULLET_PROJECTILE.get()));
        summon.getEntityData().set(StaticSummon.LIFETIME, 100);
        summon.getEntityData().set(StaticSummon.SCALE_X, 1.0f);
        summon.getEntityData().set(StaticSummon.SCALE_Y, 0.05f);
        summon.getEntityData().set(StaticSummon.SCALE_Z, 0.01f);
        summon.getEntityData().set(StaticSummon.GLOW, true);
        summon.setNoGravity(true);
    }

    public static void setVenomBullet(StaticSummon summon) {
        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.VENOM_BULLET);
        summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.VENOM_BULLET_PROJECTILE.get()));
        summon.getEntityData().set(StaticSummon.LIFETIME, 100);
        summon.getEntityData().set(StaticSummon.SCALE_X, 1.0f);
        summon.getEntityData().set(StaticSummon.SCALE_Y, 0.05f);
        summon.getEntityData().set(StaticSummon.SCALE_Z, 0.01f);
        summon.setNoGravity(true);
    }

    public static void setPartyBullet(StaticSummon summon) {
        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.PARTY_BULLET);
        summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.PARTY_BULLET_PROJECTILE.get()));
        summon.getEntityData().set(StaticSummon.LIFETIME, 100);
        summon.getEntityData().set(StaticSummon.SCALE_X, 1.0f);
        summon.getEntityData().set(StaticSummon.SCALE_Y, 0.05f);
        summon.getEntityData().set(StaticSummon.SCALE_Z, 0.01f);
        summon.setNoGravity(true);
    }

    public static void setNanoBullet(StaticSummon summon) {
        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.NANO_BULLET);
        summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.NANO_BULLET_PROJECTILE.get()));
        summon.getEntityData().set(StaticSummon.LIFETIME, 100);
        summon.getEntityData().set(StaticSummon.SCALE_X, 1.0f);
        summon.getEntityData().set(StaticSummon.SCALE_Y, 0.05f);
        summon.getEntityData().set(StaticSummon.SCALE_Z, 0.01f);
        summon.getEntityData().set(StaticSummon.GLOW, true);
        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
        customData.putInt("hit", 0);
        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
        summon.setNoGravity(true);
    }

    public static void setExplodingBullet(StaticSummon summon) {
        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.EXPLODING_BULLET);
        summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.EXPLODING_BULLET_PROJECTILE.get()));
        summon.getEntityData().set(StaticSummon.LIFETIME, 100);
        summon.getEntityData().set(StaticSummon.SCALE_X, 1.0f);
        summon.getEntityData().set(StaticSummon.SCALE_Y, 0.05f);
        summon.getEntityData().set(StaticSummon.SCALE_Z, 0.01f);
        summon.setNoGravity(true);
    }

    public static void setGoldenBullet(StaticSummon summon) {
        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.GOLDEN_BULLET);
        summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.GOLDEN_BULLET_PROJECTILE.get()));
        summon.getEntityData().set(StaticSummon.LIFETIME, 100);
        summon.getEntityData().set(StaticSummon.SCALE_X, 1.0f);
        summon.getEntityData().set(StaticSummon.SCALE_Y, 0.05f);
        summon.getEntityData().set(StaticSummon.SCALE_Z, 0.01f);
        summon.setNoGravity(true);
    }

    public static void setLuminiteBullet(StaticSummon summon) {
        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.LUMINITE_BULLET);
        summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.LUMINITE_BULLET_PROJECTILE.get()));
        summon.getEntityData().set(StaticSummon.LIFETIME, 100);
        summon.getEntityData().set(StaticSummon.SCALE_X, 1.0f);
        summon.getEntityData().set(StaticSummon.SCALE_Y, 0.05f);
        summon.getEntityData().set(StaticSummon.SCALE_Z, 0.01f);
        summon.getEntityData().set(StaticSummon.GLOW, true);
        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
        customData.putInt("hit", 0);
        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
        summon.setNoGravity(true);
    }

    public static void setTungstenBullet(StaticSummon summon) {
        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.TUNGSTEN_BULLET);
        summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.BULLET_PROJECTILE.get()));
        summon.getEntityData().set(StaticSummon.LIFETIME, 100);
        summon.getEntityData().set(StaticSummon.SCALE_X, 1.0f);
        summon.getEntityData().set(StaticSummon.SCALE_Y, 0.05f);
        summon.getEntityData().set(StaticSummon.SCALE_Z, 0.01f);
        summon.setNoGravity(true);
    }

    public static void setMeteorShot(StaticSummon summon) {
        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.METEOR_SHOT);
        summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.METEOR_SHOT_PROJECTILE.get()));
        summon.getEntityData().set(StaticSummon.LIFETIME, 100);
        summon.getEntityData().set(StaticSummon.SCALE_X, 1.0f);
        summon.getEntityData().set(StaticSummon.SCALE_Y, 0.05f);
        summon.getEntityData().set(StaticSummon.SCALE_Z, 0.01f);
        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
        customData.putInt("hit", 0);
        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
        summon.setNoGravity(true);
    }
}
