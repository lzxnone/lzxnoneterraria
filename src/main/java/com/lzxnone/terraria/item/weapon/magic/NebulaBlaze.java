package com.lzxnone.terraria.item.weapon.magic;

import com.lzxnone.terraria.effect.ManaSurgeEffect;

import com.lzxnone.terraria.effect.ModEffects;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.weapon.MagicWeapon;
import com.lzxnone.terraria.particle.CircleParticleOptions;
import com.lzxnone.terraria.particle.DustParticleOptions;
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
import net.minecraft.world.entity.HumanoidArm;
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

public class NebulaBlaze extends MagicWeapon {
    public static final Vector3f OFFSET = new Vector3f(-0.5F, -0.1F, 1.2F);
    public static final String TARGET_UUID_KEY = "target";

    public static final ConfigFloat DAMAGE = new ConfigFloat("weapon.nebula_blaze.damage", "nebula_blaze_damage", 10.0F, 0.0F, 8388600.0F);
    public static final ConfigFloat EXPLOSIVE_DAMAGE = new ConfigFloat("weapon.nebula_blaze.explosive_damage", "nebula_blaze_explosive_damage", 30.0F, 0.0F, 8388600.0F);
    public static final ConfigDouble SPEED = new ConfigDouble("weapon.nebula_blaze.speed", "nebula_blaze_speed", 1.0D, 0.0D, 24.0D);
    public static final ConfigDouble EXPLOSIVE_SPEED = new ConfigDouble("weapon.nebula_blaze.explosive_speed", "nebula_blaze_explosive_speed", 2.0D, 0.0D, 24.0D);
    public static final ConfigDouble TARGET_RANGE = new ConfigDouble("weapon.nebula_blaze.target_range", "nebula_blaze_target_range", 8.0D, 0.0D, 128.0D);
    public static final ConfigDouble EXPLOSIVE_TARGET_RANGE = new ConfigDouble("weapon.nebula_blaze.explosive_target_range", "nebula_blaze_explosive_target_range", 16.0D, 0.0D, 128.0D);
    public static final ConfigDouble EXPLOSION_RANGE = new ConfigDouble("weapon.nebula_blaze.explosion_range", "nebula_blaze_explosion_range", 3.0D, 0.0D, 64.0D);
    public static final ConfigDouble EXPLOSIVE_CHANCE = new ConfigDouble("weapon.nebula_blaze.explosive_chance", "nebula_blaze_explosive_chance", 0.2D, 0.0D, 1.0D);
    public static final ConfigDouble MANA_CONSUME = new ConfigDouble("weapon.nebula_blaze.mana_consume", "nebula_blaze_mana_consume", 12.0D, 0.0D, 10000.0D);
    public static final ConfigInt LIFETIME = new ConfigInt("weapon.nebula_blaze.lifetime", "nebula_blaze_lifetime", 100, 1, 72000);

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                DAMAGE,
                EXPLOSIVE_DAMAGE,
                SPEED,
                EXPLOSIVE_SPEED,
                TARGET_RANGE,
                EXPLOSIVE_TARGET_RANGE,
                EXPLOSION_RANGE,
                EXPLOSIVE_CHANCE,
                MANA_CONSUME,
                LIFETIME
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "nebula_blaze",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/nebula_blaze.png"),
        Component.translatable("item.lzxnoneterraria.nebula_blaze"),
        CONFIG_DATA
    );

    public static final CircleParticleOptions TRAIL_PARTICLE = new CircleParticleOptions(0.075F, 16, new Vector3f(1.0F, 0.18F, 0.72F));
    public static final CircleParticleOptions EXPLOSIVE_TRAIL_PARTICLE = new CircleParticleOptions(0.085F, 16, new Vector3f(0.24F, 0.7F, 1.0F));
    public static final DustParticleOptions DUST_PARTICLE = new DustParticleOptions(
        0.1F, 0.35F, 18, true,
        new Vector3f[] {new Vector3f(1.0F, 0.16F, 0.62F), new Vector3f(0.86F, 0.08F, 1.0F)}
    );
    public static final DustParticleOptions EXPLOSIVE_DUST_PARTICLE = new DustParticleOptions(
        0.15F, 0.35F, 18, true,
        new Vector3f[] {new Vector3f(0.12F, 0.86F, 1.0F), new Vector3f(0.22F, 0.25F, 1.0F)}
    );
    public static final DustParticleOptions DEATH_PARTICLE = new DustParticleOptions(
        0.15F, 0.45F, 32, true,
        new Vector3f[] {new Vector3f(1.0F, 0.12F, 0.58F), new Vector3f(0.95F, 0.04F, 1.0F)}
    );
    public static final DustParticleOptions EXPLOSIVE_DEATH_PARTICLE = new DustParticleOptions(
        0.3F, 0.5F, 36, true,
        new Vector3f[] {new Vector3f(0.0F, 0.95F, 1.0F), new Vector3f(0.2F, 0.35F, 1.0F)}
    );

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            checkBeforeTick(summon);
            tickProjectile(summon, SPEED.get(), TARGET_RANGE.get(), DAMAGE.get(), false);
        }

        @Override
        public void onDied(StaticSummon summon) {
            explode(summon, DAMAGE.get(), false);
        }
    };

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR2 = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            checkBeforeTick(summon);
            tickProjectile(summon, EXPLOSIVE_SPEED.get(), EXPLOSIVE_TARGET_RANGE.get(), EXPLOSIVE_DAMAGE.get(), true);
        }

        @Override
        public void onDied(StaticSummon summon) {
            explode(summon, EXPLOSIVE_DAMAGE.get(), true);
        }
    };

    public NebulaBlaze() {
        super(Tiers.DIAMOND, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public int getUseTime(ItemStack weaponStack, LivingEntity entity) {
        return computeUseTime(12, weaponStack, entity);
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
    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        if(level.isClientSide()) return;

        player.playNotifySound(ModSounds.MAGIC_SHOOT2.get(), SoundSource.PLAYERS, 4.0F, 1.0F);

        Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
        Vector3f offset = new Vector3f(
            hand == (player.getMainArm() == HumanoidArm.RIGHT ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND) ? -OFFSET.x : OFFSET.x,
            OFFSET.y,
            OFFSET.z
        );
        Vec3 eyePos = player.getEyePosition();
        Vec3 pos = new Vec3(
            eyePos.x + dirs[0].x * offset.z + dirs[1].x * offset.y + dirs[2].x * offset.x,
            eyePos.y + dirs[0].y * offset.z + dirs[1].y * offset.y + dirs[2].y * offset.x,
            eyePos.z + dirs[0].z * offset.z + dirs[1].z * offset.y + dirs[2].z * offset.x
        );
        Vec3 direction = MathUtil.toVec3(dirs[0]).normalize();

        boolean explosive = player.getRandom().nextDouble() < EXPLOSIVE_CHANCE.get();
        StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), level);
        summon.setOwner(player);
        summon.setPos(pos);
        summon.setDeltaMovement(direction.scale(explosive ? EXPLOSIVE_SPEED.get() : SPEED.get()));
        summon.getEntityData().set(StaticSummon.STACK_SOURCE, stack.copy());
        summon.getEntityData().set(StaticSummon.BEHAVIOR, explosive ? StaticSummonBehaviors.NEBULA_BLAZE_EXPLOSIVE : StaticSummonBehaviors.NEBULA_BLAZE);
        summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
        summon.getEntityData().set(StaticSummon.LIFETIME, LIFETIME.get());
        if(explosive) {
            summon.getEntityData().set(StaticSummon.SCALE_X, 2.0f);
            summon.getEntityData().set(StaticSummon.SCALE_Y, 2.0f);
            summon.getEntityData().set(StaticSummon.SCALE_Z, 2.0f);
        }else {
            summon.getEntityData().set(StaticSummon.SCALE_X, 1.5f);
            summon.getEntityData().set(StaticSummon.SCALE_Y, 1.5f);
            summon.getEntityData().set(StaticSummon.SCALE_Z, 1.5f);
        }
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

    private static void tickProjectile(StaticSummon summon, double speed, double targetRange, float damage, boolean explosive) {
        Vec3 direction = summon.getDeltaMovement();
        if(direction.lengthSqr() < 1.0E-7D) direction = summon.getLookAngle();
        if(direction.lengthSqr() < 1.0E-7D) direction = new Vec3(0.0D, 0.0D, 1.0D);

        if(summon.level() instanceof ServerLevel serverLevel) {
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            Entity target = null;
            if(customData.contains(TARGET_UUID_KEY)) {
                target = serverLevel.getEntity(customData.getUUID(TARGET_UUID_KEY));
                if(target == null || !target.isAlive()) {
                    target = null;
                    customData.remove(TARGET_UUID_KEY);
                }else if(serverLevel.clip(new ClipContext(
                    summon.position(),
                    target.getBoundingBox().getCenter(),
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    summon
                )).getType() != HitResult.Type.MISS) {
                    target = null;
                    customData.remove(TARGET_UUID_KEY);
                }
            }
            if(target == null) {
                List<Entity> targets = SearchUtil.searchEnemies(summon, summon.getOwner(), summon.getBoundingBox().inflate(targetRange));
                double bestDistance = Double.MAX_VALUE;
                for(Entity candidate : targets) {
                    Vec3 targetCenter = candidate.getBoundingBox().getCenter();
                    if(serverLevel.clip(new ClipContext(
                        summon.position(),
                        targetCenter,
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        summon
                    )).getType() != HitResult.Type.MISS) continue;

                    double distance = targetCenter.distanceToSqr(summon.position());
                    if(distance < bestDistance) {
                        bestDistance = distance;
                        target = candidate;
                    }
                }
                if(target != null) customData.putUUID(TARGET_UUID_KEY, target.getUUID());
            }
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

            if(target != null) {
                Vec3 targetDir = target.getBoundingBox().getCenter().subtract(summon.position());
                if(targetDir.lengthSqr() > 1.0E-7D) direction = targetDir.normalize();
            }
        }

        Vec3 motion = direction.normalize().scale(speed);
        Vec3 start = summon.position();
        Vec3 end = start.add(motion);
        summon.setDeltaMovement(motion);

        float[] xyRot = MathUtil.computeXYRot(direction.toVector3f());
        summon.setXRot(xyRot[0]);
        summon.setYRot(xyRot[1]);
        summon.xRotO = xyRot[0];
        summon.yRotO = xyRot[1];

        if(summon.level().isClientSide()) return;
        addTrailParticles(summon, direction, explosive);

        AABB hitBox = new AABB(start, end).inflate(0.35);
        List<Entity> hitTargets = summon.level().getEntitiesOfClass(
            Entity.class,
            hitBox,
            FilterUtil.createTargetFilter(summon, summon.getOwner())
        );
        if(!hitTargets.isEmpty()) {
            explode(summon, damage, explosive);
            return;
        }

        BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, end);
        if(blockHitResult.getType() != HitResult.Type.MISS) {
            summon.setPos(blockHitResult.getLocation());
            summon.setDeltaMovement(Vec3.ZERO);
            explode(summon, damage, explosive);
        }
    }

    private static void explode(StaticSummon summon, float damage, boolean explosive) {
        if(!(summon.level() instanceof ServerLevel serverLevel)) return;

        Vec3 pos = summon.position();
        ParticleUtil.addParticles(
            serverLevel,
            explosive ? EXPLOSIVE_DEATH_PARTICLE : DEATH_PARTICLE,
            pos,
            new Vec3(explosive ? 0.75D : 0.55D, explosive ? 0.75D : 0.55D, explosive ? 0.75D : 0.55D),
            0.03D,
            explosive ? 28 : 20
        );
        SoundUtil.playServerSound(summon.level(), ModSounds.BOOM.get(), summon.position());
        double explosionRange = EXPLOSION_RANGE.get();
        if(explosionRange > 0.0D) {
            ItemStack sourceStack = summon.getEntityData().get(StaticSummon.STACK_SOURCE);
            for(Entity target : serverLevel.getEntitiesOfClass(
                Entity.class,
                AABB.ofSize(pos, explosionRange * 2.0D, explosionRange * 2.0D, explosionRange * 2.0D),
                FilterUtil.createTargetFilter(summon, summon.getOwner())
            )) {
                if(target.distanceToSqr(pos) > explosionRange * explosionRange) continue;
                if(DamageUtil.magicAttack(summon, target, sourceStack, damage, 0.4F, 10)) {
                }
            }
        }

        summon.discard();
    }

    private static void addTrailParticles(StaticSummon summon, Vec3 direction, boolean explosive) {
        if(!(summon.level() instanceof ServerLevel serverLevel)) return;

        Vector3f[] dirs = MathUtil.computeCoordinateSystem(direction.toVector3f(), summon.getYRot());
        Vec3 up = MathUtil.toVec3(dirs[1]).normalize();
        Vec3 right = MathUtil.toVec3(dirs[2]).normalize();
        double phase = summon.getEntityData().get(StaticSummon.AGE) * 0.75D;
        double radius = explosive ? 0.5D : 0.35D;

        for(int i = 0; i < 2; i++) {
            double angle = phase + Math.PI * i;
            Vec3 particlePos = summon.position()
                .add(up.scale(Math.cos(angle) * radius))
                .add(right.scale(Math.sin(angle) * radius));
            ParticleUtil.addParticles(
                serverLevel,
                explosive ? EXPLOSIVE_TRAIL_PARTICLE : TRAIL_PARTICLE,
                particlePos,
                Vec3.ZERO,
                0.0D,
                1
            );
        }

        ParticleUtil.addParticles(
            serverLevel,
            explosive ? EXPLOSIVE_DUST_PARTICLE : DUST_PARTICLE,
            summon.position(),
            new Vec3(Math.random(), Math.random(), Math.random()),
            0.2D,
            1
        );
    }
}
