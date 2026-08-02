package com.lzxnone.terraria.client.item.summon;

import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.PureColorVertexConsumer;
import com.lzxnone.terraria.entity.TintedVertexConsumer;
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
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.Vec3;
import com.lzxnone.terraria.client.entity.summon.IStaticSummonRenderBehavior;
import static com.lzxnone.terraria.item.weapon.summon.minion.Terraprisma.*;

public class ClientTerraprisma {
    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR = new IStaticSummonRenderBehavior() {

        public static final ResourceLocation RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/sword_trail.png");

        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon) || summon.getOwner() == null) return;
            ItemStack stack = summon.getEntityData().get(StaticSummon.ITEM);
            if(stack.isEmpty()) return;

            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            int idx = customData.contains("idx") ? customData.getInt("idx") : 0;
            int state = customData.contains("state") ? customData.getInt("state") : 0;
            float[] color = computeCrystalColor(summon.tickCount + partialTick, idx);
            Vec3 currentPos = summon.position();

            if(state == State.DASH_PRE.ordinal() || state == State.DASH.ordinal()) {
                for(int i = summon.afterimageFrames.size() - 1; i >= 0; i--) {
                    StaticSummon.AfterimageFrame frame = summon.afterimageFrames.get(i);
                    float alpha = AFTERIMAGE_ALPHA * (1.0f - (float)i / AFTERIMAGE_MAX_LENGTH);
                    Vec3 offset = frame.position().subtract(currentPos);

                    poseStack.pushPose();
                    poseStack.translate(offset.x, offset.y, offset.z);

                    poseStack.mulPose(Axis.YP.rotationDegrees(-frame.yRot()));
                    poseStack.mulPose(Axis.XP.rotationDegrees(frame.xRot()));

                    poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                    poseStack.mulPose(Axis.YP.rotationDegrees(90));

                    poseStack.scale(
                        summon.getEntityData().get(StaticSummon.SCALE_X) * AFTERIMAGE_SCALE,
                        summon.getEntityData().get(StaticSummon.SCALE_Y) * AFTERIMAGE_SCALE,
                        summon.getEntityData().get(StaticSummon.SCALE_Z) * AFTERIMAGE_SCALE
                    );

                    Minecraft.getInstance().getItemRenderer().renderStatic(
                        stack,
                        ItemDisplayContext.NONE,
                        LightTexture.FULL_BRIGHT,
                        OverlayTexture.NO_OVERLAY,
                        poseStack,
                        renderType -> {
                            VertexConsumer vertexConsumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(InventoryMenu.BLOCK_ATLAS));
                            return new PureColorVertexConsumer(vertexConsumer,
                                frame.r(),
                                frame.g(),
                                frame.b(),
                                alpha);
                        },
                        entity.level(),
                        0
                    );
                    poseStack.popPose();
                }
            }

            poseStack.pushPose();

            poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTick, summon.yRotO, summon.getYRot())));
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, summon.xRotO, summon.getXRot())));

            poseStack.mulPose(Axis.ZP.rotationDegrees(90));
            poseStack.mulPose(Axis.YP.rotationDegrees(90));

            poseStack.scale(
                summon.getEntityData().get(StaticSummon.SCALE_X),
                summon.getEntityData().get(StaticSummon.SCALE_Y),
                summon.getEntityData().get(StaticSummon.SCALE_Z)
            );

            for(int i = 0;i < 3;i++) {
                Minecraft.getInstance().getItemRenderer().renderStatic(
                    stack,
                    ItemDisplayContext.NONE,
                    LightTexture.FULL_BRIGHT,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    renderType -> {
                        VertexConsumer vertexConsumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(InventoryMenu.BLOCK_ATLAS));
                        return new TintedVertexConsumer(vertexConsumer,
                            color[0],
                            color[1],
                            color[2],
                            summon.getEntityData().get(StaticSummon.COLOR_A));
                    },
                    entity.level(),
                    0
                );
            }
            poseStack.popPose();

            if(state == State.ROTATE.ordinal()) {
                VertexConsumer vertexConsumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES));
                float mainAlpha = TRAIL_ALPHA;
                int quadCount = summon.trailPositions.size() / 2 - 1;
                for(int j = 0; j < 3; j++) {
                    for(int i = 0; i < quadCount; i++) {
                        Vec3 currentPoint1 = summon.trailPositions.get(i * 2);
                        Vec3 currentPoint2 = summon.trailPositions.get(i * 2 + 1);
                        Vec3 nextPoint1 = summon.trailPositions.get(i * 2 + 3);
                        Vec3 nextPoint2 = summon.trailPositions.get(i * 2 + 2);

                        double x1 = currentPoint1.x - currentPos.x;
                        double y1 = currentPoint1.y - currentPos.y;
                        double z1 = currentPoint1.z - currentPos.z;

                        double x2 = currentPoint2.x - currentPos.x;
                        double y2 = currentPoint2.y - currentPos.y;
                        double z2 = currentPoint2.z - currentPos.z;

                        double x3 = nextPoint1.x - currentPos.x;
                        double y3 = nextPoint1.y - currentPos.y;
                        double z3 = nextPoint1.z - currentPos.z;

                        double x4 = nextPoint2.x - currentPos.x;
                        double y4 = nextPoint2.y - currentPos.y;
                        double z4 = nextPoint2.z - currentPos.z;

                        float radio1 = (float) i / quadCount;
                        float radio2 = (float) (i + 1) / quadCount;

                        vertexConsumer.addVertex(poseStack.last().pose(), (float)x1, (float)y1, (float)z1)
                            .setColor(color[0], color[1], color[2], mainAlpha).setUv(radio1, 0.0f)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                        vertexConsumer.addVertex(poseStack.last().pose(), (float)x2, (float)y2, (float)z2)
                            .setColor(color[0], color[1], color[2], 0).setUv(radio1, 1.0f)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                        vertexConsumer.addVertex(poseStack.last().pose(), (float)x3, (float)y3, (float)z3)
                            .setColor(color[0], color[1], color[2], 0).setUv(radio2, 1.0f)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                        vertexConsumer.addVertex(poseStack.last().pose(), (float)x4, (float)y4, (float)z4)
                            .setColor(color[0], color[1], color[2], mainAlpha).setUv(radio2, 0.0f)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                    }
                }
            }
        }
    };

    private static float[] computeCrystalColor(float time, int idx) {
        float hue = (time * 0.015F + idx * 0.12F) % 1.0F;
        int rgb = Mth.hsvToRgb(hue, 0.85F, 1.0F);
        float rainbowR = ((rgb >> 16) & 255) / 255.0F;
        float rainbowG = ((rgb >> 8) & 255) / 255.0F;
        float rainbowB = (rgb & 255) / 255.0F;
        float tint = 0.35F;
        float pulse = 0.92F + 0.08F * Mth.sin(time * 0.12F + idx * 0.7F);

        return new float[]{
            (1.0F * (1.0F - tint) + rainbowR * tint) * pulse,
            (1.0F * (1.0F - tint) + rainbowG * tint) * pulse,
            (1.0F * (1.0F - tint) + rainbowB * tint) * pulse
        };
    }
}
