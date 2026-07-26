package com.lzxnone.terraria.client.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID, value = Dist.CLIENT)
public class ScreenShakeHandler {
    private static int shakeTime = 0;
    private static int maxShakeTime = 0;
    private static float strength = 0.0f;

    public static void startShake(int duration, float power) {
        shakeTime = Math.max(shakeTime, duration);
        maxShakeTime = Math.max(maxShakeTime, duration);
        strength = Math.max(strength, power);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if(shakeTime <= 0) return;

        shakeTime--;
        if(shakeTime <= 0) {
            maxShakeTime = 0;
            strength = 0.0f;
        }
    }

    @SubscribeEvent
    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if(shakeTime <= 0 || maxShakeTime <= 0) return;

        float progress = shakeTime / (float) maxShakeTime;
        float power = strength * progress * progress;
        double time = System.nanoTime() / 1_000_000_000.0;

        float yawOffset = (float) Math.sin(time * 80.0) * power;
        float pitchOffset = (float) Math.cos(time * 95.0) * power;
        float rollOffset = (float) Math.sin(time * 70.0) * power * 0.6f;

        event.setYaw(event.getYaw() + yawOffset);
        event.setPitch(event.getPitch() + pitchOffset);
        event.setRoll(event.getRoll() + rollOffset);
    }
}
