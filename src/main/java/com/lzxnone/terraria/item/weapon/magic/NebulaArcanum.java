package com.lzxnone.terraria.item.weapon.magic;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.IStaticProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.weapon.MagicWeapon;
import com.lzxnone.terraria.particle.CircleParticleOptions;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;
import java.util.Locale;

public class NebulaArcanum extends MagicWeapon {
    public static final ConfigFloat DAMAGE = new ConfigFloat("weapon.nebula_arcanum.damage", "nebula_arcanum_damage", 8.0F, 0.0F, 8388600.0F);
    public static final ConfigDouble MANA_CONSUME = new ConfigDouble("weapon.nebula_arcanum.mana_consume", "nebula_arcanum_mana_consume", 30.0D, 0.0D, 10000.0D);
    public static final ConfigDouble SPEED = new ConfigDouble("weapon.nebula_arcanum.speed", "nebula_arcanum_speed", 0.5D, 0.0D, 10.0D);
    public static final ConfigInt LIFETIME = new ConfigInt("weapon.nebula_arcanum.lifetime", "nebula_arcanum_lifetime", 220, 1, 72000);
    public static final ConfigDouble TARGET_RANGE = new ConfigDouble("weapon.nebula_arcanum.target_range", "nebula_arcanum_target_range", 24.0D, 0.0D, 128.0D);
    public static final ConfigDouble EXPLOSION_RANGE = new ConfigDouble("weapon.nebula_arcanum.explosion_range", "nebula_arcanum_explosion_range", 4.0D, 0.0D, 64.0D);
    public static final ConfigInt HIT_COUNT = new ConfigInt("weapon.nebula_arcanum.hit_count", "nebula_arcanum_hit_count", 3, 1, 128);
    public static final ConfigFloat ORBIT_PROJECTILE_DAMAGE = new ConfigFloat("weapon.nebula_arcanum.orbit_projectile_damage", "nebula_arcanum_orbit_projectile_damage", 6.0F, 0.0F, 8388600.0F);
    public static final ConfigFloat SMALL_PROJECTILE_DAMAGE = new ConfigFloat("weapon.nebula_arcanum.small_projectile_damage", "nebula_arcanum_small_projectile_damage", 2.0F, 0.0F, 8388600.0F);

    private static final int TRACK_DELAY = 20;
    private static final int ORBIT_PROJECTILE_SPAWN_INTERVAL = 20;
    private static final int ORBIT_PROJECTILE_MAX_COUNT = 8;
    private static final int ORBIT_PROJECTILE_MIN_SPAWN_COUNT = 2;
    private static final int ORBIT_PROJECTILE_MAX_SPAWN_COUNT = 3;
    private static final int SMALL_PROJECTILE_MIN_COUNT = 10;
    private static final int SMALL_PROJECTILE_MAX_COUNT = 16;
    private static final int SMALL_PROJECTILE_LIFETIME = 20;
    private static final double SMALL_PROJECTILE_SPEED = 0.75D;
    private static final double HITBOX_INFLATE = 0.45D;
    private static final double ORBIT_RADIUS = 4D;
    private static final double ORBIT_SPEED = 0.1D;
    private static final double SMALL_PROJECTILE_GRAVITY = 0.04D;
    private static final double BOUNCE_SCALE = 0.85D;
    private static final String TARGET_UUID_KEY = "target";
    private static final String MAIN_UUID_KEY = "main";
    private static final String ORANGE_RED_KEY = "orangeRed";

    public static final CircleParticleOptions ORBIT_PROJECTILE_TRAIL_PARTICLE =
        new CircleParticleOptions(0.12F, 16, new Vector3f(1.0F, 0.18F, 0.72F));
    public static final CircleParticleOptions SMALL_PROJECTILE_PINK_PARTICLE =
        new CircleParticleOptions(0.16F, 14, new Vector3f(1.0F, 0.22F, 0.74F));
    public static final CircleParticleOptions SMALL_PROJECTILE_ORANGE_RED_PARTICLE =
        new CircleParticleOptions(0.16F, 14, new Vector3f(1.0F, 0.22F, 0.08F));
    public static final CircleParticleOptions MAIN_PINK_PARTICLE =
        new CircleParticleOptions(0.14F, 10, new Vector3f(1.0F, 0.18F, 0.72F));
    public static final CircleParticleOptions MAIN_ORANGE_RED_PARTICLE =
        new CircleParticleOptions(0.14F, 10, new Vector3f(1.0F, 0.18F, 0.06F));
    public static final CircleParticleOptions MAIN_BLACK_PARTICLE =
        new CircleParticleOptions(0.14F, 10, new Vector3f(0.0F, 0.0F, 0.0F));

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                DAMAGE,
                MANA_CONSUME,
                SPEED,
                LIFETIME,
                TARGET_RANGE,
                EXPLOSION_RANGE,
                HIT_COUNT,
                ORBIT_PROJECTILE_DAMAGE,
                SMALL_PROJECTILE_DAMAGE
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "nebula_arcanum",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/nebula_arcanum.png"),
        Component.translatable("item.lzxnoneterraria.nebula_arcanum"),
        CONFIG_DATA
    );

    public NebulaArcanum() {
        super(Tiers.DIAMOND, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            checkBeforeTick(summon);
            if(summon.isRemoved()) return;

            if(summon.level().isClientSide()) {
                if(summon.getEntityData().get(StaticSummon.AGE) < TRACK_DELAY) return;
                Vec3 speed = new Vec3(
                    summon.getRandom().nextDouble() * 2.0D - 1.0D,
                    summon.getRandom().nextDouble() * 2.0D - 1.0D,
                    summon.getRandom().nextDouble() * 2.0D - 1.0D
                );
                if(speed.lengthSqr() < 1.0E-7D) speed = new Vec3(0.0D, 1.0D, 0.0D);
                speed = speed.normalize();
                ParticleUtil.addParticle(summon.level(), MAIN_PINK_PARTICLE, summon.position().add(speed.scale(1.5D)), 0.0D, speed.scale(0.5D), 0.0D);

                speed = new Vec3(
                    summon.getRandom().nextDouble() * 2.0D - 1.0D,
                    summon.getRandom().nextDouble() * 2.0D - 1.0D,
                    summon.getRandom().nextDouble() * 2.0D - 1.0D
                );
                if(speed.lengthSqr() < 1.0E-7D) speed = new Vec3(0.0D, 1.0D, 0.0D);
                speed = speed.normalize();
                ParticleUtil.addParticle(summon.level(), MAIN_ORANGE_RED_PARTICLE, summon.position().add(speed.scale(1.5D)), 0.0D, speed.scale(0.5D), 0.0D);

                speed = new Vec3(
                    summon.getRandom().nextDouble() * 2.0D - 1.0D,
                    summon.getRandom().nextDouble() * 2.0D - 1.0D,
                    summon.getRandom().nextDouble() * 2.0D - 1.0D
                );
                if(speed.lengthSqr() < 1.0E-7D) speed = new Vec3(0.0D, 1.0D, 0.0D);
                speed = speed.normalize();
                ParticleUtil.addParticle(summon.level(), MAIN_BLACK_PARTICLE, summon.position().add(speed.scale(1.5D)), 0.0D, speed.scale(0.5D), 0.0D);
            }

            Vec3 motion = summon.getDeltaMovement();
            if(motion.lengthSqr() < 1.0E-7D) motion = summon.getLookAngle().normalize().scale(SPEED.get());
            if(motion.lengthSqr() < 1.0E-7D) motion = new Vec3(0.0D, 0.0D, 1.0D).scale(SPEED.get());

            int age = summon.getEntityData().get(StaticSummon.AGE);
            if(age > TRACK_DELAY && summon.level() instanceof ServerLevel serverLevel) {
                CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
                Entity target = null;
                if(customData.contains(TARGET_UUID_KEY)) {
                    target = serverLevel.getEntity(customData.getUUID(TARGET_UUID_KEY));
                    if(target == null || !target.isAlive() || !hasLineOfSight(summon, target)) {
                        target = null;
                        customData.remove(TARGET_UUID_KEY);
                    }
                }
                if(target == null) {
                    double bestDistance = Double.MAX_VALUE;
                    for(Entity candidate : SearchUtil.searchEnemies(summon, summon.getOwner(), summon.getBoundingBox().inflate(TARGET_RANGE.get()))) {
                        if(!hasLineOfSight(summon, candidate)) continue;
                        double distance = candidate.getBoundingBox().getCenter().distanceToSqr(summon.position());
                        if(distance < bestDistance) {
                            bestDistance = distance;
                            target = candidate;
                        }
                    }
                    if(target != null) customData.putUUID(TARGET_UUID_KEY, target.getUUID());
                }

                int spawned = customData.getInt("orbitProjectileCount");
                if(spawned < ORBIT_PROJECTILE_MAX_COUNT && (age - TRACK_DELAY - 1) % ORBIT_PROJECTILE_SPAWN_INTERVAL == 0) {
                    Entity owner = summon.getOwner();
                    if(owner != null) {
                        int spawnCount = ORBIT_PROJECTILE_MIN_SPAWN_COUNT + summon.getRandom().nextInt(ORBIT_PROJECTILE_MAX_SPAWN_COUNT - ORBIT_PROJECTILE_MIN_SPAWN_COUNT + 1);
                        spawnCount = Math.min(spawnCount, ORBIT_PROJECTILE_MAX_COUNT - spawned);
                        for(int i = 0; i < spawnCount; i++) {
                            Vec3 orbitDirection = summon.getDeltaMovement();
                            if(orbitDirection.lengthSqr() < 1.0E-7D) orbitDirection = summon.getLookAngle();
                            if(orbitDirection.lengthSqr() < 1.0E-7D) orbitDirection = new Vec3(0.0D, 0.0D, 1.0D);
                            Vector3f[] dirs = MathUtil.computeCoordinateSystem(orbitDirection.toVector3f(), summon.getYRot());
                            dirs = MathUtil.rotateCoordinateSystem(dirs[0], dirs[2], (int) ((Math.random() * 2 - 1) * 90));
                            double phase = summon.getRandom().nextDouble() * Math.PI * 2.0D;

                            StaticProjectile projectile = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), summon.level());
                            projectile.setOwner(summon);
                            projectile.setPos(summon.position());
                            projectile.getEntityData().set(StaticProjectile.STACK_SOURCE, summon.getEntityData().get(StaticSummon.STACK_SOURCE));
                            projectile.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.NEBULA_ARCANUM_ORBIT_PROJECTILE);
                            projectile.getEntityData().set(StaticProjectile.RENDER_MODE, "custom");
                            projectile.getEntityData().set(StaticProjectile.ORIGIN, summon.position().toVector3f());
                            projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
                            projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
                            projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);
                            projectile.getEntityData().set(StaticProjectile.LIFETIME, StaticSummon.INFINITE_LIFETIME);
                            projectile.getEntityData().set(StaticProjectile.RZPS, 18);
                            projectile.getEntityData().set(StaticProjectile.SCALE_X, 0.4f);
                            projectile.getEntityData().set(StaticProjectile.SCALE_Y, 0.4f);
                            projectile.getEntityData().set(StaticProjectile.SCALE_Z, 0.4f);
                            projectile.getEntityData().set(StaticProjectile.EXPRESSION_X, String.format(Locale.ROOT, "%.3f*cos(%.3f*t+%.3f)", ORBIT_RADIUS, ORBIT_SPEED, phase));
                            projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format(Locale.ROOT, "%.3f*sin(%.3f*t+%.3f)", ORBIT_RADIUS, ORBIT_SPEED, phase));
                            projectile.getEntityData().set(StaticProjectile.GLOW, true);

                            CompoundTag orbitData = new CompoundTag();
                            orbitData.putUUID(MAIN_UUID_KEY, summon.getUUID());
                            projectile.getEntityData().set(StaticProjectile.CUSTOM_DATA, orbitData);
                            summon.level().addFreshEntity(projectile);
                        }
                        customData.putInt("orbitProjectileCount", spawned + spawnCount);
                    }
                }
                summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

                if(target != null) {
                    Vec3 targetDir = target.getBoundingBox().getCenter().subtract(summon.position());
                    if(targetDir.lengthSqr() > 1.0E-7D) motion = targetDir.normalize().scale(SPEED.get());
                }
            }

            Vec3 start = summon.position();
            Vec3 end = start.add(motion);
            if(!summon.level().isClientSide()) {
                List<Entity> targets = summon.level().getEntitiesOfClass(
                    Entity.class,
                    new AABB(start, end).inflate(HITBOX_INFLATE),
                    FilterUtil.createTargetFilter(summon, summon.getOwner())
                );
                if(!targets.isEmpty()) {
                    CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
                    ItemStack sourceStack = summon.getEntityData().get(StaticSummon.STACK_SOURCE);
                    if(DamageUtil.magicAttack(summon, targets.getFirst(), sourceStack, DAMAGE.get(), 0.75F, 10)) {
                        int hitCount = customData.getInt("hitCount") + 1;
                        customData.putInt("hitCount", hitCount);
                        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
                        if(hitCount >= HIT_COUNT.get()) {
                            onDied(summon);
                            return;
                        }
                    }
                }

                BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, end);
                if(blockHitResult.getType() != HitResult.Type.MISS) {
                    summon.setPos(blockHitResult.getLocation().add(Vec3.atLowerCornerOf(blockHitResult.getDirection().getNormal()).scale(0.05D)));
                    motion = bounce(motion, blockHitResult).normalize().scale(SPEED.get());
                }
            }

            summon.setDeltaMovement(motion);
            float[] xyRot = MathUtil.computeXYRot(motion.toVector3f());
            summon.setXRot(xyRot[0]);
            summon.setYRot(xyRot[1]);
            summon.xRotO = xyRot[0];
            summon.yRotO = xyRot[1];
        }

        @Override
        public void onDied(StaticSummon summon) {
            if(!(summon.level() instanceof ServerLevel serverLevel)) return;

            Vec3 pos = summon.position();
            double explosionRange = EXPLOSION_RANGE.get();
            if(explosionRange > 0.0D) {
                ItemStack sourceStack = summon.getEntityData().get(StaticSummon.STACK_SOURCE);
                for(Entity target : serverLevel.getEntitiesOfClass(
                    Entity.class,
                    AABB.ofSize(pos, explosionRange * 2.0D, explosionRange * 2.0D, explosionRange * 2.0D),
                    FilterUtil.createTargetFilter(summon, summon.getOwner())
                )) {
                    if(target.distanceToSqr(pos) > explosionRange * explosionRange) continue;
                    DamageUtil.magicAttack(summon, target, sourceStack, DAMAGE.get(), 0.75F, 10);
                }
            }
            SoundUtil.playServerSound(summon.level(), ModSounds.BOOM.get(), summon.position());
            ParticleUtil.addParticles(serverLevel, MAIN_PINK_PARTICLE, pos, new Vec3(0.3D, 0.3D, 0.3D), 0.22D, 16);
            ParticleUtil.addParticles(serverLevel, MAIN_ORANGE_RED_PARTICLE, pos, new Vec3(0.3D, 0.3D, 0.3D), 0.22D, 16);
            ParticleUtil.addParticles(serverLevel, MAIN_BLACK_PARTICLE, pos, new Vec3(0.3D, 0.3D, 0.3D), 0.22D, 16);

            Entity owner = summon.getOwner();
            if(owner != null) {
                int count = SMALL_PROJECTILE_MIN_COUNT + summon.getRandom().nextInt(SMALL_PROJECTILE_MAX_COUNT - SMALL_PROJECTILE_MIN_COUNT + 1);
                for(int i = 0; i < count; i++) {
                    Vec3 dir = new Vec3(
                        summon.getRandom().nextDouble() * 2.0D - 1.0D,
                        summon.getRandom().nextDouble() * 2.0D - 1.0D,
                        summon.getRandom().nextDouble() * 2.0D - 1.0D
                    );
                    if(dir.lengthSqr() < 1.0E-7D) dir = new Vec3(0.0D, 1.0D, 0.0D);
                    dir = dir.normalize();

                    StaticSummon small = new StaticSummon(ModEntities.STATIC_SUMMON.get(), summon.level());
                    small.setOwner(owner);
                    small.setPos(summon.position());
                    small.setDeltaMovement(dir.scale(SMALL_PROJECTILE_SPEED));
                    small.getEntityData().set(StaticSummon.STACK_SOURCE, summon.getEntityData().get(StaticSummon.STACK_SOURCE));
                    small.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.NEBULA_ARCANUM_SMALL_PROJECTILE);
                    small.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
                    small.getEntityData().set(StaticSummon.LIFETIME, SMALL_PROJECTILE_LIFETIME);
                    small.getEntityData().set(StaticSummon.RZPS, 18);
                    small.getEntityData().set(StaticSummon.SCALE_X, 0.4f);
                    small.getEntityData().set(StaticSummon.SCALE_Y, 0.4f);
                    small.getEntityData().set(StaticSummon.SCALE_Z, 0.4f);
                    small.getEntityData().set(StaticSummon.GLOW, true);
                    CompoundTag smallData = new CompoundTag();
                    smallData.putBoolean(ORANGE_RED_KEY, summon.getRandom().nextBoolean());
                    small.getEntityData().set(StaticSummon.CUSTOM_DATA, smallData);
                    small.setNoGravity(true);
                    small.noPhysics = true;
                    summon.level().addFreshEntity(small);
                }
            }
            summon.discard();
        }
    };

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR2 = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            checkBeforeTick(summon);
            if(summon.isRemoved()) return;

            if(summon.level().isClientSide()) {
                CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
                ParticleUtil.addParticle(
                    summon.level(),
                    customData.getBoolean(ORANGE_RED_KEY) ? SMALL_PROJECTILE_ORANGE_RED_PARTICLE : SMALL_PROJECTILE_PINK_PARTICLE,
                    summon.position(),
                    0.0D,
                    Vec3.ZERO,
                    0.0D
                );
            }

            Vec3 motion = summon.getDeltaMovement().add(0.0D, -SMALL_PROJECTILE_GRAVITY, 0.0D);
            Vec3 start = summon.position();
            Vec3 end = start.add(motion);
            if(!summon.level().isClientSide()) {
                List<Entity> targets = summon.level().getEntitiesOfClass(
                    Entity.class,
                    new AABB(start, end).inflate(0.3D),
                    FilterUtil.createTargetFilter(summon, summon.getOwner())
                );
                if(!targets.isEmpty()) {
                    ItemStack sourceStack = summon.getEntityData().get(StaticSummon.STACK_SOURCE);
                    if(DamageUtil.magicAttack(summon, targets.getFirst(), sourceStack, SMALL_PROJECTILE_DAMAGE.get(), 0.4F, 10)) {
                        onDied(summon);
                        return;
                    }
                }

                BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, end);
                if(blockHitResult.getType() != HitResult.Type.MISS) {
                    summon.setPos(blockHitResult.getLocation().add(Vec3.atLowerCornerOf(blockHitResult.getDirection().getNormal()).scale(0.05D)));
                    motion = bounce(motion, blockHitResult).scale(BOUNCE_SCALE);
                }
            }
            summon.setDeltaMovement(motion);
        }

        @Override
        public void onDied(StaticSummon summon) {
            if(!(summon.level() instanceof ServerLevel serverLevel)) return;
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            ParticleUtil.addParticles(
                serverLevel,
                customData.getBoolean(ORANGE_RED_KEY) ? SMALL_PROJECTILE_ORANGE_RED_PARTICLE : SMALL_PROJECTILE_PINK_PARTICLE,
                summon.position(),
                new Vec3(0.18D, 0.18D, 0.18D),
                0.18D,
                12
            );
            summon.discard();
        }
    };

    public static final IStaticProjectileBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileBehavior() {
        @Override
        public void onMoving(StaticProjectile projectile) {
            if(projectile.level().isClientSide()) {
                ParticleUtil.addParticle(
                    projectile.level(),
                    ORBIT_PROJECTILE_TRAIL_PARTICLE,
                    projectile.position(),
                    0.0D,
                    Vec3.ZERO,
                    0.0D
                );
                return;
            }

            CompoundTag customData = projectile.getEntityData().get(StaticProjectile.CUSTOM_DATA);
            if(!customData.contains(MAIN_UUID_KEY) || !(projectile.level() instanceof ServerLevel serverLevel)) {
                onDied(projectile);
                return;
            }

            Entity main = serverLevel.getEntity(customData.getUUID(MAIN_UUID_KEY));
            if(main == null || !main.isAlive()) {
                onDied(projectile);
                return;
            }
            projectile.getEntityData().set(StaticProjectile.ORIGIN, main.position().toVector3f());
        }

        @Override
        public void onHitEntity(StaticProjectile projectile, net.minecraft.world.phys.EntityHitResult result) {
            if(projectile.level().isClientSide()) return;
            ItemStack sourceStack = projectile.getEntityData().get(StaticProjectile.STACK_SOURCE);
            DamageUtil.magicAttack(projectile, result.getEntity(), sourceStack, ORBIT_PROJECTILE_DAMAGE.get(), 0.0F, 10);
        }

        @Override
        public void onHitBlock(StaticProjectile projectile, BlockHitResult result) {}
    };

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
        return computeUseTime(30, weaponStack, entity);
    }

    @Override
    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        if(level.isClientSide()) return;

        player.playNotifySound(ModSounds.MAGIC_SHOOT3.get(), SoundSource.PLAYERS, 4.0F, 1.0F);

        Vec3 direction = player.getLookAngle().normalize();
        StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), level);
        summon.setOwner(player);
        summon.setPos(player.getEyePosition().add(direction.scale(1.2D)));
        summon.setDeltaMovement(direction.scale(SPEED.get()));
        summon.getEntityData().set(StaticSummon.STACK_SOURCE, stack.copy());
        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.NEBULA_ARCANUM_PROJECTILE);
        summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
        summon.getEntityData().set(StaticSummon.LIFETIME, LIFETIME.get());
        summon.getEntityData().set(StaticSummon.GLOW, true);
        summon.setNoGravity(true);
        summon.noPhysics = true;

        float[] xyRot = MathUtil.computeXYRot(direction.toVector3f());
        summon.setXRot(xyRot[0]);
        summon.setYRot(xyRot[1]);
        summon.xRotO = xyRot[0];
        summon.yRotO = xyRot[1];
        level.addFreshEntity(summon);
    }

    private static boolean hasLineOfSight(Entity entity, Entity target) {
        return entity.level().clip(new ClipContext(
            entity.position(),
            target.getBoundingBox().getCenter(),
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            entity
        )).getType() == HitResult.Type.MISS;
    }

    private static Vec3 bounce(Vec3 motion, BlockHitResult result) {
        return switch(result.getDirection().getAxis()) {
            case X -> new Vec3(-motion.x, motion.y, motion.z);
            case Y -> new Vec3(motion.x, -motion.y, motion.z);
            case Z -> new Vec3(motion.x, motion.y, -motion.z);
        };
    }

}
