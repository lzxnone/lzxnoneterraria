package com.lzxnone.terraria.item.ammo;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.weapon.ranged.bow.Bow;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.CollisionUtil;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.MathUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class WoodenArrow extends ArrowAmmo {
    public static final ConfigFloat BASE_DAMAGE = new ConfigFloat("ammo.wooden_arrow.base_damage", "wooden_arrow_base_damage", 2.0f, 0.0f, 8388600.0f);
    public static final ConfigDouble SPEED = new ConfigDouble("ammo.wooden_arrow.speed", "wooden_arrow_speed", 3.0D, 0.0D, 24.0D);
    public static final ConfigDouble GRAVITY = new ConfigDouble("ammo.wooden_arrow.gravity", "wooden_arrow_gravity", 0.05D, 0.0D, 10.0D);

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(BASE_DAMAGE, SPEED, GRAVITY);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "wooden_arrow",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/wooden_arrow.png"),
        Component.translatable("item.lzxnoneterraria.wooden_arrow"),
        CONFIG_DATA
    );

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            Vec3 motion = summon.getDeltaMovement();
            if(motion.lengthSqr() < 1.0E-7D) {
                motion = summon.getLookAngle().normalize().scale(SPEED.get());
            }
            motion = motion.add(0.0D, -GRAVITY.get(), 0.0D);

            Vec3 start = summon.position();
            Vec3 end = start.add(motion);

            updateRotation(summon, motion);

            if(summon.level().isClientSide()) {
                summon.setDeltaMovement(motion);
                return;
            }

            AABB hitBox = new AABB(start, end).inflate(0.25D);
            Level level = summon.level();
            List<Entity> targets = level.getEntitiesOfClass(
                Entity.class,
                hitBox,
                FilterUtil.createTargetFilter(summon, summon.getOwner())
            );
            if(!targets.isEmpty()) {
                float damage = customData.contains("damage") ? customData.getFloat("damage") : 0.0f;
                float knockbackScale = customData.contains("knockbackScale") ? customData.getFloat("knockbackScale") : 1.0f;
                int invulnerableTime = customData.contains("invulnerableTime") ? customData.getInt("invulnerableTime") : 20;

                ItemStack sourceStack = summon.getEntityData().get(StaticSummon.STACK_SOURCE);
                Entity target = targets.getFirst();
                if(DamageUtil.rangedAttack(summon, target, sourceStack, BASE_DAMAGE.get() + damage, knockbackScale)) {
                    if(customData.contains(StaticSummon.HIT_TARGET_IGNITE_TICKS_KEY)) {
                        target.igniteForTicks(customData.getInt(StaticSummon.HIT_TARGET_IGNITE_TICKS_KEY));
                    }
                    target.invulnerableTime = invulnerableTime;
                    this.onDied(summon);
                    return;
                }
            }

            BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, end);
            if(blockHitResult.getType() != HitResult.Type.MISS) {
                if(summon.level() instanceof ServerLevel serverLevel) Ammo.playBlockHitEffects(serverLevel, blockHitResult);
                this.onDied(summon);
                return;
            }

            summon.setDeltaMovement(motion);
        }
    };

    public static void updateRotation(StaticSummon summon, Vec3 direction) {
        if(direction.lengthSqr() < 1.0E-7D) return;
        float[] xyRot = MathUtil.computeXYRot(direction.toVector3f());
        summon.setXRot(xyRot[0]);
        summon.setYRot(xyRot[1]);
    }
    @Override
    public float getBaseDamage(ItemStack stack) {
        return BASE_DAMAGE.get();
    }

}
