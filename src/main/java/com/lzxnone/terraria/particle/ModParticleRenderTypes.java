package com.lzxnone.terraria.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.lzxnone.terraria.client.event.ShaderRegistry;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import org.joml.Vector3f;

public final class ModParticleRenderTypes {
    private static final float QUARTER_TURN = (float) Math.PI / 4.0f;

    private static final SuperellipseStyle STARLIGHT_STYLE = new SuperellipseStyle(
        0.25f, 0.90f, 0.90f, 0.0f,
        new Vector3f(1.0f), new Vector3f(1.0f), new Vector3f(0.38f), 0.68f,
        0.08f, 0.68f, 0.66f, 1.0f,
        0.10f, 1.0f,
        2.15f, 1.18f, 1.14f, 0.48f, 0.34f
    );
    private static final SuperellipseStyle TERRA_STYLE = new SuperellipseStyle(
        0.80f, 0.82f, 0.62f, 0.0f,
        new Vector3f(1.00f, 1.00f, 0.96f),
        new Vector3f(0.30f, 1.00f, 0.50f),
        new Vector3f(0.01f, 0.82f, 0.20f), 0.0f,
        0.16f, 0.58f, 0.56f, 0.96f,
        0.42f, 0.98f,
        2.45f, 1.30f, 1.14f, 0.38f, 0.34f
    );
    private static final SuperellipseStyle TRUE_NIGHTS_EDGE_STYLE = new SuperellipseStyle(
        1.0f / 3.0f, 0.64f, 0.64f, QUARTER_TURN,
        new Vector3f(0.46f, 0.96f, 0.25f),
        new Vector3f(0.08f, 0.62f, 0.13f),
        new Vector3f(0.01f, 0.25f, 0.045f), 0.0f,
        0.16f, 0.68f, 0.66f, 1.0f,
        0.18f, 1.0f,
        1.55f, 1.12f, 1.14f, 0.48f, 0.34f
    );
    private static final SuperellipseStyle TRUE_EXCALIBUR_STYLE = new SuperellipseStyle(
        0.50f, 0.58f, 0.58f, QUARTER_TURN,
        new Vector3f(1.00f, 0.56f, 0.79f),
        new Vector3f(0.96f, 0.11f, 0.52f),
        new Vector3f(0.40f, 0.018f, 0.19f), 0.0f,
        0.15f, 0.67f, 0.65f, 1.0f,
        0.16f, 1.0f,
        1.48f, 1.12f, 1.14f, 0.50f, 0.35f
    );
    private static final SuperellipseStyle EXCALIBUR_STYLE = new SuperellipseStyle(
        0.80f, 0.43f, 0.82f, 0.0f,
        new Vector3f(1.00f, 0.94f, 0.36f),
        new Vector3f(1.00f, 0.62f, 0.025f),
        new Vector3f(0.50f, 0.20f, 0.005f), 0.0f,
        0.15f, 0.68f, 0.66f, 1.0f,
        0.16f, 1.0f,
        1.52f, 1.12f, 1.14f, 0.50f, 0.35f
    );

    public static final ParticleRenderType STARLIGHT_PARTICLE = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            return beginSuperellipse(tesselator, STARLIGHT_STYLE);
        }

        @Override
        public String toString() {
            return "lzxnoneterraria:starlight_particle";
        }

        @Override
        public boolean isTranslucent() {
            return false;
        }
    };

    public static final ParticleRenderType TERRA_BEAM_HIT_PARTICLE = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            return beginSuperellipse(tesselator, TERRA_STYLE);
        }

        @Override
        public String toString() {
            return "lzxnoneterraria:terra_beam_hit_particle";
        }

        @Override
        public boolean isTranslucent() {
            return false;
        }
    };

    public static final ParticleRenderType TRUE_NIGHTS_EDGE_HIT_PARTICLE = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            return beginSuperellipse(tesselator, TRUE_NIGHTS_EDGE_STYLE);
        }

        @Override
        public String toString() {
            return "lzxnoneterraria:true_nights_edge_hit_particle";
        }

        @Override
        public boolean isTranslucent() {
            return false;
        }
    };

    public static final ParticleRenderType TRUE_EXCALIBUR_HIT_PARTICLE = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            return beginSuperellipse(tesselator, TRUE_EXCALIBUR_STYLE);
        }

        @Override
        public String toString() {
            return "lzxnoneterraria:true_excalibur_hit_particle";
        }

        @Override
        public boolean isTranslucent() {
            return false;
        }
    };

    public static final ParticleRenderType EXCALIBUR_HIT_PARTICLE = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            return beginSuperellipse(tesselator, EXCALIBUR_STYLE);
        }

        @Override
        public String toString() {
            return "lzxnoneterraria:excalibur_hit_particle";
        }

        @Override
        public boolean isTranslucent() {
            return false;
        }
    };

    public static final ParticleRenderType NIGHTS_EDGE_HIT_PARTICLE = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            RenderSystem.enableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.setShader(ShaderRegistry::getNightsEdgeHitParticle);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public String toString() {
            return "lzxnoneterraria:nights_edge_hit_particle";
        }

        @Override
        public boolean isTranslucent() {
            return false;
        }
    };

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

    private static BufferBuilder beginSuperellipse(Tesselator tesselator, SuperellipseStyle style) {
        RenderSystem.enableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        ShaderInstance shader = ShaderRegistry.getSuperellipseParticle();
        RenderSystem.setShader(() -> shader);
        set(shader, "ShapePower", style.power);
        set(shader, "ShapeRadius", style.radiusX, style.radiusY);
        set(shader, "ShapeRotation", style.rotation);
        set(shader, "CoreColor", style.coreColor);
        set(shader, "MiddleColor", style.middleColor);
        set(shader, "EdgeColor", style.edgeColor);
        set(shader, "CoreWhiteMix", style.coreWhiteMix);
        set(shader, "FirstGradient", style.firstGradientStart, style.firstGradientEnd);
        set(shader, "SecondGradient", style.secondGradientStart, style.secondGradientEnd);
        set(shader, "BrightnessGradient", style.brightnessGradientStart, style.brightnessGradientEnd);
        set(shader, "Brightness", style.coreBrightness, style.edgeBrightness);
        set(shader, "AuraScale", style.auraScale);
        set(shader, "AuraBrightness", style.auraBrightness);
        set(shader, "AuraAlpha", style.auraAlpha);
        return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
    }

    private static void set(ShaderInstance shader, String name, float value) {
        Uniform uniform = shader.getUniform(name);
        if(uniform != null) uniform.set(value);
    }

    private static void set(ShaderInstance shader, String name, float x, float y) {
        Uniform uniform = shader.getUniform(name);
        if(uniform != null) uniform.set(x, y);
    }

    private static void set(ShaderInstance shader, String name, Vector3f value) {
        Uniform uniform = shader.getUniform(name);
        if(uniform != null) uniform.set(value.x, value.y, value.z);
    }

    private record SuperellipseStyle(
        float power,
        float radiusX,
        float radiusY,
        float rotation,
        Vector3f coreColor,
        Vector3f middleColor,
        Vector3f edgeColor,
        float coreWhiteMix,
        float firstGradientStart,
        float firstGradientEnd,
        float secondGradientStart,
        float secondGradientEnd,
        float brightnessGradientStart,
        float brightnessGradientEnd,
        float coreBrightness,
        float edgeBrightness,
        float auraScale,
        float auraBrightness,
        float auraAlpha
    ) {}

    private ModParticleRenderTypes() {}
}
