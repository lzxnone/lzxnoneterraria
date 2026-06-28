package com.lzxnone.terraria.entity.projectile;

import com.lzxnone.terraria.utils.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TextureProjectileRenderer extends EntityRenderer<Entity> {

    public TextureProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(Entity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(!(entity instanceof TextureProjectile proj)) return;
        if(!proj.isInit || proj.getEntityData().get(TextureProjectile.AGE) < 2) return;

        ItemStack stack = proj.getEntityData().get(TextureProjectile.ITEM);
        if(stack.isEmpty()) return;

        float scaleX = proj.getEntityData().get(TextureProjectile.SCALE_X);
        float scaleY = proj.getEntityData().get(TextureProjectile.SCALE_Y);

        poseStack.pushPose();

        // 缩放
        poseStack.scale(scaleX, scaleY, scaleX);

        // 朝飞行方向旋转
        Vec3 velCurrent = entity.getDeltaMovement();
        Vec3 velOld = proj.prevDeltaMovement;

        Vector3f velForRot;
        if(velCurrent.lengthSqr() > 0.0001) {
            velForRot = new Vector3f(
                (float) Mth.lerp(partialTick, velOld.x, velCurrent.x),
                (float) Mth.lerp(partialTick, velOld.y, velCurrent.y),
                (float) Mth.lerp(partialTick, velOld.z, velCurrent.z)
            );
        } else {
            velForRot = new Vector3f(proj.getEntityData().get(TextureProjectile.DIRECTION));
        }

        Vector3f[] dirs = MathUtil.computeProjectileDir(velForRot);

        Matrix3f rotMatrix = new Matrix3f().set(dirs[2], dirs[1], dirs[0]);
        poseStack.mulPose(new Quaternionf().setFromNormalized(rotMatrix));

        poseStack.mulPose(Axis.XP.rotationDegrees(proj.getEntityData().get(TextureProjectile.RXP) + proj.getEntityData().get(TextureProjectile.RXPS) * proj.getEntityData().get(TextureProjectile.AGE)));
        poseStack.mulPose(Axis.YP.rotationDegrees(proj.getEntityData().get(TextureProjectile.RYP) + proj.getEntityData().get(TextureProjectile.RYPS) * proj.getEntityData().get(TextureProjectile.AGE)));
        poseStack.mulPose(Axis.ZP.rotationDegrees(proj.getEntityData().get(TextureProjectile.RZP) + proj.getEntityData().get(TextureProjectile.RZPS) * proj.getEntityData().get(TextureProjectile.AGE)));

        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack,
                ItemDisplayContext.NONE,
                proj.getEntityData().get(TextureProjectile.GLOW) ? LightTexture.FULL_BRIGHT : packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                entity.level(),
                0
        );

        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(Entity entity) {
        return ResourceLocation.parse("lzxnoneterraria:textures/particle/enchanted_sword_beam.png");
    }
}
