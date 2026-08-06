package com.lzxnone.terraria.item.effect;

import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

public interface ConditionalAttributeModifier {
    Multimap<Holder<Attribute>, AttributeModifier> getConditionalModifiers(ItemStack stack, LivingEntity entity);

    default Multimap<Holder<Attribute>, AttributeModifier> getAllConditionalModifiers(ItemStack stack, LivingEntity entity) {
        return getConditionalModifiers(stack, entity);
    }
}
