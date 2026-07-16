package com.lzxnone.terraria.entity;

import java.lang.reflect.Method;

public class IrisCompat {
    private static final boolean IRIS_PRESENT;
    private static final Method GET_INSTANCE;
    private static final Method IS_SHADER_PACK;

    static {
        Method gi = null;
        Method issp = null;
        try {
            Class<?> cls = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            gi = cls.getMethod("getInstance");
            issp = cls.getMethod("isShaderPackInUse");
        } catch (Throwable ignored) {
        }
        GET_INSTANCE = gi;
        IS_SHADER_PACK = issp;
        IRIS_PRESENT = gi != null && issp != null;
    }

    public static boolean isShaderPackInUse() {
        if (!IRIS_PRESENT) {
            return false;
        }
        try {
            Object instance = GET_INSTANCE.invoke(null);
            if (instance == null) {
                return false;
            }
            Object result = IS_SHADER_PACK.invoke(instance);
            return result instanceof Boolean b ? b : false;
        } catch (Throwable t) {
            return false;
        }
    }
}
