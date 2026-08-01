package com.lzxnone.terraria.client.item.summon;

import com.lzxnone.terraria.client.entity.summon.IStaticSummonRenderBehavior;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ClientWhip {
    private static final int SEGMENTS = 24;
    private static final float WIDTH = 1.0F;

    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR = new IStaticSummonRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon) || summon.getOwner() == null) return;
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            int lifetime = Math.max(1, summon.getEntityData().get(StaticSummon.LIFETIME));
            float progress = Mth.clamp((summon.getEntityData().get(StaticSummon.AGE) + partialTick) / lifetime, 0.0F, 1.0F);

            Vec3 origin = entity.getPosition(partialTick);
            Vec3 hand = origin;
            Vec3 dir = readVec3(customData, "dir", new Vec3(0.0D, 0.0D, 1.0D)).normalize();
            Vec3 up = readVec3(customData, "up", new Vec3(0.0D, 1.0D, 0.0D)).normalize();
            Vec3 right = readVec3(customData, "right", new Vec3(1.0D, 0.0D, 0.0D)).normalize();
            if(dir.lengthSqr() < 1.0E-6D) dir = new Vec3(0.0D, 0.0D, 1.0D);
            if(up.lengthSqr() < 1.0E-6D) up = new Vec3(0.0D, 1.0D, 0.0D);
            if(right.lengthSqr() < 1.0E-6D) right = new Vec3(1.0D, 0.0D, 0.0D);

            double range = customData.contains("range") ? customData.getDouble("range") : 8.0D;
            double height = customData.contains("height") ? customData.getDouble("height") : range * 0.25D;
            double bend = customData.contains("bend") ? customData.getDouble("bend") : range * 0.25D;
            boolean reverse = customData.contains("reverse") && customData.getBoolean("reverse");

            if(!customData.contains("res") || customData.getString("res").isEmpty()) return;
            ResourceLocation res = ResourceLocation.parse(customData.getString("res"));
            List<Vec3> points = computePoints(hand, dir, up, progress, range, height, bend, reverse, SEGMENTS);
            renderRibbon(points, origin, right, res, poseStack, bufferSource);
            renderRibbon(points, origin, up, res, poseStack, bufferSource);//
        }

        private static List<Vec3> computePoints(Vec3 hand, Vec3 dir, Vec3 up, float progress, double range, double height, double bend, boolean reverse, int segments) {
            float phase = 1.0F - Math.abs(progress * 2.0F - 1.0F);
            float s = easeInOut(phase);
            float tipArc = 4.0F * s * (1.0F - s);
            Vec3 tip = hand
                .add(dir.scale(range * s))
                .add(up.scale(height * tipArc));
            Vec3 chord = tip.subtract(hand);
            double bendAmount = bend * (1.0F - s);
            Vec3 dynamicBendDir = dir.scale(1.0F - s).add(up.scale(-s)).normalize();
            if(dynamicBendDir.lengthSqr() < 1.0E-6D) dynamicBendDir = dir;
            List<Vec3> points = new ArrayList<>(segments + 1);

            for(int i = 0; i <= segments; i++) {
                float t = i / (float)segments;
                float shape = 4.0F * t * (1.0F - t);
                Vec3 base = hand.add(chord.scale(t));
                Vec3 point = base.add(dynamicBendDir.scale(shape * bendAmount));
                boolean reflect = reverse ? progress <= 0.5F : progress > 0.5F;
                points.add(reflect ? reflectAcrossDir(hand, dir, point) : point);
            }
            return points;
        }

        private static Vec3 reflectAcrossDir(Vec3 hand, Vec3 dir, Vec3 point) {
            Vec3 relative = point.subtract(hand);
            Vec3 parallel = dir.scale(relative.dot(dir));
            Vec3 perpendicular = relative.subtract(parallel);
            return hand.add(parallel.subtract(perpendicular));
        }

        private static void renderRibbon(List<Vec3> points, Vec3 origin, Vec3 widthDir, ResourceLocation res, PoseStack poseStack, MultiBufferSource bufferSource) {
            if(points.size() < 2) return;
            VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(res));
            Vec3 halfWidth = widthDir.normalize().scale(WIDTH * 0.5F);
            double totalLength = 0.0D;
            double[] distances = new double[points.size()];

            for(int i = 1; i < points.size(); i++) {
                totalLength += points.get(i).distanceTo(points.get(i - 1));
                distances[i] = totalLength;
            }
            if(totalLength < 1.0E-5D) return;

            for(int i = 0; i < points.size() - 1; i++) {
                Vec3 p0 = points.get(i);
                Vec3 p1 = points.get(i + 1);
                Vec3 tangent = p1.subtract(p0);
                if(tangent.lengthSqr() < 1.0E-6D) continue;

                Vec3 a = p0.add(halfWidth).subtract(origin);
                Vec3 b = p0.subtract(halfWidth).subtract(origin);
                Vec3 c = p1.subtract(halfWidth).subtract(origin);
                Vec3 d = p1.add(halfWidth).subtract(origin);
                float u0 = (float)(distances[i] / totalLength);
                float u1 = (float)(distances[i + 1] / totalLength);

                addVertex(consumer, poseStack, a, u0, 0.0F);
                addVertex(consumer, poseStack, b, u0, 1.0F);
                addVertex(consumer, poseStack, c, u1, 1.0F);
                addVertex(consumer, poseStack, d, u1, 0.0F);
            }
        }

        private static void addVertex(VertexConsumer consumer, PoseStack poseStack, Vec3 pos, float u, float v) {
            consumer.addVertex(poseStack.last().pose(), (float)pos.x, (float)pos.y, (float)pos.z)
                .setColor(1.0F, 1.0F, 1.0F, 1.0F)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0.0F, 1.0F, 0.0F);
        }

        private static Vec3 readVec3(CompoundTag tag, String prefix, Vec3 fallback) {
            String x = prefix + "X";
            String y = prefix + "Y";
            String z = prefix + "Z";
            if(!tag.contains(x) || !tag.contains(y) || !tag.contains(z)) return fallback;
            return new Vec3(tag.getDouble(x), tag.getDouble(y), tag.getDouble(z));
        }

        private static float easeInOut(float value) {
            return value < 0.5F ? 2.0F * value * value : 1.0F - (float)Math.pow(-2.0F * value + 2.0F, 2.0D) / 2.0F;
        }
    };
}
