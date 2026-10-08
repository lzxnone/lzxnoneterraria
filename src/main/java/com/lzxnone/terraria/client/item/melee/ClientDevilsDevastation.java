package com.lzxnone.terraria.client.item.melee;

import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.client.event.ShaderRegistry;
import com.lzxnone.terraria.utils.*;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.particle.IronSparkParticleOptions;
import org.joml.Vector4f;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.GlStateBackup;

import java.util.*;
import com.lzxnone.terraria.client.entity.projectile.IStaticProjectileRenderBehavior;
import com.lzxnone.terraria.client.entity.summon.IStaticSummonRenderBehavior;
import static com.lzxnone.terraria.item.weapon.melee.DevilsDevastation.*;

public class ClientDevilsDevastation {
    public static final IStaticProjectileRenderBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile projectile)) return;
            renderItem(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
            VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.entityAdditiveEmissive(RES));
            Vec3 entWorldPos = new Vec3(
                Mth.lerp(partialTick, projectile.xo, projectile.getX()),
                Mth.lerp(partialTick, projectile.yo, projectile.getY()),
                Mth.lerp(partialTick, projectile.zo, projectile.getZ())
            );
            Matrix4f matrix = poseStack.last().pose();
            renderTube(buffer, matrix, entWorldPos, projectile.trailPositions, PROJECTILE_TRAIL_RADIUS * 1.0F, 1.0f, 1,1,1, true);
            renderTube(buffer, matrix, entWorldPos, projectile.trailPositions, PROJECTILE_TRAIL_RADIUS * 1.25F, 1.0f * 0.5F, 0.7F,0.15F,0.55F, true);
            renderTube(buffer, matrix, entWorldPos, projectile.trailPositions, PROJECTILE_TRAIL_RADIUS * 1.5F, 1.0f * 0.2F, 0.5F,0.1F,0.4F, true);
            renderTube(buffer, matrix, entWorldPos, projectile.trailPositions2, PROJECTILE_TRAIL_RADIUS * 1.0F, 1.0f, 1,1,1, true);
            renderTube(buffer, matrix, entWorldPos, projectile.trailPositions2, PROJECTILE_TRAIL_RADIUS * 1.25F, 1.0f * 0.5F, 0.7F,0.15F,0.55F, true);
            renderTube(buffer, matrix, entWorldPos, projectile.trailPositions2, PROJECTILE_TRAIL_RADIUS * 1.5F, 1.0f * 0.2F, 0.5F,0.1F,0.4F, true);
        }
    };

    public static final IStaticProjectileRenderBehavior PROJECTILE_BEHAVIOR2 = new IStaticProjectileRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile projectile)) return;
            ItemStack stack = projectile.getEntityData().get(StaticProjectile.ITEM);
            if(stack.isEmpty()) return;

            float scaleX = 0.05f;
            float scaleY = 0.05f;
            float scaleZ = 0.05f;

            float age = projectile.getEntityData().get(StaticProjectile.AGE) + partialTick;
            Vector3f dir = projectile.getEntityData().get(StaticProjectile.DIRECTION);
            Vector3f up = projectile.getEntityData().get(StaticProjectile.UP);
            int rotate = projectile.getEntityData().get(StaticProjectile.RZP);

            poseStack.pushPose();

            RenderUtil.applyRotate(poseStack, dir, up, age * 30 + 60, rotate);
            poseStack.scale(4.0f, 4.0f, 4.0f);

            Minecraft.getInstance().getItemRenderer().renderStatic(
                stack,
                ItemDisplayContext.NONE,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                entity.level(),
                0
            );
            poseStack.popPose();

            poseStack.pushPose();

            RenderUtil.applyRotate(poseStack, dir, up, age * 30 + 180, rotate);
            poseStack.scale(scaleX, scaleY, scaleZ);

            VertexConsumer buffer0 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES2));
            RenderUtil.renderQuad(poseStack.last().pose(), buffer0, 0.729f, 0.396f, 0.345f, 1.0f, 78, 78, 0, 0, 0);
            poseStack.popPose();

            poseStack.pushPose();

            RenderUtil.applyRotate(poseStack, dir, up, age * 30, rotate);
            poseStack.scale(scaleX, scaleY, scaleZ);

            VertexConsumer buffer1 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES3));
            RenderUtil.renderQuad(poseStack.last().pose(), buffer1, 0.8f, 0.176f, 0.78f, 1.0f, 78, 78, 0, 0, 0);
            poseStack.popPose();
        }
    };

    public static final IStaticProjectileRenderBehavior PROJECTILE_BEHAVIOR3 = new IStaticProjectileRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile projectile)) return;
            ItemStack itemStack = projectile.getEntityData().get(StaticProjectile.ITEM);
            if(itemStack == ItemStack.EMPTY) return;
            CompoundTag customData = projectile.getEntityData().get(StaticProjectile.CUSTOM_DATA);
            if(!customData.contains("w")) return;
            float w = customData.getFloat("w");
            float t = projectile.getEntityData().get(StaticProjectile.AGE) + partialTick;

            Vec3 dir = MathUtil.toVec3(projectile.getEntityData().get(StaticProjectile.DIRECTION)).normalize();
            Vec3 up = MathUtil.toVec3(projectile.getEntityData().get(StaticProjectile.UP)).normalize();

            Quaternionf rotationDir = new Quaternionf()
                .fromAxisAngleRad(dir.toVector3f(), (float) Math.toRadians(projectile.getEntityData().get(StaticProjectile.RZP)));
            Quaternionf rotationUp;
            Quaternionf rotationUp2;
            if(w > 0) {
                rotationUp = new Quaternionf().fromAxisAngleRad(up.toVector3f(), (float) Math.PI - w * t);
                rotationUp2 = new Quaternionf().fromAxisAngleRad(up.toVector3f(), (float) Math.PI * 0.75f - w * t);
            }else {
                rotationUp = new Quaternionf().fromAxisAngleRad(up.toVector3f(), (float) -Math.PI - w * t);
                rotationUp2 = new Quaternionf().fromAxisAngleRad(up.toVector3f(), (float) -Math.PI * 1.25f - w * t);
            }

            float[] xyRot = MathUtil.computeXYRot(dir.toVector3f(), up.toVector3f());

            poseStack.pushPose();

            //旋转
            poseStack.mulPose(rotationUp);
            poseStack.mulPose(rotationDir);
            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
            poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-135));

            //缩放
            poseStack.scale(4.0f, 4.0f, 4.0f);

            Minecraft.getInstance().getItemRenderer().renderStatic(
                itemStack,
                ItemDisplayContext.NONE,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                entity.level(),
                0
            );
            poseStack.popPose();

            poseStack.pushPose();

            Vector3f tipDir = dir.toVector3f().rotate(rotationUp);
            poseStack.translate(-tipDir.x * 2, -tipDir.y * 2, -tipDir.z * 2);
            poseStack.translate(-up.x * 0, -up.y * 0, -up.z * 0);
            poseStack.mulPose(rotationUp2);
            poseStack.mulPose(rotationDir);
            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
            poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-135));
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            renderEnergyWave(bufferSource, poseStack, projectile);

            poseStack.popPose();

            float radio = t / projectile.getEntityData().get(StaticProjectile.LIFETIME);
            float alpha = 1.0f - radio;

            if(alpha > 0.001f) {
                poseStack.pushPose();

                poseStack.mulPose(rotationUp);
                poseStack.mulPose(rotationDir);
                poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
                poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));
                poseStack.mulPose(Axis.XP.rotationDegrees(-90));
                if(w > 0) poseStack.mulPose(Axis.ZP.rotationDegrees(-105));
                else poseStack.mulPose(Axis.ZP.rotationDegrees(75));

                float halfWidth = 78;
                float halfHeight = 78;
                VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.entityAdditiveEmissive(RES5));

                Vector3f[] sizes = new Vector3f[]{
                    new Vector3f(0.2f, 0.2f, 0.2f),
                    new Vector3f(0.3f, 0.3f, 0.3f),
                    new Vector3f(0.4f, 0.4f, 0.4f),
                };
                Vector3f[] colors = new Vector3f[]{
                    new Vector3f(0.725f, 0.345f, 1.0f),
                    new Vector3f(0.847f, 0.247f, 0.745f),
                    new Vector3f(0.882f, 0.345f, 0.247f),
                };

                for(int i = 0;i < 3;i++) {
                    poseStack.pushPose();
                    poseStack.scale(sizes[i].x, sizes[i].y, sizes[i].z);

                    for(int j = 0;j < 3;j++) {
                        consumer.addVertex(poseStack.last().pose(), -halfWidth, halfHeight, 0.01f * i)
                            .setColor(colors[i].x, colors[i].y, colors[i].z, alpha).setUv(0.0f, 0.0f)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                        consumer.addVertex(poseStack.last().pose(), -halfWidth, -halfHeight, 0.01f * i)
                            .setColor(colors[i].x, colors[i].y, colors[i].z, alpha).setUv(0.0f, 1.0f)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                        consumer.addVertex(poseStack.last().pose(), halfWidth, -halfHeight, 0.01f * i)
                            .setColor(colors[i].x, colors[i].y, colors[i].z, alpha).setUv(1.0f, 1.0f)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                        consumer.addVertex(poseStack.last().pose(), halfWidth, halfHeight, 0.01f * i)
                            .setColor(colors[i].x, colors[i].y, colors[i].z, alpha).setUv(1.0f, 0.0f)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                    }

                    poseStack.popPose();
                }

                poseStack.popPose();

            }

        }
    };

    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR2 = new IStaticSummonRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            if(!customData.contains("dirX") || !customData.contains("dirY") || !customData.contains("dirZ") || !customData.contains("seed")) return;
            Vec3 dir = new Vec3(customData.getDouble("dirX"), customData.getDouble("dirY"), customData.getDouble("dirZ")).normalize();
            long seed = customData.getLong("seed");
            int age = summon.getEntityData().get(StaticSummon.AGE);
            int lifetime = summon.getEntityData().get(StaticSummon.LIFETIME);
            if(lifetime <= 0) return;
            float lifeRatio = Mth.clamp((age + partialTick) / (float) lifetime, 0.0F, 1.0F);
            if(lifeRatio > 0.999f) return;
            float radius = PROJECTILE_HIT_LIGHTNING_RADIUS * (1.0f - lifeRatio);
            float alpha = 1.0f - lifeRatio;

            Vec3 entWorldPos = new Vec3(
                Mth.lerp(partialTick, summon.xo, summon.getX()),
                Mth.lerp(partialTick, summon.yo, summon.getY()),
                Mth.lerp(partialTick, summon.zo, summon.getZ())
            );
            VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.entityAdditiveEmissive(RES));
            Matrix4f matrix = poseStack.last().pose();

            Random rand0 = new Random(seed);
            Vec3 start0 = entWorldPos.add(dir.scale(4.0));
            Vec3 end0 = start0.add(dir.scale(PROJECTILE_HIT_LIGHTNING_LENGTH));
            List<Vec3> branch0 = generateBranch(start0, end0, rand0, PROJECTILE_HIT_LIGHTNING_DEPTH, PROJECTILE_HIT_LIGHTNING_JITTER);

            Random rand1 = new Random(seed + 1);
            Vec3 end1 = start0.add(spreadDir(dir, rand1, 0.4F).scale(PROJECTILE_HIT_LIGHTNING_LENGTH * 0.7F));
            List<Vec3> branch1 = generateBranch(start0, end1, rand1, PROJECTILE_HIT_LIGHTNING_DEPTH, PROJECTILE_HIT_LIGHTNING_JITTER);

            Random rand2 = new Random(seed + 2);
            Vec3 start2 = branch0.get(1);
            Vec3 end2 = start2.add(spreadDir(dir, rand2, 0.4F).scale(PROJECTILE_HIT_LIGHTNING_LENGTH * 0.5F));
            List<Vec3> branch2 = generateBranch(start2, end2, rand2, PROJECTILE_HIT_LIGHTNING_DEPTH, PROJECTILE_HIT_LIGHTNING_JITTER);

            for(List<Vec3> branch : List.of(branch0, branch1, branch2)) {
                renderTube(buffer, matrix, entWorldPos, branch, radius * 1.0F, alpha, 1,1,1, true);
                renderTube(buffer, matrix, entWorldPos, branch, radius * 1.25F, alpha * 0.5F, 0.7F,0.15F,0.55F, true);
                renderTube(buffer, matrix, entWorldPos, branch, radius * 0.75F, alpha * 0.2F, 0.5F,0.1F,0.4F, true);
            }
        }
    };

    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR3 = new IStaticSummonRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;

            Vec3 entWorldPos = new Vec3(
                Mth.lerp(partialTick, entity.xo, entity.getX()),
                Mth.lerp(partialTick, entity.yo, entity.getY()),
                Mth.lerp(partialTick, entity.zo, entity.getZ())
            );

            // 公告板：根据相机计算平面基向量
            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            Vec3 toCamera = camera.getPosition().subtract(entWorldPos).normalize();
            Vec3 up = new Vec3(0, 1, 0);
            Vec3 right = toCamera.cross(up).normalize();
            if(right.lengthSqr() < 0.01) right = new Vec3(1, 0, 0);
            Vec3 realUp = right.cross(toCamera).normalize();

            // 十字交叉的4个方向
            Vec3[] dirs = {
                realUp.add(right).normalize(),
                realUp.subtract(right).normalize(),
                realUp.scale(-1.0).add(right).normalize(),
                realUp.scale(-1.0).subtract(right).normalize()
            };

            float age = summon.getEntityData().get(StaticSummon.AGE) + partialTick;
            float lifetime = summon.getEntityData().get(StaticSummon.LIFETIME);
            float progress = age / lifetime;
            float alpha = 1.0f;
            float length = MARK_LENGTH + progress * MARK_LENGTH * 2;
            float radius = MARK_RADIUS + progress * MARK_RADIUS * 2;

            VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.entityAdditiveEmissive(RES));
            Matrix4f matrix = poseStack.last().pose();

            for(Vec3 dir : dirs) {
                List<Vec3> points = new ArrayList<>();
                points.add(entWorldPos);
                points.add(entWorldPos.add(dir.scale(length)));
                renderTube(buffer, matrix, entWorldPos, points, radius * 0.5f, alpha * 0.8f, 1.0f, 1.0f, 1.0f, true);
                renderTube(buffer, matrix, entWorldPos, points, radius, alpha, 0.8f, 0.176f, 0.78f, true);
            }
        }
    };

    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR4 = new IStaticSummonRenderBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            if(!customData.contains("seed")) return;
            long seed = customData.getLong("seed");
            int ageInt = summon.getEntityData().get(StaticSummon.AGE);
            int lifetime = summon.getEntityData().get(StaticSummon.LIFETIME);
            if(lifetime <= 0) return;
            float lifeRatio = Mth.clamp((ageInt + partialTick) / (float) lifetime, 0.0F, 1.0F);

            float lengthScale = Math.min(1.0F, lifeRatio * 2.0F);
            float length = MARK_LIGHTNING_LENGTH * lengthScale;
            if(length < 0.5F) return;

            float radius = MARK_LIGHTNING_RADIUS * (1.0F - lifeRatio);
            float alpha = 1.0F - lifeRatio;

            Vec3 entWorldPos = new Vec3(
                Mth.lerp(partialTick, summon.xo, summon.getX()),
                Mth.lerp(partialTick, summon.yo, summon.getY()),
                Mth.lerp(partialTick, summon.zo, summon.getZ())
            );
            VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.entityAdditiveEmissive(RES));
            Matrix4f matrix = poseStack.last().pose();

            Random rand = new Random(seed);
            float baseAngle = rand.nextFloat() * (float)Math.PI * 2;
            Vec3[] dirs = {
                new Vec3(Math.cos(baseAngle), 0, Math.sin(baseAngle)),
                new Vec3(Math.cos(baseAngle + Math.PI / 2), 0, Math.sin(baseAngle + Math.PI / 2)),
                new Vec3(Math.cos(baseAngle + Math.PI), 0, Math.sin(baseAngle + Math.PI)),
                new Vec3(Math.cos(baseAngle + Math.PI * 3 / 2), 0, Math.sin(baseAngle + Math.PI * 3 / 2))
            };

            for(int d = 0; d < 4; d++) {
                Random mainRand = new Random(seed + d);
                Vec3 dir = dirs[d];

                Vec3 start = entWorldPos;
                Vec3 end = start.add(dir.scale(length));
                List<Vec3> mainBranch = generateBranch(start, end, mainRand, MARK_LIGHTNING_DEPTH, MARK_LIGHTNING_JITTER);

                renderTube(buffer, matrix, entWorldPos, mainBranch, radius, alpha, 0.8f, 0.176f, 0.78f, true);
                renderTube(buffer, matrix, entWorldPos, mainBranch, radius * 0.5f, alpha * 0.8f, 1.0f, 1.0f, 1.0f, true);

                for(int s = 0; s < MARK_LIGHTNING_SUB_COUNT; s++) {
                    Random subRand = new Random(seed + d * 10 + s);
                    int idx = subRand.nextInt(mainBranch.size());
                    Vec3 branchPos = mainBranch.get(idx);
                    Vec3 subEnd = branchPos.add(spreadDir(dir, subRand, MARK_LIGHTNING_SUB_SPREAD).scale(length * MARK_LIGHTNING_SUB_LENGTH));
                    List<Vec3> subBranch = generateBranch(branchPos, subEnd, subRand, MARK_LIGHTNING_DEPTH, MARK_LIGHTNING_JITTER);

                    renderTube(buffer, matrix, entWorldPos, subBranch, radius, alpha, 0.8f, 0.176f, 0.78f, true);
                    renderTube(buffer, matrix, entWorldPos, subBranch, radius * 0.5f, alpha * 0.8f, 1.0f, 1.0f, 1.0f, true);
                }
            }
        }
    };


    public static void renderTube(VertexConsumer buffer, Matrix4f matrix, Vec3 entityWorldPos,
        List<Vec3> points, float radius, float alpha, float colorR, float colorG, float colorB, boolean linear) {
        int n = points.size();
        if(n < 2) return;

        Vector3f[][] oriDirs = new Vector3f[n][3];
        Vec3[] up = new Vec3[n], right = new Vec3[n];
        for(int i = 0;i < n;i++) {
            if(i == n - 1) oriDirs[i] = MathUtil.computeCoordinateSystem(points.get(n - 1).subtract(points.get(n - 2)).toVector3f(), 0);
            else oriDirs[i] = MathUtil.computeCoordinateSystem(points.get(i + 1).subtract(points.get(i)).toVector3f(), 0);
            up[i] = MathUtil.toVec3(oriDirs[i][1]);
            right[i] = MathUtil.toVec3(oriDirs[i][2]);
        }

        for(int i = 0;i < n - 1;i++) {
            float r0 = linear ? radius * (1.0F - (float) i / (float) (n - 1)) : radius;
            float r1 = linear ? radius * (1.0F - (float) (i + 1) / (float) (n - 1)) : radius;
            if(r0 <= 0.001F && r1 <= 0.001F) continue;
            Vec3 N = right[i], B = up[i], N1 = right[i + 1], B1 = up[i + 1];
            Vec3 P = points.get(i), P1 = points.get(i + 1);
            for(int j = 0;j < RING;j++) {
                float a0 = (float) (2.0 * Math.PI * j / RING);
                float a1 = (float) (2.0 * Math.PI * (j + 1) / RING);
                Vec3 d0 = computeRingDir(N, B, a0);
                Vec3 d1 = computeRingDir(N1, B1, a0);
                Vec3 d0b = computeRingDir(N, B, a1);
                Vec3 d1b = computeRingDir(N1, B1, a1);
                Vec3 v0 = P.add(d0.scale(r0));
                Vec3 v1 = P1.add(d1.scale(r1));
                Vec3 v2 = P1.add(d1b.scale(r1));
                Vec3 v3 = P.add(d0b.scale(r0));
                for(int k = 0;k < 3;k++) {
                    writeVert(buffer, matrix, entityWorldPos, v0, 0.5f, 0.5f, alpha, colorR, colorG, colorB);
                    writeVert(buffer, matrix, entityWorldPos, v1, 0.5f, 0.5f, alpha, colorR, colorG, colorB);
                    writeVert(buffer, matrix, entityWorldPos, v2, 0.5f, 0.5f, alpha, colorR, colorG, colorB);
                    writeVert(buffer, matrix, entityWorldPos, v3, 0.5f, 0.5f, alpha, colorR, colorG, colorB);
                }
            }
        }
    }

    public static Vec3 computeRingDir(Vec3 n, Vec3 b, float ang) {
        return n.scale(Math.cos(ang)).add(b.scale(Math.sin(ang)));
    }

    public static void writeVert(VertexConsumer buffer, Matrix4f matrix, Vec3 entityWorldPos,
        Vec3 worldPos, float u, float v, float alpha, float colorR, float colorG, float colorB) {
        double lx = worldPos.x - entityWorldPos.x;
        double ly = worldPos.y - entityWorldPos.y;
        double lz = worldPos.z - entityWorldPos.z;

        buffer.addVertex(matrix, (float)lx, (float)ly, (float)lz)
            .setColor(colorR, colorG, colorB, alpha)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT)
            .setNormal(0.0F, 1.0F, 0.0F);
    }

    //三棱柱采样空间：沿 X 轴挤出，YZ 平面是从剑根线性收窄到剑尖的等腰三角形。
    private static final float BLADE_HALF_THICKNESS = 0.05f;
    private static final float BLADE_LENGTH = 32.0f;
    private static final float BLADE_ROOT_HALF_WIDTH = 1.0f;
    private static final float BLADE_MAX_OUTLINE_PROTRUSION = BLADE_ROOT_HALF_WIDTH * 2.0f * 0.15f;
    private static final float BLADE_COLOR_TO_CORE_THICKNESS_RATIO = 1.0f / 3.0f;
    private static final float BLADE_AURA_WIDTH = 0.022f;
    private static final float BLADE_EXTINCTION = 4.0f;
    private static final Vector3f BLADE_EDGE_COLOR_0 = new Vector3f(0.725f, 0.345f, 1.0f);
    private static final Vector3f BLADE_EDGE_COLOR_1 = new Vector3f(0.847f, 0.247f, 0.745f);
    private static final Vector3f BLADE_EDGE_COLOR_2 = new Vector3f(0.882f, 0.345f, 0.247f);

    public static void renderEnergyWave(MultiBufferSource buffer, PoseStack poseStack, Entity entity) {
        ShaderInstance shader = ShaderRegistry.getDevilsDevastationEnergy();
        if (shader == null) return;

        poseStack.pushPose();

        GlStateBackup backup = new GlStateBackup();
        RenderSystem.backupGlState(backup);
        ShaderInstance prevShader = RenderSystem.getShader();

        try {
            if (buffer instanceof MultiBufferSource.BufferSource source) {
                source.endBatch();
            }

            poseStack.translate(0.0, 1.4, 0.2);
            Matrix4f modelMatrix = new Matrix4f(poseStack.last().pose());
            float partialTick = Minecraft.getInstance().getTimer()
                .getGameTimeDeltaPartialTick(false);
            float renderTime = entity.tickCount + partialTick;

            //注入 uniform
            Uniform modelMat = shader.getUniform("ModelMat");
            if (modelMat != null) modelMat.set(modelMatrix);
            Uniform cameraLocalUniform = shader.getUniform("CameraLocal");
            if (cameraLocalUniform != null) {
                Vector3f cameraLocal = new Matrix4f(RenderSystem.getModelViewMatrix())
                    .mul(modelMatrix)
                    .invert()
                    .transformPosition(new Vector3f());
                cameraLocalUniform.set(cameraLocal.x, cameraLocal.y, cameraLocal.z);
            }
            Uniform bladeDimensionsUniform = shader.getUniform("BladeDimensions");
            if (bladeDimensionsUniform != null) {
                bladeDimensionsUniform.set(BLADE_HALF_THICKNESS, BLADE_LENGTH, BLADE_ROOT_HALF_WIDTH);
            }
            Uniform volumeParametersUniform = shader.getUniform("VolumeParameters");
            if (volumeParametersUniform != null) {
                volumeParametersUniform.set(
                    BLADE_MAX_OUTLINE_PROTRUSION,
                    BLADE_COLOR_TO_CORE_THICKNESS_RATIO,
                    BLADE_AURA_WIDTH,
                    BLADE_EXTINCTION
                );
            }
            Uniform flowTimeUniform = shader.getUniform("FlowTime");
            if (flowTimeUniform != null) {
                flowTimeUniform.set(renderTime);
            }
            Uniform edgeColor0Uniform = shader.getUniform("EdgeColor0");
            if (edgeColor0Uniform != null) {
                edgeColor0Uniform.set(BLADE_EDGE_COLOR_0.x, BLADE_EDGE_COLOR_0.y, BLADE_EDGE_COLOR_0.z);
            }
            Uniform edgeColor1Uniform = shader.getUniform("EdgeColor1");
            if (edgeColor1Uniform != null) {
                edgeColor1Uniform.set(BLADE_EDGE_COLOR_1.x, BLADE_EDGE_COLOR_1.y, BLADE_EDGE_COLOR_1.z);
            }
            Uniform edgeColor2Uniform = shader.getUniform("EdgeColor2");
            if (edgeColor2Uniform != null) {
                edgeColor2Uniform.set(BLADE_EDGE_COLOR_2.x, BLADE_EDGE_COLOR_2.y, BLADE_EDGE_COLOR_2.z);
            }

            //GL 状态：加法混合 + 深度测试 + 背面剔除（代理三棱柱只需朝向相机的面）
            RenderSystem.setShader(() -> shader);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE
            );
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.enableCull();

            BufferBuilder bb = RenderSystem.renderThreadTesselator().begin(
                VertexFormat.Mode.TRIANGLES,
                DefaultVertexFormat.POSITION_COLOR
            );

            float auraPadding = BLADE_AURA_WIDTH * 3.0f;
            float faceAmplitude = BLADE_HALF_THICKNESS * 0.5f;
            float deformedRootHalfWidth = BLADE_ROOT_HALF_WIDTH + BLADE_MAX_OUTLINE_PROTRUSION;
            float proxyHalfThickness = BLADE_HALF_THICKNESS + faceAmplitude + auraPadding;
            float proxyRootHalfWidth = deformedRootHalfWidth + auraPadding;
            float proxyLength = BLADE_LENGTH * proxyRootHalfWidth / deformedRootHalfWidth;

            float x0 = -proxyHalfThickness;
            float x1 = proxyHalfThickness;
            float y0 = 0.0f;
            float y1 = proxyLength;
            float z0 = -proxyRootHalfWidth;
            float z1 = proxyRootHalfWidth;

            // -X 三角面
            addEnergyVertex(bb, x0, y0, z0);
            addEnergyVertex(bb, x0, y0, z1);
            addEnergyVertex(bb, x0, y1, 0.0f);

            // +X 三角面
            addEnergyVertex(bb, x1, y0, z0);
            addEnergyVertex(bb, x1, y1, 0.0f);
            addEnergyVertex(bb, x1, y0, z1);

            // 剑根矩形面（-Y），拆成两个三角形
            addEnergyVertex(bb, x0, y0, z0);
            addEnergyVertex(bb, x1, y0, z0);
            addEnergyVertex(bb, x1, y0, z1);
            addEnergyVertex(bb, x0, y0, z0);
            addEnergyVertex(bb, x1, y0, z1);
            addEnergyVertex(bb, x0, y0, z1);

            // -Z 斜面，拆成两个三角形
            addEnergyVertex(bb, x0, y0, z0);
            addEnergyVertex(bb, x0, y1, 0.0f);
            addEnergyVertex(bb, x1, y1, 0.0f);
            addEnergyVertex(bb, x0, y0, z0);
            addEnergyVertex(bb, x1, y1, 0.0f);
            addEnergyVertex(bb, x1, y0, z0);

            // +Z 斜面，拆成两个三角形
            addEnergyVertex(bb, x0, y0, z1);
            addEnergyVertex(bb, x1, y0, z1);
            addEnergyVertex(bb, x1, y1, 0.0f);
            addEnergyVertex(bb, x0, y0, z1);
            addEnergyVertex(bb, x1, y1, 0.0f);
            addEnergyVertex(bb, x0, y1, 0.0f);

            BufferUploader.drawWithShader(Objects.requireNonNull(bb.build()));
        } finally {
            poseStack.popPose();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.defaultBlendFunc();
            if (prevShader != null) {
                RenderSystem.setShader(() -> prevShader);
            }
            RenderSystem.restoreGlState(backup);
        }
    }

    public static void renderEnergyParticles(PoseStack poseStack, ItemDisplayContext displayContext, Player player) {
        if (player.getEffect(ModEffects.KILL_MODE) == null || player.getCooldowns().isOnCooldown(ModItems.DEVILS_DEVASTATION.get())) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        poseStack.pushPose();
        poseStack.translate(0.0, 1.4, 0.2);

        Matrix4f energyMatrix = poseStack.last().pose();

        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cameraWorldPos = camera.getPosition();
        Quaternionf cameraRotation = camera.rotation();
        boolean isFirstPerson = displayContext.firstPerson();

        net.minecraft.util.RandomSource random = player.getRandom();

        int sampleCount = 2;
        for (int i = 0; i < sampleCount; i++) {
            // 1. 采样能量体剑身内的位置点
            float localY = 0.5f + random.nextFloat() * 23.5f;
            float halfWidth = (1.0f - localY / 32.0f) * 0.8f;
            float localZ = (random.nextFloat() * 2.0f - 1.0f) * halfWidth;
            float localX = (random.nextFloat() * 2.0f - 1.0f) * 0.04f;

            // 局部坐标转为渲染空间坐标
            Vector4f localPos = new Vector4f(localX, localY, localZ, 1.0f);
            localPos.mul(energyMatrix);

            // 2. 解算粒子的绝对世界发射位置
            Vec3 actualWorldPos;
            if (isFirstPerson) {
                Vector3f viewOffset = new Vector3f(localPos.x(), localPos.y(), localPos.z());
                cameraRotation.transform(viewOffset);

                actualWorldPos = new Vec3(
                    cameraWorldPos.x + viewOffset.x(),
                    cameraWorldPos.y + viewOffset.y(),
                    cameraWorldPos.z + viewOffset.z()
                );
            } else {
                actualWorldPos = new Vec3(
                    cameraWorldPos.x + localPos.x(),
                    cameraWorldPos.y + localPos.y(),
                    cameraWorldPos.z + localPos.z()
                );
            }

            // 3. 计算粒子朝向：
            // 剑身中轴为局部 +Y 轴 (0, 1, 0)
            // 沿剑身延申为主方向 (Y=1.0)，在 X/Z 方向施加偏移倾角，使朝向与中轴呈约 12° ~ 24° 夹角
            float tilt = 0.22f + random.nextFloat() * 0.20f;
            float phi = random.nextFloat() * (float) (2 * Math.PI);
            float localDirX = (float) Math.cos(phi) * tilt * 0.35f;
            float localDirY = 1.0f;
            float localDirZ = (float) Math.sin(phi) * tilt;

            Vector3f localDir = new Vector3f(localDirX, localDirY, localDirZ).normalize();

            // 计算局部的侧向正交基向量 (right)，用于铁火花平面的广告牌计算
            Vector3f localRight = new Vector3f(-localDirZ, 0.0f, localDirX);
            if (localRight.lengthSquared() < 0.001f) localRight.set(1.0f, 0.0f, 0.0f);
            localRight.normalize();

            // 4. 将方向向量和基向量变换到视角空间
            Vector3f viewDir = energyMatrix.transformDirection(localDir, new Vector3f()).normalize();
            Vector3f viewRight = energyMatrix.transformDirection(localRight, new Vector3f()).normalize();

            // 5. 视角空间向量转换为世界空间向量
            Vector3f worldDir;
            Vector3f worldRight;
            if (isFirstPerson) {
                worldDir = cameraRotation.transform(viewDir, new Vector3f());
                worldRight = cameraRotation.transform(viewRight, new Vector3f());
            } else {
                worldDir = new Vector3f(viewDir);
                worldRight = new Vector3f(viewRight);
            }

            // 速度大小控制火花的移动速度与拉伸方向
            float speed = 0.18f + random.nextFloat() * 0.12f;
            worldDir.mul(speed);

            Vector3f[] energyColors = new Vector3f[]{
                new Vector3f(0.725f, 0.345f, 1.0f),
                new Vector3f(0.847f, 0.247f, 0.745f),
                new Vector3f(0.882f, 0.345f, 0.247f)
            };

            // 6. 生成铁火花粒子
            Vector3f color = energyColors[random.nextInt(energyColors.length)];
            IronSparkParticleOptions sparkOptions = new IronSparkParticleOptions(
                0.2f,                   // initSize (火花粗细)
                16 + random.nextInt(8),  // initLifetime (生命周期)
                3.5f,                    // sparkLength (拉伸长度倍率)
                color,                   // 颜色
                worldDir,                // 飞行初速度及延伸朝向
                worldRight               // 侧向基向量
            );
            mc.level.addParticle(
                sparkOptions,
                actualWorldPos.x, actualWorldPos.y, actualWorldPos.z,
                0.0, 0.0, 0.0
            );
        }

        poseStack.popPose();
    }

    private static void addEnergyVertex(
        BufferBuilder bb,
        float x, float y, float z
    ) {
        bb.addVertex(x, y, z)
            .setColor(255, 255, 255, 255);
    }
}
