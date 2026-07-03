package com.lzxnone.terraria.entity;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.entity.beam.SwordBeam;
import com.lzxnone.terraria.entity.beam.SwordBeamBehaviors;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID, value = Dist.CLIENT)
public class EntityEmissiveRenderer {
    @SubscribeEvent
    public static void onRenderStage(RenderLevelStageEvent event) {
        if(event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) return;

        Minecraft mc = Minecraft.getInstance();
        if(mc.level == null || mc.player == null) return;

        PoseStack poseStack = event.getPoseStack();
        Camera camera = event.getCamera();
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        for(Entity entity : mc.level.entitiesForRendering()) {
            double x = Mth.lerp(partialTick, entity.xo, entity.getX());
            double y = Mth.lerp(partialTick, entity.yo, entity.getY());
            double z = Mth.lerp(partialTick, entity.zo, entity.getZ());
            float entityYaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());

            double camX = x - camera.getPosition().x();
            double camY = y - camera.getPosition().y();
            double camZ = z - camera.getPosition().z();

            if(entity instanceof StaticProjectile projectile && projectile.getEntityData().get(StaticProjectile.RENDER_MODE).equals("custom") && projectile.getEntityData().get(StaticProjectile.GLOW)) {
                poseStack.pushPose();
                poseStack.translate(camX, camY, camZ);
                StaticProjectileBehaviors.getBehavior(projectile.getEntityData().get(StaticProjectile.BEHAVIOR))
                        .render(projectile, entityYaw, partialTick, poseStack, bufferSource, 15728880);

                poseStack.popPose();
            }else if(entity instanceof StaticSummon summon && summon.getEntityData().get(StaticSummon.RENDER_MODE).equals("custom") && summon.getEntityData().get(StaticSummon.GLOW)) {
                poseStack.pushPose();
                poseStack.translate(camX, camY, camZ);
                StaticSummonBehaviors.getBehavior(summon.getEntityData().get(StaticSummon.BEHAVIOR))
                        .render(summon, entityYaw, partialTick, poseStack, bufferSource, 15728880);

                poseStack.popPose();
            }else if(entity instanceof SwordBeam beam) {
                poseStack.pushPose();
                poseStack.translate(camX, camY, camZ);
                SwordBeamBehaviors.getBehavior(beam.getEntityData().get(SwordBeam.BEHAVIOR))
                        .render(beam, entityYaw, partialTick, poseStack, bufferSource, 15728880);

                poseStack.popPose();
            }
        }
        bufferSource.endBatch();
    }
}
