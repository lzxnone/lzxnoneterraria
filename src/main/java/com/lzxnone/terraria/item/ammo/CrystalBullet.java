package com.lzxnone.terraria.item.ammo;

import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.IStaticProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.utils.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.*;
import org.joml.Vector3f;

import java.util.List;

public class CrystalBullet extends BasicBulletAmmo {
    public static final String ID = "crystal_bullet";
    public static final float BASE_DAMAGE_DEFAULT = 1.2f;
    public static final double SPEED_DEFAULT = 3.0D;

    public static final float FRAGMENT_DAMAGE_MUL_DEFAULT = 0.5f;
    public static final double FRAGMENT_SPEED_DEFAULT = 1.0;
    public static final int FRAGMENT_TIME_DEFAULT = 60;
    public static final int FRAGMENT_COUNT_DEFAULT = 3;
    public static final int PARTICLE_COUNT_DEFAULT = 5;

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            AmmoConfig.loadFloat(ID, "base_damage", BASE_DAMAGE_DEFAULT, 0.0f, 8388600.0f);
            AmmoConfig.loadDouble(ID, "speed", SPEED_DEFAULT, 0.0D, 24.0D);
            AmmoConfig.loadFloat(ID, "fragment_damage_multiplier", FRAGMENT_DAMAGE_MUL_DEFAULT, 0.0f, 100.0f);
            AmmoConfig.loadDouble(ID, "fragment_speed", FRAGMENT_SPEED_DEFAULT, 0.0D, 24.0D);
            AmmoConfig.loadInt(ID, "fragment_time", FRAGMENT_TIME_DEFAULT, 0, 72000);
            AmmoConfig.loadInt(ID, "fragment_count", FRAGMENT_COUNT_DEFAULT, 0, 100);
            AmmoConfig.loadInt(ID, "particle_count", PARTICLE_COUNT_DEFAULT, 0, 1000);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = AmmoConfig.createListItem(ID, CONFIG_DATA);

    public static float getBaseDamage() {
        return AmmoConfig.readFloat(ID, "base_damage", BASE_DAMAGE_DEFAULT, 0.0f, 8388600.0f);
    }

    public static double getSpeed() {
        return AmmoConfig.readDouble(ID, "speed", SPEED_DEFAULT, 0.0D, 24.0D);
    }

    public static float getFragmentDamageMultiplier() {
        return AmmoConfig.readFloat(ID, "fragment_damage_multiplier", FRAGMENT_DAMAGE_MUL_DEFAULT, 0.0f, 100.0f);
    }

    public static double getFragmentSpeed() {
        return AmmoConfig.readDouble(ID, "fragment_speed", FRAGMENT_SPEED_DEFAULT, 0.0D, 24.0D);
    }

    public static int getFragmentTime() {
        return AmmoConfig.readInt(ID, "fragment_time", FRAGMENT_TIME_DEFAULT, 0, 72000);
    }

    public static int getFragmentCount() {
        return AmmoConfig.readInt(ID, "fragment_count", FRAGMENT_COUNT_DEFAULT, 0, 100);
    }

    public static int getParticleCount() {
        return AmmoConfig.readInt(ID, "particle_count", PARTICLE_COUNT_DEFAULT, 0, 1000);
    }

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
                if(DamageUtil.rangedAttack(summon, target, sourceStack, getBaseDamage() + damage, knockbackScale)) {
                    target.invulnerableTime = invulnerableTime;
                    for(int i = 0;i < getFragmentCount();i++) {
                        summonProjectile(summon, (getBaseDamage() + damage) * getFragmentDamageMultiplier());
                    }
                    ParticleUtil.addParticles(
                        (ServerLevel) summon.level(), ModParticles.CRYSTAL_FRAGMENT_PARTICLE.get(),
                        summon.position(), new Vec3(0.2, 0.2, 0.2),
                        0.2, getParticleCount()
                    );
                    this.onDied(summon);
                }
            }

            //方块检测
            BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, summon.position().add(motion));
            if(blockHitResult.getType() != HitResult.Type.MISS) {
                CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
                float damage = customData.contains("damage") ? customData.getFloat("damage") : 0;
                for(int i = 0;i < getFragmentCount();i++) {
                    summonProjectile(summon, (getBaseDamage() + damage) * getFragmentDamageMultiplier());
                }
                ParticleUtil.addParticles(
                    (ServerLevel) summon.level(), ModParticles.CRYSTAL_FRAGMENT_PARTICLE.get(),
                    summon.position(), new Vec3(0.2, 0.2, 0.2),
                    0.2, getParticleCount()
                );
                this.onDied(summon);
            }
        }
    };

    public static final IStaticProjectileBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileBehavior() {
        @Override
        public void onHitEntity(StaticProjectile projectile, EntityHitResult result) {
            if(!projectile.level().isClientSide()) {
                Entity target = result.getEntity();
                Entity owner = projectile.getOwner();
                if(owner == null) return;
                if(!FilterUtil.createTargetFilter(owner).test(target) || !(owner instanceof Player player)) return;
                CompoundTag customData = projectile.getEntityData().get(StaticProjectile.CUSTOM_DATA);
                float damage = customData.contains("damage") ? customData.getFloat("damage") : 0;
                if(DamageUtil.rangedAttack(projectile, target, projectile.getEntityData().get(StaticProjectile.STACK_SOURCE), damage, 1.0f)) {
                    target.invulnerableTime = 5;
                    onDied(projectile);
                }
            }
        }

        @Override
        public void onHitBlock(StaticProjectile projectile, BlockHitResult result) {
            if(projectile.level().isClientSide()) return;
            if(!projectile.level().getBlockState(result.getBlockPos()).getCollisionShape(projectile.level(), result.getBlockPos()).isEmpty()) {
                onDied(projectile);
            }
        }
    };

    public static void summonProjectile(StaticSummon summon, float damage) {
        if(summon.level().isClientSide()) return;
        if(summon.getOwner() == null) return;
        Vec3 targetPos = MathUtil.getRandomPosOnRadius(summon.position(), 1);
        Vector3f[] dirs = MathUtil.computeCoordinateSystem(targetPos.subtract(summon.position()).toVector3f(), 0);

        StaticProjectile projectile = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), summon.level());
        projectile.setOwner(summon.getOwner());
        projectile.getEntityData().set(StaticProjectile.STACK_SOURCE, summon.getEntityData().get(StaticSummon.STACK_SOURCE).copy());
        projectile.setPos(summon.position());
        projectile.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.CRYSTAL_FRAGMENT);
        projectile.getEntityData().set(StaticProjectile.RENDER_MODE, "item");
        projectile.getEntityData().set(StaticProjectile.ORIGIN, summon.position().toVector3f());
        projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
        projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
        projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);
        projectile.getEntityData().set(StaticProjectile.ITEM, new ItemStack(ModItems.CRYSTAL_FRAGMENT.get()));
        projectile.getEntityData().set(StaticProjectile.SCALE_X, 0.5f);
        projectile.getEntityData().set(StaticProjectile.SCALE_Y, 0.5f);
        projectile.getEntityData().set(StaticProjectile.SCALE_Z, 0.5f);
        projectile.getEntityData().set(StaticProjectile.RXPS, 10);
        projectile.getEntityData().set(StaticProjectile.LIFETIME, getFragmentTime());
        projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*t", getFragmentSpeed()));

        CompoundTag customData = new CompoundTag();
        customData.putFloat("damage", damage);
        projectile.getEntityData().set(StaticProjectile.CUSTOM_DATA, customData);

        projectile.setDeltaMovement(MathUtil.toVec3(dirs[0]));
        summon.level().addFreshEntity(projectile);
    }
}
