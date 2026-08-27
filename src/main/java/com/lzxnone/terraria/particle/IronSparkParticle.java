package com.lzxnone.terraria.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public class IronSparkParticle extends TextureSheetParticle {
    private final float baseSize;
    private final float sparkLength;
    private final Vector3f dir;
    private final Vector3f right;

    protected IronSparkParticle(ClientLevel level, double x, double y, double z,
                                double xSpeed, double ySpeed, double zSpeed,
                                IronSparkParticleOptions options) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.hasPhysics = false;

        this.friction = 0.9F;
        this.gravity = 0.0F;

        this.rCol = options.color.x;
        this.gCol = options.color.y;
        this.bCol = options.color.z;

        this.baseSize = options.initSize + (this.random.nextFloat() * options.initSize * 0.5f);
        this.quadSize = baseSize;
        this.sparkLength = options.sparkLength;

        this.lifetime = options.initLifetime + this.random.nextInt(options.initLifetime / 2);

        Vector3f velocity = new Vector3f(options.dir);
        this.dir = new Vector3f(velocity).normalize();
        this.right = new Vector3f(options.right).normalize();

        this.xd = velocity.x;
        this.yd = velocity.y;
        this.zd = velocity.z;
    }

    @Override
    public void tick() {
        super.tick();

        float lifeRatio = (float) this.age / (float) this.lifetime;

        this.quadSize = this.baseSize * (1.0F - lifeRatio * 0.8f);
        this.alpha = 1.0F - lifeRatio;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        if (this.alpha <= 0.01F) return;

        float lifeRatio = (this.age + partialTick) / (float) this.lifetime;

        float cx = (float) (Mth.lerp(partialTick, this.xo, this.x) - camera.getPosition().x);
        float cy = (float) (Mth.lerp(partialTick, this.yo, this.y) - camera.getPosition().y);
        float cz = (float) (Mth.lerp(partialTick, this.zo, this.z) - camera.getPosition().z);

        float size = this.quadSize;
        float grow = (float) Mth.smoothstep(Mth.clamp(lifeRatio / 0.15f, 0.0f, 1.0f));
        float halfLen = size * this.sparkLength * grow;
        float halfWidth = size * this.sparkLength * Mth.lerp(lifeRatio, 0.025f, 0.010f);

        // 专用 shader 将 UV 当作 0..1 局部坐标，不再采样粒子图集。
        float u0 = 0.0f;
        float u1 = 1.0f;
        float v0 = 0.0f;
        float v1 = 1.0f;

        Vector3f dirVec = new Vector3f(this.dir);
        Vector3f toCamera = new Vector3f(-cx, -cy, -cz);
        Vector3f rightVec = new Vector3f();
        if (toCamera.lengthSquared() > 0.000001f) {
            toCamera.normalize();
            toCamera.cross(dirVec, rightVec);
        }
        if (rightVec.lengthSquared() <= 0.000001f) {
            rightVec.set(this.right);
        }
        rightVec.normalize();

        float[][] verts = {
            {cx + dirVec.x * halfLen - rightVec.x * halfWidth, cy + dirVec.y * halfLen - rightVec.y * halfWidth, cz + dirVec.z * halfLen - rightVec.z * halfWidth, u1, v1},
            {cx + dirVec.x * halfLen + rightVec.x * halfWidth, cy + dirVec.y * halfLen + rightVec.y * halfWidth, cz + dirVec.z * halfLen + rightVec.z * halfWidth, u1, v0},
            {cx - dirVec.x * halfLen + rightVec.x * halfWidth, cy - dirVec.y * halfLen + rightVec.y * halfWidth, cz - dirVec.z * halfLen + rightVec.z * halfWidth, u0, v0},
            {cx - dirVec.x * halfLen - rightVec.x * halfWidth, cy - dirVec.y * halfLen - rightVec.y * halfWidth, cz - dirVec.z * halfLen - rightVec.z * halfWidth, u0, v1}
        };

        for(int j = 0; j < 4; j++) {
            float[] v = verts[j];
            buffer.addVertex(v[0], v[1], v[2])
                    .setUv(v[3], v[4])
                    .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                    .setLight(LightTexture.FULL_BRIGHT);
        }
    }

    @Override
    public int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ModParticleRenderTypes.IRON_SPARK_PARTICLE;
    }

    public static class Provider implements ParticleProvider<IronSparkParticleOptions> {
        @Override
        public Particle createParticle(IronSparkParticleOptions type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new IronSparkParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, type);
        }
    }
}
