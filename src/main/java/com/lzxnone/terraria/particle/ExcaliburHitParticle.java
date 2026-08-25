package com.lzxnone.terraria.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ExcaliburHitParticle extends TextureSheetParticle {
    private final float baseSize;

    protected ExcaliburHitParticle(ClientLevel level, double x, double y, double z,
                                   double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.hasPhysics = false;

        this.friction = 0.9F;
        this.gravity = 0.0F;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.baseSize = 1.0F;
        this.quadSize = baseSize;

        this.alpha = 1.0f;

        this.lifetime = 10;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        float lifeRatio = Mth.clamp((this.age + partialTick) / (float) this.lifetime, 0.0f, 1.0f);
        float grow = (float) Mth.smoothstep(Mth.clamp(lifeRatio / 0.20f, 0.0f, 1.0f));
        float shrink = 1.0f - (float) Mth.smoothstep(
            Mth.clamp((lifeRatio - 0.60f) / 0.40f, 0.0f, 1.0f)
        );
        float fade = 1.0f - (float) Mth.smoothstep(
            Mth.clamp((lifeRatio - 0.50f) / 0.50f, 0.0f, 1.0f)
        );
        float size = this.baseSize * grow * shrink;
        if(size <= 0.001f || fade <= 0.001f) {
            return;
        }

        float cx = (float) (Mth.lerp(partialTick, this.xo, this.x) - camera.getPosition().x);
        float cy = (float) (Mth.lerp(partialTick, this.yo, this.y) - camera.getPosition().y);
        float cz = (float) (Mth.lerp(partialTick, this.zo, this.z) - camera.getPosition().z);
        Quaternionf rotation = new Quaternionf(camera.rotation());

        Vector3f[] corners = {
            new Vector3f(1.0f, -1.0f, 0.0f),
            new Vector3f(1.0f, 1.0f, 0.0f),
            new Vector3f(-1.0f, 1.0f, 0.0f),
            new Vector3f(-1.0f, -1.0f, 0.0f)
        };
        float[][] uvs = {
            {1.0f, 1.0f},
            {1.0f, 0.0f},
            {0.0f, 0.0f},
            {0.0f, 1.0f}
        };
        for(int i = 0; i < corners.length; i++) {
            Vector3f corner = corners[i].rotate(rotation).mul(size).add(cx, cy, cz);
            buffer.addVertex(corner.x, corner.y, corner.z)
                .setUv(uvs[i][0], uvs[i][1])
                .setColor(1.0f, 1.0f, 1.0f, fade)
                .setLight(LightTexture.FULL_BRIGHT);
        }
    }

    @Override
    public int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ModParticleRenderTypes.EXCALIBUR_HIT_PARTICLE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new ExcaliburHitParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}
