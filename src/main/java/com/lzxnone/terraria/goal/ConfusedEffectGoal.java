package com.lzxnone.terraria.goal;

import com.lzxnone.terraria.effect.ModEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class ConfusedEffectGoal extends Goal {
    private final PathfinderMob mob;
    private Vec3 fleePos;

    public ConfusedEffectGoal(PathfinderMob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }
    @Override
    public boolean canUse() {
        return this.mob.hasEffect(ModEffects.CONFUSED) && this.mob.getTarget() != null;
    }

    @Override
    public void start() {
        this.updateFleePosition();
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse() && !this.mob.getNavigation().isDone();
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if(target == null) return;

        this.updateFleePosition();

        if(this.fleePos != null) {
            this.mob.getLookControl().setLookAt(this.fleePos.x, this.fleePos.y, this.fleePos.z);
        }
    }

    private void updateFleePosition() {
        LivingEntity target = this.mob.getTarget();
        if(target == null) return;

        //Vec3 pos = DefaultRandomPos.getPosAway(this.mob, 16, 7, target.position());
        //if(pos == null) {
            Vec3 revertDirection = this.mob.position().subtract(target.position()).normalize();
            Vec3 pos = this.mob.position().add(revertDirection.scale(5.0));
        //}
        this.fleePos = pos;
        this.mob.getNavigation().moveTo(this.fleePos.x, this.fleePos.y, this.fleePos.z, 1);
    }

    @Override
    public void stop() {
        this.fleePos = null;
        this.mob.getNavigation().stop();
    }
}
