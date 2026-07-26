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
import org.joml.Quaternionf;
import org.joml.Vector3f;
import com.lzxnone.terraria.client.entity.summon.IStaticSummonRenderBehavior;
import com.lzxnone.terraria.client.entity.beam.ISwordBeamRenderBehavior;
import static com.lzxnone.terraria.item.weapon.melee.NightsEdge.*;

public class ClientNightsEdge {
    public static final ISwordBeamRenderBehavior SWORD_BEAM_BEHAVIOR = new ISwordBeamRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick,
            PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if (!(entity instanceof SwordBeam beam)) return;
            Entity owner = beam.getOwner();
            if (owner == null) return;

            int age = beam.getEntityData().get(SwordBeam.AGE);
            int lifetime = Math.max(1, beam.getEntityData().get(SwordBeam.LIFETIME));

            float progress = (age + partialTick) / (float) lifetime;
            if (progress > 1.0f) return;

            Vector3f[] dirs = MathUtil.computeCoordinateSystem(beam.getOwner());
            dirs = MathUtil.rotateCoordinateSystem(dirs[0], dirs[2], beam.getEntityData().get(SwordBeam.ROTATE));
            float rotate = beam.getEntityData().get(SwordBeam.ROTATE);

            Vector3f color0 = entity.getEntityData().get(SwordBeam.COLOR0);
            Vector3f color1 = entity.getEntityData().get(SwordBeam.COLOR1);
            Vector3f color2 = entity.getEntityData().get(SwordBeam.COLOR2);
            Vector3f color3 = entity.getEntityData().get(SwordBeam.COLOR3);
            float alpha;

            if (progress <= FADE_IN) {
                alpha = 1 - (FADE_IN - progress) / FADE_IN;
            } else if (progress >= FADE_OUT) {
                alpha = 1 - (progress - FADE_OUT) / (1 - FADE_OUT);
            } else {
                alpha = 1.0f;
            }
            if (beam.getEntityData().get(SwordBeam.RIGHT)) progress = 1.0f - progress;

            float halfWidth = SwordBeam.HALF_WIDTH * SwordBeam.SCALE;
            float halfHeight = SwordBeam.HALF_HEIGHT * SwordBeam.SCALE;

            VertexConsumer vertexConsumer0 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES0));

            //左边
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress - 0.1f * (1.0f - progress)) * 180, SwordBeam.DIST);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - (progress - 0.1f * (1.0f - progress))) * 180, rotate);
            renderQuad(poseStack.last().pose(), vertexConsumer0,
                color0.x(), color0.y(), color0.z(), alpha, halfWidth, halfHeight, 0f, 0, -0.01f);
            poseStack.popPose();

            //右边
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress + 0.1f) * 180, SwordBeam.DIST);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - (progress + 0.05f)) * 180, rotate);
            renderQuad(poseStack.last().pose(), vertexConsumer0,
                color2.x(), color2.y(), color2.z(), alpha, halfWidth, halfHeight, 0f, 0, -0.02f);
            poseStack.popPose();

            //中间
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], progress * 180, SwordBeam.DIST);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - progress) * 180, rotate);
            renderQuad(poseStack.last().pose(), vertexConsumer0,
                color1.x(), color1.y(), color1.z(), alpha, halfWidth, halfHeight, 0f, 0f, 0f);
            poseStack.popPose();

            VertexConsumer vertexConsumer1 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES3));

            //外线
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], progress * 180, SwordBeam.DIST * 1.15f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - progress) * 180, rotate);
            poseStack.scale(0.95f, 0.95f, 0.95f);
            renderQuad(poseStack.last().pose(), vertexConsumer1,
                color3.x(), color3.y(), color3.z(), alpha, halfWidth, halfHeight, 0f, 0f, 0.02f);
            poseStack.popPose();

            VertexConsumer vertexConsumer2 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES5));

            //闪烁
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress + 0.25f) * 180, SwordBeam.DIST * 2.25f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - progress - 0.25f) * 180, rotate);
            poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
            poseStack.scale(1.0f, 1.0f, 1.0f);
            for (int i = 0; i < 20; i++) {
                renderQuad(poseStack.last().pose(), vertexConsumer2,
                    color1.x(), color1.y(), color1.z(), alpha, 32 * SwordBeam.SCALE, 32 * SwordBeam.SCALE, 0f, 0f, 0.03f);
            }
            poseStack.popPose();
        }
    };

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
            }else if (progress >= FADE_OUT) {
                alpha = 1 - (progress - FADE_OUT) / (1 - FADE_OUT);
            }else {
                alpha = 1.0f;
            }

            if(alpha < 0.01f) return;

            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);

            Vector3f dir = new Vector3f();
            if (customData.contains("dirX")) dir.x = customData.getFloat("dirX");
            if (customData.contains("dirY")) dir.y = customData.getFloat("dirY");
            if (customData.contains("dirZ")) dir.z = customData.getFloat("dirZ");
            Vector3f up = new Vector3f();
            if (customData.contains("upX")) up.x = customData.getFloat("upX");
            if (customData.contains("upY")) up.y = customData.getFloat("upY");
            if (customData.contains("upZ")) up.z = customData.getFloat("upZ");
            Vector3f right = new Vector3f();
            if (customData.contains("rightX")) right.x = customData.getFloat("rightX");
            if (customData.contains("rightY")) right.y = customData.getFloat("rightY");
            if (customData.contains("rightZ")) right.z = customData.getFloat("rightZ");

            Vector3f[] dirs = new Vector3f[]{dir, up, right};
            double rotate = summon.getEntityData().get(StaticSummon.RZP);
            double rotateSpeed = summon.getEntityData().get(StaticSummon.RZPS);
            float rotateAngle = (float) ((summon.getEntityData().get(StaticSummon.AGE) + partialTick) * rotateSpeed);

            Quaternionf rotation = new Quaternionf().fromAxisAngleRad(
                up, (float) Math.toRadians(-rotateAngle)
            );
            poseStack.mulPose(rotation);

            VertexConsumer vertexConsumer0 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES0));

            //外层(亮)
            poseStack.pushPose();
            poseStack.mulPose(rotation);
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 90, SwordBeam.DIST);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], 0, rotate);
            //poseStack.scale(1.0f + progress, 1.0f + progress, 1.0f + progress);
            RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer0,
                COLOR0.x(), COLOR0.y(), COLOR0.z(), alpha, halfWidth, halfHeight, 0f, 0f, 0f);
            poseStack.popPose();

            VertexConsumer vertexConsumer1 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES1));

            //外线
            poseStack.pushPose();
            poseStack.mulPose(rotation);
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 90, SwordBeam.DIST * 1.15f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], 0, rotate);
            //poseStack.scale(0.95f * (1 + progress), 0.95f * (1 + progress), 0.95f * (1 + progress));
            RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer1,
                1.0f, 1.0f, 1.0f, alpha, halfWidth, halfHeight, 0f, 0f, 0.02f);
            poseStack.popPose();

            VertexConsumer vertexConsumer2 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES2));

            //闪烁
            poseStack.pushPose();
            poseStack.mulPose(rotation);
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 135, SwordBeam.DIST * 2.25f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], 0, rotate);
            //poseStack.scale(1.0f + progress, 1.0f + progress, 1.0f + progress);
            RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer2,
                COLOR0.x(), COLOR0.y(), COLOR0.z(), alpha, 32 * SwordBeam.SCALE, 32 * SwordBeam.SCALE, 0f, 0f, 0.04f);
            poseStack.popPose();

        }
    };
}
