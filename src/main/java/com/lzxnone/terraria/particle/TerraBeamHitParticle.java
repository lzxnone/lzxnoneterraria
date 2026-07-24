package com.lzxnone.terraria.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;

public class TerraBeamHitParticle extends TextureSheetParticle {
    private final float rotSpeed;
    private final float baseSize;

    private final SpriteSet spriteSet;
    private final Float INCREASE = 0.5f;
    private final Float MUL = 5.0f;

    protected TerraBeamHitParticle(ClientLevel level, double x, double y, double z,
                           double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.hasPhysics = false;

        this.friction = 0.9F;
        this.gravity = 0.0F;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.baseSize = 0.2f + (this.random.nextFloat() * 0.2f);
        this.quadSize = baseSize;

        this.roll = this.random.nextFloat() * ((float)Math.PI * 2F);
        this.oRoll = this.roll;
        this.rotSpeed = 0;

        this.lifetime = 15;

        this.spriteSet = spriteSet;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();

        this.oRoll = this.roll;
        this.roll += this.rotSpeed;

        this.setSpriteFromAge(this.spriteSet);

        float lifeRatio = (float) this.age / (float) this.lifetime;

        if(lifeRatio <= INCREASE) {
            // [0, INCREASE] 从 0 线性增长到 baseSize * MUL
            this.quadSize = this.baseSize * MUL * (lifeRatio / INCREASE);
            this.alpha = 1.0F;
        }else {
            // [INCREASE, 1] 从 baseSize * MUL 线性缩小到 0
            this.quadSize = this.baseSize * MUL * (1.0f - lifeRatio) / (1.0f - INCREASE);
            this.alpha = 1.0f - (lifeRatio - 0.5f) / 0.5f;
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        if(this.alpha <= 0.01F) return;

        float lifeRatio = (float) this.age / (float) this.lifetime - 0.5f;

        for(int i = 0;i < 10 + (int) lifeRatio * 5;i++) {
            super.render(buffer, camera, partialTick);
        }
    }

    @Override
    public int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new TerraBeamHitParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
