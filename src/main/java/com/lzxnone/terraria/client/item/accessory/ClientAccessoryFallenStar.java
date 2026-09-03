package com.lzxnone.terraria.client.item.accessory;

import com.lzxnone.terraria.client.entity.projectile.IStaticProjectileRenderBehavior;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.item.effect.FallenStarSummoner;
import com.lzxnone.terraria.utils.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class ClientAccessoryFallenStar {
    private static final ResourceLocation STAR_WAVE_RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/star_wave.png");
    private static final float STAR_WAVE_ALPHA = 0.5F;

    // 大光波尺寸与偏移
    public static final float LARGE_STAR_WAVE_HALF_WIDTH = 45 * 0.06F;
    public static final float LARGE_STAR_WAVE_HALF_HEIGHT = 17 * 0.06F;
    public static final double LARGE_STAR_WAVE_OFFSET = 1.5D;

    // 小光波尺寸与偏移（位于星星后方）
    public static final float SMALL_STAR_WAVE_HALF_WIDTH = 30 * 0.06F;
    public static final float SMALL_STAR_WAVE_HALF_HEIGHT = 11 * 0.06F;
    public static final double SMALL_STAR_WAVE_OFFSET = 2.5D;

    // 颜色配置（静态变量）
    // 紫色（星星斗篷）
    public static final float PURPLE_R = 0.7F;
    public static final float PURPLE_G = 0.3F;
    public static final float PURPLE_B = 1.0F;

    // 黄色（蜜蜂斗篷 / 星星面纱小光波）
    public static final float YELLOW_R = 1.0F;
    public static final float YELLOW_G = 0.9F;
    public static final float YELLOW_B = 0.2F;

    // 粉色（星星面纱大光波）
    public static final float PINK_R = 1.0F;
    public static final float PINK_G = 0.4F;
    public static final float PINK_B = 0.7F;

    // 深蓝色（魔力斗篷大光波）
    public static final float BLUE_R = 0.05F;
    public static final float BLUE_G = 0.2F;
    public static final float BLUE_B = 1.0F;

    // 白色（魔力斗篷小光波）
    public static final float WHITE_R = 1.0F;
    public static final float WHITE_G = 1.0F;
    public static final float WHITE_B = 1.0F;

    public static final IStaticProjectileRenderBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile projectile)) return;
            ItemStack itemStack = projectile.getEntityData().get(StaticProjectile.ITEM);
            if(itemStack == ItemStack.EMPTY) return;
            renderItem(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
            renderStarWave(projectile, poseStack, bufferSource);
        }
    };

    private static void renderStarWave(StaticProjectile projectile, PoseStack poseStack, MultiBufferSource bufferSource) {
        CompoundTag customData = projectile.getEntityData().get(StaticProjectile.CUSTOM_DATA);
        if(customData == null || !customData.contains("type")) return;

        String type = customData.getString("type");
        if(type.isEmpty()) return;

        Vec3 dir = projectile.getDeltaMovement().normalize();
        float[] xyRot = MathUtil.computeXYRot(dir.toVector3f());

        switch(type) {
            case FallenStarSummoner.STAR_CLOAK -> {
                drawStarWave(poseStack, bufferSource, dir, xyRot, LARGE_STAR_WAVE_OFFSET, LARGE_STAR_WAVE_HALF_WIDTH, LARGE_STAR_WAVE_HALF_HEIGHT, PURPLE_R, PURPLE_G, PURPLE_B, STAR_WAVE_ALPHA);
            }
            case FallenStarSummoner.BEE_CLOAK -> {
                drawStarWave(poseStack, bufferSource, dir, xyRot, LARGE_STAR_WAVE_OFFSET, LARGE_STAR_WAVE_HALF_WIDTH, LARGE_STAR_WAVE_HALF_HEIGHT, YELLOW_R, YELLOW_G, YELLOW_B, STAR_WAVE_ALPHA);
            }
            case FallenStarSummoner.STAR_VEIL -> {
                drawStarWave(poseStack, bufferSource, dir, xyRot, LARGE_STAR_WAVE_OFFSET, LARGE_STAR_WAVE_HALF_WIDTH, LARGE_STAR_WAVE_HALF_HEIGHT, PINK_R, PINK_G, PINK_B, STAR_WAVE_ALPHA);
                drawStarWave(poseStack, bufferSource, dir, xyRot, SMALL_STAR_WAVE_OFFSET, SMALL_STAR_WAVE_HALF_WIDTH, SMALL_STAR_WAVE_HALF_HEIGHT, YELLOW_R, YELLOW_G, YELLOW_B, STAR_WAVE_ALPHA);
            }
            case FallenStarSummoner.MANA_CLOAK -> {
                drawStarWave(poseStack, bufferSource, dir, xyRot, LARGE_STAR_WAVE_OFFSET, LARGE_STAR_WAVE_HALF_WIDTH, LARGE_STAR_WAVE_HALF_HEIGHT, BLUE_R, BLUE_G, BLUE_B, STAR_WAVE_ALPHA);
                drawStarWave(poseStack, bufferSource, dir, xyRot, SMALL_STAR_WAVE_OFFSET, SMALL_STAR_WAVE_HALF_WIDTH, SMALL_STAR_WAVE_HALF_HEIGHT, WHITE_R, WHITE_G, WHITE_B, STAR_WAVE_ALPHA);
            }
        }
    }

    private static void drawStarWave(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 dir, float[] xyRot,
                                     double offsetDistance, float halfWidth, float halfHeight,
                                     float r, float g, float b, float alpha) {
        poseStack.pushPose();
        poseStack.translate(-dir.x * offsetDistance, -dir.y * offsetDistance, -dir.z * offsetDistance);
        poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
        poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));

        VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(STAR_WAVE_RES));
        addQuad(consumer, poseStack.last().pose(), halfWidth, halfHeight, r, g, b, alpha);
        poseStack.mulPose(Axis.XP.rotationDegrees(60.0F));
        addQuad(consumer, poseStack.last().pose(), halfWidth, halfHeight, r, g, b, alpha);
        poseStack.mulPose(Axis.XP.rotationDegrees(60.0F));
        addQuad(consumer, poseStack.last().pose(), halfWidth, halfHeight, r, g, b, alpha);
        poseStack.popPose();
    }

    private static void addQuad(VertexConsumer consumer, Matrix4f matrix, float halfWidth, float halfHeight,
                                float r, float g, float b, float alpha) {
        consumer.addVertex(matrix, -halfWidth, -halfHeight, 0.0F)
            .setColor(r, g, b, alpha).setUv(0.0F, 1.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(matrix, halfWidth, -halfHeight, 0.0F)
            .setColor(r, g, b, alpha).setUv(1.0F, 1.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(matrix, halfWidth, halfHeight, 0.0F)
            .setColor(r, g, b, alpha).setUv(1.0F, 0.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(matrix, -halfWidth, halfHeight, 0.0F)
            .setColor(r, g, b, alpha).setUv(0.0F, 0.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0F, 1.0F, 0.0F);
    }
}
