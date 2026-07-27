package com.lzxnone.terraria.client.item.ammo;

import com.lzxnone.terraria.client.entity.summon.IStaticSummonRenderBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ClientBulletAmmo {
    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR = new IStaticSummonRenderBehavior() {
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;
            ItemStack itemStack = summon.getEntityData().get(StaticSummon.ITEM);
            if(itemStack == ItemStack.EMPTY) return;

            poseStack.pushPose();

            //旋转(方向修正)
            //poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTick, summon.yRotO, summon.getYRot())));
            //poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, summon.xRotO, summon.getXRot())));
            poseStack.mulPose(Axis.YP.rotationDegrees(-summon.getYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(summon.getXRot()));

            poseStack.mulPose(Axis.YP.rotationDegrees(-90));

            poseStack.scale(
                summon.getEntityData().get(StaticSummon.SCALE_X),
                summon.getEntityData().get(StaticSummon.SCALE_Y),
                summon.getEntityData().get(StaticSummon.SCALE_Z)
            );

            Minecraft.getInstance().getItemRenderer().renderStatic(
                itemStack,
                ItemDisplayContext.NONE,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                entity.level(),
                0
            );
            poseStack.popPose();

            poseStack.pushPose();

            //旋转(方向修正)
            //poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTick, summon.yRotO, summon.getYRot())));
            //poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, summon.xRotO, summon.getXRot())));

            poseStack.mulPose(Axis.YP.rotationDegrees(-summon.getYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(summon.getXRot()));

            poseStack.mulPose(Axis.ZP.rotationDegrees(90));
            poseStack.mulPose(Axis.YP.rotationDegrees(-90));

            poseStack.scale(
                summon.getEntityData().get(StaticSummon.SCALE_X),
                summon.getEntityData().get(StaticSummon.SCALE_Y),
                summon.getEntityData().get(StaticSummon.SCALE_Z)
            );

            Minecraft.getInstance().getItemRenderer().renderStatic(
                itemStack,
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
    };
}
