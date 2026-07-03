package com.lzxnone.terraria.entity.summon;

import com.lzxnone.terraria.entity.TintedVertexConsumer;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import java.util.UUID;

public interface IStaticSummonBehavior {
    default void renderItem(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(!(entity instanceof StaticSummon summon)) return;
        ItemStack itemStack = summon.getEntityData().get(StaticSummon.ITEM);
        if(itemStack == ItemStack.EMPTY) return;

        poseStack.pushPose();

        //旋转(方向修正)
        poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTick, summon.yRotO, summon.getYRot())));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, summon.xRotO, summon.getXRot())));

        //旋转(自修正)
        poseStack.mulPose(Axis.XP.rotationDegrees(summon.getEntityData().get(StaticSummon.RXP) + summon.getEntityData().get(StaticSummon.RXPS) * (summon.getEntityData().get(StaticSummon.AGE) + partialTick)));
        poseStack.mulPose(Axis.YP.rotationDegrees(summon.getEntityData().get(StaticSummon.RYP) + summon.getEntityData().get(StaticSummon.RYPS) * (summon.getEntityData().get(StaticSummon.AGE) + partialTick)));
        poseStack.mulPose(Axis.ZP.rotationDegrees(summon.getEntityData().get(StaticSummon.RZP) + summon.getEntityData().get(StaticSummon.RZPS) * (summon.getEntityData().get(StaticSummon.AGE) + partialTick)));

        //缩放
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

    default void renderBlock(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(!(entity instanceof StaticSummon summon)) return;
        BlockState blockState = summon.getEntityData().get(StaticSummon.BLOCK);
        if(blockState == Blocks.AIR.defaultBlockState()) return;

        poseStack.pushPose();

        //旋转(方向修正)
        poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTick, summon.yRotO, summon.getYRot())));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, summon.xRotO, summon.getXRot())));

        //旋转(自修正)
        poseStack.mulPose(Axis.XP.rotationDegrees(summon.getEntityData().get(StaticSummon.RXP) + summon.getEntityData().get(StaticSummon.RXPS) * (summon.getEntityData().get(StaticSummon.AGE) + partialTick)));
        poseStack.mulPose(Axis.YP.rotationDegrees(summon.getEntityData().get(StaticSummon.RYP) + summon.getEntityData().get(StaticSummon.RYPS) * (summon.getEntityData().get(StaticSummon.AGE) + partialTick)));
        poseStack.mulPose(Axis.ZP.rotationDegrees(summon.getEntityData().get(StaticSummon.RZP) + summon.getEntityData().get(StaticSummon.RZPS) * (summon.getEntityData().get(StaticSummon.AGE) + partialTick)));

        //缩放
        poseStack.scale(
            summon.getEntityData().get(StaticSummon.SCALE_X),
            summon.getEntityData().get(StaticSummon.SCALE_Y),
            summon.getEntityData().get(StaticSummon.SCALE_Z)
        );

        //位移(中心对齐)
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
                summon.getEntityData().get(StaticSummon.GLOW) ? LightTexture.FULL_BRIGHT : packedLight,
                OverlayTexture.NO_OVERLAY,
                net.neoforged.neoforge.client.model.data.ModelData.EMPTY,
                null
        );
        poseStack.popPose();
    }

    default void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {}

    default void checkBeforeTick(StaticSummon summon) {
        if(summon.level().isClientSide()) return;
        if(summon.getOwner() == null) {
            onDied(summon);
            return;
        }
        if(summon.getEntityData().get(StaticSummon.AGE) > summon.getEntityData().get(StaticSummon.LIFETIME)) {
            onDied(summon);
        }else {
            summon.getEntityData().set(StaticSummon.AGE, summon.getEntityData().get(StaticSummon.AGE) + 1);
        }
    }

    default void tick(StaticSummon summon) {
        checkBeforeTick(summon);
    }

    default void onDied(StaticSummon summon) {
        if(!summon.level().isClientSide()) summon.discard();
    }
}
