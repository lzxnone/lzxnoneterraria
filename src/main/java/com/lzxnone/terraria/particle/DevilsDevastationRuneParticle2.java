package com.lzxnone.terraria.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class DevilsDevastationRuneParticle2 extends EmissiveBloomParticle {
    private final float baseSize;

    protected DevilsDevastationRuneParticle2(ClientLevel level, double x, double y, double z,
                                             double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.hasPhysics = false;

        this.friction = 0.9F;
        this.gravity = 0.0F;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.rCol = 0.8f;
        this.gCol = 0.176f;
        this.bCol = 0.78f;

        this.baseSize = 0.3F + this.random.nextFloat() * 0.1F;
        this.quadSize = baseSize;

        this.roll = this.random.nextFloat() * ((float)Math.PI * 2F);
        this.oRoll = this.roll;

        this.lifetime = 20;

        this.pickSprite(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();

        float lifeRatio = (float) this.age / (float) this.lifetime;

        this.quadSize = this.baseSize * (1.0F - lifeRatio);
        this.alpha = 1.0F - lifeRatio;
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
            return new DevilsDevastationRuneParticle2(level, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}