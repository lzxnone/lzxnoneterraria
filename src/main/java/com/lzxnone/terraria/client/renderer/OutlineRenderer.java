package com.lzxnone.terraria.client.renderer;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.client.entity.projectile.ClientStaticProjectileRenderBehaviors;
import com.lzxnone.terraria.client.entity.summon.ClientStaticSummonRenderBehaviors;
import com.lzxnone.terraria.entity.IrisCompat;
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
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.GlStateBackup;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import java.util.Objects;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID, value = Dist.CLIENT)
public class OutlineRenderer {
    private static final int GL_NEAREST = 9728;
    private static final float OUTLINE_RADIUS = 2.0f;
    private static final float DEPTH_BIAS = 0.00005f;

    private static final RenderType MASK_QUADS = createMaskRenderType();

    private static RenderTarget outlineTarget;
    private static RenderTarget sceneDepthTarget;
    private static int lastW = -1;
    private static int lastH = -1;

    @SubscribeEvent
    public static void onRenderStage(RenderLevelStageEvent event) {
        if(event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;
        if(IrisCompat.isShaderPackInUse()) return;
        if(ShaderRegistry.getOutline() == null
                || ShaderRegistry.getOutlineMask() == null) return;
        if(!hasAnyOutline()) return;

        ensureTarget();
        if(outlineTarget == null) return;
        renderOutline(event);
    }

    private static void ensureTarget() {
        RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
        int w = Math.max(1, main.viewWidth);
        int h = Math.max(1, main.viewHeight);

        if(w == lastW && h == lastH && outlineTarget != null && sceneDepthTarget != null) return;

        if(outlineTarget != null) outlineTarget.destroyBuffers();
        if(sceneDepthTarget != null) sceneDepthTarget.destroyBuffers();
        outlineTarget = new TextureTarget(w, h, true, false);
        sceneDepthTarget = new TextureTarget(w, h, true, false);
        outlineTarget.setClearColor(0f, 0f, 0f, 0f);
        sceneDepthTarget.setClearColor(0f, 0f, 0f, 0f);
        outlineTarget.setFilterMode(GL_NEAREST);
        sceneDepthTarget.setFilterMode(GL_NEAREST);
        lastW = w;
        lastH = h;
    }

    private static void renderOutline(RenderLevelStageEvent event) {
        GlStateBackup backup = new GlStateBackup();
        RenderSystem.backupGlState(backup);
        ShaderInstance prevShader = RenderSystem.getShader();
        int prevTex0 = RenderSystem.getShaderTexture(0);
        int prevTex1 = RenderSystem.getShaderTexture(1);
        int prevTex2 = RenderSystem.getShaderTexture(2);

        try {
            RenderTarget main = Minecraft.getInstance().getMainRenderTarget();

            // 保存 AFTER_LEVEL 时主目标中最终可用的场景深度，供外扩像素做遮挡判断。
            sceneDepthTarget.copyDepthFrom(main);
            outlineTarget.clear(false);
            outlineTarget.copyDepthFrom(main);
            outlineTarget.bindWrite(true);

            renderOutlineEntities(
                event.getPoseStack(),
                event.getCamera(),
                event.getPartialTick().getGameTimeDeltaPartialTick(true),
                event
            );
            main.bindWrite(true);

            ShaderInstance shader = ShaderRegistry.getOutline();
            RenderSystem.setShader(() -> shader);
            RenderSystem.setShaderTexture(0, outlineTarget.getColorTextureId());
            RenderSystem.setShaderTexture(1, outlineTarget.getDepthTextureId());
            RenderSystem.setShaderTexture(2, sceneDepthTarget.getDepthTextureId());

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
                radius.set(OUTLINE_RADIUS);
            }
            Uniform depthBias = shader.getUniform("DepthBias");
            if(depthBias != null) {
                depthBias.set(DEPTH_BIAS);
            }

            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ZERO,
                GlStateManager.DestFactor.ONE
            );
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            drawFullscreen();
        }finally {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
            RenderSystem.setShaderTexture(0, prevTex0);
            RenderSystem.setShaderTexture(1, prevTex1);
            RenderSystem.setShaderTexture(2, prevTex2);
            if(prevShader != null) {
                RenderSystem.setShader(() -> prevShader);
            }
            RenderSystem.restoreGlState(backup);
        }
    }

    private static void renderOutlineEntities(PoseStack poseStack, Camera camera, float partialTick,
                                              RenderLevelStageEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if(mc.level == null) return;

        try(ByteBufferBuilder byteBuf = new ByteBufferBuilder(65536)) {
            MultiBufferSource.BufferSource maskBuffers = MultiBufferSource.immediate(byteBuf);
            MultiBufferSource bufferSource = ignored -> maskBuffers.getBuffer(MASK_QUADS);

            Matrix4f previousProjection = new Matrix4f(RenderSystem.getProjectionMatrix());
            VertexSorting previousSorting = RenderSystem.getVertexSorting();
            Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.pushMatrix();
            modelViewStack.set(event.getModelViewMatrix());
            RenderSystem.applyModelViewMatrix();
            RenderSystem.setProjectionMatrix(event.getProjectionMatrix(), previousSorting);

            try {
                for(Entity entity : mc.level.entitiesForRendering()) {
                    if(!hasOutline(entity)) continue;

                    double x = Mth.lerp(partialTick, entity.xo, entity.getX());
                    double y = Mth.lerp(partialTick, entity.yo, entity.getY());
                    double z = Mth.lerp(partialTick, entity.zo, entity.getZ());
                    float entityYaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());

                    double camX = x - camera.getPosition().x();
                    double camY = y - camera.getPosition().y();
                    double camZ = z - camera.getPosition().z();

                    poseStack.pushPose();
                    try {
                        poseStack.translate(camX, camY, camZ);
                        renderEntityMask(entity, entityYaw, partialTick, poseStack, bufferSource);
                    }finally {
                        poseStack.popPose();
                    }
                }
                maskBuffers.endBatch();
            }finally {
                RenderSystem.setProjectionMatrix(previousProjection, previousSorting);
                modelViewStack.popMatrix();
                RenderSystem.applyModelViewMatrix();
            }
        }
    }

    private static boolean hasOutline(Entity entity) {
        return entity instanceof StaticProjectile projectile
                && projectile.getEntityData().get(StaticProjectile.OUTLINE)
            || entity instanceof StaticSummon summon
                && summon.getEntityData().get(StaticSummon.OUTLINE);
    }

    private static boolean hasAnyOutline() {
        Minecraft mc = Minecraft.getInstance();
        if(mc.level == null) return false;
        for(Entity entity : mc.level.entitiesForRendering()) {
            if(hasOutline(entity)) return true;
        }
        return false;
    }

    private static void renderEntityMask(Entity entity, float entityYaw, float partialTick,
                                         PoseStack poseStack, MultiBufferSource bufferSource) {
        if(entity instanceof StaticProjectile projectile) {
            if(!projectile.isInit) return;
            var behavior = ClientStaticProjectileRenderBehaviors.getBehavior(
                projectile.getEntityData().get(StaticProjectile.BEHAVIOR));
            String mode = projectile.getEntityData().get(StaticProjectile.RENDER_MODE);
            if("item".equals(mode) && !projectile.getEntityData().get(StaticProjectile.ITEM).isEmpty()) {
                behavior.renderItem(projectile, entityYaw, partialTick, poseStack, bufferSource, LightTexture.FULL_BRIGHT);
            }else if("block".equals(mode)
                    && !projectile.getEntityData().get(StaticProjectile.BLOCK).isAir()) {
                behavior.renderBlock(projectile, entityYaw, partialTick, poseStack, bufferSource, LightTexture.FULL_BRIGHT);
            }
        }else if(entity instanceof StaticSummon summon) {
            var behavior = ClientStaticSummonRenderBehaviors.getBehavior(
                summon.getEntityData().get(StaticSummon.BEHAVIOR));
            String mode = summon.getEntityData().get(StaticSummon.RENDER_MODE);
            if("item".equals(mode) && !summon.getEntityData().get(StaticSummon.ITEM).isEmpty()) {
                behavior.renderItem(summon, entityYaw, partialTick, poseStack, bufferSource, LightTexture.FULL_BRIGHT);
            }else if("block".equals(mode)
                    && !summon.getEntityData().get(StaticSummon.BLOCK).isAir()) {
                behavior.renderBlock(summon, entityYaw, partialTick, poseStack, bufferSource, LightTexture.FULL_BRIGHT);
            }
        }
    }

    private static RenderType createMaskRenderType() {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(ShaderRegistry::getOutlineMask))
            .setTextureState(new RenderStateShard.TextureStateShard(InventoryMenu.BLOCK_ATLAS, false, false))
            .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
            .setCullState(RenderStateShard.NO_CULL)
            .setLightmapState(RenderStateShard.NO_LIGHTMAP)
            .setOverlayState(RenderStateShard.NO_OVERLAY)
            .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
            .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
            .createCompositeState(false);
        return RenderType.create(
            "outline_mask", DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS, 1536, false, false, state
        );
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
