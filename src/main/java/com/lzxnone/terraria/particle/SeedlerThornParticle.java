package com.lzxnone.terraria.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class SeedlerThornParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;
    private final float baseSize;

    protected SeedlerThornParticle(ClientLevel level, double x, double y, double z,
                              double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.hasPhysics = false;

        this.friction = 0.9F;
        this.gravity = 0.0F;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.baseSize = 0.075F + (this.random.nextFloat() * 0.05F);
        this.quadSize = baseSize;

        this.lifetime = 25;

        this.spriteSet = spriteSet;
        this.setSpriteFromAge(this.spriteSet);
    }

    @Override
    public void tick() {
        super.tick();

        float lifeRatio = (float) this.age / (float) this.lifetime;

        this.quadSize = this.baseSize * (1.0F - lifeRatio);
        this.alpha = 1.0F - lifeRatio;

        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ModParticleRenderTypes.TRANSLUCENT_EMISSIVE;
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
            return new SeedlerThornParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
