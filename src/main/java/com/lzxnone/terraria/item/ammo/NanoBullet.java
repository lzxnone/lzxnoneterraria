package com.lzxnone.terraria.item.ammo;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.CollisionUtil;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.MathUtil;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class NanoBullet extends BulletAmmo {
    public static final String ID = "nano_bullet";

    public static final ConfigFloat BASE_DAMAGE = new ConfigFloat("ammo.nano_bullet.base_damage", "nano_bullet_base_damage", 2.5f, 0.0f, 8388600.0f);
    public static final ConfigDouble SPEED = new ConfigDouble("ammo.nano_bullet.speed", "nano_bullet_speed", 3.0D, 0.0D, 24.0D);
    public static final ConfigFloat DAMAGE_DECAY = new ConfigFloat("ammo.nano_bullet.damage_decay", "nano_bullet_damage_decay", 0.67f, 0.0f, 1.0f);
    public static final ConfigInt MAX_HIT = new ConfigInt("ammo.nano_bullet.max_hit", "nano_bullet_max_hit", 2, 1, 100);
    public static final ConfigInt EFFECT_TIME = new ConfigInt("ammo.nano_bullet.effect_time", "nano_bullet_effect_time", 40, 0, 72000);

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(BASE_DAMAGE, SPEED, DAMAGE_DECAY, MAX_HIT, EFFECT_TIME);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        ID,
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/" + ID + ".png"),
        Component.translatable("item.lzxnoneterraria." + ID),
        CONFIG_DATA
    );

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            if(summon.isRemoved()) return;

            double speed = SPEED.get();
            Vec3 motion = summon.getLookAngle().normalize().scale(speed);
            Vec3 start = summon.position();
            Vec3 end = start.add(motion);

            if(summon.level().isClientSide()) {
                summon.setPos(end);
                return;
            }

            //碰撞检测
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
                Entity target = targets.getFirst();
                int hit = customData.getInt("hit");
                if(DamageUtil.rangedAttack(summon, target, sourceStack, hit > 0 ? (BASE_DAMAGE.get() + damage) * DAMAGE_DECAY.get() : BASE_DAMAGE.get() + damage, knockbackScale, invulnerableTime)) {
                    if(target instanceof LivingEntity livingEntity) {
                        MobEffectInstance effectInstance = new MobEffectInstance(ModEffects.CONFUSED, EFFECT_TIME.get(), 0);
                        livingEntity.addEffect(effectInstance);
                    }
                    onDied(summon);
                    return;
                }
            }

            //方块检测
            BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, end);
            if(blockHitResult.getType() != HitResult.Type.MISS) {
                if(summon.level() instanceof ServerLevel serverLevel) Ammo.playBlockHitEffects(serverLevel, blockHitResult);
                if(incrementHit(summon) >= MAX_HIT.get()) {
                    this.onDied(summon);
                    return;
                }

                Vec3 reflectedMotion = reflect(motion, blockHitResult.getDirection()).normalize().scale(speed);
                summon.setPos(blockHitResult.getLocation().add(Vec3.atLowerCornerOf(blockHitResult.getDirection().getNormal()).scale(0.05)));
                updateRotation(summon, reflectedMotion);
                return;
            }

            summon.setPos(end);
        }
    };

    private static int incrementHit(StaticSummon summon) {
        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
        int hit = customData.getInt("hit") + 1;
        customData.putInt("hit", hit);
        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
        return hit;
    }

    private static Vec3 reflect(Vec3 motion, Direction hitDirection) {
        Vec3 normal = Vec3.atLowerCornerOf(hitDirection.getNormal());
        return motion.subtract(normal.scale(2.0D * motion.dot(normal)));
    }

    private static void updateRotation(StaticSummon summon, Vec3 motion) {
        float[] xyRot = MathUtil.computeXYRot(MathUtil.toVector3f(motion.normalize()));
        summon.setXRot(xyRot[0]);
        summon.xRotO = xyRot[0];
        summon.setYRot(xyRot[1]);
        summon.yRotO = xyRot[1];
    }
    @Override
    public float getBaseDamage(ItemStack stack) {
        return BASE_DAMAGE.get();
    }

}
