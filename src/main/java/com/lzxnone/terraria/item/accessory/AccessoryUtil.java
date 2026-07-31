package com.lzxnone.terraria.item.accessory;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.function.BiConsumer;

public class AccessoryUtil {
    public static final String TERRARIA_ACCESSORY_SLOT = "terraria_accessory";

    public static void forEachAccessory(LivingEntity entity, BiConsumer<AccessoryItem, ItemStack> consumer) {
        CuriosApi.getCuriosInventory(entity)
            .flatMap(handler -> handler.getStacksHandler(TERRARIA_ACCESSORY_SLOT))
            .ifPresent(stacksHandler -> {
                IDynamicStackHandler stacks = stacksHandler.getStacks();
                for(int i = 0; i < stacks.getSlots(); i++) {
                    ItemStack stack = stacks.getStackInSlot(i);
                    if(stack.getItem() instanceof AccessoryItem accessory) {
                        consumer.accept(accessory, stack);
                    }
                }
            });
    }
}
