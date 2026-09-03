package com.lzxnone.terraria.item.weapon.magic.gem_staff;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.IStaticProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.ammo.Ammo;
import com.lzxnone.terraria.item.armor.GemRobe;
import com.lzxnone.terraria.item.weapon.MagicWeapon;
import com.lzxnone.terraria.particle.CircleParticleOptions;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.utils.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public abstract class GemStaff extends MagicWeapon {
    public static final int SKILL_AMETHYST = 1 << 0; // 1 (紫晶 - 追踪)
    public static final int SKILL_TOPAZ    = 1 << 1; // 2 (黄玉 - 爆炸)
    public static final int SKILL_SAPPHIRE = 1 << 2; // 4 (蓝玉 - 双螺旋)
    public static final int SKILL_EMERALD  = 1 << 3; // 8 (翡翠 - 地形附着/双速)
    public static final int SKILL_RUBY     = 1 << 4; // 16 (红玉 - 双发连射)
    public static final int SKILL_AMBER    = 1 << 5; // 32 (琥珀 - 反弹穿透)
    public static final int SKILL_DIAMOND  = 1 << 6; // 64 (钻石 - 巨型穿透)

    public static final Vector3f OFFSET = new Vector3f(-0.3f, -0.15f, 1.2f);
    public static final double DEFAULT_SPEED = 1.0D;
    public static final double DOUBLE_HELIX_RADIUS = 0.6D;
    public static final double DOUBLE_HELIX_OMEGA = 0.35D;
    public static final double BURST_OFFSET = -1.2D;

    public static final int LIFETIME = 50;
    public static final double TURN_RATE = 0.35D;
    public static final double CLOSE_TARGET_RANGE = 5.0D;
    public static final double CLOSE_TURN_RATE = 0.75D;
    public static final double DEFAULT_HITBOX_INFLATE = 0.35D;

    /**
     * 核心锚点实体的行为（完全掌控运动逻辑、追踪与方块碰撞/反弹）
     */
    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            checkBeforeTick(summon);

            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA).copy();
            int skill = customData.getInt("skill");
            boolean amethyst = (skill & SKILL_AMETHYST) != 0;
            boolean emerald = (skill & SKILL_EMERALD) != 0;
            boolean amber = (skill & SKILL_AMBER) != 0;

            Vec3 currentDir = summon.getLookAngle().normalize();
            Vec3 nextDir = currentDir;

            // 紫晶追踪
            if(amethyst) {
                double targetRange = AmethystStaff.TARGET_RANGE.get();
                Entity target = findTarget(summon, targetRange);
                if(target != null) {
                    Vec3 targetCenter = target.getBoundingBox().getCenter();
                    Vec3 targetDir = targetCenter.subtract(summon.position()).normalize();
                    double turnRate = targetCenter.distanceTo(summon.position()) <= CLOSE_TARGET_RANGE ? CLOSE_TURN_RATE : TURN_RATE;
                    if (hasLineOfSight(summon.level(), summon, summon.position(), targetCenter)) {
                        nextDir = steer(currentDir, targetDir, turnRate);
                    }
                }
            }

            double speed = summon.getDeltaMovement().length();
            if(emerald) {
                speed *= EmeraldStaff.DRAG.get();
                if(customData.contains("stop")) speed = 0;
            }
            Vec3 motion = nextDir.normalize().scale(speed);
            summon.setDeltaMovement(motion);

            //不存在翡翠：速度为0死亡
            if(!emerald && motion.lengthSqr() <= 1.0E-2D) {
                onDied(summon);
                return;
            }

            Vec3 start = summon.position();
            Vec3 nextPos = start.add(motion);

            //方块碰撞
            if(motion.lengthSqr() > 1.0E-6D) {
                BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, nextPos);
                if(blockHitResult.getType() != HitResult.Type.MISS) {
                    if(amber) {
                        // 琥珀反弹（严格镜面反射，仅当朝向方块内部运动时反弹）
                        Vec3 reflectedMotion = reflect(motion, blockHitResult.getDirection()).normalize().scale(speed);
                        summon.setPos(blockHitResult.getLocation().add(Vec3.atLowerCornerOf(blockHitResult.getDirection().getNormal()).scale(0.05)));
                        updateRotation(summon, reflectedMotion);
                        summon.setDeltaMovement(reflectedMotion);

                        if(summon.level() instanceof ServerLevel serverLevel) {
                            Ammo.playBlockHitEffects(serverLevel, blockHitResult);
                        }

                        int hitCount = customData.getInt("hitCount") + 1;
                        customData.putInt("hitCount", hitCount);
                        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
                        if(hitCount >= AmberStaff.MAX_HIT_COUNT.get()) {
                            onDied(summon);
                            return;
                        }
                        return;
                    }else if(emerald) {
                        if(summon.level() instanceof ServerLevel serverLevel) {
                            Ammo.playBlockHitEffects(serverLevel, blockHitResult);
                        }
                        int hitCount = customData.getInt("hitCount") + 1;
                        customData.putInt("hitCount", hitCount);
                        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

                        //翡翠停止
                        customData.putBoolean("stop", true);
                        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
                        summon.setPos(blockHitResult.getLocation().add(Vec3.atLowerCornerOf(blockHitResult.getDirection().getNormal()).scale(0.05D)));
                        updateRotation(summon, nextDir);
                        summon.setDeltaMovement(Vec3.ZERO);

                        return;
                    }else {
                        if(summon.level() instanceof ServerLevel serverLevel) {
                            Ammo.playBlockHitEffects(serverLevel, blockHitResult);
                        }
                        onDied(summon);
                        return;
                    }
                }
            }

            updateRotation(summon, nextDir);
        }

        @Override
        public void onDied(StaticSummon summon) {
            if(!summon.level().isClientSide() && !summon.isRemoved()) {
                CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
                if(summon.level() instanceof ServerLevel serverLevel && customData.hasUUID("projectileUUID")) {
                    Entity p = serverLevel.getEntity(customData.getUUID("projectileUUID"));
                    if(p instanceof StaticProjectile proj) {
                        PROJECTILE_BEHAVIOR.onDied(proj);
                    }
                }
                summon.discard();
            }
        }
    };

    /**
     * 实际魔法弹实体的行为（跟随核心锚点并处理实体命中）
     */
    public static final IStaticProjectileBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileBehavior() {
        @Override
        public void onMoving(StaticProjectile projectile) {
            // 移动时在客户端记录尾迹并生成相应颜色的 dust 粒子
            if(projectile.level().isClientSide()) {
                Vector3f motion = projectile.getDeltaMovement().lengthSqr() > 1.0E-6D ?
                    projectile.getDeltaMovement().toVector3f() :
                    projectile.getEntityData().get(StaticProjectile.DIRECTION);
                Vector3f[] dirs = MathUtil.computeCoordinateSystem(motion, projectile.getYRot());
                Vec3 right = MathUtil.toVec3(dirs[2]);
                projectile.trailPositions.addFirst(projectile.position().add(right.scale(0.2D)));
                projectile.trailPositions.addFirst(projectile.position().add(right.scale(-0.2D)));
                while(projectile.trailPositions.size() > 24) projectile.trailPositions.removeLast();

                float r = projectile.getEntityData().get(StaticProjectile.COLOR_R);
                float g = projectile.getEntityData().get(StaticProjectile.COLOR_G);
                float b = projectile.getEntityData().get(StaticProjectile.COLOR_B);
                Vector3f color = new Vector3f(r, g, b);
                DustParticleOptions dustParticle = new DustParticleOptions(0.1F, 0.4F, 40, true, new Vector3f[]{color});
                ParticleUtil.addParticle(projectile.level(), dustParticle, projectile.position(), 0.0D, Vec3.ZERO, 0.02D);
                return;
            }

            if(!(projectile.getOwner() instanceof StaticSummon core) || !core.isAlive()) {
                onDied(projectile);
                return;
            }

            //同步原点
            projectile.getEntityData().set(StaticProjectile.ORIGIN, core.position().toVector3f());

            CompoundTag customData = projectile.getEntityData().get(StaticProjectile.CUSTOM_DATA);
            int skill = customData.getInt("skill");
            boolean sapphire = (skill & SKILL_SAPPHIRE) != 0;
            boolean emerald = (skill & SKILL_EMERALD) != 0;

            // 蓝玉 + 翡翠：当核心速度接近0时，将相对核心距离直接写成表达式常量，保持相对位置不变
            CompoundTag coreData = core.getEntityData().get(StaticSummon.CUSTOM_DATA);
            boolean isStopped = coreData.getBoolean("stop") || core.getDeltaMovement().length() < 1.0E-2D;
            if(sapphire && emerald && isStopped) {
                String exprX = projectile.getEntityData().get(StaticProjectile.EXPRESSION_X);
                if(exprX.contains("t")) {
                    int age = projectile.getEntityData().get(StaticProjectile.AGE);
                    double curValX = projectile.exprX != null ? projectile.exprX.setVariable("t", age).evaluate() : 0.0D;
                    double curValY = projectile.exprY != null ? projectile.exprY.setVariable("t", age).evaluate() : 0.0D;
                    double curValZ = projectile.exprZ != null ? projectile.exprZ.setVariable("t", age).evaluate() : 0.0D;
                    projectile.setExpressionX(String.format(Locale.ROOT, "%.4f", curValX));
                    projectile.setExpressionY(String.format(Locale.ROOT, "%.4f", curValY));
                    projectile.setExpressionZ(String.format(Locale.ROOT, "%.4f", curValZ));
                }
            }else if(sapphire) {
                //蓝玉：时刻计算坐标系（使用核心当前朝向）
                Vec3 motion = core.getLookAngle();
                if (motion.lengthSqr() < 1.0E-7D) {
                    motion = MathUtil.toVec3(projectile.getEntityData().get(StaticProjectile.DIRECTION));
                }
                Vector3f[] dirs = MathUtil.computeCoordinateSystem(motion.toVector3f(), core.getYRot());
                projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
                projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
                projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);
            }

            // 实体碰撞检测（参考子弹逻辑，使用 AABB 包围盒范围检测）
            if(projectile.isAlive()) {
                boolean diamond = (skill & SKILL_DIAMOND) != 0;
                double inflate = diamond ? DiamondStaff.HITBOX_INFLATE.get() : DEFAULT_HITBOX_INFLATE;
                Vec3 start = projectile.prevPos == null ? projectile.position() : projectile.prevPos;

                // 若刚发射不久，起点延伸至玩家视线位置，解决近距离/贴脸敌人无法命中的问题
                Entity owner = core.getOwner();
                if(projectile.tickCount <= 1 && owner instanceof Player player) {
                    start = player.getEyePosition();
                }

                AABB hitBox = new AABB(start, projectile.position()).inflate(inflate);
                List<Entity> targets = projectile.level().getEntitiesOfClass(
                    Entity.class,
                    hitBox,
                    FilterUtil.createTargetFilter(projectile, owner)
                );

                if(!targets.isEmpty()) {
                    targets.sort(Comparator.comparingDouble(e -> e.distanceToSqr(projectile.position())));
                    for(Entity target : targets) {
                        if(!projectile.isAlive() || !core.isAlive()) break;
                        EntityHitResult hitResult = new EntityHitResult(target, target.position().add(0, target.getBbHeight() * 0.5D, 0));
                        onHitEntity(projectile, hitResult);
                    }
                }
            }
        }

        @Override
        public void onHitEntity(StaticProjectile projectile, EntityHitResult result) {
            if(projectile.level().isClientSide()) return;
            if(!(projectile.getOwner() instanceof StaticSummon core) || !core.isAlive()) return;
            if(!(core.getOwner() instanceof Player player)) return;

            Entity hitEntity = result.getEntity();
            if(hitEntity == player) return;

            CompoundTag customData = projectile.getEntityData().get(StaticProjectile.CUSTOM_DATA);
            int skill = customData.getInt("skill");

            //伤害逻辑
            float damage = customData.getFloat("damage");
            float knockback = customData.getFloat("knockback");
            float extraDamage = customData.contains("extraDamage") ? customData.getFloat("extraDamage") : 0.0F;
            float extraKnockback = customData.contains("extraKnockback") ? customData.getFloat("extraKnockback") : 0.0F;
            ItemStack stackSource = projectile.getEntityData().get(StaticProjectile.STACK_SOURCE);
            if(DamageUtil.magicAttack(projectile, hitEntity, stackSource, damage + extraDamage, knockback + extraKnockback, 10)) {
                // 击中实体时生成相应颜色的 circle 粒子
                if(projectile.level() instanceof ServerLevel serverLevel) {
                    float r = customData.contains("colorR") ? customData.getFloat("colorR") : projectile.getEntityData().get(StaticProjectile.COLOR_R);
                    float g = customData.contains("colorG") ? customData.getFloat("colorG") : projectile.getEntityData().get(StaticProjectile.COLOR_G);
                    float b = customData.contains("colorB") ? customData.getFloat("colorB") : projectile.getEntityData().get(StaticProjectile.COLOR_B);
                    Vector3f color = new Vector3f(r, g, b);
                    CircleParticleOptions hitCircle = new CircleParticleOptions(0.08F, 14, color);
                    ParticleUtil.addParticles(serverLevel, hitCircle, hitEntity.getBoundingBox().getCenter(), new Vec3(0.15D, 0.15D, 0.15D), 0.1D, 8);
                }

                //统计碰撞次数
                boolean amber = (skill & SKILL_AMBER) != 0;
                boolean emerald = (skill & SKILL_EMERALD) != 0;
                boolean diamond = (skill & SKILL_DIAMOND) != 0;

                if(amber) {
                    int hitCount = customData.getInt("hitCount") + 1;
                    if (hitCount >= AmberStaff.MAX_HIT_COUNT.get()) {
                        SUMMON_BEHAVIOR.onDied(core);
                    }
                    customData.putInt("hitCount", hitCount);
                    projectile.getEntityData().set(StaticProjectile.CUSTOM_DATA, customData);
                }else if(emerald || diamond) {
                    int hitCount = customData.getInt("hitCount") + 1;
                    if (hitCount >= Math.max(EmeraldStaff.MAX_ENTITY_HIT_COUNT.get(), DiamondStaff.MAX_ENTITY_HIT_COUNT.get())) {
                        SUMMON_BEHAVIOR.onDied(core);
                    }
                    customData.putInt("hitCount", hitCount);
                    projectile.getEntityData().set(StaticProjectile.CUSTOM_DATA, customData);
                }else {
                    SUMMON_BEHAVIOR.onDied(core);
                }

                if(!core.isAlive()) {
                    onDied(projectile);
                }
            }
        }

        @Override
        public void onHitBlock(StaticProjectile projectile, BlockHitResult result) {}

        @Override
        public void onDied(StaticProjectile projectile) {
            if(!projectile.level().isClientSide() && !projectile.isRemoved()) {
                CompoundTag customData = projectile.getEntityData().get(StaticProjectile.CUSTOM_DATA);
                int skill = customData.getInt("skill");
                boolean topaz = (skill & SKILL_TOPAZ) != 0;

                if(projectile.level() instanceof ServerLevel serverLevel) {
                    Vec3 pos = projectile.position();
                    float r = customData.contains("colorR") ? customData.getFloat("colorR") : projectile.getEntityData().get(StaticProjectile.COLOR_R);
                    float g = customData.contains("colorG") ? customData.getFloat("colorG") : projectile.getEntityData().get(StaticProjectile.COLOR_G);
                    float b = customData.contains("colorB") ? customData.getFloat("colorB") : projectile.getEntityData().get(StaticProjectile.COLOR_B);
                    Vector3f color = new Vector3f(r, g, b);

                    // 生成相应颜色的 circle 粒子
                    CircleParticleOptions deathCircle = new CircleParticleOptions(0.08F, 14, color);
                    ParticleUtil.addParticles(serverLevel, deathCircle, pos, new Vec3(0.15D, 0.15D, 0.15D), 0.1D, 8);

                    // 如果存在黄玉，额外释放更大的 circle 粒子并进行爆炸结算
                    if(topaz) {
                        double explosionRange = TopazStaff.EXPLOSION_RANGE.get();
                        float explosionDamage = TopazStaff.EXPLOSION_DAMAGE.get();
                        ItemStack stackSource = projectile.getEntityData().get(StaticProjectile.STACK_SOURCE);

                        CircleParticleOptions topazCircle = new CircleParticleOptions(0.15F, 18, color);
                        ParticleUtil.addParticles(serverLevel, topazCircle, pos, new Vec3(0.25D, 0.25D, 0.25D), 0.15D, 18);
                        //SoundUtil.playServerSound(serverLevel, ModSounds.BOOM.get(), pos);

                        UUID playerUUID = customData.hasUUID("playerUUID") ? customData.getUUID("playerUUID") : null;
                        Entity player = playerUUID != null ? serverLevel.getEntity(playerUUID) : null;

                        List<Entity> enemies = serverLevel.getEntitiesOfClass(
                            Entity.class,
                            AABB.ofSize(pos, explosionRange * 2.0D, explosionRange * 2.0D, explosionRange * 2.0D),
                            FilterUtil.createTargetFilter(projectile, player)
                        );

                        for(Entity target : enemies) {
                            if(target.distanceToSqr(pos) <= explosionRange * explosionRange) {
                                DamageUtil.magicAttack(projectile, target, stackSource, explosionDamage, 1.0F, 10);
                            }
                        }
                    }
                }
                projectile.discard();
            }
        }

        @Override
        public AABB getBoundingBoxForCulling(StaticProjectile projectile) {
            if(projectile.trailPositions.isEmpty()) return projectile.getBoundingBox();
            AABB box = projectile.getBoundingBox();
            for(Vec3 pos : projectile.trailPositions) {
                box = box.minmax(new AABB(pos, pos));
            }
            return box.inflate(0.8D);
        }
    };

    private final int skillMask;

    public GemStaff(Tier tier, Properties properties, int skillMask) {
        super(tier, properties);
        this.skillMask = skillMask;
    }

    public int getSkillMask(ItemStack stack) {
        return this.skillMask;
    }

    public boolean hasSynergy(ItemStack staffStack, LivingEntity entity) {
        if (entity == null) return false;
        ItemStack chestStack = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (chestStack.getItem() instanceof GemRobe robe) {
            return (robe.getSkillMask(chestStack) & this.getSkillMask(staffStack)) != 0;
        }
        return false;
    }

    public abstract float getDamage(ItemStack stack, LivingEntity entity);
    public abstract float getExtraDamage(ItemStack stack, LivingEntity entity);
    public float getKnockback(ItemStack stack, LivingEntity entity) {
        return 0.5F;
    }

    public float getExtraKnockback(ItemStack stack, LivingEntity entity) {
        return 0.25F;
    }

    public abstract Vector3f getColor();

    @Override
    public float getTooltipDamage(ItemStack weaponStack, LivingEntity entity) {
        float baseDmg = getDamage(weaponStack, entity) + (hasSynergy(weaponStack, entity) ? getExtraDamage(weaponStack, entity) : 0.0F);
        float damage = entity instanceof Player player ? DamageUtil.applyPlayerDamageEffects(player, baseDmg) : baseDmg;
        return applyMagicDamageBonus(weaponStack, entity, damage);
    }

    @Override
    public void shoot(Level level, Player player, InteractionHand hand, ItemStack weaponStack) {
        if (level.isClientSide()) return;

        SoundUtil.playServerSound(player, ModSounds.BEAM3.get(), 1.0F, 1.0F);

        Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
        Vec3 eyePos = player.getEyePosition();
        Vector3f offset = new Vector3f(
            hand == (player.getMainArm() == HumanoidArm.RIGHT ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND) ? -OFFSET.x : OFFSET.x,
            OFFSET.y,
            OFFSET.z
        );
        Vec3 startPos = new Vec3(
            eyePos.x + dirs[0].x * offset.z + dirs[1].x * offset.y + dirs[2].x * offset.x,
            eyePos.y + dirs[0].y * offset.z + dirs[1].y * offset.y + dirs[2].y * offset.x,
            eyePos.z + dirs[0].z * offset.z + dirs[1].z * offset.y + dirs[2].z * offset.x
        );

        Vec3 direction = MathUtil.toVec3(dirs[0]).normalize();

        // 获取武器和胸甲长袍的 mask
        int staffMask = getSkillMask(weaponStack);
        int robeMask = 0;
        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
        if(chestStack.getItem() instanceof GemRobe robe) {
            robeMask = robe.getSkillMask(chestStack);
        }

        int skill = staffMask | robeMask;
        boolean sapphire = (skill & SKILL_SAPPHIRE) != 0;
        boolean ruby = (skill & SKILL_RUBY) != 0;
        boolean emerald = (skill & SKILL_EMERALD) != 0;

        //红玉：后方生成的弹射+1
        int burstCount = ruby ? 2 : 1;

        //蓝玉：双螺旋
        int helixCount = sapphire ? 2 : 1;

        double speed = DEFAULT_SPEED;
        //翡翠：速度加快
        if(emerald) {
            speed *= EmeraldStaff.SPEED_MULTIPLIER.get();
        }

        for(int b = 0; b < burstCount; b++) {
            Vec3 burstPos = startPos.add(direction.scale(BURST_OFFSET * b));

            for(int h = 0; h < helixCount; h++) {
                // 1. 创建核心实体（一个核心独立管理一个实际飞弹）
                StaticSummon core = new StaticSummon(ModEntities.STATIC_SUMMON.get(), level);
                core.setOwner(player);
                core.setPos(burstPos);
                core.setDeltaMovement(direction.scale(speed));
                core.getEntityData().set(StaticSummon.STACK_SOURCE, weaponStack.copy());
                core.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.GEM_STAFF_PROJECTILE);
                core.getEntityData().set(StaticSummon.LIFETIME, emerald ? EmeraldStaff.LIFETIME.get() : LIFETIME);
                core.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
                core.getEntityData().set(StaticSummon.GLOW, false);
                core.setNoGravity(true);
                core.noPhysics = true;

                float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);
                core.setXRot(xyRot[0]);
                core.setYRot(xyRot[1]);
                core.xRotO = xyRot[0];
                core.yRotO = xyRot[1];

                // 2. 生成实际的宝石法杖弹射（使用 StaticProjectile 实体）
                StaticProjectile projectile = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), level);
                projectile.setOwner(core);

                CompoundTag coreData = new CompoundTag();
                coreData.putInt("skill", skill);
                coreData.putUUID("projectileUUID", projectile.getUUID());
                core.getEntityData().set(StaticSummon.CUSTOM_DATA, coreData);
                level.addFreshEntity(core);
                projectile.setPos(burstPos);
                projectile.getEntityData().set(StaticProjectile.STACK_SOURCE, weaponStack.copy());
                projectile.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.GEM_STAFF_PROJECTILE);
                projectile.getEntityData().set(StaticProjectile.RENDER_MODE, "custom");
                projectile.getEntityData().set(StaticProjectile.GLOW, true);
                projectile.getEntityData().set(StaticProjectile.LIFETIME, emerald ? EmeraldStaff.LIFETIME.get() : LIFETIME);
                projectile.getEntityData().set(StaticProjectile.ORIGIN, burstPos.toVector3f());
                projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
                projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
                projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);

                Vector3f color = getColor();
                projectile.getEntityData().set(StaticProjectile.COLOR_R, color.x);
                projectile.getEntityData().set(StaticProjectile.COLOR_G, color.y);
                projectile.getEntityData().set(StaticProjectile.COLOR_B, color.z);

                // 蓝玉：双螺旋对称圆周方程
                if(sapphire) {
                    String sign = (h == 0) ? "" : "-";
                    projectile.getEntityData().set(StaticProjectile.EXPRESSION_X, String.format(Locale.ROOT, "%s%.3f*cos(%.3f*t)", sign, DOUBLE_HELIX_RADIUS, DOUBLE_HELIX_OMEGA));
                    projectile.getEntityData().set(StaticProjectile.EXPRESSION_Y, String.format(Locale.ROOT, "%s%.3f*sin(%.3f*t)", sign, DOUBLE_HELIX_RADIUS, DOUBLE_HELIX_OMEGA));
                    projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, "0");
                }else {
                    projectile.getEntityData().set(StaticProjectile.EXPRESSION_X, "0");
                    projectile.getEntityData().set(StaticProjectile.EXPRESSION_Y, "0");
                    projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, "0");
                }

                CompoundTag projData = new CompoundTag();
                projData.putInt("skill", skill);
                projData.putUUID("playerUUID", player.getUUID());
                projData.putFloat("damage", (ruby || sapphire) ? getDamage(weaponStack, player) / 2 : getDamage(weaponStack, player));
                projData.putFloat("knockback", getKnockback(weaponStack, player));
                projData.putFloat("colorR", color.x);
                projData.putFloat("colorG", color.y);
                projData.putFloat("colorB", color.z);

                if(staffMask == robeMask && staffMask != 0) {
                    projData.putFloat("extraDamage", getExtraDamage(weaponStack, player));
                    projData.putFloat("extraKnockback", getExtraKnockback(weaponStack, player));
                }

                projectile.getEntityData().set(StaticProjectile.CUSTOM_DATA, projData);
                level.addFreshEntity(projectile);
            }
        }
    }

    // ==================== 辅助方法 ====================

    private static Entity findTarget(StaticSummon summon, double targetRange) {
        List<Entity> targets = SearchUtil.searchEnemies(summon, summon.getOwner(), summon.getBoundingBox().inflate(targetRange));
        Entity bestTarget = null;
        double bestDistance = Double.MAX_VALUE;
        for (Entity target : targets) {
            Vec3 targetCenter = target.getBoundingBox().getCenter();
            if (!hasLineOfSight(summon.level(), summon, summon.position(), targetCenter)) continue;

            double distance = targetCenter.distanceToSqr(summon.position());
            if (distance < bestDistance) {
                bestDistance = distance;
                bestTarget = target;
            }
        }
        return bestTarget;
    }

    private static boolean hasLineOfSight(Level level, Entity clipEntity, Vec3 from, Vec3 to) {
        return level.clip(new ClipContext(
            from,
            to,
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            clipEntity
        )).getType() == HitResult.Type.MISS;
    }

    private static Vec3 steer(Vec3 currentDir, Vec3 desiredDir, double turnRate) {
        Vec3 steered = currentDir.scale(1.0D - turnRate).add(desiredDir.normalize().scale(turnRate));
        if (steered.lengthSqr() < 1.0E-7D) return desiredDir.normalize();
        return steered.normalize();
    }

    private static void updateRotation(StaticSummon summon, Vec3 motion) {
        float[] xyRot = MathUtil.computeXYRot(motion.toVector3f());
        summon.setXRot(xyRot[0]);
        summon.setYRot(xyRot[1]);
        summon.xRotO = xyRot[0];
        summon.yRotO = xyRot[1];
    }

    private static Vec3 reflect(Vec3 motion, Direction hitDirection) {
        Vec3 normal = Vec3.atLowerCornerOf(hitDirection.getNormal());
        double dot = motion.dot(normal);
        if (dot >= 0.0D) {
            return motion;
        }
        return motion.subtract(normal.scale(2.0D * dot));
    }
}
