package com.lzxnone.terraria.item.ammo;

import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.utils.CollisionUtil;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class HighVelocityBullet extends BulletAmmo {
    public static final String ID = "high_velocity_bullet";
    public static final float BASE_DAMAGE_DEFAULT = 1.5f;
    public static final double SPEED_DEFAULT = 4.0D;
    public static final float DAMAGE_DECAY_DEFAULT = 0.85f;
    public static final int MAX_HIT_DEFAULT = 3;

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            AmmoConfig.loadFloat(ID, "base_damage", BASE_DAMAGE_DEFAULT, 0.0f, 8388600.0f);
            AmmoConfig.loadDouble(ID, "speed", SPEED_DEFAULT, 0.0D, 24.0D);
            AmmoConfig.loadFloat(ID, "damage_decay", DAMAGE_DECAY_DEFAULT, 0.0f, 1.0f);
            AmmoConfig.loadInt(ID, "max_hit", MAX_HIT_DEFAULT, 1, 100);
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

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            Vec3 motion = summon.getLookAngle().normalize().scale(getSpeed());
            Vec3 start = summon.position();
            Vec3 end = start.add(motion);

            if(summon.level().isClientSide()) {
                summon.setPos(end);
                return;
            }

            //碰撞检测
            AABB hitBox = new AABB(start, end).inflate(0.25);
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

                for(Entity target : targets) {
                    int hit = customData.getInt("hit");
                    if(DamageUtil.rangedAttack(summon, target, sourceStack, (getBaseDamage() + damage) * (float) Math.pow(getDamageDecay(), hit), knockbackScale)) {
                        target.invulnerableTime = invulnerableTime;
                        if(incrementHit(summon) >= getMaxHit()) {
                            this.onDied(summon);
                            return;
                        }
                    }
                }
            }

            //方块检测
            BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, end);
            if(blockHitResult.getType() != HitResult.Type.MISS) {
                this.onDied(summon);
                return;
            }

            summon.setPos(end);
        }
    };

    private static int incrementHit(StaticSummon summon) {
        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
        int hit = customData.getInt("hit") + 1;
        customData.putInt("hit", hit);
        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
        return hit;
    }
}
