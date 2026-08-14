package com.lzxnone.terraria.item.weapon.magic;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.item.ammo.Ammo;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.item.weapon.MagicWeapon;
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
import com.lzxnone.terraria.utils.ParticleUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class BetsyWrath extends MagicWeapon {
    public static final double SIDE_ANGLE = 5.0D;
    public static final double SPEED = 2.4D;
    public static final double GRAVITY = 0.05D;
    public static final double MAX_GRAVITY = 0.22D;
    public static final double MIN_TICKS_TO_TARGET = 8.0D;
    public static final double MIN_ARC_HEIGHT = 1.25D;
    public static final double MAX_ARC_HEIGHT = 6.0D;
    public static final double ARC_HEIGHT_PER_BLOCK = 0.12D;
    public static final double HITBOX_INFLATE = 0.5D;

    public static final ConfigFloat DAMAGE = new ConfigFloat("weapon.betsys_wrath.damage", "betsys_wrath_damage", 10.0F, 0.0F, 8388600.0F);
    public static final ConfigDouble RANGE = new ConfigDouble("weapon.betsys_wrath.range", "betsys_wrath_range", 64.0D, 0.0D, 512.0D);
    public static final ConfigDouble EXPLOSION_RANGE = new ConfigDouble("weapon.betsys_wrath.explosion_range", "betsys_wrath_explosion_range", 3.5D, 0.0D, 64.0D);
    public static final ConfigDouble MANA_CONSUME = new ConfigDouble("weapon.betsys_wrath.mana_consume", "betsys_wrath_mana_consume", 14.0D, 0.0D, 10000.0D);
    public static final ConfigInt BUFF_DURATION = new ConfigInt("weapon.betsys_wrath.buff_duration", "betsys_wrath_buff_duration", 200, 0, 72000);

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DAMAGE, RANGE, EXPLOSION_RANGE, MANA_CONSUME, BUFF_DURATION);
        }
    };
    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "betsys_wrath",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/betsys_wrath.png"),
        Component.translatable("item.lzxnoneterraria.betsys_wrath"),
        CONFIG_DATA
    );

    //黄
    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.08f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(1.0F, 1.0F, 0.6F),
            new Vector3f(1.0F, 0.9F, 0.0F),
            new Vector3f(1.0F, 0.8F, 0.5F),
            new Vector3f(1.0F, 0.5F, 0.0F),
        }
    );

    //红
    public static final DustParticleOptions PARTICLE2 = new DustParticleOptions(
        0.08f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(0.75F, 0.0F, 0.0F),
            new Vector3f(0.55F, 0.0F, 0.0F),
            new Vector3f(0.35F, 0.0F, 0.0F),
            new Vector3f(0.2F, 0.0F, 0.0F),
        }
    );

    //紫
    public static final DustParticleOptions PARTICLE3 = new DustParticleOptions(
        0.08f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(0.85F, 0.25F, 1.0F),
            new Vector3f(0.6F, 0.0F, 0.85F),
            new Vector3f(0.35F, 0.0F, 0.55F),
            new Vector3f(0.2F, 0.0F, 0.35F),
        }
    );

    @Override
    public float getTooltipDamage(ItemStack weaponStack, LivingEntity entity) {
        float damage = entity instanceof Player player ? DamageUtil.applyPlayerDamageEffects(player, DAMAGE.get()) : DAMAGE.get();
        return applyMagicDamageBonus(weaponStack, entity, damage);
    }

    @Override
    protected double getManaConsumeRate(ItemStack stack, LivingEntity entity) {
        return MANA_CONSUME.get();
    }

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            checkBeforeTick(summon);

            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            double gravity = customData.contains("gravity") ? customData.getDouble("gravity") : GRAVITY;
            Vec3 motion = summon.getDeltaMovement().add(0.0D, -gravity, 0.0D);
            if(motion.lengthSqr() < 1.0E-7D) {
                onDied(summon);
                return;
            }

            Vec3 nextPos = summon.position().add(motion);

            float[] xyRot = MathUtil.computeXYRot(motion.toVector3f());
            summon.setXRot(xyRot[0]);
            summon.setYRot(xyRot[1]);

            if(summon.level().isClientSide()) {
                summon.setDeltaMovement(motion);
                Vector3f[] dirs = MathUtil.computeCoordinateSystem(motion.toVector3f(), 0);
                Vec3 right = MathUtil.toVec3(dirs[2]).normalize();
                summon.trailPositions.addFirst(summon.position().add(right.scale(0.25)));
                summon.trailPositions.addFirst(summon.position().add(right.scale(-0.25)));
                while(summon.trailPositions.size() > 12) summon.trailPositions.removeLast();
                ParticleUtil.addParticles(
                    summon.level(), PARTICLE,
                    summon.position(), 2,
                    new Vec3(0, 0, 0), 0.25,
                    3
                );
                ParticleUtil.addParticles(
                    summon.level(), PARTICLE2,
                    summon.position(), 2,
                    new Vec3(0, 0, 0), 0.25,
                    3
                );
                return;
            }

            EntityHitResult entityHitResult = CollisionUtil.checkEntityHit(summon, nextPos, HITBOX_INFLATE);
            if(entityHitResult != null) {
                if(summon.level() instanceof ServerLevel serverLevel) {
                    ParticleUtil.addParticles(
                        serverLevel, PARTICLE3,
                        summon.position(), new Vec3(0.0D, 0.0D, 0.0D),
                        0.4D, 25
                    );
                }
                onDied(summon);
                return;
            }

            BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, nextPos);
            if(blockHitResult.getType() != HitResult.Type.MISS) {
                onDied(summon);
                return;
            }

            summon.setDeltaMovement(motion);
        }

        @Override
        public AABB getBoundingBoxForCulling(StaticSummon summon) {
            return summon.getBoundingBox().inflate(8.0D);
        }

        @Override
        public void onDied(StaticSummon summon) {
            if(!(summon.level() instanceof ServerLevel serverLevel)) return;

            Vec3 pos = summon.position();

            SoundUtil.playServerSound(serverLevel, ModSounds.BETSYS_FIREBALL_COLLIDE.get(), pos);
            ParticleUtil.addParticles(
                serverLevel, ParticleTypes.EXPLOSION,
                pos, new Vec3(0.0D, 0.0D, 0.0D),
                0.0D, 1
            );
            ParticleUtil.addParticles(
                serverLevel, PARTICLE,
                pos, new Vec3(0.0D, 0.0D, 0.0D),
                0.4D, 25
            );
            ParticleUtil.addParticles(
                serverLevel, PARTICLE2,
                pos, new Vec3(0.0D, 0.0D, 0.0D),
                0.4D, 25
            );

            double explosionRange = EXPLOSION_RANGE.get();
            if(explosionRange > 0.0D) {
                ItemStack sourceStack = summon.getEntityData().get(StaticSummon.STACK_SOURCE);
                for(Entity target : serverLevel.getEntitiesOfClass(
                    Entity.class,
                    AABB.ofSize(pos, explosionRange * 2.0D, explosionRange * 2.0D, explosionRange * 2.0D),
                    FilterUtil.createTargetFilter(summon, summon.getOwner())
                )) {
                    if(target.distanceToSqr(pos) > explosionRange * explosionRange) continue;
                    if(DamageUtil.magicAttack(summon, target, sourceStack, DAMAGE.get(), 0.6F, 10)) {
                        if(BUFF_DURATION.get() > 0 && target instanceof LivingEntity livingTarget) {
                            livingTarget.addEffect(new MobEffectInstance(ModEffects.BETSYS_CURSE, BUFF_DURATION.get(), 0));
                        }
                    }
                }
            }

            summon.discard();
        }
    };

    public BetsyWrath() {
        super(Tiers.DIAMOND, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public int getUseTime(ItemStack weaponStack, LivingEntity entity) {
        return 20;
    }

    @Override
    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        if(level.isClientSide()) return;
        player.playNotifySound(ModSounds.BETSYS_FIREBALL_SHOOT.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
        if(level instanceof ServerLevel serverLevel) {
            ParticleUtil.addParticles(
                serverLevel, PARTICLE,
                player.getBoundingBox().getCenter(), new Vec3(0, 0, 0), 0.25,
                25
            );
        }
        Vec3 start = player.getBoundingBox().getCenter();
        Vec3 target = MathUtil.getCrosshairPos(player, level, RANGE.get());
        double[] angles = new double[]{SIDE_ANGLE, 0.0D, -SIDE_ANGLE};
        for(int i = 0; i < angles.length; i++) {
            Vec3 offset = target.subtract(start);
            double radians = Math.toRadians(angles[i]);
            double cos = Math.cos(radians);
            double sin = Math.sin(radians);
            Vec3 angledTarget = start.add(
                offset.x * cos - offset.z * sin,
                offset.y,
                offset.x * sin + offset.z * cos
            );
            Vec3 toTarget = angledTarget.subtract(start);
            double horizontalDistance = toTarget.horizontalDistance();
            double ticksToTarget = Math.max(MIN_TICKS_TO_TARGET, horizontalDistance / SPEED);
            double arcHeight = Math.min(MAX_ARC_HEIGHT, Math.max(MIN_ARC_HEIGHT, horizontalDistance * ARC_HEIGHT_PER_BLOCK));
            double gravity = Math.min(MAX_GRAVITY, Math.max(GRAVITY, 8.0D * arcHeight / (ticksToTarget * ticksToTarget)));
            Vec3 initialMovement = new Vec3(
                toTarget.x / ticksToTarget,
                (toTarget.y + gravity * ticksToTarget * (ticksToTarget + 1.0D) * 0.5D) / ticksToTarget,
                toTarget.z / ticksToTarget
            );

            StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), level);
            summon.setOwner(player);
            summon.setPos(start);
            summon.setDeltaMovement(initialMovement);
            summon.getEntityData().set(StaticSummon.STACK_SOURCE, stack.copy());
            summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.BETSY_WRATH);
            summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
            summon.getEntityData().set(StaticSummon.LIFETIME, 1200);
            summon.getEntityData().set(StaticSummon.GLOW, true);
            summon.setNoGravity(true);
            summon.noPhysics = true;

            float[] initialXyRot = MathUtil.computeXYRot(initialMovement.toVector3f());
            summon.setXRot(initialXyRot[0]);
            summon.setYRot(initialXyRot[1]);
            summon.xRotO = summon.getXRot();
            summon.yRotO = summon.getYRot();

            CompoundTag customData = new CompoundTag();
            customData.putDouble("gravity", gravity);
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
            level.addFreshEntity(summon);
        }
    }
}
