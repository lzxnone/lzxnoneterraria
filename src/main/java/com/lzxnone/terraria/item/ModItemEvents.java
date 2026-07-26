package com.lzxnone.terraria.item;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.weapon.melee.*;
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
        if(id.getNamespace().equals("lzxnoneterraria")) getBehavior(id.getPath()).onLeftClickAir(event);
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(event.getEntity().getMainHandItem().getItem());
        if(id.getNamespace().equals("lzxnoneterraria")) getBehavior(id.getPath()).onLeftClickBlock(event);
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(event.getEntity().getMainHandItem().getItem());
        if(id.getNamespace().equals("lzxnoneterraria")) getBehavior(id.getPath()).onAttackEntity(event);
    }

    private static IItemWaveBehavior getBehavior(String id) {
        return switch(id) {
            case "bee_keeper" -> BeeKeeper.ITEM_WAVE_BEHAVIOR;
            case "the_horsemans_blade" -> TheHorsemansBlade.ITEM_WAVE_BEHAVIOR;
            case "terra_blade" -> TerraBlade.ITEM_WAVE_BEHAVIOR;
            case "true_excalibur" -> TrueExcalibur.ITEM_WAVE_BEHAVIOR;
            case "true_nights_edge" -> TrueNightsEdge.ITEM_WAVE_BEHAVIOR;
            case "excalibur" -> Excalibur.ITEM_WAVE_BEHAVIOR;
            case "nights_edge" -> NightsEdge.ITEM_WAVE_BEHAVIOR;
            case "lights_bane" -> LightsBane.ITEM_WAVE_BEHAVIOR;
            case "blood_butcherer" -> BloodButcherer.ITEM_WAVE_BEHAVIOR;
            case "muramasa" -> Muramasa.ITEM_WAVE_BEHAVIOR;
            case "volcano" -> Volcano.ITEM_WAVE_BEHAVIOR;
            case "devils_devastation" -> DevilsDevastation.ITEM_WAVE_BEHAVIOR;
            default -> new IItemWaveBehavior() {};
        };
    }
}
