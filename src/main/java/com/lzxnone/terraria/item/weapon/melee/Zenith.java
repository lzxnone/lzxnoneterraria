package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.item.weapon.MeleeWeapon;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.IStaticProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.particle.ZenithTrailParticleOptions;
import com.lzxnone.terraria.utils.*;
import com.mojang.math.Axis;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
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

public class Zenith extends MeleeWeapon {
    public static final String ID = "zenith";
    public static final ConfigFloat BASE_MELEE_DAMAGE = createBaseMeleeDamageConfig(ID, 20F);
    public static final ConfigFloat BASE_MELEE_ATTACK_SPEED = createBaseMeleeAttackSpeedConfig(ID, -2.4F);
    public static final ConfigFloat DAMAGE = new ConfigFloat(
        "weapon.zenith.damage",
        "zenith_damage",
        10.0f,
        0.0f,
        8388600.0f
    );
    public static final ConfigDouble MAX_RANGE = new ConfigDouble(
        "weapon.zenith.max_range",
        "zenith_max_range",
        64.0,
        1.0,
        1024.0
    );
    public static final ConfigBoolean DISTANCE_MODE = new ConfigBoolean(
        "weapon.zenith.distance_mode",
        "zenith_distance_mode",
        true
    );
    public static final ConfigInt MOUSE_SPEED = new ConfigInt(
        "weapon.zenith.mouse_speed",
        "zenith_mouse_speed",
        4,
        0,
        128
    );
    public static final ConfigDouble TRAIL_B = new ConfigDouble(
        "weapon.zenith.trail_b",
        "zenith_trail_b",
        4.0,
        0.1,
        512.0
    );
    public static final ConfigInt TRAIL_OFFSET = new ConfigInt(
        "weapon.zenith.trail_offset",
        "zenith_trail_offset",
        30,
        0,
        90
    );
    public static final ConfigInt WEAPON_COUNT = new ConfigInt(
        "weapon.zenith.weapon_count",
        "zenith_weapon_count",
        3,
        1,
        3
    );
    public static final ConfigDouble BOUNDING_BOX_SIZE = new ConfigDouble(
        "weapon.zenith.bounding_box_size",
        "zenith_bounding_box_size",
        1.5,
        0.1,
        4.0
    );
    public static final ConfigInt CYCLE = new ConfigInt(
        "weapon.zenith.cycle",
        "zenith_cycle",
        20,
        10,
        100
    );
    public static final float SCALE = 2.0f;
    public static final float TRAIL_ALPHA = 1.0f;
    public static final int TRAIL_MAX_LENGTH = 10;
    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                BASE_MELEE_DAMAGE,
                BASE_MELEE_ATTACK_SPEED,
                DAMAGE,
                MAX_RANGE,
                DISTANCE_MODE,
                MOUSE_SPEED,
                TRAIL_B,
                TRAIL_OFFSET,
                WEAPON_COUNT,
                BOUNDING_BOX_SIZE,
                CYCLE
            );
        }
    };
    public static float getDamage() {
        return DAMAGE.get();
    }

    @Override
    public float getTooltipDamage(ItemStack weaponStack, LivingEntity entity) {
        float damage = DAMAGE.get();
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

    public static double getMaxRange() {
        return MAX_RANGE.get();
    }

    public static boolean isDistanceMode() {
        return DISTANCE_MODE.get();
    }

    public static int getMouseSpeed() {
        return MOUSE_SPEED.get();
    }

    public static double getTrailB() {
        return TRAIL_B.get();
    }

    public static int getTrailOffset() {
        return TRAIL_OFFSET.get();
    }

    public static int getWeaponCount() {
        return WEAPON_COUNT.get();
    }

    public static int getCycle() {
        return CYCLE.get();
    }

    public static double getScale() {
        return SCALE;
    }

    public static double getTrailAlpha() {
        return TRAIL_ALPHA;
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);

    public Zenith() {
        super(Tiers.NETHERITE, new Item.Properties().fireResistant().rarity(Rarity.EPIC));
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
                new ItemStack(ModItems.COPPER_SHORTSWORD.get()),
                new ItemStack(ModItems.LIGHTS_BANE.get()),
                new ItemStack(ModItems.MURAMASA.get()),
                new ItemStack(ModItems.TERRAGRIM.get()),
                new ItemStack(ModItems.BLOOD_BUTCHERER.get()),
                new ItemStack(ModItems.STARFURY.get()),
                new ItemStack(ModItems.ENCHANTED_SWORD.get()),
                new ItemStack(ModItems.BEE_KEEPER.get()),
                new ItemStack(ModItems.BLADE_OF_GRASS.get()),
                new ItemStack(ModItems.VOLCANO.get()),
                new ItemStack(ModItems.NIGHTS_EDGE.get()),
                new ItemStack(ModItems.TRUE_NIGHTS_EDGE.get()),
                new ItemStack(ModItems.EXCALIBUR.get()),
                new ItemStack(ModItems.TRUE_EXCALIBUR.get()),
                new ItemStack(ModItems.THE_HORSEMANS_BLADE.get()),
                new ItemStack(ModItems.SEEDLER.get()),
                new ItemStack(ModItems.TERRA_BLADE.get()),
                new ItemStack(ModItems.INFLUX_WAVER.get()),
                new ItemStack(ModItems.STAR_WRATH.get()),
                new ItemStack(ModItems.MURAMASA.get()),
                new ItemStack(ModItems.ZENITH.get())
            };
        }
        return weapons;
    }

    public static final Vector3f[] COLORS = {
        new Vector3f(0.922f, 0.651f, 0.529f), // 1. 淺橙色 (#EBA687)
        new Vector3f(0.478f, 0.259f, 0.749f), // 2. 紫色 (#7A42BF)
        new Vector3f(0.220f, 0.306f, 0.824f), // 3. 海軍藍 (#384ED2)
        new Vector3f(0.698f, 1.000f, 0.706f), // 4. 亮薄荷綠 (#B2FFB4)
        new Vector3f(0.929f, 0.110f, 0.141f), // 5. 紅色 (#ED1C24)
        new Vector3f(0.925f, 0.243f, 0.753f), // 6. 粉色 (#EC3EC0)
        new Vector3f(0.357f, 0.620f, 0.910f), // 7. 淺藍色 (#5B9EE8)
        new Vector3f(1.000f, 0.906f, 0.271f), // 8. 黃色 (#FFE745)
        new Vector3f(0.420f, 0.796f, 0.000f), // 9. 綠色 (#6BCB00)
        new Vector3f(0.996f, 0.620f, 0.137f), // 10. 橙色 (#FE9E23)
        new Vector3f(0.702f, 0.212f, 0.788f), // 11. 紫色 (#B336C9)
        new Vector3f(0.702f, 0.212f, 0.788f), // 12. 紫色 (#B336C9)
        new Vector3f(0.925f, 0.784f, 0.075f), // 13. 黃色 (#ECC813)
        new Vector3f(0.925f, 0.784f, 0.075f), // 14. 黃色 (#ECC813)
        new Vector3f(0.988f, 0.373f, 0.016f), // 15. 橙色 (#FC5F04)
        new Vector3f(0.561f, 0.843f, 0.114f), // 16. 綠色 (#8FD71D)
        new Vector3f(0.314f, 0.871f, 0.478f), // 17. 淺綠色 (#50DE7A)
        new Vector3f(0.329f, 0.918f, 0.961f), // 18. 青色 (#54EAF5)
        new Vector3f(0.929f, 0.247f, 0.522f), // 19. 粉色 (#ED3F85)
        new Vector3f(0.996f, 0.761f, 0.980f), // 20. 淺粉色 (#FEC2FA)
        new Vector3f(0.698f, 1.000f, 0.706f)  // 20. 亮薄荷綠 (#B2FFB4)
    };

    public static final ResourceLocation RES0 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/sword_trail.png");
    public static final ResourceLocation RES1 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/beam_sparkle.png");

    public static final IStaticProjectileBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileBehavior() {

        @Override
        public void onMoving(StaticProjectile projectile) {
            if(projectile.getOwner() == null) return;

            CompoundTag customData = projectile.getEntityData().get(StaticProjectile.CUSTOM_DATA);
            if(!customData.contains("w") || !customData.contains("angle") || !customData.contains("start")) return;

            int start = customData.getInt("start");
            if(projectile.getEntityData().get(StaticProjectile.AGE) < start) {
                Vec3 currentPos = (projectile.getOwner().getBoundingBox().getCenter()).add(MathUtil.toVec3(projectile.getEntityData().get(StaticProjectile.DIRECTION).normalize()).scale(-2));
                projectile.getEntityData().set(StaticProjectile.ORIGIN, currentPos.toVector3f());
                return;
            }

            float lifeRadio = (float) (projectile.getEntityData().get(StaticProjectile.AGE) - start) / (float) projectile.getEntityData().get(StaticProjectile.LIFETIME);

            Vector3f originalRight = projectile.getEntityData().get(StaticProjectile.RIGHT).normalize();
            Vector3f tempRight = new Vector3f(originalRight);
            Vector3f originalDir = projectile.getEntityData().get(StaticProjectile.DIRECTION).normalize();
            Vector3f tempDir = new Vector3f(originalDir);
            Vector3f up = projectile.getEntityData().get(StaticProjectile.UP).normalize();
            double rad = customData.getDouble("w") * (projectile.getEntityData().get(StaticProjectile.AGE) - start);
            Quaternionf rotation = new Quaternionf()
                .fromAxisAngleRad(up, (float) (Math.PI / 2 - rad));

            Vec3 right = MathUtil.toVec3(tempRight.rotate(rotation)); //剑尖方向
            Vec3 dir = MathUtil.toVec3(tempDir.rotate(rotation)); //剑飞行方向
            if(customData.getDouble("w") < 0) dir = dir.scale(-1);

            projectile.trailPositions.addFirst(projectile.position().add(right.scale(-1.2)));
            projectile.trailPositions.addFirst(projectile.position().add(right.scale(1.5)));
            while(projectile.trailPositions.size() > TRAIL_MAX_LENGTH) projectile.trailPositions.removeLast();

            projectile.setBoundingBox(new AABB(
                projectile.getX() - BOUNDING_BOX_SIZE.get(), projectile.getY() - BOUNDING_BOX_SIZE.get(), projectile.getZ() - BOUNDING_BOX_SIZE.get(),
                projectile.getX() + BOUNDING_BOX_SIZE.get(), projectile.getY() + BOUNDING_BOX_SIZE.get(), projectile.getZ() + BOUNDING_BOX_SIZE.get()
            ));

            if(!projectile.level().isClientSide()) {
                List<Entity> targets = projectile.level().getEntitiesOfClass(
                    Entity.class,
                    projectile.getBoundingBox(),
                    FilterUtil.createTargetFilter(projectile, projectile.getOwner())
                );
                for(Entity target : targets) this.onHitEntity(projectile, new EntityHitResult(target, target.position()));
            }else {
                if(lifeRadio > 0.1f && lifeRadio < 0.9f && projectile.getRandom().nextInt(5) == 0) {
                    Vector3f color = new Vector3f(
                        projectile.getEntityData().get(StaticProjectile.COLOR_R),
                        projectile.getEntityData().get(StaticProjectile.COLOR_G),
                        projectile.getEntityData().get(StaticProjectile.COLOR_B)
                    );
                    ZenithTrailParticleOptions options = new ZenithTrailParticleOptions(0.05f, 40, true, color, up, right.toVector3f(), customData.getInt("angle"));
                    Vec3 pos = right.scale(Math.random()).add(projectile.position());
                    Vec3 speed = dir.scale(Math.max(0.05, Math.random() * 0.2));
                    ParticleUtil.addParticle(
                        projectile.level(), options,
                        pos, 0,
                        speed, 0
                    );
                }
            }

            Vec3 currentPos = (projectile.getOwner().getBoundingBox().getCenter()).add(MathUtil.toVec3(originalDir).scale(-2));
            projectile.getEntityData().set(StaticProjectile.ORIGIN, currentPos.toVector3f());
        }

        @Override
        public void onHitEntity(StaticProjectile projectile, EntityHitResult result) {
            if(!projectile.level().isClientSide()) {
                Entity target = result.getEntity();
                if(projectile.getOwner() instanceof Player player && FilterUtil.createTargetFilter(projectile, projectile.getOwner()).test(target)) {
                    if(DamageUtil.meleeAttack(projectile, target, projectile.getEntityData().get(StaticProjectile.STACK_SOURCE), (float) (DAMAGE.get() + Math.random() * DAMAGE.get()), 0.2f, 10)) {
                    }
                }
            }
        }
    };

    public static void summon(Player player, double deltaDist, boolean isFirst) {
        Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
        int randomAngle = (int) ((Math.random() * 2 - 1) * TRAIL_OFFSET.get());
        dirs = MathUtil.rotateCoordinateSystem(dirs[0], dirs[2], randomAngle);

        Vec3 pos = (player.getBoundingBox().getCenter()).add(MathUtil.toVec3(dirs[0]).scale(-2));
        double dist;
        if(DISTANCE_MODE.get()) {
            Vec3 targetPos = MathUtil.getCrosshairPos(player, player.level(), MAX_RANGE.get());
            dist = Math.max(0, Math.min(targetPos.subtract(pos).length() + deltaDist, MAX_RANGE.get()));
        }else {
            dist = deltaDist;
        }
        int cycle = (int) Math.max(10, dist / MAX_RANGE.get() * CYCLE.get());
        double a = Math.max(dist / 2, 2);
        double b = Math.min(Math.random() * TRAIL_B.get() + TRAIL_B.get(), a / 2);
        double w = Math.PI * 2 / (double) cycle;
        if(player.getRandom().nextInt(2) == 0) w = -w;

        int randomIndex = isFirst ? getWeapons().length - 1 : player.getRandom().nextInt(getWeapons().length);

        for(int i = 0;i < WEAPON_COUNT.get();i++) {
            StaticProjectile projectile = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), player.level());
            projectile.setOwner(player);
            projectile.getEntityData().set(StaticProjectile.STACK_SOURCE, player.getWeaponItem().copy());

            projectile.setPos(pos);
            projectile.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.ZENITH_PROJECTILE);
            projectile.getEntityData().set(StaticProjectile.RENDER_MODE, "custom");
            projectile.getEntityData().set(StaticProjectile.ITEM, getWeapons()[randomIndex]);
            projectile.getEntityData().set(StaticProjectile.ORIGIN, pos.toVector3f());
            projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
            projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
            projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);
            projectile.getEntityData().set(StaticProjectile.COLOR_R, COLORS[randomIndex].x);
            projectile.getEntityData().set(StaticProjectile.COLOR_G, COLORS[randomIndex].y);
            projectile.getEntityData().set(StaticProjectile.COLOR_B, COLORS[randomIndex].z);
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
                projectile.getEntityData().set(StaticProjectile.SCALE_X, SCALE);
                projectile.getEntityData().set(StaticProjectile.SCALE_Y, SCALE);
                projectile.getEntityData().set(StaticProjectile.LIFETIME, cycle);
                projectile.getEntityData().set(StaticProjectile.EXPRESSION_X, String.format("%.3f*cos(%.3ft-1.571)", b, w));
                projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*sin(%.3ft-1.571)+%.3f", a, w, a));
                customData.putInt("start", 0);
            }else if(i == 1) {
                projectile.getEntityData().set(StaticProjectile.COLOR_A, 0.75f);
                projectile.getEntityData().set(StaticProjectile.SCALE_X, SCALE * 0.75f);
                projectile.getEntityData().set(StaticProjectile.SCALE_Y, SCALE * 0.75f);
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
                projectile.getEntityData().set(StaticProjectile.SCALE_X, SCALE * 0.5f);
                projectile.getEntityData().set(StaticProjectile.SCALE_Y, SCALE * 0.5f);
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
        CustomData.update(DataComponents.CUSTOM_DATA, stack,
            tag -> tag.putBoolean("isFirst", true));
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
        boolean isFirst = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                    .copyTag().getBoolean("isFirst");
        if(isFirst) {
            CustomData.update(DataComponents.CUSTOM_DATA, stack,
                tag -> tag.putBoolean("isFirst", false));
        }
        summon(player, deltaDist, isFirst);
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
