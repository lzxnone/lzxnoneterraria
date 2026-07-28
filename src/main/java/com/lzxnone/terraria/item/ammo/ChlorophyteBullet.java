package com.lzxnone.terraria.item.ammo;

import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.utils.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class ChlorophyteBullet extends BulletAmmo {
    public static final String ID = "chlorophyte_bullet";
    public static final float BASE_DAMAGE_DEFAULT = 1.5f;
    public static final double SPEED_DEFAULT = 3.0D;
    public static final double TARGET_RANGE_DEFAULT = 24.0D;

    public static final double AVOID_CHECK_DISTANCE = 3.0D;
    public static final double TURN_RATE = 0.35D;
    public static final double CLOSE_TARGET_RANGE = 5.0D;
    public static final double CLOSE_TURN_RATE = 0.75D;
    public static final double TARGET_WEIGHT = 1.0D;
    public static final double AVOID_WEIGHT = 0.8D;

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            AmmoConfig.loadFloat(ID, "base_damage", BASE_DAMAGE_DEFAULT, 0.0f, 8388600.0f);
            AmmoConfig.loadDouble(ID, "speed", SPEED_DEFAULT, 0.0D, 24.0D);
            AmmoConfig.loadDouble(ID, "target_range", TARGET_RANGE_DEFAULT, 0.0D, 128.0D);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = AmmoConfig.createListItem(ID, CONFIG_DATA);

    public static float getBaseDamage() {
        return AmmoConfig.readFloat(ID, "base_damage", BASE_DAMAGE_DEFAULT, 0.0f, 8388600.0f);
    }

    public static double getSpeed() {
        return AmmoConfig.readDouble(ID, "speed", SPEED_DEFAULT, 0.0D, 24.0D);
    }

    public static double getTargetRange() {
        return AmmoConfig.readDouble(ID, "target_range", TARGET_RANGE_DEFAULT, 0.0D, 128.0D);
    }

    private static final double[][] CANDIDATE_OFFSETS = new double[][]{
        {0.0D, 0.0D},
        {0.7D, 0.0D},
        {-0.7D, 0.0D},
        {0.0D, 0.7D},
        {0.0D, -0.7D},
        {0.7D, 0.4D},
        {0.7D, -0.4D},
        {-0.7D, 0.4D},
        {-0.7D, -0.4D},
        {1.1D, 0.0D},
        {-1.1D, 0.0D},
        {0.0D, 1.1D},
        {0.0D, -1.1D}
    };

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.025f, 0f, 20, true, new Vector3f[]{
            new Vector3f(0.5F, 1.0F, 0.5F),
            new Vector3f(0.0F, 1.0F, 0.2F)
        }
    );

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);

            Vec3 currentDir = getCurrentDirection(summon);
            Vec3 oldMotion = currentDir.normalize().scale(getSpeed());

            Entity target = findTarget(summon);
            Vec3 nextDir = currentDir;
            if(target != null) {
                Vec3 targetCenter = target.getBoundingBox().getCenter();
                Vec3 targetDir = targetCenter.subtract(summon.position()).normalize();
                double turnRate = targetCenter.distanceTo(summon.position()) <= CLOSE_TARGET_RANGE ? CLOSE_TURN_RATE : TURN_RATE;
                if(hasLineOfSight(summon.level(), summon, summon.position(), targetCenter)) {
                    nextDir = steer(currentDir, targetDir, turnRate);
                }else {
                    nextDir = steer(currentDir, findBestClearDirection(summon, currentDir, targetDir), turnRate);
                }
            }else if(getClearDistance(summon.level(), summon, summon.position(), currentDir, AVOID_CHECK_DISTANCE) < AVOID_CHECK_DISTANCE) {
                nextDir = steer(currentDir, findBestClearDirection(summon, currentDir, currentDir));
            }

            Vec3 motion = nextDir.normalize().scale(getSpeed());
            Vec3 start = summon.position();
            Vec3 end = start.add(motion);

            updateRotation(summon, nextDir);

            if(summon.level().isClientSide()) {
                summon.setPos(end);
                summon.trailPositions.addFirst(summon.position());
                while(summon.trailPositions.size() > 2) summon.trailPositions.removeLast();
                if(summon.trailPositions.size() == 2) {
                    Vec3 pos1 = summon.trailPositions.getFirst();
                    Vec3 pos2 = summon.trailPositions.getLast();
                    Vec3 dir = pos1.subtract(pos2).normalize();
                    double length = pos1.subtract(pos2).length();
                    int count = 10;
                    double deltaLength = length / count;
                    for(int i = 0;i < count;i++) {
                        Vec3 pos = pos2.add(dir.scale(deltaLength * i));
                        ParticleUtil.addParticle(
                            summon.level(), PARTICLE,
                            pos, 0.0,
                            new Vec3(0, 0, 0), 0
                        );
                    }
                }
                return;
            }

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
                Entity hitTarget = targets.getFirst();
                if(DamageUtil.rangedAttack(summon, hitTarget, sourceStack, getBaseDamage() + damage, knockbackScale)) {
                    hitTarget.invulnerableTime = invulnerableTime;
                    this.onDied(summon);
                    return;
                }
            }
            BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, summon.position().add(oldMotion));
            if(blockHitResult.getType() != HitResult.Type.MISS) {
                this.onDied(summon);
                return;
            }

            summon.setPos(end);
        }
    };

    private static Entity findTarget(StaticSummon summon) {
        AABB searchBox = summon.getBoundingBox().inflate(getTargetRange());
        List<Monster> targets = summon.level().getEntitiesOfClass(
            Monster.class,
            searchBox,
            FilterUtil.createTargetFilter(summon, summon.getOwner())
        );
        Entity bestTarget = null;
        double bestDistance = Double.MAX_VALUE;
        for(Entity target : targets) {
            Vec3 targetCenter = target.getBoundingBox().getCenter();
            if(!hasLineOfSight(summon.level(), summon, summon.position(), targetCenter)) continue;

            double distance = targetCenter.distanceToSqr(summon.position());
            if(distance < bestDistance) {
                bestDistance = distance;
                bestTarget = target;
            }
        }
        return bestTarget;
    }

    private static Vec3 findBestClearDirection(StaticSummon summon, Vec3 currentDir, Vec3 targetDir) {
        Vector3f[] basis = MathUtil.computeCoordinateSystem(currentDir.toVector3f(), 0);
        Vec3 forward = MathUtil.toVec3(basis[0]).normalize();
        Vec3 up = MathUtil.toVec3(basis[1]).normalize();
        Vec3 right = MathUtil.toVec3(basis[2]).normalize();

        Vec3 bestDir = currentDir;
        double bestScore = -Double.MAX_VALUE;
        for(double[] offset : CANDIDATE_OFFSETS) {
            Vec3 candidate = forward.add(right.scale(offset[0])).add(up.scale(offset[1])).normalize();
            double clearDistance = getClearDistance(summon.level(), summon, summon.position(), candidate, AVOID_CHECK_DISTANCE);
            double clearRatio = clearDistance / AVOID_CHECK_DISTANCE;
            if(clearRatio < 0.25D) continue;

            double targetScore = candidate.dot(targetDir);
            double score = targetScore * TARGET_WEIGHT + clearRatio * AVOID_WEIGHT;
            if(score > bestScore) {
                bestScore = score;
                bestDir = candidate;
            }
        }
        return bestDir.normalize();
    }

    private static boolean hasLineOfSight(Level level, Entity clipEntity, Vec3 from, Vec3 to) {
        return level.clip(new ClipContext(
            from,
            to,
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            clipEntity
        )).getType() == HitResult.Type.MISS;
    }

    private static double getClearDistance(Level level, Entity clipEntity, Vec3 pos, Vec3 direction, double maxDistance) {
        BlockHitResult hitResult = level.clip(new ClipContext(
            pos,
            pos.add(direction.normalize().scale(maxDistance)),
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            clipEntity
        ));
        if(hitResult.getType() == HitResult.Type.MISS) return maxDistance;
        return Math.max(0.0D, hitResult.getLocation().distanceTo(pos));
    }

    private static Vec3 getCurrentDirection(StaticSummon summon) {
        Vec3 direction = summon.getDeltaMovement();
        if(direction.lengthSqr() < 1.0E-7D) {
            direction = summon.getLookAngle();
        }
        if(direction.lengthSqr() < 1.0E-7D) {
            return new Vec3(0.0D, 0.0D, 1.0D);
        }
        return direction.normalize();
    }

    private static Vec3 steer(Vec3 currentDir, Vec3 desiredDir) {
        return steer(currentDir, desiredDir, TURN_RATE);
    }

    private static Vec3 steer(Vec3 currentDir, Vec3 desiredDir, double turnRate) {
        Vec3 steered = currentDir.scale(1.0D - turnRate).add(desiredDir.normalize().scale(turnRate));
        if(steered.lengthSqr() < 1.0E-7D) return desiredDir.normalize();
        return steered.normalize();
    }

    private static void updateRotation(StaticSummon summon, Vec3 direction) {
        float[] xyRot = MathUtil.computeXYRot(direction.toVector3f());
        summon.setXRot(xyRot[0]);
        summon.setYRot(xyRot[1]);
        summon.xRotO = xyRot[0];
        summon.yRotO = xyRot[1];
    }
}
