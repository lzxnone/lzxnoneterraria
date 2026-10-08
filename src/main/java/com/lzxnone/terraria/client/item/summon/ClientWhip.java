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
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class ClientWhip implements IStaticSummonRenderBehavior {
    private static final int SEGMENTS = 24;
    private static final float WIDTH = 1.0F;

    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR = new ClientWhip();

    protected Vector4f getVertexColor(StaticSummon summon, float progressAlongWhip, float partialTick) {
        return new Vector4f(
            summon.getEntityData().get(StaticSummon.COLOR_R),
            summon.getEntityData().get(StaticSummon.COLOR_G),
            summon.getEntityData().get(StaticSummon.COLOR_B),
            summon.getEntityData().get(StaticSummon.COLOR_A)
        );
    }

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

        List<Vec3> points = computePoints(hand, dir, up, progress, range, height, bend, reverse, SEGMENTS);
        if(points.size() < 2) return;

        // 计算归一化累积弧长序列
        int n = points.size();
        double[] cumulativeDist = new double[n];
        double totalLength = 0.0D;
        for(int i = 1; i < n; i++) {
            totalLength += points.get(i).distanceTo(points.get(i - 1));
            cumulativeDist[i] = totalLength;
        }
        if(totalLength < 1.0E-5D) return;

        double[] normalizedDist = new double[n];
        for(int i = 0; i < n; i++) {
            normalizedDist[i] = cumulativeDist[i] / totalLength;
        }

        if(!customData.contains("tailRes") || !customData.contains("bodyRes") || !customData.contains("headRes")) return;

        String tailResStr = customData.getString("tailRes");
        String bodyResStr = customData.getString("bodyRes");
        String headResStr = customData.getString("headRes");
        if(tailResStr.isEmpty() || bodyResStr.isEmpty() || headResStr.isEmpty()) return;

        float tailRatio = customData.getFloat("tailRatio");
        float headRatio = customData.getFloat("headRatio");
        float bodyUnitRatio = customData.getFloat("bodyUnitRatio");

        tailRatio = Mth.clamp(tailRatio, 0.0F, 0.9F);
        headRatio = Mth.clamp(headRatio, 0.0F, 0.9F - tailRatio);
        bodyUnitRatio = Math.max(0.001F, bodyUnitRatio);

        float bodyStart = tailRatio;
        float bodyEnd = 1.0F - headRatio;

        ResourceLocation tailRes = ResourceLocation.parse(tailResStr);
        ResourceLocation bodyRes = ResourceLocation.parse(bodyResStr);
        ResourceLocation headRes = ResourceLocation.parse(headResStr);

        // 1. 渲染尾部 (Tail - 不拉伸，单张贴图完整映射)
        if(tailRatio > 1.0E-4F) {
            List<Vec3> tailPoints = extractSubPath(points, normalizedDist, 0.0F, tailRatio);
            renderRibbonCross(summon, tailPoints, 0.0F, tailRatio, origin, right, up, tailRes, 0.0F, 1.0F, poseStack, bufferSource, partialTick);
        }

        // 2. 渲染身体 (Body - Tiling 平铺渲染，按 bodyUnitRatio 单元循环平铺)
        if(bodyEnd > bodyStart + 1.0E-4F) {
            float cur = bodyStart;
            while(cur < bodyEnd - 1.0E-5F) {
                float next = Math.min(bodyEnd, cur + bodyUnitRatio);
                float uMax = (next - cur) / bodyUnitRatio;
                List<Vec3> bodyTilePoints = extractSubPath(points, normalizedDist, cur, next);
                renderRibbonCross(summon, bodyTilePoints, cur, next, origin, right, up, bodyRes, 0.0F, uMax, poseStack, bufferSource, partialTick);
                cur = next;
            }
        }

        // 3. 渲染头部 (Head - 不拉伸，单张贴图完整映射)
        if(headRatio > 1.0E-4F) {
            List<Vec3> headPoints = extractSubPath(points, normalizedDist, bodyEnd, 1.0F);
            renderRibbonCross(summon, headPoints, bodyEnd, 1.0F, origin, right, up, headRes, 0.0F, 1.0F, poseStack, bufferSource, partialTick);
        }
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

        private static List<Vec3> extractSubPath(List<Vec3> points, double[] s, float startS, float endS) {
            List<Vec3> result = new ArrayList<>();
            if(points.size() < 2 || startS >= endS) return result;

            int n = points.size();
            for(int i = 0; i < n - 1; i++) {
                double s0 = s[i];
                double s1 = s[i + 1];

                if(s1 < startS - 1.0E-6) continue;
                if(s0 > endS + 1.0E-6) break;

                Vec3 p0 = points.get(i);
                Vec3 p1 = points.get(i + 1);

                // 若起点落在 (s0, s1] 之间，插值得到起点
                if(s0 < startS && startS <= s1) {
                    double t = (startS - s0) / (s1 - s0);
                    Vec3 startP = p0.lerp(p1, t);
                    result.add(startP);
                } else if(s0 >= startS && result.isEmpty()) {
                    result.add(p0);
                }

                // 若终点落在 [s0, s1) 之间，插值得到终点并结束
                if(s0 <= endS && endS < s1) {
                    double t = (endS - s0) / (s1 - s0);
                    Vec3 endP = p0.lerp(p1, t);
                    result.add(endP);
                    break;
                } else if(s1 <= endS) {
                    result.add(p1);
                }
            }
            return result;
        }

    private void renderRibbonCross(StaticSummon summon, List<Vec3> points, float segStartProgress, float segEndProgress, Vec3 origin, Vec3 right, Vec3 up, ResourceLocation res, float uMin, float uMax, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick) {
        renderRibbon(summon, points, segStartProgress, segEndProgress, origin, right, res, uMin, uMax, poseStack, bufferSource, partialTick);
        renderRibbon(summon, points, segStartProgress, segEndProgress, origin, up, res, uMin, uMax, poseStack, bufferSource, partialTick);
    }

    private void renderRibbon(StaticSummon summon, List<Vec3> points, float segStartProgress, float segEndProgress, Vec3 origin, Vec3 widthDir, ResourceLocation res, float uMin, float uMax, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick) {
        if(points.size() < 2) return;
        VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(res));
        Vec3 halfWidth = widthDir.normalize().scale(WIDTH * 0.5F);
        double subLength = 0.0D;
        double[] distances = new double[points.size()];

        for(int i = 1; i < points.size(); i++) {
            subLength += points.get(i).distanceTo(points.get(i - 1));
            distances[i] = subLength;
        }
        if(subLength < 1.0E-5D) return;

        for(int i = 0; i < points.size() - 1; i++) {
            Vec3 p0 = points.get(i);
            Vec3 p1 = points.get(i + 1);
            Vec3 tangent = p1.subtract(p0);
            if(tangent.lengthSqr() < 1.0E-6D) continue;

            Vec3 a = p0.add(halfWidth).subtract(origin);
            Vec3 b = p0.subtract(halfWidth).subtract(origin);
            Vec3 c = p1.subtract(halfWidth).subtract(origin);
            Vec3 d = p1.add(halfWidth).subtract(origin);
            float u0 = uMin + (float)(distances[i] / subLength) * (uMax - uMin);
            float u1 = uMin + (float)(distances[i + 1] / subLength) * (uMax - uMin);

            float localProgress0 = (float)(distances[i] / subLength);
            float localProgress1 = (float)(distances[i + 1] / subLength);
            float globalProgress0 = Mth.lerp(localProgress0, segStartProgress, segEndProgress);
            float globalProgress1 = Mth.lerp(localProgress1, segStartProgress, segEndProgress);

            Vector4f color0 = getVertexColor(summon, globalProgress0, partialTick);
            Vector4f color1 = getVertexColor(summon, globalProgress1, partialTick);

            addVertex(consumer, poseStack, a, u0, 0.0F, color0);
            addVertex(consumer, poseStack, b, u0, 1.0F, color0);
            addVertex(consumer, poseStack, c, u1, 1.0F, color1);
            addVertex(consumer, poseStack, d, u1, 0.0F, color1);
        }
    }

    private static void addVertex(VertexConsumer consumer, PoseStack poseStack, Vec3 pos, float u, float v, Vector4f color) {
        consumer.addVertex(poseStack.last().pose(), (float)pos.x, (float)pos.y, (float)pos.z)
            .setColor(color.x(), color.y(), color.z(), color.w())
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
}
