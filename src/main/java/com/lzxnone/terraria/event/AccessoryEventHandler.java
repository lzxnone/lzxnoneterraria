package com.lzxnone.terraria.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.accessory.AccessoryUtil;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID)
public class AccessoryEventHandler {
    @SubscribeEvent
    public static void onCurioChange(CurioChangeEvent event) {
        LivingEntity entity = event.getEntity();
        if(entity.level().isClientSide()) return;
        AccessoryUtil.removeImmuneEffects(entity);
    }
}
