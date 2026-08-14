package com.lzxnone.terraria.entity.summon;

import com.lzxnone.terraria.item.weapon.melee.BeeKeeper;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.MathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class BeeSummon extends Bee {
    public static final int LIFETIME = 1200;
    public static final int MAX_ATTACK_COUNT = 2;

    public static final double RANGE = 32.0;
    public static final double SPEED = 1;
    public static final double FRICTION = 0.25;

    public int customAge = 0;
    public int customAttackCount = 0;

    public LivingEntity owner = null;
    public ItemStack stackSource = ItemStack.EMPTY;
    public double damage = 0.0D;

    public BeeSummon(EntityType<? extends Bee> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
        //this.moveControl = new FlyingMoveControl(this, 45, true);
        //this.lookControl = new LookControl(this);
        //this.navigation = createNavigation(level);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Bee.createAttributes()
            .add(Attributes.MAX_HEALTH, MAX_ATTACK_COUNT)
            .add(Attributes.SCALE, 0.5)
            .add(Attributes.ATTACK_DAMAGE, 2.0)
            .add(Attributes.FOLLOW_RANGE, 32);
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.level().isClientSide()) {
            if(owner == null) {
                discard();
                return;
            }
            this.customAge++;
            if(this.customAge >= LIFETIME) {
                this.discard();
            }

            LivingEntity target = null;

            if(owner != null) {
                LivingEntity playerAttackTarget = owner.getLastHurtMob();
                if(playerAttackTarget != null && playerAttackTarget.isAlive() && this.distanceToSqr(playerAttackTarget) <= RANGE * RANGE) {
                    target = playerAttackTarget;
                }

                if(target == null) {
                    LivingEntity playerAttacker = owner.getLastHurtByMob();
                    if(playerAttacker != null && playerAttacker.isAlive() && this.distanceToSqr(playerAttacker) <= RANGE * RANGE) {
                        target = playerAttacker;
                    }
                }
            }

            if(target != null) {
                this.setTarget(target);
            }

            target = this.getTarget();
            if(target != null && target.isAlive()) {
                double dist = this.distanceToSqr(target);
                if(dist > 4) {
                    Vec3 targetPos = new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ());
                    this.setDeltaMovement(targetPos.subtract(this.position()).normalize().scale(SPEED));
                }else {
                    this.setDeltaMovement(this.getDeltaMovement().normalize().scale(FRICTION));
                    this.doHurtTarget(target);
                }
                Vector3f[] dirs = MathUtil.computeCoordinateSystem(this.getDeltaMovement().toVector3f(), this.getYRot());
                float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);
                this.setXRot(xyRot[0]);
                this.setYRot(xyRot[1]);
            }else {
                if(this.tickCount % 40 == 0) {
                    Vec3 targetPos = MathUtil.getRandomPosInRadius(this.position(), 16);
                    this.setDeltaMovement(targetPos.subtract(this.position()).normalize().scale(SPEED / 4));
                    Vector3f[] dirs = MathUtil.computeCoordinateSystem(this.getDeltaMovement().toVector3f(), this.getYRot());
                    float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);
                    this.setXRot(xyRot[0]);
                    this.setYRot(xyRot[1]);
                }
            }
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.removeAllGoals(goal -> true);
        this.targetSelector.removeAllGoals(goal -> true);
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, false, FilterUtil.createEnemyFilter(this)));
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if(owner == null) return false;
        boolean isHurt;
        if(owner instanceof Player) {
            isHurt = DamageUtil.meleeAttack(this, target, stackSource, (float) damage, 1.0f, -1);
        }else {
            DamageSource damageSource = this.damageSources().mobAttack(owner);
            isHurt = target.hurt(damageSource, (float) damage);
        }

        if(isHurt) {
            this.customAttackCount++;
            if(this.customAttackCount >= MAX_ATTACK_COUNT) {
                this.discard();
            }else {
                this.setHealth(this.getHealth() - 1);
            }
        }
        return isHurt;
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("customAttackCount", this.customAttackCount);
        tag.putInt("customAge", this.customAge);
        if(this.owner != null) tag.putUUID("owner", this.owner.getUUID());
        if(!this.stackSource.isEmpty()) tag.put("stackSource", this.stackSource.save(this.level().registryAccess()));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if(tag.contains("customAttackCount")) this.customAttackCount = tag.getInt("customAttackCount");
        if(tag.contains("customAge")) this.customAge = tag.getInt("customAge");
        if(tag.contains("owner") && this.level() instanceof ServerLevel level) {
            Entity entity = level.getEntity(tag.getUUID("owner"));
            if(entity instanceof LivingEntity livingEntity) this.owner = livingEntity;
        }
        if(tag.contains("stackSource")) {
            ItemStack.parse(this.level().registryAccess(), tag.getCompound("stackSource"))
                    .ifPresent(stack -> this.stackSource = stack);
        }
    }

}
