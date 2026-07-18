package com.lzxnone.terraria.renderer;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.entity.IrisCompat;
import com.lzxnone.terraria.entity.beam.SwordBeam;
import com.lzxnone.terraria.entity.beam.SwordBeamBehaviors;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.event.ShaderRegistry;
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
public class BloomRenderer {
    private static final int GL_LINEAR = 9729;
    private static final int GLOW_DIV = 2;
    private static final int BLOOM_DIV = 16;

    private static RenderTarget glowTarget;
    private static RenderTarget bloomTarget;
    private static RenderTarget pingTarget;
    private static RenderTarget pongTarget;
    private static int lastGlowW = -1;
    private static int lastGlowH = -1;
    private static int lastBloomW = -1;
    private static int lastBloomH = -1;

    @SubscribeEvent
    public static void onRenderStage(RenderLevelStageEvent event) {
        //开启光影禁用
        if(IrisCompat.isShaderPackInUse()) return;
        if(ShaderRegistry.getBlur() == null || ShaderRegistry.getComposite() == null) return;

        ensureTargets();
        if(glowTarget == null || bloomTarget == null) return;

        if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            onAfterEntities(event);
        }else if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            onAfterLevel();
        }
    }

    private static void ensureTargets() {
        RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
        int gw = Math.max(1, main.viewWidth / GLOW_DIV);
        int gh = Math.max(1, main.viewHeight / GLOW_DIV);
        int bw = Math.max(1, main.viewWidth / BLOOM_DIV);
        int bh = Math.max(1, main.viewHeight / BLOOM_DIV);

        if(gw != lastGlowW || gh != lastGlowH || glowTarget == null) {
            if(glowTarget != null) glowTarget.destroyBuffers();
            glowTarget = new TextureTarget(gw, gh, true, false);
            glowTarget.setClearColor(0f, 0f, 0f, 0f);
            glowTarget.setFilterMode(GL_LINEAR);
            lastGlowW = gw;
            lastGlowH = gh;
        }
        if(bw != lastBloomW || bh != lastBloomH || bloomTarget == null) {
            if(bloomTarget != null) bloomTarget.destroyBuffers();
            if(pingTarget != null) pingTarget.destroyBuffers();
            if(pongTarget != null) pongTarget.destroyBuffers();
            bloomTarget = new TextureTarget(bw, bh, false, false);
            pingTarget = new TextureTarget(bw, bh, false, false);
            pongTarget = new TextureTarget(bw, bh, false, false);
            for(RenderTarget t : new RenderTarget[]{bloomTarget, pingTarget, pongTarget}) {
                t.setClearColor(0f, 0f, 0f, 0f);
                t.setFilterMode(GL_LINEAR);
            }
            lastBloomW = bw;
            lastBloomH = bh;
        }
    }

    private static void onAfterEntities(RenderLevelStageEvent event) {
        GlStateBackup backup = new GlStateBackup();
        RenderSystem.backupGlState(backup);
        ShaderInstance prevShader = RenderSystem.getShader();
        int prevTex0 = RenderSystem.getShaderTexture(0);

        try{
            RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
            glowTarget.clear(false);
            glowTarget.bindWrite(true);
            glowTarget.copyDepthFrom(main);
            glowTarget.bindWrite(true);

            renderGlowEntities(
                event.getPoseStack(),
                event.getCamera(),
                event.getPartialTick().getGameTimeDeltaPartialTick(true)
            );

            downsamplePass(glowTarget, bloomTarget);

            blurPass(bloomTarget, pingTarget, true);
            blurPass(pingTarget, pongTarget, false);
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

        try{
            RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
            main.bindWrite(true);

            ShaderInstance shader = ShaderRegistry.getComposite();
            RenderSystem.setShader(() -> shader);
            RenderSystem.setShaderTexture(0, pongTarget.getColorTextureId());
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
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

    private static void downsamplePass(RenderTarget src, RenderTarget dst) {
        dst.bindWrite(true);
        ShaderInstance shader = ShaderRegistry.getComposite();
        RenderSystem.setShader(() -> shader);
        RenderSystem.setShaderTexture(0, src.getColorTextureId());
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        drawFullscreen();
    }

    private static void blurPass(RenderTarget src, RenderTarget dst, boolean horizontal) {
        dst.bindWrite(true);

        ShaderInstance shader = ShaderRegistry.getBlur();
        RenderSystem.setShader(() -> shader);
        RenderSystem.setShaderTexture(0, src.getColorTextureId());

        Uniform dir = shader.getUniform("Direction");
        if(dir != null) {
            float dx = horizontal ? (1f / src.viewWidth) : 0f;
            float dy = horizontal ? 0f : (1f / src.viewHeight);
            dir.set(dx, dy);
        }

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableBlend();
        drawFullscreen();
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

    public static void renderGlowEntities(PoseStack poseStack, Camera camera, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        if(mc.level == null) {
            return;
        }
        try(ByteBufferBuilder byteBuf = new ByteBufferBuilder(65536)) {
            MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(byteBuf);
            for (Entity entity : mc.level.entitiesForRendering()) {
                double x = Mth.lerp(partialTick, entity.xo, entity.getX());
                double y = Mth.lerp(partialTick, entity.yo, entity.getY());
                double z = Mth.lerp(partialTick, entity.zo, entity.getZ());
                float entityYaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());

                double camX = x - camera.getPosition().x();
                double camY = y - camera.getPosition().y();
                double camZ = z - camera.getPosition().z();

                if(entity instanceof StaticProjectile projectile
                        && projectile.getEntityData().get(StaticProjectile.GLOW)) {
                    poseStack.pushPose();
                    poseStack.translate(camX, camY, camZ);
                    StaticProjectileBehaviors.getBehavior(projectile.getEntityData().get(StaticProjectile.BEHAVIOR))
                            .render(projectile, entityYaw, partialTick, poseStack, bufferSource, LightTexture.FULL_BRIGHT);
                    poseStack.popPose();
                }else if (entity instanceof StaticSummon summon
                        && summon.getEntityData().get(StaticSummon.GLOW)) {
                    poseStack.pushPose();
                    poseStack.translate(camX, camY, camZ);
                    StaticSummonBehaviors.getBehavior(summon.getEntityData().get(StaticSummon.BEHAVIOR))
                            .render(summon, entityYaw, partialTick, poseStack, bufferSource, LightTexture.FULL_BRIGHT);
                    poseStack.popPose();
                }else if (entity instanceof SwordBeam beam) {
                    poseStack.pushPose();
                    poseStack.translate(camX, camY, camZ);
                    SwordBeamBehaviors.getBehavior(beam.getEntityData().get(SwordBeam.BEHAVIOR))
                            .render(beam, entityYaw, partialTick, poseStack, bufferSource, LightTexture.FULL_BRIGHT);
                    poseStack.popPose();
                }
            }
            bufferSource.endBatch();
        }
    }
}