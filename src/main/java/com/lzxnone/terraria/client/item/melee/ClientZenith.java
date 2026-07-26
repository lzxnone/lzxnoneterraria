package com.lzxnone.terraria.client.item.melee;

import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.TintedVertexConsumer;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.utils.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import com.lzxnone.terraria.client.entity.projectile.IStaticProjectileRenderBehavior;
import static com.lzxnone.terraria.item.weapon.melee.Zenith.*;

public class ClientZenith {
    public static final IStaticProjectileRenderBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile proj)) return;
            ItemStack stack = proj.getEntityData().get(StaticProjectile.ITEM);
            if(stack.isEmpty()) return;

            CompoundTag customData = proj.getEntityData().get(StaticProjectile.CUSTOM_DATA);
            if(!customData.contains("a") || !customData.contains("w") || !customData.contains("angle") || !customData.contains("start") || !customData.contains("idx")) return;
            int start = customData.getInt("start");
            if(proj.getEntityData().get(StaticProjectile.AGE) <= start) return;
            int idx = customData.getInt("idx");

            Vec3 dir = MathUtil.toVec3(proj.getEntityData().get(StaticProjectile.DIRECTION)).normalize();
            Vec3 up = MathUtil.toVec3(proj.getEntityData().get(StaticProjectile.UP)).normalize();
            Vec3 right = MathUtil.toVec3(proj.getEntityData().get(StaticProjectile.RIGHT)).normalize();

            Quaternionf rotationDir = new Quaternionf()
                .fromAxisAngleRad(dir.toVector3f(), (float) Math.toRadians(customData.getInt("angle")));
            Quaternionf rotationUp1 = new Quaternionf()
                .fromAxisAngleRad(up.toVector3f(), (float) -(customData.getDouble("w") * (proj.getEntityData().get(StaticProjectile.AGE) - start + partialTick)));
            Quaternionf rotationUp2 = new Quaternionf()
                .fromAxisAngleRad(up.toVector3f(), (float) Math.PI);

            float[] xyRot = MathUtil.computeXYRot(dir.toVector3f(), up.toVector3f());

            poseStack.pushPose();

            //旋转
            poseStack.mulPose(rotationUp1);
            poseStack.mulPose(rotationUp2);
            poseStack.mulPose(rotationDir);
            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
            poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));
            poseStack.mulPose(Axis.XP.rotationDegrees(proj.getEntityData().get(StaticProjectile.RXP)));
            poseStack.mulPose(Axis.YP.rotationDegrees(proj.getEntityData().get(StaticProjectile.RYP)));
            poseStack.mulPose(Axis.ZP.rotationDegrees(proj.getEntityData().get(StaticProjectile.RZP)));

            //缩放
            poseStack.scale(
                proj.getEntityData().get(StaticProjectile.SCALE_X),
                proj.getEntityData().get(StaticProjectile.SCALE_Y),
                proj.getEntityData().get(StaticProjectile.SCALE_Z)
            );

            Minecraft.getInstance().getItemRenderer().renderStatic(
                stack,
                ItemDisplayContext.NONE,
                proj.getEntityData().get(StaticProjectile.GLOW) ? LightTexture.FULL_BRIGHT : packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                renderType -> {
                    VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
                    return new TintedVertexConsumer(vertexConsumer,
                        1.0f,
                        1.0f,
                        1.0f,
                        proj.getEntityData().get(StaticProjectile.COLOR_A));
                },
                entity.level(),
                0
            );

            poseStack.popPose();

            Vec3 currentPos = proj.getPosition(partialTick);

            VertexConsumer vertexConsumer0 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES0));
            Matrix4f matrix = poseStack.last().pose();

            Vector3f color = new Vector3f(
                proj.getEntityData().get(StaticProjectile.COLOR_R),
                proj.getEntityData().get(StaticProjectile.COLOR_G),
                proj.getEntityData().get(StaticProjectile.COLOR_B)
            );
            float mainAlpha = (float) getTrailAlpha();
            if(idx == 1) mainAlpha = mainAlpha * 0.5f;
            if(idx == 2) mainAlpha = mainAlpha * 0.25f;

            //尾迹
            int quadCount = proj.trailPositions.size() / 2 - 1;
            for(int j = 0;j < 2;j++) {
                for(int i = 1; i < quadCount; i++) {
                    Vec3 currentPoint1 = proj.trailPositions.get(i * 2);
                    Vec3 currentPoint2 = proj.trailPositions.get(i * 2 + 1);
                    Vec3 nextPoint1 = proj.trailPositions.get(i * 2 + 3);
                    Vec3 nextPoint2 = proj.trailPositions.get(i * 2 + 2);

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

                    vertexConsumer0.addVertex(matrix, (float) x1, (float) y1, (float) z1)
                        .setColor(color.x, color.y, color.z, mainAlpha).setUv(radio1, 0.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                    vertexConsumer0.addVertex(matrix, (float) x2, (float) y2, (float) z2)
                        .setColor(color.x, color.y, color.z, 0).setUv(radio1, 1.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                    vertexConsumer0.addVertex(matrix, (float) x3, (float) y3, (float) z3)
                        .setColor(color.x, color.y, color.z, 0).setUv(radio2, 1.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                    vertexConsumer0.addVertex(matrix, (float) x4, (float) y4, (float) z4)
                        .setColor(color.x, color.y, color.z, mainAlpha).setUv(radio2, 0.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                }
            }

            //闪烁
            if(idx != 0) return;
            float halfWidth = 32 * 0.05f;
            float halfHeight = 32 * 0.05f;

            float radio = (float) (proj.getEntityData().get(StaticProjectile.AGE) - start) / (float) proj.getEntityData().get(StaticProjectile.LIFETIME);
            float alpha;
            if(radio > 0.25f && radio < 0.4f) {
                alpha = (radio - 0.25f) / 0.15f;
            }else if(radio >= 0.4f && radio < 0.6f) {
                alpha = 1.0f;
            }else if(radio >= 0.6f && radio < 0.75f) {
                alpha = 1.0f - (radio - 0.6f) / 0.15f;
            }else {
                alpha = 0;
            }
            if(Math.abs(alpha) < 0.001) return;

            VertexConsumer vertexConsumer1 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES1));
            poseStack.pushPose();

            Vector3f tempRight = right.toVector3f();
            double rad = customData.getDouble("w") * (proj.getEntityData().get(StaticProjectile.AGE) - start + partialTick);
            Vector3f swordTipDir = tempRight.rotate(new Quaternionf().fromAxisAngleRad(up.toVector3f(), (float) (Math.PI / 2 - rad))).normalize();
            Vec3 trans = MathUtil.toVec3(swordTipDir).scale(1.25);

            poseStack.translate(trans.x, trans.y, trans.z);
            poseStack.mulPose(new Quaternionf()
                    .fromAxisAngleRad(dir.toVector3f(), (float) Math.toRadians(customData.getInt("angle")))
                    .rotateY((float) Math.toRadians(-xyRot[1]))
                    .rotateX((float) Math.toRadians(-90 + xyRot[0]))
            );

            poseStack.scale(1.0f, 1.0f, 1.0f);
            matrix = poseStack.last().pose();
            for(int i = 0; i < 3; i++) {
                vertexConsumer1.addVertex(matrix, -halfWidth, -halfHeight, 0.01f)
                    .setColor(color.x, color.y, color.z, alpha).setUv(0.0f, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                vertexConsumer1.addVertex(matrix, halfWidth, -halfHeight, 0.01f)
                    .setColor(color.x, color.y, color.z, alpha).setUv(1.0f, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                vertexConsumer1.addVertex(matrix, halfWidth, halfHeight, 0.01f)
                    .setColor(color.x, color.y, color.z, alpha).setUv(1.0f, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                vertexConsumer1.addVertex(matrix, -halfWidth, halfHeight, 0.01f)
                    .setColor(color.x, color.y, color.z, alpha).setUv(0.0f, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            }
            poseStack.popPose();
        }
    };
}
