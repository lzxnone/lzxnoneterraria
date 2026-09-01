package com.lzxnone.terraria.client.item.ranged;

import com.lzxnone.terraria.client.entity.projectile.IStaticProjectileRenderBehavior;
import com.lzxnone.terraria.client.entity.summon.IStaticSummonRenderBehavior;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.TintedVertexConsumer;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.utils.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class ClientSuperStarShooter {
    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR = new IStaticSummonRenderBehavior() {
        public static final ResourceLocation RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/star_wave.png");

        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;
            ItemStack itemStack = summon.getEntityData().get(StaticSummon.ITEM);
            if(itemStack == ItemStack.EMPTY) return;

            float age = summon.getEntityData().get(StaticSummon.AGE) + partialTick;

            poseStack.pushPose();

            //旋转(方向修正)
            poseStack.mulPose(Axis.YP.rotationDegrees(-summon.getYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(summon.getXRot()));

            poseStack.mulPose(Axis.XP.rotationDegrees(age * 15));
            poseStack.mulPose(Axis.YP.rotationDegrees(-90));

            poseStack.scale(
                summon.getEntityData().get(StaticSummon.SCALE_X),
                summon.getEntityData().get(StaticSummon.SCALE_Y),
                summon.getEntityData().get(StaticSummon.SCALE_Z)
            );

            Minecraft.getInstance().getItemRenderer().renderStatic(
                itemStack,
                ItemDisplayContext.NONE,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                renderType -> {
                    VertexConsumer vertexConsumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(InventoryMenu.BLOCK_ATLAS));
                    return new TintedVertexConsumer(vertexConsumer,
                        1.0f,
                        0.95f,
                        0.2f,
                        1.0f);
                },
                entity.level(),
                0
            );
            poseStack.popPose();

            poseStack.pushPose();
            float starHalfWidth = 45 * 0.06f;
            float starHalfHeight = 17 * 0.06f;
            VertexConsumer starConsumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES));

            Vec3 dir = summon.getLookAngle().normalize();
            double dist = -0.75;
            poseStack.translate(dir.x * dist, dir.y * dist, dir.z * dist);

            poseStack.mulPose(Axis.YP.rotationDegrees(-summon.getYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(summon.getXRot()));

            poseStack.mulPose(Axis.YP.rotationDegrees(270));

            poseStack.scale(
                summon.getEntityData().get(StaticSummon.SCALE_X),
                summon.getEntityData().get(StaticSummon.SCALE_Y),
                summon.getEntityData().get(StaticSummon.SCALE_Z)
            );

            Vector3f color = new Vector3f(1.0f, 0.72f, 0.35f);
            float alpha = 0.5f;
            for(int i = 0; i < 3; i++) {
                starConsumer.addVertex(poseStack.last().pose(), -starHalfWidth, -starHalfHeight, 0f)
                    .setColor(color.x, color.y, color.z, alpha).setUv(0.0f, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                starConsumer.addVertex(poseStack.last().pose(), starHalfWidth, -starHalfHeight, 0f)
                    .setColor(color.x, color.y, color.z, alpha).setUv(1.0f, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                starConsumer.addVertex(poseStack.last().pose(), starHalfWidth, starHalfHeight, 0f)
                    .setColor(color.x, color.y, color.z, alpha).setUv(1.0f, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                starConsumer.addVertex(poseStack.last().pose(), -starHalfWidth, starHalfHeight, 0f)
                    .setColor(color.x, color.y, color.z, alpha).setUv(0.0f, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                poseStack.mulPose(Axis.XP.rotationDegrees(60.0F));
            }
            poseStack.popPose();
        }
    };

    private static void renderPrismaticBolt(StaticProjectile projectile, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation res, float r, float g, float b, float a) {
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

        float starHalfWidth = 72 * 0.02f;
        float starHalfHeight = 72 * 0.02f * 2.0f;

        Vec3 w = right.scale(starHalfWidth);
        Vec3 l = dir.scale(starHalfHeight);

        // 顶点生成：沿 dir 方向拉伸，沿 right 方向铺展，法线完全正对摄像机
        Vec3 v0 = w.scale(-1).subtract(l);
        Vec3 v1 = w.subtract(l);
        Vec3 v2 = w.add(l);
        Vec3 v3 = w.scale(-1).add(l);

        VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.entityAdditiveEmissive(res));
        org.joml.Matrix4f matrix = poseStack.last().pose();

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

    public static final IStaticProjectileRenderBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileRenderBehavior() {
        public static final ResourceLocation RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/prismatic_bolt.png");

        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile projectile)) return;
            renderPrismaticBolt(projectile, partialTick, poseStack, bufferSource, RES, 1.0f, 1.0f, 1.0f, 1.0f);
        }
    };
}
