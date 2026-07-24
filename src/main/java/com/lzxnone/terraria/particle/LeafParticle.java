package com.lzxnone.terraria.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class LeafParticle extends TextureSheetParticle {
    private final float rotSpeed;
    private final float baseSize;

    protected LeafParticle(ClientLevel level, double x, double y, double z,
                                     double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.hasPhysics = false;

        this.friction = 0.9F;
        this.gravity = 0.0F;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.baseSize = 0.05F + (this.random.nextFloat() * 0.05F);
        this.quadSize = baseSize;

        this.roll = this.random.nextFloat() * ((float)Math.PI * 2F);
        this.oRoll = this.roll;
        this.rotSpeed = (this.random.nextFloat() - 0.5F) * 0.5F;

        this.lifetime = 20 + this.random.nextInt(10);

        this.pickSprite(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();

        this.oRoll = this.roll;
        this.roll += this.rotSpeed;

        float lifeRatio = (float) this.age / (float) this.lifetime;

        this.quadSize = this.baseSize * (1.0F - lifeRatio);
        this.alpha = 1.0F - lifeRatio;
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
            return new LeafParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
