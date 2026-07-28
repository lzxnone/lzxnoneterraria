package com.lzxnone.terraria.item.ammo;

import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.utils.CollisionUtil;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.ParticleUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class LuminiteBullet extends BulletAmmo {
    public static final String ID = "luminite_bullet";
    public static final float BASE_DAMAGE_DEFAULT = 3.0f;
    public static final double SPEED_DEFAULT = 3.5D;
    public static final float DAMAGE_DECAY_DEFAULT = 0.96f;

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            AmmoConfig.loadFloat(ID, "base_damage", BASE_DAMAGE_DEFAULT, 0.0f, 8388600.0f);
            AmmoConfig.loadDouble(ID, "speed", SPEED_DEFAULT, 0.0D, 24.0D);
            AmmoConfig.loadFloat(ID, "damage_decay", DAMAGE_DECAY_DEFAULT, 0.0f, 1.0f);
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

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.025f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(0.0F, 1.0F, 1.0F),
            new Vector3f(0.1F, 0.4F, 1.0F)
        }
    );

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
                        hit++;
                        customData.putInt("hit", hit);
                        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
                        ParticleUtil.addParticles(
                            (ServerLevel) summon.level(), PARTICLE,
                            end, new Vec3(0, 0, 0),
                            0.2, 5
                        );
                    }
                }
            }

            //方块检测
            BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, end);
            if(blockHitResult.getType() != HitResult.Type.MISS) {
                summon.setPos(blockHitResult.getLocation());
                ParticleUtil.addParticles(
                    (ServerLevel) summon.level(), PARTICLE,
                    summon.position(), new Vec3(0, 0, 0),
                    0.2, 10
                );
                this.onDied(summon);
                return;
            }

            summon.setPos(end);
        }
    };
}
