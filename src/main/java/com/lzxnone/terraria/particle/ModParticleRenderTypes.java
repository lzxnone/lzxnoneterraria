package com.lzxnone.terraria.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.lzxnone.terraria.client.event.ShaderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

public final class ModParticleRenderTypes {
    public static final ParticleRenderType IRON_SPARK_PARTICLE = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            RenderSystem.enableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.setShader(ShaderRegistry::getIronSparkParticle);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public String toString() {
            return "lzxnoneterraria:iron_spark_particle";
        }

        @Override
        public boolean isTranslucent() {
            return false;
        }
    };

    public static final ParticleRenderType CIRCLE_PARTICLE = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            RenderSystem.enableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.setShader(ShaderRegistry::getCircleParticle);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public String toString() {
            return "lzxnoneterraria:circle_particle";
        }

        @Override
        public boolean isTranslucent() {
            return false;
        }
    };

    public static final ParticleRenderType EMISSIVE_BLOOM_PARTICLE = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            RenderSystem.enableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.setShader(ShaderRegistry::getParticleBloom);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public String toString() {
            return "lzxnoneterraria:emissive_bloom_particle";
        }

        @Override
        public boolean isTranslucent() {
            return false;
        }
    };

    private ModParticleRenderTypes() {}
}
