package com.lzxnone.terraria.item.weapon.magic;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.ModRenderTypes;
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
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
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
import org.joml.Matrix4f;
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
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;
            Entity owner = summon.getOwner();
            if(owner == null) return;

            Vector3f[] dirs = MathUtil.computeCoordinateSystem(owner);
            float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);

            float time = summon.getEntityData().get(StaticSummon.AGE) + partialTick;
            float size = 0.35F;
            float hue = (time * 0.02F) % 1.0F;
            float baseZ = -size / 3.0F;
            float baseRadius = size * 2.0F * Mth.sqrt(2.0F) / 3.0F;

            Vec3 v0 = new Vec3(0.0D, 0.0D, size);
            Vec3 v1 = new Vec3(baseRadius, 0.0D, baseZ);
            Vec3 v2 = new Vec3(baseRadius * Math.cos(Math.toRadians(120.0D)), baseRadius * Math.sin(Math.toRadians(120.0D)), baseZ);
            Vec3 v3 = new Vec3(baseRadius * Math.cos(Math.toRadians(240.0D)), baseRadius * Math.sin(Math.toRadians(240.0D)), baseZ);

            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
            poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));

            poseStack.mulPose(Axis.ZP.rotationDegrees(computePrismAngle(time)));

            Matrix4f matrix = poseStack.last().pose();
            VertexConsumer innerConsumer = bufferSource.getBuffer(ModRenderTypes.entitySolidEmissiveTriangles(RES));
            renderTetrahedronFace(innerConsumer, matrix, v0, v1, v2, 1.0F, 1.0F, 1.0F, 0.5f);
            renderTetrahedronFace(innerConsumer, matrix, v0, v3, v1, 1.0F, 1.0F, 1.0F, 0.5f);
            renderTetrahedronFace(innerConsumer, matrix, v0, v2, v3, 1.0F, 1.0F, 1.0F, 0.5f);
            renderTetrahedronFace(innerConsumer, matrix, v1, v3, v2, 1.0F, 1.0F, 1.0F, 0.5f);

            float outerScale = 1.2F;
            VertexConsumer outerConsumer = bufferSource.getBuffer(ModRenderTypes.entitySolidEmissiveTriangles(RES));
            renderTetrahedronFace(outerConsumer, matrix, v0.scale(outerScale), v1.scale(outerScale), v2.scale(outerScale), hue);
            renderTetrahedronFace(outerConsumer, matrix, v0.scale(outerScale), v3.scale(outerScale), v1.scale(outerScale), hue + 0.25F);
            renderTetrahedronFace(outerConsumer, matrix, v0.scale(outerScale), v2.scale(outerScale), v3.scale(outerScale), hue + 0.50F);
            renderTetrahedronFace(outerConsumer, matrix, v1.scale(outerScale), v3.scale(outerScale), v2.scale(outerScale), hue + 0.75F);
            poseStack.popPose();
        }

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
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon beam)) return;
            if(!(beam.getOwner() instanceof StaticSummon tri)) return;

            BeamData beamData = computeBeamData(tri, getBeamIndex(beam), partialTick);
            if(beamData == null) return;

            VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES));
            Matrix4f matrix = poseStack.last().pose();
            List<Vec3> points = List.of(beamData.start(), beamData.end());

            renderTube(consumer, matrix, beam.position(),
                points, beamData.radius() * 0.8f, 1.0f, 1.0f, 1.0f, 1.0f, false);
            renderTube(consumer, matrix, beam.position(),
                points, beamData.radius(), beam.getEntityData().get(StaticSummon.COLOR_A),
                beam.getEntityData().get(StaticSummon.COLOR_R),
                beam.getEntityData().get(StaticSummon.COLOR_G),
                beam.getEntityData().get(StaticSummon.COLOR_B), false);
        }

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
                    float damage = MagicWeapon.applyMagicDamageBonus(stack, player, getDamage() * beamData.ratio());
                    if(DamageUtil.normalAttack(beam, hitEntity, damage, 0.1f)) {
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

    private static void renderTetrahedronFace(
        VertexConsumer buffer,
        Matrix4f matrix,
        Vec3 v0,
        Vec3 v1,
        Vec3 v2,
        float hue
    ) {
        float[] color0 = computeSoftPrismColor(hue);
        float[] color1 = computeSoftPrismColor(hue + 0.10F);
        float[] color2 = computeSoftPrismColor(hue + 0.20F);

        renderTetrahedronVertex(buffer, matrix, v0, color0[0], color0[1], color0[2]);
        renderTetrahedronVertex(buffer, matrix, v1, color1[0], color1[1], color1[2]);
        renderTetrahedronVertex(buffer, matrix, v2, color2[0], color2[1], color2[2]);
    }

    private static void renderTetrahedronFace(
        VertexConsumer buffer,
        Matrix4f matrix,
        Vec3 v0,
        Vec3 v1,
        Vec3 v2,
        float r,
        float g,
        float b,
        float alpha
    ) {
        renderTetrahedronVertex(buffer, matrix, v0, r, g, b, alpha);
        renderTetrahedronVertex(buffer, matrix, v1, r, g, b, alpha);
        renderTetrahedronVertex(buffer, matrix, v2, r, g, b, alpha);
    }

    private static void renderTetrahedronVertex(
        VertexConsumer buffer,
        Matrix4f matrix,
        Vec3 vertex,
        float r,
        float g,
        float b
    ) {
        renderTetrahedronVertex(buffer, matrix, vertex, r, g, b, 1.0F);
    }

    private static void renderTetrahedronVertex(
        VertexConsumer buffer,
        Matrix4f matrix,
        Vec3 vertex,
        float r,
        float g,
        float b,
        float alpha
    ) {
        buffer.addVertex(matrix, (float)vertex.x, (float)vertex.y, (float)vertex.z)
            .setColor(r, g, b, alpha)
            .setUv(0.5F, 0.5F)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT)
            .setNormal(0.0F, 1.0F, 0.0F);
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

    public static void renderTube(VertexConsumer buffer, Matrix4f matrix, Vec3 entityWorldPos,
                                  List<Vec3> points, float radius, float alpha, float colorR, float colorG, float colorB, boolean linear) {
        int n = points.size();
        if(n < 2) return;

        Vector3f[][] oriDirs = new Vector3f[n][3];
        Vec3[] dir = new Vec3[n], up = new Vec3[n], right = new Vec3[n];
        for(int i = 0;i < n;i++) {
            if(i == n - 1) oriDirs[i] = MathUtil.computeCoordinateSystem(points.get(n - 1).subtract(points.get(n - 2)).toVector3f(), 0);
            else oriDirs[i] = MathUtil.computeCoordinateSystem(points.get(i + 1).subtract(points.get(i)).toVector3f(), 0);
            dir[i] = MathUtil.toVec3(oriDirs[i][0]);
            up[i] = MathUtil.toVec3(oriDirs[i][1]);
            right[i] = MathUtil.toVec3(oriDirs[i][2]);
        }

        for(int i = 0;i < n - 1;i++) {
            float r0 = linear ? radius * (1.0F - (float) i / (float) (n - 1)) : radius;
            float r1 = linear ? radius * (1.0F - (float) (i + 1) / (float) (n - 1)) : radius;
            if(r0 <= 0.001F && r1 <= 0.001F) continue;
            Vec3 N = right[i], B = up[i], N1 = right[i + 1], B1 = up[i + 1];
            Vec3 P = points.get(i), P1 = points.get(i + 1);
            for(int j = 0;j < RING;j++) {
                float a0 = (float) (2.0 * Math.PI * j / RING);
                float a1 = (float) (2.0 * Math.PI * (j + 1) / RING);
                Vec3 d0 = computeRingDir(N, B, a0);
                Vec3 d1 = computeRingDir(N1, B1, a0);
                Vec3 d0b = computeRingDir(N, B, a1);
                Vec3 d1b = computeRingDir(N1, B1, a1);
                Vec3 v0 = P.add(d0.scale(r0));
                Vec3 v1 = P1.add(d1.scale(r1));
                Vec3 v2 = P1.add(d1b.scale(r1));
                Vec3 v3 = P.add(d0b.scale(r0));
                for(int k = 0;k < 3;k++) {
                    writeVert(buffer, matrix, entityWorldPos, v0, 0.5f, 0.5f, alpha, colorR, colorG, colorB);
                    writeVert(buffer, matrix, entityWorldPos, v1, 0.5f, 0.5f, alpha, colorR, colorG, colorB);
                    writeVert(buffer, matrix, entityWorldPos, v2, 0.5f, 0.5f, alpha, colorR, colorG, colorB);
                    writeVert(buffer, matrix, entityWorldPos, v3, 0.5f, 0.5f, alpha, colorR, colorG, colorB);
                }
            }
        }
    }

    public static Vec3 computeRingDir(Vec3 n, Vec3 b, float ang) {
        return n.scale(Math.cos(ang)).add(b.scale(Math.sin(ang)));
    }

    public static void writeVert(VertexConsumer buffer, Matrix4f matrix, Vec3 entityWorldPos,
                                  Vec3 worldPos, float u, float v, float alpha, float colorR, float colorG, float colorB) {
        double lx = worldPos.x - entityWorldPos.x;
        double ly = worldPos.y - entityWorldPos.y;
        double lz = worldPos.z - entityWorldPos.z;

        buffer.addVertex(matrix, (float)lx, (float)ly, (float)lz)
            .setColor(colorR, colorG, colorB, alpha)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT)
            .setNormal(0.0F, 1.0F, 0.0F);
    }
}
