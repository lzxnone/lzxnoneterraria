package com.lzxnone.terraria.item.ammo;

import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.utils.CollisionUtil;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.MathUtil;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class NanoBullet extends BasicBulletAmmo {
    public static final String ID = "nano_bullet";
    public static final float BASE_DAMAGE_DEFAULT = 2.5f;
    public static final double SPEED_DEFAULT = 3.0D;
    public static final float DAMAGE_DECAY_DEFAULT = 0.67f;
    public static final int MAX_HIT_DEFAULT = 2;
    public static final int EFFECT_TIME_DEFAULT = 40;

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            AmmoConfig.loadFloat(ID, "base_damage", BASE_DAMAGE_DEFAULT, 0.0f, 8388600.0f);
            AmmoConfig.loadDouble(ID, "speed", SPEED_DEFAULT, 0.0D, 24.0D);
            AmmoConfig.loadFloat(ID, "damage_decay", DAMAGE_DECAY_DEFAULT, 0.0f, 1.0f);
            AmmoConfig.loadInt(ID, "max_hit", MAX_HIT_DEFAULT, 1, 100);
            AmmoConfig.loadInt(ID, "effect_time", EFFECT_TIME_DEFAULT, 0, 72000);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = AmmoConfig.createListItem(ID, CONFIG_DATA);

    public static float getBaseDamage() {
        return AmmoConfig.readFloat(ID, "base_damage", BASE_DAMAGE_DEFAULT, 0.0f, 8388600.0f);
    }

    public static double getSpeed() {
        return AmmoConfig.readDouble(ID, "speed", SPEED_DEFAULT, 0.0D, 24.0D);
    }

    public static float getDamageDecay() {
        return AmmoConfig.readFloat(ID, "damage_decay", DAMAGE_DECAY_DEFAULT, 0.0f, 1.0f);
    }

    public static int getMaxHit() {
        return AmmoConfig.readInt(ID, "max_hit", MAX_HIT_DEFAULT, 1, 100);
    }

    public static int getEffectTime() {
        return AmmoConfig.readInt(ID, "effect_time", EFFECT_TIME_DEFAULT, 0, 72000);
    }

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            if(summon.isRemoved()) return;

            double speed = getSpeed();
            Vec3 motion = summon.getDeltaMovement();
            if(motion.lengthSqr() < 0.0001) {
                motion = summon.getLookAngle().normalize().scale(speed);
            }else {
                motion = motion.normalize().scale(speed);
            }
            summon.setDeltaMovement(motion);

            //碰撞检测
            if(!summon.level().isClientSide()) {
                AABB hitBox = new AABB(summon.position(), summon.position().add(motion)).inflate(0.25);
                List<Entity> targets = summon.level().getEntitiesOfClass(
                        Entity.class,
                        hitBox,
                        FilterUtil.createTargetFilter(summon, summon.getOwner())
                );
                if(!targets.isEmpty()) {
                    CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
                    float damage = customData.contains("damage") ? customData.getFloat("damage") : 0;
                    float knockbackScale = customData.contains("knockbackScale") ? customData.getFloat("knockbackScale") : 1.0f;
                    int invulnerableTime = customData.contains("invulnerableTime") ? customData.getInt("invulnerableTime") : 20;

                    ItemStack sourceStack = summon.getEntityData().get(StaticSummon.STACK_SOURCE);
                    Entity target = targets.getFirst();
                    int hit = customData.getInt("hit");
                    if(DamageUtil.rangedAttack(summon, target, sourceStack, hit > 0 ? (getBaseDamage() + damage) * getDamageDecay() : getBaseDamage() + damage, knockbackScale)) {
                        target.invulnerableTime = invulnerableTime;
                        if(target instanceof LivingEntity livingEntity) {
                            MobEffectInstance effectInstance = new MobEffectInstance(ModEffects.CONFUSED, getEffectTime(), 0);
                            livingEntity.addEffect(effectInstance);
                        }
                        onDied(summon);
                    }
                }
            }

            //方块检测
            BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, summon.position().add(motion));
            if(blockHitResult.getType() != HitResult.Type.MISS) {
                if(incrementHit(summon) >= getMaxHit()) {
                    this.onDied(summon);
                    return;
                }

                Vec3 reflectedMotion = reflect(motion, blockHitResult.getDirection()).normalize().scale(speed);
                summon.setPos(blockHitResult.getLocation().add(Vec3.atLowerCornerOf(blockHitResult.getDirection().getNormal()).scale(0.05)));
                summon.setDeltaMovement(reflectedMotion);
                updateRotation(summon, reflectedMotion);
            }
        }
    };

    private static int incrementHit(StaticSummon summon) {
        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
        int hit = customData.getInt("hit") + 1;
        customData.putInt("hit", hit);
        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
        return hit;
    }

    private static Vec3 reflect(Vec3 motion, Direction hitDirection) {
        Vec3 normal = Vec3.atLowerCornerOf(hitDirection.getNormal());
        return motion.subtract(normal.scale(2.0D * motion.dot(normal)));
    }

    private static void updateRotation(StaticSummon summon, Vec3 motion) {
        float[] xyRot = MathUtil.computeXYRot(MathUtil.toVector3f(motion.normalize()));
        summon.setXRot(xyRot[0]);
        summon.xRotO = xyRot[0];
        summon.setYRot(xyRot[1]);
        summon.yRotO = xyRot[1];
    }
}
