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
import net.minecraft.world.item.*;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import net.minecraft.client.Camera;
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
            poseStack.translate(-up.x * 0.5, -up.y * 0.5, -up.z * 0.5);
            poseStack.mulPose(rotationUp2);
            poseStack.mulPose(rotationDir);
            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
            poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-135));
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

    //采样空间（光线步进长方体）：x 厚度 ±0.25、y 长 0→32、z 宽 ±1
    private static final float BLADE_MIN_X = -0.25f;
    private static final float BLADE_MAX_X = 0.25f;
    private static final float BLADE_MIN_Y = 0.0f;
    private static final float BLADE_MAX_Y = 32.0f;
    private static final float BLADE_MIN_Z = -1.0f;
    private static final float BLADE_MAX_Z = 1.0f;

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

            //注入 uniform
            Uniform modelMat = shader.getUniform("ModelMat");
            if (modelMat != null) modelMat.set(modelMatrix);
            Uniform gameTime = shader.getUniform("EffectTime");
            if (gameTime != null) gameTime.set((float) entity.tickCount);
            Uniform colorA = shader.getUniform("ColorA");
            if (colorA != null) colorA.set(0.729f, 0.396f, 0.345f, 1.0f);
            Uniform colorB = shader.getUniform("ColorB");
            if (colorB != null) colorB.set(0.8f, 0.176f, 0.78f, 1.0f);

            //GL 状态：加法混合 + 深度测试 + 背面剔除（代理盒只需朝向相机的面）
            RenderSystem.setShader(() -> shader);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE
            );
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.enableCull();

            //加法混合下重复绘制 3 次 = 亮度叠加，补偿 LDR 无 bloom 时的亮度损失
            for (int pass = 0; pass < 3; pass++) {
                BufferBuilder bb = RenderSystem.renderThreadTesselator().begin(
                    VertexFormat.Mode.QUADS,
                    DefaultVertexFormat.NEW_ENTITY
                );
                addBox(
                    bb,
                    BLADE_MIN_X, BLADE_MIN_Y, BLADE_MIN_Z,
                    BLADE_MAX_X, BLADE_MAX_Y, BLADE_MAX_Z
                );
                BufferUploader.drawWithShader(Objects.requireNonNull(bb.build()));
            }
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

    private static void addBox(
        BufferBuilder bb,
        float minX, float minY, float minZ,
        float maxX, float maxY, float maxZ
    ) {
    // +X
    addQuad(bb,
        maxX, minY, minZ,
        maxX, maxY, minZ,
        maxX, maxY, maxZ,
        maxX, minY, maxZ,
        1, 0, 0
    );

    // -X
    addQuad(bb,
        minX, minY, maxZ,
        minX, maxY, maxZ,
        minX, maxY, minZ,
        minX, minY, minZ,
        -1, 0, 0
    );

    // +Y
    addQuad(bb,
        minX, maxY, maxZ,
        maxX, maxY, maxZ,
        maxX, maxY, minZ,
        minX, maxY, minZ,
        0, 1, 0
    );

    // -Y
    addQuad(bb,
        minX, minY, minZ,
        maxX, minY, minZ,
        maxX, minY, maxZ,
        minX, minY, maxZ,
        0, -1, 0
    );

    // +Z
    addQuad(bb,
        minX, minY, maxZ,
        maxX, minY, maxZ,
        maxX, maxY, maxZ,
        minX, maxY, maxZ,
        0, 0, 1
    );

    // -Z
    addQuad(bb,
        maxX, minY, minZ,
        minX, minY, minZ,
        minX, maxY, minZ,
        maxX, maxY, minZ,
        0, 0, -1
    );
}

    private static void addQuad(
        BufferBuilder bb,
        float x0, float y0, float z0,
        float x1, float y1, float z1,
        float x2, float y2, float z2,
        float x3, float y3, float z3,
        float nx, float ny, float nz
    ) {
        addVertex(bb, x0, y0, z0, nx, ny, nz);
        addVertex(bb, x1, y1, z1, nx, ny, nz);
        addVertex(bb, x2, y2, z2, nx, ny, nz);
        addVertex(bb, x3, y3, z3, nx, ny, nz);
    }

    private static void addVertex(
        BufferBuilder bb,
        float x, float y, float z,
        float nx, float ny, float nz
    ) {
        bb.addVertex(x, y, z)
            .setColor(255, 255, 255, 255)
            .setUv(0, 0)
            .setUv1(0, 0)
            .setUv2(0, 0)
            .setNormal(nx, ny, nz);
    }
}
