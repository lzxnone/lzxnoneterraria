package com.lzxnone.terraria.client.item.ranged;

import com.lzxnone.terraria.client.entity.summon.IStaticSummonRenderBehavior;
import com.lzxnone.terraria.entity.TintedVertexConsumer;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ClientSnowballCannon {
    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR = new IStaticSummonRenderBehavior() {
        @Override
        public void renderItem(Entity entity, float entityYaw, float partialTick,
                               PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;
            ItemStack itemStack = summon.getEntityData().get(StaticSummon.ITEM);
            if(itemStack == ItemStack.EMPTY) return;

            poseStack.pushPose();

            poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

            poseStack.mulPose(Axis.XP.rotationDegrees(summon.getEntityData().get(StaticSummon.RXP) + summon.getEntityData().get(StaticSummon.RXPS) * (summon.getEntityData().get(StaticSummon.AGE) + partialTick)));
            poseStack.mulPose(Axis.YP.rotationDegrees(summon.getEntityData().get(StaticSummon.RYP) + summon.getEntityData().get(StaticSummon.RYPS) * (summon.getEntityData().get(StaticSummon.AGE) + partialTick)));
            poseStack.mulPose(Axis.ZP.rotationDegrees(summon.getEntityData().get(StaticSummon.RZP) + summon.getEntityData().get(StaticSummon.RZPS) * (summon.getEntityData().get(StaticSummon.AGE) + partialTick)));

            poseStack.scale(
                summon.getEntityData().get(StaticSummon.SCALE_X),
                summon.getEntityData().get(StaticSummon.SCALE_Y),
                summon.getEntityData().get(StaticSummon.SCALE_Z)
            );

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
    };
}
