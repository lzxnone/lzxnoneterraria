package com.lzxnone.terraria.entity.summon;

import net.minecraft.world.phys.AABB;

public interface IStaticSummonBehavior {
    default void checkBeforeTick(StaticSummon summon) {
        if(summon.level().isClientSide()) return;
        if(summon.getOwner() == null || !summon.getOwner().isAlive()) {
            onDied(summon);
            return;
        }
        if(summon.getEntityData().get(StaticSummon.AGE) > summon.getEntityData().get(StaticSummon.LIFETIME)) {
            onDied(summon);
        }else {
            summon.getEntityData().set(StaticSummon.AGE, summon.getEntityData().get(StaticSummon.AGE) + 1);
        }
    }

    default void tick(StaticSummon summon) {
        checkBeforeTick(summon);
    }

    default void onDied(StaticSummon summon) {
        if(!summon.level().isClientSide()) summon.discard();
    }

    default AABB getBoundingBoxForCulling(StaticSummon summon) {
        return summon.getBoundingBox();
    }
}
