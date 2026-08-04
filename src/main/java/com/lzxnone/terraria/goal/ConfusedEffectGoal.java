package com.lzxnone.terraria.goal;

import com.lzxnone.terraria.effect.ModEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class ConfusedEffectGoal extends Goal {
    private static final int FLEE_SEARCH_RADIUS = 16;
    private static final int FLEE_SEARCH_HORIZONTAL_RADIUS = 7;
    private static final double WALK_SPEED = 1.0D;
    private static final double SPRINT_SPEED = 1.2D;
    private static final double SPRINT_DISTANCE_SQ = 49.0D;

    private final PathfinderMob mob;
    private LivingEntity toAvoid;
    private Path fleePath;

    public ConfusedEffectGoal(PathfinderMob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        if(!this.mob.hasEffect(ModEffects.CONFUSED) || target == null || !target.isAlive()) {
            return false;
        }

        Vec3 fleePos = DefaultRandomPos.getPosAway(
            this.mob,
            FLEE_SEARCH_RADIUS,
            FLEE_SEARCH_HORIZONTAL_RADIUS,
            target.position()
        );
        if(fleePos == null) {
            return false;
        }

        if(target.distanceToSqr(fleePos) < target.distanceToSqr(this.mob)) {
            return false;
        }

        this.toAvoid = target;
        this.fleePath = this.mob.getNavigation().createPath(fleePos.x, fleePos.y, fleePos.z, 0);
        return this.fleePath != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.hasEffect(ModEffects.CONFUSED)
            && this.toAvoid != null
            && this.toAvoid.isAlive()
            && !this.mob.getNavigation().isDone();
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.fleePath, WALK_SPEED);
    }

    @Override
    public void stop() {
        this.toAvoid = null;
        this.fleePath = null;
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        if(this.toAvoid != null && this.mob.distanceToSqr(this.toAvoid) < SPRINT_DISTANCE_SQ) {
            this.mob.getNavigation().setSpeedModifier(SPRINT_SPEED);
        }else {
            this.mob.getNavigation().setSpeedModifier(WALK_SPEED);
        }
    }
}

