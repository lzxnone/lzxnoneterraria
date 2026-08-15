package com.lzxnone.terraria.item.weapon.summon.minion;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.IStaticProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.particle.CircleParticleOptions;
import com.lzxnone.terraria.particle.DustParticleOptions;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class StardustCellStaff extends MinionWeapon {
    public static final ConfigDouble TARGET_RANGE = new ConfigDouble(
        "weapon.stardust_cell_staff.target_range", "stardust_cell_staff_target_range", 64.0D, 1.0D, 256.0D
    );
    public static final ConfigDouble SHOOT_RANGE = new ConfigDouble(
        "weapon.stardust_cell_staff.shoot_range", "stardust_cell_staff_shoot_range", 12.0D, 1.0D, 256.0D
    );
    public static final ConfigFloat DAMAGE = new ConfigFloat(
        "weapon.stardust_cell_staff.damage", "stardust_cell_staff_damage", 5.0F, 0.0F, 8388600.0F
    );
    public static final ConfigInt CELLED_DURATION = new ConfigInt(
        "weapon.stardust_cell_staff.celled_duration", "stardust_cell_staff_celled_duration", 100, 1, 72000
    );

    public static final ConfigDouble MIN_SHOOT_DISTANCE = new ConfigDouble(
        "weapon.stardust_cell_staff.min_shoot_distance", "stardust_cell_staff_min_shoot_distance", 8.0D, 0.1D, 256.0D
    );
    public static final ConfigDouble PROJECTILE_SPEED = new ConfigDouble(
        "weapon.stardust_cell_staff.projectile_speed", "stardust_cell_staff_projectile_speed", 1.5D, 0.01D, 32.0D
    );
    public static final ConfigInt PROJECTILE_LIFETIME = new ConfigInt(
        "weapon.stardust_cell_staff.projectile_lifetime", "stardust_cell_staff_projectile_lifetime", 100, 1, 72000
    );

    public static final ConfigDouble WANDER_SPEED = new ConfigDouble(
        "weapon.stardust_cell_staff.wander_speed", "stardust_cell_staff_wander_speed", 0.6D, 0.01D, 32.0D
    );
    public static final ConfigDouble CHASE_SPEED = new ConfigDouble(
        "weapon.stardust_cell_staff.chase_speed", "stardust_cell_staff_chase_speed", 1.0D, 0.01D, 32.0D
    );
    public static final ConfigDouble BACK_AWAY_SPEED = new ConfigDouble(
        "weapon.stardust_cell_staff.back_away_speed", "stardust_cell_staff_back_away_speed", 0.45D, 0.01D, 32.0D
    );
    public static final ConfigDouble RECOIL_SPEED = new ConfigDouble(
        "weapon.stardust_cell_staff.recoil_speed", "stardust_cell_staff_recoil_speed", 0.5D, 0.01D, 32.0D
    );
    public static final ConfigDouble PRE_SHOOT_FRICTION = new ConfigDouble(
        "weapon.stardust_cell_staff.pre_shoot_friction", "stardust_cell_staff_pre_shoot_friction", 0.35D, 0.0D, 1.0D
    );
    public static final ConfigDouble RECOIL_BRAKE = new ConfigDouble(
        "weapon.stardust_cell_staff.recoil_brake", "stardust_cell_staff_recoil_brake", 0.12D, 0.0D, 1.0D
    );

    private static final double WANDER_RADIUS = 12.0D;
    private static final double WANDER_TELEPORT_DISTANCE = WANDER_RADIUS * 1.5D;
    private static final int TELEPORT_CHASE_TICKS = 20;
    private static final double TELEPORT_RADIUS = 4.0D;
    private static final float ROTATION_LERP = 0.25F;

    private static final int TELEPORT_PARTICLE_COUNT = 100;
    private static final DustParticleOptions TELEPORT_PARTICLE = new DustParticleOptions(
        0.05f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(0.0F, 0.5F, 1.0F),
            new Vector3f(0.0F, 1.0F, 1.0F),
        }
    );

    public static final CircleParticleOptions CELL_CIRCLE_BLUE = new CircleParticleOptions(0.08F, 40, new Vector3f(0.0F, 0.5F, 1.0F));
    public static final CircleParticleOptions CELL_CIRCLE_CYAN = new CircleParticleOptions(0.08F, 40, new Vector3f(0.0F, 1.0F, 1.0F));

    public enum State {
        IDLE,
        WANDER,
        FIGHT,
        CHASE,
        PRE_SHOOT,
        SHOOT,
        DONE
    }

    public StardustCellStaff() {
        super(Tiers.NETHERITE, new Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC));
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                TARGET_RANGE,
                SHOOT_RANGE,
                DAMAGE,
                CELLED_DURATION,
                MIN_SHOOT_DISTANCE,
                PROJECTILE_SPEED,
                PROJECTILE_LIFETIME,
                WANDER_SPEED,
                CHASE_SPEED,
                BACK_AWAY_SPEED,
                RECOIL_SPEED,
                PRE_SHOOT_FRICTION,
                RECOIL_BRAKE
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "stardust_cell_staff",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/stardust_cell_staff.png"),
        Component.translatable("item.lzxnoneterraria.stardust_cell_staff"),
        CONFIG_DATA
    );

    @Override
    public float getTooltipDamage(ItemStack weaponStack, LivingEntity entity) {
        float damage = entity instanceof Player player
            ? DamageUtil.applyPlayerDamageEffects(player, DAMAGE.get())
            : DAMAGE.get();
        return applySummonDamageBonus(weaponStack, entity, damage);
    }

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            checkBeforeTick(summon);
            Entity owner = summon.getOwner();
            if(owner != null && owner.position().subtract(summon.position()).length() > MinionWeapon.MAX_RANGE) {
                summon.setPos(owner.getBoundingBox().getCenter());
                summon.setDeltaMovement(Vec3.ZERO);
            }
            if(!summon.level().isClientSide() && owner instanceof Player player && summon.isAlive()) {
                onState(summon, player);
            }
        }
    };

    public static void onState(StaticSummon summon, Player player) {
        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA).copy();
        int state = customData.contains("state") ? customData.getInt("state") : State.IDLE.ordinal();

        if(state == State.IDLE.ordinal()) {
            storeSummonTarget(summon, findSummonTarget(summon, player, TARGET_RANGE.get()));
            if(getStoredSummonTarget(summon) == null) changeState(summon, State.WANDER);
            else changeState(summon, State.FIGHT);
            return;
        }

        if(state == State.WANDER.ordinal()) {
            storeSummonTarget(summon, findSummonTarget(summon, player, TARGET_RANGE.get()));
            if(getStoredSummonTarget(summon) != null) {
                changeState(summon, State.FIGHT);
                return;
            }

            customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA).copy();
            if(!customData.contains("wanderX") || !customData.contains("wanderY") || !customData.contains("wanderZ")) {
                changeState(summon, State.IDLE);
                return;
            }
            Vec3 wanderPos = new Vec3(customData.getDouble("wanderX"), customData.getDouble("wanderY"), customData.getDouble("wanderZ"));
            Vec3 distance = wanderPos.subtract(summon.position());
            customData.putInt("wanderTicks", customData.getInt("wanderTicks") + 1);
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
            if(distance.length() > WANDER_TELEPORT_DISTANCE && customData.getInt("wanderTicks") > TELEPORT_CHASE_TICKS) {
                teleportSummon(summon, wanderPos, State.IDLE);
                return;
            }
            if(distance.length() > 0.25D) {
                summon.setDeltaMovement(distance.normalize().scale(Math.min(WANDER_SPEED.get(), distance.length())));
                float[] xyRot = MathUtil.computeXYRot(summon.getDeltaMovement().toVector3f());
                summon.setXRot(Mth.rotLerp(ROTATION_LERP, summon.getXRot(), xyRot[0]));
                summon.setYRot(Mth.rotLerp(ROTATION_LERP, summon.getYRot(), xyRot[1]));
            }else {
                summon.setDeltaMovement(summon.getDeltaMovement().scale(0.35D));
                if(summon.getDeltaMovement().length() < 0.05D) {
                    summon.setDeltaMovement(Vec3.ZERO);
                    changeState(summon, State.IDLE);
                }
            }
            return;
        }

        if(state == State.FIGHT.ordinal()) {
            storeSummonTarget(summon, findSummonTarget(summon, player, TARGET_RANGE.get()));
            Entity target = getStoredSummonTarget(summon);
            if(target == null) {
                changeState(summon, State.IDLE);
                return;
            }

            Vec3 targetDirection = target.getBoundingBox().getCenter().subtract(summon.position());
            float[] xyRot = MathUtil.computeXYRot(targetDirection.toVector3f());
            summon.setXRot(Mth.rotLerp(ROTATION_LERP, summon.getXRot(), xyRot[0]));
            summon.setYRot(Mth.rotLerp(ROTATION_LERP, summon.getYRot(), xyRot[1]));
            if(targetDirection.length() > SHOOT_RANGE.get()) changeState(summon, State.CHASE);
            else changeState(summon, State.PRE_SHOOT);
            return;
        }

        if(state == State.CHASE.ordinal()) {
            Entity target = getStoredSummonTarget(summon);
            if(target == null || !target.isAlive()
                || target.distanceToSqr(summon) > TARGET_RANGE.get() * TARGET_RANGE.get()
                || !FilterUtil.createTargetFilter(summon, player).test(target)) {
                changeState(summon, State.FIGHT);
                return;
            }

            Vec3 targetDirection = target.getBoundingBox().getCenter().subtract(summon.position());
            if(targetDirection.length() <= SHOOT_RANGE.get()) {
                changeState(summon, State.FIGHT);
                return;
            }

            summon.setDeltaMovement(targetDirection.normalize().scale(CHASE_SPEED.get()));
            float[] xyRot = MathUtil.computeXYRot(summon.getDeltaMovement().toVector3f());
            summon.setXRot(Mth.rotLerp(ROTATION_LERP, summon.getXRot(), xyRot[0]));
            summon.setYRot(Mth.rotLerp(ROTATION_LERP, summon.getYRot(), xyRot[1]));

            customData.putInt("chaseTicks", customData.getInt("chaseTicks") + 1);
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
            if(customData.getInt("chaseTicks") > TELEPORT_CHASE_TICKS) {
                teleportSummon(summon, target.getBoundingBox().getCenter(), State.FIGHT);
            }
            return;
        }

        if(state == State.PRE_SHOOT.ordinal()) {
            Entity target = getStoredSummonTarget(summon);
            if(target == null || !target.isAlive() || !FilterUtil.createTargetFilter(summon, player).test(target)) {
                changeState(summon, State.FIGHT);
                return;
            }

            Vec3 targetDirection = target.getBoundingBox().getCenter().subtract(summon.position());
            double distance = targetDirection.length();
            if(distance > SHOOT_RANGE.get()) {
                changeState(summon, State.FIGHT);
            }else if(distance < MIN_SHOOT_DISTANCE.get()) {
                summon.setDeltaMovement(targetDirection.normalize().scale(-BACK_AWAY_SPEED.get()));
            }else {
                summon.setDeltaMovement(summon.getDeltaMovement().scale(PRE_SHOOT_FRICTION.get()));
                if(summon.getDeltaMovement().length() < 0.05D) {
                    summon.setDeltaMovement(Vec3.ZERO);
                    changeState(summon, State.SHOOT);
                }
            }
            return;
        }

        if(state == State.SHOOT.ordinal()) {
            Entity target = getStoredSummonTarget(summon);
            if(target == null || !target.isAlive() || !FilterUtil.createTargetFilter(summon, player).test(target)) {
                changeState(summon, State.FIGHT);
                return;
            }

            Vec3 direction = target.getBoundingBox().getCenter().subtract(summon.position()).normalize();
            Vector3f[] directions = MathUtil.computeCoordinateSystem(direction.toVector3f(), summon.getYRot());
            StaticProjectile projectile = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), summon.level());
            projectile.setOwner(player);
            projectile.setPos(summon.position());
            projectile.getEntityData().set(StaticProjectile.STACK_SOURCE, summon.getEntityData().get(StaticSummon.STACK_SOURCE).copy());
            projectile.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.STARDUST_CELL_PROJECTILE);
            projectile.getEntityData().set(StaticProjectile.RENDER_MODE, "custom");
            projectile.getEntityData().set(StaticProjectile.ORIGIN, summon.position().toVector3f());
            projectile.getEntityData().set(StaticProjectile.DIRECTION, directions[0]);
            projectile.getEntityData().set(StaticProjectile.UP, directions[1]);
            projectile.getEntityData().set(StaticProjectile.RIGHT, directions[2]);
            projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, PROJECTILE_SPEED.get() + "*t");
            projectile.getEntityData().set(StaticProjectile.LIFETIME, PROJECTILE_LIFETIME.get());
            projectile.getEntityData().set(StaticProjectile.SCALE_X, 0.5F);
            projectile.getEntityData().set(StaticProjectile.SCALE_Y, 0.5F);
            projectile.getEntityData().set(StaticProjectile.SCALE_Z, 0.5F);
            projectile.getEntityData().set(StaticProjectile.GLOW, true);
            projectile.setDeltaMovement(direction.scale(PROJECTILE_SPEED.get()));
            summon.level().addFreshEntity(projectile);

            ParticleUtil.addParticles((ServerLevel) summon.level(), CELL_CIRCLE_BLUE, summon.position(), Vec3.ZERO, 0.2D, 12);
            ParticleUtil.addParticles((ServerLevel) summon.level(), CELL_CIRCLE_CYAN, summon.position(), Vec3.ZERO, 0.2D, 12);

            customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA).copy();
            customData.putInt("state", State.DONE.ordinal());
            customData.putDouble("shootDirX", direction.x);
            customData.putDouble("shootDirY", direction.y);
            customData.putDouble("shootDirZ", direction.z);
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
            summon.setDeltaMovement(direction.scale(-RECOIL_SPEED.get()));
            return;
        }

        if(state == State.DONE.ordinal()) {
            Vec3 shootDirection = new Vec3(
                customData.getDouble("shootDirX"),
                customData.getDouble("shootDirY"),
                customData.getDouble("shootDirZ")
            );
            Vec3 velocity = summon.getDeltaMovement();
            if(shootDirection.lengthSqr() < 1.0E-6D || velocity.length() < 0.05D || velocity.dot(shootDirection) >= -0.05D) {
                summon.setDeltaMovement(Vec3.ZERO);
                changeState(summon, State.FIGHT);
            }else {
                summon.setDeltaMovement(velocity.add(shootDirection.normalize().scale(RECOIL_BRAKE.get())));
            }
        }
    }

    private static void teleportSummon(StaticSummon summon, Vec3 targetCenter, State nextState) {
        Vec3 from = summon.position();
        Vec3 to = MathUtil.getRandomPosInRadius(targetCenter, TELEPORT_RADIUS);
        SoundUtil.playServerSound(summon.level(), SoundEvents.ENDERMAN_TELEPORT, from);
        summon.setPos(to);
        summon.setDeltaMovement(Vec3.ZERO);
        for(int i = 0; i < TELEPORT_PARTICLE_COUNT; i++) {
            Vec3 pos = from.lerp(to, i / (double) (TELEPORT_PARTICLE_COUNT - 1));
            ParticleUtil.addParticles((ServerLevel) summon.level(), TELEPORT_PARTICLE, pos, Vec3.ZERO, 0.0D, 1);
        }
        ParticleUtil.addParticles((ServerLevel) summon.level(), CELL_CIRCLE_BLUE, from, Vec3.ZERO, 0.2D, 12);
        ParticleUtil.addParticles((ServerLevel) summon.level(), CELL_CIRCLE_CYAN, from, Vec3.ZERO, 0.2D, 12);
        ParticleUtil.addParticles((ServerLevel) summon.level(), CELL_CIRCLE_BLUE, to, Vec3.ZERO, 0.2D, 12);
        ParticleUtil.addParticles((ServerLevel) summon.level(), CELL_CIRCLE_CYAN, to, Vec3.ZERO, 0.2D, 12);
        changeState(summon, nextState);
    }

    public static void changeState(StaticSummon summon, State state) {
        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA).copy();
        customData.putInt("state", state.ordinal());
        if(state == State.IDLE) {
            summon.setDeltaMovement(Vec3.ZERO);
        }else if(state == State.WANDER && summon.getOwner() != null) {
            Vec3 wanderPos = MathUtil.getRandomPosInRadius(summon.getOwner().getBoundingBox().getCenter(), WANDER_RADIUS);
            customData.putDouble("wanderX", wanderPos.x);
            customData.putDouble("wanderY", wanderPos.y);
            customData.putDouble("wanderZ", wanderPos.z);
            customData.putInt("wanderTicks", 0);
        }else if(state == State.CHASE) {
            customData.putInt("chaseTicks", 0);
        }
        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
    }

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR2 = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            Entity target = summon.getOwner();
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            if(target == null || !target.isAlive()
                || !customData.contains("offsetX") || !customData.contains("offsetY") || !customData.contains("offsetZ")) {
                onDied(summon);
                return;
            }
            Vec3 position = target.getBoundingBox().getCenter().add(
                customData.getDouble("offsetX"),
                customData.getDouble("offsetY"),
                customData.getDouble("offsetZ")
            );
            summon.setPos(position);
        }
    };

    public static final IStaticProjectileBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileBehavior() {
        @Override
        public void onMoving(StaticProjectile projectile) {
            if(projectile.level().isClientSide() || projectile.prevPos == null || !projectile.isAlive()) return;
            EntityHitResult entityHit = CollisionUtil.checkEntityHit(projectile, projectile.prevPos, projectile.position(), 0.35D);
            if(entityHit != null) {
                onHitEntity(projectile, entityHit);
            }
        }

        @Override
        public void onHitEntity(StaticProjectile projectile, EntityHitResult result) {
            if(projectile.level().isClientSide()) return;
            Entity target = result.getEntity();
            Entity owner = projectile.getOwner();
            if(owner == null) return;
            if(!FilterUtil.createTargetFilter(owner).test(target) || !(owner instanceof Player)) return;
            if(DamageUtil.summonAttack(projectile, target, projectile.getEntityData().get(StaticProjectile.STACK_SOURCE), (float) DAMAGE.get(), 0.5f, 10)) {
                if(target instanceof LivingEntity livingEntity) {
                    MobEffectInstance effectInstance = new MobEffectInstance(ModEffects.CELLED, CELLED_DURATION.get(), 0);
                    livingEntity.addEffect(effectInstance);
                }
                Vec3 pos = projectile.position();
                Vec3 offset = pos.subtract(target.getBoundingBox().getCenter());
                StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), projectile.level());
                summon.setOwner(target);
                summon.setPos(pos.add(offset));
                summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.STARDUST_CELL_STUCK_PROJECTILE);
                summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
                summon.getEntityData().set(StaticSummon.LIFETIME, CELLED_DURATION.get());
                summon.getEntityData().set(StaticSummon.SCALE_X, 0.5F);
                summon.getEntityData().set(StaticSummon.SCALE_Y, 0.5F);
                summon.getEntityData().set(StaticSummon.SCALE_Z, 0.5F);
                summon.getEntityData().set(StaticSummon.GLOW, true);

                CompoundTag customData = new CompoundTag();
                customData.putDouble("offsetX", offset.x);
                customData.putDouble("offsetY", offset.y);
                customData.putDouble("offsetZ", offset.z);
                summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

                summon.setNoGravity(true);
                summon.noPhysics = true;

                projectile.level().addFreshEntity(summon);
                ParticleUtil.addParticles((ServerLevel) projectile.level(), CELL_CIRCLE_BLUE, target.getBoundingBox().getCenter(), Vec3.ZERO, 0.2D, 12);
                ParticleUtil.addParticles((ServerLevel) projectile.level(), CELL_CIRCLE_CYAN, target.getBoundingBox().getCenter(), Vec3.ZERO, 0.2D, 12);
                projectile.discard();
            }
        }
    };

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(stack.is(ModItems.STARDUST_CELL_STAFF.get()) && level instanceof ServerLevel) {
            StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), level);
            summon.setOwner(player);
            summon.setPos(player.getBoundingBox().getCenter());
            summon.getEntityData().set(StaticSummon.STACK_SOURCE, stack.copy());
            summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.STARDUST_CELL_STAFF);
            summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
            summon.getEntityData().set(StaticSummon.LIFETIME, StaticSummon.INFINITE_LIFETIME);
            summon.getEntityData().set(StaticSummon.SCALE_X, 1.0F);
            summon.getEntityData().set(StaticSummon.SCALE_Y, 1.0F);
            summon.getEntityData().set(StaticSummon.SCALE_Z, 1.0F);
            summon.getEntityData().set(StaticSummon.GLOW, true);
            CompoundTag customData = new CompoundTag();
            customData.putInt("state", State.IDLE.ordinal());
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
            summon.setNoGravity(true);
            summon.noPhysics = true;
            MinionWeapon.addFreshSummon(player, StaticSummonBehaviors.STARDUST_CELL_STAFF, List.of(summon));
        }
        SoundUtil.playClientSound(player, ModSounds.SUMMON.get());
        return InteractionResultHolder.consume(stack);
    }
}
