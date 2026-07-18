package com.lzxnone.terraria.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EmissiveBloomParticle extends TextureSheetParticle {

    protected EmissiveBloomParticle(ClientLevel level, double x, double y, double z,
                                     double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        if (this.alpha <= 0.01F) return;

        float f = (float) (Mth.lerp(partialTick, this.xo, this.x) - camera.getPosition().x);
        float f1 = (float) (Mth.lerp(partialTick, this.yo, this.y) - camera.getPosition().y);
        float f2 = (float) (Mth.lerp(partialTick, this.zo, this.z) - camera.getPosition().z);

        // 相机朝向 billboard，叠加 roll 旋转
        Quaternionf quaternion = new Quaternionf(camera.rotation());
        quaternion.rotateZ(Mth.lerp(partialTick, this.oRoll, this.roll));

        float size = this.quadSize;

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();

        float[][] uvs = {
            {u1, v1},
            {u1, v0},
            {u0, v0},
            {u0, v1}
        };

        Vector3f[] corners = {
            new Vector3f(1.0F, -1.0F, 0.0F),
            new Vector3f(1.0F, 1.0F, 0.0F),
            new Vector3f(-1.0F, 1.0F, 0.0F),
            new Vector3f(-1.0F, -1.0F, 0.0F)
        };

        for (int i = 0; i < 4; i++) {
            Vector3f vertex = corners[i];
            vertex.rotate(quaternion);
            vertex.mul(size);
            vertex.add(f, f1, f2);

            buffer.addVertex(vertex.x(), vertex.y(), vertex.z())
                .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                .setUv(uvs[i][0], uvs[i][1])
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0.0F, 0.0F, 1.0F);
        }
    }

    @Override
    public int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ModParticleRenderTypes.EMISSIVE_BLOOM;
    }
}