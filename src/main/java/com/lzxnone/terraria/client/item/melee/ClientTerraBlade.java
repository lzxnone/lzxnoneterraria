package com.lzxnone.terraria.client.item.melee;

import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.beam.SwordBeam;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.utils.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.*;
import org.joml.Vector3f;
import com.lzxnone.terraria.client.entity.summon.IStaticSummonRenderBehavior;
import static com.lzxnone.terraria.item.weapon.melee.TerraBlade.*;

public class ClientTerraBlade {
    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR = new IStaticSummonRenderBehavior() {
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

            float[] dz = {0.1f, 0f, -0.1f};

            for(int x = 0;x < 3;x++) {

                VertexConsumer vertexConsumer0 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES0));

                //左边
                poseStack.pushPose();
                RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 90 - 18 * (1.0f - progress), SwordBeam.DIST);
                RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], 18 * (1.0f - progress), rotate);
                RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer0,
                    COLOR0.x(), COLOR0.y(), COLOR0.z(), alpha, halfWidth, halfHeight, 0f, 0, -0.01f + dz[x]);
                poseStack.popPose();


                //中间
                poseStack.pushPose();
                RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 90, SwordBeam.DIST);
                RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], 0, rotate);
                RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer0,
                    COLOR1.x(), COLOR1.y(), COLOR1.z(), alpha, halfWidth, halfHeight, 0f, 0f, 0f + dz[x]);
                poseStack.popPose();

                //右边
                poseStack.pushPose();
                RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 108, SwordBeam.DIST);
                RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], -9 * (1.0f - progress), rotate);
                RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer0,
                    COLOR2.x(), COLOR2.y(), COLOR2.z(), alpha, halfWidth, halfHeight, 0f, 0, 0.01f + dz[x]);
                poseStack.popPose();

                VertexConsumer vertexConsumer1 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES1));

                //三线
                poseStack.pushPose();
                RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 90, SwordBeam.DIST / 1.5f);
                RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], 0, rotate);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer1,
                    1.0f, 1.0f, 1.0f, alpha, halfWidth, halfHeight, 0f, 0f, 0.02f + dz[x]);
                poseStack.popPose();

                poseStack.pushPose();
                RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 90, SwordBeam.DIST / 1.25f);
                RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], 0, rotate);
                poseStack.scale(0.75f, 0.75f, 0.75f);
                RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer1,
                    1.0f, 1.0f, 1.0f, alpha, halfWidth, halfHeight, 0f, 0f, 0.02f + dz[x]);
                poseStack.popPose();

                poseStack.pushPose();
                RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 90, SwordBeam.DIST * 1.15f);
                RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], 0, rotate);
                poseStack.scale(0.95f, 0.95f, 0.95f);
                RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer1,
                    1.0f, 1.0f, 1.0f, alpha, halfWidth, halfHeight, 0f, 0f, 0.02f + dz[x]);
                poseStack.popPose();

                VertexConsumer vertexConsumer2 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES2));

                //边缘高光
                poseStack.pushPose();
                RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 100, SwordBeam.DIST * 1.15f);
                RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], 0, rotate);
                poseStack.scale(1.0f, 1.0f, 1.0f);
                for (int i = 0; i < 5; i++) {
                    RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer2,
                        COLOR2.x(), COLOR2.y(), COLOR2.z(), alpha, halfWidth, halfHeight, 0f, 0f, 0.03f + dz[x]);
                }
                poseStack.popPose();


                float sparkleAlpha;
                if (progress < FADE_IN) {
                    sparkleAlpha = 0;
                } else if (progress > FADE_OUT) {
                    sparkleAlpha = 1.0f - (progress - FADE_OUT) / (1.0f - FADE_OUT);
                } else {
                    sparkleAlpha = (progress - FADE_IN) / (FADE_OUT - FADE_IN);
                }

                VertexConsumer vertexConsumer3 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES3));

                //闪烁(外层)
                poseStack.pushPose();
                for (int k = 0; k < 2; k++) {
                    poseStack.pushPose();
                    if (k == 0) {
                        RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 90 - 60.0 * progress, SwordBeam.DIST * 2f);
                        RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], 60 * progress, rotate);
                    } else {
                        RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 90 + 60.0 * progress, SwordBeam.DIST * 2f + 0.5f * progress);
                        RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], -60 * progress, rotate);
                    }
                    for (int j = 0; j < 2; j++) {
                        poseStack.pushPose();
                        if (j == 0) poseStack.mulPose(Axis.ZP.rotationDegrees(0.0F));
                        else poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
                        poseStack.scale(0.5f, 0.5f, 0.5f);
                        for (int i = 0; i < 5; i++) {
                            RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer3,
                                COLOR2.x(), COLOR2.y(), COLOR2.z(), sparkleAlpha, 32 * SwordBeam.SCALE, 32 * SwordBeam.SCALE, 0f, 0f, 0.03f + dz[x]);
                        }
                        poseStack.popPose();
                    }
                    poseStack.popPose();
                }
                poseStack.popPose();

                //闪烁(中层)
                poseStack.pushPose();
                for (int k = 0; k < 2; k++) {
                    poseStack.pushPose();
                    if (k == 0) {
                        RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 90 - 30.0 * progress, SwordBeam.DIST * 2f);
                        RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], 30 * progress, rotate);
                    } else {
                        RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 90 + 30.0 * progress, SwordBeam.DIST * 2.1f);
                        RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], -30 * progress, rotate);
                    }
                    for (int j = 0; j < 2; j++) {
                        poseStack.pushPose();
                        if (j == 0) poseStack.mulPose(Axis.ZP.rotationDegrees(0.0F));
                        else poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
                        poseStack.scale(0.75f, 0.75f, 0.75f);
                        for (int i = 0; i < 5; i++) {
                            RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer3,
                                COLOR2.x(), COLOR2.y(), COLOR2.z(), sparkleAlpha, 32 * SwordBeam.SCALE, 32 * SwordBeam.SCALE, 0f, 0f, 0.03f + dz[x]);
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
                for (int j = 0; j < 2; j++) {
                    poseStack.pushPose();
                    if (j == 0) poseStack.mulPose(Axis.ZP.rotationDegrees(0.0F));
                    else poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
                    poseStack.scale(1.0f, 1.0f, 1.0f);
                    for (int i = 0; i < 5; i++) {
                        RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer3,
                            COLOR2.x(), COLOR2.y(), COLOR2.z(), sparkleAlpha, 32 * SwordBeam.SCALE, 32 * SwordBeam.SCALE, 0f, 0f, 0.03f + dz[x]);
                    }
                    poseStack.popPose();
                }
                poseStack.popPose();

            }

            poseStack.popPose();
        }
    };
}
