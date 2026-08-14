package com.lzxnone.terraria.client.item.magic;

import com.lzxnone.terraria.client.entity.summon.IStaticSummonRenderBehavior;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class ClientNebulaBlaze {
    private static final ResourceLocation[] RES = {
        ResourceLocation.parse("lzxnoneterraria:textures/vfx/nebula_blaze_projectile0.png"),
        ResourceLocation.parse("lzxnoneterraria:textures/vfx/nebula_blaze_projectile1.png"),
        ResourceLocation.parse("lzxnoneterraria:textures/vfx/nebula_blaze_projectile2.png"),
        ResourceLocation.parse("lzxnoneterraria:textures/vfx/nebula_blaze_projectile3.png")
    };
    private static final ResourceLocation[] EXPLOSIVE_RES = {
        ResourceLocation.parse("lzxnoneterraria:textures/vfx/nebula_blaze_ex_projectile0.png"),
        ResourceLocation.parse("lzxnoneterraria:textures/vfx/nebula_blaze_ex_projectile1.png"),
        ResourceLocation.parse("lzxnoneterraria:textures/vfx/nebula_blaze_ex_projectile2.png"),
        ResourceLocation.parse("lzxnoneterraria:textures/vfx/nebula_blaze_ex_projectile3.png")
    };

    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR = new IStaticSummonRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;
            boolean explosive = summon.getEntityData().get(StaticSummon.BEHAVIOR).equals(StaticSummonBehaviors.NEBULA_BLAZE_EXPLOSIVE);
            ResourceLocation[] frames = explosive ? EXPLOSIVE_RES : RES;
            float age = summon.getEntityData().get(StaticSummon.AGE) + partialTick;
            int frame = Mth.floor(age * 0.625F) % frames.length;
            float halfSize = explosive ? 0.55F : 0.42F;

            poseStack.pushPose();
            poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
            poseStack.mulPose(Axis.ZP.rotationDegrees(age * 18.0F));

            poseStack.scale(
                summon.getEntityData().get(StaticSummon.SCALE_X),
                summon.getEntityData().get(StaticSummon.SCALE_Y),
                summon.getEntityData().get(StaticSummon.SCALE_Z)
            );

            VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.entityAdditiveEmissive(frames[frame]));
            consumer.addVertex(poseStack.last().pose(), -halfSize, -halfSize, 0.0F)
                .setColor(1.0F, 1.0F, 1.0F, 1.0F)
                .setUv(0.0F, 1.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0.0F, 1.0F, 0.0F);
            consumer.addVertex(poseStack.last().pose(), halfSize, -halfSize, 0.0F)
                .setColor(1.0F, 1.0F, 1.0F, 1.0F)
                .setUv(1.0F, 1.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0.0F, 1.0F, 0.0F);
            consumer.addVertex(poseStack.last().pose(), halfSize, halfSize, 0.0F)
                .setColor(1.0F, 1.0F, 1.0F, 1.0F)
                .setUv(1.0F, 0.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0.0F, 1.0F, 0.0F);
            consumer.addVertex(poseStack.last().pose(), -halfSize, halfSize, 0.0F)
                .setColor(1.0F, 1.0F, 1.0F, 1.0F)
                .setUv(0.0F, 0.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0.0F, 1.0F, 0.0F);
            poseStack.popPose();
        }
    };

    public static void renderHandProjectile(MultiBufferSource bufferSource, PoseStack poseStack, Entity entity) {
        float age = entity.tickCount + Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
        int frame = Mth.floor(age * 0.625F) % RES.length;
        float halfSize = 0.35F;

        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, 0.08F);
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        poseStack.mulPose(Axis.ZP.rotationDegrees(age * 18.0F));

        VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.entityAdditiveEmissive(RES[frame]));
        consumer.addVertex(poseStack.last().pose(), -halfSize, -halfSize, 0.0F)
            .setColor(1.0F, 1.0F, 1.0F, 1.0F)
            .setUv(0.0F, 1.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT)
            .setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(poseStack.last().pose(), halfSize, -halfSize, 0.0F)
            .setColor(1.0F, 1.0F, 1.0F, 1.0F)
            .setUv(1.0F, 1.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT)
            .setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(poseStack.last().pose(), halfSize, halfSize, 0.0F)
            .setColor(1.0F, 1.0F, 1.0F, 1.0F)
            .setUv(1.0F, 0.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT)
            .setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(poseStack.last().pose(), -halfSize, halfSize, 0.0F)
            .setColor(1.0F, 1.0F, 1.0F, 1.0F)
            .setUv(0.0F, 0.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT)
            .setNormal(0.0F, 1.0F, 0.0F);
        poseStack.popPose();
    }
}
