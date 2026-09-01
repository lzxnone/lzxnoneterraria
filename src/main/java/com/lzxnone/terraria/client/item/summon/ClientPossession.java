package com.lzxnone.terraria.client.item.summon;

import com.lzxnone.terraria.client.entity.projectile.IStaticProjectileRenderBehavior;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.utils.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class ClientPossession {
    public static final IStaticProjectileRenderBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileRenderBehavior() {
        private static final ResourceLocation RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/possession_beam.png");

        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile projectile)) return;
            if(projectile.trailPositions.size() < 4) return;

            VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.entityAdditiveEmissive(RES));
            Matrix4f matrix = poseStack.last().pose();
            Vec3 origin = projectile.getPosition(partialTick);
            int pairCount = projectile.trailPositions.size() / 2;
            int quadCount = pairCount - 1;
            if(quadCount <= 0) return;

            float lifetime = Math.max(1.0F, projectile.getEntityData().get(StaticProjectile.LIFETIME));
            float age = projectile.getEntityData().get(StaticProjectile.AGE) + partialTick;
            float baseAlpha = Math.max(0.0F, 1.0F - Math.max(0.0F, age - lifetime * 0.75F) / (lifetime * 0.25F));

            for(int i = 0; i < quadCount; i++) {
                Vec3 headRight = projectile.trailPositions.get(i * 2);
                Vec3 headLeft = projectile.trailPositions.get(i * 2 + 1);
                Vec3 tailRight = projectile.trailPositions.get((i + 1) * 2);
                Vec3 tailLeft = projectile.trailPositions.get((i + 1) * 2 + 1);

                float headU = 1.0F - i / (float)quadCount;
                float tailU = 1.0F - (i + 1) / (float)quadCount;

                for(int k = 0; k < 3; k++) {
                    addVertex(consumer, matrix, headRight.subtract(origin), headU, 0.0F, 1.0f);
                    addVertex(consumer, matrix, headLeft.subtract(origin), headU, 1.0F, 1.0f);
                    addVertex(consumer, matrix, tailLeft.subtract(origin), tailU, 1.0F, 1.0f);
                    addVertex(consumer, matrix, tailRight.subtract(origin), tailU, 0.0F, 1.0f);
                }
            }
        }

        private static void addVertex(VertexConsumer consumer, Matrix4f matrix, Vec3 pos, float u, float v, float alpha) {
            consumer.addVertex(matrix, (float)pos.x, (float)pos.y, (float)pos.z)
                .setColor(1.0F, 1.0F, 1.0F, alpha)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0.0F, 1.0F, 0.0F);
        }
    };

    public static final IStaticProjectileRenderBehavior BOLT_PROJECTILE_BEHAVIOR = new IStaticProjectileRenderBehavior() {
        private static final ResourceLocation RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/prismatic_bolt.png");

        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile projectile)) return;

            Vec3 projPos = projectile.getPosition(partialTick);
            Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            Vec3 toCam = camPos.subtract(projPos);

            Vec3 dir = projectile.getDeltaMovement();
            if(dir.lengthSqr() < 1.0E-6D) {
                dir = MathUtil.toVec3(projectile.getEntityData().get(StaticProjectile.DIRECTION));
            }
            if(dir.lengthSqr() < 1.0E-6D) {
                dir = new Vec3(0, 0, 1);
            }
            dir = dir.normalize();

            // 计算垂直于飞行方向且垂直于摄像机视线的侧向展开向量 right
            Vec3 right = dir.cross(toCam);
            if(right.lengthSqr() < 1.0E-6D) {
                right = MathUtil.toVec3(projectile.getEntityData().get(StaticProjectile.RIGHT));
            }
            if(right.lengthSqr() < 1.0E-6D) {
                right = new Vec3(1, 0, 0);
            }
            right = right.normalize();

            float starHalfWidth = 72 * 0.0075f;
            float starHalfHeight = 72 * 0.0075f * 2.0f;

            Vec3 w = right.scale(starHalfWidth);
            Vec3 l = dir.scale(starHalfHeight);

            // 顶点生成：沿 dir 方向拉伸，沿 right 方向铺展，法线完全正对摄像机
            Vec3 v0 = w.scale(-1).subtract(l);
            Vec3 v1 = w.subtract(l);
            Vec3 v2 = w.add(l);
            Vec3 v3 = w.scale(-1).add(l);

            VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.entityAdditiveEmissive(RES));
            Matrix4f matrix = poseStack.last().pose();

            float r = 0.15f;
            float g = 0.85f;
            float b = 1.0f;
            float a = 1.0f;

            consumer.addVertex(matrix, (float)v0.x, (float)v0.y, (float)v0.z)
                .setColor(r, g, b, a).setUv(0.0f, 1.0f)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            consumer.addVertex(matrix, (float)v1.x, (float)v1.y, (float)v1.z)
                .setColor(r, g, b, a).setUv(1.0f, 1.0f)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            consumer.addVertex(matrix, (float)v2.x, (float)v2.y, (float)v2.z)
                .setColor(r, g, b, a).setUv(1.0f, 0.0f)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            consumer.addVertex(matrix, (float)v3.x, (float)v3.y, (float)v3.z)
                .setColor(r, g, b, a).setUv(0.0f, 0.0f)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
        }
    };
}
