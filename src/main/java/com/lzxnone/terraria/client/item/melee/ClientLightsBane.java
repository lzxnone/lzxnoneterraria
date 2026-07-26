package com.lzxnone.terraria.client.item.melee;

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
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import com.lzxnone.terraria.client.entity.summon.IStaticSummonRenderBehavior;
import static com.lzxnone.terraria.item.weapon.melee.LightsBane.*;

public class ClientLightsBane {
    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR = new IStaticSummonRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick,
            PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;

            float lifeRatio = (summon.getEntityData().get(StaticSummon.AGE) + partialTick) / summon.getEntityData().get(StaticSummon.LIFETIME);
            if(lifeRatio > 1.0f) return;

            int frame = Math.min((int) (lifeRatio * 12.0f), 11);

            poseStack.pushPose();
            poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());

            VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES[frame]));
            Matrix4f matrix = poseStack.last().pose();

            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            float scale;
            if(customData.contains("big") && customData.getBoolean("big")) scale = SCALE_BIG;
            else scale = SCALE_SMALL;

            poseStack.mulPose(Axis.ZP.rotationDegrees(summon.getEntityData().get(StaticSummon.RZP)));

            consumer.addVertex(matrix, -HALF_WIDTH * scale, -HALF_HEIGHT * scale, 0)
                .setColor(255, 255, 255, 255).setUv(0.0f, 1.0f)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            consumer.addVertex(matrix, HALF_WIDTH * scale, -HALF_HEIGHT * scale, 0)
                .setColor(255, 255, 255, 255).setUv(1.0f, 1.0f)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            consumer.addVertex(matrix, HALF_WIDTH * scale, HALF_HEIGHT * scale, 0)
                .setColor(255, 255, 255, 255).setUv(1.0f, 0.0f)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            consumer.addVertex(matrix, -HALF_WIDTH * scale, HALF_HEIGHT * scale, 0)
                .setColor(255, 255, 255, 255).setUv(0.0f, 0.0f)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);

            poseStack.popPose();
        }
    };
}
