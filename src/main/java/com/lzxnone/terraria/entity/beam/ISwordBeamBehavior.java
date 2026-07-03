package com.lzxnone.terraria.entity.beam;

import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.utils.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface ISwordBeamBehavior {
    ResourceLocation RES0 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/terra_beam0.png");
    ResourceLocation RES1 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/terra_beam1.png");
    ResourceLocation RES2 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/terra_beam2.png");
    ResourceLocation RES3 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/terra_beam3.png");
    ResourceLocation RES4 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/terra_beam4.png");
    ResourceLocation RES5 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/beam_sparkle.png");

    float FADE_IN = 0.33f;
    float FADE_OUT = 0.67f;

    default void render(Entity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(!(entity instanceof SwordBeam beam)) return;
        Entity owner = beam.getOwner();
        if(owner == null) return;

        int age = beam.getEntityData().get(SwordBeam.AGE);
        int lifetime = Math.max(1, beam.getEntityData().get(SwordBeam.LIFETIME));

        float progress = (age + partialTick) / (float) lifetime;
        if(progress > 1.0f) progress = 1.0f;

        Vec3 look = owner.getLookAngle().normalize();
        Vector3f[] dirs = MathUtil.computeDir(MathUtil.toVector3f(look));

        VertexConsumer vertexConsumer0 = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RES0));
        VertexConsumer vertexConsumer1 = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RES0));
        VertexConsumer vertexConsumer2 = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RES0));
        VertexConsumer vertexConsumer3 = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RES3));
        VertexConsumer vertexConsumer4 = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RES4));
        VertexConsumer vertexConsumer5 = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RES5));

        Vector3f color0 = entity.getEntityData().get(SwordBeam.COLOR0);
        Vector3f color1 = entity.getEntityData().get(SwordBeam.COLOR1);
        Vector3f color2 = entity.getEntityData().get(SwordBeam.COLOR2);
        Vector3f color3 = entity.getEntityData().get(SwordBeam.COLOR3);
        float alpha;

        if(progress <= FADE_IN) {
            alpha = 1 - (FADE_IN - progress) / FADE_IN;
        }else if(progress >= FADE_OUT) {
            alpha = 1 - (progress - FADE_OUT) / (1 - FADE_OUT);
        }else {
            alpha = 1.0f;
        }

        float halfWidth = SwordBeam.HALF_WIDTH * SwordBeam.SCALE;
        float halfHeight = SwordBeam.HALF_HEIGHT * SwordBeam.SCALE;

        //左边
        poseStack.pushPose();
        this.applyTranslate(poseStack, dirs, progress - 0.1f * (1.0f - progress), SwordBeam.DIST);
        this.applyRotate(poseStack, dirs, progress - 0.1f * (1.0f - progress));
        renderQuad(poseStack.last().pose(), vertexConsumer0,
                color0.x(), color0.y(), color0.z(), alpha, halfWidth, halfHeight, 0f, 0, -0.01f);
        poseStack.popPose();

        //右边
        poseStack.pushPose();
        this.applyTranslate(poseStack, dirs, progress + 0.1f, SwordBeam.DIST);
        this.applyRotate(poseStack, dirs, progress + 0.05f);
        renderQuad(poseStack.last().pose(), vertexConsumer2,
                color2.x(), color2.y(), color2.z(), alpha, halfWidth, halfHeight, 0f, 0, -0.02f);
        poseStack.popPose();

        //中间
        poseStack.pushPose();
        this.applyTranslate(poseStack, dirs, progress, SwordBeam.DIST);
        this.applyRotate(poseStack, dirs, progress);
        renderQuad(poseStack.last().pose(), vertexConsumer1,
                color1.x(), color1.y(), color1.z(), alpha, halfWidth, halfHeight, 0f, 0f, 0f);
        poseStack.popPose();

        //三线
        poseStack.pushPose();
        this.applyTranslate(poseStack, dirs, progress, SwordBeam.DIST / 1.5f);
        this.applyRotate(poseStack, dirs, progress);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        renderQuad(poseStack.last().pose(), vertexConsumer3,
                color3.x(), color3.y(), color3.z(), alpha, halfWidth, halfHeight, 0f, 0f, 0.02f);
        poseStack.popPose();

        poseStack.pushPose();
        this.applyTranslate(poseStack, dirs, progress, SwordBeam.DIST / 1.25f);
        this.applyRotate(poseStack, dirs, progress);
        poseStack.scale(0.75f, 0.75f, 0.75f);
        renderQuad(poseStack.last().pose(), vertexConsumer3,
                color3.x(), color3.y(), color3.z(), alpha, halfWidth, halfHeight, 0f, 0f, 0.02f);
        poseStack.popPose();

        poseStack.pushPose();
        this.applyTranslate(poseStack, dirs, progress, SwordBeam.DIST * 1.15f);
        this.applyRotate(poseStack, dirs, progress);
        poseStack.scale(0.95f, 0.95f, 0.95f);
        renderQuad(poseStack.last().pose(), vertexConsumer3,
                color3.x(), color3.y(), color3.z(), alpha, halfWidth, halfHeight, 0f, 0f, 0.02f);
        poseStack.popPose();

        //边缘高光
        poseStack.pushPose();
        this.applyTranslate(poseStack, dirs, progress + 0.05f, SwordBeam.DIST * 1.15f);
        this.applyRotate(poseStack, dirs, progress + 0.05f);
        poseStack.scale(1.0f, 1.0f, 1.0f);
        for(int i = 0;i < 20;i++) {
            renderQuad(poseStack.last().pose(), vertexConsumer4,
                    color1.x(), color1.y(), color1.z(), alpha, halfWidth, halfHeight, 0f, 0f, 0.03f);
        }
        poseStack.popPose();

        //闪烁
        poseStack.pushPose();
        this.applyTranslate(poseStack, dirs, progress + 0.25f, SwordBeam.DIST * 2.25f);
        this.applyRotate(poseStack, dirs, progress + 0.25f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
        poseStack.scale(1.0f, 1.0f, 1.0f);
        for(int i = 0;i < 20;i++) {
            renderQuad(poseStack.last().pose(), vertexConsumer5,
                    color1.x(), color1.y(), color1.z(), alpha, 32 * SwordBeam.SCALE, 32 * SwordBeam.SCALE, 0f, 0f, 0.03f);
        }
        poseStack.popPose();
    }

    default void applyRotate(PoseStack poseStack, Vector3f[] dirs, float progress) {
        Vector3f dir = dirs[0];
        Vector3f up = dirs[1];

        float angle = (float) ((0.5 - progress) * Math.PI);
        Quaternionf rotation = new Quaternionf().fromAxisAngleRad(up, angle);
        poseStack.mulPose(rotation);

        float xzLen0 = (float) Math.sqrt(dir.x * dir.x + dir.z * dir.z);
        float pitch0 = (float) (Math.atan2(-dir.y, xzLen0) * (180.0 / Math.PI));
        float yaw0 = (float) (Math.atan2(-dir.x, dir.z) * (180.0 / Math.PI));
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw0));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch0));

        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
    }

    default void applyTranslate(PoseStack poseStack, Vector3f[] dirs, float progress, float dist) {
        Vector3f dir = dirs[0];
        Vector3f right = dirs[2];

        float cos = (float) Math.cos(progress * Math.PI);
        float sin = (float) Math.sin(progress * Math.PI);
        Vector3f current = new Vector3f(
            cos * right.x  + sin * dir.x,
            cos * right.y  + sin * dir.y,
            cos * right.z  + sin * dir.z
        );
        poseStack.translate(current.x * dist, current.y * dist, current.z * dist);
    }

    default void renderQuad(Matrix4f matrix, VertexConsumer consumer, float r, float g, float b, float a, float halfWidth, float halfHeight, float offsetX, float offsetY, float offsetZ) {
        int ir = (int) (r * 255.0F);
        int ig = (int) (g * 255.0F);
        int ib = (int) (b * 255.0F);
        int ia = (int) (a * 255.0F);

        consumer.addVertex(matrix, -halfWidth, -halfHeight + offsetY, offsetZ)
                .setColor(ir, ig, ib, ia).setUv(0.0f, 1.0f)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
        consumer.addVertex(matrix, halfWidth, -halfHeight + offsetY, offsetZ)
                .setColor(ir, ig, ib, ia).setUv(1.0f, 1.0f)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
        consumer.addVertex(matrix, halfWidth, halfHeight + offsetY, offsetZ)
                .setColor(ir, ig, ib, ia).setUv(1.0f, 0.0f)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
        consumer.addVertex(matrix, -halfWidth, halfHeight + offsetY, offsetZ)
                .setColor(ir, ig, ib, ia).setUv(0.0f, 0.0f)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
    }

    default void generate(Entity entity, CompoundTag beamData) {
        if(entity == null) return;
        SwordBeam beam = new SwordBeam(ModEntities.SWORD_BEAM.get(), entity.level());
        beam.setOwner(entity);
        Vec3 pos = new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ());
        beam.setPos(pos);
        if(beamData.contains("behavior")) beam.getEntityData().set(SwordBeam.BEHAVIOR, beamData.getString("behavior"));
        if(beamData.contains("color0R") && beamData.contains("color0G") && beamData.contains("color0B")) {
            beam.getEntityData().set(SwordBeam.COLOR0, new Vector3f(beamData.getFloat("color0R"), beamData.getFloat("color0G"), beamData.getFloat("color0B")));
        }
        if(beamData.contains("color1R") && beamData.contains("color1G") && beamData.contains("color1B")) {
            beam.getEntityData().set(SwordBeam.COLOR1, new Vector3f(beamData.getFloat("color1R"), beamData.getFloat("color1G"), beamData.getFloat("color1B")));
        }
        if(beamData.contains("color2R") && beamData.contains("color2G") && beamData.contains("color2B")) {
            beam.getEntityData().set(SwordBeam.COLOR2, new Vector3f(beamData.getFloat("color2R"), beamData.getFloat("color2G"), beamData.getFloat("color2B")));
        }
        if(beamData.contains("color3R") && beamData.contains("color3G") && beamData.contains("color3B")) {
            beam.getEntityData().set(SwordBeam.COLOR3, new Vector3f(beamData.getFloat("color3R"), beamData.getFloat("color3G"), beamData.getFloat("color3B")));
        }
        if(beamData.contains("age")) beam.getEntityData().set(SwordBeam.AGE, beamData.getInt("age"));
        if(beamData.contains("lifetime")) beam.getEntityData().set(SwordBeam.LIFETIME, beamData.getInt("lifetime"));
        if(beamData.contains("customData")) beam.getEntityData().set(SwordBeam.CUSTOM_DATA, beamData.getCompound("customData").copy());
        if(beamData.contains("behavior") && beamData.contains("cooldown") && entity instanceof Player player) {
            ResourceLocation itemKey = ResourceLocation.fromNamespaceAndPath("lzxnoneterraria", beamData.getString("behavior"));
            Item item = BuiltInRegistries.ITEM.get(itemKey);
            if(item != Items.AIR) player.getCooldowns().addCooldown(item, beamData.getInt("cooldown"));
        }
        entity.level().addFreshEntity(beam);
    }

    default void onMoving(SwordBeam beam) {}
    default void onHitEntity(SwordBeam beam, EntityHitResult result) {}
    default void onHitBlock(SwordBeam beam, BlockHitResult result) {}
    default void onDied(SwordBeam beam) {
        if(!beam.level().isClientSide()) {
            beam.discard();
        }
    }
}
