package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.item.weapon.MeleeWeapon;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import com.lzxnone.terraria.utils.DamageUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import net.minecraft.world.item.enchantment.Enchantments;

public class TrueCopperShortsword extends MeleeWeapon {
    public static final String ID = "true_copper_shortsword";
    public static final ConfigFloat BASE_MELEE_DAMAGE = createBaseMeleeDamageConfig(ID, 20F);
    public static final ConfigFloat BASE_MELEE_ATTACK_SPEED = createBaseMeleeAttackSpeedConfig(ID, -2.4F);

    public static final IConfigData CONFIG_DATA = createConfigData(BASE_MELEE_DAMAGE, BASE_MELEE_ATTACK_SPEED);
    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);
    public TrueCopperShortsword() {
        super(Tiers.NETHERITE, new Item.Properties().fireResistant().rarity(Rarity.EPIC));
    }

    @Override
    public float getTooltipDamage(ItemStack weaponStack, LivingEntity entity) {
        float damage = Zenith.DAMAGE.get();
        //锋利附魔
        int sharpnessLevel = getEnchantmentLevel(entity, weaponStack, Enchantments.SHARPNESS);
        if(sharpnessLevel > 0) {
            damage += 1.0F + Math.max(0, sharpnessLevel - 1) * 0.5F;
        }
        //药水
        if(entity instanceof Player player) damage = DamageUtil.applyPlayerDamageEffects(player, damage);
        //近战加成
        return MeleeWeapon.applyMeleeDamageBonus(weaponStack, entity, damage);
    }

    @Override
    protected float getBaseMeleeDamage(ItemStack stack) {
        return BASE_MELEE_DAMAGE.get();
    }

    @Override
    protected float getBaseMeleeAttackSpeed(ItemStack stack) {
        return BASE_MELEE_ATTACK_SPEED.get();
    }

    public static void summon(Player player, double deltaDist) {
        Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
        int randomAngle = (int) ((Math.random() * 2 - 1) * Zenith.getTrailOffset());
        dirs = MathUtil.rotateCoordinateSystem(dirs[0], dirs[2], randomAngle);

        Vec3 pos = (player.getBoundingBox().getCenter()).add(MathUtil.toVec3(dirs[0]).scale(-2));
        double dist;
        if(Zenith.isDistanceMode()) {
            Vec3 targetPos = MathUtil.getCrosshairPos(player, player.level(), Zenith.getMaxRange());
            dist = Math.max(0, Math.min(targetPos.subtract(pos).length() + deltaDist, Zenith.getMaxRange()));
        }else {
            dist = deltaDist;
        }
        int cycle = (int) Math.max(10, dist / Zenith.getMaxRange() * Zenith.getCycle());
        double a = Math.max(dist / 2, 2);
        double b = Math.min(Math.random() * Zenith.getTrailB() + Zenith.getTrailB(), a / 2);
        double w = Math.PI * 2 / (double) cycle;
        if(player.getRandom().nextInt(2) == 0) w = -w;

        for(int i = 0;i < Zenith.getWeaponCount();i++) {
            StaticProjectile projectile = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), player.level());
            projectile.setOwner(player);
            projectile.getEntityData().set(StaticProjectile.STACK_SOURCE, player.getWeaponItem().copy());

            projectile.setPos(pos);
            projectile.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.ZENITH_PROJECTILE);
            projectile.getEntityData().set(StaticProjectile.RENDER_MODE, "custom");
            projectile.getEntityData().set(StaticProjectile.ITEM, new ItemStack(ModItems.COPPER_SHORTSWORD.get()));
            projectile.getEntityData().set(StaticProjectile.ORIGIN, pos.toVector3f());
            projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
            projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
            projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);
            projectile.getEntityData().set(StaticProjectile.COLOR_R, 0.922f);
            projectile.getEntityData().set(StaticProjectile.COLOR_G, 0.651f);
            projectile.getEntityData().set(StaticProjectile.COLOR_B, 0.529f);
            projectile.getEntityData().set(StaticProjectile.RXP, -90);
            projectile.getEntityData().set(StaticProjectile.RZP, -135);
            projectile.getEntityData().set(StaticProjectile.GLOW, true);

            CompoundTag customData = new CompoundTag();
            customData.putDouble("a", a);
            customData.putDouble("b", b);
            customData.putDouble("w", w);
            customData.putInt("angle", randomAngle);
            if(i == 0) {
                projectile.getEntityData().set(StaticProjectile.COLOR_A, 1.0f);
                projectile.getEntityData().set(StaticProjectile.SCALE_X, (float) Zenith.getScale());
                projectile.getEntityData().set(StaticProjectile.SCALE_Y, (float) Zenith.getScale());
                projectile.getEntityData().set(StaticProjectile.LIFETIME, cycle);
                projectile.getEntityData().set(StaticProjectile.EXPRESSION_X, String.format("%.3f*cos(%.3ft-1.571)", b, w));
                projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*sin(%.3ft-1.571)+%.3f", a, w, a));
                customData.putInt("start", 0);
            }else if(i == 1) {
                projectile.getEntityData().set(StaticProjectile.COLOR_A, 0.75f);
                projectile.getEntityData().set(StaticProjectile.SCALE_X, (float) Zenith.getScale() * 0.75f);
                projectile.getEntityData().set(StaticProjectile.SCALE_Y, (float) Zenith.getScale() * 0.75f);
                projectile.getEntityData().set(StaticProjectile.LIFETIME, (int) (cycle * 1.25));
                if(w > 0) {
                    projectile.getEntityData().set(StaticProjectile.EXPRESSION_X, String.format("%.3f*cos(%.3ft-3.142)", b, w));
                    projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*sin(%.3ft-3.142)+%.3f", a, w, a));
                }else {
                    projectile.getEntityData().set(StaticProjectile.EXPRESSION_X, String.format("%.3f*cos(%.3ft)", b, w));
                    projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*sin(%.3ft)+%.3f", a, w, a));
                }
                customData.putInt("start", (int) (cycle * 0.25));
            }else if(i == 2) {
                projectile.getEntityData().set(StaticProjectile.COLOR_A, 0.5f);
                projectile.getEntityData().set(StaticProjectile.SCALE_X, (float) Zenith.getScale() * 0.5f);
                projectile.getEntityData().set(StaticProjectile.SCALE_Y, (float) Zenith.getScale() * 0.5f);
                projectile.getEntityData().set(StaticProjectile.LIFETIME, (int) (cycle * 1.5));
                if(w > 0) {
                    projectile.getEntityData().set(StaticProjectile.EXPRESSION_X, String.format("%.3f*cos(%.3ft-4.713)", b, w));
                    projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*sin(%.3ft-4.713)+%.3f", a, w, a));
                }else {
                    projectile.getEntityData().set(StaticProjectile.EXPRESSION_X, String.format("%.3f*cos(%.3ft+1.571)", b, w));
                    projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*sin(%.3ft+1.571)+%.3f", a, w, a));
                }
                customData.putInt("start", (int) (cycle * 0.5));
            }
            customData.putInt("idx", i);
            projectile.getEntityData().set(StaticProjectile.CUSTOM_DATA, customData);

            float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);
            projectile.setXRot(xyRot[0]);
            projectile.setYRot(xyRot[1]);
            player.level().addFreshEntity(projectile);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(player.getCooldowns().isOnCooldown(stack.getItem()) || !tryShoot(level, player, hand, stack)) return InteractionResultHolder.fail(stack);
        player.startUsingItem(hand);
        player.getCooldowns().addCooldown(stack.getItem(), Math.max(1, getUseTime() / 3));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int count) {
        if(!(livingEntity instanceof Player player)) return;
        if(!shouldShootThisTick(stack, livingEntity, count)) return;

        if(!tryShoot(level, player, player.getUsedItemHand(), stack)) {
            player.stopUsingItem();
        }
    }

    public boolean shouldShootThisTick(ItemStack weaponStack, LivingEntity entity, int remainingUseTicks) {
        int useTime = Math.max(1, getUseTime());
        int elapsedMinecraftTicks = getUseDuration(weaponStack, entity) - remainingUseTicks;
        if(elapsedMinecraftTicks <= 0) return false;

        int currentShot = elapsedMinecraftTicks * 3 / useTime;
        int previousShot = (elapsedMinecraftTicks - 1) * 3 / useTime;
        return currentShot > previousShot;
    }

    public boolean tryShoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        shoot(level, player, hand, stack);
        return true;
    }

    public void shoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        SoundUtil.playClientSound(player, ModSounds.WAVE.get());
        if(level.isClientSide()) return;
        double deltaDist = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
            .copyTag().getDouble("deltaDist");
        summon(player, deltaDist);
    }

    public int getUseTime() {
        return 10;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public @NonNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }
}


