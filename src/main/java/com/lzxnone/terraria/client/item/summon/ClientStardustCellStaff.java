package com.lzxnone.terraria.client.item.summon;

import com.lzxnone.terraria.client.entity.projectile.IStaticProjectileRenderBehavior;
import com.lzxnone.terraria.client.entity.summon.IStaticSummonRenderBehavior;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class ClientStardustCellStaff {
    private static final ResourceLocation[] CELL_FRAMES = {
        ResourceLocation.parse("lzxnoneterraria:textures/vfx/stardust_cell0.png"),
        ResourceLocation.parse("lzxnoneterraria:textures/vfx/stardust_cell1.png"),
        ResourceLocation.parse("lzxnoneterraria:textures/vfx/stardust_cell2.png"),
        ResourceLocation.parse("lzxnoneterraria:textures/vfx/stardust_cell3.png")
    };
    private static final ResourceLocation[] CELL_MINI_FRAMES = {
        ResourceLocation.parse("lzxnoneterraria:textures/vfx/stardust_cell_mini0.png"),
        ResourceLocation.parse("lzxnoneterraria:textures/vfx/stardust_cell_mini1.png"),
        ResourceLocation.parse("lzxnoneterraria:textures/vfx/stardust_cell_mini2.png"),
        ResourceLocation.parse("lzxnoneterraria:textures/vfx/stardust_cell_mini3.png")
    };

    private static final float FRAME_HALF_SIZE = 0.5625F;
    private static final int FRAME_LOOP_TICKS = 6;

    private static int frameForAge(float age) {
        int t = Mth.floor(age) % FRAME_LOOP_TICKS;
        if(t < 1) return 0;
        if(t < 3) return 1;
        if(t < 4) return 2;
        return 3;
    }

    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR = new IStaticSummonRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack,
                           MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;
            float age = summon.getEntityData().get(StaticSummon.AGE) + partialTick;
            int frame = frameForAge(age);
            if(summon.getEntityData().get(StaticSummon.BEHAVIOR).equals(StaticSummonBehaviors.STARDUST_CELL_STAFF)) {
                renderBillboardFrame(
                        poseStack,
                        bufferSource,
                        CELL_FRAMES[frame],
                        summon.getEntityData().get(StaticSummon.SCALE_X) * FRAME_HALF_SIZE,
                        summon.getEntityData().get(StaticSummon.SCALE_Y) * FRAME_HALF_SIZE,
                        summon.getEntityData().get(StaticSummon.COLOR_R),
                        summon.getEntityData().get(StaticSummon.COLOR_G),
                        summon.getEntityData().get(StaticSummon.COLOR_B),
                        summon.getEntityData().get(StaticSummon.COLOR_A)
                );
            }else {
                renderBillboardFrame(
                        poseStack,
                        bufferSource,
                        CELL_MINI_FRAMES[frame],
                        summon.getEntityData().get(StaticSummon.SCALE_X) * FRAME_HALF_SIZE,
                        summon.getEntityData().get(StaticSummon.SCALE_Y) * FRAME_HALF_SIZE,
                        summon.getEntityData().get(StaticSummon.COLOR_R),
                        summon.getEntityData().get(StaticSummon.COLOR_G),
                        summon.getEntityData().get(StaticSummon.COLOR_B),
                        summon.getEntityData().get(StaticSummon.COLOR_A)
                );
            }
        }
    };

    public static final IStaticProjectileRenderBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack,
                           MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile projectile)) return;
            float age = projectile.getEntityData().get(StaticProjectile.AGE) + partialTick;
            int frame = frameForAge(age);
            renderBillboardFrame(
                poseStack,
                bufferSource,
                CELL_MINI_FRAMES[frame],
                projectile.getEntityData().get(StaticProjectile.SCALE_X) * FRAME_HALF_SIZE,
                projectile.getEntityData().get(StaticProjectile.SCALE_Y) * FRAME_HALF_SIZE,
                projectile.getEntityData().get(StaticProjectile.COLOR_R),
                projectile.getEntityData().get(StaticProjectile.COLOR_G),
                projectile.getEntityData().get(StaticProjectile.COLOR_B),
                projectile.getEntityData().get(StaticProjectile.COLOR_A)
            );
        }
    };

    private static void renderBillboardFrame(PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation texture,
                                             float halfWidth, float halfHeight,
                                             float r, float g, float b, float a) {
        poseStack.pushPose();
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());

        VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(texture));
        consumer.addVertex(poseStack.last().pose(), -halfWidth, -halfHeight, 0.0F)
            .setColor(r, g, b, a).setUv(0.0F, 1.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(poseStack.last().pose(), halfWidth, -halfHeight, 0.0F)
            .setColor(r, g, b, a).setUv(1.0F, 1.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(poseStack.last().pose(), halfWidth, halfHeight, 0.0F)
            .setColor(r, g, b, a).setUv(1.0F, 0.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(poseStack.last().pose(), -halfWidth, halfHeight, 0.0F)
            .setColor(r, g, b, a).setUv(0.0F, 0.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0F, 1.0F, 0.0F);
        poseStack.popPose();
    }
}
