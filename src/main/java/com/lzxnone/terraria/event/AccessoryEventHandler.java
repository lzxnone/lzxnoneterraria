package com.lzxnone.terraria.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.attachment.LavaImmunity;
import com.lzxnone.terraria.attachment.ModAttachments;
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
        //curios 在实体卸载/玩家登出时也会触发该事件，此时 entity 为 null
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
        //岩浆免疫上限：穿戴/脱下都刷新为当前所有饰品最大值，当前值收敛到上限内（不破坏自恢复机制）
        LavaImmunity lavaImmunity = entity.getData(ModAttachments.LAVA_IMMUNITY);
        lavaImmunity.setMaxTicks(AccessoryUtil.getLavaImmunityTicks(entity));
        lavaImmunity.setCurrentTicks(Math.min(lavaImmunity.getMaxTicks(), lavaImmunity.getCurrentTicks()));
    }
}
