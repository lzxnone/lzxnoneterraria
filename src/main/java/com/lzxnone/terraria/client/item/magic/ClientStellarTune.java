package com.lzxnone.terraria.client.item.magic;

import com.lzxnone.terraria.client.entity.projectile.IStaticProjectileRenderBehavior;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.TintedVertexConsumer;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.utils.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ClientStellarTune {
    private static final ResourceLocation STAR_WAVE_RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/star_wave.png");
    private static final float STAR_WAVE_HALF_WIDTH = 45 * 0.06F;
    private static final float STAR_WAVE_HALF_HEIGHT = 17 * 0.06F;

    public static final IStaticProjectileRenderBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile projectile)) return;
            ItemStack itemStack = projectile.getEntityData().get(StaticProjectile.ITEM);
            if(itemStack.isEmpty()) return;

            Vec3 dir = projectile.getDeltaMovement();
            if(dir.lengthSqr() < 1.0E-7D) dir = MathUtil.toVec3(projectile.getEntityData().get(StaticProjectile.DIRECTION));
            if(dir.lengthSqr() < 1.0E-7D) dir = new Vec3(0.0D, 0.0D, 1.0D);
            dir = dir.normalize();
            float[] xyRot = MathUtil.computeXYRot(dir.toVector3f());

            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
            poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
            poseStack.scale(0.5f, 0.5f, 0.5f);

            Minecraft.getInstance().getItemRenderer().renderStatic(
                itemStack,
                ItemDisplayContext.NONE,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                renderType -> new TintedVertexConsumer(
                    bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(InventoryMenu.BLOCK_ATLAS)),
                    1.0F,
                    0.95F,
                    0.2F,
                    1.0F
                ),
                entity.level(),
                0
            );
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(-dir.x * 0.75D, -dir.y * 0.75D, -dir.z * 0.75D);
            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
            poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));
            poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
            poseStack.scale(0.5f, 0.5f, 0.5f);

            VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(STAR_WAVE_RES));
            addQuad(consumer, poseStack.last().pose(), STAR_WAVE_HALF_WIDTH, STAR_WAVE_HALF_HEIGHT, 1.0F, 0.62F, 0.9F, 0.5F);
            poseStack.mulPose(Axis.XP.rotationDegrees(60.0F));
            addQuad(consumer, poseStack.last().pose(), STAR_WAVE_HALF_WIDTH, STAR_WAVE_HALF_HEIGHT, 1.0F, 0.62F, 0.9F, 0.5F);
            poseStack.mulPose(Axis.XP.rotationDegrees(60.0F));
            addQuad(consumer, poseStack.last().pose(), STAR_WAVE_HALF_WIDTH, STAR_WAVE_HALF_HEIGHT, 1.0F, 0.62F, 0.9F, 0.5F);
            poseStack.popPose();
        }
    };

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
