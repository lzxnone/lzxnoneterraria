package com.lzxnone.terraria.client.item.magic;

import com.lzxnone.terraria.client.entity.projectile.IStaticProjectileRenderBehavior;
import com.lzxnone.terraria.client.entity.summon.IStaticSummonRenderBehavior;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class ClientNebulaArcanum {
    private static final ResourceLocation PROJECTILE =
        ResourceLocation.parse("lzxnoneterraria:textures/vfx/nebula_arcanum_projectile.png");
    private static final ResourceLocation CIRCLE =
        ResourceLocation.parse("lzxnoneterraria:textures/vfx/circle.png");
    private static final String ORANGE_RED_KEY = "orangeRed";

    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR = new IStaticSummonRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;

            float age = summon.getEntityData().get(StaticSummon.AGE) + partialTick;
            renderBillboard(PROJECTILE, poseStack, bufferSource, 1.6F, 1.0F, 1.0F, 1.0F, 1.0F, age * 10.0F);
        }
    };

    public static final IStaticProjectileRenderBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile projectile)) return;
            float age = projectile.getEntityData().get(StaticProjectile.AGE) + partialTick;
            //renderBillboard(CIRCLE, poseStack, bufferSource, 0.3F, 1.0F, 0.18F, 0.72F, 1.0F, age * 18.0F);
        }
    };

    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR2 = new IStaticSummonRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;
            float age = summon.getEntityData().get(StaticSummon.AGE) + partialTick;
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            if(customData.getBoolean(ORANGE_RED_KEY)) {
                //renderBillboard(CIRCLE, poseStack, bufferSource, 0.16F, 1.0F, 0.22F, 0.08F, 1.0F, age * 22.0F);
            }else {
                //renderBillboard(CIRCLE, poseStack, bufferSource, 0.16F, 1.0F, 0.22F, 0.74F, 1.0F, age * 22.0F);
            }
        }
    };

    private static void renderBillboard(ResourceLocation texture, PoseStack poseStack, MultiBufferSource bufferSource,
                                        float halfSize, float r, float g, float b, float a, float rotationDegrees) {
        poseStack.pushPose();
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        poseStack.mulPose(Axis.ZP.rotationDegrees(rotationDegrees));

        VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.entityAdditiveEmissive(texture));
        consumer.addVertex(poseStack.last().pose(), -halfSize, -halfSize, 0.0F)
            .setColor(r, g, b, a)
            .setUv(0.0F, 1.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT)
            .setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(poseStack.last().pose(), halfSize, -halfSize, 0.0F)
            .setColor(r, g, b, a)
            .setUv(1.0F, 1.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT)
            .setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(poseStack.last().pose(), halfSize, halfSize, 0.0F)
            .setColor(r, g, b, a)
            .setUv(1.0F, 0.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT)
            .setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(poseStack.last().pose(), -halfSize, halfSize, 0.0F)
            .setColor(r, g, b, a)
            .setUv(0.0F, 0.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT)
            .setNormal(0.0F, 1.0F, 0.0F);
        poseStack.popPose();
    }
}
