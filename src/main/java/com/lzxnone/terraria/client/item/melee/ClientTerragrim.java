package com.lzxnone.terraria.client.item.melee;

import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.utils.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import org.joml.Quaternionf;
import com.lzxnone.terraria.client.entity.summon.IStaticSummonRenderBehavior;
import org.joml.Vector3f;

import static com.lzxnone.terraria.item.weapon.melee.Terragrim.*;

public class ClientTerragrim {
    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR = new IStaticSummonRenderBehavior() {

        public static final float HALF_WIDTH = 32.0f;
        public static final float HALF_HEIGHT = 30.0f;
        public static final float SCALE = 0.05f;

        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;
            Entity owner = summon.getOwner();
            if(owner == null) return;

            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            if(!customData.contains("idx")) return;

            VertexConsumer vertexConsumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES[Math.min(customData.getInt("idx"), RES.length - 1)]));
            Vector3f[] dirs = MathUtil.computeCoordinateSystem(owner);
            int rotate = summon.getEntityData().get(StaticSummon.RZP);

            Quaternionf rotation = new Quaternionf()
                .fromAxisAngleRad(dirs[0], (float) Math.toRadians(rotate));
            poseStack.mulPose(rotation);

            float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);
            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
            poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));

            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));

            vertexConsumer.addVertex(poseStack.last().pose(), -HALF_WIDTH * SCALE, -HALF_HEIGHT * SCALE, 0)
                .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 1.0f)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            vertexConsumer.addVertex(poseStack.last().pose(), HALF_WIDTH * SCALE, -HALF_HEIGHT * SCALE, 0)
                .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(1.0f, 1.0f)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            vertexConsumer.addVertex(poseStack.last().pose(), HALF_WIDTH * SCALE, HALF_HEIGHT * SCALE, 0)
                .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(1.0f, 0.0f)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            vertexConsumer.addVertex(poseStack.last().pose(), -HALF_WIDTH * SCALE, HALF_HEIGHT * SCALE, 0)
                .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.0f)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
        }
    };
}
