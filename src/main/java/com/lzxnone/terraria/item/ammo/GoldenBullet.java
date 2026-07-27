package com.lzxnone.terraria.item.ammo;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.ConfigUtil;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.utils.CollisionUtil;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class GoldenBullet extends BasicBulletAmmo {
    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";

    public static final String BASE_DAMAGE_PATH = "ammo.golden_bullet.base_damage";
    public static final float BASE_DAMAGE_DEFAULT = 1.5f;
    public static final float BASE_DAMAGE_MIN = 0.0f;
    public static final float BASE_DAMAGE_MAX = 8388600.0f;

    public static final String SPEED_PATH = "ammo.golden_bullet.speed";
    public static final double SPEED_DEFAULT = 3.0D;
    public static final double SPEED_MIN = 0.0D;
    public static final double SPEED_MAX = 24.0D;

    public static final String EFFECT_TIME_PATH = "ammo.golden_bullet.effect_time";
    public static final int EFFECT_TIME_DEFAULT = 40;
    public static final int EFFECT_TIME_MIN = 0;
    public static final int EFFECT_TIME_MAX = 72000;

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigFactory.loadFloatConfig(BASE_DAMAGE_PATH, configText("golden_bullet_base_damage"), configTooltip("golden_bullet_base_damage"), BASE_DAMAGE_DEFAULT, BASE_DAMAGE_MIN, BASE_DAMAGE_MAX);
            ConfigFactory.loadDoubleConfig(SPEED_PATH, configText("golden_bullet_speed"), configTooltip("golden_bullet_speed"), SPEED_DEFAULT, SPEED_MIN, SPEED_MAX);
            ConfigFactory.loadIntConfig(EFFECT_TIME_PATH, configText("golden_bullet_effect_time"), configTooltip("golden_bullet_effect_time"), EFFECT_TIME_DEFAULT, EFFECT_TIME_MIN, EFFECT_TIME_MAX);
        }
    };

    private static Component configText(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key);
    }

    private static Component configTooltip(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key + ".tooltip");
    }

    public static float getBaseDamage() {
        return Math.clamp(ConfigUtil.readFloat(BASE_DAMAGE_PATH, BASE_DAMAGE_DEFAULT), BASE_DAMAGE_MIN, BASE_DAMAGE_MAX);
    }

    public static double getSpeed() {
        return Math.clamp(ConfigUtil.readDouble(SPEED_PATH, SPEED_DEFAULT), SPEED_MIN, SPEED_MAX);
    }

    public static int getEffectTime() {
        return Math.clamp(ConfigUtil.readInt(EFFECT_TIME_PATH, EFFECT_TIME_DEFAULT), EFFECT_TIME_MIN, EFFECT_TIME_MAX);
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "golden_bullet",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/golden_bullet.png"),
        Component.translatable("item.lzxnoneterraria.golden_bullet"),
        CONFIG_DATA
    );

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            Vec3 motion = summon.getLookAngle().normalize().scale(getSpeed());
            summon.setDeltaMovement(motion);

            if(summon.level().isClientSide()) return;

            //碰撞检测
            AABB hitBox = new AABB(summon.position(), summon.position().add(motion)).inflate(0.25);
            List<Entity> targets = summon.level().getEntitiesOfClass(
                Entity.class,
                hitBox,
                FilterUtil.createTargetFilter(summon, summon.getOwner())
            );
            if(!targets.isEmpty()) {
                CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
                float damage = customData.contains("damage") ? customData.getFloat("damage") : 0;
                float knockbackScale = customData.contains("knockbackScale") ? customData.getFloat("knockbackScale") : 1.0f;
                int invulnerableTime = customData.contains("invulnerableTime") ? customData.getInt("invulnerableTime") : 20;

                ItemStack sourceStack = summon.getEntityData().get(StaticSummon.STACK_SOURCE);
                Entity target = targets.getFirst();
                if(DamageUtil.rangedAttack(summon, target, sourceStack, getBaseDamage() + damage, knockbackScale)) {
                    target.invulnerableTime = invulnerableTime;
                    if(target instanceof LivingEntity livingEntity) {
                        MobEffectInstance effectInstance = new MobEffectInstance(ModEffects.MIDAS, getEffectTime(), 0);
                        livingEntity.addEffect(effectInstance);
                    }
                    this.onDied(summon);
                }
            }

            //方块检测
            BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, summon.position().add(motion));
            if(blockHitResult.getType() != HitResult.Type.MISS) {
                this.onDied(summon);
            }
        }
    };
}
