package com.lzxnone.terraria.client.item.normal;

import com.lzxnone.terraria.client.entity.summon.IStaticSummonRenderBehavior;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.TintedVertexConsumer;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.utils.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class ClientDropFallenStar {
    private static final ResourceLocation RIBBON_RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/normal_trail.png");
    private static final ResourceLocation STAR_WAVE_RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/star_wave.png");
    private static final int RIBBON_COUNT = 14;
    private static final float MIN_RIBBON_LENGTH = 0.45F;
    private static final float MAX_RIBBON_LENGTH = 1.2F;
    private static final float MAX_RIBBON_WIDTH = 0.21F;
    private static final float RIBBON_ALPHA = 0.55F;
    private static final float RIBBON_COLOR_R = 1.0F;
    private static final float RIBBON_COLOR_G = 0.78F;
    private static final float RIBBON_COLOR_B = 0.16F;
    private static final float STAR_WAVE_HALF_WIDTH = 45 * 0.06F;
    private static final float STAR_WAVE_HALF_HEIGHT = 17 * 0.06F;
    private static final float STAR_WAVE_ALPHA = 0.5F;
    private static final double RIBBON_CENTER_Y_OFFSET = 0.5D;
    private static final double GOLDEN_ANGLE = Math.PI * (3.0D - Math.sqrt(5.0D));

    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR = new IStaticSummonRenderBehavior() {
        @Override
        public void renderItem(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;
            ItemStack itemStack = summon.getEntityData().get(StaticSummon.ITEM);
            if(itemStack == ItemStack.EMPTY) return;

            poseStack.pushPose();

            poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTick, summon.yRotO, summon.getYRot())));
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, summon.xRotO, summon.getXRot())));

            poseStack.mulPose(Axis.YP.rotationDegrees(9 * (summon.getEntityData().get(StaticSummon.AGE) + partialTick)));

            poseStack.translate(0, 0.5f, 0);

            poseStack.scale(0.75f, 0.75f, 0.75f);

            Minecraft.getInstance().getItemRenderer().renderStatic(
                itemStack,
                ItemDisplayContext.NONE,
                summon.getEntityData().get(StaticSummon.GLOW) ? LightTexture.FULL_BRIGHT : packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                renderType -> {
                    VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
                    return new TintedVertexConsumer(vertexConsumer,
                        summon.getEntityData().get(StaticSummon.COLOR_R),
                        summon.getEntityData().get(StaticSummon.COLOR_G),
                        summon.getEntityData().get(StaticSummon.COLOR_B),
                        summon.getEntityData().get(StaticSummon.COLOR_A));
                },
                entity.level(),
                0
            );
            poseStack.popPose();
        }

        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;
            renderItem(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
            renderRibbons(summon, partialTick, poseStack, bufferSource);
            renderStarWave(summon, poseStack, bufferSource);
        }
    };

    private static void renderStarWave(StaticSummon summon, PoseStack poseStack, MultiBufferSource bufferSource) {
        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
        if(customData.contains("ground")) return;
        if(summon.onGround()) return;

        Vec3 dir = summon.getDeltaMovement().normalize();
        float[] xyRot = MathUtil.computeXYRot(dir.toVector3f());

        poseStack.pushPose();
        poseStack.translate(-dir.x * 2, -dir.y * 2, -dir.z * 2);
        poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
        poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));
        poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));

        VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(STAR_WAVE_RES));
        addQuad(consumer, poseStack.last().pose(), STAR_WAVE_HALF_WIDTH, STAR_WAVE_HALF_HEIGHT,
            0.29F, 0.29F, 0.871F, STAR_WAVE_ALPHA);
        poseStack.popPose();
    }

    private static void renderRibbons(StaticSummon summon, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
        VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.entityAdditiveEmissive(RIBBON_RES));
        Matrix4f matrix = poseStack.last().pose();
        float age = summon.getEntityData().get(StaticSummon.AGE) + partialTick;
        Vec3 entityPos = summon.getPosition(partialTick);
        Vec3 cameraDir = entityPos.subtract(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition());
        if(cameraDir.lengthSqr() < 1.0E-4D) cameraDir = new Vec3(0.0D, 0.0D, 1.0D);
        else cameraDir = cameraDir.normalize();

        for(int i = 0; i < RIBBON_COUNT; i++) {
            Vec3 ray = getRibbonDirection(i, age, summon.getId());
            Vec3 side = ray.cross(cameraDir);
            if(side.lengthSqr() < 1.0E-4D) side = ray.cross(new Vec3(0.0D, 1.0D, 0.0D));
            if(side.lengthSqr() < 1.0E-4D) side = ray.cross(new Vec3(1.0D, 0.0D, 0.0D));
            side = side.normalize();

            float pulse = 0.65F + 0.35F * (float)Math.sin(age * 0.18F + i * 1.37F);
            float length = MIN_RIBBON_LENGTH + (MAX_RIBBON_LENGTH - MIN_RIBBON_LENGTH) * pulse;
            float width = MAX_RIBBON_WIDTH * (0.55F + 0.45F * pulse);
            float alpha = RIBBON_ALPHA * (0.6F + 0.4F * pulse);

            Vec3 centerOffset = new Vec3(0.0D, RIBBON_CENTER_Y_OFFSET, 0.0D);
            Vec3 start = centerOffset.add(ray.scale(0.08D));
            Vec3 end = centerOffset.add(ray.scale(length));
            addRibbon(consumer, matrix, start, end, side, width, RIBBON_COLOR_R, RIBBON_COLOR_G, RIBBON_COLOR_B, alpha);
        }
    }

    private static Vec3 getRibbonDirection(int index, float age, int seed) {
        double y = 1.0D - (index + 0.5D) * 2.0D / RIBBON_COUNT;
        double radius = Math.sqrt(Math.max(0.0D, 1.0D - y * y));
        double angle = index * GOLDEN_ANGLE + seed * 0.37D + age * 0.045D;
        return new Vec3(Math.cos(angle) * radius, y, Math.sin(angle) * radius).normalize();
    }

    private static void addRibbon(VertexConsumer consumer, Matrix4f matrix, Vec3 start, Vec3 end, Vec3 side,
                                  float width, float r, float g, float b, float alpha) {
        Vec3 endA = end.add(side.scale(width));
        Vec3 endB = end.subtract(side.scale(width));

        consumer.addVertex(matrix, (float)start.x, (float)start.y, (float)start.z)
            .setColor(r, g, b, alpha).setUv(0.5F, 1.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(matrix, (float)start.x, (float)start.y, (float)start.z)
            .setColor(r, g, b, alpha).setUv(0.5F, 0.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(matrix, (float)endA.x, (float)endA.y, (float)endA.z)
            .setColor(r, g, b, 0.0F).setUv(1.0F, 0.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(matrix, (float)endB.x, (float)endB.y, (float)endB.z)
            .setColor(r, g, b, 0.0F).setUv(0.0F, 1.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0F, 1.0F, 0.0F);
    }

    private static void addQuad(VertexConsumer consumer, Matrix4f matrix, float halfWidth, float halfHeight,
                                float r, float g, float b, float alpha) {
        consumer.addVertex(matrix, -halfWidth, -halfHeight, 0.0F)
            .setColor(r, g, b, alpha).setUv(0.0F, 1.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(matrix, halfWidth, -halfHeight, 0.0F)
            .setColor(r, g, b, alpha).setUv(1.0F, 1.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(matrix, halfWidth, halfHeight, 0.0F)
            .setColor(r, g, b, alpha).setUv(1.0F, 0.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(matrix, -halfWidth, halfHeight, 0.0F)
            .setColor(r, g, b, alpha).setUv(0.0F, 0.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0F, 1.0F, 0.0F);
    }
}
