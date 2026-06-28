package com.lzxnone.terraria.utils;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class MathUtil {
    public static Vector3f[] computeProjectileDir(Vector3f tempDir) {
        Vector3f dir = new Vector3f(tempDir.x, tempDir.y, tempDir.z).normalize();

        // 方向接近垂直时用 X 轴，否则用 Y 轴做参考向量
        Vector3f ref = Math.abs(dir.y) > 0.999f ? new Vector3f(1, 0, 0) : new Vector3f(0, 1, 0);

        Vector3f right = new Vector3f();
        ref.cross(dir, right).normalize();

        Vector3f up = new Vector3f();
        dir.cross(right, up).normalize();

        return new Vector3f[]{dir, up, right};
    }

    public static Vector3f toVector3f(Vec3 vec3) {
        return new Vector3f((float) vec3.x, (float) vec3.y, (float) vec3.z);
    }

    public static Vec3 toVec3(Vector3f vector3f) {
        return new Vec3(vector3f.x, vector3f.y, vector3f.z);
    }
}
