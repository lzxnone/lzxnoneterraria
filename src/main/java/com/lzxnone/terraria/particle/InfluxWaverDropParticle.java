package com.lzxnone.terraria.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class InfluxWaverDropParticle extends TextureSheetParticle {
    private final float rotSpeed;
    private final float baseSize;
    private final double dropSpeed;
    private final int canDrop;

    private static final double DROP_RADIO = 0.25;
    private static final double ACCELERATION = -0.5;

    private static final double MAX_DIST = 0.1;
    private static final int COUNT = 15;

    protected InfluxWaverDropParticle(ClientLevel level, double x, double y, double z,
                           double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.hasPhysics = false;

        this.friction = 0.0F;
        this.gravity = 0.0F;
        this.dropSpeed = ySpeed;

        if(Math.abs(dropSpeed) < 0.001) this.canDrop = 1;
        else this.canDrop = this.random.nextInt(5);

        this.baseSize = 0.05f + (this.random.nextFloat() * 0.05f);
        this.quadSize = baseSize;

        this.roll = this.random.nextFloat() * ((float)Math.PI * 2F);
        this.oRoll = this.roll;
        this.rotSpeed = (this.random.nextFloat() - 0.5F) * 0.025f;

        this.lifetime = 40 + this.random.nextInt(40 / 2);
        this.setSprite(spriteSet.get(this.random));
    }

    @Override
    public void tick() {
        super.tick();

        this.oRoll = this.roll;
        this.roll += this.rotSpeed;

        float lifeRatio = (float) this.age / (float) this.lifetime;

        if(this.canDrop == 0 && lifeRatio > DROP_RADIO) {
            this.yd = dropSpeed + (lifeRatio - DROP_RADIO) / (1.0 - DROP_RADIO) * ACCELERATION;
        }

        this.quadSize = this.baseSize * (1.0F - lifeRatio);
        this.alpha = 1.0F - lifeRatio;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        if (this.alpha <= 0.01F) return;

        float lifeRatio = (this.age + partialTick) / (float) this.lifetime;
        double progress = (lifeRatio - DROP_RADIO) / (1.0 - DROP_RADIO);

        if(progress <= 0 || canDrop != 0) {
            super.render(buffer, camera, partialTick);
            return;
        }

        float dist = (float) (progress * MAX_DIST);

        Quaternionf quaternion = new Quaternionf();
        this.getFacingCameraMode().setRotation(quaternion, camera, partialTick);
        if(this.roll != 0.0F) {
            quaternion.rotateZ(Mth.lerp(partialTick, this.oRoll, this.roll));
        }

        float baseX = (float) (Mth.lerp(partialTick, this.xo, this.x) - camera.getPosition().x);
        float baseY = (float) (Mth.lerp(partialTick, this.yo, this.y) - camera.getPosition().y);
        float baseZ = (float) (Mth.lerp(partialTick, this.zo, this.z) - camera.getPosition().z);

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();

        Vector3f[] baseVertices = new Vector3f[]{
            new Vector3f(1.0F, -1.0F, 0.0F),
            new Vector3f(1.0F,  1.0F, 0.0F),
            new Vector3f( -1.0F,  1.0F, 0.0F),
            new Vector3f( -1.0F, -1.0F, 0.0F)
        };

        float[][] uvs = new float[][]{
            new float[]{u1, v1},
            new float[]{u1, v0},
            new float[]{u0, v0},
            new float[]{u0, v1}
        };

        for(int i = 0; i < COUNT; i++) {
            float sizeRatio = 1.0F - ((float) i / (COUNT - 1));
            float currentQuadSize = this.quadSize * sizeRatio;

            if(currentQuadSize <= 0.001F) continue;

            float offsetY = i * dist;

            for(int j = 0; j < 4; j++) {
                Vector3f vertex = new Vector3f(baseVertices[j]);
                vertex.rotate(quaternion).mul(currentQuadSize).add(baseX, baseY + offsetY, baseZ);

                buffer.addVertex(vertex.x(), vertex.y(), vertex.z())
                    .setUv(uvs[j][0], uvs[j][1])
                    .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                    .setLight(LightTexture.FULL_BRIGHT);
            }
        }
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
            return new InfluxWaverDropParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
