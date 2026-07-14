package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.Config;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.beam.ISwordBeamBehavior;
import com.lzxnone.terraria.entity.beam.SwordBeam;
import com.lzxnone.terraria.entity.beam.SwordBeamBehaviors;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.IItemWaveBehavior;
import com.lzxnone.terraria.network.payload.SwordBeamPayload;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.utils.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class TerraBlade extends SwordItem {
    public TerraBlade() {
        super(Tiers.DIAMOND, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 10, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -1.0, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ).rarity(Rarity.RARE));
    }

    public static final CompoundTag BEAM_DATA = Util.make(new CompoundTag(), tag -> {
        tag.putString("behavior", "terra_blade");
        tag.putInt("lifetime", 5);
        tag.putInt("cooldown", 5);
        tag.putFloat("color0R", 0.173f);
        tag.putFloat("color0G", 0.482f);
        tag.putFloat("color0B", 0.796f);
        tag.putFloat("color1R", 0.431f);
        tag.putFloat("color1G", 0.729f);
        tag.putFloat("color1B", 0.396f);
        tag.putFloat("color2R", 0.478f);
        tag.putFloat("color2G", 0.663f);
        tag.putFloat("color2B", 0.220f);

        CompoundTag customData = new CompoundTag();
        customData.putInt("hitEntityCount", 0);
        tag.put("customData", customData);
    });

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.075f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(0.5F, 1.0F, 0.5F),
            new Vector3f(0.0F, 1.0F, 0.2F),
            new Vector3f(0.7F, 0.7F, 0.7F)
        }
    );

    public static final ISwordBeamBehavior SWORD_BEAM_BEHAVIOR = new ISwordBeamBehavior() {
        @Override
        public void onMoving(SwordBeam beam) {
            if(beam.currentPosition == null) return;
            ParticleUtil.addParticle(
                beam.level(), PARTICLE,
                beam.currentPosition, 0.2,
                new Vec3(0, 0, 0), 0.2
            );
        }

        @Override
        public void onHitEntity(SwordBeam beam, EntityHitResult result) {
            if(!beam.level().isClientSide()) {
                Entity target = result.getEntity();
                if(beam.getOwner() instanceof Player player && FilterUtil.createTargetFilter(player).test(target)) {
                    CompoundTag custom_data = beam.getEntityData().get(SwordBeam.CUSTOM_DATA);
                    if(custom_data.contains("hitEntityCount")) {
                        int count = custom_data.getInt("hitEntityCount");
                        if(count < Config.terraBladeMaxHitCount) {
                            if(DamageUtil.attack(player, target, (float) Config.terraBladeDamage)) {
                                target.invulnerableTime = 20;
                                count++;
                                custom_data.putInt("hitEntityCount", count);
                                beam.getEntityData().set(SwordBeam.CUSTOM_DATA, custom_data);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void generate(Entity entity, CompoundTag beamData) {
            int randomAngle = (int) (Config.terraBladeRotateRange * (Math.random() * 2 - 1));
            beamData.putInt("rotate", randomAngle);
            ISwordBeamBehavior.super.generate(entity, beamData);
            if(entity instanceof Player player) summon(player, randomAngle);
        }
    };

    public static final Vector3f COLOR0 = new Vector3f(0.255f, 0.420f, 0.302f);
    public static final Vector3f COLOR1 = new Vector3f(0.173f, 0.482f, 0.796f);
    public static final Vector3f COLOR2 = new Vector3f(0.431f, 0.729f, 0.396f);

    public static final ResourceLocation RES0 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/terra_beam0.png");
    public static final ResourceLocation RES1 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/terra_beam3.png");
    public static final ResourceLocation RES2 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/terra_beam4.png");
    public static final ResourceLocation RES3 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/beam_sparkle.png");

    public static final float FADE_IN = 0.33f;
    public static final float FADE_OUT = 0.67f;

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;

            float halfWidth = SwordBeam.HALF_WIDTH * SwordBeam.SCALE;
            float halfHeight = SwordBeam.HALF_HEIGHT * SwordBeam.SCALE;

            float progress = (summon.getEntityData().get(StaticSummon.AGE) + partialTick) / (float) summon.getEntityData().get(StaticSummon.LIFETIME);

            float alpha;
            if(progress <= FADE_IN) {
                alpha = 1 - (FADE_IN - progress) / FADE_IN;
            }else if(progress >= FADE_OUT) {
                alpha = 1 - (progress - FADE_OUT) / (1 - FADE_OUT);
            }else {
                alpha = 1.0f;
            }

            if(alpha < 0.01f) return;

            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);

            Vector3f dir = new Vector3f();
            if(customData.contains("dirX")) dir.x = customData.getFloat("dirX");
            if(customData.contains("dirY")) dir.y = customData.getFloat("dirY");
            if(customData.contains("dirZ")) dir.z = customData.getFloat("dirZ");
            Vector3f up = new Vector3f();
            if(customData.contains("upX")) up.x = customData.getFloat("upX");
            if(customData.contains("upY")) up.y = customData.getFloat("upY");
            if(customData.contains("upZ")) up.z = customData.getFloat("upZ");
            Vector3f right = new Vector3f();
            if(customData.contains("rightX")) right.x = customData.getFloat("rightX");
            if(customData.contains("rightY")) right.y = customData.getFloat("rightY");
            if(customData.contains("rightZ")) right.z = customData.getFloat("rightZ");

            Vector3f[] dirs = new Vector3f[]{dir, up, right};
            double rotate = summon.getEntityData().get(StaticSummon.RZP);

            poseStack.pushPose();

            float size = 1.0f -  0.25f * progress;
            poseStack.scale(size, size, size);

            VertexConsumer vertexConsumer0 = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RES0));

            //左边
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 90 - 18 * (1.0f - progress), SwordBeam.DIST);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], 18 * (1.0f - progress), rotate);
            RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer0,
                    COLOR0.x(), COLOR0.y(), COLOR0.z(), alpha, halfWidth, halfHeight, 0f, 0, -0.01f);
            poseStack.popPose();


            //中间
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 90, SwordBeam.DIST);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], 0, rotate);
            RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer0,
                    COLOR1.x(), COLOR1.y(), COLOR1.z(), alpha, halfWidth, halfHeight, 0f, 0f, 0f);
            poseStack.popPose();

            //右边
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 108, SwordBeam.DIST);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], -9 * (1.0f - progress), rotate);
            RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer0,
                    COLOR2.x(), COLOR2.y(), COLOR2.z(), alpha, halfWidth, halfHeight, 0f, 0, 0.01f);
            poseStack.popPose();

            VertexConsumer vertexConsumer1 = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RES1));

            //三线
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 90, SwordBeam.DIST / 1.5f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], 0, rotate);
            poseStack.scale(0.5f, 0.5f, 0.5f);
            RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer1,
                    1.0f, 1.0f, 1.0f, alpha, halfWidth, halfHeight, 0f, 0f, 0.02f);
            poseStack.popPose();

            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 90, SwordBeam.DIST / 1.25f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], 0, rotate);
            poseStack.scale(0.75f, 0.75f, 0.75f);
            RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer1,
                    1.0f, 1.0f, 1.0f, alpha, halfWidth, halfHeight, 0f, 0f, 0.02f);
            poseStack.popPose();

            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 90, SwordBeam.DIST * 1.15f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], 0, rotate);
            poseStack.scale(0.95f, 0.95f, 0.95f);
            RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer1,
                    1.0f, 1.0f, 1.0f, alpha, halfWidth, halfHeight, 0f, 0f, 0.02f);
            poseStack.popPose();

            VertexConsumer vertexConsumer2 = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RES2));

            //边缘高光
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 100, SwordBeam.DIST * 1.15f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], 0, rotate);
            poseStack.scale(1.0f, 1.0f, 1.0f);
            for(int i = 0;i < 5;i++) {
                RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer2,
                        COLOR2.x(), COLOR2.y(), COLOR2.z(), alpha, halfWidth, halfHeight, 0f, 0f, 0.03f);
            }
            poseStack.popPose();


            float sparkleAlpha;
            if(progress < FADE_IN) {
                sparkleAlpha = 0;
            }else if(progress > FADE_OUT) {
                sparkleAlpha = 1.0f - (progress - FADE_OUT) / (1.0f - FADE_OUT);
            }else {
                sparkleAlpha = (progress - FADE_IN) / (FADE_OUT - FADE_IN);
            }

            VertexConsumer vertexConsumer3 = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RES3));

            //闪烁(外层)
            poseStack.pushPose();
            for(int k = 0;k < 2;k++) {
                poseStack.pushPose();
                if(k == 0) {
                    RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 90 - 60.0 * progress, SwordBeam.DIST * 2f);
                    RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], 60 * progress, rotate);
                }else {
                    RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 90 + 60.0 * progress, SwordBeam.DIST * 2f + 0.5f * progress);
                    RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], -60 * progress, rotate);
                }
                for(int j = 0; j < 2; j++) {
                    poseStack.pushPose();
                    if(j == 0) poseStack.mulPose(Axis.ZP.rotationDegrees(0.0F));
                    else poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
                    poseStack.scale(0.5f, 0.5f, 0.5f);
                    for(int i = 0; i < 5; i++) {
                        RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer3,
                                COLOR2.x(), COLOR2.y(), COLOR2.z(), sparkleAlpha, 32 * SwordBeam.SCALE, 32 * SwordBeam.SCALE, 0f, 0f, 0.03f);
                    }
                    poseStack.popPose();
                }
                poseStack.popPose();
            }
            poseStack.popPose();

            //闪烁(中层)
            poseStack.pushPose();
            for(int k = 0;k < 2;k++) {
                poseStack.pushPose();
                if(k == 0) {
                    RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 90 - 30.0 * progress, SwordBeam.DIST * 2f);
                    RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], 30 * progress, rotate);
                }else {
                    RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 90 + 30.0 * progress, SwordBeam.DIST * 2.1f);
                    RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], -30 * progress, rotate);
                }
                for(int j = 0; j < 2; j++) {
                    poseStack.pushPose();
                    if(j == 0) poseStack.mulPose(Axis.ZP.rotationDegrees(0.0F));
                    else poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
                    poseStack.scale(0.75f, 0.75f, 0.75f);
                    for(int i = 0; i < 5; i++) {
                        RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer3,
                                COLOR2.x(), COLOR2.y(), COLOR2.z(), sparkleAlpha, 32 * SwordBeam.SCALE, 32 * SwordBeam.SCALE, 0f, 0f, 0.03f);
                    }
                    poseStack.popPose();
                }
                poseStack.popPose();
            }
            poseStack.popPose();

            //闪烁(内层)
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 90, SwordBeam.DIST * 2f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], 0, rotate);
            for(int j = 0; j < 2; j++) {
                poseStack.pushPose();
                if(j == 0) poseStack.mulPose(Axis.ZP.rotationDegrees(0.0F));
                else poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
                poseStack.scale(1.0f, 1.0f, 1.0f);
                for(int i = 0; i < 5; i++) {
                    RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer3,
                            COLOR2.x(), COLOR2.y(), COLOR2.z(), sparkleAlpha, 32 * SwordBeam.SCALE, 32 * SwordBeam.SCALE, 0f, 0f, 0.03f);
                }
                poseStack.popPose();
            }
            poseStack.popPose();

            poseStack.popPose();
        }

        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);

            //运动逻辑
            if(!customData.contains("dead")) {
                float progress = summon.getEntityData().get(StaticSummon.AGE) / (float) summon.getEntityData().get(StaticSummon.LIFETIME);
                if(progress > FADE_IN) {
                    summon.setDeltaMovement(summon.getLookAngle().normalize().scale(Config.terraProjectileSpeed - Config.terraProjectileSpeed * (progress - FADE_IN) / (1.0f - FADE_IN)));
                }else {
                    summon.setDeltaMovement(summon.getLookAngle().normalize().scale(Config.terraProjectileSpeed));
                }
            }else {
                summon.setDeltaMovement(summon.getLookAngle().normalize().scale(0.1));
                return;
            }

            //碰撞箱计算
            Vec3 center = summon.position().add(summon.getLookAngle().normalize().scale(SwordBeam.DIST));
            if(customData.contains("extX") && customData.contains("extY") && customData.contains("extZ")) {
                summon.setBoundingBox(new AABB(
                    center.x - customData.getFloat("extX"), center.y - customData.getFloat("extY"), center.z - customData.getFloat("extZ"),
                    center.x + customData.getFloat("extX"), center.y + customData.getFloat("extY"), center.z + customData.getFloat("extZ")
                ));
            }

            //碰撞计算
            if(!summon.level().isClientSide() && customData.contains("hitCount") && summon.getOwner() instanceof Player player) {
                int count = customData.getInt("hitCount");
                List<Entity> targets = summon.level().getEntitiesOfClass(Entity.class, summon.getBoundingBox(), FilterUtil.createTargetFilter(summon, summon.getOwner()));
                for(Entity target : targets) {
                    if(DamageUtil.attack(player, target, (float) Config.terraProjectileDamage * (float) Math.pow(Config.terraProjectileDamageDecay, count))) {
                        count++;
                        ParticleUtil.addParticles(
                            (ServerLevel) summon.level(), ModParticles.TERRA_BEAM_HIT_PARTICLE.get(),
                            new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2.0, target.getZ()), new Vec3(0, 0, 0),
                            0, 1
                        );
                        target.invulnerableTime = 12;
                    }
                }
                if(!targets.isEmpty()) customData.putInt("hitCount", count);
            }

            ClipContext context = new ClipContext(
                summon.position(),
                summon.position().add(summon.getDeltaMovement()),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                summon
            );
            BlockHitResult blockHit = summon.level().clip(context);
            if(blockHit.getType() != HitResult.Type.MISS) {
                customData.putBoolean("dead", true);
            }

            //粒子
            if(summon.level().isClientSide() && summon.tickCount % 4 == 0) {
                Vec3 dir = new Vec3(
                    customData.contains("dirX") ? customData.getFloat("dirX") : 0,
                    customData.contains("dirY") ? customData.getFloat("dirY") : 0,
                    customData.contains("dirZ") ? customData.getFloat("dirZ") : 0
                );
                Vec3 right = new Vec3(
                    customData.contains("rightX") ? customData.getFloat("rightX") : 0,
                    customData.contains("rightY") ? customData.getFloat("rightY") : 0,
                    customData.contains("rightZ") ? customData.getFloat("rightZ") : 0
                );

                Vec3 speed = dir.scale(summon.getDeltaMovement().length() / 4);
                for(int i = 0;i < 5;i++) {
                    Vec3 delta = right.scale((Math.random() - 0.5) * 6);
                    ParticleUtil.addParticle(
                        summon.level(), PARTICLE,
                        summon.position().add(delta), 0.0,
                        speed, 0.0
                    );
                }
            }
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
        }
    };

    public static void summon(Player player, int randomAngle) {
        StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), player.level());
        summon.setOwner(player);
        Vec3 pos = new Vec3(player.getX(), player.getEyeY() - 0.1, player.getZ());
        summon.setPos(pos);

        if(!Config.terraProjectileAlignToBlade) randomAngle = (int) (Config.terraProjectileRotateRange * (Math.random() * 2 - 1));

        Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
        dirs = MathUtil.rotateCoordinateSystem(dirs[0], dirs[2], randomAngle);
        float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);
        summon.setXRot(xyRot[0]);
        summon.xRotO = xyRot[0];
        summon.setYRot(xyRot[1]);
        summon.yRotO = xyRot[1];

        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.TERRA_BLADE_BEAM);
        summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
        summon.getEntityData().set(StaticSummon.LIFETIME, Config.terraProjectileLifetime);
        summon.getEntityData().set(StaticSummon.RZP, randomAngle);
        summon.getEntityData().set(StaticSummon.GLOW, true);
        summon.setNoGravity(true);
        summon.noPhysics = true;

        CompoundTag customData = new CompoundTag();

        Quaternionf rotation = new Quaternionf()
            .fromAxisAngleRad(dirs[0], (float) Math.toRadians(Math.abs(dirs[0].y) > 0.999 ? 0 : randomAngle))
            .rotateY((float) Math.toRadians(-xyRot[1]))
            .rotateX((float) Math.toRadians(xyRot[0]))
            .rotateX((float) Math.toRadians(-90.0))
            .rotateZ((float) Math.toRadians(-90.0));

        Vector3f axisX = new Vector3f(1, 0, 0).rotate(rotation);
        Vector3f axisY = new Vector3f(0, 1, 0).rotate(rotation);
        Vector3f axisZ = new Vector3f(0, 0, 1).rotate(rotation);

        float extX = SwordBeam.HALF_WIDTH * SwordBeam.SCALE * Math.abs(axisX.x()) + SwordBeam.HALF_HEIGHT * SwordBeam.SCALE * Math.abs(axisY.x()) + SwordBeam.HALF_THICKNESS * SwordBeam.SCALE * Math.abs(axisZ.x());
        float extY = SwordBeam.HALF_WIDTH * SwordBeam.SCALE * Math.abs(axisX.y()) + SwordBeam.HALF_HEIGHT * SwordBeam.SCALE * Math.abs(axisY.y()) + SwordBeam.HALF_THICKNESS * SwordBeam.SCALE * Math.abs(axisZ.y());
        float extZ = SwordBeam.HALF_WIDTH * SwordBeam.SCALE * Math.abs(axisX.z()) + SwordBeam.HALF_HEIGHT * SwordBeam.SCALE * Math.abs(axisY.z()) + SwordBeam.HALF_THICKNESS * SwordBeam.SCALE * Math.abs(axisZ.z());

        customData.putInt("hitCount", 0);
        customData.putFloat("extX", extX);
        customData.putFloat("extY", extY);
        customData.putFloat("extZ", extZ);
        customData.putFloat("dirX", dirs[0].x);
        customData.putFloat("dirY", dirs[0].y);
        customData.putFloat("dirZ", dirs[0].z);
        customData.putFloat("upX", dirs[1].x);
        customData.putFloat("upY", dirs[1].y);
        customData.putFloat("upZ", dirs[1].z);
        customData.putFloat("rightX", dirs[2].x);
        customData.putFloat("rightY", dirs[2].y);
        customData.putFloat("rightZ", dirs[2].z);
        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

        summon.setDeltaMovement(player.getLookAngle().normalize().scale(Config.terraProjectileSpeed));

        player.level().addFreshEntity(summon);
    }

    public static final IItemWaveBehavior ITEM_WAVE_BEHAVIOR = new IItemWaveBehavior() {
        public void onLeftClickAir(PlayerInteractEvent.LeftClickEmpty event) {
            Player player = event.getEntity();
            ItemStack itemStack = player.getMainHandItem();
            if(itemStack.isEmpty()) return;
            Item item = itemStack.getItem();
            if(item instanceof TerraBlade && !player.getCooldowns().isOnCooldown(item)) {
                PacketDistributor.sendToServer(new SwordBeamPayload("terra_blade", BEAM_DATA));
                SoundUtil.playClientSound(player, ModSounds.WAVE.get());
                SoundUtil.playClientSound(player, ModSounds.WAVE3.get());
            }
        }
        public void onAttackEntity(AttackEntityEvent event) {
            Player player = event.getEntity();
            ItemStack itemStack = player.getMainHandItem();
            if(itemStack.isEmpty()) return;
            Item item = itemStack.getItem();
            if(item instanceof TerraBlade && !player.getCooldowns().isOnCooldown(item)) {
                if(!player.level().isClientSide()) {
                    SwordBeamBehaviors.getBehavior("terra_blade").generate(player, BEAM_DATA);
                }else {
                    SoundUtil.playClientSound(player, ModSounds.WAVE.get());
                    SoundUtil.playClientSound(player, ModSounds.WAVE3.get());
                }
            }
            event.setCanceled(true);
        }
    };

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(!level.isClientSide()) {
            SwordBeamBehaviors.getBehavior("terra_blade").generate(player, BEAM_DATA);
        }else {
            SoundUtil.playClientSound(player, ModSounds.WAVE.get());
            SoundUtil.playClientSound(player, ModSounds.WAVE3.get());
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
