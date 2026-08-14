package com.lzxnone.terraria.client.item.melee;

import com.lzxnone.terraria.client.entity.projectile.IStaticProjectileRenderBehavior;
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
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ClientStarfallProjectile {
    public static final IStaticProjectileRenderBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileRenderBehavior() {
        @Override
        public void renderItem(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile projectile)) return;
            ItemStack stack = projectile.getEntityData().get(StaticProjectile.ITEM);
            if(stack.isEmpty()) return;

            poseStack.pushPose();

            Vec3 velCurrent = entity.getDeltaMovement();
            Vec3 velOld = projectile.prevDeltaMovement;
            if(velOld == null) velOld = velCurrent;

            Vector3f velForRot;
            if(velCurrent.lengthSqr() > 0.0001D) {
                velForRot = new Vector3f(
                    (float) Mth.lerp(partialTick, velOld.x, velCurrent.x),
                    (float) Mth.lerp(partialTick, velOld.y, velCurrent.y),
                    (float) Mth.lerp(partialTick, velOld.z, velCurrent.z)
                );
            }else {
                velForRot = new Vector3f(projectile.getEntityData().get(StaticProjectile.DIRECTION));
            }

            Vector3f[] dirs = MathUtil.computeCoordinateSystem(velForRot, 0);
            Matrix3f rotMatrix = new Matrix3f().set(dirs[2], dirs[1], dirs[0]);
            poseStack.mulPose(new Quaternionf().setFromNormalized(rotMatrix));

            poseStack.mulPose(Axis.XP.rotationDegrees(projectile.getEntityData().get(StaticProjectile.RXP) + projectile.getEntityData().get(StaticProjectile.RXPS) * (projectile.getEntityData().get(StaticProjectile.AGE) + partialTick)));
            poseStack.mulPose(Axis.YP.rotationDegrees(projectile.getEntityData().get(StaticProjectile.RYP) + projectile.getEntityData().get(StaticProjectile.RYPS) * (projectile.getEntityData().get(StaticProjectile.AGE) + partialTick)));
            poseStack.mulPose(Axis.ZP.rotationDegrees(projectile.getEntityData().get(StaticProjectile.RZP) + projectile.getEntityData().get(StaticProjectile.RZPS) * (projectile.getEntityData().get(StaticProjectile.AGE) + partialTick)));

            poseStack.scale(
                projectile.getEntityData().get(StaticProjectile.SCALE_X),
                projectile.getEntityData().get(StaticProjectile.SCALE_Y),
                projectile.getEntityData().get(StaticProjectile.SCALE_Z)
            );

            for(int i = 0; i < 3; i++) {
                Minecraft.getInstance().getItemRenderer().renderStatic(
                    stack,
                    ItemDisplayContext.NONE,
                    projectile.getEntityData().get(StaticProjectile.GLOW) ? LightTexture.FULL_BRIGHT : packedLight,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    renderType -> {
                        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
                        return new TintedVertexConsumer(vertexConsumer,
                            projectile.getEntityData().get(StaticProjectile.COLOR_R),
                            projectile.getEntityData().get(StaticProjectile.COLOR_G),
                            projectile.getEntityData().get(StaticProjectile.COLOR_B),
                            projectile.getEntityData().get(StaticProjectile.COLOR_A));
                    },
                    entity.level(),
                    0
                );
                poseStack.mulPose(Axis.XP.rotationDegrees(60.0F));
            }

            poseStack.popPose();
        }
    };
}
