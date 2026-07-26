package com.lzxnone.terraria.item.weapon.magic;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.weapon.MagicWeapon;
import com.lzxnone.terraria.item.weapon.melee.Mace;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.ConfigUtil;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.ParticleUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import com.mojang.math.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

public class LastPrism extends MagicWeapon {
    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";

    public static final String DAMAGE_PATH = "weapon.last_prism.damage";
    public static final float DAMAGE_DEFAULT = 1.0f;
    public static final float DAMAGE_MIN = 0.0f;
    public static final float DAMAGE_MAX = 8388600.0f;

    public static final String CHARGE_TIME_PATH = "weapon.last_prism.charge_time";
    public static final int CHARGE_TIME_DEFAULT = 67;
    public static final int CHARGE_TIME_MIN = 1;
    public static final int CHARGE_TIME_MAX = 72000;

    public static final String MAX_USE_TIME_PATH = "weapon.last_prism.max_use_time";
    public static final int MAX_USE_TIME_DEFAULT = 1200;
    public static final int MAX_USE_TIME_MIN = 1;
    public static final int MAX_USE_TIME_MAX = 72000;

    public static final String MAX_RANGE_PATH = "weapon.last_prism.max_range";
    public static final double MAX_RANGE_DEFAULT = 64.0;
    public static final double MAX_RANGE_MIN = 1.0;
    public static final double MAX_RANGE_MAX = 512.0;

    public static final String MANA_CONSUME_RATE_PATH = "weapon.last_prism.mana_consume_rate";
    public static final int MANA_CONSUME_RATE_DEFAULT = 20;
    public static final int MANA_CONSUME_RATE_MIN = 0;
    public static final int MANA_CONSUME_RATE_MAX = 72000;

    public static final String MANA_RECOVER_RATE_PATH = "weapon.last_prism.mana_recover_rate";
    public static final int MANA_RECOVER_RATE_DEFAULT = 20;
    public static final int MANA_RECOVER_RATE_MIN = 0;
    public static final int MANA_RECOVER_RATE_MAX = 72000;

    public LastPrism() {
        super(
            Tiers.NETHERITE,
            new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC),
            LastPrism::getManaConsumeRate,
            LastPrism::getManaRecoverRate
        );
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigFactory.loadFloatConfig(DAMAGE_PATH, configText("last_prism_damage"), configTooltip("last_prism_damage"), DAMAGE_DEFAULT, DAMAGE_MIN, DAMAGE_MAX);
            ConfigFactory.loadIntConfig(CHARGE_TIME_PATH, configText("last_prism_charge_time"), configTooltip("last_prism_charge_time"), CHARGE_TIME_DEFAULT, CHARGE_TIME_MIN, CHARGE_TIME_MAX);
            ConfigFactory.loadIntConfig(MAX_USE_TIME_PATH, configText("last_prism_max_use_time"), configTooltip("last_prism_max_use_time"), MAX_USE_TIME_DEFAULT, MAX_USE_TIME_MIN, MAX_USE_TIME_MAX);
            ConfigFactory.loadDoubleConfig(MAX_RANGE_PATH, configText("last_prism_max_range"), configTooltip("last_prism_max_range"), MAX_RANGE_DEFAULT, MAX_RANGE_MIN, MAX_RANGE_MAX);
            ConfigFactory.loadIntConfig(MANA_CONSUME_RATE_PATH, configText("last_prism_mana_consume_rate"), configTooltip("last_prism_mana_consume_rate"), MANA_CONSUME_RATE_DEFAULT, MANA_CONSUME_RATE_MIN, MANA_CONSUME_RATE_MAX);
            ConfigFactory.loadIntConfig(MANA_RECOVER_RATE_PATH, configText("last_prism_mana_recover_rate"), configTooltip("last_prism_mana_recover_rate"), MANA_RECOVER_RATE_DEFAULT, MANA_RECOVER_RATE_MIN, MANA_RECOVER_RATE_MAX);
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

    public static int getChargeTime() {
        return Math.clamp(ConfigUtil.readInt(CHARGE_TIME_PATH, CHARGE_TIME_DEFAULT), CHARGE_TIME_MIN, CHARGE_TIME_MAX);
    }

    public static int getMaxUseTime() {
        return Math.clamp(ConfigUtil.readInt(MAX_USE_TIME_PATH, MAX_USE_TIME_DEFAULT), MAX_USE_TIME_MIN, MAX_USE_TIME_MAX);
    }

    public static double getMaxRange() {
        return Math.clamp(ConfigUtil.readDouble(MAX_RANGE_PATH, MAX_RANGE_DEFAULT), MAX_RANGE_MIN, MAX_RANGE_MAX);
    }

    public static int getManaConsumeRate() {
        return Math.clamp(ConfigUtil.readInt(MANA_CONSUME_RATE_PATH, MANA_CONSUME_RATE_DEFAULT), MANA_CONSUME_RATE_MIN, MANA_CONSUME_RATE_MAX);
    }

    public static int getManaRecoverRate() {
        return Math.clamp(ConfigUtil.readInt(MANA_RECOVER_RATE_PATH, MANA_RECOVER_RATE_DEFAULT), MANA_RECOVER_RATE_MIN, MANA_RECOVER_RATE_MAX);
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "last_prism",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/last_prism.png"),
        Component.translatable("item.lzxnoneterraria.last_prism"),
        CONFIG_DATA
    );

    public static final ResourceLocation RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/normal_trail.png");
    public static final int RING = 8;
    public static final float MAX_ROTATE = 36.0f;
    public static final float MAX_RADIUS = 0.5f;
    public static final float DELTA_DIST = 0.5f;
    public static final float MIN_DIST = 0.25f;
    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.1f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(0.0F, 1.0F, 1.0F),
            new Vector3f(0.1F, 0.4F, 1.0F),
            new Vector3f(1.0F, 1.0F, 0.6F),
            new Vector3f(1.0F, 0.9F, 0.0F),
            new Vector3f(0.5F, 1.0F, 0.5F),
            new Vector3f(0.0F, 1.0F, 0.2F),
            new Vector3f(1.0F, 0.6F, 0.8F),
            new Vector3f(1.0F, 0.0F, 1.0F),
            new Vector3f(1.0F, 0.8F, 0.5F),
            new Vector3f(1.0F, 0.5F, 0.0F),
            new Vector3f(0.7F, 0.7F, 0.7F)
        }
    );

    private static final Vector3f[] BEAM_COLORS = {
        new Vector3f(1.0f, 0.0f, 0.0f),
        new Vector3f(1.0f, 1.0f, 0.0f),
        new Vector3f(0.5f, 0.0f, 0.5f),
        new Vector3f(0.0f, 1.0f, 0.0f),
        new Vector3f(0.0f, 1.0f, 1.0f),
        new Vector3f(1.0f, 0.0f, 0.5f)
    };

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {

        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            if(summon.getOwner() instanceof Player player) {
                ItemStack stack = player.getMainHandItem();
                if(!stack.is(ModItems.LAST_PRISM.get()) || !player.isUsingItem()) {
                    this.onDied(summon);
                    return;
                }
                ItemStack usingStack = player.getUseItem();
                if(!usingStack.is(ModItems.LAST_PRISM.get())) {
                    this.onDied(summon);
                    return;
                }
                Vector3f[] dirs = MathUtil.computeCoordinateSystem(summon.getOwner());
                Vec3 pos = summon.getOwner().getEyePosition().add(MathUtil.toVec3(dirs[0]).scale(1.25)).add(MathUtil.toVec3(dirs[1]).scale(-1.0));
                summon.setPos(pos);
            }
        }

        @Override
        public AABB getBoundingBoxForCulling(StaticSummon summon) {
            return summon.getBoundingBox().inflate(2.0);
        }
    };

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR2 = new IStaticSummonBehavior() {

        @Override
        public void tick(StaticSummon beam) {
            this.checkBeforeTick(beam);
            if(!(beam.getOwner() instanceof StaticSummon tri)) {
                this.onDied(beam);
                return;
            }
            if(!(tri.getOwner() instanceof Player player)) {
                this.onDied(beam);
                return;
            }

            ItemStack stack = player.getMainHandItem();
            ItemStack usingStack = player.getUseItem();
            if(!stack.is(ModItems.LAST_PRISM.get()) || !player.isUsingItem() || !usingStack.is(ModItems.LAST_PRISM.get())) {
                this.onDied(beam);
                return;
            }

            beam.setPos(tri.position());
            BeamData beamData = computeBeamData(tri, getBeamIndex(beam), 0.0f);
            if(beamData == null) return;

            if(beamData.blocked() && beam.level() instanceof ServerLevel serverLevel) {
                ParticleUtil.addParticles(
                    serverLevel,
                    PARTICLE,
                    beamData.end(),
                    new Vec3(0.08, 0.08, 0.08),
                    0.05,
                    2
                );
            }

            AABB searchBox = new AABB(beamData.start(), beamData.end()).inflate(1.0f);
            List<Entity> entities = beam.level().getEntities(
                beam,
                searchBox,
                FilterUtil.createTargetFilter(beam, player)
            );

            for(Entity hitEntity : entities) {
                AABB entityBox = hitEntity.getBoundingBox().inflate(beamData.radius());
                Optional<Vec3> clipResult = entityBox.clip(beamData.start(), beamData.end());
                if(entityBox.contains(beamData.start()) || clipResult.isPresent()) {
                    if(DamageUtil.magicAttack(beam, hitEntity, beam.getEntityData().get(StaticSummon.STACK_SOURCE), getDamage() * beamData.ratio(), 0.1f)) {
                        hitEntity.invulnerableTime = 5;
                    }
                }
            }
        }

        @Override
        public AABB getBoundingBoxForCulling(StaticSummon beam) {
            if(!(beam.getOwner() instanceof StaticSummon tri)) return IStaticSummonBehavior.super.getBoundingBoxForCulling(beam);
            BeamData beamData = computeBeamData(tri, getBeamIndex(beam), 0.0f);
            if(beamData == null) return IStaticSummonBehavior.super.getBoundingBoxForCulling(beam);
            return new AABB(beamData.start(), beamData.end()).inflate(MAX_RADIUS + 1.0f);
        }
    };

    private record BeamData(Vec3 start, Vec3 end, float radius, float ratio, boolean blocked) {}

    private static int getBeamIndex(StaticSummon beam) {
        CompoundTag customData = beam.getEntityData().get(StaticSummon.CUSTOM_DATA);
        return Mth.clamp(customData.getInt("idx"), 0, BEAM_COLORS.length - 1);
    }

    private static BeamData computeBeamData(StaticSummon tri, int idx, float partialTick) {
        Entity owner = tri.getOwner();
        if(owner == null) return null;

        Vector3f[] dirs = MathUtil.computeCoordinateSystem(owner);
        float time = tri.getEntityData().get(StaticSummon.AGE) + partialTick;
        float angle = computePrismAngle(time);
        float ratio = Math.min(1.0f, time / getChargeTime());
        double maxRange = getMaxRange();
        double dy = maxRange / 2;

        Vec3 basePos = tri.position().add(MathUtil.toVec3(dirs[0]).scale(0.5f));
        Vector3f baseDir = MathUtil.toVec3(dirs[0])
            .scale(maxRange)
            .add(MathUtil.toVec3(dirs[1]).scale(dy * (1.0f - ratio)))
            .toVector3f();
        Vector3f deltaDir = MathUtil.toVec3(dirs[1])
            .scale(DELTA_DIST * (1.0f - ratio) + MIN_DIST)
            .toVector3f();

        Quaternionf rotation = new Quaternionf().fromAxisAngleRad(dirs[0], (float) Math.toRadians(angle + 60.0 * idx));
        Vec3 shootDir = MathUtil.toVec3(baseDir.rotate(rotation));
        Vec3 shootPos = basePos.add(MathUtil.toVec3(deltaDir.rotate(rotation)));
        Vec3 endPos = shootPos.add(shootDir);
        boolean blocked = false;

        BlockHitResult blockHit = tri.level().clip(new ClipContext(
            shootPos,
            endPos,
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            tri
        ));
        if(blockHit.getType() != HitResult.Type.MISS) {
            endPos = blockHit.getLocation();
            blocked = true;
        }

        return new BeamData(shootPos, endPos, MAX_RADIUS * ratio, ratio, blocked);
    }

    private static float computePrismAngle(float time) {
        int chargeTime = getChargeTime();
        float a = MAX_ROTATE / chargeTime;
        float v = Math.min(a * time, MAX_ROTATE);
        if(v < MAX_ROTATE) {
            return 0.5f * a * time * time;
        }
        return 0.5f * a * chargeTime * chargeTime + v * time;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(!canUseMagic(stack, player)) return InteractionResultHolder.fail(stack);

        if(stack.is(ModItems.LAST_PRISM.get()) && !player.level().isClientSide()) {
            Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);

            StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), level);
            summon.setOwner(player);
            summon.getEntityData().set(StaticSummon.STACK_SOURCE, stack.copy());
            Vec3 pos = player.getEyePosition().add(MathUtil.toVec3(dirs[0]).scale(0.5)).add(MathUtil.toVec3(dirs[1]).scale(-1.0));
            summon.setPos(pos);
            summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.LAST_PRISM_TRI);
            summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
            summon.getEntityData().set(StaticSummon.LIFETIME, 72000);
            summon.getEntityData().set(StaticSummon.GLOW, true);

            summon.setNoGravity(true);
            summon.noPhysics = true;

            float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);
            summon.setXRot(xyRot[0]);
            summon.setYRot(xyRot[1]);

            level.addFreshEntity(summon);

            for(int i = 0; i < BEAM_COLORS.length; i++) {
                StaticSummon beam = new StaticSummon(ModEntities.STATIC_SUMMON.get(), level);
                Vector3f color = BEAM_COLORS[i];
                beam.setOwner(summon);
                beam.getEntityData().set(StaticSummon.STACK_SOURCE, stack.copy());
                beam.setPos(pos);
                beam.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.LAST_PRISM_BEAM);
                beam.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
                beam.getEntityData().set(StaticSummon.LIFETIME, getMaxUseTime());
                beam.getEntityData().set(StaticSummon.GLOW, true);
                beam.getEntityData().set(StaticSummon.COLOR_R, color.x);
                beam.getEntityData().set(StaticSummon.COLOR_G, color.y);
                beam.getEntityData().set(StaticSummon.COLOR_B, color.z);
                beam.getEntityData().set(StaticSummon.COLOR_A, 1.0f);

                CompoundTag customData = new CompoundTag();
                customData.putInt("idx", i);
                beam.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

                beam.setNoGravity(true);
                beam.noPhysics = true;

                level.addFreshEntity(beam);
            }

            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public @NonNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    protected void onMagicUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int count) {
        if(!(livingEntity instanceof Player player)) return;
        if(player.tickCount % 10 == 0) {
            SoundUtil.playClientSound(player, ModSounds.BEAM2.get());
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }





    private static float[] computeSoftPrismColor(float hue) {
        float normalizedHue = Math.floorMod((int)(hue * 1000.0F), 1000) / 1000.0F;
        int color = Mth.hsvToRgb(normalizedHue, 0.85F, 1.0F);
        float rainbowR = ((color >> 16) & 255) / 255.0F;
        float rainbowG = ((color >> 8) & 255) / 255.0F;
        float rainbowB = (color & 255) / 255.0F;
        float tint = 0.75F;

        return new float[]{
            1.0F * (1.0F - tint) + rainbowR * tint,
            1.0F * (1.0F - tint) + rainbowG * tint,
            1.0F * (1.0F - tint) + rainbowB * tint
        };
    }


    public static Vec3 computeRingDir(Vec3 n, Vec3 b, float ang) {
        return n.scale(Math.cos(ang)).add(b.scale(Math.sin(ang)));
    }

}
