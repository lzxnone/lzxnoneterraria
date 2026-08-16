package com.lzxnone.terraria.client.item.melee;

import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.weapon.melee.flail.Flail;
import com.lzxnone.terraria.utils.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.*;
import org.joml.Vector3f;
import com.lzxnone.terraria.client.entity.projectile.IStaticProjectileRenderBehavior;
import com.lzxnone.terraria.client.entity.summon.IStaticSummonRenderBehavior;

public class ClientFlail {
    private static double getRightSign(boolean mainHand, Entity owner) {
        if(!(owner instanceof Player player)) return 1.0;
        boolean isRightHand = mainHand == (player.getMainArm() == HumanoidArm.RIGHT);
        return isRightHand ? 1.0 : -1.0;
    }

    public static final float CHAIN_LENGTH = 0.25f;

    public static final IStaticProjectileRenderBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile projectile)) return;
            if(projectile.getOwner() == null) return;
            ItemStack itemStack = projectile.getEntityData().get(StaticProjectile.ITEM);
            if(itemStack == ItemStack.EMPTY) return;
            CompoundTag customData = projectile.getEntityData().get(StaticProjectile.CUSTOM_DATA);
            ResourceLocation chainRes = ResourceLocation.parse(customData.contains("chainRes") ? customData.getString("chainRes") : "");

            Vector3f[] dirs = MathUtil.computeCoordinateSystem(projectile.getOwner());
            float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);

            poseStack.pushPose();

            //旋转(方向修正)
            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
            poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));

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
            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
            poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0] + 90));

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

            if(projectile.getOwner() == null) return;
            Vec3 eyePos = projectile.getOwner().getEyePosition();
            boolean projectileMainHand = customData.getBoolean("mainHand");
            Vec3 start = eyePos.add(new Vec3(
                dirs[0].x * Flail.DIRECTION_OFFSET + dirs[1].x * Flail.UP_OFFSET + dirs[2].x * Flail.RIGHT_OFFSET * getRightSign(projectileMainHand, projectile.getOwner()),
                dirs[0].y * Flail.DIRECTION_OFFSET + dirs[1].y * Flail.UP_OFFSET + dirs[2].y * Flail.RIGHT_OFFSET * getRightSign(projectileMainHand, projectile.getOwner()),
                dirs[0].z * Flail.DIRECTION_OFFSET + dirs[1].z * Flail.UP_OFFSET + dirs[2].z * Flail.RIGHT_OFFSET * getRightSign(projectileMainHand, projectile.getOwner())
            ));
            Vec3 end = projectile.getPosition(partialTick);
            double dist = end.subtract(start).length();
            Vector3f[] dirs2 = MathUtil.computeCoordinateSystem(end.subtract(start).toVector3f(), 0);
            Vec3 dir = MathUtil.toVec3(dirs2[0]);
            Vec3 right = MathUtil.toVec3(dirs2[2]);
            Vec3 up = MathUtil.toVec3(dirs2[1]);

            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(chainRes));

            int count = (int) (dist / CHAIN_LENGTH);
            for(int j = 0;j < 2;j++) {
                for(int i = 0; i < count; i++) {
                    Vec3 current = start.add(new Vec3(
                        dir.x * i * CHAIN_LENGTH,
                        dir.y * i * CHAIN_LENGTH,
                        dir.z * i * CHAIN_LENGTH
                    )).subtract(projectile.getPosition(partialTick));

                    Vec3 next = start.add(new Vec3(
                        dir.x * (i + 1) * CHAIN_LENGTH,
                        dir.y * (i + 1) * CHAIN_LENGTH,
                        dir.z * (i + 1) * CHAIN_LENGTH
                    )).subtract(projectile.getPosition(partialTick));

                    Vec3 p1, p2, p3, p4;
                    if(j == 0) {
                        p1 = current.add(right.scale(0.125));
                        p2 = current.add(right.scale(-0.125));
                        p3 = next.add(right.scale(-0.125));
                        p4 = next.add(right.scale(0.125));
                    }else {
                        p1 = current.add(up.scale(0.125));
                        p2 = current.add(up.scale(-0.125));
                        p3 = next.add(up.scale(-0.125));
                        p4 = next.add(up.scale(0.125));
                    }

                    vertexConsumer.addVertex(poseStack.last().pose(), (float) p1.x, (float) p1.y, (float) p1.z)
                        .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 1, 0);
                    vertexConsumer.addVertex(poseStack.last().pose(), (float) p2.x, (float) p2.y, (float) p2.z)
                        .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 1.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 1, 0);
                    vertexConsumer.addVertex(poseStack.last().pose(), (float) p3.x, (float) p3.y, (float) p3.z)
                        .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(1.0f, 1.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 1, 0);
                    vertexConsumer.addVertex(poseStack.last().pose(), (float) p4.x, (float) p4.y, (float) p4.z)
                        .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(1.0f, 0.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 1, 0);
                }
            }
        }
    };

    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR = new IStaticSummonRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;
            ItemStack itemStack = summon.getEntityData().get(StaticSummon.ITEM);
            if(itemStack == ItemStack.EMPTY) return;
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            ResourceLocation chainRes = ResourceLocation.parse(customData.contains("chainRes") ? customData.getString("chainRes") : "");

            poseStack.pushPose();

            //旋转(方向修正)
            poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTick, summon.yRotO, summon.getYRot())));
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, summon.xRotO, summon.getXRot())));

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
            poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTick, summon.yRotO, summon.getYRot())));
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, summon.xRotO + 90, summon.getXRot() + 90)));

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

            if(summon.getOwner() == null) return;
            Vector3f[] dirs = MathUtil.computeCoordinateSystem(summon.getOwner());
            Vec3 eyePos = summon.getOwner().getEyePosition();
            boolean summonMainHand = summon.getEntityData().get(StaticSummon.CUSTOM_DATA).getBoolean("mainHand");
            Vec3 start = eyePos.add(new Vec3(
                dirs[0].x * Flail.DIRECTION_OFFSET + dirs[1].x * Flail.UP_OFFSET + dirs[2].x * Flail.RIGHT_OFFSET * getRightSign(summonMainHand, summon.getOwner()),
                dirs[0].y * Flail.DIRECTION_OFFSET + dirs[1].y * Flail.UP_OFFSET + dirs[2].y * Flail.RIGHT_OFFSET * getRightSign(summonMainHand, summon.getOwner()),
                dirs[0].z * Flail.DIRECTION_OFFSET + dirs[1].z * Flail.UP_OFFSET + dirs[2].z * Flail.RIGHT_OFFSET * getRightSign(summonMainHand, summon.getOwner())
            ));
            Vec3 end = summon.getPosition(partialTick);
            double dist = end.subtract(start).length();
            Vector3f[] dirs2 = MathUtil.computeCoordinateSystem(end.subtract(start).toVector3f(), 0);
            Vec3 dir = MathUtil.toVec3(dirs2[0]);
            Vec3 right = MathUtil.toVec3(dirs2[2]);
            Vec3 up = MathUtil.toVec3(dirs2[1]);

            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(chainRes));

            int count = (int) (dist / CHAIN_LENGTH);
            for(int j = 0;j < 2;j++) {
                for(int i = 0; i < count; i++) {
                    Vec3 current = start.add(new Vec3(
                        dir.x * i * CHAIN_LENGTH,
                        dir.y * i * CHAIN_LENGTH,
                        dir.z * i * CHAIN_LENGTH
                    )).subtract(summon.getPosition(partialTick));

                    Vec3 next = start.add(new Vec3(
                        dir.x * (i + 1) * CHAIN_LENGTH,
                        dir.y * (i + 1) * CHAIN_LENGTH,
                        dir.z * (i + 1) * CHAIN_LENGTH
                    )).subtract(summon.getPosition(partialTick));

                    Vec3 p1, p2, p3, p4;
                    if(j == 0) {
                        p1 = current.add(right.scale(0.125));
                        p2 = current.add(right.scale(-0.125));
                        p3 = next.add(right.scale(-0.125));
                        p4 = next.add(right.scale(0.125));
                    }else {
                        p1 = current.add(up.scale(0.125));
                        p2 = current.add(up.scale(-0.125));
                        p3 = next.add(up.scale(-0.125));
                        p4 = next.add(up.scale(0.125));
                    }

                    vertexConsumer.addVertex(poseStack.last().pose(), (float) p1.x, (float) p1.y, (float) p1.z)
                        .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 1, 0);
                    vertexConsumer.addVertex(poseStack.last().pose(), (float) p2.x, (float) p2.y, (float) p2.z)
                        .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 1.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 1, 0);
                    vertexConsumer.addVertex(poseStack.last().pose(), (float) p3.x, (float) p3.y, (float) p3.z)
                        .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(1.0f, 1.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 1, 0);
                    vertexConsumer.addVertex(poseStack.last().pose(), (float) p4.x, (float) p4.y, (float) p4.z)
                        .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(1.0f, 0.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 1, 0);
                }
            }
        }
    };
}
