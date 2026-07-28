package com.lzxnone.terraria.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.joml.Vector3f;

public class PartyParticle extends TextureSheetParticle {
    private final float rotSpeed;

    private final Vector3f[] COLORS = new Vector3f[]{
        new Vector3f(1.0f, 0.0f, 1.0f),
        new Vector3f(0.0f, 0.4f, 1.0f),
        new Vector3f(1.0f, 0.9f, 0.0f),
        new Vector3f(0.6f, 0.1f, 0.8f),
        new Vector3f(0.1f, 0.8f, 0.2f)
    };

    protected PartyParticle(ClientLevel level, double x, double y, double z,
                                double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.hasPhysics = true;

        this.friction = 0.9F;
        this.gravity = 0.5F;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.quadSize = 0.075f + (this.random.nextFloat() * 0.075f);

        this.roll = this.random.nextFloat() * ((float)Math.PI * 2F);
        this.oRoll = this.roll;
        this.rotSpeed = (this.random.nextFloat() - 0.5F) * 0.5f;

        this.lifetime = 30 + this.random.nextInt(15);

        Vector3f color = COLORS[this.random.nextInt(COLORS.length)];
        this.rCol = color.x;
        this.gCol = color.y;
        this.bCol = color.z;

        this.pickSprite(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();

        this.oRoll = this.roll;
        this.roll += this.rotSpeed;

        float lifeRatio = (float) this.age / (float) this.lifetime;

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
            return new PartyParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
