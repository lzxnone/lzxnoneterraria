package com.lzxnone.terraria.client.item.magic;

import com.lzxnone.terraria.client.entity.summon.IStaticSummonRenderBehavior;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.utils.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.LinkedList;

public class ClientBetsysWrath {
    private static final ResourceLocation RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/normal_wave.png");
    private static final int POINTS_PER_SECTION = 2;
    private static final float[] PLANE_ROTATIONS = new float[]{0.0F, 60.0F, 120.0F};
    private static final double SCALE = 2.0D;
    private static final float TAIL_COLOR_START = 0.1F;
    private static final float BODY_RED = 1.0F;
    private static final float BODY_GREEN = 0.85F;
    private static final float BODY_BLUE = 0.12F;
    private static final float TAIL_RED = 0.65F;
    private static final float TAIL_GREEN = 0.0F;
    private static final float TAIL_BLUE = 0.0F;

    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR = new IStaticSummonRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;
            LinkedList<Vec3> positions = summon.trailPositions;
            if(positions.size() < POINTS_PER_SECTION * 2) return;

            VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES));
            Matrix4f matrix = poseStack.last().pose();
            Vec3 renderOrigin = summon.getPosition(partialTick);
            int sectionCount = positions.size() / POINTS_PER_SECTION;

            for(float rotationDegrees : PLANE_ROTATIONS) {
                for(int i = 0; i < sectionCount - 1; i++) {
                    Vec3 sectionRight0 = positions.get(i * POINTS_PER_SECTION);
                    Vec3 sectionLeft0 = positions.get(i * POINTS_PER_SECTION + 1);
                    Vec3 sectionRight1 = positions.get((i + 1) * POINTS_PER_SECTION);
                    Vec3 sectionLeft1 = positions.get((i + 1) * POINTS_PER_SECTION + 1);

                    Vec3 center0 = sectionRight0.add(sectionLeft0).scale(0.5D);
                    Vec3 center1 = sectionRight1.add(sectionLeft1).scale(0.5D);
                    Vec3 half0 = sectionRight0.subtract(sectionLeft0).scale(0.5D * SCALE);
                    Vec3 half1 = sectionRight1.subtract(sectionLeft1).scale(0.5D * SCALE);
                    Vec3 direction = center1.subtract(center0);

                    if(direction.lengthSqr() > 1.0E-7D) {
                        Quaternionf rotation = new Quaternionf().fromAxisAngleRad(direction.normalize().toVector3f(), (float) Math.toRadians(rotationDegrees));
                        half0 = MathUtil.toVec3(half0.toVector3f().rotate(rotation));
                        half1 = MathUtil.toVec3(half1.toVector3f().rotate(rotation));
                    }

                    Vec3 right0 = center0.add(half0).subtract(renderOrigin);
                    Vec3 left0 = center0.subtract(half0).subtract(renderOrigin);
                    Vec3 right1 = center1.add(half1).subtract(renderOrigin);
                    Vec3 left1 = center1.subtract(half1).subtract(renderOrigin);

                    float v0 = i / (float) (sectionCount - 1);
                    float v1 = (i + 1) / (float) (sectionCount - 1);
                    float u0 = 1.0F - v0;
                    float u1 = 1.0F - v1;
                    float tail0 = Math.max(0.0F, Math.min(1.0F, (v0 - TAIL_COLOR_START) / (1.0F - TAIL_COLOR_START)));
                    float tail1 = Math.max(0.0F, Math.min(1.0F, (v1 - TAIL_COLOR_START) / (1.0F - TAIL_COLOR_START)));
                    for(int layer = 0; layer < 2; layer++) {
                        boolean tailLayer = layer == 1;
                        float red0 = tailLayer ? TAIL_RED : BODY_RED;
                        float green0 = tailLayer ? TAIL_GREEN : BODY_GREEN;
                        float blue0 = tailLayer ? TAIL_BLUE : BODY_BLUE;
                        float alpha0 = tailLayer ? tail0 : 1.0F - tail0;
                        float red1 = tailLayer ? TAIL_RED : BODY_RED;
                        float green1 = tailLayer ? TAIL_GREEN : BODY_GREEN;
                        float blue1 = tailLayer ? TAIL_BLUE : BODY_BLUE;
                        float alpha1 = tailLayer ? tail1 : 1.0F - tail1;

                        consumer.addVertex(matrix, (float) left0.x, (float) left0.y, (float) left0.z)
                            .setColor(red0, green0, blue0, alpha0).setUv(u0, 0.0F)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0F, 1.0F, 0.0F);
                        consumer.addVertex(matrix, (float) right0.x, (float) right0.y, (float) right0.z)
                            .setColor(red0, green0, blue0, alpha0).setUv(u0, 1.0F)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0F, 1.0F, 0.0F);
                        consumer.addVertex(matrix, (float) right1.x, (float) right1.y, (float) right1.z)
                            .setColor(red1, green1, blue1, alpha1).setUv(u1, 1.0F)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0F, 1.0F, 0.0F);
                        consumer.addVertex(matrix, (float) left1.x, (float) left1.y, (float) left1.z)
                            .setColor(red1, green1, blue1, alpha1).setUv(u1, 0.0F)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0F, 1.0F, 0.0F);
                    }
                }
            }
        }
    };
}
