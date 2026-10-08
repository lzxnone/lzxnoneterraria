package com.lzxnone.terraria.client.item.summon;

import com.lzxnone.terraria.client.entity.summon.IStaticSummonRenderBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import net.minecraft.util.Mth;
import org.joml.Vector4f;

public class ClientKaleidoscope extends ClientWhip {
    public static final IStaticSummonRenderBehavior SUMMON_BEHAVIOR = new ClientKaleidoscope();

    @Override
    protected Vector4f getVertexColor(StaticSummon summon, float progressAlongWhip, float partialTick) {
        float time = summon.tickCount + partialTick;
        // 沿鞭身呈现完整彩虹色谱，并随时间平滑流动
        float hue = (progressAlongWhip + time * 0.04F) % 1.0F;
        int rgb = Mth.hsvToRgb(hue, 0.85F, 1.0F);
        float r = ((rgb >> 16) & 255) / 255.0F;
        float g = ((rgb >> 8) & 255) / 255.0F;
        float b = (rgb & 255) / 255.0F;
        float a = summon.getEntityData().get(StaticSummon.COLOR_A);
        return new Vector4f(r, g, b, a);
    }
}
