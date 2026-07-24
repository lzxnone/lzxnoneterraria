package com.lzxnone.terraria.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;

public class NightsEdgeHitParticle extends TextureSheetParticle {
    private final float baseSize;
    private final SpriteSet spriteSet;

    protected NightsEdgeHitParticle(ClientLevel level, double x, double y, double z,
                                            double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.hasPhysics = false;

        this.friction = 0.9F;
        this.gravity = 0.0F;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.baseSize = 0.8F;
        this.quadSize = baseSize;

        this.alpha = 1.0f;

        this.lifetime = 10;

        this.spriteSet = spriteSet;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();

        float lifeRatio = (float) this.age / (float) this.lifetime;
        if(lifeRatio <= 0.25f) {
            this.quadSize = (lifeRatio / 0.25f) * this.baseSize;
        }else if(lifeRatio >= 0.75f) {
            this.quadSize = (1.0f - (lifeRatio - 0.75f) / 0.25f) * this.baseSize;
        }else {
            this.quadSize = this.baseSize;
        }

        this.setSpriteFromAge(this.spriteSet);
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
            return new NightsEdgeHitParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
