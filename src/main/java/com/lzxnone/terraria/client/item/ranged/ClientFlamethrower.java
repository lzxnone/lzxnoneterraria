package com.lzxnone.terraria.client.item.ranged;

import com.lzxnone.terraria.client.entity.summon.IStaticSummonRenderBehavior;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ClientFlamethrower {
    private static final double RENDER_INTERVAL = 2.0D;
    private static final float MIN_HALF_SIZE = 0.05f;
    private static final float MAX_HALF_SIZE = 3.0f;

    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR = new IStaticSummonRenderBehavior() {
        public static final ResourceLocation[] RES = {
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/flame_projectile0.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/flame_projectile1.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/flame_projectile2.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/flame_projectile3.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/flame_projectile4.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/flame_projectile5.png"),
            ResourceLocation.parse("lzxnoneterraria:textures/vfx/flame_projectile6.png")
        };

        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            float age = summon.getEntityData().get(StaticSummon.AGE) + partialTick;
            float lifetime = Math.max(1.0f, summon.getEntityData().get(StaticSummon.LIFETIME));
            Vec3 end = new Vec3(
                Mth.lerp(partialTick, summon.xo, summon.getX()),
                Mth.lerp(partialTick, summon.yo, summon.getY()),
                Mth.lerp(partialTick, summon.zo, summon.getZ())
            );
            Vec3 start = new Vec3(
                customData.contains("startX") ? customData.getFloat("startX") : end.x,
                customData.contains("startY") ? customData.getFloat("startY") : end.y,
                customData.contains("startZ") ? customData.getFloat("startZ") : end.z
            );

            float progress = Mth.clamp(age / lifetime, 0.0f, 1.0f);
            int resIndex = Math.min(RES.length - 1, (int) (progress * RES.length));
            float alpha = 1.0f - progress * 0.85f;
            float flameRed = customData.contains("flameRed") ? customData.getFloat("flameRed") : 1.0f;
            float flameGreen = customData.contains("flameGreen") ? customData.getFloat("flameGreen") : 0.55f;
            float flameBlue = customData.contains("flameBlue") ? customData.getFloat("flameBlue") : 0.1f;
            Vec3 span = end.subtract(start);
            double length = span.length();
            if(length <= 1.0E-4D) return;
            Vec3 direction = span.scale(1.0D / length);

            VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.entityAdditiveEmissive(RES[resIndex]));
            int index = 0;
            for(double distance = Math.min(RENDER_INTERVAL, length); distance <= length + 1.0E-4D; distance += RENDER_INTERVAL) {
                float segmentProgress = (float) (distance / length);
                Vec3 localPos = start.add(direction.scale(distance)).subtract(end);
                float halfSize = Mth.lerp(segmentProgress, MIN_HALF_SIZE, MAX_HALF_SIZE);
                float rotationAge = age + index;

                poseStack.pushPose();
                poseStack.translate(localPos.x, localPos.y, localPos.z);
                poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
                poseStack.mulPose(Axis.ZP.rotationDegrees(rotationAge * 24.0f));

                consumer.addVertex(poseStack.last().pose(), -halfSize, -halfSize, 0.0f)
                    .setColor(flameRed, Math.min(1.0f, flameGreen * 1.45f), Math.min(1.0f, flameBlue * 1.45f), alpha).setUv(0.0f, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                consumer.addVertex(poseStack.last().pose(), halfSize, -halfSize, 0.0f)
                    .setColor(flameRed, flameGreen, flameBlue, alpha).setUv(1.0f, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                consumer.addVertex(poseStack.last().pose(), halfSize, halfSize, 0.0f)
                    .setColor(flameRed, flameGreen * 0.35f, flameBlue * 0.35f, alpha).setUv(1.0f, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                consumer.addVertex(poseStack.last().pose(), -halfSize, halfSize, 0.0f)
                    .setColor(flameRed, flameGreen, flameBlue, alpha).setUv(0.0f, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);

                poseStack.popPose();
                index++;
            }
        }
    };
}
