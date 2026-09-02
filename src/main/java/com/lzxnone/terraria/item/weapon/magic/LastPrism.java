package com.lzxnone.terraria.item.weapon.magic;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.weapon.MagicWeapon;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.ParticleUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;

public class LastPrism extends MagicWeapon {
    private static final String USE_START_GAME_TIME_KEY = "lastPrismUseStartGameTime";

    public static final ConfigFloat DAMAGE = new ConfigFloat(
        "weapon.last_prism.damage",
        "last_prism_damage",
        3f,
        0.0f,
        8388600.0f
    );
    public static final ConfigInt CHARGE_TIME = new ConfigInt(
        "weapon.last_prism.charge_time",
        "last_prism_charge_time",
        67,
        1,
        72000
    );
    public static final ConfigInt MAX_USE_TIME = new ConfigInt(
        "weapon.last_prism.max_use_time",
        "last_prism_max_use_time",
        1200,
        1,
        72000
    );
    public static final ConfigDouble MAX_RANGE = new ConfigDouble(
        "weapon.last_prism.max_range",
        "last_prism_max_range",
        64.0,
        1.0,
        512.0
    );
    public static final ConfigInt MANA_CONSUME_RATE = new ConfigInt(
        "weapon.last_prism.mana_consume_rate",
        "last_prism_mana_consume_rate",
        144,
        0,
        10000
    );
    public LastPrism() {
        super(
            Tiers.NETHERITE,
            new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC)
        );
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                DAMAGE,
                CHARGE_TIME,
                MAX_USE_TIME,
                MAX_RANGE,
                MANA_CONSUME_RATE
            );
        }
    };

    public static int getChargeTime() {
        return CHARGE_TIME.get();
    }

    public static double getMaxRange() {
        return MAX_RANGE.get();
    }

    @Override
    public float getTooltipDamage(ItemStack weaponStack, LivingEntity entity) {
        float damage = entity instanceof Player player ? DamageUtil.applyPlayerDamageEffects(player, DAMAGE.get()) : DAMAGE.get();
        return applyMagicDamageBonus(weaponStack, entity, damage);
    }

    @Override
    protected double getManaConsumeRate(ItemStack stack, LivingEntity entity) {
        long startGameTime = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
            .copyTag()
            .getLong(USE_START_GAME_TIME_KEY);
        long elapsedTime = Math.max(0L, entity.level().getGameTime() - startGameTime);
        double useRatio = Mth.clamp(elapsedTime / (double)CHARGE_TIME.get(), 0.0D, 1.0D);
        return MANA_CONSUME_RATE.get() * useRatio / 20.0D;
    }

    @Override
    protected double getManaTooltipValue(ItemStack stack) {
        return MANA_CONSUME_RATE.get();
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
                if(!player.isUsingItem()) {
                    this.onDied(summon);
                    return;
                }
                ItemStack stack = player.getUseItem();
                if(!stack.is(ModItems.LAST_PRISM.get())) {
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
            ItemStack weaponStack = tri.getEntityData().get(StaticSummon.STACK_SOURCE);
            if(weaponStack.isEmpty() || !(weaponStack.getItem() instanceof LastPrism lastPrism)) {
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
                    DamageUtil.magicAttack(beam, hitEntity, beam.getEntityData().get(StaticSummon.STACK_SOURCE), DAMAGE.get() * beamData.ratio(), 0.1f, lastPrism.testConsumeMana(player, lastPrism.getFinalManaConsumeRate(weaponStack, player)) ? 2 : 5);
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
        float ratio = Math.min(1.0f, time / CHARGE_TIME.get());
        double maxRange = MAX_RANGE.get();
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
        int chargeTime = CHARGE_TIME.get();
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
        player.startUsingItem(hand);
        if(level.isClientSide()) return InteractionResultHolder.consume(stack);
        if(!canStartUsingPrism(player)) return InteractionResultHolder.fail(stack);

        if(stack.is(ModItems.LAST_PRISM.get())) {
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putLong(USE_START_GAME_TIME_KEY, level.getGameTime()));
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
                beam.getEntityData().set(StaticSummon.LIFETIME, MAX_USE_TIME.get());
                beam.getEntityData().set(StaticSummon.GLOW, true);
                beam.getEntityData().set(StaticSummon.COLOR_R, color.x);
                beam.getEntityData().set(StaticSummon.COLOR_G, color.y);
                beam.getEntityData().set(StaticSummon.COLOR_B, color.z);
                beam.getEntityData().set(StaticSummon.COLOR_A, 0.25f);

                CompoundTag customData = new CompoundTag();
                customData.putInt("idx", i);
                beam.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

                beam.setNoGravity(true);
                beam.noPhysics = true;

                level.addFreshEntity(beam);
            }
        }
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
        if(level.isClientSide()) return;
        if(!(entity instanceof Player player)) return;
        if(entity.tickCount % 10 == 0) {
            player.playNotifySound(ModSounds.BEAM2.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
        }
        /*if(!tryConsumeMana(player, getFinalManaConsumeRate(stack, player))) {
            player.stopUsingItem();
        }*/
        tryConsumeMana(player, getFinalManaConsumeRate(stack, player));
    }

    private boolean canStartUsingPrism(Player player) {
        /*if(player.hasInfiniteMaterials()) return true;
        if(player.getData(ModAttachments.PLAYER_MANA).getMana() > 0) return true;
        return tryAutoUseManaPotionToReachTarget(player, 1.0D);*/
        return true;
    }
}
