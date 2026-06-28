package com.lzxnone.terraria.item;

import com.lzxnone.terraria.LzxnoneTerraria;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID)
public class ModItemEvents {
    @SubscribeEvent
    public static void onLeftClickAir(PlayerInteractEvent.LeftClickEmpty event) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(event.getEntity().getMainHandItem().getItem());
        if(id.getNamespace().equals("lzxnoneterraria")) ItemWaveBehaviors.getBehavior(id.getPath()).onLeftClickAir(event);
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(event.getEntity().getMainHandItem().getItem());
        if(id.getNamespace().equals("lzxnoneterraria")) ItemWaveBehaviors.getBehavior(id.getPath()).onLeftClickBlock(event);
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(event.getEntity().getMainHandItem().getItem());
        if(id.getNamespace().equals("lzxnoneterraria")) ItemWaveBehaviors.getBehavior(id.getPath()).onAttackEntity(event);
    }
}
