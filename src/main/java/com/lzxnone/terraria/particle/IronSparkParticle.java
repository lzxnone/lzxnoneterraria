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
                                IronSparkParticleOptions options, SpriteSet spriteSet) {
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

        this.dir = new Vector3f(options.dir).normalize();
        this.right = new Vector3f(options.right).normalize();

        this.xd = dir.x;
        this.yd = dir.y;
        this.zd = dir.z;

        this.pickSprite(spriteSet);
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
        float halfLen = size * this.sparkLength * lifeRatio;
        float halfWidth = size * this.sparkLength * (1 - lifeRatio) * 0.01f;

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();

        Vector3f dirVec = new Vector3f(this.dir);
        Vector3f rightVec = new Vector3f(this.right);

        float[][] verts = {
            {cx + dirVec.x * halfLen - rightVec.x * halfWidth, cy + dirVec.y * halfLen - rightVec.y * halfWidth, cz + dirVec.z * halfLen - rightVec.z * halfWidth, u1, v1},
            {cx + dirVec.x * halfLen + rightVec.x * halfWidth, cy + dirVec.y * halfLen + rightVec.y * halfWidth, cz + dirVec.z * halfLen + rightVec.z * halfWidth, u1, v0},
            {cx - dirVec.x * halfLen + rightVec.x * halfWidth, cy - dirVec.y * halfLen + rightVec.y * halfWidth, cz - dirVec.z * halfLen + rightVec.z * halfWidth, u0, v0},
            {cx - dirVec.x * halfLen - rightVec.x * halfWidth, cy - dirVec.y * halfLen - rightVec.y * halfWidth, cz - dirVec.z * halfLen - rightVec.z * halfWidth, u0, v1}
        };

        for(int k = 0;k < 10;k++) {
            for(int j = 0; j < 4; j++) {
                float[] v = verts[j];
                buffer.addVertex(v[0], v[1], v[2])
                        .setUv(v[3], v[4])
                        .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                        .setLight(LightTexture.FULL_BRIGHT);
            }
        }
    }

    @Override
    public int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ModParticleRenderTypes.EMISSIVE_BLOOM_PARTICLE;
    }

    public static class Provider implements ParticleProvider<IronSparkParticleOptions> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(IronSparkParticleOptions type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new IronSparkParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, type, this.spriteSet);
        }
    }
}
