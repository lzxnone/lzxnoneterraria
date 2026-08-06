package com.lzxnone.terraria.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.accessory.AccessoryItem;
import com.lzxnone.terraria.item.accessory.AccessoryUtil;
import com.lzxnone.terraria.item.effect.ConditionalAttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;
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
        //脱下条件属性饰品时，移除其全部动态属性
        ItemStack from = event.getFrom();
        if(from.getItem() instanceof AccessoryItem accessory && accessory instanceof ConditionalAttributeModifier modifier) {
            modifier.getAllConditionalModifiers(from, entity).forEach((attribute, attributeModifier) -> {
                AttributeInstance instance = entity.getAttribute(attribute);
                if(instance != null) instance.removeModifier(attributeModifier.id());
            });
        }
    }
}
