package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.Config;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.projectile.IStaticProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.utils.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.List;

public class BladeOfGrass extends SwordItem {
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
        public static final ResourceLocation RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/normal_trail.png");
        public static final Vector3f TRAIL_COLOR = new Vector3f(0.271f, 0.486f, 0.016f);
        public static final float TRAIL_ALPHA = 0.25f;
        public static final int MAX_LENGTH = 10;

        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile projectile)) return;
            this.renderItem(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

            Vec3 currentPos = projectile.getPosition(partialTick);

            VertexConsumer vertexConsumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucent(RES));
            Matrix4f matrix = poseStack.last().pose();

            int quadCount = projectile.trailPositions.size() / 2 - 1;
            for(int i = 1; i < quadCount;i++) {
                Vec3 currentPoint1 = projectile.trailPositions.get(i * 2);
                Vec3 currentPoint2 = projectile.trailPositions.get(i * 2 + 1);
                Vec3 nextPoint1 = projectile.trailPositions.get(i * 2 + 3);
                Vec3 nextPoint2 = projectile.trailPositions.get(i * 2 + 2);

                double x1 = currentPoint1.x - currentPos.x;
                double y1 = currentPoint1.y - currentPos.y;
                double z1 = currentPoint1.z - currentPos.z;

                double x2 = currentPoint2.x - currentPos.x;
                double y2 = currentPoint2.y - currentPos.y;
                double z2 = currentPoint2.z - currentPos.z;

                double x3 = nextPoint1.x - currentPos.x;
                double y3 = nextPoint1.y - currentPos.y;
                double z3 = nextPoint1.z - currentPos.z;

                double x4 = nextPoint2.x - currentPos.x;
                double y4 = nextPoint2.y - currentPos.y;
                double z4 = nextPoint2.z - currentPos.z;

                float radio1 = (float) i / quadCount;
                float radio2 = (float) (i + 1) / quadCount;

                vertexConsumer.addVertex(matrix, (float)x1, (float)y1, (float)z1)
                    .setColor(TRAIL_COLOR.x, TRAIL_COLOR.y, TRAIL_COLOR.z, TRAIL_ALPHA).setUv(radio1, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 1, 0);
                vertexConsumer.addVertex(matrix, (float)x2, (float)y2, (float)z2)
                    .setColor(TRAIL_COLOR.x, TRAIL_COLOR.y, TRAIL_COLOR.z, TRAIL_ALPHA).setUv(radio1, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 1, 0);
                vertexConsumer.addVertex(matrix, (float)x3, (float)y3, (float)z3)
                    .setColor(TRAIL_COLOR.x, TRAIL_COLOR.y, TRAIL_COLOR.z, TRAIL_ALPHA).setUv(radio2, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 1, 0);
                vertexConsumer.addVertex(matrix, (float)x4, (float)y4, (float)z4)
                    .setColor(TRAIL_COLOR.x, TRAIL_COLOR.y, TRAIL_COLOR.z, TRAIL_ALPHA).setUv(radio2, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 1, 0);
            }
        }

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
                if(!customData.contains("hitCount") || customData.getInt("hitCount") >= Config.bladeOfGrassProjectileMaxHitCount) return;
                if(!FilterUtil.createTargetFilter(owner).test(target) || !(owner instanceof Player player)) return;
                if(DamageUtil.attack(player, target, (float) Config.bladeOfGrassProjectileDamage)) {
                    int count = customData.getInt("hitCount");
                    count++;
                    if(target instanceof LivingEntity livingEntity && projectile.getRandom().nextInt(4) == 0) {
                        livingEntity.addEffect(new MobEffectInstance(
                            MobEffects.POISON,
                            Config.bladeOfGrassEffectDuration,
                            0
                        ));
                    }
                    if(count >= Config.bladeOfGrassProjectileMaxHitCount) onDied(projectile);
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
                AABB.ofSize(player.getBoundingBox().getCenter(), Config.bladeOfGrassTargetRange * 2, Config.bladeOfGrassTargetRange * 2, Config.bladeOfGrassTargetRange * 2),
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
            projectile.getEntityData().set(StaticProjectile.LIFETIME, Config.bladeOfGrassProjectileLifetime);
            projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("(%.3f+%.3f*t)*cos(%.3f*t+%.3f)", radius, Config.bladeOfGrassRadiusGrowth, Config.bladeOfGrassRotationSpeed, angleRad));
            projectile.getEntityData().set(StaticProjectile.EXPRESSION_Y, String.format("(%.3f+%.3f*t)*sin(%.3f*t+%.3f)", radius, Config.bladeOfGrassRadiusGrowth, Config.bladeOfGrassRotationSpeed, angleRad));

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
