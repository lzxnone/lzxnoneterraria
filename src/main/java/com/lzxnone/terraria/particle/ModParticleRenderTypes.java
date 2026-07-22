package com.lzxnone.terraria.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
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

    public static final ParticleRenderType EMISSIVE_BLOOM = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.depthMask(true);
            RenderSystem.disableCull();
            RenderSystem.enableDepthTest();
            RenderSystem.setShader(GameRenderer::getRendertypeEntityTranslucentEmissiveShader);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);
        }

        @Override
        public String toString() {
            return "lzxnoneterraria:emissive_bloom";
        }

        @Override
        public boolean isTranslucent() {
            return true;
        }
    };

    private ModParticleRenderTypes() {}
}
