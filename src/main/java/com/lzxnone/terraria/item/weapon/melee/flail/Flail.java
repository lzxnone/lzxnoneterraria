package com.lzxnone.terraria.item.weapon.melee.flail;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.IStaticProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.weapon.MeleeWeapon;
import com.lzxnone.terraria.utils.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

import java.util.List;

public abstract class Flail extends MeleeWeapon {
    public static final double DIRECTION_OFFSET = 0.6;
    public static final double UP_OFFSET = -0.3;
    public static final double RIGHT_OFFSET = -0.5;

    public static final double DROPPING_HITBOX_SIZE = 0.5;
    public static final double USING_HITBOX_SIZE = 0.5;
    public static final double THROWING_INFLATE = 2.0;

    protected Flail(Tier tier, Properties properties) {
        super(tier, properties);
    }

    public enum State {
        IDLE,
        USING,
        THROWING,
        THROWING_BACK,
        DROPPING
    }

    public abstract ItemStack getItemStack();

    public abstract ItemStack getProjectileItemStack();

    public abstract String getChainRes();

    public abstract boolean canDrop();

    public abstract float getDamage();

    public abstract int getFlyTime();

    public abstract double getProjectileSpeed();

    public abstract double getGravity();

    public abstract double getMaxRange();

    @Override
    public float getTooltipDamage(ItemStack weaponStack, LivingEntity entity) {
        float damage = getDamage();
        //锋利附魔
        int sharpnessLevel = getEnchantmentLevel(entity, weaponStack, Enchantments.SHARPNESS);
        if(sharpnessLevel > 0) {
            damage += 1.0F + Math.max(0, sharpnessLevel - 1) * 0.5F;
        }
        //药水
        if(entity instanceof Player player) damage = DamageUtil.applyPlayerDamageEffects(player, damage);
        //近战加成
        return MeleeWeapon.applyMeleeDamageBonus(weaponStack, entity, damage);
    }

    //来回甩
    public static final IStaticProjectileBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileBehavior() {
        @Override
        public void onMoving(StaticProjectile projectile) {
            Entity entity = projectile.getOwner();
            if(!(entity instanceof Player player)) return;
            if(!player.isUsingItem()) {
                onDied(projectile);
                return;
            }
            ItemStack stack = player.getUseItem();
            if(!(stack.getItem() instanceof Flail flail)) {
                onDied(projectile);
                return;
            }
            if(!stack.is(flail.getItemStack().getItem())) {
                onDied(projectile);
                return;
            }

            int state = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag().getInt("state");
            if(state == Flail.State.USING.ordinal()) {
                Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
                Vec3 eyePos = player.getEyePosition();
                boolean mainHand = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                    .copyTag().getBoolean("mainHand");
                boolean isRightHand = mainHand == (player.getMainArm() == HumanoidArm.RIGHT);
                double rightSign = isRightHand ? 1.0 : -1.0;
                Vec3 pos = eyePos.add(new Vec3(
                    dirs[0].x * DIRECTION_OFFSET + dirs[1].x * UP_OFFSET + dirs[2].x * RIGHT_OFFSET * rightSign,
                    dirs[0].y * DIRECTION_OFFSET + dirs[1].y * UP_OFFSET + dirs[2].y * RIGHT_OFFSET * rightSign,
                    dirs[0].z * DIRECTION_OFFSET + dirs[1].z * UP_OFFSET + dirs[2].z * RIGHT_OFFSET * rightSign
                ));
                projectile.getEntityData().set(StaticProjectile.ORIGIN, pos.toVector3f());
                projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
                projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
                projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);

                if(!projectile.level().isClientSide()) {
                    List<Entity> targets = projectile.level().getEntitiesOfClass(
                        Entity.class,
                        new AABB(
                            projectile.getX() - USING_HITBOX_SIZE, projectile.getY() - USING_HITBOX_SIZE, projectile.getZ() - USING_HITBOX_SIZE,
                            projectile.getX() + USING_HITBOX_SIZE, projectile.getY() + USING_HITBOX_SIZE, projectile.getZ() + USING_HITBOX_SIZE
                        ),
                        FilterUtil.createTargetFilter(projectile, player)
                    );
                    for(Entity target : targets) {
                        if(DamageUtil.meleeAttack(projectile, target, projectile.getEntityData().get(StaticProjectile.STACK_SOURCE), flail.getDamage(), 1.0f, 10)) {
                        }
                    }
                }
            }else {
                onDied(projectile);
                return;
            }
        }
    };

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public AABB getBoundingBoxForCulling(StaticSummon summon) {
            Entity owner = summon.getOwner();
            if(owner != null) {
                AABB hammerBox = summon.getBoundingBox();
                AABB ownerBox = owner.getBoundingBoxForCulling();
                return hammerBox.minmax(ownerBox).inflate(1.0D);
            }
            return summon.getBoundingBox();
        }

        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            Entity entity = summon.getOwner();
            if(!(entity instanceof Player player)) return;
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            boolean mainHand = !customData.contains("mainHand") || customData.getBoolean("mainHand");
            ItemStack stack = player.getItemInHand(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
            if(!(stack.getItem() instanceof Flail flail)) {
                onDied(summon);
                return;
            }
            if(!stack.is(flail.getItemStack().getItem())) {
                onDied(summon);
                return;
            }

            AABB hitBox = new AABB(
                summon.getX() - DROPPING_HITBOX_SIZE, summon.getY() - DROPPING_HITBOX_SIZE, summon.getZ() - DROPPING_HITBOX_SIZE,
                summon.getX() + DROPPING_HITBOX_SIZE, summon.getY() + DROPPING_HITBOX_SIZE, summon.getZ() + DROPPING_HITBOX_SIZE
            );
            int state = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag().getInt("state");
            if(state == Flail.State.THROWING.ordinal()) {
                int flyTime = customData.contains("flyTime") ? customData.getInt("flyTime") : 0;
                int age = summon.getEntityData().get(StaticSummon.AGE);
                if(age < flyTime) {
                    summon.setDeltaMovement(summon.getLookAngle().normalize().scale(flail.getProjectileSpeed()));
                    BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, summon.position().add(summon.getDeltaMovement()));
                    if(blockHitResult.getType() != HitResult.Type.MISS) {
                        summon.getEntityData().set(StaticSummon.AGE, flyTime);
                        if(summon.level() instanceof ServerLevel serverLevel) {
                            BlockState hitState = serverLevel.getBlockState(blockHitResult.getBlockPos());
                            ParticleUtil.addParticles(
                                serverLevel, new BlockParticleOption(ParticleTypes.BLOCK, hitState),
                                blockHitResult.getLocation(), new Vec3(0, 0, 0),
                                0.2, 25
                            );
                            SoundUtil.playServerSound(serverLevel, ModSounds.DIG.get(), blockHitResult.getLocation());
                        }
                    }
                }else {
                    Vec3 moveDir = player.getEyePosition().subtract(summon.position());
                    double dist = moveDir.length();
                    summon.setDeltaMovement(moveDir.normalize().scale(flail.getProjectileSpeed()));
                    if(dist < 2.0f) {
                        onDied(summon);
                        return;
                    }
                    BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, summon.position().add(summon.getDeltaMovement()));
                    if(blockHitResult.getType() != HitResult.Type.MISS) {
                        int hitBlockCount = customData.contains("hitBlockCount") ? customData.getInt("hitBlockCount") : 0;
                        if(hitBlockCount > 10) {
                            summon.noPhysics = true;
                            CustomData.update(DataComponents.CUSTOM_DATA, stack,
                                tag -> tag.putInt("state", Flail.State.THROWING_BACK.ordinal()));
                        }else {
                            customData.putInt("hitBlockCount", hitBlockCount + 1);
                            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
                        }
                        if(summon.level() instanceof ServerLevel serverLevel) {
                            BlockState hitState = serverLevel.getBlockState(blockHitResult.getBlockPos());
                            ParticleUtil.addParticles(
                                serverLevel, new BlockParticleOption(ParticleTypes.BLOCK, hitState),
                                blockHitResult.getLocation(), new Vec3(0, 0, 0),
                                0.2, 25
                            );
                            SoundUtil.playServerSound(serverLevel, ModSounds.DIG.get(), blockHitResult.getLocation());
                        }
                    }
                }
                double dist = summon.position().subtract(player.position()).length();
                if(dist > flail.getMaxRange()) {
                    CustomData.update(DataComponents.CUSTOM_DATA, stack,
                            tag -> tag.putInt("state", Flail.State.THROWING_BACK.ordinal()));
                }
                if(!summon.level().isClientSide()) {
                    EntityHitResult entityHitResult = CollisionUtil.checkEntityHit(summon, summon.position().add(summon.getDeltaMovement()), THROWING_INFLATE);
                    if(entityHitResult != null) {
                        Entity target = entityHitResult.getEntity();
                        if(DamageUtil.meleeAttack(summon, target, summon.getEntityData().get(StaticSummon.STACK_SOURCE), flail.getDamage(), 1.0f, 10)) {
                        }
                    }
                }
            }else if(state == Flail.State.DROPPING.ordinal()) {
                Vec3 g = new Vec3(0, -1, 0);
                summon.setDeltaMovement(g.scale(flail.getGravity()));
                double dist = summon.position().subtract(player.position()).length();
                if(dist > flail.getMaxRange()) {
                    CustomData.update(DataComponents.CUSTOM_DATA, stack,
                            tag -> tag.putInt("state", Flail.State.THROWING_BACK.ordinal()));
                }
                List<Entity> targets = summon.level().getEntitiesOfClass(
                    Entity.class,
                    hitBox,
                    FilterUtil.createTargetFilter(summon, summon.getOwner())
                );
                for(Entity target : targets) {
                    DamageUtil.meleeAttack(summon, target, summon.getEntityData().get(StaticSummon.STACK_SOURCE), flail.getDamage(), 1.0f, 10);
                }
            }else if(state == Flail.State.THROWING_BACK.ordinal()){
                Vec3 moveDir = player.getEyePosition().subtract(summon.position());
                double dist = moveDir.length();
                summon.setDeltaMovement(moveDir.normalize().scale(flail.getProjectileSpeed()));
                if(!summon.noPhysics) {
                    BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, summon.position().add(summon.getDeltaMovement()));
                    if(blockHitResult.getType() != HitResult.Type.MISS) {
                        int hitBlockCount = customData.contains("hitBlockCount") ? customData.getInt("hitBlockCount") : 0;
                        if(hitBlockCount > 10) {
                            summon.noPhysics = true;
                        }else {
                            customData.putInt("hitBlockCount", hitBlockCount + 1);
                            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
                        }
                        if(summon.level() instanceof ServerLevel serverLevel) {
                            BlockState hitState = serverLevel.getBlockState(blockHitResult.getBlockPos());
                            ParticleUtil.addParticles(
                                serverLevel, new BlockParticleOption(ParticleTypes.BLOCK, hitState),
                                blockHitResult.getLocation(), new Vec3(0, 0, 0),
                                0.2, 25
                            );
                            SoundUtil.playServerSound(serverLevel, ModSounds.DIG.get(), blockHitResult.getLocation());
                        }
                    }
                }
                EntityHitResult entityHitResult = CollisionUtil.checkEntityHit(summon, summon.position().add(summon.getDeltaMovement()), THROWING_INFLATE);
                if(entityHitResult != null) {
                    Entity target = entityHitResult.getEntity();
                    if(DamageUtil.meleeAttack(summon, target, summon.getEntityData().get(StaticSummon.STACK_SOURCE), flail.getDamage(), 1.0f, 10)) {
                    }
                }
                if(dist < 2.0f) {
                    onDied(summon);
                    return;
                }
            }else {
                onDied(summon);
                return;
            }
        }

        @Override
        public void onDied(StaticSummon summon) {
            if(summon.level().isClientSide()) return;
            Entity entity = summon.getOwner();
            if(!(entity instanceof Player player)) {
                summon.discard();
                return;
            }
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            boolean mainHand = !customData.contains("mainHand") || customData.getBoolean("mainHand");
            ItemStack stack = player.getItemInHand(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
            if(!(stack.getItem() instanceof Flail flail)) {
                summon.discard();
                return;
            }
            if(!stack.is(flail.getItemStack().getItem())) {
                summon.discard();
                return;
            }
            CustomData.update(DataComponents.CUSTOM_DATA, stack,
                    tag -> tag.putInt("state", Flail.State.IDLE.ordinal()));
            summon.discard();
        }
    };

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(!(stack.getItem() instanceof Flail flail)) return InteractionResultHolder.fail(stack);;

        int state = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag().getInt("state");
        if(state == Flail.State.IDLE.ordinal()) {
            StaticProjectile projectile = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), level);
            projectile.setOwner(player);
            projectile.getEntityData().set(StaticProjectile.STACK_SOURCE, player.getItemInHand(hand).copy());
            Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);

            Vec3 pos = new Vec3(player.getX(), player.getEyeY() - 0.1, player.getZ());
            projectile.setPos(pos);
            projectile.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.FLAIL_PROJECTILE);
            projectile.getEntityData().set(StaticProjectile.RENDER_MODE, "custom");
            projectile.getEntityData().set(StaticProjectile.ITEM, flail.getProjectileItemStack());
            projectile.getEntityData().set(StaticProjectile.ORIGIN, pos.toVector3f());
            projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
            projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
            projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);
            projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*cos(%.3f*t)", 1.0, 1.0));
            projectile.getEntityData().set(StaticProjectile.EXPRESSION_Y, String.format("%.3f*sin(%.3f*t)", 1.0, 1.0));

            projectile.getEntityData().set(StaticProjectile.LIFETIME, StaticSummon.INFINITE_LIFETIME);

            CompoundTag customData = new CompoundTag();
            customData.putString("chainRes", flail.getChainRes());
            customData.putBoolean("mainHand", hand == InteractionHand.MAIN_HAND);
            projectile.getEntityData().set(StaticProjectile.CUSTOM_DATA, customData);

            level.addFreshEntity(projectile);

            CustomData.update(DataComponents.CUSTOM_DATA, stack,
                    tag -> tag.putInt("state", Flail.State.USING.ordinal()));
            CustomData.update(DataComponents.CUSTOM_DATA, stack,
                    tag -> tag.putBoolean("mainHand", hand == InteractionHand.MAIN_HAND));
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }else if(state == Flail.State.THROWING.ordinal()) {
            if(!canDrop()) return InteractionResultHolder.fail(stack);
            CustomData.update(DataComponents.CUSTOM_DATA, stack,
                    tag -> tag.putInt("state", Flail.State.DROPPING.ordinal()));
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }else {
            return InteractionResultHolder.fail(stack);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if(!(entity instanceof Player player)) return;
        if(!(stack.getItem() instanceof Flail flail)) return;

        int state = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag().getInt("state");
        boolean mainHand = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag().getBoolean("mainHand");
        if(state == Flail.State.USING.ordinal()) {
            if(!level.isClientSide()) {
                CustomData.update(DataComponents.CUSTOM_DATA, stack,
                        tag -> tag.putInt("state", Flail.State.THROWING.ordinal()));

                Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
                float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);
                StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), level);
                summon.setOwner(player);
                summon.getEntityData().set(StaticSummon.STACK_SOURCE, stack.copy());
                Vec3 pos = new Vec3(player.getX(), player.getEyeY() - 0.1, player.getZ());
                summon.setPos(pos);
                summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.FLAIL_PROJECTILE);
                summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
                summon.getEntityData().set(StaticSummon.ITEM, flail.getProjectileItemStack());
                summon.getEntityData().set(StaticSummon.LIFETIME, StaticSummon.INFINITE_LIFETIME);

                summon.setXRot(xyRot[0]);
                summon.xRotO = xyRot[0];
                summon.setYRot(xyRot[1]);
                summon.yRotO = xyRot[1];

                CompoundTag customData = new CompoundTag();
                customData.putInt("flyTime", flail.getFlyTime());
                customData.putString("chainRes", flail.getChainRes());
                customData.putBoolean("mainHand", mainHand);
                summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

                summon.setNoGravity(true);

                level.addFreshEntity(summon);
            }
        }else if(state == Flail.State.DROPPING.ordinal()) {
            CustomData.update(DataComponents.CUSTOM_DATA, stack,
                    tag -> tag.putInt("state", Flail.State.THROWING_BACK.ordinal()));
        }
    }

    @Override
    public @NonNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if(entity instanceof Player player) {
            if(!isSelected
                && !player.getMainHandItem().is(stack.getItem())
                && !player.getOffhandItem().is(stack.getItem())) {
                CustomData.update(DataComponents.CUSTOM_DATA, stack,
                        tag -> tag.putInt("state", Flail.State.IDLE.ordinal()));
            }
        }
    }
}
