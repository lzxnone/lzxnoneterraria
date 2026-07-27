package com.lzxnone.terraria.item.ammo;

import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.utils.*;
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

public class ExplodingBullet extends BasicBulletAmmo {
    public static final String ID = "exploding_bullet";
    public static final float BASE_DAMAGE_DEFAULT = 1.5f;
    public static final double SPEED_DEFAULT = 3.0D;
    public static final float KNOCKBACK_MULTIPLIER_DEFAULT = 2.0f;
    public static final int DUST_COUNT_DEFAULT = 10;

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            AmmoConfig.loadFloat(ID, "base_damage", BASE_DAMAGE_DEFAULT, 0.0f, 8388600.0f);
            AmmoConfig.loadDouble(ID, "speed", SPEED_DEFAULT, 0.0D, 24.0D);
            AmmoConfig.loadFloat(ID, "knockback_multiplier", KNOCKBACK_MULTIPLIER_DEFAULT, 0.0f, 100.0f);
            AmmoConfig.loadInt(ID, "dust_count", DUST_COUNT_DEFAULT, 0, 1000);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = AmmoConfig.createListItem(ID, CONFIG_DATA);

    public static float getBaseDamage() {
        return AmmoConfig.readFloat(ID, "base_damage", BASE_DAMAGE_DEFAULT, 0.0f, 8388600.0f);
    }

    public static double getSpeed() {
        return AmmoConfig.readDouble(ID, "speed", SPEED_DEFAULT, 0.0D, 24.0D);
    }

    public static float getKnockbackMultiplier() {
        return AmmoConfig.readFloat(ID, "knockback_multiplier", KNOCKBACK_MULTIPLIER_DEFAULT, 0.0f, 100.0f);
    }

    public static int getDustCount() {
        return AmmoConfig.readInt(ID, "dust_count", DUST_COUNT_DEFAULT, 0, 1000);
    }

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.075f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(1.0F, 0.8F, 0.5F),
            new Vector3f(1.0F, 0.5F, 0.0F),
            new Vector3f(1.0F, 0.9F, 0.0F),
        }
    );

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            Vec3 motion = summon.getLookAngle().normalize().scale(getSpeed());
            summon.setDeltaMovement(motion);

            if(summon.level().isClientSide()) return;

            //碰撞检测
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
                if(DamageUtil.rangedAttack(summon, target, sourceStack, getBaseDamage() + damage, knockbackScale * getKnockbackMultiplier())) {
                    target.invulnerableTime = invulnerableTime;
                    SoundUtil.playServerSound(summon.level(), ModSounds.BOOM.get(), summon.position());
                    ParticleUtil.addParticles(
                        (ServerLevel) summon.level(), ModParticles.EXPLODE_PARTICLE.get(),
                        summon.position(), new Vec3(0, 0, 0),
                        0.2, 1
                    );
                    ParticleUtil.addParticles(
                        (ServerLevel) summon.level(), PARTICLE,
                        summon.position(), new Vec3(0, 0, 0),
                        0.2, getDustCount()
                    );
                    this.onDied(summon);
                }
            }

            //方块检测
            BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, summon.position().add(motion));
            if(blockHitResult.getType() != HitResult.Type.MISS) {
                SoundUtil.playServerSound(summon.level(), ModSounds.BOOM.get(), summon.position());
                ParticleUtil.addParticles(
                    (ServerLevel) summon.level(), ModParticles.EXPLODE_PARTICLE.get(),
                    summon.position(), new Vec3(0, 0, 0),
                    0.2, 1
                );
                ParticleUtil.addParticles(
                    (ServerLevel) summon.level(), PARTICLE,
                    summon.position(), new Vec3(0, 0, 0),
                    0.2, getDustCount()
                );
                this.onDied(summon);
            }
        }
    };
}
