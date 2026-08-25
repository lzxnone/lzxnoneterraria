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

public class TerraBeamHitParticle extends TextureSheetParticle {
    private final float baseSize;
    private static final float QUAD_SIZE_MULTIPLIER = 2.5f;
    private static final float OFFSET_X_MULTIPLIER = 1.35f;
    private static final float OFFSET_Y_MULTIPLIER = 1.05f;
    private static final float QUAD_OPACITY = 0.55f;
    private static final float GROW_END = 0.15f;
    private static final float SHRINK_START = 0.22f;
    private static final float FADE_START = 0.35f;

    protected TerraBeamHitParticle(ClientLevel level, double x, double y, double z,
                           double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.hasPhysics = false;

        this.friction = 0.9F;
        this.gravity = 0.0F;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.baseSize = 0.18f;
        this.quadSize = baseSize;

        this.roll = 0;
        this.oRoll = this.roll;

        this.lifetime = 15;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        float lifeRatio = Mth.clamp((this.age + partialTick) / (float) this.lifetime, 0.0f, 1.0f);
        float growProgress = Mth.clamp(lifeRatio / GROW_END, 0.0f, 1.0f);
        float grow = 1.0f - (float) Math.pow(1.0f - growProgress, 3.0);
        float shrink = 1.0f - (float) Mth.smoothstep(
            Mth.clamp((lifeRatio - SHRINK_START) / (1.0f - SHRINK_START), 0.0f, 1.0f)
        );
        float size = this.baseSize * QUAD_SIZE_MULTIPLIER * grow * shrink;
        float fade = 1.0f - (float) Mth.smoothstep(
            Mth.clamp((lifeRatio - FADE_START) / (1.0f - FADE_START), 0.0f, 1.0f)
        );
        if(size <= 0.001f || fade <= 0.001f) return;

        float cx = (float) (Mth.lerp(partialTick, this.xo, this.x) - camera.getPosition().x);
        float cy = (float) (Mth.lerp(partialTick, this.yo, this.y) - camera.getPosition().y);
        float cz = (float) (Mth.lerp(partialTick, this.zo, this.z) - camera.getPosition().z);

        Quaternionf rotation = new Quaternionf(camera.rotation());
        rotation.rotateZ(Mth.lerp(partialTick, this.oRoll, this.roll));

        // 五个 quad 的中心位置固定，生命周期缩放只影响各自的半尺寸。
        // 这样每个超椭圆都会围绕自己的中心放大、缩小，而不是整组一起缩向原点。
        float offsetX = this.baseSize * OFFSET_X_MULTIPLIER;
        float offsetY = this.baseSize * OFFSET_Y_MULTIPLIER;
        float[][] localCenters = {
            {0.0f, 0.0f},
            {-offsetX, 0.0f},
            {offsetX, 0.0f},
            {0.0f, -offsetY},
            {0.0f, offsetY}
        };

        // 与 SingleQuadParticle 保持相同的正面绕序：右下 -> 右上 -> 左上 -> 左下。
        // 自定义 ParticleRenderType 不会关闭背面剔除，反向绕序会让整张 billboard 被裁掉。
        Vector3f[] corners = {
            new Vector3f(1.0f, -1.0f, 0.0f),
            new Vector3f(1.0f, 1.0f, 0.0f),
            new Vector3f(-1.0f, 1.0f, 0.0f),
            new Vector3f(-1.0f, -1.0f, 0.0f)
        };
        float[][] uvs = {
            {1.0f, 1.0f},
            {1.0f, 0.0f},
            {0.0f, 0.0f},
            {0.0f, 1.0f}
        };
        for(float[] localCenter : localCenters) {
            Vector3f quadCenter = new Vector3f(localCenter[0], localCenter[1], 0.0f)
                .rotate(rotation)
                .add(cx, cy, cz);
            for(int i = 0; i < corners.length; i++) {
                Vector3f corner = new Vector3f(corners[i])
                    .rotate(rotation)
                    .mul(size)
                    .add(quadCenter);
                buffer.addVertex(corner.x, corner.y, corner.z)
                    .setUv(uvs[i][0], uvs[i][1])
                    .setColor(1.0f, 1.0f, 1.0f, fade * QUAD_OPACITY)
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
        return ModParticleRenderTypes.TERRA_BEAM_HIT_PARTICLE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new TerraBeamHitParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}
