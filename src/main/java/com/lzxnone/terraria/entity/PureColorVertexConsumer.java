package com.lzxnone.terraria.entity;

import com.mojang.blaze3d.vertex.VertexConsumer;

public class PureColorVertexConsumer implements VertexConsumer {
    private final VertexConsumer vertexConsumer;
    private final float r;
    private final float g;
    private final float b;
    private final float a;

    public PureColorVertexConsumer(VertexConsumer vertexConsumer, float r, float g, float b, float a) {
        this.vertexConsumer = vertexConsumer;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    @Override
    public VertexConsumer addVertex(float v, float v1, float v2) {
        vertexConsumer.addVertex(v, v1, v2);
        return this;
    }

    @Override
    public VertexConsumer setColor(int i, int i1, int i2, int i3) {
        vertexConsumer.setColor((int) (255 * r), (int) (255 * g), (int) (255 * b), (int) (i3 * a));
        return this;
    }

    @Override
    public VertexConsumer setUv(float v, float v1) {
        vertexConsumer.setUv(v, v1);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int i, int i1) {
        vertexConsumer.setUv1(i, i1);
        return this;
    }

    @Override
    public VertexConsumer setUv2(int i, int i1) {
        vertexConsumer.setUv2(i, i1);
        return this;
    }

    @Override
    public VertexConsumer setNormal(float v, float v1, float v2) {
        vertexConsumer.setNormal(v, v1, v2);
        return this;
    }
}
