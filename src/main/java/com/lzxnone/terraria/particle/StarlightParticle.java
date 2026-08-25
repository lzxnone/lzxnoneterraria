package com.lzxnone.terraria.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class StarlightParticle extends TextureSheetParticle {
    private static final Vector3f DEFAULT_COLOR = new Vector3f(1.0f, 1.0f, 1.0f);

    private final float baseSize;
    private final float rotSpeed;

    protected StarlightParticle(ClientLevel level, double x, double y, double z,
                                double xSpeed, double ySpeed, double zSpeed,
                                StarlightParticleOptions options) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.hasPhysics = false;
        this.friction = 0.9f;
        this.gravity = 0.0f;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        float initSize = Math.max(0.0f, options.initSize);
        this.baseSize = initSize + this.random.nextFloat() * initSize;
        this.quadSize = this.baseSize;
        this.roll = this.random.nextFloat() * Mth.TWO_PI;
        this.oRoll = this.roll;
        this.rotSpeed = (this.random.nextFloat() - 0.5f) * options.initRotSpeed;

        int initLifetime = Math.max(1, options.initLifetime);
        this.lifetime = initLifetime + this.random.nextInt(Math.max(1, initLifetime / 2));

        Vector3f[] palette = options.colors;
        Vector3f color = palette != null && palette.length > 0
            ? palette[this.random.nextInt(palette.length)]
            : DEFAULT_COLOR;
        this.rCol = color.x;
        this.gCol = color.y;
        this.bCol = color.z;
    }

    @Override
    public void tick() {
        super.tick();
        this.oRoll = this.roll;
        this.roll += this.rotSpeed;

        float lifeRatio = Mth.clamp(this.age / (float) this.lifetime, 0.0f, 1.0f);
        this.quadSize = this.baseSize * (1.0f - lifeRatio);
        this.alpha = 1.0f - lifeRatio;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        float size = this.getQuadSize(partialTick);
        float alpha = Mth.lerp(partialTick, Math.min(1.0f, this.alpha + 1.0f / this.lifetime), this.alpha);
        if(size <= 0.001f || alpha <= 0.001f) {
            return;
        }

        float cx = (float) (Mth.lerp(partialTick, this.xo, this.x) - camera.getPosition().x);
        float cy = (float) (Mth.lerp(partialTick, this.yo, this.y) - camera.getPosition().y);
        float cz = (float) (Mth.lerp(partialTick, this.zo, this.z) - camera.getPosition().z);
        Quaternionf rotation = new Quaternionf(camera.rotation());
        rotation.rotateZ(Mth.lerp(partialTick, this.oRoll, this.roll));

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
                .setColor(this.rCol, this.gCol, this.bCol, alpha)
                .setLight(LightTexture.FULL_BRIGHT);
        }
    }

    @Override
    public int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ModParticleRenderTypes.STARLIGHT_PARTICLE;
    }

    public static class Provider implements ParticleProvider<StarlightParticleOptions> {
        @Override
        public Particle createParticle(StarlightParticleOptions options, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new StarlightParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, options);
        }
    }
}
