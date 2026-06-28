package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import org.joml.Matrix4f;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID, value = Dist.CLIENT)
public class EffectIconRenderer {
    private static final ResourceLocation CONFUSED_ICON =
            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/mob_effect_display/confused.png");

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Post<?, ?> event) {
        LivingEntity entity = event.getEntity();
        if(entity.hasEffect(ModEffects.CONFUSED)) {
            renderIcon(event, CONFUSED_ICON);
        }
    }

    public static void renderIcon(RenderLivingEvent.Post<?, ?> event, ResourceLocation icon) {
        LivingEntity entity = event.getEntity();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource buffer = event.getMultiBufferSource();

        poseStack.pushPose();

        float heightOffset = entity.getBbHeight() + 0.8F;
        poseStack.translate(0.0F, heightOffset, 0.0F);

        float partialTicks = event.getPartialTick();
        float headYaw = net.minecraft.util.Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);

        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-headYaw));

        float scale = 0.5F;
        poseStack.scale(scale, -scale, scale);

        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutoutNoCull(icon));

        // 光照
        int light = event.getPackedLight();

        builder.addVertex(matrix, -0.5F, -0.5F, 0).setColor(255, 255, 255, 255)
                .setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0, 0, -1);
        builder.addVertex(matrix, -0.5F, 0.5F, 0).setColor(255, 255, 255, 255)
                .setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0, 0, -1);
        builder.addVertex(matrix, 0.5F, 0.5F, 0).setColor(255, 255, 255, 255)
                .setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0, 0, -1);
        builder.addVertex(matrix, 0.5F, -0.5F, 0).setColor(255, 255, 255, 255)
                .setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0, 0, -1);

        poseStack.popPose();
    }
}
