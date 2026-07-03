package com.lzxnone.terraria.utils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.joml.Vector3f;

public class MathUtil {
    public static Vector3f[] computeDir(Vector3f tempDir) {
        Vector3f dir = new Vector3f(tempDir.x, tempDir.y, tempDir.z).normalize();

        // 方向接近垂直时用 X 轴，否则用 Y 轴做参考向量
        Vector3f ref = Math.abs(dir.y) > 0.999f ? new Vector3f(1, 0, 0) : new Vector3f(0, 1, 0);

        Vector3f right = new Vector3f();
        ref.cross(dir, right).normalize();

        Vector3f up = new Vector3f();
        dir.cross(right, up).normalize();

        return new Vector3f[]{dir, up, right};
    }

    public static Vector3f[] computeDir(Vector3f tempDir, Vector3f tempRight) {
        Vector3f dir = new Vector3f(tempDir.x, tempDir.y, tempDir.z).normalize();
        Vector3f right = new Vector3f(tempRight.x, tempRight.y, tempRight.z).normalize();

        Vector3f up = new Vector3f();
        dir.cross(right, up).normalize();

        up.cross(dir, right).normalize();

        return new Vector3f[]{dir, up, right};
    }

    public static float[] computeXYRot(Vector3f dir) {
        float xzLen = (float) Math.sqrt(dir.x() * dir.x() + dir.z() * dir.z());
        float pitch = (float) (Math.atan2(-dir.y(), xzLen) * (180.0 / Math.PI));
        float yaw = (float) (Math.atan2(-dir.x(), dir.z()) * (180.0 / Math.PI));
        return new float[]{pitch, yaw};
    }

    public static Vec3 getRandomPosForRadius(Vec3 pos, double radius, boolean on) {
        double u = Math.random();
        double v = Math.random();
        double w = Math.random();

        double r = on ? radius : radius * Math.cbrt(u);
        double cosPhi = 2.0 * v - 1.0;
        double sinPhi = Math.sqrt(1.0 - cosPhi * cosPhi);
        double theta = w * Math.PI * 2.0;

        double offsetX = r * sinPhi * Math.cos(theta);
        double offsetY = r * cosPhi;
        double offsetZ = r * sinPhi * Math.sin(theta);

        return new Vec3(pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ);
    }

    public static Vec3 getRandomPosOnRadius(Vec3 pos, double radius) {
        return getRandomPosForRadius(pos, radius, true);
    }

    public static Vec3 getRandomPosInRadius(Vec3 pos, double radius) {
        return getRandomPosForRadius(pos, radius, false);
    }

    public static Vec3 getCrosshairPos(Player player, Level level, double range, boolean checkBlock, boolean checkEntity) {
        Vec3 eyePos = player.getEyePosition(1.0F);
        Vec3 lookVec = player.getLookAngle().normalize();
        Vec3 maxRangeEnd = eyePos.add(lookVec.scale(range));
        Vec3 finalTarget = maxRangeEnd;

        if(checkBlock) {
            BlockHitResult blockHit = level.clip(new ClipContext(
                    eyePos, maxRangeEnd,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    player
            ));

            if(blockHit.getType() != HitResult.Type.MISS) {
                finalTarget = blockHit.getLocation();
            }
        }

        if(checkEntity) {
            AABB searchBox = player.getBoundingBox().expandTowards(lookVec.scale(range)).inflate(1.0D);
            EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                    level, player, eyePos, finalTarget, searchBox,
                    entity -> !entity.isSpectator() && entity.isPickable() && entity != player
            );

            if(entityHit != null) {
                finalTarget = entityHit.getLocation();
            }
        }

        return finalTarget;
    }

    public static Vec3 getCrosshairPos(Player player, Level level, double range) {
        return getCrosshairPos(player, level, range, true, true);
    }

    public static Vector3f toVector3f(Vec3 vec3) {
        return new Vector3f((float) vec3.x, (float) vec3.y, (float) vec3.z);
    }

    public static Vec3 toVec3(Vector3f vector3f) {
        return new Vec3(vector3f.x, vector3f.y, vector3f.z);
    }
}
