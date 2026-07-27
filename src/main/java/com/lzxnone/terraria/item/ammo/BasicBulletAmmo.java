package com.lzxnone.terraria.item.ammo;

import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.utils.CollisionUtil;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public abstract class BasicBulletAmmo extends BulletAmmo {
    public static IStaticSummonBehavior createSummonBehavior(float baseDamage, double speed) {
        return new IStaticSummonBehavior() {
            @Override
            public void tick(StaticSummon summon) {
                this.checkBeforeTick(summon);
                Vec3 motion = summon.getLookAngle().normalize().scale(speed);
                summon.setDeltaMovement(motion);

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
                    if(DamageUtil.rangedAttack(summon, target, sourceStack, baseDamage + damage, knockbackScale)) {
                        target.invulnerableTime = invulnerableTime;
                        this.onDied(summon);
                    }
                }

                BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, summon.position().add(motion));
                if(blockHitResult.getType() != HitResult.Type.MISS) {
                    this.onDied(summon);
                }
            }
        };
    }
}
