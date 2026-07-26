package com.lzxnone.terraria.client.renderer;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.client.entity.beam.ClientSwordBeamRenderBehaviors;
import com.lzxnone.terraria.client.entity.projectile.ClientStaticProjectileRenderBehaviors;
import com.lzxnone.terraria.client.entity.summon.ClientStaticSummonRenderBehaviors;
import com.lzxnone.terraria.entity.IrisCompat;
import com.lzxnone.terraria.entity.beam.SwordBeam;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.client.event.ShaderRegistry;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.GlStateBackup;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.Objects;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID, value = Dist.CLIENT)
public class OutlineRenderer {
    private static final int GL_LINEAR = 9729;

    private static RenderTarget outlineTarget;
    private static int lastW = -1;
    private static int lastH = -1;

    @SubscribeEvent
    public static void onRenderStage(RenderLevelStageEvent event) {
        if(IrisCompat.isShaderPackInUse()) return;
        if(ShaderRegistry.getOutline() == null) return;

        ensureTarget();
        if(outlineTarget == null) return;

        if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            onAfterEntities(event);
        }else if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            onAfterLevel();
        }
    }

    private static void ensureTarget() {
        RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
        int w = Math.max(1, main.viewWidth);
        int h = Math.max(1, main.viewHeight);

        if(w == lastW && h == lastH && outlineTarget != null) return;

        if(outlineTarget != null) outlineTarget.destroyBuffers();
        outlineTarget = new TextureTarget(w, h, true, false);
        outlineTarget.setClearColor(0f, 0f, 0f, 0f);
        outlineTarget.setFilterMode(GL_LINEAR);
        lastW = w;
        lastH = h;
    }

    private static void onAfterEntities(RenderLevelStageEvent event) {
        GlStateBackup backup = new GlStateBackup();
        RenderSystem.backupGlState(backup);
        ShaderInstance prevShader = RenderSystem.getShader();
        int prevTex0 = RenderSystem.getShaderTexture(0);

        try {
            RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
            outlineTarget.clear(false);
            outlineTarget.bindWrite(true);
            outlineTarget.copyDepthFrom(main);
            outlineTarget.bindWrite(true);

            renderOutlineEntities(
                event.getPoseStack(),
                event.getCamera(),
                event.getPartialTick().getGameTimeDeltaPartialTick(true)
            );
        }finally {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
            RenderSystem.setShaderTexture(0, prevTex0);
            if(prevShader != null) {
                RenderSystem.setShader(() -> prevShader);
            }
            RenderSystem.restoreGlState(backup);
        }
    }

    private static void onAfterLevel() {
        GlStateBackup backup = new GlStateBackup();
        RenderSystem.backupGlState(backup);
        ShaderInstance prevShader = RenderSystem.getShader();
        int prevTex0 = RenderSystem.getShaderTexture(0);

        try {
            RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
            main.bindWrite(true);

            ShaderInstance shader = ShaderRegistry.getOutline();
            RenderSystem.setShader(() -> shader);
            RenderSystem.setShaderTexture(0, outlineTarget.getColorTextureId());

            Uniform texelSize = shader.getUniform("TexelSize");
            if(texelSize != null) {
                texelSize.set(1.0f / outlineTarget.viewWidth, 1.0f / outlineTarget.viewHeight);
            }
            Uniform outlineColor = shader.getUniform("OutlineColor");
            if(outlineColor != null) {
                outlineColor.set(0.8f, 0.176f, 0.78f, 1.0f);
            }
            Uniform radius = shader.getUniform("Radius");
            if(radius != null) {
                radius.set(2.0f);
            }

            RenderSystem.enableBlend();
            RenderSystem.blendFunc(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
            );
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            drawFullscreen();
        }finally {
            RenderSystem.setShaderTexture(0, prevTex0);
            if(prevShader != null) {
                RenderSystem.setShader(() -> prevShader);
            }
            RenderSystem.restoreGlState(backup);
        }
    }

    private static void renderOutlineEntities(PoseStack poseStack, Camera camera, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        if(mc.level == null) return;

        try(ByteBufferBuilder byteBuf = new ByteBufferBuilder(65536)) {
            MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(byteBuf);
            for(Entity entity : mc.level.entitiesForRendering()) {
                double x = Mth.lerp(partialTick, entity.xo, entity.getX());
                double y = Mth.lerp(partialTick, entity.yo, entity.getY());
                double z = Mth.lerp(partialTick, entity.zo, entity.getZ());
                float entityYaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());

                double camX = x - camera.getPosition().x();
                double camY = y - camera.getPosition().y();
                double camZ = z - camera.getPosition().z();

                if(entity instanceof StaticProjectile projectile
                        && projectile.getEntityData().get(StaticProjectile.OUTLINE)) {
                    poseStack.pushPose();
                    poseStack.translate(camX, camY, camZ);
                    ClientStaticProjectileRenderBehaviors.getBehavior(projectile.getEntityData().get(StaticProjectile.BEHAVIOR))
                        .render(projectile, entityYaw, partialTick, poseStack, bufferSource, LightTexture.FULL_BRIGHT);
                    poseStack.popPose();
                }else if(entity instanceof StaticSummon summon
                        && summon.getEntityData().get(StaticSummon.OUTLINE)) {
                    poseStack.pushPose();
                    poseStack.translate(camX, camY, camZ);
                    ClientStaticSummonRenderBehaviors.getBehavior(summon.getEntityData().get(StaticSummon.BEHAVIOR))
                        .render(summon, entityYaw, partialTick, poseStack, bufferSource, LightTexture.FULL_BRIGHT);
                    poseStack.popPose();
                }else if(entity instanceof SwordBeam beam
                        && beam.getEntityData().get(SwordBeam.OUTLINE)) {
                    poseStack.pushPose();
                    poseStack.translate(camX, camY, camZ);
                    ClientSwordBeamRenderBehaviors.getBehavior(beam.getEntityData().get(SwordBeam.BEHAVIOR))
                        .render(beam, entityYaw, partialTick, poseStack, bufferSource, LightTexture.FULL_BRIGHT);
                    poseStack.popPose();
                }
            }
            bufferSource.endBatch();
        }
    }

    private static void drawFullscreen() {
        BufferBuilder bb = RenderSystem.renderThreadTesselator().begin(
            VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bb.addVertex(-1f, -1f, 0f).setUv(0f, 0f);
        bb.addVertex(1f, -1f, 0f).setUv(1f, 0f);
        bb.addVertex(1f, 1f, 0f).setUv(1f, 1f);
        bb.addVertex(-1f, 1f, 0f).setUv(0f, 1f);
        BufferUploader.drawWithShader(Objects.requireNonNull(bb.build()));
    }
}
