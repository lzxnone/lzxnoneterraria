package com.lzxnone.terraria.event;

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

    public static ShaderInstance getBlur() {
        return blur;
    }

    public static ShaderInstance getComposite() {
        return composite;
    }

    public static ShaderInstance getOutline() {
        return outline;
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
        } catch (IOException e) {
            throw new RuntimeException("Shader load error", e);
        }
    }
}
