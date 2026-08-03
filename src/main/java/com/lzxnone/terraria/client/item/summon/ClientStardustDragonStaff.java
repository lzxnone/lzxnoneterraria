package com.lzxnone.terraria.client.item.summon;

import com.lzxnone.terraria.block.ModBlocks;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class ClientStardustDragonStaff {
    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR = new IStaticSummonRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;

            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            BlockState blockState = getSegmentBlockState(customData);
            if(blockState == null) return;

            poseStack.pushPose();

            poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTick, summon.yRotO, summon.getYRot())));
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, summon.xRotO, summon.getXRot())));

            poseStack.scale(
                summon.getEntityData().get(StaticSummon.SCALE_X),
                summon.getEntityData().get(StaticSummon.SCALE_Y),
                summon.getEntityData().get(StaticSummon.SCALE_Z)
            );

            if(customData.contains("head")) poseStack.translate(0, 0.1, 0);

            poseStack.translate(-0.5D, -0.5D, -0.5D);

            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                blockState,
                poseStack,
                renderType -> {
                    VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
                    return new TintedVertexConsumer(vertexConsumer,
                        summon.getEntityData().get(StaticSummon.COLOR_R),
                        summon.getEntityData().get(StaticSummon.COLOR_G),
                        summon.getEntityData().get(StaticSummon.COLOR_B),
                        summon.getEntityData().get(StaticSummon.COLOR_A));
                },
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY,
                null
            );
            poseStack.popPose();
        }
    };

    private static BlockState getSegmentBlockState(CompoundTag customData) {
        if(customData.contains("head")) return ModBlocks.STARDUST_DRAGON_HEAD.get().defaultBlockState();
        if(customData.contains("bodyA")) return ModBlocks.STARDUST_DRAGON_BODY_A.get().defaultBlockState();
        if(customData.contains("bodyB")) return ModBlocks.STARDUST_DRAGON_BODY_B.get().defaultBlockState();
        if(customData.contains("tail")) return ModBlocks.STARDUST_DRAGON_TAIL.get().defaultBlockState();
        return null;
    }
}
