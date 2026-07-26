package com.lzxnone.terraria.client.item.melee;

import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.utils.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
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

    public static void renderTubeSegmented(VertexConsumer buffer, Matrix4f matrix, Vec3 entityWorldPos,
        List<Vec3> points, float[] radii,
        Vector3f[] colors, float alpha,
        float uvOffsetU, float uvOffsetV, float uvScaleU, float uvScaleV) {
        int n = points.size();
        if(n < 2 || radii.length < n || colors.length < n) return;

        Vector3f[][] oriDirs = new Vector3f[n][3];
        Vec3[] up = new Vec3[n], right = new Vec3[n];
        for(int i = 0;i < n;i++) {
            if(i == n - 1) oriDirs[i] = MathUtil.computeCoordinateSystem(points.get(n - 1).subtract(points.get(n - 2)).toVector3f(), 0);
            else oriDirs[i] = MathUtil.computeCoordinateSystem(points.get(i + 1).subtract(points.get(i)).toVector3f(), 0);
            up[i] = MathUtil.toVec3(oriDirs[i][1]);
            right[i] = MathUtil.toVec3(oriDirs[i][2]);
        }

        for(int i = 0;i < n - 1;i++) {
            float r0 = radii[i];
            float r1 = radii[i + 1];
            if(r0 <= 0.001F && r1 <= 0.001F) continue;
            Vec3 N = right[i], B = up[i], N1 = right[i + 1], B1 = up[i + 1];
            Vec3 P = points.get(i), P1 = points.get(i + 1);
            Vector3f c0 = colors[i], c1 = colors[i + 1];

            float u0 = uvOffsetU + uvScaleU * ((float)i / (float)(n - 1));
            float u1 = uvOffsetU + uvScaleU * ((float)(i + 1) / (float)(n - 1));

            for(int j = 0;j < RING;j++) {
                float a0 = (float) (2.0 * Math.PI * j / RING);
                float a1 = (float) (2.0 * Math.PI * (j + 1) / RING);

                float v0 = (float) j / (float)RING + uvOffsetV;
                float v1 = (float) (j + 1) / (float)RING + uvOffsetV;

                Vec3 d0 = computeRingDir(N, B, a0);
                Vec3 d1 = computeRingDir(N1, B1, a0);
                Vec3 d0b = computeRingDir(N, B, a1);
                Vec3 d1b = computeRingDir(N1, B1, a1);
                Vec3 v00 = P.add(d0.scale(r0));
                Vec3 v01 = P1.add(d1.scale(r1));
                Vec3 v02 = P1.add(d1b.scale(r1));
                Vec3 v03 = P.add(d0b.scale(r0));
                for(int k = 0;k < 10;k++) {
                    writeVert(buffer, matrix, entityWorldPos, v00, u0, v0, alpha, c0.x(), c0.y(), c0.z());
                    writeVert(buffer, matrix, entityWorldPos, v01, u1, v0, alpha, c1.x(), c1.y(), c1.z());
                    writeVert(buffer, matrix, entityWorldPos, v02, u1, v1, alpha, c1.x(), c1.y(), c1.z());
                    writeVert(buffer, matrix, entityWorldPos, v03, u0, v1, alpha, c0.x(), c0.y(), c0.z());
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

    public static void renderEnergyWave(MultiBufferSource buffer, PoseStack poseStack, Entity entity) {
        Matrix4f matrix = poseStack.last().pose();
        int time = entity.tickCount;

        poseStack.translate(0, 1.4, 0.6);

        float baseRadius = 1.0f;
        float speed = 0.25f;

        Vector3f colorA = new Vector3f(0.729f, 0.396f, 0.345f);
        Vector3f colorB = new Vector3f(0.8f, 0.176f, 0.78f);

        List<Vec3> points = new ArrayList<>();
        float[] outRadius = new float[ENERGY_WAVE_SEG + 1];
        Vector3f[] outColors = new Vector3f[ENERGY_WAVE_SEG + 1];

        for(int i = 0; i <= ENERGY_WAVE_SEG; i++) {
            float t = (float) i / ENERGY_WAVE_SEG;
            float y = t * ENERGY_WAVE_LENGTH;

            float phase = time * speed * 6f - t * (float) Math.PI * 5;
            float wave = (float) Math.sin(phase);

            points.add(new Vec3(0, y, 0));

            float taper = (float) Math.cos(t * Math.PI * 0.5f);
            float pulse = 0.7f + 0.3f * (0.5f + 0.5f * wave);
            outRadius[i] = baseRadius * taper * pulse;

            float blend = 0.5f + 0.5f * (float) Math.sin(time * 0.3f * speed - t * (float) Math.PI * 2);
            outColors[i] = new Vector3f(
                colorA.x + (colorB.x - colorA.x) * blend,
                colorA.y + (colorB.y - colorA.y) * blend,
                colorA.z + (colorB.z - colorA.z) * blend
            );
        }

        float[] inRadius = new float[ENERGY_WAVE_SEG + 1];
        Vector3f[] inColors = new Vector3f[ENERGY_WAVE_SEG + 1];
        for(int i = 0; i <= ENERGY_WAVE_SEG; i++) {
            inRadius[i] = outRadius[i] * 0.1f;
            inColors[i] = new Vector3f(1.0f, 1.0f, 1.0f);
        }
        VertexConsumer consumer2 = buffer.getBuffer(ModRenderTypes.entityAdditiveEmissive(RES));
        renderTubeSegmented(consumer2, matrix, new Vec3(0, 0, 0), points, inRadius, inColors, 1.0f,
            0, 0, 1.0f, 0.0f);

        float uOffset = time * 0.125f;
        float vOffset = time * 0.125f;
        VertexConsumer consumer = buffer.getBuffer(ModRenderTypes.entityAdditiveEmissive(RES4));
        for(int i = 0;i < 1;i++) {
            renderTubeSegmented(consumer, matrix, new Vec3(0, 0, 0), points, outRadius, outColors, 1.0f,
                uOffset, 0 + vOffset, 2.0f, 0.0f);
            renderTubeSegmented(consumer, matrix, new Vec3(0, 0, 0), points, outRadius, outColors, 1.0f,
                uOffset, 0.5f + vOffset, 2.0f, 0.0f);
        }
    }
}
