package com.lzxnone.terraria.item.weapon.ranged.gun;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.enchantment.ModEnchantmentConfigs;
import com.lzxnone.terraria.enchantment.ModEnchantments;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.ModItemTags;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.weapon.RangedWeapon;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.AmmoUtil;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class Gun extends RangedWeapon {
    private static final float DAMAGE_MIN = 0.0f;
    private static final float DAMAGE_MAX = 8388600.0f;
    private static final float AMMO_NOT_CONSUME_CHANCE_MIN = 0.0f;
    private static final float AMMO_NOT_CONSUME_CHANCE_MAX = 1.0f;
    public static final double SPRINT_SPREAD_MULTIPLIER = 2.0D;
    public static final double WALK_SPREAD_MULTIPLIER = 1.5D;
    public static final double CROUCH_SPREAD_MULTIPLIER = 0.5D;
    public static final Vector3f DEFAULT_OFFSET = new Vector3f(-0.3f, -0.15f, 1.5f);

    public Gun(Tier tier, Properties properties) {
        super(tier, properties);
    }

    public static IConfigData createConfigData(ConfigStruct... configs) {
        return new IConfigData() {
            @Override
            public void onConfigLoad() {
                ConfigStruct.loadAll(configs);
            }
        };
    }

    public static IConfigData createConfigData(String id, float damageDefault) {
        return createConfigData(createDamageConfig(id, damageDefault));
    }

    public static IConfigData createConfigData(String id, float damageDefault, float ammoNotConsumeChanceDefault) {
        return createConfigData(
            createDamageConfig(id, damageDefault),
            createAmmoNotConsumeChanceConfig(id, ammoNotConsumeChanceDefault)
        );
    }

    public static ConfigFloat createDamageConfig(String id, float damageDefault) {
        return new ConfigFloat(damagePath(id), id + "_damage", damageDefault, DAMAGE_MIN, DAMAGE_MAX);
    }

    public static ConfigFloat createAmmoNotConsumeChanceConfig(String id, float ammoNotConsumeChanceDefault) {
        return new ConfigFloat(ammoNotConsumeChancePath(id), id + "_ammo_not_consume_chance", ammoNotConsumeChanceDefault, AMMO_NOT_CONSUME_CHANCE_MIN, AMMO_NOT_CONSUME_CHANCE_MAX);
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

    @Override
    public boolean canUseAmmo(ItemStack weaponStack, ItemStack ammoStack) {
        return ammoStack.is(ModItemTags.BULLET_AMMO);
    }

    @Override
    protected ResourceLocation getDefaultAmmo(ItemStack weaponStack) {
        return ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "musket_ball");
    }

    @Override
    public int getUseTime(ItemStack weaponStack, LivingEntity entity) {
        return 20;
    }

    @Override
    public int getAmmoConsumeAmount(ItemStack weaponStack, LivingEntity entity) {
        float chance = getAmmoNotConsumeChance(weaponStack, entity);
        if(chance <= 0.0f) return super.getAmmoConsumeAmount(weaponStack, entity);
        return entity.getRandom().nextFloat() < chance ? 0 : 1;
    }

    protected float getDamage(ItemStack stack, Player player) {
        return 0.0f;
    }

    @Override
    public float getTooltipDamage(ItemStack weaponStack, LivingEntity entity) {
        Player player = entity instanceof Player playerEntity ? playerEntity : null;
        ItemStack ammoStack = getTooltipAmmoStack(weaponStack, entity);
        float damage = getDamage(weaponStack, player) + getAmmoBaseDamage(ammoStack);
        if(player != null) damage = DamageUtil.applyPlayerDamageEffects(player, damage);
        return applyRangedDamageBonus(weaponStack, entity, damage);
    }

    protected ItemStack getTooltipAmmoStack(ItemStack weaponStack, LivingEntity entity) {
        ItemStack ammoStack = getAmmoStack(weaponStack);
        if(ammoStack.is(ModItems.ENDLESS_MUSKET_POUCH.get())) return ModItems.MUSKET_BALL.get().getDefaultInstance();
        return ammoStack;
    }

    protected float getAmmoNotConsumeChance(ItemStack stack, LivingEntity entity) {
        return 0.0f;
    }

    protected Vector3f getOffset(ItemStack stack, Player player) {
        return DEFAULT_OFFSET;
    }

    protected SoundEvent getShootSound(ItemStack stack, Player player) {
        return ModSounds.SHOT.get();
    }

    protected float getKnockbackScale(ItemStack stack, Player player) {
        return 0.1f;
    }

    protected int getInvulnerableTime(ItemStack stack, Player player) {
        return 10;
    }

    protected int getProjectileCount(ItemStack stack, Player player) {
        return 1;
    }

    protected float getSpreadDegrees(ItemStack stack, Player player, int index) {
        return 0.0f;
    }

    protected ItemStack getBulletAmmoStack(Level level, Player player, InteractionHand hand, ItemStack weaponStack) {
        return getAmmoStack(weaponStack);
    }

    protected void postprocessBulletSummon(Level level, Player player, InteractionHand hand, ItemStack weaponStack,
                                           StaticSummon summon, int index) {
    }

    protected float getFinalSpreadDegrees(float baseSpread, ItemStack weaponStack, Player player) {
        double spread = baseSpread;
        if(player.isSprinting()) {
            spread *= SPRINT_SPREAD_MULTIPLIER;
        }else if(player.getDeltaMovement().horizontalDistanceSqr() > 1.0E-4D) {
            spread *= WALK_SPREAD_MULTIPLIER;
        }
        if(player.isShiftKeyDown()) {
            spread *= CROUCH_SPREAD_MULTIPLIER;
        }

        int steadyBreathLevel = getEnchantmentLevel(player, weaponStack, ModEnchantments.STEADY_BREATH);
        if(steadyBreathLevel > 0) {
            spread *= Math.pow(ModEnchantmentConfigs.getSteadyBreathSpreadMultiplier(), steadyBreathLevel);
        }
        return (float) spread;
    }

    protected void applySpread(Player player, StaticSummon summon, float spreadDegrees) {
        if(spreadDegrees <= 0.0f) return;

        Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
        Vec3 forward = MathUtil.toVec3(dirs[0]).normalize();
        Vec3 up = MathUtil.toVec3(dirs[1]).normalize();
        Vec3 right = MathUtil.toVec3(dirs[2]).normalize();

        double yaw = Math.toRadians((player.getRandom().nextFloat() * 2.0f - 1.0f) * spreadDegrees);
        double pitch = Math.toRadians((player.getRandom().nextFloat() * 2.0f - 1.0f) * spreadDegrees);
        Vec3 spreadDir = forward
            .add(right.scale(Math.tan(yaw)))
            .add(up.scale(Math.tan(pitch)))
            .normalize();

        float[] xyRot = MathUtil.computeXYRot(spreadDir.toVector3f());
        summon.setXRot(xyRot[0]);
        summon.xRotO = xyRot[0];
        summon.setYRot(xyRot[1]);
        summon.yRotO = xyRot[1];
    }

    protected StaticSummon createBulletSummon(Level level, Player player, InteractionHand hand, ItemStack stack, int index) {
        ItemStack ammoStack = getBulletAmmoStack(level, player, hand, stack);
        StaticSummon summon = AmmoUtil.createAmmoSummon(level, player, hand, stack, ammoStack, getOffset(stack, player));
        applySpread(player, summon, getSpreadDegrees(stack, player, index));

        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
        customData.putFloat("damage", getDamage(stack, player));
        customData.putFloat("knockbackScale", getKnockbackScale(stack, player));
        customData.putInt("invulnerableTime", getInvulnerableTime(stack, player));
        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

        postprocessBulletSummon(level, player, hand, stack, summon, index);
        return summon;
    }

    @Override
    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        SoundUtil.playClientSound(player, getShootSound(stack, player));
        if(level.isClientSide()) return;

        int projectileCount = getProjectileCount(stack, player);
        for(int i = 0; i < projectileCount; i++) {
            StaticSummon summon = createBulletSummon(level, player, hand, stack, i);
            level.addFreshEntity(summon);
        }
    }

}

