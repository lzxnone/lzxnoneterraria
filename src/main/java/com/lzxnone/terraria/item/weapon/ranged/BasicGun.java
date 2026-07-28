package com.lzxnone.terraria.item.weapon.ranged;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.ModItemTags;
import com.lzxnone.terraria.item.weapon.RangedWeapon;
import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.ConfigUtil;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.utils.AmmoUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class BasicGun extends RangedWeapon {
    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";
    private static final float DAMAGE_MIN = 0.0f;
    private static final float DAMAGE_MAX = 8388600.0f;
    private static final float AMMO_NOT_CONSUME_CHANCE_MIN = 0.0f;
    private static final float AMMO_NOT_CONSUME_CHANCE_MAX = 1.0f;

    private final String id;
    private final Vector3f offset;
    private final float damageDefault;
    private final boolean hasAmmoNotConsumeChance;
    private final float ammoNotConsumeChanceDefault;
    private final int useTime;
    private final Supplier<SoundEvent> sound;
    private final float knockbackScale;
    private final int invulnerableTime;

    public BasicGun(Tier tier, Properties properties, String id, Vector3f offset, float damageDefault, int useTime,
                    Supplier<SoundEvent> sound, float knockbackScale, int invulnerableTime) {
        this(tier, properties, id, offset, damageDefault, false, 0.0f, useTime, sound, knockbackScale, invulnerableTime);
    }

    public BasicGun(Tier tier, Properties properties, String id, Vector3f offset, float damageDefault, float ammoNotConsumeChanceDefault,
                    int useTime, Supplier<SoundEvent> sound, float knockbackScale, int invulnerableTime) {
        this(tier, properties, id, offset, damageDefault, true, ammoNotConsumeChanceDefault, useTime, sound, knockbackScale, invulnerableTime);
    }

    private BasicGun(Tier tier, Properties properties, String id, Vector3f offset, float damageDefault, boolean hasAmmoNotConsumeChance,
                     float ammoNotConsumeChanceDefault, int useTime, Supplier<SoundEvent> sound,
                     float knockbackScale, int invulnerableTime) {
        super(tier, properties);
        this.id = id;
        this.offset = offset;
        this.damageDefault = damageDefault;
        this.hasAmmoNotConsumeChance = hasAmmoNotConsumeChance;
        this.ammoNotConsumeChanceDefault = ammoNotConsumeChanceDefault;
        this.useTime = useTime;
        this.sound = sound;
        this.knockbackScale = knockbackScale;
        this.invulnerableTime = invulnerableTime;
    }

    public static IConfigData createConfigData(String id, float damageDefault) {
        return new IConfigData() {
            @Override
            public void onConfigLoad() {
                ConfigFactory.loadFloatConfig(damagePath(id), configText(id + "_damage"), configTooltip(id + "_damage"), damageDefault, DAMAGE_MIN, DAMAGE_MAX);
            }
        };
    }

    public static IConfigData createConfigData(String id, float damageDefault, float ammoNotConsumeChanceDefault) {
        return new IConfigData() {
            @Override
            public void onConfigLoad() {
                ConfigFactory.loadFloatConfig(ammoNotConsumeChancePath(id), configText(id + "_ammo_not_consume_chance"), configTooltip(id + "_ammo_not_consume_chance"), ammoNotConsumeChanceDefault, AMMO_NOT_CONSUME_CHANCE_MIN, AMMO_NOT_CONSUME_CHANCE_MAX);
                ConfigFactory.loadFloatConfig(damagePath(id), configText(id + "_damage"), configTooltip(id + "_damage"), damageDefault, DAMAGE_MIN, DAMAGE_MAX);
            }
        };
    }

    public static ConfigListItem createConfigListItem(String id, IConfigData configData) {
        return new ConfigListItem(
            id,
            ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/" + id + ".png"),
            Component.translatable("item.lzxnoneterraria." + id),
            configData
        );
    }

    private static String damagePath(String id) {
        return "weapon." + id + ".damage";
    }

    private static String ammoNotConsumeChancePath(String id) {
        return "weapon." + id + ".ammo_not_consume_chance";
    }

    private static Component configText(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key);
    }

    private static Component configTooltip(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key + ".tooltip");
    }

    public float getDamage() {
        return Math.clamp(ConfigUtil.readFloat(damagePath(id), damageDefault), DAMAGE_MIN, DAMAGE_MAX);
    }

    public float getAmmoNotConsumeChance() {
        if(!hasAmmoNotConsumeChance) return 0.0f;
        return Math.clamp(
            ConfigUtil.readFloat(ammoNotConsumeChancePath(id), ammoNotConsumeChanceDefault),
            AMMO_NOT_CONSUME_CHANCE_MIN,
            AMMO_NOT_CONSUME_CHANCE_MAX
        );
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(player.getCooldowns().isOnCooldown(stack.getItem()) || !tryShoot(level, player, hand, stack)) return InteractionResultHolder.fail(stack);
        player.startUsingItem(hand);
        player.getCooldowns().addCooldown(stack.getItem(), Math.max(1, getUseTime(stack, player) / 3));
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public boolean canUseAmmo(ItemStack weaponStack, ItemStack ammoStack) {
        return ammoStack.is(ModItemTags.BULLET_AMMO);
    }

    @Override
    public int getUseTime(ItemStack weaponStack, LivingEntity entity) {
        return useTime;
    }

    @Override
    public int getAmmoConsumeAmount(ItemStack weaponStack, LivingEntity entity) {
        if(!hasAmmoNotConsumeChance) return super.getAmmoConsumeAmount(weaponStack, entity);
        return entity.getRandom().nextFloat() < getAmmoNotConsumeChance() ? 0 : 1;
    }

    @Override
    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        SoundUtil.playClientSound(player, sound.get());
        if(!level.isClientSide()) {
            StaticSummon summon = AmmoUtil.createAmmoSummon(level, player, hand, stack, offset);
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            customData.putFloat("damage", getDamage());
            customData.putFloat("knockbackScale", knockbackScale);
            customData.putInt("invulnerableTime", invulnerableTime);
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
            level.addFreshEntity(summon);
        }
    }
}
