package com.lzxnone.terraria.client.item.melee;

import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import com.lzxnone.terraria.client.entity.projectile.IStaticProjectileRenderBehavior;

public class ClientMuramasa {
    public static final IStaticProjectileRenderBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileRenderBehavior() {

        public static final ResourceLocation RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/muramasa_trail.png");
        public static final int MAX_LENGTH = 100;

        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile projectile)) return;
            Vec3 currentPos = projectile.getPosition(partialTick);

            VertexConsumer vertexConsumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES));
            Matrix4f matrix = poseStack.last().pose();

            int quadCount = projectile.trailPositions.size() / 2 - 1;
            for(int i = 0; i < quadCount;i++) {
                Vec3 currentPoint1 = projectile.trailPositions.get(i * 2);
                Vec3 currentPoint2 = projectile.trailPositions.get(i * 2 + 1);
                Vec3 nextPoint1 = projectile.trailPositions.get(i * 2 + 3);
                Vec3 nextPoint2 = projectile.trailPositions.get(i * 2 + 2);

                double x1 = currentPoint1.x - currentPos.x;
                double y1 = currentPoint1.y - currentPos.y;
                double z1 = currentPoint1.z - currentPos.z;

                double x2 = currentPoint2.x - currentPos.x;
                double y2 = currentPoint2.y - currentPos.y;
                double z2 = currentPoint2.z - currentPos.z;

                double x3 = nextPoint1.x - currentPos.x;
                double y3 = nextPoint1.y - currentPos.y;
                double z3 = nextPoint1.z - currentPos.z;

                double x4 = nextPoint2.x - currentPos.x;
                double y4 = nextPoint2.y - currentPos.y;
                double z4 = nextPoint2.z - currentPos.z;

                float radio1 = (float) i / quadCount;
                float radio2 = (float) (i + 1) / quadCount;

                vertexConsumer.addVertex(matrix, (float)x1, (float)y1, (float)z1)
                    .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(radio1, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                vertexConsumer.addVertex(matrix, (float)x2, (float)y2, (float)z2)
                    .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(radio1, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                vertexConsumer.addVertex(matrix, (float)x3, (float)y3, (float)z3)
                    .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(radio2, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                vertexConsumer.addVertex(matrix, (float)x4, (float)y4, (float)z4)
                    .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(radio2, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
            }
        }
    };
}
