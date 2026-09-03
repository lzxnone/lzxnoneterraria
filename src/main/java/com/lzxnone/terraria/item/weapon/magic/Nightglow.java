package com.lzxnone.terraria.item.weapon.magic;

import com.lzxnone.terraria.effect.ManaSurgeEffect;

import com.lzxnone.terraria.effect.ModEffects;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.weapon.MagicWeapon;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.*;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class Nightglow extends MagicWeapon {
    public static final ConfigFloat DAMAGE = new ConfigFloat("weapon.nightglow.damage", "nightglow_damage", 5.0F, 0.0F, 8388600.0F);
    public static final ConfigDouble MANA_CONSUME = new ConfigDouble("weapon.nightglow.mana_consume", "nightglow_mana_consume", 23.0D, 0.0D, 10000.0D);
    public static final ConfigInt PROJECTILE_COUNT = new ConfigInt("weapon.nightglow.projectile_count", "nightglow_projectile_count", 4, 1, 64);
    public static final ConfigInt DELAY_TIME = new ConfigInt("weapon.nightglow.delay_time", "nightglow_delay_time", 10, 0, 1200);
    public static final ConfigInt CHASE_TIME = new ConfigInt("weapon.nightglow.chase_time", "nightglow_chase_time", 30, 1, 1200);
    public static final ConfigDouble DELAY_SPEED = new ConfigDouble("weapon.nightglow.delay_speed", "nightglow_delay_speed", 0.75D, 0.01D, 64.0D);
    public static final ConfigDouble CHASE_SPEED = new ConfigDouble("weapon.nightglow.chase_speed", "nightglow_chase_speed", 1.5D, 0.01D, 64.0D);
    public static final ConfigDouble TARGET_RANGE = new ConfigDouble("weapon.nightglow.target_range", "nightglow_target_range", 32.0D, 1.0D, 256.0D);

    public static final double DELAY_DIR_LERP = 0.25;

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                DAMAGE,
                MANA_CONSUME,
                PROJECTILE_COUNT,
                DELAY_TIME,
                CHASE_TIME,
                DELAY_SPEED,
                CHASE_SPEED,
                TARGET_RANGE
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "nightglow",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/nightglow.png"),
        Component.translatable("item.lzxnoneterraria.nightglow"),
        CONFIG_DATA
    );

    public static final List<Vector3f> COLORS = List.of(
        new Vector3f(1.00F, 1.00F, 1.00F), // 白
        new Vector3f(0.10F, 0.10F, 0.10F), // 黑
        new Vector3f(1.00F, 0.20F, 0.20F), // 红
        new Vector3f(0.60F, 0.05F, 0.05F), // 深红
        new Vector3f(1.00F, 0.55F, 0.10F), // 橙
        new Vector3f(0.85F, 0.35F, 0.00F), // 深橙
        new Vector3f(1.00F, 0.95F, 0.20F), // 黄
        new Vector3f(0.20F, 0.90F, 0.30F), // 绿
        new Vector3f(0.05F, 0.50F, 0.10F), // 深绿
        new Vector3f(1.00F, 0.50F, 0.75F), // 粉
        new Vector3f(0.20F, 0.50F, 1.00F), // 蓝
        new Vector3f(0.40F, 0.85F, 1.00F), // 浅蓝
        new Vector3f(0.75F, 0.25F, 0.95F)  // 紫
    );

    public Nightglow() {
        super(Tiers.DIAMOND, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public float getTooltipDamage(ItemStack weaponStack, LivingEntity entity) {
        float damage = entity instanceof Player player ? DamageUtil.applyPlayerDamageEffects(player, DAMAGE.get()) : DAMAGE.get();
        return applyMagicDamageBonus(weaponStack, entity, damage);
    }

    @Override
    protected double getManaConsumeRate(ItemStack stack, LivingEntity entity) {
        return MANA_CONSUME.get();
    }

    @Override
    public int getUseTime(ItemStack weaponStack, LivingEntity entity) {
        return computeUseTime(34, weaponStack, entity);
    }

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            checkBeforeTick(summon);

            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            Vector3f color = new Vector3f(
                customData.contains("colorR") ? customData.getFloat("colorR") : 1.0f,
                customData.contains("colorG") ? customData.getFloat("colorG") : 1.0f,
                customData.contains("colorB") ? customData.getFloat("colorB") : 1.0f
            );

            if(summon.level().isClientSide()) {
                Vector3f[] dirs = MathUtil.computeCoordinateSystem(summon);
                Vec3 right = MathUtil.toVec3(dirs[2]);
                summon.trailPositions.addFirst(summon.position().add(right.scale(0.15)));
                summon.trailPositions.addFirst(summon.position().add(right.scale(-0.15)));
                while(summon.trailPositions.size() > 20) summon.trailPositions.removeLast();
                if(summon.getEntityData().get(StaticSummon.AGE) > DELAY_TIME.get()) {
                    DustParticleOptions particle = new DustParticleOptions(0.08f, 0.5f, 40, true, new Vector3f[]{color});
                    ParticleUtil.addParticle(
                            summon.level(), particle,
                            summon.position(), 0,
                            new Vec3(0, 0, 0), 0.2f
                    );
                }
            }
            if(summon.level().isClientSide()) return;

            // 1. 运动控制逻辑（分阶段）
            if(summon.getEntityData().get(StaticSummon.AGE) > DELAY_TIME.get()) {
                int dashTime = customData.contains("dashTime") ? customData.getInt("dashTime") : 0;
                if(dashTime > 0) {
                    // 处于冲刺状态：直接获取当前运动方向，保持 CHASE_SPEED 移动
                    Vec3 movement = summon.getDeltaMovement();
                    if(movement.lengthSqr() > 1.0E-6D) {
                        summon.setDeltaMovement(movement.normalize().scale(CHASE_SPEED.get()));
                    }
                    customData.putInt("dashTime", dashTime - 1);
                }else {
                    AABB searchBox = summon.getBoundingBox().inflate(TARGET_RANGE.get());
                    List<Entity> enemies = SearchUtil.searchEnemies(summon, summon.getOwner(), searchBox);
                    Entity target = null;
                    double bestDistSqr = Double.MAX_VALUE;
                    Vec3 summonPos = summon.position();
                    for(Entity enemy : enemies) {
                        if(!enemy.isAlive()) continue;
                        Vec3 enemyCenter = enemy.getBoundingBox().getCenter();
                        double distSqr = summonPos.distanceToSqr(enemyCenter);
                        if(distSqr >= bestDistSqr) continue;
                        if(!hasLineOfSight(summon, enemyCenter)) continue;
                        bestDistSqr = distSqr;
                        target = enemy;
                    }

                    if(target != null && target.isAlive()) {
                        Vec3 targetPos = target.getBoundingBox().getCenter();
                        Vec3 toTarget = targetPos.subtract(summon.position());
                        double dist = toTarget.length();

                        if(dist < CHASE_SPEED.get()) {
                            // 距离小于 CHASE_SPEED 时，直接设置冲刺速度并记录 dashTime 为 2
                            Vec3 dashDir = toTarget.lengthSqr() > 1.0E-6D ? toTarget.normalize() : summon.getLookAngle().normalize();
                            summon.setDeltaMovement(dashDir.scale(CHASE_SPEED.get()));
                            customData.putInt("dashTime", 2);
                        }else {
                            // 追逐逻辑：平滑转向朝目标飞去
                            Vec3 chaseDir = toTarget.normalize();
                            Vec3 currentMovement = summon.getDeltaMovement();
                            double turnRate = 0.35D;
                            Vec3 targetMovement = chaseDir.scale(CHASE_SPEED.get());
                            Vec3 newMovement = currentMovement.lerp(targetMovement, turnRate).normalize().scale(CHASE_SPEED.get());
                            summon.setDeltaMovement(newMovement);
                        }
                    }else {
                        Vec3 movement = summon.getDeltaMovement();
                        if(movement.lengthSqr() > 1.0E-6D) {
                            summon.setDeltaMovement(movement.normalize().scale(CHASE_SPEED.get()));
                        }
                    }
                }
            }else {
                Vec3 delayPos = new Vec3(
                    customData.contains("delayPosX") ? customData.getDouble("delayPosX") : summon.getX(),
                    customData.contains("delayPosY") ? customData.getDouble("delayPosY") : summon.getY(),
                    customData.contains("delayPosZ") ? customData.getDouble("delayPosZ") : summon.getZ()
                );
                Vec3 movement = summon.getDeltaMovement();
                Vec3 distV = delayPos.subtract(summon.position());
                if(distV.lengthSqr() > 1.0E-6D) {
                    Vec3 targetMovement = distV.normalize().scale(Math.min(DELAY_SPEED.get(), distV.length()));
                    Vec3 newMovement = movement.lerp(targetMovement, DELAY_DIR_LERP);
                    summon.setDeltaMovement(newMovement);
                }
            }

            // 2. 碰到方块反弹逻辑（全局生效）
            Vec3 currentPos = summon.position();
            Vec3 nextPos = currentPos.add(summon.getDeltaMovement());
            BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, nextPos);
            if(blockHitResult.getType() != HitResult.Type.MISS) {
                Direction face = blockHitResult.getDirection();
                Vec3 motion = summon.getDeltaMovement();
                double mx = motion.x;
                double my = motion.y;
                double mz = motion.z;
                switch(face.getAxis()) {
                    case X -> mx = -mx;
                    case Y -> my = -my;
                    case Z -> mz = -mz;
                }
                summon.setDeltaMovement(new Vec3(mx, my, mz));
                //SoundUtil.playServerSound(summon.level(), ModSounds.STAR_COLLIDE.get(), summon.position());
            }

            // 3. 实体碰撞伤害逻辑（全局生效）
            Vec3 endPos = summon.position().add(summon.getDeltaMovement());
            List<Entity> hitEntities = summon.level().getEntitiesOfClass(
                Entity.class,
                new AABB(currentPos, endPos).inflate(0.35D),
                FilterUtil.createTargetFilter(summon, summon.getOwner())
            );
            for(Entity hitEntity: hitEntities) {
                ItemStack sourceStack = summon.getEntityData().get(StaticSummon.STACK_SOURCE);
                DamageUtil.magicAttack(summon, hitEntity, sourceStack, DAMAGE.get(), 0.5F, 10);
            }

            // 4. 更新实体的正确朝向（全局生效）
            Vec3 finalMovement = summon.getDeltaMovement();
            if(finalMovement.lengthSqr() > 1.0E-6D) {
                float[] xyRot = MathUtil.computeXYRot(finalMovement.toVector3f());
                summon.setXRot(xyRot[0]);
                summon.setYRot(xyRot[1]);
                summon.xRotO = xyRot[0];
                summon.yRotO = xyRot[1];
            }
        }

        @Override
        public AABB getBoundingBoxForCulling(StaticSummon summon) {
            if(summon.trailPositions.isEmpty()) return summon.getBoundingBox();
            AABB box = summon.getBoundingBox();
            for(Vec3 pos : summon.trailPositions) {
                box = box.minmax(new AABB(pos, pos));
            }
            return box.inflate(0.5D);
        }
    };

    @Override
    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        if(level.isClientSide()) return;
        player.playNotifySound(ModSounds.SUMMON2.get(), SoundSource.PLAYERS, 4.0F, 1.0F);

        for(int i = 0;i < PROJECTILE_COUNT.get();i++) {
            Vector3f selectedColor = COLORS.get(player.getRandom().nextInt(COLORS.size()));
            if(level instanceof ServerLevel serverLevel) {
                DustParticleOptions particle = new DustParticleOptions(0.08f, 0.5f, 40, true, new Vector3f[]{selectedColor});
                ParticleUtil.addParticles(
                        serverLevel, particle,
                        player.getBoundingBox().getCenter(), new Vec3(0, 0, 0), 0.25,
                        10
                );
            }
            Vec3 center = player.getBoundingBox().getCenter();
            Vec3 delayMovement = MathUtil.getRandomPosOnRadius(center, 4).subtract(center).normalize().scale(DELAY_SPEED.get());
            Vec3 delayPos = MathUtil.getRandomPosOnRadius(center, 12);

            StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), level);
            summon.setOwner(player);
            summon.setPos(center);
            summon.setDeltaMovement(delayMovement);
            summon.getEntityData().set(StaticSummon.STACK_SOURCE, stack.copy());
            summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.NIGHTGLOW_PROJECTILE);
            summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.NIGHTGLOW_PROJECTILE.get()));
            summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
            summon.getEntityData().set(StaticSummon.RXP, -90);
            summon.getEntityData().set(StaticSummon.COLOR_R, selectedColor.x);
            summon.getEntityData().set(StaticSummon.COLOR_G, selectedColor.y);
            summon.getEntityData().set(StaticSummon.COLOR_B, selectedColor.z);
            summon.getEntityData().set(StaticSummon.LIFETIME, DELAY_TIME.get() + CHASE_TIME.get());
            summon.getEntityData().set(StaticSummon.GLOW, true);
            summon.setNoGravity(true);
            summon.noPhysics = true;

            float[] initialXyRot = MathUtil.computeXYRot(delayMovement.toVector3f());
            summon.setXRot(initialXyRot[0]);
            summon.setYRot(initialXyRot[1]);
            summon.xRotO = summon.getXRot();
            summon.yRotO = summon.getYRot();

            CompoundTag customData = new CompoundTag();
            customData.putFloat("colorR", selectedColor.x);
            customData.putFloat("colorG", selectedColor.y);
            customData.putFloat("colorB", selectedColor.z);
            customData.putDouble("delayPosX", delayPos.x);
            customData.putDouble("delayPosY", delayPos.y);
            customData.putDouble("delayPosZ", delayPos.z);
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
            level.addFreshEntity(summon);
        }
    }

    private static boolean hasLineOfSight(Entity entity, Vec3 targetPos) {
        return entity.level().clip(new ClipContext(
            entity.position(),
            targetPos,
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            entity
        )).getType() == HitResult.Type.MISS;
    }
}
