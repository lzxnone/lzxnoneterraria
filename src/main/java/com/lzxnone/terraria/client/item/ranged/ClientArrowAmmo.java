package com.lzxnone.terraria.client.item.ranged;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.client.entity.summon.IStaticSummonRenderBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ClientArrowAmmo {
    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR = new IStaticSummonRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;
            ItemStack itemStack = summon.getEntityData().get(StaticSummon.ITEM);
            if(itemStack == ItemStack.EMPTY) return;

            renderArrowPlane(entity, summon, poseStack, bufferSource, packedLight, 0.0F);
            renderArrowPlane(entity, summon, poseStack, bufferSource, packedLight, 90.0F);
        }

        private void renderArrowPlane(Entity entity, StaticSummon summon, PoseStack poseStack,
                                      MultiBufferSource bufferSource, int packedLight, float roll) {
            poseStack.pushPose();

            poseStack.mulPose(Axis.YP.rotationDegrees(-summon.getYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(summon.getXRot()));
            poseStack.mulPose(Axis.ZP.rotationDegrees(roll));
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));

            poseStack.scale(
                summon.getEntityData().get(StaticSummon.SCALE_X),
                summon.getEntityData().get(StaticSummon.SCALE_Y),
                summon.getEntityData().get(StaticSummon.SCALE_Z)
            );

            Minecraft.getInstance().getItemRenderer().renderStatic(
                summon.getEntityData().get(StaticSummon.ITEM),
                ItemDisplayContext.NONE,
                summon.getEntityData().get(StaticSummon.GLOW) ? LightTexture.FULL_BRIGHT : packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                entity.level(),
                0
            );
            poseStack.popPose();
        }
    };
}
