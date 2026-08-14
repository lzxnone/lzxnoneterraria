package com.lzxnone.terraria.client.item.ranged;

import com.lzxnone.terraria.client.entity.projectile.IStaticProjectileRenderBehavior;
import com.lzxnone.terraria.client.entity.summon.IStaticSummonRenderBehavior;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.TintedVertexConsumer;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.summon.StaticSummon;
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
import org.joml.Vector3f;

public class ClientSuperStarShooter {
    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR = new IStaticSummonRenderBehavior() {
        public static final ResourceLocation RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/star_wave.png");

        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;
            ItemStack itemStack = summon.getEntityData().get(StaticSummon.ITEM);
            if(itemStack == ItemStack.EMPTY) return;

            float age = summon.getEntityData().get(StaticSummon.AGE) + partialTick;

            poseStack.pushPose();

            //旋转(方向修正)
            poseStack.mulPose(Axis.YP.rotationDegrees(-summon.getYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(summon.getXRot()));

            poseStack.mulPose(Axis.XP.rotationDegrees(age * 15));
            poseStack.mulPose(Axis.YP.rotationDegrees(-90));

            poseStack.scale(
                summon.getEntityData().get(StaticSummon.SCALE_X),
                summon.getEntityData().get(StaticSummon.SCALE_Y),
                summon.getEntityData().get(StaticSummon.SCALE_Z)
            );

            Minecraft.getInstance().getItemRenderer().renderStatic(
                itemStack,
                ItemDisplayContext.NONE,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                renderType -> {
                    VertexConsumer vertexConsumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(InventoryMenu.BLOCK_ATLAS));
                    return new TintedVertexConsumer(vertexConsumer,
                        1.0f,
                        0.95f,
                        0.2f,
                        1.0f);
                },
                entity.level(),
                0
            );
            poseStack.popPose();

            poseStack.pushPose();
            float starHalfWidth = 45 * 0.06f;
            float starHalfHeight = 17 * 0.06f;
            VertexConsumer starConsumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES));

            Vec3 dir = summon.getLookAngle().normalize();
            double dist = -0.75;
            poseStack.translate(dir.x * dist, dir.y * dist, dir.z * dist);

            poseStack.mulPose(Axis.YP.rotationDegrees(-summon.getYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(summon.getXRot()));

            poseStack.mulPose(Axis.YP.rotationDegrees(270));

            poseStack.scale(
                summon.getEntityData().get(StaticSummon.SCALE_X),
                summon.getEntityData().get(StaticSummon.SCALE_Y),
                summon.getEntityData().get(StaticSummon.SCALE_Z)
            );

            Vector3f color = new Vector3f(1.0f, 0.72f, 0.35f);
            float alpha = 0.5f;
            for(int i = 0; i < 3; i++) {
                starConsumer.addVertex(poseStack.last().pose(), -starHalfWidth, -starHalfHeight, 0f)
                    .setColor(color.x, color.y, color.z, alpha).setUv(0.0f, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                starConsumer.addVertex(poseStack.last().pose(), starHalfWidth, -starHalfHeight, 0f)
                    .setColor(color.x, color.y, color.z, alpha).setUv(1.0f, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                starConsumer.addVertex(poseStack.last().pose(), starHalfWidth, starHalfHeight, 0f)
                    .setColor(color.x, color.y, color.z, alpha).setUv(1.0f, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                starConsumer.addVertex(poseStack.last().pose(), -starHalfWidth, starHalfHeight, 0f)
                    .setColor(color.x, color.y, color.z, alpha).setUv(0.0f, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                poseStack.mulPose(Axis.XP.rotationDegrees(60.0F));
            }
            poseStack.popPose();
        }
    };

    public static final IStaticProjectileRenderBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileRenderBehavior() {
        public static final ResourceLocation RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/prismatic_bolt.png");

        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile projectile)) return;

            poseStack.pushPose();
            float starHalfWidth = 72 * 0.02f;
            float starHalfHeight = 72 * 0.02f;
            VertexConsumer starConsumer = bufferSource.getBuffer(ModRenderTypes.entityAdditiveEmissive(RES));

            Vector3f[] dirs = MathUtil.computeCoordinateSystem(projectile.getDeltaMovement().toVector3f(), 0);
            float[] xyRot = MathUtil.computeXYRot(dirs[0]);
            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
            poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));

            poseStack.mulPose(Axis.XP.rotationDegrees(90));

            poseStack.scale(1.0f, 2.0f, 1.0f);

            for(int i = 0; i < 3; i++) {
                starConsumer.addVertex(poseStack.last().pose(), -starHalfWidth, -starHalfHeight, 0f)
                    .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                starConsumer.addVertex(poseStack.last().pose(), starHalfWidth, -starHalfHeight, 0f)
                    .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(1.0f, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                starConsumer.addVertex(poseStack.last().pose(), starHalfWidth, starHalfHeight, 0f)
                    .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(1.0f, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                starConsumer.addVertex(poseStack.last().pose(), -starHalfWidth, starHalfHeight, 0f)
                    .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            }
            poseStack.popPose();
        }
    };
}
