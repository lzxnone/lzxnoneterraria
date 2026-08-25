package com.lzxnone.terraria.client.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID, value = Dist.CLIENT)
public class ShaderRegistry {
    private static ShaderInstance blur;
    private static ShaderInstance composite;
    private static ShaderInstance outline;
    private static ShaderInstance outlineMask;
    private static ShaderInstance particleBloom;
    private static ShaderInstance ironSparkParticle;
    private static ShaderInstance circleParticle;
    private static ShaderInstance superellipseParticle;
    private static ShaderInstance nightsEdgeHitParticle;
    private static ShaderInstance devilsDevastationEnergy;

    public static ShaderInstance getBlur() {
        return blur;
    }

    public static ShaderInstance getComposite() {
        return composite;
    }

    public static ShaderInstance getOutline() {
        return outline;
    }

    public static ShaderInstance getOutlineMask() {
        return outlineMask;
    }

    public static ShaderInstance getParticleBloom() {
        return particleBloom;
    }

    public static ShaderInstance getIronSparkParticle() {
        return ironSparkParticle;
    }

    public static ShaderInstance getCircleParticle() {
        return circleParticle;
    }

    public static ShaderInstance getSuperellipseParticle() {
        return superellipseParticle;
    }

    public static ShaderInstance getNightsEdgeHitParticle() {
        return nightsEdgeHitParticle;
    }

    public static ShaderInstance getDevilsDevastationEnergy() {
        return devilsDevastationEnergy;
    }

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) {
        try {
            event.registerShader(
                new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "blur"),
                    DefaultVertexFormat.POSITION_TEX
                ),
                shader -> blur = shader
            );
            event.registerShader(
                new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "composite"),
                    DefaultVertexFormat.POSITION_TEX
                ),
                shader -> composite = shader
            );
            event.registerShader(
                new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "outline"),
                    DefaultVertexFormat.POSITION_TEX
                ),
                shader -> outline = shader
            );
            event.registerShader(
                new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "outline_mask"),
                    DefaultVertexFormat.POSITION_TEX
                ),
                shader -> outlineMask = shader
            );
            event.registerShader(
                new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "particle_bloom"),
                    DefaultVertexFormat.PARTICLE
                ),
                shader -> particleBloom = shader
            );
            event.registerShader(
                new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "iron_spark_particle"),
                    DefaultVertexFormat.PARTICLE
                ),
                shader -> ironSparkParticle = shader
            );
            event.registerShader(
                new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "circle_particle"),
                    DefaultVertexFormat.PARTICLE
                ),
                shader -> circleParticle = shader
            );
            event.registerShader(
                new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "superellipse_particle"),
                    DefaultVertexFormat.PARTICLE
                ),
                shader -> superellipseParticle = shader
            );
            event.registerShader(
                new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "nights_edge_hit_particle"),
                    DefaultVertexFormat.PARTICLE
                ),
                shader -> nightsEdgeHitParticle = shader
            );
            event.registerShader(
                new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "devils_devastation_energy"),
                    DefaultVertexFormat.POSITION_COLOR
                ),
                shader -> devilsDevastationEnergy = shader
            );
        } catch (IOException e) {
            throw new RuntimeException("Shader load error", e);
        }
    }
}
