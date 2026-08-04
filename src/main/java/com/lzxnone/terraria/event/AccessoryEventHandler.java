package com.lzxnone.terraria.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.item.accessory.AccessoryUtil;
import com.lzxnone.terraria.item.effect.FireBlockImmunityModifier;
import com.lzxnone.terraria.item.effect.MobEffectImmunityModifier;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
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
        removeImmuneEffects(entity);
    }

    public static boolean isImmuneToEffect(LivingEntity entity, Holder<MobEffect> effect) {
        boolean[] result = {false};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(result[0]) return;
            if(accessory instanceof MobEffectImmunityModifier modifier) {
                if(effect == MobEffects.POISON && modifier.isPoisonImmunity(stack, entity)
                    || effect == MobEffects.WITHER && modifier.isWitherImmunity(stack, entity)
                    || effect == MobEffects.DARKNESS && modifier.isDarknessImmunity(stack, entity)
                    || effect == MobEffects.MOVEMENT_SLOWDOWN && modifier.isSlownessImmunity(stack, entity)
                    || effect == MobEffects.WEAKNESS && modifier.isWeaknessImmunity(stack, entity)
                    || effect == ModEffects.BLOOD_BUTCHERED && modifier.isBleedImmunity(stack, entity)
                    || effect == ModEffects.CONFUSED && modifier.isConfusedImmunity(stack, entity)
                    || effect == MobEffects.LEVITATION && modifier.isLevitationImmunity(stack, entity)
                    || effect == MobEffects.DIG_SLOWDOWN && modifier.isMiningFatigueImmunity(stack, entity)) {
                    result[0] = true;
                }
            }
        });
        return result[0];
    }

    public static void removeImmuneEffects(LivingEntity entity) {
        if(entity.hasEffect(MobEffects.POISON) && isImmuneToEffect(entity, MobEffects.POISON)) {
            entity.removeEffect(MobEffects.POISON);
        }
        if(entity.hasEffect(MobEffects.WITHER) && isImmuneToEffect(entity, MobEffects.WITHER)) {
            entity.removeEffect(MobEffects.WITHER);
        }
        if(entity.hasEffect(MobEffects.DARKNESS) && isImmuneToEffect(entity, MobEffects.DARKNESS)) {
            entity.removeEffect(MobEffects.DARKNESS);
        }
        if(entity.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) && isImmuneToEffect(entity, MobEffects.MOVEMENT_SLOWDOWN)) {
            entity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        }
        if(entity.hasEffect(MobEffects.WEAKNESS) && isImmuneToEffect(entity, MobEffects.WEAKNESS)) {
            entity.removeEffect(MobEffects.WEAKNESS);
        }
        if(entity.hasEffect(ModEffects.BLOOD_BUTCHERED) && isImmuneToEffect(entity, ModEffects.BLOOD_BUTCHERED)) {
            entity.removeEffect(ModEffects.BLOOD_BUTCHERED);
        }
        if(entity.hasEffect(ModEffects.CONFUSED) && isImmuneToEffect(entity, ModEffects.CONFUSED)) {
            entity.removeEffect(ModEffects.CONFUSED);
        }
        if(entity.hasEffect(MobEffects.LEVITATION) && isImmuneToEffect(entity, MobEffects.LEVITATION)) {
            entity.removeEffect(MobEffects.LEVITATION);
        }
        if(entity.hasEffect(MobEffects.DIG_SLOWDOWN) && isImmuneToEffect(entity, MobEffects.DIG_SLOWDOWN)) {
            entity.removeEffect(MobEffects.DIG_SLOWDOWN);
        }
    }

    public static boolean isFireBlockImmune(LivingEntity entity) {
        boolean[] result = {false};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(result[0]) return;
            if(accessory instanceof FireBlockImmunityModifier modifier) {
                result[0] = modifier.isFireBlockImmunity(stack, entity);
            }
        });
        return result[0];
    }
}
