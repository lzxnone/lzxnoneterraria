package com.lzxnone.terraria.client.item.accessory;

import com.lzxnone.terraria.client.entity.projectile.IStaticProjectileRenderBehavior;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.utils.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class ClientAccessoryFallenStar {
    private static final ResourceLocation STAR_WAVE_RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/star_wave.png");
    private static final float STAR_WAVE_HALF_WIDTH = 45 * 0.06F;
    private static final float STAR_WAVE_HALF_HEIGHT = 17 * 0.06F;
    private static final float STAR_WAVE_ALPHA = 0.5F;
    //紫色
    private static final float STAR_WAVE_COLOR_R = 0.7F;
    private static final float STAR_WAVE_COLOR_G = 0.3F;
    private static final float STAR_WAVE_COLOR_B = 1.0F;

    public static final IStaticProjectileRenderBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile projectile)) return;
            ItemStack itemStack = projectile.getEntityData().get(StaticProjectile.ITEM);
            if(itemStack == ItemStack.EMPTY) return;
            renderItem(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
            renderStarWave(projectile, poseStack, bufferSource);
        }
    };

    private static void renderStarWave(StaticProjectile projectile, PoseStack poseStack, MultiBufferSource bufferSource) {
        Vec3 dir = projectile.getDeltaMovement().normalize();
        float[] xyRot = MathUtil.computeXYRot(dir.toVector3f());

        poseStack.pushPose();
        poseStack.translate(-dir.x * 1.5, -dir.y * 1.5, -dir.z * 1.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
        poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));

        VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(STAR_WAVE_RES));
        addQuad(consumer, poseStack.last().pose(), STAR_WAVE_HALF_WIDTH, STAR_WAVE_HALF_HEIGHT,
            STAR_WAVE_COLOR_R, STAR_WAVE_COLOR_G, STAR_WAVE_COLOR_B, STAR_WAVE_ALPHA);
        poseStack.popPose();
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
