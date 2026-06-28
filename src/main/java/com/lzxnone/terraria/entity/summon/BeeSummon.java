package com.lzxnone.terraria.entity.summon;

import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BeeSummon extends Bee {

    public static final int LIFETIME = 1200;
    public static final int MAX_ATTACK_COUNT = 2;
    public int customAge = 0;
    public int customAttackCount = 0;

    public UUID owner = null;

    public BeeSummon(EntityType<? extends Bee> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 45, true);
        this.lookControl = new LookControl(this);
        this.navigation = createNavigation(level);
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
            if(owner == null || this.level().getPlayerByUUID(owner) == null) {
                discard();
            }
            this.customAge++;
            if(this.customAge >= LIFETIME) {
                this.discard();
            }

            Player player = this.level().getPlayerByUUID(owner);
            LivingEntity target = null;

            if(player != null) {
                LivingEntity playerAttackTarget = player.getLastHurtMob();
                if(playerAttackTarget != null && playerAttackTarget.isAlive() && this.distanceToSqr(playerAttackTarget) <= 1024) {
                    target = playerAttackTarget;
                }

                if(target == null) {
                    LivingEntity playerAttacker = player.getLastHurtByMob();
                    if(playerAttacker != null && playerAttacker.isAlive() && this.distanceToSqr(playerAttacker) <= 1024) {
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
                if(dist > 256) {
                    flyTowards(target.getBoundingBox().getCenter(), 1, 0.5);
                }else if(dist > 64) {
                    flyTowards(target.getBoundingBox().getCenter(), 0.75, 0.5);
                }else if(dist > 4) {
                    flyTowards(target.getBoundingBox().getCenter(), 0.5, 0.5);
                }else {
                    this.doHurtTarget(target);
                }
            }else if(player != null) {
                Vec3 hoverPos = player.position().add(0, 1.5, 0);
                if(this.distanceToSqr(player) > 64) {
                    flyTowards(hoverPos, 0.75, 0.5);
                }else {
                    this.setDeltaMovement(this.getDeltaMovement().scale(0.8));
                }
            }
        }
    }

    private void flyTowards(Vec3 destination, double acceleration, double friction) {
        Vec3 myPos = this.getBoundingBox().getCenter();
        Vec3 dir = destination.subtract(myPos);

        if(dir.lengthSqr() > 0.05) {
            dir = dir.normalize().scale(acceleration);
            Vec3 newMovement = this.getDeltaMovement().add(dir).scale(friction);
            this.setDeltaMovement(newMovement);

            this.getLookControl().setLookAt(destination.x, destination.y, destination.z, 30.0F, 30.0F);
            this.setYRot((float)(Math.atan2(newMovement.z, newMovement.x) * (180F / Math.PI)) - 90.0F);
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.removeAllGoals(goal -> true);
        this.targetSelector.removeAllGoals(goal -> true);
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Monster.class, false));
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if(owner == null) return false;
        Player player = this.level().getPlayerByUUID(owner);
        if(player == null) return false;
        DamageSource damageSource = this.damageSources().mobAttack(player);
        boolean isHurt = target.hurt(damageSource, (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));

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
        return false;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("customAttackCount", this.customAttackCount);
        tag.putInt("customAge", this.customAge);
        if(this.owner != null) tag.putUUID("owner", this.owner);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if(tag.contains("customAttackCount")) this.customAttackCount = tag.getInt("customAttackCount");
        if(tag.contains("customAge")) this.customAge = tag.getInt("customAge");
        if(tag.contains("owner")) this.owner = tag.getUUID("owner");
    }

}
