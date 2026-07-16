package com.lzxnone.terraria.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RenderUtil {
    public static void applyRotate(PoseStack poseStack, Vector3f dir, Vector3f up, double angle, double rotate) {
        Quaternionf rotation = new Quaternionf()
            .fromAxisAngleRad(up, (float) Math.toRadians(angle));
        Quaternionf rotation2 = new Quaternionf()
            .fromAxisAngleRad(dir, (float) Math.toRadians(Math.abs(dir.y) > 0.999 ? 0 : rotate));
        poseStack.mulPose(rotation);
        poseStack.mulPose(rotation2);

        float[] xyRot = MathUtil.computeXYRot(dir, up);
        poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
        poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));

        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
    }

    public static void applyTranslate(PoseStack poseStack, Vector3f dir, Vector3f right, double angle, double dist) {
        float cos = (float) Math.cos(Math.toRadians(angle));
        float sin = (float) Math.sin(Math.toRadians(angle));
        Vector3f current = new Vector3f(
            cos * right.x  + sin * dir.x,
            cos * right.y  + sin * dir.y,
            cos * right.z  + sin * dir.z
        ).normalize();
        poseStack.translate(current.x * dist, current.y * dist, current.z * dist);
    }

    public static void renderQuad(Matrix4f matrix, VertexConsumer consumer, float r, float g, float b, float a, float halfWidth, float halfHeight, float offsetX, float offsetY, float offsetZ) {
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

    public static void renderGlow(Matrix4f matrix, VertexConsumer consumer,
                                  float r, float g, float b, float baseAlpha,
                                  float halfWidth, float halfHeight,
                                  int layers, float scaleStep, float alphaDecay,
                                  float offsetX, float offsetY, float offsetZ) {
        float scale = 1.0f;
        float a = baseAlpha;
        for (int i = 0; i < layers; i++) {
            renderQuad(matrix, consumer, r, g, b, a,
                    halfWidth * scale, halfHeight * scale,
                    offsetX, offsetY, offsetZ);
            scale += scaleStep;
            a *= alphaDecay;
        }
    }
}
