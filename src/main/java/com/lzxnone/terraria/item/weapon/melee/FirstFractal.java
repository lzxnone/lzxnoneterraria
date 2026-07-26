package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.item.weapon.MeleeWeapon;
import com.lzxnone.terraria.LzxnoneTerraria;
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
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.ConfigUtil;
import com.lzxnone.terraria.ui.config.IConfigData;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.UUID;

public class FirstFractal extends MeleeWeapon {
    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";

    public static final String PROJECTILE_DAMAGE_PATH = "weapon.first_fractal.projectile_damage";
    public static final float PROJECTILE_DAMAGE_DEFAULT = 10.0f;
    public static final float PROJECTILE_DAMAGE_MIN = 0.0f;
    public static final float PROJECTILE_DAMAGE_MAX = 8388600.0f;

    public static final String BOUNDING_BOX_SIZE_PATH = "weapon.first_fractal.bounding_box_size";
    public static final double BOUNDING_BOX_SIZE_DEFAULT = 1.5;
    public static final double BOUNDING_BOX_SIZE_MIN = 0.5;
    public static final double BOUNDING_BOX_SIZE_MAX = 10.0;

    public static final String PROJECTILE_SPEED_PATH = "weapon.first_fractal.projectile_speed";
    public static final double PROJECTILE_SPEED_DEFAULT = 1.5;
    public static final double PROJECTILE_SPEED_MIN = 0.0;
    public static final double PROJECTILE_SPEED_MAX = 10.0;

    public static final String PROJECTILE_LIFETIME_PATH = "weapon.first_fractal.projectile_lifetime";
    public static final int PROJECTILE_LIFETIME_DEFAULT = 30;
    public static final int PROJECTILE_LIFETIME_MIN = 1;
    public static final int PROJECTILE_LIFETIME_MAX = 1200;

    public static final String DISTANCE_MODE_PATH = "weapon.first_fractal.distance_mode";
    public static final boolean DISTANCE_MODE_DEFAULT = true;

    public static final String SCROLL_SPEED_PATH = "weapon.first_fractal.scroll_speed";
    public static final int SCROLL_SPEED_DEFAULT = 4;
    public static final int SCROLL_SPEED_MIN = 0;
    public static final int SCROLL_SPEED_MAX = 128;

    public static final String MAX_RANGE_PATH = "weapon.first_fractal.max_range";
    public static final double MAX_RANGE_DEFAULT = 64.0;
    public static final double MAX_RANGE_MIN = 1.0;
    public static final double MAX_RANGE_MAX = 1024.0;

    public static final String SPAWN_RANGE_PATH = "weapon.first_fractal.spawn_range";
    public static final double SPAWN_RANGE_DEFAULT = 16.0;
    public static final double SPAWN_RANGE_MIN = 1.0;
    public static final double SPAWN_RANGE_MAX = 64.0;

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigFactory.loadFloatConfig(PROJECTILE_DAMAGE_PATH, configText("first_fractal_projectile_damage"), configTooltip("first_fractal_projectile_damage"), PROJECTILE_DAMAGE_DEFAULT, PROJECTILE_DAMAGE_MIN, PROJECTILE_DAMAGE_MAX);
            ConfigFactory.loadDoubleConfig(BOUNDING_BOX_SIZE_PATH, configText("first_fractal_bounding_box_size"), configTooltip("first_fractal_bounding_box_size"), BOUNDING_BOX_SIZE_DEFAULT, BOUNDING_BOX_SIZE_MIN, BOUNDING_BOX_SIZE_MAX);
            ConfigFactory.loadDoubleConfig(PROJECTILE_SPEED_PATH, configText("first_fractal_projectile_speed"), configTooltip("first_fractal_projectile_speed"), PROJECTILE_SPEED_DEFAULT, PROJECTILE_SPEED_MIN, PROJECTILE_SPEED_MAX);
            ConfigFactory.loadIntConfig(PROJECTILE_LIFETIME_PATH, configText("first_fractal_projectile_lifetime"), configTooltip("first_fractal_projectile_lifetime"), PROJECTILE_LIFETIME_DEFAULT, PROJECTILE_LIFETIME_MIN, PROJECTILE_LIFETIME_MAX);
            ConfigFactory.loadBooleanConfig(DISTANCE_MODE_PATH, configText("first_fractal_distance_mode"), configTooltip("first_fractal_distance_mode"), DISTANCE_MODE_DEFAULT);
            ConfigFactory.loadIntConfig(SCROLL_SPEED_PATH, configText("first_fractal_scroll_speed"), configTooltip("first_fractal_scroll_speed"), SCROLL_SPEED_DEFAULT, SCROLL_SPEED_MIN, SCROLL_SPEED_MAX);
            ConfigFactory.loadDoubleConfig(MAX_RANGE_PATH, configText("first_fractal_max_range"), configTooltip("first_fractal_max_range"), MAX_RANGE_DEFAULT, MAX_RANGE_MIN, MAX_RANGE_MAX);
            ConfigFactory.loadDoubleConfig(SPAWN_RANGE_PATH, configText("first_fractal_spawn_range"), configTooltip("first_fractal_spawn_range"), SPAWN_RANGE_DEFAULT, SPAWN_RANGE_MIN, SPAWN_RANGE_MAX);
        }
    };

    private static Component configText(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key);
    }

    private static Component configTooltip(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key + ".tooltip");
    }

    public static float getProjectileDamage() {
        return Math.clamp(ConfigUtil.readFloat(PROJECTILE_DAMAGE_PATH, PROJECTILE_DAMAGE_DEFAULT), PROJECTILE_DAMAGE_MIN, PROJECTILE_DAMAGE_MAX);
    }

    public static double getBoundingBoxSize() {
        return Math.clamp(ConfigUtil.readDouble(BOUNDING_BOX_SIZE_PATH, BOUNDING_BOX_SIZE_DEFAULT), BOUNDING_BOX_SIZE_MIN, BOUNDING_BOX_SIZE_MAX);
    }

    public static double getProjectileSpeed() {
        return Math.clamp(ConfigUtil.readDouble(PROJECTILE_SPEED_PATH, PROJECTILE_SPEED_DEFAULT), PROJECTILE_SPEED_MIN, PROJECTILE_SPEED_MAX);
    }

    public static int getProjectileLifetime() {
        return Math.clamp(ConfigUtil.readInt(PROJECTILE_LIFETIME_PATH, PROJECTILE_LIFETIME_DEFAULT), PROJECTILE_LIFETIME_MIN, PROJECTILE_LIFETIME_MAX);
    }

    public static boolean isDistanceMode() {
        return ConfigUtil.readBoolean(DISTANCE_MODE_PATH, DISTANCE_MODE_DEFAULT);
    }

    public static int getScrollSpeed() {
        return Math.clamp(ConfigUtil.readInt(SCROLL_SPEED_PATH, SCROLL_SPEED_DEFAULT), SCROLL_SPEED_MIN, SCROLL_SPEED_MAX);
    }

    public static double getMaxRange() {
        return Math.clamp(ConfigUtil.readDouble(MAX_RANGE_PATH, MAX_RANGE_DEFAULT), MAX_RANGE_MIN, MAX_RANGE_MAX);
    }

    public static double getSpawnRange() {
        return Math.clamp(ConfigUtil.readDouble(SPAWN_RANGE_PATH, SPAWN_RANGE_DEFAULT), SPAWN_RANGE_MIN, SPAWN_RANGE_MAX);
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "first_fractal",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/first_fractal.png"),
        Component.translatable("item.lzxnoneterraria.first_fractal"),
        CONFIG_DATA
    );

    public FirstFractal() {
        super(Tiers.NETHERITE, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 20.0, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -2.4, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ).fireResistant().rarity(Rarity.EPIC));
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
        public static final ResourceLocation RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/beam_sparkle.png");
        public static final ResourceLocation RES2 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/first_fractal_star.png");


        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            summon.setDeltaMovement(summon.getLookAngle().normalize().scale(getProjectileSpeed()));

            summon.setBoundingBox(new AABB(
                summon.getX() - getBoundingBoxSize(), summon.getY() - getBoundingBoxSize(), summon.getZ() - getBoundingBoxSize(),
                summon.getX() + getBoundingBoxSize(), summon.getY() + getBoundingBoxSize(), summon.getZ() + getBoundingBoxSize()
            ));

            if(!summon.level().isClientSide()) {
                if(summon.getOwner() instanceof Player player) {
                    List<Entity> targets = summon.level().getEntitiesOfClass(
                            Entity.class,
                            summon.getBoundingBox(),
                            FilterUtil.createTargetFilter(summon, summon.getOwner())
                    );
                    for(Entity target : targets) {
                        if(DamageUtil.meleeAttack(summon, target, summon.getEntityData().get(StaticSummon.STACK_SOURCE), (float) getProjectileDamage(), 0.2f)) {
                            target.invulnerableTime = 10;
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
                Vec3 speed = MathUtil.toVec3(dirs[0]).scale(getProjectileSpeed());
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
            if(isDistanceMode()) {
                Vec3 origin = MathUtil.getCrosshairPos(player, player.level(), getMaxRange());
                double dist = origin.subtract(player.getEyePosition()).length();
                double maxScale = getMaxRange() - dist;
                double minScale = -dist;
                double scale = Math.min(deltaDist, maxScale);
                if(deltaDist < minScale) scale = -minScale;
                targetPos = origin.add(player.getLookAngle().normalize().scale(scale));
            }else {
                targetPos = player.getEyePosition().add(player.getLookAngle().normalize().scale(Math.min(deltaDist, getMaxRange())));
            }

            Vec3 summonPos = MathUtil.getRandomPosInRadius(targetPos, getSpawnRange());
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
            summon.getEntityData().set(StaticSummon.LIFETIME, getProjectileLifetime());

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
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int count) {
        if(!(livingEntity instanceof Player player)) return;
        if(player.tickCount % 3 == 0) {
            double deltaDist = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag().getDouble("deltaDist");
            summon(player, deltaDist);
            SoundUtil.playClientSound(player, ModSounds.WAVE.get());
        }
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

