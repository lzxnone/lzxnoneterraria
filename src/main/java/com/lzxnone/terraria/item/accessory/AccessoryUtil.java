package com.lzxnone.terraria.item.accessory;

import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.effect.FallenStarSummoner;
import com.lzxnone.terraria.item.effect.FireBlockImmunityModifier;
import com.lzxnone.terraria.item.effect.FreezingImmunityModifier;
import com.lzxnone.terraria.item.effect.InvulnerableTimeModifier;
import com.lzxnone.terraria.item.effect.MobEffectImmunityModifier;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.function.BiConsumer;

public class AccessoryUtil {
    public static void forEachAccessory(LivingEntity entity, BiConsumer<AccessoryItem, ItemStack> consumer) {
        CuriosApi.getCuriosInventory(entity)
            .flatMap(handler -> handler.getStacksHandler("terraria_accessory"))
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

    public static boolean isFreezeImmune(LivingEntity entity) {
        boolean[] result = {false};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(result[0]) return;
            result[0] = accessory instanceof FreezingImmunityModifier modifier && modifier.isFreezingImmunity(stack, entity);
        });
        return result[0];
    }

    public static void applyInvulnerableTimeModifier(LivingEntity entity) {
        double finalTime = entity.invulnerableTime;
        double[] rate = {1.0D};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(accessory instanceof InvulnerableTimeModifier modifier) {
                rate[0] *= modifier.getInvulnerableTimeMultiplier(stack, entity);
            }
        });
        finalTime *= rate[0];
        entity.invulnerableTime = (int) finalTime;
    }

    public static void applyFallenStarSummoner(LivingEntity entity) {
        boolean[] result = {false};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(result[0]) return;
            if(accessory instanceof FallenStarSummoner summoner) {
                result[0] = summoner.summonThreeFallenStar(stack, entity);
            }
        });
        if(result[0]) {
            SoundUtil.playServerSound(entity.level(), ModSounds.STAR_FALL.get(), entity.position());
            for(int i = 0;i < 3;i++) {
                Vec3 targetPos = entity.position();

                Vec3 spawnPos = new Vec3(
                        targetPos.x + 5.0 * (Math.random() * 2 - 1),
                        targetPos.y + 12,
                        targetPos.z + 5.0 * (Math.random() * 2 - 1)
                );

                Vector3f[] dirs = MathUtil.computeCoordinateSystem(new Vec3(targetPos.x - spawnPos.x, targetPos.y - spawnPos.y, targetPos.z - spawnPos.z).toVector3f(), 0);

                StaticProjectile projectile = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), entity.level());
                projectile.setOwner(entity);
                projectile.setPos(spawnPos);
                projectile.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.ACCESSORY_FALLEN_STAR_PROJECTILE);
                projectile.getEntityData().set(StaticProjectile.RENDER_MODE, "custom");
                projectile.getEntityData().set(StaticProjectile.ORIGIN, MathUtil.toVector3f(spawnPos));
                projectile.getEntityData().set(StaticProjectile.ITEM, new ItemStack(ModItems.WHITE_STAR.get()));
                projectile.getEntityData().set(StaticProjectile.LIFETIME, 300);
                projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
                projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
                projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);
                projectile.getEntityData().set(StaticProjectile.GLOW, true);
                projectile.getEntityData().set(StaticProjectile.RXP, 90);
                projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*t", 2.0));

                CompoundTag customData = new CompoundTag();
                customData.putInt("targetLifetime", (int) Math.floor(targetPos.distanceTo(spawnPos) / 2.0));
                projectile.getEntityData().set(StaticProjectile.CUSTOM_DATA, customData);

                projectile.setDeltaMovement(MathUtil.toVec3(dirs[0]));
                entity.level().addFreshEntity(projectile);
            }
        }
    }
}
