package com.lzxnone.terraria.item.weapon.melee;

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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.ClipContext;
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
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -2.4, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ));
    }

    public static final CompoundTag BEAM_DATA = Util.make(new CompoundTag(), tag -> {
        tag.putString("behavior", "terra_blade");
        tag.putInt("cooldown", 8);
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
        public static final int MAX_HIT_ENTITY_COUNT = 3;

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
                if(result.getEntity() instanceof LivingEntity target && beam.getOwner() instanceof Player player && FilterUtil.createLivingTargetFilter(player).test(target)) {
                    CompoundTag custom_data = beam.getEntityData().get(SwordBeam.CUSTOM_DATA);
                    if(custom_data.contains("hitEntityCount")) {
                        int count = custom_data.getInt("hitEntityCount");
                        if(count < MAX_HIT_ENTITY_COUNT) {
                            if(target.hurt(beam.level().damageSources().playerAttack(player), 11.0f)) {
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
            ISwordBeamBehavior.super.generate(entity, beamData);
            if(entity instanceof Player player) summon(player);
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

    public static final double SPEED = 1.5;
    public static final float DAMAGE = 11;
    public static final float DAMAGE_PUNISHMENT = 0.75f;


    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;

            VertexConsumer vertexConsumer0 = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RES0));
            VertexConsumer vertexConsumer1 = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RES1));
            VertexConsumer vertexConsumer2 = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RES2));
            VertexConsumer vertexConsumer3 = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RES3));

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

            Quaternionf rotationX = new Quaternionf().fromAxisAngleRad(MathUtil.toVector3f(summon.getLookAngle()), (float) Math.toRadians(summon.getEntityData().get(StaticSummon.RZP)));
            Vector3f[] dirs = MathUtil.computeDir(
                MathUtil.toVector3f(summon.getLookAngle()),
                MathUtil.computeDir(MathUtil.toVector3f(summon.getLookAngle()))[2].rotate(rotationX)
            );

            poseStack.pushPose();

            float size = 1.0f -  0.25f * progress;
            poseStack.scale(size, size, size);

            //左边
            poseStack.pushPose();
            this.applyTranslate(poseStack, dirs[0], dirs[2], 90 - 18 * (1.0f - progress), SwordBeam.DIST);
            this.applyRotate(summon, poseStack, dirs[0], dirs[1], 18 * (1.0f - progress));
            renderQuad(poseStack.last().pose(), vertexConsumer0,
                    COLOR0.x(), COLOR0.y(), COLOR0.z(), alpha, halfWidth, halfHeight, 0f, 0, -0.01f);
            poseStack.popPose();

            //中间
            poseStack.pushPose();
            this.applyTranslate(poseStack, dirs[0], dirs[2], 90, SwordBeam.DIST);
            this.applyRotate(summon, poseStack, dirs[0], dirs[1], 0);
            renderQuad(poseStack.last().pose(), vertexConsumer0,
                    COLOR1.x(), COLOR1.y(), COLOR1.z(), alpha, halfWidth, halfHeight, 0f, 0f, 0f);
            poseStack.popPose();

            //右边
            poseStack.pushPose();
            this.applyTranslate(poseStack, dirs[0], dirs[2], 108, SwordBeam.DIST);
            this.applyRotate(summon, poseStack, dirs[0], dirs[1], -9 * (1.0f - progress));
            renderQuad(poseStack.last().pose(), vertexConsumer0,
                    COLOR2.x(), COLOR2.y(), COLOR2.z(), alpha, halfWidth, halfHeight, 0f, 0, 0.01f);
            poseStack.popPose();

            //三线
            poseStack.pushPose();
            this.applyTranslate(poseStack, dirs[0], dirs[2], 90, SwordBeam.DIST / 1.5f);
            this.applyRotate(summon, poseStack, dirs[0], dirs[1], 0);
            poseStack.scale(0.5f, 0.5f, 0.5f);
            renderQuad(poseStack.last().pose(), vertexConsumer1,
                    1.0f, 1.0f, 1.0f, alpha, halfWidth, halfHeight, 0f, 0f, 0.02f);
            poseStack.popPose();

            poseStack.pushPose();
            this.applyTranslate(poseStack, dirs[0], dirs[2], 90, SwordBeam.DIST / 1.25f);
            this.applyRotate(summon, poseStack, dirs[0], dirs[1], 0);
            poseStack.scale(0.75f, 0.75f, 0.75f);
            renderQuad(poseStack.last().pose(), vertexConsumer1,
                    1.0f, 1.0f, 1.0f, alpha, halfWidth, halfHeight, 0f, 0f, 0.02f);
            poseStack.popPose();

            poseStack.pushPose();
            this.applyTranslate(poseStack, dirs[0], dirs[2], 90, SwordBeam.DIST * 1.15f);
            this.applyRotate(summon, poseStack, dirs[0], dirs[1], 0);
            poseStack.scale(0.95f, 0.95f, 0.95f);
            renderQuad(poseStack.last().pose(), vertexConsumer1,
                    1.0f, 1.0f, 1.0f, alpha, halfWidth, halfHeight, 0f, 0f, 0.02f);
            poseStack.popPose();

            //边缘高光
            poseStack.pushPose();
            this.applyTranslate(poseStack, dirs[0], dirs[2], 100, SwordBeam.DIST * 1.15f);
            this.applyRotate(summon, poseStack, dirs[0], dirs[1], 0);
            poseStack.scale(1.0f, 1.0f, 1.0f);
            for(int i = 0;i < 5;i++) {
                renderQuad(poseStack.last().pose(), vertexConsumer2,
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

            //闪烁(外层)
            poseStack.pushPose();
            for(int k = 0;k < 2;k++) {
                poseStack.pushPose();
                if(k == 0) {
                    this.applyTranslate(poseStack, dirs[0], dirs[2], 90 - 60.0 * progress, SwordBeam.DIST * 2f);
                    this.applyRotate(summon, poseStack, dirs[0], dirs[1], 60 * progress);
                }else {
                    this.applyTranslate(poseStack, dirs[0], dirs[2], 90 + 60.0 * progress, SwordBeam.DIST * 2f + 0.5f * progress);
                    this.applyRotate(summon, poseStack, dirs[0], dirs[1], -60 * progress);
                }
                for(int j = 0; j < 2; j++) {
                    poseStack.pushPose();
                    if(j == 0) poseStack.mulPose(Axis.ZP.rotationDegrees(0.0F));
                    else poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
                    poseStack.scale(0.5f, 0.5f, 0.5f);
                    for(int i = 0; i < 5; i++) {
                        renderQuad(poseStack.last().pose(), vertexConsumer3,
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
                    this.applyTranslate(poseStack, dirs[0], dirs[2], 90 - 30.0 * progress, SwordBeam.DIST * 2f);
                    this.applyRotate(summon, poseStack, dirs[0], dirs[1], 30 * progress);
                }else {
                    this.applyTranslate(poseStack, dirs[0], dirs[2], 90 + 30.0 * progress, SwordBeam.DIST * 2.1f);
                    this.applyRotate(summon, poseStack, dirs[0], dirs[1], -30 * progress);
                }
                for(int j = 0; j < 2; j++) {
                    poseStack.pushPose();
                    if(j == 0) poseStack.mulPose(Axis.ZP.rotationDegrees(0.0F));
                    else poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
                    poseStack.scale(0.75f, 0.75f, 0.75f);
                    for(int i = 0; i < 5; i++) {
                        renderQuad(poseStack.last().pose(), vertexConsumer3,
                                COLOR2.x(), COLOR2.y(), COLOR2.z(), sparkleAlpha, 32 * SwordBeam.SCALE, 32 * SwordBeam.SCALE, 0f, 0f, 0.03f);
                    }
                    poseStack.popPose();
                }
                poseStack.popPose();
            }
            poseStack.popPose();

            //闪烁(内层)
            poseStack.pushPose();
            this.applyTranslate(poseStack, dirs[0], dirs[2], 90, SwordBeam.DIST * 2f);
            this.applyRotate(summon, poseStack, dirs[0], dirs[1], 0);
            for(int j = 0; j < 2; j++) {
                poseStack.pushPose();
                if(j == 0) poseStack.mulPose(Axis.ZP.rotationDegrees(0.0F));
                else poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
                poseStack.scale(1.0f, 1.0f, 1.0f);
                for(int i = 0; i < 5; i++) {
                    renderQuad(poseStack.last().pose(), vertexConsumer3,
                            COLOR2.x(), COLOR2.y(), COLOR2.z(), sparkleAlpha, 32 * SwordBeam.SCALE, 32 * SwordBeam.SCALE, 0f, 0f, 0.03f);
                }
                poseStack.popPose();
            }
            poseStack.popPose();


            poseStack.popPose();
        }

        public void applyRotate(StaticSummon summon, PoseStack poseStack, Vector3f dir, Vector3f up, double angle) {
            Quaternionf rotation = new Quaternionf().fromAxisAngleRad(up, (float) Math.toRadians(angle));
            poseStack.mulPose(rotation);

            float[] xyRot = MathUtil.computeXYRot(dir);
            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
            poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));

            poseStack.mulPose(Axis.ZP.rotationDegrees(summon.getEntityData().get(StaticSummon.RZP)));
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
        }

        public void applyTranslate(PoseStack poseStack, Vector3f dir, Vector3f right, double angle, double dist) {
            float cos = (float) Math.cos(Math.toRadians(angle));
            float sin = (float) Math.sin(Math.toRadians(angle));
            Vector3f current = new Vector3f(
                cos * right.x  + sin * dir.x,
                cos * right.y  + sin * dir.y,
                cos * right.z  + sin * dir.z
            );
            poseStack.translate(current.x * dist, current.y * dist, current.z * dist);
        }

        public void renderQuad(Matrix4f matrix, VertexConsumer consumer, float r, float g, float b, float a, float halfWidth, float halfHeight, float offsetX, float offsetY, float offsetZ) {
            int ir = (int) (r * 255.0F);
            int ig = (int) (g * 255.0F);
            int ib = (int) (b * 255.0F);
            int ia = (int) (a * 255.0F);

            consumer.addVertex(matrix, -halfWidth, -halfHeight + offsetY, offsetZ)
                    .setColor(ir, ig, ib, ia).setUv(0.0f, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            consumer.addVertex(matrix, halfWidth, -halfHeight + offsetY, offsetZ)
                    .setColor(ir, ig, ib, ia).setUv(1.0f, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            consumer.addVertex(matrix, halfWidth, halfHeight + offsetY, offsetZ)
                    .setColor(ir, ig, ib, ia).setUv(1.0f, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            consumer.addVertex(matrix, -halfWidth, halfHeight + offsetY, offsetZ)
                    .setColor(ir, ig, ib, ia).setUv(0.0f, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
        }


        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);

            //运动逻辑
            if(!customData.contains("dead")) {
                float progress = summon.getEntityData().get(StaticSummon.AGE) / (float) summon.getEntityData().get(StaticSummon.LIFETIME);
                if(progress > FADE_IN) {
                    summon.setDeltaMovement(summon.getLookAngle().normalize().scale(SPEED - SPEED * (progress - FADE_IN) / (1.0f - FADE_IN)));
                }else {
                    summon.setDeltaMovement(summon.getLookAngle().normalize().scale(SPEED));
                }
            }else {
                summon.setDeltaMovement(summon.getLookAngle().normalize().scale(0.5));
                return;
            }

            //碰撞箱计算
            Vec3 center = summon.position().add(summon.getLookAngle().normalize().scale(SwordBeam.DIST));
            if(customData.contains("extX") && customData.contains("extY") && customData.contains("extZ")) {
                summon.setBoundingBox(new AABB(
                    center.x - customData.getInt("extX"), center.y - customData.getInt("extY"), center.z - customData.getInt("extZ"),
                    center.x + customData.getInt("extX"), center.y + customData.getInt("extY"), center.z + customData.getInt("extZ")
                ));
            }

            //碰撞计算
            if(!summon.level().isClientSide() && customData.contains("hitCount") && summon.getOwner() instanceof Player player) {
                int count = customData.getInt("hitCount");
                LzxnoneTerraria.LOGGER.info("11w111");
                List<LivingEntity> targets = summon.level().getEntitiesOfClass(LivingEntity.class, summon.getBoundingBox(), FilterUtil.createTargetFilter(summon, summon.getOwner()));
                for(LivingEntity target : targets) {
                    if(target.hurt(summon.damageSources().playerAttack(player), DAMAGE * (float) Math.pow(DAMAGE_PUNISHMENT, count))) {
                        count++;
                        //粒子效果
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
                Quaternionf rotationX = new Quaternionf().fromAxisAngleRad(MathUtil.toVector3f(summon.getLookAngle()), (float) Math.toRadians(summon.getEntityData().get(StaticSummon.RZP)));
                Vector3f[] dirs = MathUtil.computeDir(
                    MathUtil.toVector3f(summon.getLookAngle()),
                    MathUtil.computeDir(MathUtil.toVector3f(summon.getLookAngle()))[2].rotate(rotationX)
                );
                Vec3 right = MathUtil.toVec3(dirs[2]).normalize();
                Vec3 speed = MathUtil.toVec3(dirs[0]).normalize().scale(summon.getDeltaMovement().length() / 2);
                for(int i = 0;i < 5;i++) {
                    Vec3 delta = right.scale((Math.random() - 0.5) * 4);
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

    public static void summon(Player player) {
        StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), player.level());
        summon.setOwner(player);
        Vec3 pos = new Vec3(player.getX(), player.getEyeY() - 0.1, player.getZ());
        summon.setPos(pos);

        float[] xyRot = MathUtil.computeXYRot(MathUtil.toVector3f(player.getLookAngle()));
        summon.setXRot(xyRot[0]);
        summon.xRotO = xyRot[0];
        summon.setYRot(xyRot[1]);
        summon.yRotO = xyRot[1];

        int randomAngle = (int) ((Math.random() * 2 - 1) * 60);

        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.TERRA_BLADE_BEAM);
        summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
        summon.getEntityData().set(StaticSummon.LIFETIME, 40);
        summon.getEntityData().set(StaticSummon.RZP, randomAngle);
        summon.getEntityData().set(StaticSummon.GLOW, true);
        summon.setNoGravity(true);
        summon.noPhysics = true;

        CompoundTag customData = new CompoundTag();

        Quaternionf rotation = new Quaternionf()
            .rotateY((float) Math.toRadians(-xyRot[1]))
            .rotateX((float) Math.toRadians(xyRot[0]))
            .rotateZ((float) Math.toRadians(randomAngle))
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
        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

        summon.setDeltaMovement(player.getLookAngle().normalize().scale(SPEED));

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
                summon(player);
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
                    summon(player);
                }else {
                    SoundUtil.playClientSound(player, ModSounds.WAVE.get());
                }
            }
            event.setCanceled(true);
        }
    };
}
