package com.lzxnone.terraria.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.ParticleOptions;

public class CircleParticle extends TextureSheetParticle {
    private final float baseSize;

    protected CircleParticle(ClientLevel level, double x, double y, double z,
                             double xSpeed, double ySpeed, double zSpeed,
                             CircleParticleOptions options, SpriteSet spriteSet) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.hasPhysics = false;

        this.friction = 0.9F;
        this.gravity = 0.0F;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.rCol = options.color.x;
        this.gCol = options.color.y;
        this.bCol = options.color.z;

        this.baseSize = options.initSize + (this.random.nextFloat() * options.initSize * 0.5f);
        this.quadSize = baseSize;

        this.lifetime = options.initLifetime + this.random.nextInt(Math.max(1, options.initLifetime / 2));

        this.pickSprite(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();

        float lifeRatio = (float) this.age / (float) this.lifetime;

        this.quadSize = this.baseSize * (1.0F - lifeRatio);
        this.alpha = 1.0F - lifeRatio;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        for(int i = 0;i < 10;i++) super.render(buffer, camera, partialTick);
    }

    @Override
    public int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ModParticleRenderTypes.EMISSIVE_BLOOM_PARTICLE;
    }

    public static class Provider implements ParticleProvider<CircleParticleOptions> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(CircleParticleOptions type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new CircleParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, type, this.spriteSet);
        }
    }
}
