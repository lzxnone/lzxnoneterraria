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
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
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
import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.ConfigUtil;
import com.lzxnone.terraria.ui.config.IConfigData;
import net.minecraft.network.chat.Component;

import java.util.List;

public class Mace extends MeleeWeapon {
    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";

    public static final String PROJECTILE_SPEED_PATH = "weapon.mace.projectile_speed";
    public static final double PROJECTILE_SPEED_DEFAULT = 2.0;
    public static final double PROJECTILE_SPEED_MIN = 0.0;
    public static final double PROJECTILE_SPEED_MAX = 10.0;

    public static final String GRAVITY_PATH = "weapon.mace.gravity";
    public static final double GRAVITY_DEFAULT = 0.75;
    public static final double GRAVITY_MIN = 0.0;
    public static final double GRAVITY_MAX = 5.0;

    public static final String DAMAGE_PATH = "weapon.mace.damage";
    public static final float DAMAGE_DEFAULT = 4.0f;
    public static final float DAMAGE_MIN = 0.0f;
    public static final float DAMAGE_MAX = 8388600.0f;

    public static final String FLY_TIME_PATH = "weapon.mace.fly_time";
    public static final int FLY_TIME_DEFAULT = 10;
    public static final int FLY_TIME_MIN = 1;
    public static final int FLY_TIME_MAX = 100;

    public static final String MAX_RANGE_PATH = "weapon.mace.max_range";
    public static final double MAX_RANGE_DEFAULT = 32.0;
    public static final double MAX_RANGE_MIN = 1.0;
    public static final double MAX_RANGE_MAX = 512.0;

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigFactory.loadDoubleConfig(PROJECTILE_SPEED_PATH, configText("mace_projectile_speed"), configTooltip("mace_projectile_speed"), PROJECTILE_SPEED_DEFAULT, PROJECTILE_SPEED_MIN, PROJECTILE_SPEED_MAX);
            ConfigFactory.loadDoubleConfig(GRAVITY_PATH, configText("mace_gravity"), configTooltip("mace_gravity"), GRAVITY_DEFAULT, GRAVITY_MIN, GRAVITY_MAX);
            ConfigFactory.loadFloatConfig(DAMAGE_PATH, configText("mace_damage"), configTooltip("mace_damage"), DAMAGE_DEFAULT, DAMAGE_MIN, DAMAGE_MAX);
            ConfigFactory.loadIntConfig(FLY_TIME_PATH, configText("mace_fly_time"), configTooltip("mace_fly_time"), FLY_TIME_DEFAULT, FLY_TIME_MIN, FLY_TIME_MAX);
            ConfigFactory.loadDoubleConfig(MAX_RANGE_PATH, configText("mace_max_range"), configTooltip("mace_max_range"), MAX_RANGE_DEFAULT, MAX_RANGE_MIN, MAX_RANGE_MAX);
        }
    };

    private static Component configText(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key);
    }

    private static Component configTooltip(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key + ".tooltip");
    }

    public static double getProjectileSpeed() {
        return Math.clamp(ConfigUtil.readDouble(PROJECTILE_SPEED_PATH, PROJECTILE_SPEED_DEFAULT), PROJECTILE_SPEED_MIN, PROJECTILE_SPEED_MAX);
    }

    public static double getGravity() {
        return Math.clamp(ConfigUtil.readDouble(GRAVITY_PATH, GRAVITY_DEFAULT), GRAVITY_MIN, GRAVITY_MAX);
    }

    public static float getDamage() {
        return Math.clamp(ConfigUtil.readFloat(DAMAGE_PATH, DAMAGE_DEFAULT), DAMAGE_MIN, DAMAGE_MAX);
    }

    public static int getFlyTime() {
        return Math.clamp(ConfigUtil.readInt(FLY_TIME_PATH, FLY_TIME_DEFAULT), FLY_TIME_MIN, FLY_TIME_MAX);
    }

    public static double getMaxRange() {
        return Math.clamp(ConfigUtil.readDouble(MAX_RANGE_PATH, MAX_RANGE_DEFAULT), MAX_RANGE_MIN, MAX_RANGE_MAX);
    }

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
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile projectile)) return;
            if(projectile.getOwner() == null) return;
            ItemStack itemStack = projectile.getEntityData().get(StaticProjectile.ITEM);
            if(itemStack == ItemStack.EMPTY) return;

            Vector3f[] dirs = MathUtil.computeCoordinateSystem(projectile.getOwner());
            float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);

            poseStack.pushPose();

            //旋转(方向修正)
            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
            poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));

            Minecraft.getInstance().getItemRenderer().renderStatic(
                    itemStack,
                    ItemDisplayContext.NONE,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    bufferSource,
                    entity.level(),
                    0
            );
            poseStack.popPose();

            poseStack.pushPose();

            //旋转(方向修正)
            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
            poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0] + 90));

            Minecraft.getInstance().getItemRenderer().renderStatic(
                    itemStack,
                    ItemDisplayContext.NONE,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    bufferSource,
                    entity.level(),
                    0
            );
            poseStack.popPose();

            if(projectile.getOwner() == null) return;
            Vec3 eyePos = projectile.getOwner().getEyePosition();
            Vec3 start = eyePos.add(new Vec3(
                dirs[0].x * DIRECTION_OFFSET + dirs[1].x * UP_OFFSET + dirs[2].x * RIGHT_OFFSET,
                dirs[0].y * DIRECTION_OFFSET + dirs[1].y * UP_OFFSET + dirs[2].y * RIGHT_OFFSET,
                dirs[0].z * DIRECTION_OFFSET + dirs[1].z * UP_OFFSET + dirs[2].z * RIGHT_OFFSET
            ));
            Vec3 end = projectile.getPosition(partialTick);
            double dist = end.subtract(start).length();
            Vector3f[] dirs2 = MathUtil.computeCoordinateSystem(end.subtract(start).toVector3f(), 0);
            Vec3 dir = MathUtil.toVec3(dirs2[0]);
            Vec3 right = MathUtil.toVec3(dirs2[2]);
            Vec3 up = MathUtil.toVec3(dirs2[1]);

            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(RES));

            int count = (int) (dist / CHAIN_LENGTH);
            for(int j = 0;j < 2;j++) {
                for(int i = 0; i < count; i++) {
                    Vec3 current = start.add(new Vec3(
                            dir.x * i * CHAIN_LENGTH,
                            dir.y * i * CHAIN_LENGTH,
                            dir.z * i * CHAIN_LENGTH
                    )).subtract(projectile.getPosition(partialTick));

                    Vec3 next = start.add(new Vec3(
                            dir.x * (i + 1) * CHAIN_LENGTH,
                            dir.y * (i + 1) * CHAIN_LENGTH,
                            dir.z * (i + 1) * CHAIN_LENGTH
                    )).subtract(projectile.getPosition(partialTick));

                    Vec3 p1, p2, p3, p4;
                    if(j == 0) {
                        p1 = current.add(right.scale(0.125));
                        p2 = current.add(right.scale(-0.125));
                        p3 = next.add(right.scale(-0.125));
                        p4 = next.add(right.scale(0.125));
                    }else {
                        p1 = current.add(up.scale(0.125));
                        p2 = current.add(up.scale(-0.125));
                        p3 = next.add(up.scale(-0.125));
                        p4 = next.add(up.scale(0.125));
                    }

                    vertexConsumer.addVertex(poseStack.last().pose(), (float) p1.x, (float) p1.y, (float) p1.z)
                            .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.0f)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 1, 0);
                    vertexConsumer.addVertex(poseStack.last().pose(), (float) p2.x, (float) p2.y, (float) p2.z)
                            .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 1.0f)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 1, 0);
                    vertexConsumer.addVertex(poseStack.last().pose(), (float) p3.x, (float) p3.y, (float) p3.z)
                            .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(1.0f, 1.0f)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 1, 0);
                    vertexConsumer.addVertex(poseStack.last().pose(), (float) p4.x, (float) p4.y, (float) p4.z)
                            .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(1.0f, 0.0f)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 1, 0);
                }
            }
        }

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
                        if(DamageUtil.normalAttack(projectile, target, (float) getDamage(), 1.0f)) {
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
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;
            ItemStack itemStack = summon.getEntityData().get(StaticSummon.ITEM);
            if(itemStack == ItemStack.EMPTY) return;

            poseStack.pushPose();

            //旋转(方向修正)
            poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTick, summon.yRotO, summon.getYRot())));
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, summon.xRotO, summon.getXRot())));

            Minecraft.getInstance().getItemRenderer().renderStatic(
                    itemStack,
                    ItemDisplayContext.NONE,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    bufferSource,
                    entity.level(),
                    0
            );
            poseStack.popPose();

            poseStack.pushPose();

            //旋转(方向修正)
            poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTick, summon.yRotO, summon.getYRot())));
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, summon.xRotO + 90, summon.getXRot() + 90)));

            Minecraft.getInstance().getItemRenderer().renderStatic(
                    itemStack,
                    ItemDisplayContext.NONE,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    bufferSource,
                    entity.level(),
                    0
            );
            poseStack.popPose();

            if(summon.getOwner() == null) return;
            Vector3f[] dirs = MathUtil.computeCoordinateSystem(summon.getOwner());
            Vec3 eyePos = summon.getOwner().getEyePosition();
            Vec3 start = eyePos.add(new Vec3(
                dirs[0].x * DIRECTION_OFFSET + dirs[1].x * UP_OFFSET + dirs[2].x * RIGHT_OFFSET,
                dirs[0].y * DIRECTION_OFFSET + dirs[1].y * UP_OFFSET + dirs[2].y * RIGHT_OFFSET,
                dirs[0].z * DIRECTION_OFFSET + dirs[1].z * UP_OFFSET + dirs[2].z * RIGHT_OFFSET
            ));
            Vec3 end = summon.getPosition(partialTick);
            double dist = end.subtract(start).length();
            Vector3f[] dirs2 = MathUtil.computeCoordinateSystem(end.subtract(start).toVector3f(), 0);
            Vec3 dir = MathUtil.toVec3(dirs2[0]);
            Vec3 right = MathUtil.toVec3(dirs2[2]);
            Vec3 up = MathUtil.toVec3(dirs2[1]);

            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(RES));

            int count = (int) (dist / CHAIN_LENGTH);
            for(int j = 0;j < 2;j++) {
                for(int i = 0; i < count; i++) {
                    Vec3 current = start.add(new Vec3(
                            dir.x * i * CHAIN_LENGTH,
                            dir.y * i * CHAIN_LENGTH,
                            dir.z * i * CHAIN_LENGTH
                    )).subtract(summon.getPosition(partialTick));

                    Vec3 next = start.add(new Vec3(
                            dir.x * (i + 1) * CHAIN_LENGTH,
                            dir.y * (i + 1) * CHAIN_LENGTH,
                            dir.z * (i + 1) * CHAIN_LENGTH
                    )).subtract(summon.getPosition(partialTick));

                    Vec3 p1, p2, p3, p4;
                    if(j == 0) {
                        p1 = current.add(right.scale(0.125));
                        p2 = current.add(right.scale(-0.125));
                        p3 = next.add(right.scale(-0.125));
                        p4 = next.add(right.scale(0.125));
                    }else {
                        p1 = current.add(up.scale(0.125));
                        p2 = current.add(up.scale(-0.125));
                        p3 = next.add(up.scale(-0.125));
                        p4 = next.add(up.scale(0.125));
                    }

                    vertexConsumer.addVertex(poseStack.last().pose(), (float) p1.x, (float) p1.y, (float) p1.z)
                            .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.0f)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 1, 0);
                    vertexConsumer.addVertex(poseStack.last().pose(), (float) p2.x, (float) p2.y, (float) p2.z)
                            .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 1.0f)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 1, 0);
                    vertexConsumer.addVertex(poseStack.last().pose(), (float) p3.x, (float) p3.y, (float) p3.z)
                            .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(1.0f, 1.0f)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 1, 0);
                    vertexConsumer.addVertex(poseStack.last().pose(), (float) p4.x, (float) p4.y, (float) p4.z)
                            .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(1.0f, 0.0f)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 1, 0);
                }
            }
        }

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
                    summon.setDeltaMovement(summon.getLookAngle().normalize().scale(getProjectileSpeed()));
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
                    summon.setDeltaMovement(moveDir.normalize().scale(getProjectileSpeed()));
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
                if(dist > getMaxRange()) {
                    CustomData.update(DataComponents.CUSTOM_DATA, stack,
                            tag -> tag.putInt("state", State.THROWING_BACK.ordinal()));
                }
                if(!summon.level().isClientSide()) {
                    EntityHitResult entityHitResult = CollisionUtil.checkEntityHit(summon, summon.position().add(summon.getDeltaMovement()));
                    if(entityHitResult != null) {
                        Entity target = entityHitResult.getEntity();
                        if(DamageUtil.normalAttack(summon, target, (float) getDamage(), 1.0f)) {
                            target.invulnerableTime = 10;
                        }
                    }
                }
            }else if(state == State.DROPPING.ordinal()) {
                Vec3 g = new Vec3(0, -1, 0);
                summon.setDeltaMovement(g.scale(getGravity()));
                double dist = summon.position().subtract(player.position()).length();
                if(dist > getMaxRange()) {
                    CustomData.update(DataComponents.CUSTOM_DATA, stack,
                            tag -> tag.putInt("state", State.THROWING_BACK.ordinal()));
                }
                List<Entity> targets = summon.level().getEntitiesOfClass(
                    Entity.class,
                    summon.getBoundingBox(),
                    FilterUtil.createTargetFilter(summon, summon.getOwner())
                );
                for(Entity target : targets) {
                    DamageUtil.normalAttack(summon, target, (float) getDamage(), 1.0f);
                }
            }else if(state == State.THROWING_BACK.ordinal()){
                Vec3 moveDir = player.getEyePosition().subtract(summon.position());
                double dist = moveDir.length();
                summon.setDeltaMovement(moveDir.normalize().scale(getProjectileSpeed()));
                EntityHitResult entityHitResult = CollisionUtil.checkEntityHit(summon, summon.position().add(summon.getDeltaMovement()));
                if(entityHitResult != null) {
                    Entity target = entityHitResult.getEntity();
                    if(DamageUtil.normalAttack(summon, target, (float) getDamage(), 1.0f)) {
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
                customData.putInt("flyTime", getFlyTime());
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

