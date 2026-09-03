package com.lzxnone.terraria.client.item.magic;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.client.entity.projectile.IStaticProjectileRenderBehavior;
import com.lzxnone.terraria.client.event.ShaderRegistry;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.item.weapon.magic.gem_staff.GemStaff;
import com.lzxnone.terraria.utils.MathUtil;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.GlStateBackup;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.LinkedList;
import java.util.Objects;

public class ClientGemStaff {
    public static final ResourceLocation TRAIL_RES = ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/normal_trail2.png");
    public static final ResourceLocation SPARKLE_RES = ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/beam_sparkle.png");

    // 超椭圆体基础尺寸：a = b = c
    private static final float BASE_RADIUS = 0.75f;
    private static final float DIAMOND_RADIUS = 1.5f;

    // 消光系数, 核心比例, 外发光宽度, 核心亮度
    private static final float EXTINCTION = 6.0f;
    private static final float CORE_RATIO = 0.55f;
    private static final float AURA_WIDTH = 0.15f;
    private static final float CORE_BRIGHTNESS = 2.5f;

    // 尾迹平面旋转角度（双交叉平面形成立体感）
    private static final float[] TRAIL_PLANE_ANGLES = new float[]{0.0f, 90.0f};

    public static final IStaticProjectileRenderBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if (!(entity instanceof StaticProjectile proj)) return;

            CompoundTag customData = proj.getEntityData().get(StaticProjectile.CUSTOM_DATA);
            int skill = customData.getInt("skill");
            boolean diamond = (skill & GemStaff.SKILL_DIAMOND) != 0;

            Vector3f color = new Vector3f(
                customData.contains("colorR") ? customData.getFloat("colorR") : proj.getEntityData().get(StaticProjectile.COLOR_R),
                customData.contains("colorG") ? customData.getFloat("colorG") : proj.getEntityData().get(StaticProjectile.COLOR_G),
                customData.contains("colorB") ? customData.getFloat("colorB") : proj.getEntityData().get(StaticProjectile.COLOR_B)
            );

            // 1. 渲染 a=b=c 超椭圆体体积能量体（钻石尺寸更大）
            renderSuperellipsoid(proj, color, diamond, partialTick, poseStack, bufferSource);

            // 2. 渲染优化后的双层尾迹
            renderTrail(proj, color, partialTick, poseStack, bufferSource);

            // 3. 钻石专属：面向公告板的旋转 beam_sparkle
            if (diamond) {
                renderSparkle(proj, color, partialTick, poseStack, bufferSource);
            }
        }
    };

    private static void renderSuperellipsoid(StaticProjectile proj, Vector3f color, boolean diamond, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
        ShaderInstance shader = ShaderRegistry.getNightglowSuperellipsoid();
        if (shader == null) return;

        poseStack.pushPose();

        GlStateBackup backup = new GlStateBackup();
        RenderSystem.backupGlState(backup);
        ShaderInstance prevShader = RenderSystem.getShader();

        try {
            if (bufferSource instanceof MultiBufferSource.BufferSource source) {
                source.endBatch();
            }

            Vector3f motion = proj.getDeltaMovement().lengthSqr() > 1.0E-6D ?
                proj.getDeltaMovement().toVector3f() :
                proj.getEntityData().get(StaticProjectile.DIRECTION);
            Vector3f[] dirs = MathUtil.computeCoordinateSystem(motion, proj.getYRot());

            Matrix4f orientation = new Matrix4f(
                dirs[2].x, dirs[2].y, dirs[2].z, 0.0f, // col 0: right (X)
                dirs[1].x, dirs[1].y, dirs[1].z, 0.0f, // col 1: up (Y)
                dirs[0].x, dirs[0].y, dirs[0].z, 0.0f, // col 2: dir (Z)
                0.0f,      0.0f,      0.0f,      1.0f  // col 3: translation
            );
            poseStack.mulPose(orientation);

            Matrix4f modelMatrix = new Matrix4f(poseStack.last().pose());
            float renderTime = proj.tickCount + partialTick;

            float radius = diamond ? DIAMOND_RADIUS : BASE_RADIUS;
            CompoundTag customData = proj.getEntityData().get(StaticProjectile.CUSTOM_DATA);
            int skill = customData.getInt("skill");
            boolean emerald = (skill & GemStaff.SKILL_EMERALD) != 0;
            if(emerald) {
                float ratio = 1.0f - (float) proj.getEntityData().get(StaticProjectile.AGE) / proj.getEntityData().get(StaticProjectile.LIFETIME);
                radius = radius * ratio;
            }

            Uniform modelMat = shader.getUniform("ModelMat");
            if (modelMat != null) modelMat.set(modelMatrix);

            Uniform cameraLocalUniform = shader.getUniform("CameraLocal");
            if (cameraLocalUniform != null) {
                Vector3f cameraLocal = new Matrix4f(RenderSystem.getModelViewMatrix())
                    .mul(modelMatrix)
                    .invert()
                    .transformPosition(new Vector3f());
                cameraLocalUniform.set(cameraLocal.x, cameraLocal.y, cameraLocal.z);
            }

            Uniform dimensionsUniform = shader.getUniform("Dimensions");
            if (dimensionsUniform != null) {
                dimensionsUniform.set(radius, radius, radius);
            }

            Uniform volumeParametersUniform = shader.getUniform("VolumeParameters");
            if (volumeParametersUniform != null) {
                volumeParametersUniform.set(EXTINCTION, CORE_RATIO, AURA_WIDTH, CORE_BRIGHTNESS);
            }

            Uniform colorUniform = shader.getUniform("SuperellipsoidColor");
            if (colorUniform != null) {
                colorUniform.set(color.x, color.y, color.z);
            }

            Uniform flowTimeUniform = shader.getUniform("FlowTime");
            if (flowTimeUniform != null) {
                flowTimeUniform.set(renderTime);
            }

            // GL 状态：加法混合 + 深度测试 + 启用背面剔除
            RenderSystem.setShader(() -> shader);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE
            );
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.enableCull();

            BufferBuilder bb = RenderSystem.renderThreadTesselator().begin(
                VertexFormat.Mode.TRIANGLES,
                DefaultVertexFormat.POSITION_COLOR
            );

            // 立方体 AABB 包围盒
            float pad = 1.08f;
            float r = radius * pad;

            // 6 个面，12 个三角形（正面向外）
            // +Z 面
            addCubeVertex(bb, -r, -r,  r);
            addCubeVertex(bb,  r, -r,  r);
            addCubeVertex(bb,  r,  r,  r);
            addCubeVertex(bb, -r, -r,  r);
            addCubeVertex(bb,  r,  r,  r);
            addCubeVertex(bb, -r,  r,  r);

            // -Z 面
            addCubeVertex(bb,  r, -r, -r);
            addCubeVertex(bb, -r, -r, -r);
            addCubeVertex(bb, -r,  r, -r);
            addCubeVertex(bb,  r, -r, -r);
            addCubeVertex(bb, -r,  r, -r);
            addCubeVertex(bb,  r,  r, -r);

            // +X 面
            addCubeVertex(bb,  r, -r,  r);
            addCubeVertex(bb,  r, -r, -r);
            addCubeVertex(bb,  r,  r, -r);
            addCubeVertex(bb,  r, -r,  r);
            addCubeVertex(bb,  r,  r, -r);
            addCubeVertex(bb,  r,  r,  r);

            // -X 面
            addCubeVertex(bb, -r, -r, -r);
            addCubeVertex(bb, -r, -r,  r);
            addCubeVertex(bb, -r,  r,  r);
            addCubeVertex(bb, -r, -r, -r);
            addCubeVertex(bb, -r,  r,  r);
            addCubeVertex(bb, -r,  r, -r);

            // +Y 面
            addCubeVertex(bb, -r,  r,  r);
            addCubeVertex(bb,  r,  r,  r);
            addCubeVertex(bb,  r,  r, -r);
            addCubeVertex(bb, -r,  r,  r);
            addCubeVertex(bb,  r,  r, -r);
            addCubeVertex(bb, -r,  r, -r);

            // -Y 面
            addCubeVertex(bb, -r, -r, -r);
            addCubeVertex(bb,  r, -r, -r);
            addCubeVertex(bb,  r, -r,  r);
            addCubeVertex(bb, -r, -r, -r);
            addCubeVertex(bb,  r, -r,  r);
            addCubeVertex(bb, -r, -r,  r);

            BufferUploader.drawWithShader(Objects.requireNonNull(bb.build()));
        } finally {
            poseStack.popPose();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.defaultBlendFunc();
            if (prevShader != null) {
                RenderSystem.setShader(() -> prevShader);
            }
            RenderSystem.restoreGlState(backup);
        }
    }

    private static void addCubeVertex(BufferBuilder bb, float x, float y, float z) {
        bb.addVertex(x, y, z).setColor(255, 255, 255, 255);
    }

    private static void renderSparkle(StaticProjectile proj, Vector3f color, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
        poseStack.pushPose();
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        //float age = proj.tickCount + partialTick;
        poseStack.mulPose(Axis.ZP.rotationDegrees(45));

        float size = 2.0f;
        CompoundTag customData = proj.getEntityData().get(StaticProjectile.CUSTOM_DATA);
        int skill = customData.getInt("skill");
        boolean emerald = (skill & GemStaff.SKILL_EMERALD) != 0;
        if(emerald) {
            float ratio = 1.0f - (float) proj.getEntityData().get(StaticProjectile.AGE) / proj.getEntityData().get(StaticProjectile.LIFETIME);
            size = size * ratio;
        }
        VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.entityAdditiveEmissive(SPARKLE_RES));
        Matrix4f matrix = poseStack.last().pose();

        consumer.addVertex(matrix, -size, -size, 0.0f)
            .setColor(color.x, color.y, color.z, 0.95f)
            .setUv(0.0f, 1.0f)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT)
            .setNormal(0.0f, 1.0f, 0.0f);
        consumer.addVertex(matrix, size, -size, 0.0f)
            .setColor(color.x, color.y, color.z, 0.95f)
            .setUv(1.0f, 1.0f)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT)
            .setNormal(0.0f, 1.0f, 0.0f);
        consumer.addVertex(matrix, size, size, 0.0f)
            .setColor(color.x, color.y, color.z, 0.95f)
            .setUv(1.0f, 0.0f)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT)
            .setNormal(0.0f, 1.0f, 0.0f);
        consumer.addVertex(matrix, -size, size, 0.0f)
            .setColor(color.x, color.y, color.z, 0.95f)
            .setUv(0.0f, 0.0f)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT)
            .setNormal(0.0f, 1.0f, 0.0f);

        poseStack.popPose();
    }

    private static void renderTrail(StaticProjectile proj, Vector3f color, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
        LinkedList<Vec3> positions = proj.trailPositions;
        if (positions.size() < 6) return;

        VertexConsumer vertexConsumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(TRAIL_RES));
        Matrix4f matrix = poseStack.last().pose();
        Vec3 renderOrigin = proj.getPosition(partialTick);

        int sectionCount = positions.size() / 2;
        int quadCount = sectionCount - 1;

        for (float planeAngle : TRAIL_PLANE_ANGLES) {
            // 第一段尾迹不渲染 (i 从 1 开始)，防止尾迹超出飞弹位置
            for (int i = 1; i < quadCount; i++) {
                Vec3 pLeft0 = positions.get(i * 2);
                Vec3 pRight0 = positions.get(i * 2 + 1);
                Vec3 pLeft1 = positions.get((i + 1) * 2);
                Vec3 pRight1 = positions.get((i + 1) * 2 + 1);

                Vec3 center0 = pLeft0.add(pRight0).scale(0.5D);
                Vec3 center1 = pLeft1.add(pRight1).scale(0.5D);
                Vec3 half0 = pRight0.subtract(pLeft0).scale(0.5D);
                Vec3 half1 = pRight1.subtract(pLeft1).scale(0.5D);
                Vec3 segmentDir = center1.subtract(center0);

                if (planeAngle != 0.0f && segmentDir.lengthSqr() > 1.0E-6D) {
                    Quaternionf rotation = new Quaternionf().fromAxisAngleRad(
                        segmentDir.normalize().toVector3f(),
                        (float) Math.toRadians(planeAngle)
                    );
                    half0 = MathUtil.toVec3(half0.toVector3f().rotate(rotation));
                    half1 = MathUtil.toVec3(half1.toVector3f().rotate(rotation));
                }

                float widthScale0 = 1.0f - (float) (i - 1) / (quadCount - 1);
                float widthScale1 = 1.0f - (float) i / (quadCount - 1);

                float u0 = (float) (i - 1) / (quadCount - 1);
                float u1 = (float) i / (quadCount - 1);

                // 双层条带：Layer 0 为外层彩色光翼，Layer 1 为内层纯白强光激光芯
                for (int layer = 0; layer < 2; layer++) {
                    boolean isCore = (layer == 1);
                    float layerWidthMult = isCore ? 0.40f : 1.10f;
                    float r0 = isCore ? 1.0f : color.x;
                    float g0 = isCore ? 1.0f : color.y;
                    float b0 = isCore ? 1.0f : color.z;
                    float a0 = isCore ? widthScale0 : widthScale0 * 0.85f;
                    float a1 = isCore ? widthScale1 : widthScale1 * 0.85f;

                    Vec3 scaledHalf0 = half0.scale(widthScale0 * layerWidthMult);
                    Vec3 scaledHalf1 = half1.scale(widthScale1 * layerWidthMult);

                    Vec3 left0 = center0.subtract(scaledHalf0).subtract(renderOrigin);
                    Vec3 right0 = center0.add(scaledHalf0).subtract(renderOrigin);
                    Vec3 left1 = center1.subtract(scaledHalf1).subtract(renderOrigin);
                    Vec3 right1 = center1.add(scaledHalf1).subtract(renderOrigin);

                    vertexConsumer.addVertex(matrix, (float) left0.x, (float) left0.y, (float) left0.z)
                        .setColor(r0, g0, b0, a0).setUv(u0, 0.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                    vertexConsumer.addVertex(matrix, (float) right0.x, (float) right0.y, (float) right0.z)
                        .setColor(r0, g0, b0, a0).setUv(u0, 1.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                    vertexConsumer.addVertex(matrix, (float) right1.x, (float) right1.y, (float) right1.z)
                        .setColor(r0, g0, b0, a1).setUv(u1, 1.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                    vertexConsumer.addVertex(matrix, (float) left1.x, (float) left1.y, (float) left1.z)
                        .setColor(r0, g0, b0, a1).setUv(u1, 0.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                }
            }
        }
    }
}
