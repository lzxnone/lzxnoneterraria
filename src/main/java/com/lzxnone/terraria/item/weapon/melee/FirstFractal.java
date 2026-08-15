package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.item.weapon.MeleeWeapon;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.summon.*;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.particle.ZenithTrailParticleOptions;
import com.lzxnone.terraria.utils.*;
import com.mojang.math.Axis;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigBoolean;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;

import java.util.List;
import java.util.UUID;
import net.minecraft.world.item.enchantment.Enchantments;

public class FirstFractal extends MeleeWeapon {
    public static final String ID = "first_fractal";
    public static final ConfigFloat BASE_MELEE_DAMAGE = createBaseMeleeDamageConfig(ID, 20.0F);
    public static final ConfigFloat BASE_MELEE_ATTACK_SPEED = createBaseMeleeAttackSpeedConfig(ID, -2.4F);
    public static final ConfigFloat PROJECTILE_DAMAGE = new ConfigFloat(
        "weapon.first_fractal.projectile_damage",
        "first_fractal_projectile_damage",
        25.0f,
        0.0f,
        8388600.0f
    );
    public static final ConfigDouble BOUNDING_BOX_SIZE = new ConfigDouble(
        "weapon.first_fractal.bounding_box_size",
        "first_fractal_bounding_box_size",
        1.5,
        0.5,
        10.0
    );
    public static final ConfigDouble PROJECTILE_SPEED = new ConfigDouble(
        "weapon.first_fractal.projectile_speed",
        "first_fractal_projectile_speed",
        1.5,
        0.0,
        10.0
    );
    public static final ConfigInt PROJECTILE_LIFETIME = new ConfigInt(
        "weapon.first_fractal.projectile_lifetime",
        "first_fractal_projectile_lifetime",
        30,
        1,
        1200
    );
    public static final ConfigBoolean DISTANCE_MODE = new ConfigBoolean(
        "weapon.first_fractal.distance_mode",
        "first_fractal_distance_mode",
        true
    );
    public static final ConfigInt SCROLL_SPEED = new ConfigInt(
        "weapon.first_fractal.scroll_speed",
        "first_fractal_scroll_speed",
        4,
        0,
        128
    );
    public static final ConfigDouble MAX_RANGE = new ConfigDouble(
        "weapon.first_fractal.max_range",
        "first_fractal_max_range",
        64.0,
        1.0,
        1024.0
    );
    public static final ConfigDouble SPAWN_RANGE = new ConfigDouble(
        "weapon.first_fractal.spawn_range",
        "first_fractal_spawn_range",
        16.0,
        1.0,
        64.0
    );
    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                BASE_MELEE_DAMAGE,
                BASE_MELEE_ATTACK_SPEED,
                PROJECTILE_DAMAGE,
                BOUNDING_BOX_SIZE,
                PROJECTILE_SPEED,
                PROJECTILE_LIFETIME,
                DISTANCE_MODE,
                SCROLL_SPEED,
                MAX_RANGE,
                SPAWN_RANGE
            );
        }
    };

    public static boolean isDistanceMode() {
        return DISTANCE_MODE.get();
    }

    public static int getScrollSpeed() {
        return SCROLL_SPEED.get();
    }

    public static double getMaxRange() {
        return MAX_RANGE.get();
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);

    public FirstFractal() {
        super(Tiers.NETHERITE, new Item.Properties().fireResistant().rarity(Rarity.EPIC));
    }

    @Override
    public float getTooltipDamage(ItemStack weaponStack, LivingEntity entity) {
        float damage = PROJECTILE_DAMAGE.get();
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

    private static ItemStack[] weapons;

    private static ItemStack[] getWeapons() {
        if(weapons == null) {
            weapons = new ItemStack[]{
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE0.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE1.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE2.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE3.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE4.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE5.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE6.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE7.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE8.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE9.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE10.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE11.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE12.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE13.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE14.get()),
            };
        }
        return weapons;
    }

    public static final Vector3f[] COLORS = new Vector3f[] {
        new Vector3f(1.0f, 0.0f, 0.0f),     // 红 (Red)
        new Vector3f(1.0f, 0.5f, 0.0f),     // 橙 (Orange)
        new Vector3f(1.0f, 1.0f, 0.0f),     // 黄 (Yellow)
        new Vector3f(0.0f, 1.0f, 0.0f),     // 绿 (Green)
        new Vector3f(0.0f, 1.0f, 1.0f),     // 青 (Cyan)
        new Vector3f(0.0f, 0.0f, 1.0f),     // 蓝 (Blue)
        new Vector3f(0.5f, 0.0f, 1.0f)      // 紫 (Violet)
    };

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            summon.setDeltaMovement(summon.getLookAngle().normalize().scale(PROJECTILE_SPEED.get()));

            summon.setBoundingBox(new AABB(
                summon.getX() - BOUNDING_BOX_SIZE.get(), summon.getY() - BOUNDING_BOX_SIZE.get(), summon.getZ() - BOUNDING_BOX_SIZE.get(),
                summon.getX() + BOUNDING_BOX_SIZE.get(), summon.getY() + BOUNDING_BOX_SIZE.get(), summon.getZ() + BOUNDING_BOX_SIZE.get()
            ));

            if(!summon.level().isClientSide()) {
                if(summon.getOwner() instanceof Player player) {
                    List<Entity> targets = summon.level().getEntitiesOfClass(
                            Entity.class,
                            summon.getBoundingBox(),
                            FilterUtil.createTargetFilter(summon, summon.getOwner())
                    );
                    for(Entity target : targets) {
                        if(DamageUtil.meleeAttack(summon, target, summon.getEntityData().get(StaticSummon.STACK_SOURCE), (float) PROJECTILE_DAMAGE.get(), 0.2f, 10)) {
                        }
                    }
                }
            }else {
                Vector3f color = new Vector3f(
                    summon.getEntityData().get(StaticSummon.COLOR_R),
                    summon.getEntityData().get(StaticSummon.COLOR_G),
                    summon.getEntityData().get(StaticSummon.COLOR_B)
                );
                Vector3f[] dirs = MathUtil.computeCoordinateSystem(summon.getLookAngle().toVector3f(), 0);
                ZenithTrailParticleOptions options = new ZenithTrailParticleOptions(0.05f, 40, true, color, dirs[1], dirs[2], 90);
                Vec3 pos = MathUtil.toVec3(dirs[1]).scale(Math.random() * 2).add(summon.position());
                Vec3 speed = MathUtil.toVec3(dirs[0]).scale(PROJECTILE_SPEED.get());
                ParticleUtil.addParticle(
                    summon.level(), options,
                    pos, 0,
                    speed, 0
                );
            }
        }
    };

    public static void summon(Player player, double deltaDist) {
        if(!player.level().isClientSide()) {
            Vec3 targetPos;
            if(DISTANCE_MODE.get()) {
                Vec3 origin = MathUtil.getCrosshairPos(player, player.level(), MAX_RANGE.get());
                double dist = origin.subtract(player.getEyePosition()).length();
                double maxScale = MAX_RANGE.get() - dist;
                double minScale = -dist;
                double scale = Math.min(deltaDist, maxScale);
                if(deltaDist < minScale) scale = -minScale;
                targetPos = origin.add(player.getLookAngle().normalize().scale(scale));
            }else {
                targetPos = player.getEyePosition().add(player.getLookAngle().normalize().scale(Math.min(deltaDist, MAX_RANGE.get())));
            }

            Vec3 summonPos = MathUtil.getRandomPosInRadius(targetPos, SPAWN_RANGE.get());
            Vector3f color = COLORS[player.getRandom().nextInt(COLORS.length)];

            StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), player.level());
            summon.setOwner(player);
            summon.getEntityData().set(StaticSummon.STACK_SOURCE, player.getWeaponItem().copy());
            summon.setPos(summonPos);
            summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.FIRST_FRACTAL_PROJECTILE);
            summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
            summon.getEntityData().set(StaticSummon.ITEM, getWeapons()[player.getRandom().nextInt(getWeapons().length)]);
            summon.getEntityData().set(StaticSummon.COLOR_R, color.x);
            summon.getEntityData().set(StaticSummon.COLOR_G, color.y);
            summon.getEntityData().set(StaticSummon.COLOR_B, color.z);
            summon.getEntityData().set(StaticSummon.GLOW, true);
            summon.getEntityData().set(StaticSummon.LIFETIME, PROJECTILE_LIFETIME.get());

            Vector3f[] dirs = MathUtil.computeCoordinateSystem(targetPos.subtract(summonPos).toVector3f(), 0);
            float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);
            summon.setXRot(xyRot[0]);
            summon.xRotO = xyRot[0];
            summon.setYRot(xyRot[1]);
            summon.yRotO = xyRot[1];

            summon.setNoGravity(true);
            summon.noPhysics = true;

            player.level().addFreshEntity(summon);
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

