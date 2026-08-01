package com.lzxnone.terraria.item.weapon.summon.whip;

import com.lzxnone.terraria.block.ModBlocks;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.IStaticProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.ParticleUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Locale;

public class Possession extends Whip {
    private static final double RANGE = 32.0D;
    private static final float DAMAGE = 5.0F;
    private static final int ROTATE = 60;
    private static final double BEAM_SPEED = 1.5;
    private static final double BEAM_HEIGHT = 4.0D;
    private static final float BEAM_DAMAGE = 5.0f;

    public Possession() {
        super(Tiers.NETHERITE, new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.RARE));
    }

    @Override
    protected double getRange() {
        return RANGE;
    }

    @Override
    protected String getRes(StaticSummon summon) {
        return "lzxnoneterraria:textures/vfx/possession_projectile" + summon.getRandom().nextInt(3) + ".png";
    }

    @Override
    protected float getDamage() {
        return DAMAGE;
    }

    @Override
    protected int getLifetime() {
        return 10;
    }

    @Override
    protected int getRotateAngle() {
        return ROTATE;
    }

    @Override
    protected int getCooldown() {
        return 3;
    }

    @Override
    protected int getInvulnerableTime() {
        return 12;
    }

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.1f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(0.0F, 1.0F, 1.0F),
            new Vector3f(0.1F, 0.4F, 1.0F)
        }
    );

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            int age = summon.getEntityData().get(StaticSummon.AGE);
            int lifetime = summon.getEntityData().get(StaticSummon.LIFETIME);
            float progress = (float) age / lifetime;
            if(progress > 0.5f) {
                summon.getEntityData().set(StaticSummon.COLOR_A, Math.max(0, 1.0f - (progress - 0.5f) / 0.5f));
            }
            if(progress > 0.25f) {
                CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
                Vec3 move = new Vec3(
                        customData.contains("moveX") ? customData.getDouble("moveX") : 0,
                        customData.contains("moveY") ? customData.getDouble("moveY") : 0,
                        customData.contains("moveZ") ? customData.getDouble("moveZ") : 0
                );
                summon.setPos(summon.position().add(move.normalize().scale(0.25)));
            }
        }
    };

    public static final IStaticProjectileBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileBehavior() {
        @Override
        public void onMoving(StaticProjectile projectile) {
            Vector3f[] dirs = MathUtil.computeCoordinateSystem(projectile.getDeltaMovement().toVector3f(), 0);
            Vec3 right = MathUtil.toVec3(dirs[2]);
            projectile.trailPositions.addFirst(projectile.position().add(right.scale(0.1)));
            projectile.trailPositions.addFirst(projectile.position().add(right.scale(-0.1)));
            while(projectile.trailPositions.size() > 10) projectile.trailPositions.removeLast();
        }

        @Override
        public void onHitEntity(StaticProjectile projectile, EntityHitResult result) {
            if(!projectile.level().isClientSide()) {
                Entity target = result.getEntity();
                if(DamageUtil.meleeAttack(projectile, target, projectile.getEntityData().get(StaticProjectile.STACK_SOURCE), BEAM_DAMAGE, 0)) {
                    target.invulnerableTime = 5;
                }
            }
        }
    };

    @Override
    protected void onHitTarget(StaticSummon summon, Entity target) {
        if(summon.level() instanceof ServerLevel serverLevel) {
            Vec3 summonPos = MathUtil.getRandomPosOnRadius(summon.position(), 8);
            Vector3f[] dirs = MathUtil.computeCoordinateSystem(target.position().subtract(summonPos).toVector3f(), 0);
            float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);

            StaticSummon visionSummon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), serverLevel);
            visionSummon.setOwner(summon.getOwner());
            visionSummon.setPos(summonPos);
            visionSummon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.POSSESSION_VISION);
            visionSummon.getEntityData().set(StaticSummon.RENDER_MODE, "block");
            visionSummon.getEntityData().set(StaticSummon.BLOCK, ModBlocks.VISION.get().defaultBlockState());
            visionSummon.getEntityData().set(StaticSummon.LIFETIME, 40);
            visionSummon.getEntityData().set(StaticSummon.GLOW, true);
            visionSummon.setNoGravity(true);
            visionSummon.noPhysics = true;
            visionSummon.setXRot(xyRot[0]);
            visionSummon.setYRot(xyRot[1]);
            Vec3 randomPos = MathUtil.getRandomPosInRadius(visionSummon.position(), 1);
            Vec3 randomDir = randomPos.subtract(visionSummon.position()).normalize();
            CompoundTag customData = new CompoundTag();
            customData.putDouble("moveX", randomDir.x);
            customData.putDouble("moveY", randomDir.y);
            customData.putDouble("moveZ", randomDir.z);
            visionSummon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
            summon.level().addFreshEntity(visionSummon);

            double dist = summonPos.subtract(target.getBoundingBox().getCenter()).length();
            int lifetime = Math.max(1, (int)Math.ceil(dist / BEAM_SPEED));
            double speed = dist / lifetime;
            double height = Math.min(BEAM_HEIGHT, dist * 0.15D);

            StaticProjectile projectile = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), serverLevel);
            projectile.setOwner(summon.getOwner());
            projectile.setPos(summonPos);
            projectile.getEntityData().set(StaticProjectile.STACK_SOURCE, summon.getEntityData().get(StaticSummon.STACK_SOURCE).copy());
            projectile.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.POSSESSION_PROJECTILE);
            projectile.getEntityData().set(StaticProjectile.RENDER_MODE, "custom");
            projectile.getEntityData().set(StaticProjectile.ORIGIN, summonPos.toVector3f());
            projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
            projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
            projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);
            projectile.getEntityData().set(StaticProjectile.LIFETIME, lifetime);
            projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format(Locale.ROOT, "%.5f*t", speed));
            projectile.getEntityData().set(
                StaticProjectile.EXPRESSION_Y,
                String.format(Locale.ROOT, "%.5f*4*(t/%.5f)*(1-t/%.5f)", height, (double)lifetime, (double)lifetime)
            );
            projectile.getEntityData().set(StaticProjectile.GLOW, true);
            summon.level().addFreshEntity(projectile);

            ParticleUtil.addParticles(
                serverLevel, PARTICLE,
                target.getBoundingBox().getCenter(), new Vec3(0, 0, 0),
                0.1, 5
            );
        }
    }
}
