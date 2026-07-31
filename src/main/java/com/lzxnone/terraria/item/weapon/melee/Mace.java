package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.item.weapon.MeleeWeapon;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.IStaticProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.utils.*;
import com.mojang.math.Axis;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import net.minecraft.network.chat.Component;

import java.util.List;

public class Mace extends MeleeWeapon {
    public static final ConfigDouble PROJECTILE_SPEED = new ConfigDouble(
        "weapon.mace.projectile_speed",
        "mace_projectile_speed",
        2.0,
        0.0,
        10.0
    );
    public static final ConfigDouble GRAVITY = new ConfigDouble(
        "weapon.mace.gravity",
        "mace_gravity",
        0.75,
        0.0,
        5.0
    );
    public static final ConfigFloat DAMAGE = new ConfigFloat(
        "weapon.mace.damage",
        "mace_damage",
        4.0f,
        0.0f,
        8388600.0f
    );
    public static final ConfigInt FLY_TIME = new ConfigInt(
        "weapon.mace.fly_time",
        "mace_fly_time",
        10,
        1,
        100
    );
    public static final ConfigDouble MAX_RANGE = new ConfigDouble(
        "weapon.mace.max_range",
        "mace_max_range",
        32.0,
        1.0,
        512.0
    );
    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                PROJECTILE_SPEED,
                GRAVITY,
                DAMAGE,
                FLY_TIME,
                MAX_RANGE
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "mace",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/mace.png"),
        Component.translatable("item.lzxnoneterraria.mace"),
        CONFIG_DATA
    );

    public Mace() {
        super(Tiers.IRON, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 3, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -2.4, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ));
    }

    public enum State {
        IDLE,
        USING,
        THROWING,
        THROWING_BACK,
        DROPPING
    }

    public static final ResourceLocation RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/mace_chain.png");
    public static final float CHAIN_LENGTH = 0.25f;

    public static final double DIRECTION_OFFSET = 0.6;
    public static final double UP_OFFSET = -0.3;
    public static final double RIGHT_OFFSET = -0.3;

    public static final IStaticProjectileBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileBehavior() {

        @Override
        public void onMoving(StaticProjectile projectile) {
            Entity entity = projectile.getOwner();
            if(!(entity instanceof Player player)) return;
            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
            if(!stack.is(ModItems.MACE.get())) {
                onDied(projectile);
                return;
            }

            projectile.setBoundingBox(new AABB(
                projectile.getX() - 0.5f, projectile.getY() - 0.5f, projectile.getZ() - 0.5f,
                projectile.getX() + 0.5f, projectile.getY() + 0.5f, projectile.getZ() + 0.5f
            ));

            int state = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag().getInt("state");
            if(state == State.USING.ordinal()) {
                Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
                Vec3 eyePos = player.getEyePosition();
                Vec3 pos = eyePos.add(new Vec3(
                    dirs[0].x * DIRECTION_OFFSET + dirs[1].x * UP_OFFSET + dirs[2].x * RIGHT_OFFSET,
                    dirs[0].y * DIRECTION_OFFSET + dirs[1].y * UP_OFFSET + dirs[2].y * RIGHT_OFFSET,
                    dirs[0].z * DIRECTION_OFFSET + dirs[1].z * UP_OFFSET + dirs[2].z * RIGHT_OFFSET
                ));
                projectile.getEntityData().set(StaticProjectile.ORIGIN, pos.toVector3f());
                projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
                projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
                projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);

                if(!projectile.level().isClientSide()) {
                    List<Entity> targets = projectile.level().getEntitiesOfClass(
                        Entity.class,
                        projectile.getBoundingBox(),
                        FilterUtil.createTargetFilter(projectile, player)
                    );
                    for(Entity target : targets) {
                        if(DamageUtil.meleeAttack(projectile, target, projectile.getEntityData().get(StaticProjectile.STACK_SOURCE), (float) DAMAGE.get(), 1.0f)) {
                            target.invulnerableTime = 15;
                        }
                    }
                }
            }else {
                onDied(projectile);
                return;
            }
        }

        @Override
        public void onDied(StaticProjectile projectile) {
            Entity entity = projectile.getOwner();
            if(!(entity instanceof Player player)) {
                if(!projectile.level().isClientSide()) projectile.discard();
                return;
            }
            if(!projectile.level().isClientSide()) projectile.discard();
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
            if(!(entity instanceof Player player)) {
                onDied(summon);
                return;
            }
            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
            if(!stack.is(ModItems.MACE.get())) {
                onDied(summon);
                return;
            }
            summon.setBoundingBox(new AABB(
                summon.getX() - 0.25f, summon.getY() - 0.25f, summon.getZ() - 0.25f,
                summon.getX() + 0.25f, summon.getY() + 0.25f, summon.getZ() + 0.25f
            ));
            int state = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag().getInt("state");
            if(state == State.THROWING.ordinal()) {
                CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
                int flyTime = customData.contains("flyTime") ? customData.getInt("flyTime") : 0;
                int age = summon.getEntityData().get(StaticSummon.AGE);
                if(age < flyTime) {
                    summon.setDeltaMovement(summon.getLookAngle().normalize().scale(PROJECTILE_SPEED.get()));
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
                    summon.setDeltaMovement(moveDir.normalize().scale(PROJECTILE_SPEED.get()));
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
                                tag -> tag.putInt("state", State.THROWING_BACK.ordinal()));
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
                if(dist > MAX_RANGE.get()) {
                    CustomData.update(DataComponents.CUSTOM_DATA, stack,
                            tag -> tag.putInt("state", State.THROWING_BACK.ordinal()));
                }
                if(!summon.level().isClientSide()) {
                    EntityHitResult entityHitResult = CollisionUtil.checkEntityHit(summon, summon.position().add(summon.getDeltaMovement()), 1.0);
                    if(entityHitResult != null) {
                        Entity target = entityHitResult.getEntity();
                        if(DamageUtil.meleeAttack(summon, target, summon.getEntityData().get(StaticSummon.STACK_SOURCE), (float) DAMAGE.get(), 1.0f)) {
                            target.invulnerableTime = 10;
                        }
                    }
                }
            }else if(state == State.DROPPING.ordinal()) {
                Vec3 g = new Vec3(0, -1, 0);
                summon.setDeltaMovement(g.scale(GRAVITY.get()));
                double dist = summon.position().subtract(player.position()).length();
                if(dist > MAX_RANGE.get()) {
                    CustomData.update(DataComponents.CUSTOM_DATA, stack,
                            tag -> tag.putInt("state", State.THROWING_BACK.ordinal()));
                }
                List<Entity> targets = summon.level().getEntitiesOfClass(
                    Entity.class,
                    summon.getBoundingBox(),
                    FilterUtil.createTargetFilter(summon, summon.getOwner())
                );
                for(Entity target : targets) {
                    DamageUtil.meleeAttack(summon, target, summon.getEntityData().get(StaticSummon.STACK_SOURCE), (float) DAMAGE.get(), 1.0f);
                }
            }else if(state == State.THROWING_BACK.ordinal()){
                Vec3 moveDir = player.getEyePosition().subtract(summon.position());
                double dist = moveDir.length();
                summon.setDeltaMovement(moveDir.normalize().scale(PROJECTILE_SPEED.get()));
                if(!summon.noPhysics) {
                    CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
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
                EntityHitResult entityHitResult = CollisionUtil.checkEntityHit(summon, summon.position().add(summon.getDeltaMovement()), 1.0);
                if(entityHitResult != null) {
                    Entity target = entityHitResult.getEntity();
                    if(DamageUtil.meleeAttack(summon, target, summon.getEntityData().get(StaticSummon.STACK_SOURCE), (float) DAMAGE.get(), 1.0f)) {
                        target.invulnerableTime = 10;
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
            Entity entity = summon.getOwner();
            if(!(entity instanceof Player player)) {
                if(!summon.level().isClientSide()) summon.discard();
                return;
            }
            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
            CustomData.update(DataComponents.CUSTOM_DATA, stack,
                    tag -> tag.putInt("state", State.IDLE.ordinal()));
            if(!summon.level().isClientSide()) summon.discard();
        }
    };

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int state = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag().getInt("state");
        if(state == State.IDLE.ordinal()) {
            StaticProjectile projectile = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), level);
            projectile.setOwner(player);
            projectile.getEntityData().set(StaticProjectile.STACK_SOURCE, player.getWeaponItem().copy());
            Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);

            Vec3 pos = new Vec3(player.getX(), player.getEyeY() - 0.1, player.getZ());
            projectile.setPos(pos);
            projectile.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.MACE_PROJECTILE);
            projectile.getEntityData().set(StaticProjectile.RENDER_MODE, "custom");
            projectile.getEntityData().set(StaticProjectile.ITEM, new ItemStack(ModItems.MACE_PROJECTILE.get()));
            projectile.getEntityData().set(StaticProjectile.ORIGIN, pos.toVector3f());
            projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
            projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
            projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);
            projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*cos(%.3f*t)", 1.0, 1.0));
            projectile.getEntityData().set(StaticProjectile.EXPRESSION_Y, String.format("%.3f*sin(%.3f*t)", 1.0, 1.0));

            projectile.getEntityData().set(StaticProjectile.LIFETIME, 12000);
            level.addFreshEntity(projectile);

            CustomData.update(DataComponents.CUSTOM_DATA, stack,
                    tag -> tag.putInt("state", State.USING.ordinal()));
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }else if(state == State.THROWING.ordinal()) {
            CustomData.update(DataComponents.CUSTOM_DATA, stack,
                    tag -> tag.putInt("state", State.DROPPING.ordinal()));
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }else {
            return InteractionResultHolder.pass(stack);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if(!(entity instanceof Player player)) return;
        int state = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag().getInt("state");
        if(state == State.USING.ordinal()) {
            if(!level.isClientSide()) {
                CustomData.update(DataComponents.CUSTOM_DATA, stack,
                        tag -> tag.putInt("state", State.THROWING.ordinal()));

                Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
                float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);
                StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), level);
                summon.setOwner(player);
                summon.getEntityData().set(StaticSummon.STACK_SOURCE, player.getWeaponItem().copy());
                Vec3 pos = new Vec3(player.getX(), player.getEyeY() - 0.1, player.getZ());
                summon.setPos(pos);
                summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.MACE_PROJECTILE);
                summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
                summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.MACE_PROJECTILE.get()));
                summon.getEntityData().set(StaticSummon.LIFETIME, 12000);

                summon.setXRot(xyRot[0]);
                summon.xRotO = xyRot[0];
                summon.setYRot(xyRot[1]);
                summon.yRotO = xyRot[1];

                CompoundTag customData = new CompoundTag();
                customData.putInt("flyTime", FLY_TIME.get());
                summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

                summon.setNoGravity(true);

                level.addFreshEntity(summon);
            }
        }else if(state == State.DROPPING.ordinal()) {
            CustomData.update(DataComponents.CUSTOM_DATA, stack,
                    tag -> tag.putInt("state", State.THROWING_BACK.ordinal()));

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
            if(!isSelected) {
                CustomData.update(DataComponents.CUSTOM_DATA, stack,
                        tag -> tag.putInt("state", State.IDLE.ordinal()));
            }
        }
    }
}

