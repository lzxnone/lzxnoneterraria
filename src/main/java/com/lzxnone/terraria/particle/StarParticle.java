package com.lzxnone.terraria.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import org.joml.Vector3f;

public class StarParticle extends TextureSheetParticle {
    private final float baseSize;

    private static final Vector3f[] COLOR_PALETTE = new Vector3f[]{
        new Vector3f(1.0F, 0.9F, 0.0F), // 0: 黄色 (Yellow)
        new Vector3f(0.5F, 0.8F, 1.0F), // 1: 淡蓝 (Light Blue)
        new Vector3f(1.0F, 0.0F, 1.0F), // 2: 品红 (Magenta)
        new Vector3f(0.7F, 0.7F, 0.7F)  // 3: 淡灰 (Light Gray)
    };

    protected StarParticle(ClientLevel level, double x, double y, double z,
                                         double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.hasPhysics = false;

        this.friction = 0.9F;
        this.gravity = 0.0F;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.baseSize = 0.025F + (this.random.nextFloat() * 0.1F);
        this.quadSize = baseSize;

        this.lifetime = 40 + this.random.nextInt(10);

        int randomIndex = this.random.nextInt(COLOR_PALETTE.length);
        Vector3f chosenColor = COLOR_PALETTE[randomIndex];
        this.rCol = chosenColor.x;
        this.gCol = chosenColor.y;
        this.bCol = chosenColor.z;

        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();

        float lifeRatio = (float) this.age / (float) this.lifetime;

        this.quadSize = this.baseSize * (1.0F - lifeRatio);
        this.alpha = 1.0F - lifeRatio;
    }

    @Override
    public int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
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
            return new StarParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
