package com.lzxnone.terraria.client.entity.projectile;

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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface IStaticProjectileRenderBehavior {
    default void renderItem(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(!(entity instanceof StaticProjectile proj)) return;
        ItemStack stack = proj.getEntityData().get(StaticProjectile.ITEM);
        if(stack.isEmpty()) return;

        poseStack.pushPose();

        Vec3 velCurrent = entity.getDeltaMovement();
        Vec3 velOld = proj.prevDeltaMovement;
        if(velOld == null) velOld = velCurrent;

        Vector3f velForRot;
        if(velCurrent.lengthSqr() > 0.0001) {
            velForRot = new Vector3f(
                (float) Mth.lerp(partialTick, velOld.x, velCurrent.x),
                (float) Mth.lerp(partialTick, velOld.y, velCurrent.y),
                (float) Mth.lerp(partialTick, velOld.z, velCurrent.z)
            );
        }else {
            velForRot = new Vector3f(proj.getEntityData().get(StaticProjectile.DIRECTION));
        }

        Vector3f[] dirs = MathUtil.computeCoordinateSystem(velForRot, 0);

        Matrix3f rotMatrix = new Matrix3f().set(dirs[2], dirs[1], dirs[0]);
        poseStack.mulPose(new Quaternionf().setFromNormalized(rotMatrix));

        poseStack.mulPose(Axis.XP.rotationDegrees(proj.getEntityData().get(StaticProjectile.RXP) + proj.getEntityData().get(StaticProjectile.RXPS) * (proj.getEntityData().get(StaticProjectile.AGE) + partialTick)));
        poseStack.mulPose(Axis.YP.rotationDegrees(proj.getEntityData().get(StaticProjectile.RYP) + proj.getEntityData().get(StaticProjectile.RYPS) * (proj.getEntityData().get(StaticProjectile.AGE) + partialTick)));
        poseStack.mulPose(Axis.ZP.rotationDegrees(proj.getEntityData().get(StaticProjectile.RZP) + proj.getEntityData().get(StaticProjectile.RZPS) * (proj.getEntityData().get(StaticProjectile.AGE) + partialTick)));

        poseStack.scale(
            proj.getEntityData().get(StaticProjectile.SCALE_X),
            proj.getEntityData().get(StaticProjectile.SCALE_Y),
            proj.getEntityData().get(StaticProjectile.SCALE_Z)
        );

        Minecraft.getInstance().getItemRenderer().renderStatic(
            stack,
            ItemDisplayContext.NONE,
            proj.getEntityData().get(StaticProjectile.GLOW) ? LightTexture.FULL_BRIGHT : packedLight,
            OverlayTexture.NO_OVERLAY,
            poseStack,
            renderType -> {
                VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
                return new TintedVertexConsumer(vertexConsumer,
                    proj.getEntityData().get(StaticProjectile.COLOR_R),
                    proj.getEntityData().get(StaticProjectile.COLOR_G),
                    proj.getEntityData().get(StaticProjectile.COLOR_B),
                    proj.getEntityData().get(StaticProjectile.COLOR_A));
            },
            entity.level(),
            0
        );

        poseStack.popPose();
    }

    default void renderBlock(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(!(entity instanceof StaticProjectile proj)) return;
        BlockState blockState = proj.getEntityData().get(StaticProjectile.BLOCK);
        if(blockState == Blocks.AIR.defaultBlockState()) return;

        Vec3 velCurrent = entity.getDeltaMovement();
        Vec3 velOld = proj.prevDeltaMovement;
        if(velOld == null) velOld = velCurrent;

        Vector3f velForRot;
        if(velCurrent.lengthSqr() > 0.0001) {
            velForRot = new Vector3f(
                (float) Mth.lerp(partialTick, velOld.x, velCurrent.x),
                (float) Mth.lerp(partialTick, velOld.y, velCurrent.y),
                (float) Mth.lerp(partialTick, velOld.z, velCurrent.z)
            );
        }else {
            velForRot = new Vector3f(proj.getEntityData().get(StaticProjectile.DIRECTION));
        }

        Vector3f[] dirs = MathUtil.computeCoordinateSystem(velForRot, 0);

        Matrix3f rotMatrix = new Matrix3f().set(dirs[2], dirs[1], dirs[0]);
        poseStack.mulPose(new Quaternionf().setFromNormalized(rotMatrix));

        poseStack.mulPose(Axis.XP.rotationDegrees(proj.getEntityData().get(StaticProjectile.RXP) + proj.getEntityData().get(StaticProjectile.RXPS) * (proj.getEntityData().get(StaticProjectile.AGE) + partialTick)));
        poseStack.mulPose(Axis.YP.rotationDegrees(proj.getEntityData().get(StaticProjectile.RYP) + proj.getEntityData().get(StaticProjectile.RYPS) * (proj.getEntityData().get(StaticProjectile.AGE) + partialTick)));
        poseStack.mulPose(Axis.ZP.rotationDegrees(proj.getEntityData().get(StaticProjectile.RZP) + proj.getEntityData().get(StaticProjectile.RZPS) * (proj.getEntityData().get(StaticProjectile.AGE) + partialTick)));

        poseStack.scale(
            proj.getEntityData().get(StaticProjectile.SCALE_X),
            proj.getEntityData().get(StaticProjectile.SCALE_Y),
            proj.getEntityData().get(StaticProjectile.SCALE_Z)
        );

        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
            blockState,
            poseStack,
            renderType -> {
                VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
                return new TintedVertexConsumer(vertexConsumer,
                    proj.getEntityData().get(StaticProjectile.COLOR_R),
                    proj.getEntityData().get(StaticProjectile.COLOR_G),
                    proj.getEntityData().get(StaticProjectile.COLOR_B),
                    proj.getEntityData().get(StaticProjectile.COLOR_A));
            },
            proj.getEntityData().get(StaticProjectile.GLOW) ? LightTexture.FULL_BRIGHT : packedLight,
            OverlayTexture.NO_OVERLAY,
            net.neoforged.neoforge.client.model.data.ModelData.EMPTY,
            null
        );
    }

    default void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {}
}
