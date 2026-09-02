package com.lzxnone.terraria.client.item.magic;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.client.entity.summon.IStaticSummonRenderBehavior;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.weapon.magic.ArcSurge;
import com.lzxnone.terraria.utils.MathUtil;
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
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public class ClientArcSurge {
    public static final ResourceLocation RES = ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/normal_trail.png");
    public static final int RING = 8;

    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR = new IStaticSummonRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            if(customData.isEmpty()) return;

            int age = summon.getEntityData().get(StaticSummon.AGE);
            int lifetime = summon.getEntityData().get(StaticSummon.LIFETIME);
            if(lifetime <= 0) return;
            float lifeRatio = Mth.clamp((age + partialTick) / (float) lifetime, 0.0F, 1.0F);
            if(lifeRatio > 0.999F) return;

            float alpha = 1.0F - lifeRatio;
            float radius = ArcSurge.LIGHTNING_RADIUS * (1.0F - lifeRatio);

            List<List<Vec3>> branches = ArcSurge.getAllLightningBranches(customData, summon.level(), summon);
            if(branches.isEmpty()) return;

            Vec3 entWorldPos = new Vec3(
                Mth.lerp(partialTick, summon.xo, summon.getX()),
                Mth.lerp(partialTick, summon.yo, summon.getY()),
                Mth.lerp(partialTick, summon.zo, summon.getZ())
            );

            VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.entityAdditiveEmissive(RES));
            Matrix4f matrix = poseStack.last().pose();

            for(List<Vec3> branch : branches) {
                // 内白芯（Inner White Core - 纯白高亮高能核心，线性收缩至末端半径为 0）
                renderTube(buffer, matrix, entWorldPos, branch, radius * 0.5F, alpha, 1.0F, 1.0F, 1.0F, true);

                // 外红层（Outer Red Layer - 主泛光与外圈微光，线性收缩至末端半径为 0）
                renderTube(buffer, matrix, entWorldPos, branch, radius * 1.0F, alpha * 0.7F, 1.0F, 0.15F, 0.15F, true);
                renderTube(buffer, matrix, entWorldPos, branch, radius * 1.3F, alpha * 0.3F, 0.85F, 0.05F, 0.1F, true);
            }
        }
    };

    public static void renderTube(VertexConsumer buffer, Matrix4f matrix, Vec3 entityWorldPos,
        List<Vec3> points, float radius, float alpha, float colorR, float colorG, float colorB, boolean linear) {
        int n = points.size();
        if(n < 2) return;

        Vector3f[][] oriDirs = new Vector3f[n][3];
        Vec3[] up = new Vec3[n], right = new Vec3[n];
        for(int i = 0; i < n; i++) {
            if(i == n - 1) oriDirs[i] = MathUtil.computeCoordinateSystem(points.get(n - 1).subtract(points.get(n - 2)).toVector3f(), 0);
            else oriDirs[i] = MathUtil.computeCoordinateSystem(points.get(i + 1).subtract(points.get(i)).toVector3f(), 0);
            up[i] = MathUtil.toVec3(oriDirs[i][1]);
            right[i] = MathUtil.toVec3(oriDirs[i][2]);
        }

        for(int i = 0; i < n - 1; i++) {
            float r0 = linear ? radius * (1.0F - (float) i / (float) (n - 1)) : radius;
            float r1 = linear ? radius * (1.0F - (float) (i + 1) / (float) (n - 1)) : radius;
            if(r0 <= 0.0001F && r1 <= 0.0001F) continue;
            Vec3 N = right[i], B = up[i], N1 = right[i + 1], B1 = up[i + 1];
            Vec3 P = points.get(i), P1 = points.get(i + 1);
            for(int j = 0; j < RING; j++) {
                float a0 = (float) (2.0 * Math.PI * j / RING);
                float a1 = (float) (2.0 * Math.PI * (j + 1) / RING);
                Vec3 d0 = computeRingDir(N, B, a0);
                Vec3 d1 = computeRingDir(N1, B1, a0);
                Vec3 d0b = computeRingDir(N, B, a1);
                Vec3 d1b = computeRingDir(N1, B1, a1);
                Vec3 v0 = P.add(d0.scale(r0));
                Vec3 v1 = P1.add(d1.scale(r1));
                Vec3 v2 = P1.add(d1b.scale(r1));
                Vec3 v3 = P.add(d0b.scale(r0));
                for(int k = 0; k < 3; k++) {
                    writeVert(buffer, matrix, entityWorldPos, v0, 0.5f, 0.5f, alpha, colorR, colorG, colorB);
                    writeVert(buffer, matrix, entityWorldPos, v1, 0.5f, 0.5f, alpha, colorR, colorG, colorB);
                    writeVert(buffer, matrix, entityWorldPos, v2, 0.5f, 0.5f, alpha, colorR, colorG, colorB);
                    writeVert(buffer, matrix, entityWorldPos, v3, 0.5f, 0.5f, alpha, colorR, colorG, colorB);
                }
            }
        }
    }

    public static Vec3 computeRingDir(Vec3 n, Vec3 b, float ang) {
        return n.scale(Math.cos(ang)).add(b.scale(Math.sin(ang)));
    }

    public static void writeVert(VertexConsumer buffer, Matrix4f matrix, Vec3 entityWorldPos,
        Vec3 worldPos, float u, float v, float alpha, float colorR, float colorG, float colorB) {
        double lx = worldPos.x - entityWorldPos.x;
        double ly = worldPos.y - entityWorldPos.y;
        double lz = worldPos.z - entityWorldPos.z;

        buffer.addVertex(matrix, (float) lx, (float) ly, (float) lz)
            .setColor(colorR, colorG, colorB, alpha)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT)
            .setNormal(0.0F, 1.0F, 0.0F);
    }
}
