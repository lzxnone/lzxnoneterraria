package com.lzxnone.terraria.client.item.magic;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.client.entity.summon.IStaticSummonRenderBehavior;
import com.lzxnone.terraria.client.event.ShaderRegistry;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.utils.MathUtil;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
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

public class ClientNightglow {
    public static final ResourceLocation RES = ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/normal_trail2.png");

    // 超椭圆体尺寸：宽(X)=0.20, 高(Y)=0.20, 长(Z)=0.40（长为宽和高的两倍，宽和高相等）
    private static final float HALF_WIDTH = 0.60f;
    private static final float HALF_HEIGHT = 0.60f;
    private static final float HALF_LENGTH = 1.20f;

    // 消光系数, 核心比例, 外发光宽度, 核心亮度
    private static final float EXTINCTION = 6.0f;
    private static final float CORE_RATIO = 0.55f;
    private static final float AURA_WIDTH = 0.15f;
    private static final float CORE_BRIGHTNESS = 2.5f;

    // 尾迹平面旋转角度（双交叉平面形成3D立体感）
    private static final float[] TRAIL_PLANE_ANGLES = new float[]{0.0f, 90.0f};

    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR = new IStaticSummonRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            IStaticSummonRenderBehavior.super.renderItem(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
            if(!(entity instanceof StaticSummon summon)) return;

            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            Vector3f color = new Vector3f(
                customData.contains("colorR") ? customData.getFloat("colorR") : 1.0f,
                customData.contains("colorG") ? customData.getFloat("colorG") : 1.0f,
                customData.contains("colorB") ? customData.getFloat("colorB") : 1.0f
            );

            // 1. 渲染 n=0.5 超椭圆体体积能量体
            renderSuperellipsoid(summon, color, partialTick, poseStack, bufferSource);

            // 2. 渲染优化后的双条带/双层尾迹
            renderTrail(summon, color, partialTick, poseStack, bufferSource);
        }
    };

    private static void renderSuperellipsoid(StaticSummon summon, Vector3f color, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
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

            // 计算局部坐标系：dir 为朝向(Z), up 为高(Y), right 为宽(X)
            Vector3f[] dirs = MathUtil.computeCoordinateSystem(summon);
            Matrix4f orientation = new Matrix4f(
                dirs[2].x, dirs[2].y, dirs[2].z, 0.0f, // col 0: right (X 宽方向)
                dirs[1].x, dirs[1].y, dirs[1].z, 0.0f, // col 1: up (Y 高方向)
                dirs[0].x, dirs[0].y, dirs[0].z, 0.0f, // col 2: dir (Z 长/前进朝向)
                0.0f,      0.0f,      0.0f,      1.0f  // col 3: translation
            );
            poseStack.mulPose(orientation);

            Matrix4f modelMatrix = new Matrix4f(poseStack.last().pose());
            float renderTime = summon.tickCount + partialTick;

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
                dimensionsUniform.set(HALF_WIDTH, HALF_HEIGHT, HALF_LENGTH);
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
            RenderSystem.depthMask(false);
            RenderSystem.enableCull();

            BufferBuilder bb = RenderSystem.renderThreadTesselator().begin(
                VertexFormat.Mode.TRIANGLES,
                DefaultVertexFormat.POSITION_COLOR
            );

            // AABB 包围盒
            float pad = 1.08f;
            float x = HALF_WIDTH * pad;
            float y = HALF_HEIGHT * pad;
            float z = HALF_LENGTH * pad;

            // 6 个面，12 个三角形（逆时针正面向外）
            // +Z 面 (dir 前方)
            addCubeVertex(bb, -x, -y,  z);
            addCubeVertex(bb,  x, -y,  z);
            addCubeVertex(bb,  x,  y,  z);
            addCubeVertex(bb, -x, -y,  z);
            addCubeVertex(bb,  x,  y,  z);
            addCubeVertex(bb, -x,  y,  z);

            // -Z 面 (dir 后方)
            addCubeVertex(bb,  x, -y, -z);
            addCubeVertex(bb, -x, -y, -z);
            addCubeVertex(bb, -x,  y, -z);
            addCubeVertex(bb,  x, -y, -z);
            addCubeVertex(bb, -x,  y, -z);
            addCubeVertex(bb,  x,  y, -z);

            // +X 面 (right 右方)
            addCubeVertex(bb,  x, -y,  z);
            addCubeVertex(bb,  x, -y, -z);
            addCubeVertex(bb,  x,  y, -z);
            addCubeVertex(bb,  x, -y,  z);
            addCubeVertex(bb,  x,  y, -z);
            addCubeVertex(bb,  x,  y,  z);

            // -X 面 (right 左方)
            addCubeVertex(bb, -x, -y, -z);
            addCubeVertex(bb, -x, -y,  z);
            addCubeVertex(bb, -x,  y,  z);
            addCubeVertex(bb, -x, -y, -z);
            addCubeVertex(bb, -x,  y,  z);
            addCubeVertex(bb, -x,  y, -z);

            // +Y 面 (up 上方)
            addCubeVertex(bb, -x,  y,  z);
            addCubeVertex(bb,  x,  y,  z);
            addCubeVertex(bb,  x,  y, -z);
            addCubeVertex(bb, -x,  y,  z);
            addCubeVertex(bb,  x,  y, -z);
            addCubeVertex(bb, -x,  y, -z);

            // -Y 面 (up 下方)
            addCubeVertex(bb, -x, -y, -z);
            addCubeVertex(bb,  x, -y, -z);
            addCubeVertex(bb,  x, -y,  z);
            addCubeVertex(bb, -x, -y, -z);
            addCubeVertex(bb,  x, -y,  z);
            addCubeVertex(bb, -x, -y,  z);

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

    private static void renderTrail(StaticSummon summon, Vector3f color, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
        LinkedList<Vec3> positions = summon.trailPositions;
        if(positions.size() < 4) return;

        VertexConsumer vertexConsumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES));
        Matrix4f matrix = poseStack.last().pose();
        Vec3 renderOrigin = summon.getPosition(partialTick);

        int sectionCount = positions.size() / 2;
        int quadCount = sectionCount - 1;

        for(float planeAngle : TRAIL_PLANE_ANGLES) {
            for(int i = 0; i < quadCount; i++) {
                Vec3 pLeft0 = positions.get(i * 2);
                Vec3 pRight0 = positions.get(i * 2 + 1);
                Vec3 pLeft1 = positions.get((i + 1) * 2);
                Vec3 pRight1 = positions.get((i + 1) * 2 + 1);

                Vec3 center0 = pLeft0.add(pRight0).scale(0.5D);
                Vec3 center1 = pLeft1.add(pRight1).scale(0.5D);
                Vec3 half0 = pRight0.subtract(pLeft0).scale(0.5D);
                Vec3 half1 = pRight1.subtract(pLeft1).scale(0.5D);
                Vec3 segmentDir = center1.subtract(center0);

                if(planeAngle != 0.0f && segmentDir.lengthSqr() > 1.0E-6D) {
                    Quaternionf rotation = new Quaternionf().fromAxisAngleRad(
                        segmentDir.normalize().toVector3f(),
                        (float) Math.toRadians(planeAngle)
                    );
                    half0 = MathUtil.toVec3(half0.toVector3f().rotate(rotation));
                    half1 = MathUtil.toVec3(half1.toVector3f().rotate(rotation));
                }

                float widthScale0 = 1.0f - (float) i / sectionCount;
                float widthScale1 = 1.0f - (float) (i + 1) / sectionCount;

                float u0 = (float) i / quadCount;
                float u1 = (float) (i + 1) / quadCount;

                // 双层条带：Layer 0 为外层彩色光翼，Layer 1 为内层纯白强光激光芯
                for(int layer = 0; layer < 2; layer++) {
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
