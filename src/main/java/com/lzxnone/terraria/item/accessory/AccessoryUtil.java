package com.lzxnone.terraria.item.accessory;

import com.google.common.collect.Multimap;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.entity.summon.BeeSummon;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.effect.*;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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

    //同步条件限定饰品的动态属性：按当前生效集合与全集 diff（状态切换/值变化才增删，平时只读零开销）
    public static void syncConditionalModifiers(LivingEntity entity) {
        forEachAccessory(entity, (accessory, stack) -> {
            if(!(accessory instanceof ConditionalAttributeModifier modifier)) return;
            Multimap<Holder<Attribute>, AttributeModifier> current = modifier.getConditionalModifiers(stack, entity);
            modifier.getAllConditionalModifiers(stack, entity).forEach((attribute, attributeModifier) -> {
                AttributeInstance instance = entity.getAttribute(attribute);
                if(instance == null) return;
                AttributeModifier existing = instance.getModifier(attributeModifier.id());
                if(current.containsEntry(attribute, attributeModifier)) {
                    if(existing == null) {
                        instance.addTransientModifier(attributeModifier);
                    }else if(existing.amount() != attributeModifier.amount() || existing.operation() != attributeModifier.operation()) {
                        instance.addOrUpdateTransientModifier(attributeModifier);
                    }
                }else if(existing != null) {
                    instance.removeModifier(attributeModifier.id());
                }
            });
        });
    }

    //耐魔性持续时间比例：取所有饰品中的最小值（降低优先）
    public static double getManaSicknessDurationMultiplier(LivingEntity entity) {
        double[] multiplier = {1.0D};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(accessory instanceof ManaSicknessDurationModifier modifier) {
                multiplier[0] = Math.min(multiplier[0], modifier.getManaSicknessDurationMultiplier(stack, entity));
            }
        });
        return multiplier[0];
    }

    //星星拾取范围：取所有饰品中的最大值
    public static double getStarPickupRange(LivingEntity entity) {
        double[] range = {0.0D};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(accessory instanceof StarPickupRangeModifier modifier) {
                range[0] = Math.max(range[0], modifier.getStarPickupRange(stack, entity));
            }
        });
        return range[0];
    }

    //受击魔力恢复倍率：取所有饰品中的最大值（与天界手铐等效果不叠加）
    public static double getManaOnHurtMultiplier(LivingEntity entity) {
        double[] multiplier = {0.0D};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(accessory instanceof ManaOnHurtModifier modifier) {
                multiplier[0] = Math.max(multiplier[0], modifier.getManaOnHurtMultiplier(stack, entity));
            }
        });
        return multiplier[0];
    }

    //魔力恢复延迟倍率：取所有饰品中的最小值（减半优先）
    public static double getManaRecoverDelayMultiplier(LivingEntity entity) {
        double[] multiplier = {1.0D};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(accessory instanceof ManaRegenerationModifier modifier) {
                multiplier[0] = Math.min(multiplier[0], modifier.getManaRecoverDelayMultiplier(stack, entity));
            }
        });
        return multiplier[0];
    }

    //额外魔力恢复速率：累加所有饰品
    public static double getManaRegenBonus(LivingEntity entity, int currentMana, int maxMana) {
        double[] bonus = {0.0D};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(accessory instanceof ManaRegenerationModifier modifier) {
                bonus[0] += modifier.getManaRegenBonus(stack, entity, currentMana, maxMana);
            }
        });
        return bonus[0];
    }

    //最大魔力加成：累加所有饰品的加成
    public static int getMaxManaBonus(LivingEntity entity) {
        int[] bonus = {0};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(accessory instanceof MaxManaModifier modifier) {
                bonus[0] += modifier.getMaxManaBonus(stack, entity);
            }
        });
        return bonus[0];
    }

    //每秒生命回复量：累加所有饰品的生命再生加成
    public static float getHealthPerSecond(LivingEntity entity) {
        float[] amount = {0.0F};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(accessory instanceof LifeRegenerationModifier modifier) {
                amount[0] += modifier.getHealthPerSecond(stack, entity);
            }
        });
        return amount[0];
    }

    //岩浆免疫时长（刻）：取所有饰品中的最大值
    public static int getLavaImmunityTicks(LivingEntity entity) {
        int[] ticks = {0};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(accessory instanceof LavaImmunityModifier modifier) {
                ticks[0] = Math.max(ticks[0], modifier.getLavaImmunityTicks(stack, entity));
            }
        });
        return ticks[0];
    }

    //是否佩戴恐慌类饰品（受到攻击后触发加速）
    public static boolean canApplyPanic(LivingEntity entity) {
        boolean[] result = {false};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(accessory instanceof PanicModifier modifier && modifier.canApplyPanic(stack, entity)) {
                result[0] = true;
            }
        });
        return result[0];
    }

    //岩浆伤害减免倍率：取所有饰品中的最大值
    public static double getLavaDamageReduction(LivingEntity entity) {
        double[] reduction = {0.0D};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(accessory instanceof LavaDamageReductionModifier modifier) {
                reduction[0] = Math.max(reduction[0], modifier.getLavaDamageReduction(stack, entity));
            }
        });
        return reduction[0];
    }

    public static double applyDamageImmunity(LivingEntity entity) {
        double[] chance = {0.0D};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(accessory instanceof DamageImmunityModifier modifier) {
                chance[0] = Math.max(modifier.getDamageImmunityChance(stack, entity), chance[0]);
            }
        });
        return chance[0];
    }

    public static double getVisibilityMultiplier(LivingEntity entity) {
        double[] multiplier = {1.0D};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(accessory instanceof VisibilityModifier modifier) {
                multiplier[0] *= modifier.getVisibilityMultiplier(stack, entity);
            }
        });
        return multiplier[0];
    }

    public static double getGuardianRange(LivingEntity entity) {
        double[] range = {0.0D};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(accessory instanceof GuardianShieldModifier modifier) {
                if(modifier.canApplyGuardianShield(stack, entity)) range[0] = Math.max(modifier.getGuardianRange(stack, entity), range[0]);
            }
        });
        return range[0];
    }

    public static double getGuardianMinHealthRatio(LivingEntity entity) {
        double[] ratio = {1.0D};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(accessory instanceof GuardianShieldModifier modifier) {
                if(modifier.canApplyGuardianShield(stack, entity)) ratio[0] = Math.min(modifier.getMinHealthRatio(stack, entity), ratio[0]);
            }
        });
        return ratio[0];
    }

    public static boolean canApplyGuardianShield(LivingEntity entity) {
        boolean[] result = {false};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(result[0]) return;
            if(accessory instanceof GuardianShieldModifier modifier) {
                result[0] = modifier.canApplyGuardianShield(stack, entity);
            }
        });
        return result[0];
    }

    public static double getIceBarrierMaxHealthRatio(LivingEntity entity) {
        double[] ratio = {1.0D};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(accessory instanceof IceBarrierModifier modifier) {
                if(modifier.canApplyIceBarrier(stack, entity)) ratio[0] = Math.max(modifier.getMaxHealthRatio(stack, entity), ratio[0]);
            }
        });
        return ratio[0];
    }

    public static boolean canApplyIceBarrier(LivingEntity entity) {
        boolean[] result = {false};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(result[0]) return;
            if(accessory instanceof IceBarrierModifier modifier) {
                result[0] = modifier.canApplyIceBarrier(stack, entity);
            }
        });
        return result[0];
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

    //为目标挂载护甲/护甲韧性减小修饰符（返回是否实际挂载了）
    public static boolean applyTargetArmorModifiers(LivingEntity attacker, LivingEntity target) {
        double[] armorReduction = {0.0D};
        double[] toughnessReduction = {0.0D};
        AccessoryUtil.forEachAccessory(attacker, (accessory, stack) -> {
            if(accessory instanceof TargetArmorModifier modifier) {
                armorReduction[0] += modifier.getTargetArmorReduction(stack, attacker, target);
                toughnessReduction[0] += modifier.getTargetArmorToughnessReduction(stack, attacker, target);
            }
        });

        boolean applied = false;
        if(armorReduction[0] > 0.0D) {
            AttributeInstance armor = target.getAttribute(Attributes.ARMOR);
            if(armor != null) {
                armor.addTransientModifier(new AttributeModifier(TARGET_ARMOR_PIERCE_ID, -armorReduction[0], AttributeModifier.Operation.ADD_VALUE));
                applied = true;
            }
        }
        if(toughnessReduction[0] > 0.0D) {
            AttributeInstance toughness = target.getAttribute(Attributes.ARMOR_TOUGHNESS);
            if(toughness != null) {
                toughness.addTransientModifier(new AttributeModifier(TARGET_ARMOR_TOUGHNESS_PIERCE_ID, -toughnessReduction[0], AttributeModifier.Operation.ADD_VALUE));
                applied = true;
            }
        }
        return applied;
    }

    //移除为目标挂载的护甲/护甲韧性减小修饰符（幂等）
    public static void removeTargetArmorModifiers(LivingEntity target) {
        AttributeInstance armor = target.getAttribute(Attributes.ARMOR);
        if(armor != null) armor.removeModifier(TARGET_ARMOR_PIERCE_ID);
        AttributeInstance toughness = target.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if(toughness != null) toughness.removeModifier(TARGET_ARMOR_TOUGHNESS_PIERCE_ID);
    }

    private static final ResourceLocation TARGET_ARMOR_PIERCE_ID = ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "target_armor_pierce");
    private static final ResourceLocation TARGET_ARMOR_TOUGHNESS_PIERCE_ID = ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "target_armor_toughness_pierce");

    //近战暴击率：累加所有饰品的近战暴击率加成
    public static double getMeleeCritChance(LivingEntity entity) {
        double[] chance = {0.0D};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(accessory instanceof MeleeCritChanceModifier modifier) {
                chance[0] += modifier.getMeleeCritChance(stack, entity);
            }
        });
        return chance[0];
    }

    //瞄准放大：是否有饰品提供放大能力（如步枪瞄准镜）
    public static boolean hasScopeZoom(LivingEntity entity) {
        boolean[] result = {false};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(result[0]) return;
            if(accessory instanceof ScopeZoomModifier modifier) {
                result[0] = modifier.canZoom(stack, entity);
            }
        });
        return result[0];
    }

    //水下呼吸：是否有饰品提供水下呼吸
    public static boolean isWaterBreathing(LivingEntity entity) {
        boolean[] result = {false};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(result[0]) return;
            if(accessory instanceof WaterBreathingModifier modifier) {
                result[0] = modifier.canBreatheUnderwater(stack, entity);
            }
        });
        return result[0];
    }

    //暴击率：累加所有饰品的暴击率加成
    public static double getCritChance(LivingEntity entity) {
        double[] chance = {0.0D};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(accessory instanceof CriticalStrikeModifier modifier) {
                chance[0] += modifier.getCritChance(stack, entity);
            }
        });
        return chance[0];
    }

    //岩浆石：能否对命中目标施加着火
    public static boolean canApplyIgnite(LivingEntity entity) {
        boolean[] result = {false};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(result[0]) return;
            if(accessory instanceof IgniteOnHitModifier modifier) {
                result[0] = modifier.canApplyIgnite(stack, entity);
            }
        });
        return result[0];
    }

    //岩浆石：着火持续时间（刻）
    public static int getIgniteTicks(LivingEntity entity) {
        int[] ticks = {0};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(ticks[0] > 0) return;
            if(accessory instanceof IgniteOnHitModifier modifier && modifier.canApplyIgnite(stack, entity)) {
                ticks[0] = modifier.getIgniteTicks(stack, entity);
            }
        });
        return ticks[0];
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
        boolean[] summonManaStar = {false};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(result[0]) return;
            if(accessory instanceof FallenStarSummoner summoner) {
                result[0] = summoner.summonThreeFallenStar(stack, entity);
                if(summoner.summonManaStarOnLanding(stack, entity)) summonManaStar[0] = true;
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
                if(summonManaStar[0] && i == 0) customData.putBoolean("summonManaStar", true);
                projectile.getEntityData().set(StaticProjectile.CUSTOM_DATA, customData);

                projectile.setDeltaMovement(MathUtil.toVec3(dirs[0]));
                entity.level().addFreshEntity(projectile);
            }
        }
    }


    public static void applyBeeSummoner(LivingEntity entity) {
        int[] maxCount = {0};
        double[] damage = {0.0D};
        AccessoryUtil.forEachAccessory(entity, (accessory, stack) -> {
            if(accessory instanceof BeeSummoner summoner) {
                maxCount[0] = Math.max(summoner.getBeeSummonMaxCount(stack, entity), maxCount[0]);
                damage[0] = Math.max(summoner.getBeeSummonDamage(stack, entity), damage[0]);
            }
        });
        int count = maxCount[0] > 0 ? entity.level().random.nextInt(maxCount[0]) + 1 : 0;
        while(count-- > 0) {
            BeeSummon bee = ModEntities.BEE_SUMMON.get().create(entity.level());
            if(bee != null) {
                bee.owner = entity;
                bee.stackSource = ItemStack.EMPTY;
                bee.damage = damage[0];
                bee.setPos(new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ()));
                entity.level().addFreshEntity(bee);
            }
        }
    }
}
