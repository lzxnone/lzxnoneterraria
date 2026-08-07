package com.lzxnone.terraria.client.item.normal;

import com.lzxnone.terraria.client.entity.summon.IStaticSummonRenderBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ClientManaStar {
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
                bufferSource,
                entity.level(),
                0
            );
            poseStack.popPose();
        }
    };
}
