package com.lzxnone.terraria.client.item.melee;

import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.beam.SwordBeam;
import com.lzxnone.terraria.utils.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import org.joml.Vector3f;
import com.lzxnone.terraria.client.entity.beam.ISwordBeamRenderBehavior;

public class ClientTrueExcalibur {
    public static final ISwordBeamRenderBehavior SWORD_BEAM_BEHAVIOR = new ISwordBeamRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick,
            PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof SwordBeam beam)) return;
            Entity owner = beam.getOwner();
            if(owner == null) return;

            int age = beam.getEntityData().get(SwordBeam.AGE);
            int lifetime = Math.max(1, beam.getEntityData().get(SwordBeam.LIFETIME));

            float progress = (age + partialTick) / (float) lifetime;
            if(progress > 1.0f) return;

            Vector3f[] dirs = MathUtil.computeCoordinateSystem(beam.getOwner());
            dirs = MathUtil.rotateCoordinateSystem(dirs[0], dirs[2], beam.getEntityData().get(SwordBeam.ROTATE));
            float rotate = beam.getEntityData().get(SwordBeam.ROTATE);

            Vector3f color0 = new Vector3f(0.651f, 0.102f, 0.224f);
            Vector3f color1 = new Vector3f(0.945f, 0.286f, 0.525f);
            Vector3f color2 = new Vector3f(0.945f, 0.286f, 0.525f);
            Vector3f color3 = new Vector3f(1.0f, 1.0f, 1.0f);
            float alpha;

            if(progress <= FADE_IN) {
                alpha = 1 - (FADE_IN - progress) / FADE_IN;
            }else if(progress >= FADE_OUT) {
                alpha = 1 - (progress - FADE_OUT) / (1 - FADE_OUT);
            }else {
                alpha = 1.0f;
            }
            if(beam.getEntityData().get(SwordBeam.RIGHT)) progress = 1.0f - progress;

            float halfWidth = SwordBeam.HALF_WIDTH * SwordBeam.SCALE;
            float halfHeight = SwordBeam.HALF_HEIGHT * SwordBeam.SCALE;

            VertexConsumer vertexConsumer0 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES0));

            //左边
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress - 0.1f * (1.0f - progress)) * 180, SwordBeam.DIST * 1.5f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - (progress - 0.1f * (1.0f - progress))) * 180, rotate);
            poseStack.scale(1.5f, 1.5f, 1.5f);
            renderQuad(poseStack.last().pose(), vertexConsumer0,
                color0.x(), color0.y(), color0.z(), alpha, halfWidth, halfHeight, 0f, 0, -0.05f);
            poseStack.popPose();

            //右边
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress + 0.1f) * 180, SwordBeam.DIST * 1.5f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - (progress + 0.05f)) * 180, rotate);
            poseStack.scale(1.5f, 1.5f, 1.5f);
            renderQuad(poseStack.last().pose(), vertexConsumer0,
                color2.x(), color2.y(), color2.z(), alpha, halfWidth, halfHeight, 0f, 0, -0.06f);
            poseStack.popPose();

            //中间
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], progress * 180, SwordBeam.DIST * 1.5f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - progress) * 180, rotate);
            poseStack.scale(1.5f, 1.5f, 1.5f);
            renderQuad(poseStack.last().pose(), vertexConsumer0,
                color1.x(), color1.y(), color1.z(), alpha, halfWidth, halfHeight, 0f, 0f, -0.04f);
            poseStack.popPose();

            VertexConsumer vertexConsumer1 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES3));

            //三线
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress + 0.05f) * 180, SwordBeam.DIST);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - progress) * 180, rotate);
            poseStack.scale(0.75f, 0.75f, 0.75f);
            renderQuad(poseStack.last().pose(), vertexConsumer1,
                color3.x(), color3.y(), color3.z(), alpha, halfWidth, halfHeight, 0f, 0f, -0.045f);
            poseStack.popPose();

            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress + 0.05f) * 180, SwordBeam.DIST * 1.2f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - progress) * 180, rotate);
            poseStack.scale(1.125f, 1.125f, 1.125f);
            renderQuad(poseStack.last().pose(), vertexConsumer1,
                color3.x(), color3.y(), color3.z(), alpha, halfWidth, halfHeight, 0f, 0f, -0.045f);
            poseStack.popPose();

            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], progress * 180, SwordBeam.DIST * 1.725f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - progress) * 180, rotate);
            poseStack.scale(1.425f, 1.425f, 1.425f);
            renderQuad(poseStack.last().pose(), vertexConsumer1,
                color3.x(), color3.y(), color3.z(), alpha, halfWidth, halfHeight, 0f, 0f, -0.045f);
            poseStack.popPose();

            VertexConsumer vertexConsumer2 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES4));

            //边缘高光
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress + 0.05f) * 180, SwordBeam.DIST * 1.725f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - (progress + 0.05f)) * 180, rotate);
            poseStack.scale(1.5f, 1.5f, 1.5f);
            for(int i = 0;i < 3;i++) {
                renderQuad(poseStack.last().pose(), vertexConsumer2,
                    color1.x(), color1.y(), color1.z(), alpha, halfWidth, halfHeight, 0f, 0f, -0.03f);
            }
            poseStack.popPose();

            VertexConsumer vertexConsumer3 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES5));

            //闪烁
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress + 0.25f) * 180, SwordBeam.DIST * 3.375f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - (progress + 0.25f)) * 180, rotate);
            poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
            poseStack.scale(1.5f, 1.5f, 1.5f);
            for(int i = 0;i < 20;i++) {
                renderQuad(poseStack.last().pose(), vertexConsumer3,
                    color1.x(), color1.y(), color1.z(), alpha, 32 * SwordBeam.SCALE, 32 * SwordBeam.SCALE, 0f, 0f, -0.03f);
            }
            poseStack.popPose();

            ISwordBeamRenderBehavior.super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
            ISwordBeamRenderBehavior.super.renderSword(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        }
    };
}
