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
import net.minecraft.world.entity.monster.Monster;
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
import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.ConfigUtil;
import com.lzxnone.terraria.ui.config.IConfigData;
import net.minecraft.network.chat.Component;

import java.util.Comparator;
import java.util.List;

public class BladeOfGrass extends MeleeWeapon {
    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";

    public static final String PROJECTILE_DAMAGE_PATH = "weapon.blade_of_grass.projectile_damage";
    public static final float PROJECTILE_DAMAGE_DEFAULT = 2.0f;
    public static final float PROJECTILE_DAMAGE_MIN = 0.0f;
    public static final float PROJECTILE_DAMAGE_MAX = 8388600.0f;

    public static final String EFFECT_DURATION_PATH = "weapon.blade_of_grass.effect_duration";
    public static final int EFFECT_DURATION_DEFAULT = 140;
    public static final int EFFECT_DURATION_MIN = 0;
    public static final int EFFECT_DURATION_MAX = 72000;

    public static final String TARGET_RANGE_PATH = "weapon.blade_of_grass.target_range";
    public static final double TARGET_RANGE_DEFAULT = 8.0;
    public static final double TARGET_RANGE_MIN = 1.0;
    public static final double TARGET_RANGE_MAX = 64.0;

    public static final String PROJECTILE_LIFETIME_PATH = "weapon.blade_of_grass.projectile_lifetime";
    public static final int PROJECTILE_LIFETIME_DEFAULT = 60;
    public static final int PROJECTILE_LIFETIME_MIN = 1;
    public static final int PROJECTILE_LIFETIME_MAX = 600;

    public static final String PROJECTILE_MAX_HIT_COUNT_PATH = "weapon.blade_of_grass.projectile_max_hit_count";
    public static final int PROJECTILE_MAX_HIT_COUNT_DEFAULT = 2;
    public static final int PROJECTILE_MAX_HIT_COUNT_MIN = 0;
    public static final int PROJECTILE_MAX_HIT_COUNT_MAX = 100;

    public static final String RADIUS_GROWTH_PATH = "weapon.blade_of_grass.radius_growth";
    public static final double RADIUS_GROWTH_DEFAULT = 0.05;
    public static final double RADIUS_GROWTH_MIN = 0.0;
    public static final double RADIUS_GROWTH_MAX = 1.0;

    public static final String ROTATION_SPEED_PATH = "weapon.blade_of_grass.rotation_speed";
    public static final double ROTATION_SPEED_DEFAULT = 0.2;
    public static final double ROTATION_SPEED_MIN = 0.0;
    public static final double ROTATION_SPEED_MAX = 1.0;

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigFactory.loadFloatConfig(PROJECTILE_DAMAGE_PATH, configText("blade_of_grass_projectile_damage"), configTooltip("blade_of_grass_projectile_damage"), PROJECTILE_DAMAGE_DEFAULT, PROJECTILE_DAMAGE_MIN, PROJECTILE_DAMAGE_MAX);
            ConfigFactory.loadIntConfig(EFFECT_DURATION_PATH, configText("blade_of_grass_effect_duration"), configTooltip("blade_of_grass_effect_duration"), EFFECT_DURATION_DEFAULT, EFFECT_DURATION_MIN, EFFECT_DURATION_MAX);
            ConfigFactory.loadDoubleConfig(TARGET_RANGE_PATH, configText("blade_of_grass_target_range"), configTooltip("blade_of_grass_target_range"), TARGET_RANGE_DEFAULT, TARGET_RANGE_MIN, TARGET_RANGE_MAX);
            ConfigFactory.loadIntConfig(PROJECTILE_LIFETIME_PATH, configText("blade_of_grass_projectile_lifetime"), configTooltip("blade_of_grass_projectile_lifetime"), PROJECTILE_LIFETIME_DEFAULT, PROJECTILE_LIFETIME_MIN, PROJECTILE_LIFETIME_MAX);
            ConfigFactory.loadIntConfig(PROJECTILE_MAX_HIT_COUNT_PATH, configText("blade_of_grass_projectile_max_hit_count"), configTooltip("blade_of_grass_projectile_max_hit_count"), PROJECTILE_MAX_HIT_COUNT_DEFAULT, PROJECTILE_MAX_HIT_COUNT_MIN, PROJECTILE_MAX_HIT_COUNT_MAX);
            ConfigFactory.loadDoubleConfig(RADIUS_GROWTH_PATH, configText("blade_of_grass_radius_growth"), configTooltip("blade_of_grass_radius_growth"), RADIUS_GROWTH_DEFAULT, RADIUS_GROWTH_MIN, RADIUS_GROWTH_MAX);
            ConfigFactory.loadDoubleConfig(ROTATION_SPEED_PATH, configText("blade_of_grass_rotation_speed"), configTooltip("blade_of_grass_rotation_speed"), ROTATION_SPEED_DEFAULT, ROTATION_SPEED_MIN, ROTATION_SPEED_MAX);
        }
    };

    private static Component configText(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key);
    }

    private static Component configTooltip(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key + ".tooltip");
    }

    public static float getProjectileDamage() {
        return Math.clamp(ConfigUtil.readFloat(PROJECTILE_DAMAGE_PATH, PROJECTILE_DAMAGE_DEFAULT), PROJECTILE_DAMAGE_MIN, PROJECTILE_DAMAGE_MAX);
    }

    public static int getEffectDuration() {
        return Math.clamp(ConfigUtil.readInt(EFFECT_DURATION_PATH, EFFECT_DURATION_DEFAULT), EFFECT_DURATION_MIN, EFFECT_DURATION_MAX);
    }

    public static double getTargetRange() {
        return Math.clamp(ConfigUtil.readDouble(TARGET_RANGE_PATH, TARGET_RANGE_DEFAULT), TARGET_RANGE_MIN, TARGET_RANGE_MAX);
    }

    public static int getProjectileLifetime() {
        return Math.clamp(ConfigUtil.readInt(PROJECTILE_LIFETIME_PATH, PROJECTILE_LIFETIME_DEFAULT), PROJECTILE_LIFETIME_MIN, PROJECTILE_LIFETIME_MAX);
    }

    public static int getProjectileMaxHitCount() {
        return Math.clamp(ConfigUtil.readInt(PROJECTILE_MAX_HIT_COUNT_PATH, PROJECTILE_MAX_HIT_COUNT_DEFAULT), PROJECTILE_MAX_HIT_COUNT_MIN, PROJECTILE_MAX_HIT_COUNT_MAX);
    }

    public static double getRadiusGrowth() {
        return Math.clamp(ConfigUtil.readDouble(RADIUS_GROWTH_PATH, RADIUS_GROWTH_DEFAULT), RADIUS_GROWTH_MIN, RADIUS_GROWTH_MAX);
    }

    public static double getRotationSpeed() {
        return Math.clamp(ConfigUtil.readDouble(ROTATION_SPEED_PATH, ROTATION_SPEED_DEFAULT), ROTATION_SPEED_MIN, ROTATION_SPEED_MAX);
    }

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
                if(!customData.contains("hitCount") || customData.getInt("hitCount") >= getProjectileMaxHitCount()) return;
                if(!FilterUtil.createTargetFilter(owner).test(target) || !(owner instanceof Player player)) return;
                if(DamageUtil.meleeAttack(projectile, target, projectile.getEntityData().get(StaticProjectile.STACK_SOURCE), (float) getProjectileDamage(), 1.0f)) {
                    int count = customData.getInt("hitCount");
                    count++;
                    if(target instanceof LivingEntity livingEntity && projectile.getRandom().nextInt(4) == 0) {
                        livingEntity.addEffect(new MobEffectInstance(
                            MobEffects.POISON,
                            getEffectDuration(),
                            0
                        ));
                    }
                    if(count >= getProjectileMaxHitCount()) onDied(projectile);
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
             List<Monster> targets = player.level().getEntitiesOfClass(
                Monster.class,
                AABB.ofSize(player.getBoundingBox().getCenter(), getTargetRange() * 2, getTargetRange() * 2, getTargetRange() * 2),
                FilterUtil.createMonsterFilter(player)
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
            projectile.getEntityData().set(StaticProjectile.LIFETIME, getProjectileLifetime());
            projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("(%.3f+%.3f*t)*cos(%.3f*t+%.3f)", radius, getRadiusGrowth(), getRotationSpeed(), angleRad));
            projectile.getEntityData().set(StaticProjectile.EXPRESSION_Y, String.format("(%.3f+%.3f*t)*sin(%.3f*t+%.3f)", radius, getRadiusGrowth(), getRotationSpeed(), angleRad));

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

