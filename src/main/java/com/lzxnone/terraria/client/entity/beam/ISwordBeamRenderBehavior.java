package com.lzxnone.terraria.client.entity.beam;

import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.beam.SwordBeam;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.RenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public interface ISwordBeamRenderBehavior {
    ResourceLocation RES0 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/terra_beam0.png");
    ResourceLocation RES1 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/terra_beam1.png");
    ResourceLocation RES2 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/terra_beam2.png");
    ResourceLocation RES3 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/terra_beam3.png");
    ResourceLocation RES4 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/terra_beam4.png");
    ResourceLocation RES5 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/beam_sparkle.png");

    float FADE_IN = 0.33f;
    float FADE_OUT = 0.67f;

    default void render(Entity entity, float entityYaw, float partialTick,
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

        Vector3f color0 = entity.getEntityData().get(SwordBeam.COLOR0);
        Vector3f color1 = entity.getEntityData().get(SwordBeam.COLOR1);
        Vector3f color2 = entity.getEntityData().get(SwordBeam.COLOR2);
        Vector3f color3 = entity.getEntityData().get(SwordBeam.COLOR3);
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

        poseStack.pushPose();
        RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress - 0.1f * (1.0f - progress)) * 180, SwordBeam.DIST);
        RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - (progress - 0.1f * (1.0f - progress))) * 180, rotate);
        renderQuad(poseStack.last().pose(), vertexConsumer0,
            color0.x(), color0.y(), color0.z(), alpha, halfWidth, halfHeight, 0f, 0, -0.01f);
        poseStack.popPose();

        poseStack.pushPose();
        RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress + 0.1f) * 180, SwordBeam.DIST);
        RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - (progress + 0.05f)) * 180, rotate);
        renderQuad(poseStack.last().pose(), vertexConsumer0,
            color2.x(), color2.y(), color2.z(), alpha, halfWidth, halfHeight, 0f, 0, -0.02f);
        poseStack.popPose();

        poseStack.pushPose();
        RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], progress * 180, SwordBeam.DIST);
        RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - progress) * 180, rotate);
        renderQuad(poseStack.last().pose(), vertexConsumer0,
            color1.x(), color1.y(), color1.z(), alpha, halfWidth, halfHeight, 0f, 0f, 0f);
        poseStack.popPose();

        VertexConsumer vertexConsumer1 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES3));

        poseStack.pushPose();
        RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress + 0.05f) * 180, SwordBeam.DIST / 1.5f);
        RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - progress) * 180, rotate);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        renderQuad(poseStack.last().pose(), vertexConsumer1,
            color3.x(), color3.y(), color3.z(), alpha, halfWidth, halfHeight, 0f, 0f, 0.02f);
        poseStack.popPose();

        poseStack.pushPose();
        RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress + 0.05f) * 180, SwordBeam.DIST / 1.25f);
        RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - progress) * 180, rotate);
        poseStack.scale(0.75f, 0.75f, 0.75f);
        renderQuad(poseStack.last().pose(), vertexConsumer1,
            color3.x(), color3.y(), color3.z(), alpha, halfWidth, halfHeight, 0f, 0f, 0.02f);
        poseStack.popPose();

        poseStack.pushPose();
        RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], progress * 180, SwordBeam.DIST * 1.15f);
        RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - progress) * 180, rotate);
        poseStack.scale(0.95f, 0.95f, 0.95f);
        renderQuad(poseStack.last().pose(), vertexConsumer1,
            color3.x(), color3.y(), color3.z(), alpha, halfWidth, halfHeight, 0f, 0f, 0.02f);
        poseStack.popPose();

        VertexConsumer vertexConsumer2 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES4));

        poseStack.pushPose();
        RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress + 0.05f) * 180, SwordBeam.DIST * 1.15f);
        RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - progress - 0.05f) * 180, rotate);
        poseStack.scale(1.0f, 1.0f, 1.0f);
        for(int i = 0;i < 3;i++) {
            renderQuad(poseStack.last().pose(), vertexConsumer2,
                color1.x(), color1.y(), color1.z(), alpha, halfWidth, halfHeight, 0f, 0f, 0.03f);
        }
        poseStack.popPose();

        VertexConsumer vertexConsumer3 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES5));

        poseStack.pushPose();
        RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress + 0.25f) * 180, SwordBeam.DIST * 2.25f);
        RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - progress - 0.25f) * 180, rotate);
        poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
        poseStack.scale(1.0f, 1.0f, 1.0f);
        for(int i = 0;i < 20;i++) {
            renderQuad(poseStack.last().pose(), vertexConsumer3,
                color1.x(), color1.y(), color1.z(), alpha, 32 * SwordBeam.SCALE, 32 * SwordBeam.SCALE, 0f, 0f, 0.03f);
        }
        poseStack.popPose();

        renderSword(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    default void renderSword(Entity entity, float entityYaw, float partialTick,
                             PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(!(entity instanceof SwordBeam beam)) return;
        ItemStack stack = beam.getEntityData().get(SwordBeam.STACK_SOURCE);
        if(stack.isEmpty()) return;
        Entity owner = beam.getOwner();
        if(owner == null) return;

        int age = beam.getEntityData().get(SwordBeam.AGE);
        int lifetime = Math.max(1, beam.getEntityData().get(SwordBeam.LIFETIME));
        float progress = (age + partialTick) / (float) lifetime;
        if(progress > 1.0f) return;
        if(beam.getEntityData().get(SwordBeam.RIGHT)) progress = 1.0f - progress;

        Vector3f[] dirs = MathUtil.computeCoordinateSystem(owner);
        dirs = MathUtil.rotateCoordinateSystem(dirs[0], dirs[2], beam.getEntityData().get(SwordBeam.ROTATE));
        float rotate = beam.getEntityData().get(SwordBeam.ROTATE);

        poseStack.pushPose();
        RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress + 0.1f) * 180, SwordBeam.DIST * 0.75);
        RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - (progress + 0.05f)) * 180, rotate);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-45));
        poseStack.scale(1.8f, 1.8f, 1.8f);
        Minecraft.getInstance().getItemRenderer().renderStatic(
            stack,
            ItemDisplayContext.NONE,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            poseStack,
            bufferSource,
            entity.level(),
            0
        );
        poseStack.popPose();
    }

    default void renderQuad(Matrix4f matrix, VertexConsumer consumer, float r, float g, float b, float a, float halfWidth, float halfHeight, float offsetX, float offsetY, float offsetZ) {
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
}
