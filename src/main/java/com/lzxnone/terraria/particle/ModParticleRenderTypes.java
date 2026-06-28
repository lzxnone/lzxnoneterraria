package com.lzxnone.terraria.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureManager;

public final class ModParticleRenderTypes {

    public static final ParticleRenderType TRANSLUCENT_EMISSIVE = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
            return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT.begin(tesselator, textureManager);
        }

        @Override
        public String toString() {
            return "lzxnoneterraria:translucent_emissive";
        }

        @Override
        public boolean isTranslucent() {
            return true;
        }
    };

    private ModParticleRenderTypes() {}
}
