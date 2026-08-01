package com.lzxnone.terraria.client.item.summon;

import com.lzxnone.terraria.client.entity.projectile.IStaticProjectileRenderBehavior;
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

public class ClientPossession {
    public static final IStaticProjectileRenderBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileRenderBehavior() {
        private static final ResourceLocation RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/possession_beam.png");

        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile projectile)) return;
            if(projectile.trailPositions.size() < 4) return;

            VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.entityAdditiveEmissive(RES));
            Matrix4f matrix = poseStack.last().pose();
            Vec3 origin = projectile.getPosition(partialTick);
            int pairCount = projectile.trailPositions.size() / 2;
            int quadCount = pairCount - 1;
            if(quadCount <= 0) return;

            float lifetime = Math.max(1.0F, projectile.getEntityData().get(StaticProjectile.LIFETIME));
            float age = projectile.getEntityData().get(StaticProjectile.AGE) + partialTick;
            float baseAlpha = Math.max(0.0F, 1.0F - Math.max(0.0F, age - lifetime * 0.75F) / (lifetime * 0.25F));

            for(int i = 0; i < quadCount; i++) {
                Vec3 headRight = projectile.trailPositions.get(i * 2);
                Vec3 headLeft = projectile.trailPositions.get(i * 2 + 1);
                Vec3 tailRight = projectile.trailPositions.get((i + 1) * 2);
                Vec3 tailLeft = projectile.trailPositions.get((i + 1) * 2 + 1);

                float headU = 1.0F - i / (float)quadCount;
                float tailU = 1.0F - (i + 1) / (float)quadCount;

                for(int k = 0;k < 3;k++) {
                    addVertex(consumer, matrix, headRight.subtract(origin), headU, 0.0F, 1.0f);
                    addVertex(consumer, matrix, headLeft.subtract(origin), headU, 1.0F, 1.0f);
                    addVertex(consumer, matrix, tailLeft.subtract(origin), tailU, 1.0F, 1.0f);
                    addVertex(consumer, matrix, tailRight.subtract(origin), tailU, 0.0F, 1.0f);
                }
            }
        }

        private static void addVertex(VertexConsumer consumer, Matrix4f matrix, Vec3 pos, float u, float v, float alpha) {
            consumer.addVertex(matrix, (float)pos.x, (float)pos.y, (float)pos.z)
                .setColor(1.0F, 1.0F, 1.0F, alpha)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0.0F, 1.0F, 0.0F);
        }
    };
}
