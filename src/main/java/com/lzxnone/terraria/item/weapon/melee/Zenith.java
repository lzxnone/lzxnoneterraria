package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.item.weapon.MeleeWeapon;
import com.lzxnone.terraria.LzxnoneTerraria;
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
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.ConfigUtil;
import com.lzxnone.terraria.ui.config.IConfigData;
import net.minecraft.network.chat.Component;

import java.util.List;

public class Zenith extends MeleeWeapon {
    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";

    public static final String DAMAGE_PATH = "weapon.zenith.damage";
    public static final float DAMAGE_DEFAULT = 4.0f;
    public static final float DAMAGE_MIN = 1.0f;
    public static final float DAMAGE_MAX = 8388600.0f;

    public static final String MAX_RANGE_PATH = "weapon.zenith.max_range";
    public static final double MAX_RANGE_DEFAULT = 64.0;
    public static final double MAX_RANGE_MIN = 1.0;
    public static final double MAX_RANGE_MAX = 1024.0;

    public static final String DISTANCE_MODE_PATH = "weapon.zenith.distance_mode";
    public static final boolean DISTANCE_MODE_DEFAULT = true;

    public static final String MOUSE_SPEED_PATH = "weapon.zenith.mouse_speed";
    public static final int MOUSE_SPEED_DEFAULT = 4;
    public static final int MOUSE_SPEED_MIN = 0;
    public static final int MOUSE_SPEED_MAX = 128;

    public static final String TRAIL_B_PATH = "weapon.zenith.trail_b";
    public static final double TRAIL_B_DEFAULT = 4.0;
    public static final double TRAIL_B_MIN = 0.1;
    public static final double TRAIL_B_MAX = 512.0;

    public static final String TRAIL_OFFSET_PATH = "weapon.zenith.trail_offset";
    public static final int TRAIL_OFFSET_DEFAULT = 30;
    public static final int TRAIL_OFFSET_MIN = 0;
    public static final int TRAIL_OFFSET_MAX = 90;

    public static final String WEAPON_COUNT_PATH = "weapon.zenith.weapon_count";
    public static final int WEAPON_COUNT_DEFAULT = 3;
    public static final int WEAPON_COUNT_MIN = 1;
    public static final int WEAPON_COUNT_MAX = 3;

    public static final String BOUNDING_BOX_SIZE_PATH = "weapon.zenith.bounding_box_size";
    public static final double BOUNDING_BOX_SIZE_DEFAULT = 1.5;
    public static final double BOUNDING_BOX_SIZE_MIN = 0.1;
    public static final double BOUNDING_BOX_SIZE_MAX = 4.0;

    public static final String CYCLE_PATH = "weapon.zenith.cycle";
    public static final int CYCLE_DEFAULT = 20;
    public static final int CYCLE_MIN = 10;
    public static final int CYCLE_MAX = 100;

    public static final String SCALE_PATH = "weapon.zenith.scale";
    public static final double SCALE_DEFAULT = 2.0;
    public static final double SCALE_MIN = 0.1;
    public static final double SCALE_MAX = 10.0;

    public static final String TRAIL_ALPHA_PATH = "weapon.zenith.trail_alpha";
    public static final double TRAIL_ALPHA_DEFAULT = 0.15;
    public static final double TRAIL_ALPHA_MIN = 0.0;
    public static final double TRAIL_ALPHA_MAX = 1.0;

    public static final String TRAIL_MAX_LENGTH_PATH = "weapon.zenith.trail_max_length";
    public static final int TRAIL_MAX_LENGTH_DEFAULT = 10;
    public static final int TRAIL_MAX_LENGTH_MIN = 0;
    public static final int TRAIL_MAX_LENGTH_MAX = 100;

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigFactory.loadFloatConfig(DAMAGE_PATH, configText("zenith_damage"), configTooltip("zenith_damage"), DAMAGE_DEFAULT, DAMAGE_MIN, DAMAGE_MAX);
            ConfigFactory.loadDoubleConfig(MAX_RANGE_PATH, configText("zenith_max_range"), configTooltip("zenith_max_range"), MAX_RANGE_DEFAULT, MAX_RANGE_MIN, MAX_RANGE_MAX);
            ConfigFactory.loadBooleanConfig(DISTANCE_MODE_PATH, configText("zenith_distance_mode"), configTooltip("zenith_distance_mode"), DISTANCE_MODE_DEFAULT);
            ConfigFactory.loadIntConfig(MOUSE_SPEED_PATH, configText("zenith_mouse_speed"), configTooltip("zenith_mouse_speed"), MOUSE_SPEED_DEFAULT, MOUSE_SPEED_MIN, MOUSE_SPEED_MAX);
            ConfigFactory.loadDoubleConfig(TRAIL_B_PATH, configText("zenith_trail_b"), configTooltip("zenith_trail_b"), TRAIL_B_DEFAULT, TRAIL_B_MIN, TRAIL_B_MAX);
            ConfigFactory.loadIntConfig(TRAIL_OFFSET_PATH, configText("zenith_trail_offset"), configTooltip("zenith_trail_offset"), TRAIL_OFFSET_DEFAULT, TRAIL_OFFSET_MIN, TRAIL_OFFSET_MAX);
            ConfigFactory.loadIntConfig(WEAPON_COUNT_PATH, configText("zenith_weapon_count"), configTooltip("zenith_weapon_count"), WEAPON_COUNT_DEFAULT, WEAPON_COUNT_MIN, WEAPON_COUNT_MAX);
            ConfigFactory.loadDoubleConfig(BOUNDING_BOX_SIZE_PATH, configText("zenith_bounding_box_size"), configTooltip("zenith_bounding_box_size"), BOUNDING_BOX_SIZE_DEFAULT, BOUNDING_BOX_SIZE_MIN, BOUNDING_BOX_SIZE_MAX);
            ConfigFactory.loadIntConfig(CYCLE_PATH, configText("zenith_cycle"), configTooltip("zenith_cycle"), CYCLE_DEFAULT, CYCLE_MIN, CYCLE_MAX);
            ConfigFactory.loadDoubleConfig(SCALE_PATH, configText("zenith_scale"), configTooltip("zenith_scale"), SCALE_DEFAULT, SCALE_MIN, SCALE_MAX);
            ConfigFactory.loadDoubleConfig(TRAIL_ALPHA_PATH, configText("zenith_trail_alpha"), configTooltip("zenith_trail_alpha"), TRAIL_ALPHA_DEFAULT, TRAIL_ALPHA_MIN, TRAIL_ALPHA_MAX);
            ConfigFactory.loadIntConfig(TRAIL_MAX_LENGTH_PATH, configText("zenith_trail_max_length"), configTooltip("zenith_trail_max_length"), TRAIL_MAX_LENGTH_DEFAULT, TRAIL_MAX_LENGTH_MIN, TRAIL_MAX_LENGTH_MAX);
        }
    };

    private static Component configText(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key);
    }

    private static Component configTooltip(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key + ".tooltip");
    }

    public static float getDamage() {
        return Math.clamp(ConfigUtil.readFloat(DAMAGE_PATH, DAMAGE_DEFAULT), DAMAGE_MIN, DAMAGE_MAX);
    }

    public static double getMaxRange() {
        return Math.clamp(ConfigUtil.readDouble(MAX_RANGE_PATH, MAX_RANGE_DEFAULT), MAX_RANGE_MIN, MAX_RANGE_MAX);
    }

    public static boolean isDistanceMode() {
        return ConfigUtil.readBoolean(DISTANCE_MODE_PATH, DISTANCE_MODE_DEFAULT);
    }

    public static int getMouseSpeed() {
        return Math.clamp(ConfigUtil.readInt(MOUSE_SPEED_PATH, MOUSE_SPEED_DEFAULT), MOUSE_SPEED_MIN, MOUSE_SPEED_MAX);
    }

    public static double getTrailB() {
        return Math.clamp(ConfigUtil.readDouble(TRAIL_B_PATH, TRAIL_B_DEFAULT), TRAIL_B_MIN, TRAIL_B_MAX);
    }

    public static int getTrailOffset() {
        return Math.clamp(ConfigUtil.readInt(TRAIL_OFFSET_PATH, TRAIL_OFFSET_DEFAULT), TRAIL_OFFSET_MIN, TRAIL_OFFSET_MAX);
    }

    public static int getWeaponCount() {
        return Math.clamp(ConfigUtil.readInt(WEAPON_COUNT_PATH, WEAPON_COUNT_DEFAULT), WEAPON_COUNT_MIN, WEAPON_COUNT_MAX);
    }

    public static double getBoundingBoxSize() {
        return Math.clamp(ConfigUtil.readDouble(BOUNDING_BOX_SIZE_PATH, BOUNDING_BOX_SIZE_DEFAULT), BOUNDING_BOX_SIZE_MIN, BOUNDING_BOX_SIZE_MAX);
    }

    public static int getCycle() {
        return Math.clamp(ConfigUtil.readInt(CYCLE_PATH, CYCLE_DEFAULT), CYCLE_MIN, CYCLE_MAX);
    }

    public static double getScale() {
        return Math.clamp(ConfigUtil.readDouble(SCALE_PATH, SCALE_DEFAULT), SCALE_MIN, SCALE_MAX);
    }

    public static double getTrailAlpha() {
        return Math.clamp(ConfigUtil.readDouble(TRAIL_ALPHA_PATH, TRAIL_ALPHA_DEFAULT), TRAIL_ALPHA_MIN, TRAIL_ALPHA_MAX);
    }

    public static int getTrailMaxLength() {
        return Math.clamp(ConfigUtil.readInt(TRAIL_MAX_LENGTH_PATH, TRAIL_MAX_LENGTH_DEFAULT), TRAIL_MAX_LENGTH_MIN, TRAIL_MAX_LENGTH_MAX);
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "zenith",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/zenith.png"),
        Component.translatable("item.lzxnoneterraria.zenith"),
        CONFIG_DATA
    );

    public Zenith() {
        super(Tiers.NETHERITE, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 20, AttributeModifier.Operation.ADD_VALUE),
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
            while(projectile.trailPositions.size() > getTrailMaxLength()) projectile.trailPositions.removeLast();

            projectile.setBoundingBox(new AABB(
                projectile.getX() - getBoundingBoxSize(), projectile.getY() - getBoundingBoxSize(), projectile.getZ() - getBoundingBoxSize(),
                projectile.getX() + getBoundingBoxSize(), projectile.getY() + getBoundingBoxSize(), projectile.getZ() + getBoundingBoxSize()
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
                    if(DamageUtil.meleeAttack(projectile, target, projectile.getEntityData().get(StaticProjectile.STACK_SOURCE), (float) (getDamage() + Math.random() * getDamage()), 0.2f)) {
                        target.invulnerableTime = 2;
                    }
                }
            }
        }
    };

    public static void summon(Player player, double deltaDist, boolean isFirst) {
        Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
        int randomAngle = (int) ((Math.random() * 2 - 1) * getTrailOffset());
        dirs = MathUtil.rotateCoordinateSystem(dirs[0], dirs[2], randomAngle);

        Vec3 pos = (player.getBoundingBox().getCenter()).add(MathUtil.toVec3(dirs[0]).scale(-2));
        double dist;
        if(isDistanceMode()) {
            Vec3 targetPos = MathUtil.getCrosshairPos(player, player.level(), getMaxRange());
            dist = Math.max(0, Math.min(targetPos.subtract(pos).length() + deltaDist, getMaxRange()));
        }else {
            dist = deltaDist;
        }
        int cycle = (int) Math.max(10, dist / getMaxRange() * getCycle());
        double a = Math.max(dist / 2, 2);
        double b = Math.min(Math.random() * getTrailB() + getTrailB(), a / 2);
        double w = Math.PI * 2 / (double) cycle;
        if(player.getRandom().nextInt(2) == 0) w = -w;

        int randomIndex = isFirst ? getWeapons().length - 1 : player.getRandom().nextInt(getWeapons().length);

        for(int i = 0;i < getWeaponCount();i++) {
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
                projectile.getEntityData().set(StaticProjectile.SCALE_X, (float) getScale());
                projectile.getEntityData().set(StaticProjectile.SCALE_Y, (float) getScale());
                projectile.getEntityData().set(StaticProjectile.LIFETIME, cycle);
                projectile.getEntityData().set(StaticProjectile.EXPRESSION_X, String.format("%.3f*cos(%.3ft-1.571)", b, w));
                projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*sin(%.3ft-1.571)+%.3f", a, w, a));
                customData.putInt("start", 0);
            }else if(i == 1) {
                projectile.getEntityData().set(StaticProjectile.COLOR_A, 0.75f);
                projectile.getEntityData().set(StaticProjectile.SCALE_X, (float) getScale() * 0.75f);
                projectile.getEntityData().set(StaticProjectile.SCALE_Y, (float) getScale() * 0.75f);
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
                projectile.getEntityData().set(StaticProjectile.SCALE_X, (float) getScale() * 0.5f);
                projectile.getEntityData().set(StaticProjectile.SCALE_Y, (float) getScale() * 0.5f);
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
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int count) {
        if(!(livingEntity instanceof Player player)) return;
        if(player.tickCount % 3 == 0) {
            double deltaDist = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag().getDouble("deltaDist");
            boolean isFirst = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                        .copyTag().getBoolean("isFirst");
            if(isFirst) {
                CustomData.update(DataComponents.CUSTOM_DATA, stack,
                    tag -> tag.putBoolean("isFirst", false));
            }
            summon(player, deltaDist, isFirst);
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

