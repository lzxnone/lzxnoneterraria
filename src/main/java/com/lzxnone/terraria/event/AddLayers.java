package com.lzxnone.terraria.event;
/*
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.renderer.StuckDevilsDevastationProjectileRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID, value = Dist.CLIENT)
public class AddLayers {
    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        // 以给原版僵尸添加为例
        LivingEntityRenderer renderer = event.getRenderer(EntityType.ZOMBIE);
        if (renderer != null) {
            renderer.addLayer(new StuckDevilsDevastationProjectileRenderer<>(event.getContext(), renderer));
        }

        // 如果想给玩家添加，需要分别获取 default 和 slim 模型
        LivingEntityRenderer playerRenderer = event.getSkin("default");
        if (playerRenderer != null) {
            playerRenderer.addLayer(new StuckDevilsDevastationProjectileRenderer<>(event.getContext(), playerRenderer));
        }
    }
}
*/