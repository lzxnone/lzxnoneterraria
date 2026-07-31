package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.item.weapon.MeleeWeapon;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.IStaticProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import net.minecraft.network.chat.Component;

import java.util.Comparator;
import java.util.List;

public class BladeOfGrass extends MeleeWeapon {
    public static final ConfigFloat PROJECTILE_DAMAGE = new ConfigFloat(
        "weapon.blade_of_grass.projectile_damage",
        "blade_of_grass_projectile_damage",
        2.0f,
        0.0f,
        8388600.0f
    );
    public static final ConfigInt EFFECT_DURATION = new ConfigInt(
        "weapon.blade_of_grass.effect_duration",
        "blade_of_grass_effect_duration",
        140,
        0,
        72000
    );
    public static final ConfigDouble TARGET_RANGE = new ConfigDouble(
        "weapon.blade_of_grass.target_range",
        "blade_of_grass_target_range",
        8.0,
        1.0,
        64.0
    );
    public static final ConfigInt PROJECTILE_LIFETIME = new ConfigInt(
        "weapon.blade_of_grass.projectile_lifetime",
        "blade_of_grass_projectile_lifetime",
        60,
        1,
        600
    );
    public static final ConfigInt PROJECTILE_MAX_HIT_COUNT = new ConfigInt(
        "weapon.blade_of_grass.projectile_max_hit_count",
        "blade_of_grass_projectile_max_hit_count",
        2,
        0,
        100
    );
    public static final ConfigDouble RADIUS_GROWTH = new ConfigDouble(
        "weapon.blade_of_grass.radius_growth",
        "blade_of_grass_radius_growth",
        0.05,
        0.0,
        1.0
    );
    public static final ConfigDouble ROTATION_SPEED = new ConfigDouble(
        "weapon.blade_of_grass.rotation_speed",
        "blade_of_grass_rotation_speed",
        0.2,
        0.0,
        1.0
    );
    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                PROJECTILE_DAMAGE,
                EFFECT_DURATION,
                TARGET_RANGE,
                PROJECTILE_LIFETIME,
                PROJECTILE_MAX_HIT_COUNT,
                RADIUS_GROWTH,
                ROTATION_SPEED
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "blade_of_grass",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/blade_of_grass.png"),
        Component.translatable("item.lzxnoneterraria.blade_of_grass"),
        CONFIG_DATA
    );

    public BladeOfGrass() {
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

    public static final IStaticProjectileBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileBehavior() {
        public static final int MAX_LENGTH = 10;

        @Override
        public void onMoving(StaticProjectile projectile) {
            ParticleUtil.addParticle(
                projectile.level(), ModParticles.LEAF_PARTICLE.get(),
                projectile.position(), 0.0,
                new Vec3(0, 0, 0), 0.1
            );
            Vector3f originalRight = projectile.getEntityData().get(StaticProjectile.RIGHT);
            Vec3 right = MathUtil.toVec3(originalRight).normalize();
            projectile.trailPositions.addFirst(projectile.position().add(right.scale(0.1)));
            projectile.trailPositions.addFirst(projectile.position().add(right.scale(-0.1)));
            while(projectile.trailPositions.size() > MAX_LENGTH) projectile.trailPositions.removeLast();
        }
        @Override
        public void onHitEntity(StaticProjectile projectile, EntityHitResult result) {
            if(!projectile.level().isClientSide()) {
                Entity target = result.getEntity();
                Entity owner = projectile.getOwner();
                if(owner == null) return;
                CompoundTag customData = projectile.getEntityData().get(StaticProjectile.CUSTOM_DATA);
                if(!customData.contains("hitCount") || customData.getInt("hitCount") >= PROJECTILE_MAX_HIT_COUNT.get()) return;
                if(!FilterUtil.createTargetFilter(owner).test(target) || !(owner instanceof Player player)) return;
                if(DamageUtil.meleeAttack(projectile, target, projectile.getEntityData().get(StaticProjectile.STACK_SOURCE), (float) PROJECTILE_DAMAGE.get(), 1.0f)) {
                    int count = customData.getInt("hitCount");
                    count++;
                    if(target instanceof LivingEntity livingEntity && projectile.getRandom().nextInt(4) == 0) {
                        livingEntity.addEffect(new MobEffectInstance(
                            MobEffects.POISON,
                            EFFECT_DURATION.get(),
                            0
                        ));
                    }
                    if(count >= PROJECTILE_MAX_HIT_COUNT.get()) onDied(projectile);
                    else customData.putInt("hitCount", count);
                }
            }
        }
        @Override
        public void onHitBlock(StaticProjectile projectile, BlockHitResult result) {
            if(projectile.level().isClientSide()) return;
            if(!projectile.level().getBlockState(result.getBlockPos()).getCollisionShape(projectile.level(), result.getBlockPos()).isEmpty()) {
                Level level = projectile.level();
                BlockPos pos = result.getBlockPos();
                BlockState state = level.getBlockState(pos);
                boolean isFire = state.is(BlockTags.FIRE);
                boolean isLava = state.getFluidState().is(FluidTags.LAVA);
                if(isFire || isLava) onDied(projectile);
            }
        }
    };

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if(!level.isClientSide()) {
             List<Entity> targets = CollisionUtil.searchEnemies(
                player.level(),
                AABB.ofSize(player.getBoundingBox().getCenter(), TARGET_RANGE.get() * 2, TARGET_RANGE.get() * 2, TARGET_RANGE.get() * 2),
                player,
                player
            );
            targets.sort(Comparator.comparingDouble(e -> e.distanceToSqr(player.position())));

            Vec3 playerPos = player.getEyePosition();

            Vec3 targetPos = playerPos.add(player.getLookAngle().normalize().scale(Math.random() * 4 + 4));
            if(!targets.isEmpty()) targetPos = targets.getFirst().getBoundingBox().getCenter();
            Vec3 originalDir = targetPos.subtract(playerPos);

            Vector3f[] dirs = MathUtil.computeCoordinateSystem(originalDir.toVector3f(), player.getYRot());
            dirs = MathUtil.rotateCoordinateSystem(dirs[0], dirs[2], (int) ((Math.random() * 2 - 1) * 30));

            Vec3 center = playerPos.add(originalDir.scale(0.5).add(MathUtil.toVec3(dirs[1]).scale(Math.random() * 4 + 4)));
            double radius = playerPos.subtract(center).length();
            double angleRad = -dirs[0].angle(playerPos.subtract(center).toVector3f());

            StaticProjectile projectile = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), level);
            projectile.setOwner(player);
            projectile.getEntityData().set(StaticProjectile.STACK_SOURCE, player.getWeaponItem().copy());
            Vec3 pos = new Vec3(player.getX(), player.getEyeY() - 0.1, player.getZ());
            projectile.setPos(pos);
            projectile.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.LEAF_PROJECTILE);
            projectile.getEntityData().set(StaticProjectile.RENDER_MODE, "custom");
            projectile.getEntityData().set(StaticProjectile.ORIGIN, MathUtil.toVector3f(center));
            projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
            projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
            projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);
            projectile.getEntityData().set(StaticProjectile.ITEM, new ItemStack(ModItems.LEAF_PROJECTILE.get()));
            projectile.getEntityData().set(StaticProjectile.SCALE_X, 0.5f);
            projectile.getEntityData().set(StaticProjectile.SCALE_Y, 0.5f);
            projectile.getEntityData().set(StaticProjectile.SCALE_Z, 0.5f);
            projectile.getEntityData().set(StaticProjectile.RXP, -90);
            projectile.getEntityData().set(StaticProjectile.RZP, -90);
            projectile.getEntityData().set(StaticProjectile.RYPS, 10);
            projectile.getEntityData().set(StaticProjectile.LIFETIME, PROJECTILE_LIFETIME.get());
            projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("(%.3f+%.3f*t)*cos(%.3f*t+%.3f)", radius, RADIUS_GROWTH.get(), ROTATION_SPEED.get(), angleRad));
            projectile.getEntityData().set(StaticProjectile.EXPRESSION_Y, String.format("(%.3f+%.3f*t)*sin(%.3f*t+%.3f)", radius, RADIUS_GROWTH.get(), ROTATION_SPEED.get(), angleRad));

            CompoundTag customData = new CompoundTag();
            customData.putInt("hitCount", 0);
            projectile.getEntityData().set(StaticProjectile.CUSTOM_DATA, customData);

            projectile.setDeltaMovement(MathUtil.toVec3(dirs[0]));
            level.addFreshEntity(projectile);
        }else {
            ParticleUtil.addParticles(
                player.level(), ModParticles.LEAF_PARTICLE.get(),
                player.getBoundingBox().getCenter(), 0.2,
                new Vec3(0, 0, 0), 0.2,
                10
            );
        }

        player.getCooldowns().addCooldown(stack.getItem(), 7);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}

