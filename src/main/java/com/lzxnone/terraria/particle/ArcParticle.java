package com.lzxnone.terraria.particle;

import com.lzxnone.terraria.utils.MathUtil;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArcParticle extends TextureSheetParticle {
    private final float baseSize;
    private final float length;
    private final Vector3f baseColor;
    private final int rebuildInterval;

    private final List<List<Vec3>> cachedBranches = new ArrayList<>();

    protected ArcParticle(ClientLevel level, double x, double y, double z,
                          double xSpeed, double ySpeed, double zSpeed,
                          ArcParticleOptions options) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.hasPhysics = false;
        this.friction = 0.92F;
        this.gravity = 0.0F;

        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.baseSize = options.size;
        this.quadSize = this.baseSize;
        this.lifetime = options.lifetime + this.random.nextInt(Math.max(1, options.lifetime / 3));
        this.length = options.length;
        this.baseColor = new Vector3f(options.color);
        this.rebuildInterval = 2;

        this.rCol = options.color.x;
        this.gCol = options.color.y;
        this.bCol = options.color.z;

        rebuildBranches();
    }

    @Override
    public void tick() {
        super.tick();
        if(this.age % this.rebuildInterval == 0 || this.cachedBranches.isEmpty()) {
            rebuildBranches();
        }
    }

    private void rebuildBranches() {
        this.cachedBranches.clear();
        Random rand = new Random(this.random.nextLong());

        // 随机主电弧目标方向与长度
        float l = this.length * (0.85F + rand.nextFloat() * 0.3F);
        Vec3 end = new Vec3(
            (rand.nextFloat() - 0.5F) * 2.0F,
            (rand.nextFloat() - 0.5F) * 2.0F,
            (rand.nextFloat() - 0.5F) * 2.0F
        ).normalize().scale(l);

        // 主电弧（递归深度 3，微型扰动）
        List<Vec3> mainBranch = generateBranch(Vec3.ZERO, end, rand, 3, 0.22F);
        this.cachedBranches.add(mainBranch);

        // 1~2 个次级分叉（深度 2）
        int subCount = 1 + rand.nextInt(2);
        for(int s = 0; s < subCount; s++) {
            if(mainBranch.size() < 3) break;
            int idx = 1 + rand.nextInt(mainBranch.size() - 2);
            Vec3 subStart = mainBranch.get(idx);
            Vec3 mainDir = end.normalize();
            Vec3 subDir = spreadDir(mainDir, rand, 0.45F);
            Vec3 subEnd = subStart.add(subDir.scale(l * 0.45F));
            List<Vec3> subBranch = generateBranch(subStart, subEnd, rand, 2, 0.22F);
            this.cachedBranches.add(subBranch);
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        float lifeRatio = Mth.clamp((this.age + partialTick) / (float) this.lifetime, 0.0F, 1.0F);
        if(lifeRatio > 0.999F) return;

        float alpha = 1.0F - lifeRatio;
        float radius = this.baseSize * (1.0F - lifeRatio * 0.4F);

        float cx = (float) (Mth.lerp(partialTick, this.xo, this.x) - camera.getPosition().x);
        float cy = (float) (Mth.lerp(partialTick, this.yo, this.y) - camera.getPosition().y);
        float cz = (float) (Mth.lerp(partialTick, this.zo, this.z) - camera.getPosition().z);

        for(List<Vec3> branch : this.cachedBranches) {
            int n = branch.size();
            for(int i = 0; i < n - 1; i++) {
                Vec3 p0 = branch.get(i);
                Vec3 p1 = branch.get(i + 1);

                float t0 = (float) i / (float) (n - 1);
                float t1 = (float) (i + 1) / (float) (n - 1);
                float r0 = radius * (1.0F - t0 * 0.7F);
                float r1 = radius * (1.0F - t1 * 0.7F);

                Vec3 segStart = new Vec3(cx + p0.x, cy + p0.y, cz + p0.z);
                Vec3 segEnd = new Vec3(cx + p1.x, cy + p1.y, cz + p1.z);
                Vec3 segDir = segEnd.subtract(segStart);
                if(segDir.lengthSqr() < 1.0E-6D) continue;

                Vec3 segCenter = segStart.add(segEnd).scale(0.5D);
                Vec3 normal = segDir.cross(segCenter).normalize();
                if(normal.lengthSqr() < 1.0E-6D) normal = new Vec3(0, 1, 0);

                // 外层彩色光晕 (Outer Color Glow)
                renderSegment(buffer, segStart, segEnd, normal, r0 * 1.5F, r1 * 1.5F, this.baseColor.x, this.baseColor.y, this.baseColor.z, alpha * 0.65F);

                // 内层高亮白芯 (Inner White Core)
                renderSegment(buffer, segStart, segEnd, normal, r0 * 0.55F, r1 * 0.55F, 1.0F, 1.0F, 1.0F, alpha * 0.95F);
            }
        }
    }

    private static void renderSegment(VertexConsumer buffer, Vec3 start, Vec3 end, Vec3 normal, float r0, float r1, float r, float g, float b, float a) {
        Vec3 w0 = normal.scale(r0);
        Vec3 w1 = normal.scale(r1);

        Vec3 v0 = start.subtract(w0);
        Vec3 v1 = start.add(w0);
        Vec3 v2 = end.add(w1);
        Vec3 v3 = end.subtract(w1);

        buffer.addVertex((float)v0.x, (float)v0.y, (float)v0.z).setColor(r, g, b, a);
        buffer.addVertex((float)v1.x, (float)v1.y, (float)v1.z).setColor(r, g, b, a);
        buffer.addVertex((float)v2.x, (float)v2.y, (float)v2.z).setColor(r, g, b, a);
        buffer.addVertex((float)v3.x, (float)v3.y, (float)v3.z).setColor(r, g, b, a);
    }

    @Override
    public int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ModParticleRenderTypes.ARC_PARTICLE;
    }

    // 分形分支算法
    private static List<Vec3> generateBranch(Vec3 start, Vec3 end, Random rand, int depth, float jitter) {
        List<Vec3> branch = new ArrayList<>();
        branch.add(start);
        subdivide(start, end, depth, jitter, rand, branch);
        branch.add(end);
        return branch;
    }

    private static void subdivide(Vec3 start, Vec3 end, int depth, float jitter, Random rand, List<Vec3> out) {
        if(depth <= 0) return;
        Vec3 mid = start.add(end).scale(0.5D);
        Vec3 seg = end.subtract(start);
        Vector3f[] dirs = MathUtil.computeCoordinateSystem(seg.toVector3f(), 0);
        mid = getSurfaceRandomPosInRadius(mid, MathUtil.toVec3(dirs[2]), MathUtil.toVec3(dirs[1]), seg.length() * jitter, rand);
        subdivide(start, mid, depth - 1, jitter, rand, out);
        out.add(mid);
        subdivide(mid, end, depth - 1, jitter, rand, out);
    }

    private static Vec3 getSurfaceRandomPosInRadius(Vec3 pos, Vec3 axisX, Vec3 axisY, double radius, Random random) {
        double s = Math.sqrt(random.nextDouble());
        axisX = axisX.normalize().scale(radius).scale(s);
        axisY = axisY.normalize().scale(radius).scale(s);
        double rad = Math.PI * 2 * random.nextDouble();
        return new Vec3(
            pos.x + Math.cos(rad) * axisX.x + Math.sin(rad) * axisY.x,
            pos.y + Math.cos(rad) * axisX.y + Math.sin(rad) * axisY.y,
            pos.z + Math.cos(rad) * axisX.z + Math.sin(rad) * axisY.z
        );
    }

    private static Vec3 spreadDir(Vec3 dir, Random rand, float maxAngle) {
        float angle = (rand.nextFloat() - 0.5F) * 2.0F * maxAngle;
        Vec3 axisV = new Vec3(rand.nextDouble() - 0.5, rand.nextDouble() - 0.5, rand.nextDouble() - 0.5).normalize();
        if(axisV.lengthSqr() < 0.01D) axisV = new Vec3(1, 0, 0);
        axisV = axisV.cross(dir).normalize();
        if(axisV.lengthSqr() < 0.01D) axisV = new Vec3(0, 0, 1);
        Vector3f d = dir.toVector3f().normalize();
        new Quaternionf().fromAxisAngleRad(axisV.toVector3f().normalize(), angle).transform(d);
        return new Vec3(d.x(), d.y(), d.z());
    }

    public static class Provider implements ParticleProvider<ArcParticleOptions> {
        @Override
        public Particle createParticle(ArcParticleOptions type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new ArcParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, type);
        }
    }
}
