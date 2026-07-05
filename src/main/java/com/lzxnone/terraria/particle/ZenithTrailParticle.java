package com.lzxnone.terraria.particle;

import com.lzxnone.terraria.utils.MathUtil;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;


public class ZenithTrailParticle extends TextureSheetParticle {
    private final float baseSize;

    public float initSize;
    public int initLifetime;
    public boolean glow;

    public Vec3 dir;
    public Vec3 up;
    public Vec3 right;
    public int angle;

    private static final float MAX_DIST = 0.05f;
    private static final int COUNT = 30;

    protected ZenithTrailParticle(ClientLevel level, double x, double y, double z,
                                  double xSpeed, double ySpeed, double zSpeed,
                                  ZenithTrailParticleOptions options, SpriteSet spriteSet) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.hasPhysics = false;

        this.friction = 0.9F;
        this.gravity = 0.0F;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.initSize = options.initSize;
        this.initLifetime = options.initLifetime;
        this.glow = options.glow;
        this.dir = new Vec3(xSpeed, ySpeed, zSpeed).normalize();
        this.up = new Vec3(options.up).normalize();
        this.right = new Vec3(options.right).normalize();

        this.rCol = options.color.x;
        this.gCol = options.color.y;
        this.bCol = options.color.z;

        this.baseSize = initSize + (this.random.nextFloat() * initSize);
        this.quadSize = baseSize;

        this.roll = this.random.nextFloat() * ((float)Math.PI * 2F);
        this.oRoll = this.roll;

        this.lifetime = initLifetime + this.random.nextInt(initLifetime / 2);

        this.pickSprite(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();

        this.oRoll = this.roll;

        float lifeRatio = (float) this.age / (float) this.lifetime;

        this.quadSize = this.baseSize * (1.0F - lifeRatio);
        this.alpha = 1.0F - lifeRatio;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        if(this.alpha <= 0.01F) return;
        float lifeRatio = (this.age + partialTick) / (float) this.lifetime;

        Vec3 headPos = new Vec3(
            (Mth.lerp(partialTick, this.xo, this.x) - camera.getPosition().x),
            (Mth.lerp(partialTick, this.yo, this.y) - camera.getPosition().y),
            (Mth.lerp(partialTick, this.zo, this.z) - camera.getPosition().z)
        );

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();

        float[][] uvs = new float[][]{
            new float[]{u1, v1},
            new float[]{u1, v0},
            new float[]{u0, v0},
            new float[]{u0, v1}
        };

        float[] xyRot = MathUtil.computeXYRot(dir.toVector3f(), up.toVector3f());
        Quaternionf rotation = new Quaternionf()
            .fromAxisAngleRad(dir.toVector3f(), (float) Math.toRadians(this.angle))
            .rotateY((float) Math.toRadians(-xyRot[1]))
            .rotateX((float) Math.toRadians(-90 + xyRot[0])
        );

        Vector3f[] baseVertices = new Vector3f[]{
            new Vector3f(1.0F, -1.0F, 0.0F).rotate(rotation),
            new Vector3f(1.0F,  1.0F, 0.0F).rotate(rotation),
            new Vector3f( -1.0F,  1.0F, 0.0F).rotate(rotation),
            new Vector3f( -1.0F, -1.0F, 0.0F).rotate(rotation)
        };


        float dist = (1.0f - lifeRatio) * MAX_DIST;

        for(int i = 0; i < COUNT; i++) {
            float radio = 1.0F - ((float) i / (COUNT - 1));;
            float currentQuadSize = this.quadSize * radio;
            float currentAlpha = this.alpha * radio;

            if(currentQuadSize < 0.001f || alpha < 0.001f) continue;

            Vec3 currentPos = headPos.add(dir.scale(-i * dist));

            for(int j = 0; j < 4; j++) {
                Vector3f vertex = new Vector3f(baseVertices[j]);
                vertex.mul(currentQuadSize).add((float) currentPos.x, (float) currentPos.y, (float) currentPos.z);

                buffer.addVertex(vertex.x(), vertex.y(), vertex.z())
                    .setUv(uvs[j][0], uvs[j][1])
                    .setColor(this.rCol, this.gCol, this.bCol, currentAlpha)
                    .setLight(LightTexture.FULL_BRIGHT);
            }
        }
    }

    @Override
    public int getLightColor(float partialTick) {
        return glow ? LightTexture.FULL_BRIGHT : super.getLightColor(partialTick);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ModParticleRenderTypes.TRANSLUCENT_EMISSIVE;
    }

    public static class Provider implements ParticleProvider<ZenithTrailParticleOptions> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(ZenithTrailParticleOptions type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new ZenithTrailParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, type, this.spriteSet);
        }
    }
}
