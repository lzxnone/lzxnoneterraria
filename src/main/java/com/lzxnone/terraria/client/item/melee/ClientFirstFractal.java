package com.lzxnone.terraria.client.item.melee;

import com.lzxnone.terraria.client.ClientSkinUtil;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.TintedVertexConsumer;
import com.lzxnone.terraria.entity.summon.*;
import com.lzxnone.terraria.utils.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import java.util.UUID;
import com.lzxnone.terraria.client.entity.summon.IStaticSummonRenderBehavior;
import com.lzxnone.terraria.client.entity.summon.StaticSummonRenderer;

public class ClientFirstFractal {
    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR = new IStaticSummonRenderBehavior() {

        public static final ResourceLocation RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/beam_sparkle.png");
        public static final ResourceLocation RES2 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/first_fractal_star.png");

        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;
            UUID uuid = summon.getEntityData().get(StaticSummon.OWNER).orElse(null);
            if(uuid == null) return;
            ItemStack item = summon.getEntityData().get(StaticSummon.ITEM);
            if(item == ItemStack.EMPTY) return;

            PlayerSkin playerSkin = ClientSkinUtil.getPlayerSkin(uuid, summon.getOwner());

            boolean isSlim = playerSkin.model() == PlayerSkin.Model.SLIM;
            PlayerModel<LivingEntity> activeModel = isSlim ? StaticSummonRenderer.getSlimModel() : StaticSummonRenderer.getWideModel();
            activeModel.rightArm.resetPose();
            activeModel.leftArm.resetPose();
            activeModel.rightLeg.resetPose();
            activeModel.leftLeg.resetPose();
            activeModel.rightSleeve.resetPose();
            activeModel.leftSleeve.resetPose();
            activeModel.rightPants.resetPose();
            activeModel.leftPants.resetPose();
            activeModel.young = false;

            //确定模型方向
            activeModel.rightArm.xRot = (float) Math.toRadians(-90);
            activeModel.leftArm.xRot = (float) Math.toRadians(-90);
            activeModel.rightArm.yRot = (float) Math.toRadians(-30);
            activeModel.leftArm.yRot = (float) Math.toRadians(30);
            activeModel.rightLeg.xRot = (float) Math.toRadians(-30);
            activeModel.leftLeg.xRot = (float) Math.toRadians(30);

            activeModel.rightSleeve.xRot = activeModel.rightArm.xRot;
            activeModel.rightSleeve.yRot = activeModel.rightArm.yRot;
            activeModel.leftSleeve.xRot = activeModel.leftArm.xRot;
            activeModel.leftSleeve.yRot = activeModel.leftArm.yRot;
            activeModel.rightPants.xRot = activeModel.rightLeg.xRot;
            activeModel.leftPants.xRot = activeModel.leftLeg.xRot;

            Vector3f[] dirs = MathUtil.computeCoordinateSystem(summon.getLookAngle().toVector3f(), 0);
            float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);

            Vector3f color = new Vector3f(
                summon.getEntityData().get(StaticSummon.COLOR_R),
                summon.getEntityData().get(StaticSummon.COLOR_G),
                summon.getEntityData().get(StaticSummon.COLOR_B)
            );

            float radio = (float) (summon.getEntityData().get(StaticSummon.AGE) + partialTick) / (float) summon.getEntityData().get(StaticSummon.LIFETIME);
            float alpha;
            if(radio < 0.25f) {
                alpha = radio / 0.25f;
            }else if(radio > 0.75f) {
                alpha = 1.0f - (radio - 0.75f) / 0.25f;
            }else {
                alpha = 1.0f;
            }

            if(alpha < 0.001f) return;

            //流星
            poseStack.pushPose();
            float starHalfWidth = 45 * 0.06f;
            float starHalfHeight = 17 * 0.06f;
            VertexConsumer starConsumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES2));

            poseStack.translate(
                dirs[0].x * 0 + dirs[1].x * 1.3,
                dirs[0].y * 0 + dirs[1].y * 1.3,
                dirs[0].z * 0 + dirs[1].z * 1.3
            );

            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
            poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));

            poseStack.mulPose(Axis.YP.rotationDegrees(270));

            poseStack.scale(0.75f + alpha * 0.25f, 0.75f + alpha * 0.25f , 0.75f + alpha * 0.25f);
            for(int i = 0; i < 3; i++) {
                starConsumer.addVertex(poseStack.last().pose(), -starHalfWidth, -starHalfHeight, 0f)
                    .setColor(color.x, color.y, color.z, alpha * 0.5f).setUv(0.0f, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                starConsumer.addVertex(poseStack.last().pose(), starHalfWidth, -starHalfHeight, 0f)
                    .setColor(color.x, color.y, color.z, alpha * 0.5f).setUv(1.0f, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                starConsumer.addVertex(poseStack.last().pose(), starHalfWidth, starHalfHeight, 0f)
                    .setColor(color.x, color.y, color.z, alpha * 0.5f).setUv(1.0f, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                starConsumer.addVertex(poseStack.last().pose(), -starHalfWidth, starHalfHeight, 0f)
                    .setColor(color.x, color.y, color.z, alpha * 0.5f).setUv(0.0f, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            }
            poseStack.popPose();

            //闪烁
            poseStack.pushPose();
            float sparkleHalfWidth = 32 * 0.12f;
            float sparkleHalfHeight = 32 * 0.03f;

            VertexConsumer sparkleConsumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES));

            poseStack.translate(
                dirs[0].x * 2 + dirs[1].x * 1.3,
                dirs[0].y * 2 + dirs[1].y * 1.3,
                dirs[0].z * 2 + dirs[1].z * 1.3
            );

            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));

            poseStack.mulPose(Axis.YP.rotationDegrees(270));

            poseStack.scale(alpha, alpha , alpha);

            for(int i = 0; i < 3; i++) {
                sparkleConsumer.addVertex(poseStack.last().pose(), -sparkleHalfWidth, -sparkleHalfHeight, 0.01f)
                    .setColor(color.x, color.y, color.z, alpha).setUv(0.0f, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                sparkleConsumer.addVertex(poseStack.last().pose(), sparkleHalfWidth, -sparkleHalfHeight, 0.01f)
                    .setColor(color.x, color.y, color.z, alpha).setUv(1.0f, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                sparkleConsumer.addVertex(poseStack.last().pose(), sparkleHalfWidth, sparkleHalfHeight, 0.01f)
                    .setColor(color.x, color.y, color.z, alpha).setUv(1.0f, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                sparkleConsumer.addVertex(poseStack.last().pose(), -sparkleHalfWidth, sparkleHalfHeight, 0.01f)
                    .setColor(color.x, color.y, color.z, alpha).setUv(0.0f, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            }
            poseStack.popPose();

            //剑
            poseStack.pushPose();

            poseStack.translate(
                dirs[0].x * 2.2 + dirs[1].x * 1.5,
                dirs[0].y * 2.2 + dirs[1].y * 1.5,
                dirs[0].z * 2.2 + dirs[1].z * 1.5
            );

            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
            poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));

            poseStack.mulPose(Axis.YP.rotationDegrees(270));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-45));

            poseStack.scale(4.0f, 4.0f , 4.0f);

            Minecraft.getInstance().getItemRenderer().renderStatic(
                item,
                ItemDisplayContext.NONE,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                renderType -> {
                    VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
                    return new TintedVertexConsumer(vertexConsumer, 1.0f, 1.0f, 1.0f, alpha);
                },
                summon.level(),
                0
            );

            poseStack.popPose();

            //模型
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
            poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));

            poseStack.mulPose(Axis.YP.rotationDegrees(180));

            poseStack.scale(-1.0F, -1.0F, 1.0F);
            poseStack.translate(0.0F, -1.501F, 0.0F);

            VertexConsumer modelConsumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucent(playerSkin.texture()));

            int packedColor = FastColor.ARGB32.color(Math.min((int)(alpha * 255), 255), 255, 255, 255);
            activeModel.renderToBuffer(poseStack, modelConsumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, packedColor);
            poseStack.popPose();

            //恢复模型
            activeModel.rightArm.resetPose();
            activeModel.leftArm.resetPose();
            activeModel.rightLeg.resetPose();
            activeModel.leftLeg.resetPose();
            activeModel.rightSleeve.resetPose();
            activeModel.leftSleeve.resetPose();
            activeModel.rightPants.resetPose();
            activeModel.leftPants.resetPose();
            activeModel.young = false;
        }
    };
}
