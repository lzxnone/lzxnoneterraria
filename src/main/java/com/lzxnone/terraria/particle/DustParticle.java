package com.lzxnone.terraria.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Vector3f;

public class DustParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private final float rotSpeed;
    private final float baseSize;

    public float initSize;
    public float initRotSpeed;
    public int initLifetime;
    public boolean glow;

    public Vector3f[] colors = new Vector3f[]{ new Vector3f(1.0F, 1.0F, 1.0F) };

    protected DustParticle(ClientLevel level, double x, double y, double z,
                           double xSpeed, double ySpeed, double zSpeed,
                           DustParticleOptions options, SpriteSet spriteSet) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.hasPhysics = false;

        this.sprites = spriteSet;

        this.friction = 0.9F;
        this.gravity = 0.0F;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.initSize = options.initSize;
        this.initRotSpeed = options.initRotSpeed;
        this.initLifetime = options.initLifetime;
        this.glow = options.glow;

        this.baseSize = initSize + (this.random.nextFloat() * initSize);
        this.quadSize = baseSize;

        this.roll = this.random.nextFloat() * ((float)Math.PI * 2F);
        this.oRoll = this.roll;
        this.rotSpeed = (this.random.nextFloat() - 0.5F) * initRotSpeed;

        this.lifetime = initLifetime + this.random.nextInt(initLifetime / 2);

        Vector3f[] palette = options.colors != null ? options.colors : this.colors;
        int randomIndex = this.random.nextInt(palette.length);
        this.rCol = palette[randomIndex].x;
        this.gCol = palette[randomIndex].y;
        this.bCol = palette[randomIndex].z;

        this.setSpriteFromAge(spriteSet);
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
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        if(this.alpha <= 0.01F) return;

        float coloredR = this.rCol;
        float coloredG = this.gCol;
        float coloredB = this.bCol;

        this.setSprite(this.sprites.get(0, 1));
        this.rCol = coloredR;
        this.gCol = coloredG;
        this.bCol = coloredB;
        super.render(buffer, camera, partialTick);

        this.setSprite(this.sprites.get(1, 1));
        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;
        super.render(buffer, camera, partialTick);

        this.rCol = coloredR;
        this.gCol = coloredG;
        this.bCol = coloredB;
    }

    @Override
    public int getLightColor(float partialTick) {
        return glow ? LightTexture.FULL_BRIGHT : super.getLightColor(partialTick);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ModParticleRenderTypes.TRANSLUCENT_EMISSIVE;
    }

    public static class Provider implements ParticleProvider<DustParticleOptions> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(DustParticleOptions type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new DustParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, type, this.spriteSet);
        }
    }
}
