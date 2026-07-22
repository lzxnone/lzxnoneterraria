package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.projectile.IStaticProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.IItemWaveBehavior;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.network.payload.DevilsDevastationLeftClickPayload;
import com.lzxnone.terraria.particle.CircleParticleOptions;
import com.lzxnone.terraria.particle.IronSparkParticleOptions;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.utils.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import net.minecraft.client.Camera;
import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.ConfigUtil;
import com.lzxnone.terraria.ui.config.IConfigData;


import java.util.*;


public class DevilsDevastation extends SwordItem {
    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";

    public static final String KILL_MODE_TIME_PATH = "weapon.devils_devastation.kill_mode_time";
    public static final int KILL_MODE_TIME_DEFAULT = 100;
    public static final int KILL_MODE_TIME_MIN = 0;
    public static final int KILL_MODE_TIME_MAX = 72000;

    public static final String KILL_MODE_COOLDOWN_TIME_PATH = "weapon.devils_devastation.kill_mode_cooldown_time";
    public static final int KILL_MODE_COOLDOWN_TIME_DEFAULT = 100;
    public static final int KILL_MODE_COOLDOWN_TIME_MIN = 0;
    public static final int KILL_MODE_COOLDOWN_TIME_MAX = 72000;

    public static final String PROJECTILE_DAMAGE_PATH = "weapon.devils_devastation.projectile_damage";
    public static final float PROJECTILE_DAMAGE_DEFAULT = 30.0f;
    public static final float PROJECTILE_DAMAGE_MIN = 0.0f;
    public static final float PROJECTILE_DAMAGE_MAX = 8388600.0f;

    public static final String PROJECTILE_LIFETIME_PATH = "weapon.devils_devastation.projectile_lifetime";
    public static final int PROJECTILE_LIFETIME_DEFAULT = 40;
    public static final int PROJECTILE_LIFETIME_MIN = 1;
    public static final int PROJECTILE_LIFETIME_MAX = 1200;

    public static final String PROJECTILE_SPEED_PATH = "weapon.devils_devastation.projectile_speed";
    public static final double PROJECTILE_SPEED_DEFAULT = 3.0;
    public static final double PROJECTILE_SPEED_MIN = 0.0;
    public static final double PROJECTILE_SPEED_MAX = 10.0;

    public static final String STUCK_PROJECTILE_DAMAGE_PATH = "weapon.devils_devastation.stuck_projectile_damage";
    public static final float STUCK_PROJECTILE_DAMAGE_DEFAULT = 30.0f;
    public static final float STUCK_PROJECTILE_DAMAGE_MIN = 0.0f;
    public static final float STUCK_PROJECTILE_DAMAGE_MAX = 8388600.0f;

    public static final String STUCK_PROJECTILE_LIFETIME_PATH = "weapon.devils_devastation.stuck_projectile_lifetime";
    public static final int STUCK_PROJECTILE_LIFETIME_DEFAULT = 40;
    public static final int STUCK_PROJECTILE_LIFETIME_MIN = 1;
    public static final int STUCK_PROJECTILE_LIFETIME_MAX = 1200;

    public static final String STUCK_PROJECTILE_SPEED_PATH = "weapon.devils_devastation.stuck_projectile_speed";
    public static final double STUCK_PROJECTILE_SPEED_DEFAULT = 3.0;
    public static final double STUCK_PROJECTILE_SPEED_MIN = 0.0;
    public static final double STUCK_PROJECTILE_SPEED_MAX = 10.0;

    public static final String KILL_MODE_PROJECTILE_CYCLE_PATH = "weapon.devils_devastation.kill_mode_projectile_cycle";
    public static final int KILL_MODE_PROJECTILE_CYCLE_DEFAULT = 10;
    public static final int KILL_MODE_PROJECTILE_CYCLE_MIN = 1;
    public static final int KILL_MODE_PROJECTILE_CYCLE_MAX = 200;

    public static final String KILL_MODE_PROJECTILE_DAMAGE_PATH = "weapon.devils_devastation.kill_mode_projectile_damage";
    public static final float KILL_MODE_PROJECTILE_DAMAGE_DEFAULT = 200.0f;
    public static final float KILL_MODE_PROJECTILE_DAMAGE_MIN = 0.0f;
    public static final float KILL_MODE_PROJECTILE_DAMAGE_MAX = 8388600.0f;

    public static final String KILL_MODE_PROJECTILE_ROTATE_PATH = "weapon.devils_devastation.kill_mode_projectile_rotate";
    public static final int KILL_MODE_PROJECTILE_ROTATE_DEFAULT = 30;
    public static final int KILL_MODE_PROJECTILE_ROTATE_MIN = 0;
    public static final int KILL_MODE_PROJECTILE_ROTATE_MAX = 360;

    public static final String STUCK_LIFETIME_PATH = "weapon.devils_devastation.stuck_lifetime";
    public static final int STUCK_LIFETIME_DEFAULT = 200;
    public static final int STUCK_LIFETIME_MIN = 1;
    public static final int STUCK_LIFETIME_MAX = 72000;

    public static final String MAX_STUCK_COUNT_PATH = "weapon.devils_devastation.max_stuck_count";
    public static final int MAX_STUCK_COUNT_DEFAULT = 5;
    public static final int MAX_STUCK_COUNT_MIN = 1;
    public static final int MAX_STUCK_COUNT_MAX = 50;

    public static final String MARK_LIGHTNING_DAMAGE_PATH = "weapon.devils_devastation.mark_lightning_damage";
    public static final float MARK_LIGHTNING_DAMAGE_DEFAULT = 100.0f;
    public static final float MARK_LIGHTNING_DAMAGE_MIN = 0.0f;
    public static final float MARK_LIGHTNING_DAMAGE_MAX = 8388600.0f;

    public static final String PROJECTILE_EFFECT_DURATION_PATH = "weapon.devils_devastation.projectile_effect_duration";
    public static final int PROJECTILE_EFFECT_DURATION_DEFAULT = 40;
    public static final int PROJECTILE_EFFECT_DURATION_MIN = 0;
    public static final int PROJECTILE_EFFECT_DURATION_MAX = 72000;

    public static final String STUCK_PROJECTILE_EFFECT_DURATION_PATH = "weapon.devils_devastation.stuck_projectile_effect_duration";
    public static final int STUCK_PROJECTILE_EFFECT_DURATION_DEFAULT = 60;
    public static final int STUCK_PROJECTILE_EFFECT_DURATION_MIN = 0;
    public static final int STUCK_PROJECTILE_EFFECT_DURATION_MAX = 72000;

    public static final String KILL_MODE_PROJECTILE_EFFECT_DURATION_PATH = "weapon.devils_devastation.kill_mode_projectile_effect_duration";
    public static final int KILL_MODE_PROJECTILE_EFFECT_DURATION_DEFAULT = 70;
    public static final int KILL_MODE_PROJECTILE_EFFECT_DURATION_MIN = 0;
    public static final int KILL_MODE_PROJECTILE_EFFECT_DURATION_MAX = 72000;

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigFactory.loadIntConfig(KILL_MODE_TIME_PATH, configText("devils_devastation_kill_mode_time"), configTooltip("devils_devastation_kill_mode_time"), KILL_MODE_TIME_DEFAULT, KILL_MODE_TIME_MIN, KILL_MODE_TIME_MAX);
            ConfigFactory.loadIntConfig(KILL_MODE_COOLDOWN_TIME_PATH, configText("devils_devastation_kill_mode_cooldown_time"), configTooltip("devils_devastation_kill_mode_cooldown_time"), KILL_MODE_COOLDOWN_TIME_DEFAULT, KILL_MODE_COOLDOWN_TIME_MIN, KILL_MODE_COOLDOWN_TIME_MAX);
            ConfigFactory.loadFloatConfig(PROJECTILE_DAMAGE_PATH, configText("devils_devastation_projectile_damage"), configTooltip("devils_devastation_projectile_damage"), PROJECTILE_DAMAGE_DEFAULT, PROJECTILE_DAMAGE_MIN, PROJECTILE_DAMAGE_MAX);
            ConfigFactory.loadIntConfig(PROJECTILE_LIFETIME_PATH, configText("devils_devastation_projectile_lifetime"), configTooltip("devils_devastation_projectile_lifetime"), PROJECTILE_LIFETIME_DEFAULT, PROJECTILE_LIFETIME_MIN, PROJECTILE_LIFETIME_MAX);
            ConfigFactory.loadDoubleConfig(PROJECTILE_SPEED_PATH, configText("devils_devastation_projectile_speed"), configTooltip("devils_devastation_projectile_speed"), PROJECTILE_SPEED_DEFAULT, PROJECTILE_SPEED_MIN, PROJECTILE_SPEED_MAX);
            ConfigFactory.loadFloatConfig(STUCK_PROJECTILE_DAMAGE_PATH, configText("devils_devastation_stuck_projectile_damage"), configTooltip("devils_devastation_stuck_projectile_damage"), STUCK_PROJECTILE_DAMAGE_DEFAULT, STUCK_PROJECTILE_DAMAGE_MIN, STUCK_PROJECTILE_DAMAGE_MAX);
            ConfigFactory.loadIntConfig(STUCK_PROJECTILE_LIFETIME_PATH, configText("devils_devastation_stuck_projectile_lifetime"), configTooltip("devils_devastation_stuck_projectile_lifetime"), STUCK_PROJECTILE_LIFETIME_DEFAULT, STUCK_PROJECTILE_LIFETIME_MIN, STUCK_PROJECTILE_LIFETIME_MAX);
            ConfigFactory.loadDoubleConfig(STUCK_PROJECTILE_SPEED_PATH, configText("devils_devastation_stuck_projectile_speed"), configTooltip("devils_devastation_stuck_projectile_speed"), STUCK_PROJECTILE_SPEED_DEFAULT, STUCK_PROJECTILE_SPEED_MIN, STUCK_PROJECTILE_SPEED_MAX);
            ConfigFactory.loadIntConfig(KILL_MODE_PROJECTILE_CYCLE_PATH, configText("devils_devastation_kill_mode_projectile_cycle"), configTooltip("devils_devastation_kill_mode_projectile_cycle"), KILL_MODE_PROJECTILE_CYCLE_DEFAULT, KILL_MODE_PROJECTILE_CYCLE_MIN, KILL_MODE_PROJECTILE_CYCLE_MAX);
            ConfigFactory.loadFloatConfig(KILL_MODE_PROJECTILE_DAMAGE_PATH, configText("devils_devastation_kill_mode_projectile_damage"), configTooltip("devils_devastation_kill_mode_projectile_damage"), KILL_MODE_PROJECTILE_DAMAGE_DEFAULT, KILL_MODE_PROJECTILE_DAMAGE_MIN, KILL_MODE_PROJECTILE_DAMAGE_MAX);
            ConfigFactory.loadIntConfig(KILL_MODE_PROJECTILE_ROTATE_PATH, configText("devils_devastation_kill_mode_projectile_rotate"), configTooltip("devils_devastation_kill_mode_projectile_rotate"), KILL_MODE_PROJECTILE_ROTATE_DEFAULT, KILL_MODE_PROJECTILE_ROTATE_MIN, KILL_MODE_PROJECTILE_ROTATE_MAX);
            ConfigFactory.loadIntConfig(STUCK_LIFETIME_PATH, configText("devils_devastation_stuck_lifetime"), configTooltip("devils_devastation_stuck_lifetime"), STUCK_LIFETIME_DEFAULT, STUCK_LIFETIME_MIN, STUCK_LIFETIME_MAX);
            ConfigFactory.loadIntConfig(MAX_STUCK_COUNT_PATH, configText("devils_devastation_max_stuck_count"), configTooltip("devils_devastation_max_stuck_count"), MAX_STUCK_COUNT_DEFAULT, MAX_STUCK_COUNT_MIN, MAX_STUCK_COUNT_MAX);
            ConfigFactory.loadFloatConfig(MARK_LIGHTNING_DAMAGE_PATH, configText("devils_devastation_mark_lightning_damage"), configTooltip("devils_devastation_mark_lightning_damage"), MARK_LIGHTNING_DAMAGE_DEFAULT, MARK_LIGHTNING_DAMAGE_MIN, MARK_LIGHTNING_DAMAGE_MAX);
            ConfigFactory.loadIntConfig(PROJECTILE_EFFECT_DURATION_PATH, configText("devils_devastation_projectile_effect_duration"), configTooltip("devils_devastation_projectile_effect_duration"), PROJECTILE_EFFECT_DURATION_DEFAULT, PROJECTILE_EFFECT_DURATION_MIN, PROJECTILE_EFFECT_DURATION_MAX);
            ConfigFactory.loadIntConfig(STUCK_PROJECTILE_EFFECT_DURATION_PATH, configText("devils_devastation_stuck_projectile_effect_duration"), configTooltip("devils_devastation_stuck_projectile_effect_duration"), STUCK_PROJECTILE_EFFECT_DURATION_DEFAULT, STUCK_PROJECTILE_EFFECT_DURATION_MIN, STUCK_PROJECTILE_EFFECT_DURATION_MAX);
            ConfigFactory.loadIntConfig(KILL_MODE_PROJECTILE_EFFECT_DURATION_PATH, configText("devils_devastation_kill_mode_projectile_effect_duration"), configTooltip("devils_devastation_kill_mode_projectile_effect_duration"), KILL_MODE_PROJECTILE_EFFECT_DURATION_DEFAULT, KILL_MODE_PROJECTILE_EFFECT_DURATION_MIN, KILL_MODE_PROJECTILE_EFFECT_DURATION_MAX);
        }
    };

    private static Component configText(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key);
    }

    private static Component configTooltip(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key + ".tooltip");
    }

    public static int getKillModeTime() {
        return Math.clamp(ConfigUtil.readInt(KILL_MODE_TIME_PATH, KILL_MODE_TIME_DEFAULT), KILL_MODE_TIME_MIN, KILL_MODE_TIME_MAX);
    }

    public static int getKillModeCooldownTime() {
        return Math.clamp(ConfigUtil.readInt(KILL_MODE_COOLDOWN_TIME_PATH, KILL_MODE_COOLDOWN_TIME_DEFAULT), KILL_MODE_COOLDOWN_TIME_MIN, KILL_MODE_COOLDOWN_TIME_MAX);
    }

    public static float getProjectileDamage() {
        return Math.clamp(ConfigUtil.readFloat(PROJECTILE_DAMAGE_PATH, PROJECTILE_DAMAGE_DEFAULT), PROJECTILE_DAMAGE_MIN, PROJECTILE_DAMAGE_MAX);
    }

    public static int getProjectileLifetime() {
        return Math.clamp(ConfigUtil.readInt(PROJECTILE_LIFETIME_PATH, PROJECTILE_LIFETIME_DEFAULT), PROJECTILE_LIFETIME_MIN, PROJECTILE_LIFETIME_MAX);
    }

    public static double getProjectileSpeed() {
        return Math.clamp(ConfigUtil.readDouble(PROJECTILE_SPEED_PATH, PROJECTILE_SPEED_DEFAULT), PROJECTILE_SPEED_MIN, PROJECTILE_SPEED_MAX);
    }

    public static float getStuckProjectileDamage() {
        return Math.clamp(ConfigUtil.readFloat(STUCK_PROJECTILE_DAMAGE_PATH, STUCK_PROJECTILE_DAMAGE_DEFAULT), STUCK_PROJECTILE_DAMAGE_MIN, STUCK_PROJECTILE_DAMAGE_MAX);
    }

    public static int getStuckProjectileLifetime() {
        return Math.clamp(ConfigUtil.readInt(STUCK_PROJECTILE_LIFETIME_PATH, STUCK_PROJECTILE_LIFETIME_DEFAULT), STUCK_PROJECTILE_LIFETIME_MIN, STUCK_PROJECTILE_LIFETIME_MAX);
    }

    public static double getStuckProjectileSpeed() {
        return Math.clamp(ConfigUtil.readDouble(STUCK_PROJECTILE_SPEED_PATH, STUCK_PROJECTILE_SPEED_DEFAULT), STUCK_PROJECTILE_SPEED_MIN, STUCK_PROJECTILE_SPEED_MAX);
    }

    public static int getKillModeProjectileCycle() {
        return Math.clamp(ConfigUtil.readInt(KILL_MODE_PROJECTILE_CYCLE_PATH, KILL_MODE_PROJECTILE_CYCLE_DEFAULT), KILL_MODE_PROJECTILE_CYCLE_MIN, KILL_MODE_PROJECTILE_CYCLE_MAX);
    }

    public static float getKillModeProjectileDamage() {
        return Math.clamp(ConfigUtil.readFloat(KILL_MODE_PROJECTILE_DAMAGE_PATH, KILL_MODE_PROJECTILE_DAMAGE_DEFAULT), KILL_MODE_PROJECTILE_DAMAGE_MIN, KILL_MODE_PROJECTILE_DAMAGE_MAX);
    }

    public static int getKillModeProjectileRotate() {
        return Math.clamp(ConfigUtil.readInt(KILL_MODE_PROJECTILE_ROTATE_PATH, KILL_MODE_PROJECTILE_ROTATE_DEFAULT), KILL_MODE_PROJECTILE_ROTATE_MIN, KILL_MODE_PROJECTILE_ROTATE_MAX);
    }

    public static int getStuckLifetime() {
        return Math.clamp(ConfigUtil.readInt(STUCK_LIFETIME_PATH, STUCK_LIFETIME_DEFAULT), STUCK_LIFETIME_MIN, STUCK_LIFETIME_MAX);
    }

    public static int getMaxStuckCount() {
        return Math.clamp(ConfigUtil.readInt(MAX_STUCK_COUNT_PATH, MAX_STUCK_COUNT_DEFAULT), MAX_STUCK_COUNT_MIN, MAX_STUCK_COUNT_MAX);
    }

    public static float getMarkLightningDamage() {
        return Math.clamp(ConfigUtil.readFloat(MARK_LIGHTNING_DAMAGE_PATH, MARK_LIGHTNING_DAMAGE_DEFAULT), MARK_LIGHTNING_DAMAGE_MIN, MARK_LIGHTNING_DAMAGE_MAX);
    }

    public static int getProjectileEffectDuration() {
        return Math.clamp(ConfigUtil.readInt(PROJECTILE_EFFECT_DURATION_PATH, PROJECTILE_EFFECT_DURATION_DEFAULT), PROJECTILE_EFFECT_DURATION_MIN, PROJECTILE_EFFECT_DURATION_MAX);
    }

    public static int getStuckProjectileEffectDuration() {
        return Math.clamp(ConfigUtil.readInt(STUCK_PROJECTILE_EFFECT_DURATION_PATH, STUCK_PROJECTILE_EFFECT_DURATION_DEFAULT), STUCK_PROJECTILE_EFFECT_DURATION_MIN, STUCK_PROJECTILE_EFFECT_DURATION_MAX);
    }

    public static int getKillModeProjectileEffectDuration() {
        return Math.clamp(ConfigUtil.readInt(KILL_MODE_PROJECTILE_EFFECT_DURATION_PATH, KILL_MODE_PROJECTILE_EFFECT_DURATION_DEFAULT), KILL_MODE_PROJECTILE_EFFECT_DURATION_MIN, KILL_MODE_PROJECTILE_EFFECT_DURATION_MAX);
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "devils_devastation",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/devils_devastation.png"),
        Component.translatable("item.lzxnoneterraria.devils_devastation"),
        CONFIG_DATA
    );

    public DevilsDevastation() {
        super(Tiers.NETHERITE, new Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 30, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -2.4, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ).fireResistant().rarity(Rarity.EPIC));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("tooltip.lzxnoneterraria.devils_devastation.0").withStyle(style -> style.withColor(0xAE3288)));
        tooltipComponents.add(Component.translatable("tooltip.lzxnoneterraria.devils_devastation.1").withStyle(style -> style.withColor(0xAE3288)));
        tooltipComponents.add(Component.translatable("tooltip.lzxnoneterraria.devils_devastation.2").withStyle(style -> style.withColor(0xAE3288)));
    }

    public static final ResourceLocation RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/normal_trail.png");
    public static final ResourceLocation RES2 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/circular_smear_smokey.png");
    public static final ResourceLocation RES3 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/circular_smear_fire3.png");
    public static final ResourceLocation RES4 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/sylvestaff_streak.png");
    public static final ResourceLocation RES5 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/circular_smear_smokey_white.png");

    //圆柱的环数
    public static final int RING = 8;

    //能量剑体
    public static final float ENERGY_WAVE_LENGTH = 30;
    public static final int ENERGY_WAVE_SEG = 20;

    //右键射出的弹射
    public static final int PROJECTILE_TRAIL_LENGTH = 10;
    public static final float PROJECTILE_TRAIL_RADIUS = 0.25F;
    public static final double PROJECTILE_BOUNDING_SIZE = 1;

    //弹出的弹射
    public static final double STUCK_PROJECTILE_BOUNDING_SIZE = 3.0;

    //右键弹射击中生成的闪电
    public static final float PROJECTILE_HIT_LIGHTNING_RADIUS = 1.0F;
    public static final float PROJECTILE_HIT_LIGHTNING_LENGTH = 32F;
    public static final int PROJECTILE_HIT_LIGHTNING_DEPTH = 1;
    public static final float PROJECTILE_HIT_LIGHTNING_JITTER = 0.45F;

    //标记
    public static final float MARK_RADIUS = 0.25f;
    public static final float MARK_LENGTH = 2.0f;

    //标记触发后的闪电
    public static final float MARK_LIGHTNING_LENGTH = 32F;
    public static final float MARK_LIGHTNING_RADIUS = 3.0F;
    public static final int MARK_LIGHTNING_DEPTH = 3;
    public static final float MARK_LIGHTNING_JITTER = 0.45F;
    public static final int MARK_LIGHTNING_SUB_COUNT = 2;
    public static final float MARK_LIGHTNING_SUB_LENGTH = 0.4F;
    public static final float MARK_LIGHTNING_SUB_SPREAD = 0.6F;

    //右键射出的弹射
    public static final IStaticProjectileBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile projectile)) return;
            renderItem(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
            VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.entityAdditiveEmissive(RES));
            Vec3 entWorldPos = new Vec3(
                Mth.lerp(partialTick, projectile.xo, projectile.getX()),
                Mth.lerp(partialTick, projectile.yo, projectile.getY()),
                Mth.lerp(partialTick, projectile.zo, projectile.getZ())
            );
            Matrix4f matrix = poseStack.last().pose();
            renderTube(buffer, matrix, entWorldPos, projectile.trailPositions, PROJECTILE_TRAIL_RADIUS * 1.0F, 1.0f, 1,1,1, true);
            renderTube(buffer, matrix, entWorldPos, projectile.trailPositions, PROJECTILE_TRAIL_RADIUS * 1.25F, 1.0f * 0.5F, 0.7F,0.15F,0.55F, true);
            renderTube(buffer, matrix, entWorldPos, projectile.trailPositions, PROJECTILE_TRAIL_RADIUS * 1.5F, 1.0f * 0.2F, 0.5F,0.1F,0.4F, true);
            renderTube(buffer, matrix, entWorldPos, projectile.trailPositions2, PROJECTILE_TRAIL_RADIUS * 1.0F, 1.0f, 1,1,1, true);
            renderTube(buffer, matrix, entWorldPos, projectile.trailPositions2, PROJECTILE_TRAIL_RADIUS * 1.25F, 1.0f * 0.5F, 0.7F,0.15F,0.55F, true);
            renderTube(buffer, matrix, entWorldPos, projectile.trailPositions2, PROJECTILE_TRAIL_RADIUS * 1.5F, 1.0f * 0.2F, 0.5F,0.1F,0.4F, true);
        }

        @Override
        public void onMoving(StaticProjectile projectile) {
            Vec3 right = MathUtil.toVec3(projectile.getEntityData().get(StaticProjectile.RIGHT));
            projectile.trailPositions.addFirst(projectile.position().add(right.scale(PROJECTILE_TRAIL_RADIUS / 2 * 1.5f)));
            projectile.trailPositions2.addFirst(projectile.position().add(right.scale(-PROJECTILE_TRAIL_RADIUS / 2 * 1.5f)));
            while(projectile.trailPositions.size() > PROJECTILE_TRAIL_LENGTH) projectile.trailPositions.removeLast();
            while(projectile.trailPositions2.size() > PROJECTILE_TRAIL_LENGTH) projectile.trailPositions2.removeLast();

            projectile.setBoundingBox(new AABB(
                projectile.getX() - PROJECTILE_BOUNDING_SIZE, projectile.getY() - PROJECTILE_BOUNDING_SIZE, projectile.getZ() - PROJECTILE_BOUNDING_SIZE,
                projectile.getX() + PROJECTILE_BOUNDING_SIZE, projectile.getY() + PROJECTILE_BOUNDING_SIZE, projectile.getZ() + PROJECTILE_BOUNDING_SIZE
            ));

            if(!projectile.level().isClientSide()) {
                List<Entity> targets = projectile.level().getEntitiesOfClass(
                        Entity.class,
                        projectile.getBoundingBox(),
                        FilterUtil.createTargetFilter(projectile, projectile.getOwner())
                );
                for(Entity target : targets) {
                    this.onHitEntity(projectile, new EntityHitResult(target, target.position()));
                }
            }else {
                if(projectile.getRandom().nextInt(2) == 0) {
                    ParticleUtil.addParticle(
                        projectile.level(), ModParticles.DEVILS_DEVASTATION_RUNE_PARTICLE2.get(),
                        projectile.position(), 0.5,
                        new Vec3(0, 0, 0), 0.0
                    );
                }
            }
        }

        @Override
        public void onHitEntity(StaticProjectile projectile, EntityHitResult result) {
            if(!projectile.level().isClientSide()) {
                Entity target = result.getEntity();
                Entity owner = projectile.getOwner();
                if(owner == null) return;
                if(!FilterUtil.createTargetFilter(owner).test(target) || !(owner instanceof Player player)) return;
                if(DamageUtil.attack(player, target, (float) getProjectileDamage())) {
                    if(target instanceof LivingEntity le) {
                        MobEffectInstance effectInstance = new MobEffectInstance(ModEffects.DEMONIC_FLAMES, getProjectileEffectDuration(), 0);
                        le.addEffect(effectInstance);
                    }
                    target.invulnerableTime = 5;

                    StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), projectile.level());
                    summon.setOwner(target);
                    Vec3 pos = projectile.position();
                    summon.setPos(pos);
                    summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.DEVILS_DEVASTATION_STUCK_PROJECTILE);
                    summon.getEntityData().set(StaticSummon.RENDER_MODE, "item");
                    summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.DEVILS_DEVASTATION.get()));
                    summon.getEntityData().set(StaticSummon.SCALE_X, 4.0f);
                    summon.getEntityData().set(StaticSummon.SCALE_Y, 4.0f);
                    summon.getEntityData().set(StaticSummon.SCALE_Z, 4.0f);
                    summon.getEntityData().set(StaticSummon.RXP, -90);
                    summon.getEntityData().set(StaticSummon.RZP, -135);
                    summon.getEntityData().set(StaticSummon.LIFETIME, getStuckLifetime());
                    summon.getEntityData().set(StaticSummon.GLOW, true);

                    CompoundTag customData = new CompoundTag();
                    Vec3 deltaPos = pos.subtract(target.position());
                    customData.putDouble("dx", deltaPos.x);
                    customData.putDouble("dy", deltaPos.y);
                    customData.putDouble("dz", deltaPos.z);
                    customData.putUUID("uuid", player.getUUID());
                    summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

                    float[] xyRot = MathUtil.computeXYRot(projectile.getEntityData().get(StaticProjectile.DIRECTION), projectile.getEntityData().get(StaticProjectile.UP));
                    summon.setXRot(xyRot[0]);
                    summon.setYRot(xyRot[1]);
                    summon.xRotO = xyRot[0];
                    summon.yRotO = xyRot[1];

                    summon.level().addFreshEntity(summon);

                    if(!target.isAlive()) {
                        summonStuckProjectile(summon);
                    }
                    if(target.isAlive()) {
                        List<UUID> uuids = new ArrayList<>(target.getData(ModAttachments.STUCK_DEVILS_DEVASTATION_PROJECTILE));
                        if (uuids.size() >= getMaxStuckCount()) {
                            Entity stuckProjectile = ((ServerLevel) projectile.level()).getEntity(uuids.getFirst());
                            if (stuckProjectile != null && stuckProjectile.isAlive() && stuckProjectile instanceof StaticSummon stuckStaticProjectile)
                                summonStuckProjectile(stuckStaticProjectile);
                            uuids.removeFirst();
                        }
                        uuids.add(summon.getUUID());
                        target.setData(ModAttachments.STUCK_DEVILS_DEVASTATION_PROJECTILE, uuids);
                    }

                    StaticSummon lightning = new StaticSummon(ModEntities.STATIC_SUMMON.get(), projectile.level());
                    lightning.setOwner(owner);
                    lightning.setPos(pos);
                    lightning.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.DEVILS_DEVASTATION_LIGHTNING);
                    lightning.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
                    Vector3f dirVec = projectile.getEntityData().get(StaticProjectile.DIRECTION);
                    CompoundTag lightningData = new CompoundTag();
                    lightningData.putDouble("dirX", dirVec.x());
                    lightningData.putDouble("dirY", dirVec.y());
                    lightningData.putDouble("dirZ", dirVec.z());
                    lightningData.putLong("seed", projectile.getRandom().nextLong());
                    lightning.getEntityData().set(StaticSummon.CUSTOM_DATA, lightningData);
                    lightning.getEntityData().set(StaticSummon.LIFETIME, 10);
                    lightning.level().addFreshEntity(lightning);

                    for(int i = 0;i < 50;i++) {
                        Vec3 dir = MathUtil.toVec3(projectile.getEntityData().get(StaticProjectile.DIRECTION));
                        dir = spreadDir(dir, new Random(), 0.5f).normalize();
                        Vector3f[] dirs = MathUtil.computeCoordinateSystem(dir.toVector3f(), 0);
                        IronSparkParticleOptions ironSparkParticleOptions;
                        CircleParticleOptions circleParticleOptions;
                        if(projectile.getRandom().nextInt(2) == 0) {
                            ironSparkParticleOptions = new IronSparkParticleOptions(
                                    1.0f, 40, 4.0f, new Vector3f(0.729f, 0.396f, 0.345f), dirs[0].mul(0.2f), dirs[2]
                            );
                            circleParticleOptions = new CircleParticleOptions(
                                    0.1f, 40, new Vector3f(0.729f, 0.396f, 0.345f)
                            );
                        }else {
                            ironSparkParticleOptions = new IronSparkParticleOptions(
                                    1.0f, 40, 4.0f, new Vector3f(0.8f, 0.176f, 0.78f), dirs[0].mul(0.2f), dirs[2]
                            );
                            circleParticleOptions = new CircleParticleOptions(
                                    0.1f, 40, new Vector3f(0.8f, 0.176f, 0.78f)
                            );
                        }
                        ParticleUtil.addParticles(
                            (ServerLevel) (projectile.level()), ironSparkParticleOptions,
                            projectile.position().add(dir.scale(3.0f)), new Vec3(0, 0, 0),
                            0, 1
                        );
                        ParticleUtil.addParticles(
                            (ServerLevel) (projectile.level()), circleParticleOptions,
                            projectile.position().add(dir.scale(3.0f)), new Vec3(0, 0, 0),
                            0.2, 1
                        );
                    }
                    SoundUtil.playServerSound(projectile.level(), ModSounds.DEMON_SWORD_IMPACT.get(), projectile.position());
                    onDied(projectile);
                }
            }
        }

        @Override
        public void onHitBlock(StaticProjectile projectile, BlockHitResult result) {}
    };

    //弹出的弹射
    public static final IStaticProjectileBehavior PROJECTILE_BEHAVIOR2 = new IStaticProjectileBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile projectile)) return;
            ItemStack stack = projectile.getEntityData().get(StaticProjectile.ITEM);
            if(stack.isEmpty()) return;

            float scaleX = 0.05f;
            float scaleY = 0.05f;
            float scaleZ = 0.05f;

            float age = projectile.getEntityData().get(StaticProjectile.AGE) + partialTick;
            Vector3f dir = projectile.getEntityData().get(StaticProjectile.DIRECTION);
            Vector3f up = projectile.getEntityData().get(StaticProjectile.UP);
            int rotate = projectile.getEntityData().get(StaticProjectile.RZP);

            poseStack.pushPose();

            RenderUtil.applyRotate(poseStack, dir, up, age * 30 + 60, rotate);
            poseStack.scale(4.0f, 4.0f, 4.0f);

            Minecraft.getInstance().getItemRenderer().renderStatic(
                    stack,
                    ItemDisplayContext.NONE,
                    LightTexture.FULL_BRIGHT,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    bufferSource,
                    entity.level(),
                    0
            );
            poseStack.popPose();

            poseStack.pushPose();

            RenderUtil.applyRotate(poseStack, dir, up, age * 30 + 180, rotate);
            poseStack.scale(scaleX, scaleY, scaleZ);

            VertexConsumer buffer0 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES2));
            RenderUtil.renderQuad(poseStack.last().pose(), buffer0, 0.729f, 0.396f, 0.345f, 1.0f, 78, 78, 0, 0, 0);
            poseStack.popPose();

            poseStack.pushPose();

            RenderUtil.applyRotate(poseStack, dir, up, age * 30, rotate);
            poseStack.scale(scaleX, scaleY, scaleZ);

            VertexConsumer buffer1 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES3));
            RenderUtil.renderQuad(poseStack.last().pose(), buffer1, 0.8f, 0.176f, 0.78f, 1.0f, 78, 78, 0, 0, 0);
            poseStack.popPose();
        }

        @Override
        public void onMoving(StaticProjectile projectile) {
            projectile.setBoundingBox(new AABB(
                projectile.getX() - STUCK_PROJECTILE_BOUNDING_SIZE, projectile.getY() - STUCK_PROJECTILE_BOUNDING_SIZE, projectile.getZ() - STUCK_PROJECTILE_BOUNDING_SIZE,
                projectile.getX() + STUCK_PROJECTILE_BOUNDING_SIZE, projectile.getY() + STUCK_PROJECTILE_BOUNDING_SIZE, projectile.getZ() + STUCK_PROJECTILE_BOUNDING_SIZE
            ));

            if(!projectile.level().isClientSide()) {
                List<Entity> targets = projectile.level().getEntitiesOfClass(
                        Entity.class,
                        projectile.getBoundingBox(),
                        FilterUtil.createTargetFilter(projectile, projectile.getOwner())
                );
                for(Entity target : targets) {
                    this.onHitEntity(projectile, new EntityHitResult(target, target.position()));
                }
            }

            if(projectile.level().isClientSide()) {
                ParticleUtil.addParticle(
                    projectile.level(), ModParticles.DEVILS_DEVASTATION_RUNE_PARTICLE2.get(),
                    projectile.position(), 0.5,
                    new Vec3(0, 0, 0), 0.0
                );
                int age = projectile.getEntityData().get(StaticProjectile.AGE);
                Vector3f dir = projectile.getEntityData().get(StaticProjectile.DIRECTION);
                Vector3f up = projectile.getEntityData().get(StaticProjectile.UP);
                Vector3f right = projectile.getEntityData().get(StaticProjectile.RIGHT);
                int rotate = projectile.getEntityData().get(StaticProjectile.RZP);
                float[] xyRot = MathUtil.computeXYRot(dir, up);

                Quaternionf rotation = new Quaternionf()
                    .fromAxisAngleRad(up, (float) Math.toRadians(age * 30));
                Quaternionf rotation2 = new Quaternionf()
                    .fromAxisAngleRad(dir, (float) Math.toRadians(Math.abs(dir.y) > 0.999 ? 0 : rotate))
                    .rotateY((float) Math.toRadians(-xyRot[1]))
                    .rotateX((float) Math.toRadians(xyRot[0]));

                Vector3f currentDir = new Vector3f(0, 0, 1);
                currentDir.rotate(rotation);
                currentDir.rotate(rotation2);

                Vector3f speed = new Vector3f(currentDir.x, currentDir.y, currentDir.z).mul(0.2f);

                IronSparkParticleOptions ironSparkParticleOptions;
                if(projectile.getRandom().nextInt(2) == 0) {
                    ironSparkParticleOptions = new IronSparkParticleOptions(
                            1.0f, 40, 4.0f, new Vector3f(0.729f, 0.396f, 0.345f), speed, right
                    );
                }else {
                    ironSparkParticleOptions = new IronSparkParticleOptions(
                            1.0f, 40, 4.0f, new Vector3f(0.8f, 0.176f, 0.78f), speed, right
                    );
                }
                ParticleUtil.addParticle(
                    projectile.level(), ironSparkParticleOptions,
                    projectile.position().add(MathUtil.toVec3(currentDir).scale(4)), 0,
                    new Vec3(0, 0, 0), 0.0
                );
            }
        }

        @Override
        public void onHitEntity(StaticProjectile projectile, EntityHitResult result) {
            if(!projectile.level().isClientSide()) {
                Entity target = result.getEntity();
                Entity owner = projectile.getOwner();
                if(owner == null) return;
                if(!FilterUtil.createTargetFilter(owner).test(target) || !(owner instanceof Player player)) return;
                if(DamageUtil.attack(player, target, (float) getStuckProjectileDamage())) {
                    if(target instanceof LivingEntity le) {
                        MobEffectInstance effectInstance = new MobEffectInstance(ModEffects.DEMONIC_FLAMES, getStuckProjectileEffectDuration(), 0);
                        le.addEffect(effectInstance);
                    }
                }
            }
        }

        @Override
        public void onHitBlock(StaticProjectile projectile, BlockHitResult result) {}
    };

    //杀戮模式弹射
    public static final IStaticProjectileBehavior PROJECTILE_BEHAVIOR3 = new IStaticProjectileBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile projectile)) return;
            ItemStack itemStack = projectile.getEntityData().get(StaticProjectile.ITEM);
            if(itemStack == ItemStack.EMPTY) return;
            CompoundTag customData = projectile.getEntityData().get(StaticProjectile.CUSTOM_DATA);
            if(!customData.contains("w")) return;
            float w = customData.getFloat("w");
            float t = projectile.getEntityData().get(StaticProjectile.AGE) + partialTick;

            Vec3 dir = MathUtil.toVec3(projectile.getEntityData().get(StaticProjectile.DIRECTION)).normalize();
            Vec3 up = MathUtil.toVec3(projectile.getEntityData().get(StaticProjectile.UP)).normalize();

            Quaternionf rotationDir = new Quaternionf()
                .fromAxisAngleRad(dir.toVector3f(), (float) Math.toRadians(projectile.getEntityData().get(StaticProjectile.RZP)));
            Quaternionf rotationUp;
            Quaternionf rotationUp2;
            if(w > 0) {
                rotationUp = new Quaternionf().fromAxisAngleRad(up.toVector3f(), (float) Math.PI - w * t);
                rotationUp2 = new Quaternionf().fromAxisAngleRad(up.toVector3f(), (float) Math.PI * 0.75f - w * t);
            }else {
                rotationUp = new Quaternionf().fromAxisAngleRad(up.toVector3f(), (float) -Math.PI - w * t);
                rotationUp2 = new Quaternionf().fromAxisAngleRad(up.toVector3f(), (float) -Math.PI * 1.25f - w * t);
            }

            float[] xyRot = MathUtil.computeXYRot(dir.toVector3f(), up.toVector3f());

            poseStack.pushPose();

            //旋转
            poseStack.mulPose(rotationUp);
            poseStack.mulPose(rotationDir);
            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
            poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-135));

            //缩放
            poseStack.scale(4.0f, 4.0f, 4.0f);

            Minecraft.getInstance().getItemRenderer().renderStatic(
                    itemStack,
                    ItemDisplayContext.NONE,
                    LightTexture.FULL_BRIGHT,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    bufferSource,
                    entity.level(),
                    0
            );
            poseStack.popPose();

            poseStack.pushPose();

            Vector3f tipDir = dir.toVector3f().rotate(rotationUp);
            poseStack.translate(-tipDir.x * 2, -tipDir.y * 2, -tipDir.z * 2);
            poseStack.translate(-up.x * 0.5, -up.y * 0.5, -up.z * 0.5);
            poseStack.mulPose(rotationUp2);
            poseStack.mulPose(rotationDir);
            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
            poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-135));
            renderEnergyWave(bufferSource, poseStack, projectile);

            poseStack.popPose();

            float radio = t / projectile.getEntityData().get(StaticProjectile.LIFETIME);
            float alpha = 1.0f - radio;

            if(alpha > 0.001f) {
                poseStack.pushPose();

                poseStack.mulPose(rotationUp);
                poseStack.mulPose(rotationDir);
                poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
                poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));
                poseStack.mulPose(Axis.XP.rotationDegrees(-90));
                if(w > 0) poseStack.mulPose(Axis.ZP.rotationDegrees(-105));
                else poseStack.mulPose(Axis.ZP.rotationDegrees(75));

                float halfWidth = 78;
                float halfHeight = 78;
                VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.entityAdditiveEmissive(RES5));

                Vector3f[] sizes = new Vector3f[]{
                    new Vector3f(0.2f, 0.2f, 0.2f),
                    new Vector3f(0.3f, 0.3f, 0.3f),
                    new Vector3f(0.4f, 0.4f, 0.4f),
                };
                Vector3f[] colors = new Vector3f[]{
                    new Vector3f(0.725f, 0.345f, 1.0f),
                    new Vector3f(0.847f, 0.247f, 0.745f),
                    new Vector3f(0.882f, 0.345f, 0.247f),
                };

                for(int i = 0;i < 3;i++) {
                    poseStack.pushPose();
                    poseStack.scale(sizes[i].x, sizes[i].y, sizes[i].z);

                    for(int j = 0;j < 3;j++) {
                        consumer.addVertex(poseStack.last().pose(), -halfWidth, halfHeight, 0.01f * i)
                                .setColor(colors[i].x, colors[i].y, colors[i].z, alpha).setUv(0.0f, 0.0f)
                                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                        consumer.addVertex(poseStack.last().pose(), -halfWidth, -halfHeight, 0.01f * i)
                                .setColor(colors[i].x, colors[i].y, colors[i].z, alpha).setUv(0.0f, 1.0f)
                                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                        consumer.addVertex(poseStack.last().pose(), halfWidth, -halfHeight, 0.01f * i)
                                .setColor(colors[i].x, colors[i].y, colors[i].z, alpha).setUv(1.0f, 1.0f)
                                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                        consumer.addVertex(poseStack.last().pose(), halfWidth, halfHeight, 0.01f * i)
                                .setColor(colors[i].x, colors[i].y, colors[i].z, alpha).setUv(1.0f, 0.0f)
                                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                    }

                    poseStack.popPose();
                }

                poseStack.popPose();

            }

        }

        @Override
        public void onMoving(StaticProjectile projectile) {
            CompoundTag customData = projectile.getEntityData().get(StaticProjectile.CUSTOM_DATA);
            if(!customData.contains("w")) return;
            float w = customData.getFloat("w");
            int t = projectile.getEntityData().get(StaticProjectile.AGE);

            Vec3 dir = MathUtil.toVec3(projectile.getEntityData().get(StaticProjectile.DIRECTION)).normalize();
            Vec3 up = MathUtil.toVec3(projectile.getEntityData().get(StaticProjectile.UP)).normalize();

            Quaternionf rotationUp;
            if(w > 0) {
                rotationUp = new Quaternionf().fromAxisAngleRad(up.toVector3f(), (float) Math.PI - w * t);
            }else {
                rotationUp = new Quaternionf().fromAxisAngleRad(up.toVector3f(), (float) -Math.PI - w * t);
            }
            Vector3f tipDir = dir.toVector3f().rotate(rotationUp);
            Vec3 tipDir2 = MathUtil.toVec3(tipDir).normalize();
            Vector3f[] currentDirs = MathUtil.computeCoordinateSystem(tipDir, 0);

            //位置锁定
            if(projectile.getOwner() != null) projectile.getEntityData().set(StaticProjectile.ORIGIN, projectile.getOwner().getBoundingBox().getCenter().toVector3f());

            //实体击中判断
            Vec3 start = projectile.position();
            Vec3 end = start.add(tipDir2.scale(ENERGY_WAVE_LENGTH));
            double r = 4.0f;
            projectile.setBoundingBox(new AABB(
                Math.min(start.x, end.x) - r, Math.min(start.y, end.y) - r, Math.min(start.z, end.z) - r,
                Math.max(start.x, end.x) + r, Math.max(start.y, end.y) + r, Math.max(start.z, end.z) + r
            ));

            if(!projectile.level().isClientSide()) {
                List<Entity> targets = projectile.level().getEntitiesOfClass(
                        Entity.class,
                        projectile.getBoundingBox(),
                        FilterUtil.createTargetFilter(projectile, projectile.getOwner())
                );
                for(Entity target : targets) {
                    this.onHitEntity(projectile, new EntityHitResult(target, target.position()));
                }
            }

            //粒子
            for(int i = 0;i < 50;i++) {
                Vector3f speed = new Vector3f(
                        (float) (Math.random() * currentDirs[0].x),
                        (float) (Math.random() * currentDirs[0].y),
                        (float) (Math.random() * currentDirs[0].z)
                ).mul(0.2f);
                IronSparkParticleOptions ironSparkParticleOptions;
                if (projectile.getRandom().nextInt(2) == 0) {
                    ironSparkParticleOptions = new IronSparkParticleOptions(
                            1.0f, 20, 16.0f, new Vector3f(0.729f, 0.396f, 0.345f), speed, MathUtil.computeCoordinateSystem(speed, 0)[2]
                    );
                } else {
                    ironSparkParticleOptions = new IronSparkParticleOptions(
                            1.0f, 20, 16.0f, new Vector3f(0.8f, 0.176f, 0.78f), speed, MathUtil.computeCoordinateSystem(speed, 0)[2]
                    );
                }
                ParticleUtil.addParticle(
                        projectile.level(), ironSparkParticleOptions,
                        projectile.position().add(tipDir2.scale(Math.random() * ENERGY_WAVE_LENGTH)), 0,
                        new Vec3(0, 0, 0), 0.0
                );
            }
            for(int i = 0;i < 3;i++) {
                CircleParticleOptions circleParticleOptions;
                if(projectile.getRandom().nextInt(2) == 0) {
                    circleParticleOptions = new CircleParticleOptions(0.15f, 20, new Vector3f(0.729f, 0.396f, 0.345f));
                }else {
                    circleParticleOptions = new CircleParticleOptions(0.15f, 20, new Vector3f(0.8f, 0.176f, 0.78f));
                }

                ParticleUtil.addParticle(
                        projectile.level(), circleParticleOptions,
                        projectile.position().add(tipDir2.scale(ENERGY_WAVE_LENGTH)), 1.0,
                        new Vec3(0, 0, 0), 0.0
                );
                ParticleUtil.addParticle(
                        projectile.level(), ModParticles.DEVILS_DEVASTATION_RUNE_PARTICLE.get(),
                        projectile.position().add(tipDir2.scale(ENERGY_WAVE_LENGTH)), 1.0,
                        new Vec3(0, 0, 0), 0.2
                );
            }

        }

        @Override
        public void onHitEntity(StaticProjectile projectile, EntityHitResult result) {
            if(projectile.level() instanceof ServerLevel serverLevel) {
                Entity target = result.getEntity();
                List<UUID> tempStuckList = target.getData(ModAttachments.STUCK_DEVILS_DEVASTATION_PROJECTILE);
                boolean valid = !tempStuckList.isEmpty();
                if(projectile.getOwner() instanceof Player player && DamageUtil.attack(player, target, (float) getKillModeProjectileDamage())) {
                    if(target instanceof LivingEntity le) {
                        MobEffectInstance effectInstance = new MobEffectInstance(ModEffects.DEMONIC_FLAMES, getKillModeProjectileEffectDuration(), 1);
                        le.addEffect(effectInstance);
                    }

                    ItemStack stack = player.getMainHandItem();
                    if(stack.is(ModItems.DEVILS_DEVASTATION.get())) {
                        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                            .copyTag();
                        if(tag.hasUUID("mark")) {
                            UUID uuid = tag.getUUID("mark");
                            Entity entityMark = serverLevel.getEntity(uuid);
                            if(entityMark instanceof StaticSummon summon && summon.isAlive()) {
                                summon.discard();
                            }
                        }
                    }

                    StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), projectile.level());
                    summon.setOwner(target);
                    Vec3 pos = target.getBoundingBox().getCenter();
                    summon.setPos(pos);
                    summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.DEVILS_DEVASTATION_MARK);
                    summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
                    summon.getEntityData().set(StaticSummon.LIFETIME, 100);
                    summon.getEntityData().set(StaticSummon.GLOW, true);

                    CompoundTag summonCustomData = new CompoundTag();
                    summonCustomData.putUUID("uuid", player.getUUID());
                    summon.getEntityData().set(StaticSummon.CUSTOM_DATA, summonCustomData);

                    serverLevel.addFreshEntity(summon);

                    CustomData.update(DataComponents.CUSTOM_DATA, stack,
                            tag -> tag.putUUID("mark", summon.getUUID()));

                    CompoundTag customData = projectile.getEntityData().get(StaticProjectile.CUSTOM_DATA);
                    if(customData.contains("hit") && !customData.getBoolean("hit")) {
                        SoundUtil.playServerSound(projectile.level(), ModSounds.DEMON_SWORD_INSANE_IMPACT.get(), target.position(), 16.0f, 1.0f);
                        customData.putBoolean("hit", true);
                        projectile.getEntityData().set(StaticProjectile.CUSTOM_DATA, customData);
                    }
                    List<UUID> stuckList = new ArrayList<>(target.getData(ModAttachments.STUCK_DEVILS_DEVASTATION_PROJECTILE));
                    if(!customData.contains("validHit") && valid) {
                        customData.putBoolean("validHit", true);
                        while(!stuckList.isEmpty()) {
                            UUID uuid = stuckList.getLast();
                            stuckList.removeLast();
                            Entity stuck = serverLevel.getEntity(uuid);
                            if(stuck instanceof StaticSummon stuckProjectile) summonStuckProjectile(stuckProjectile);
                        }
                        target.setData(ModAttachments.STUCK_DEVILS_DEVASTATION_PROJECTILE, stuckList);
                    }
                    projectile.getEntityData().set(StaticProjectile.CUSTOM_DATA, customData);

                    StaticSummon lightning = new StaticSummon(ModEntities.STATIC_SUMMON.get(), projectile.level());
                    lightning.setOwner(player);
                    lightning.setPos(target.getBoundingBox().getCenter());
                    lightning.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.DEVILS_DEVASTATION_LIGHTNING);
                    lightning.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
                    Vector3f dirVec = target.getBoundingBox().getCenter().subtract(projectile.position()).normalize().toVector3f();
                    CompoundTag lightningData = new CompoundTag();
                    lightningData.putDouble("dirX", dirVec.x());
                    lightningData.putDouble("dirY", dirVec.y());
                    lightningData.putDouble("dirZ", dirVec.z());
                    lightningData.putLong("seed", projectile.getRandom().nextLong());
                    lightning.getEntityData().set(StaticSummon.CUSTOM_DATA, lightningData);
                    lightning.getEntityData().set(StaticSummon.LIFETIME, 10);
                    lightning.level().addFreshEntity(lightning);
                }
            }
        }

        @Override
        public void onDied(StaticProjectile projectile) {
            if(!projectile.level().isClientSide()) {
                CompoundTag customData = projectile.getEntityData().get(StaticProjectile.CUSTOM_DATA);
                if(projectile.getOwner() instanceof LivingEntity livingEntity) {
                    if(customData.contains("validHit")) {
                        MobEffectInstance effectInstance = new MobEffectInstance(ModEffects.KILL_MODE, getKillModeTime(), 0);
                        livingEntity.addEffect(effectInstance);

                        MobEffectInstance effectInstance2 = livingEntity.getEffect(ModEffects.KILL_MODE_COOLDOWN);
                        if(effectInstance2 != null) livingEntity.removeEffect(effectInstance2.getEffect());
                    }else {
                        MobEffectInstance effectInstance = new MobEffectInstance(ModEffects.KILL_MODE_COOLDOWN, getKillModeCooldownTime(), 0);
                        livingEntity.addEffect(effectInstance);

                        MobEffectInstance effectInstance2 = livingEntity.getEffect(ModEffects.KILL_MODE);
                        if(effectInstance2 != null) livingEntity.removeEffect(effectInstance2.getEffect());
                    }
                }
                projectile.discard();
            }
        }

        @Override
        public void onHitBlock(StaticProjectile projectile, BlockHitResult result) {}
    };

    //附着弹射
    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            if(!customData.contains("dx") || !customData.contains("dy") || !customData.contains("dz")) { onDied(summon); return; }
            Vec3 deltaPos = new Vec3(customData.getDouble("dx"), customData.getDouble("dy"), customData.getDouble("dz"));
            Entity entity = summon.getOwner();
            if(entity == null) { onDied(summon); return; }
            summon.setPos(entity.position().add(deltaPos));
        }
        @Override
        public void onDied(StaticSummon summon) {
            if(!summon.level().isClientSide()) {
                Entity entity = summon.getOwner();
                if(entity != null && entity.isAlive()) {
                    List<UUID> uuids = new ArrayList<>(entity.getData(ModAttachments.STUCK_DEVILS_DEVASTATION_PROJECTILE));
                    uuids.remove(summon.getUUID());
                    entity.setData(ModAttachments.STUCK_DEVILS_DEVASTATION_PROJECTILE, uuids);
                }
                summon.discard();
            }
        }
    };

    //右键弹射击中后生成的闪电
    public static final IStaticSummonBehavior SUMMON_BEHAVIOR2 = new IStaticSummonBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            if(!customData.contains("dirX") || !customData.contains("dirY") || !customData.contains("dirZ") || !customData.contains("seed")) return;
            Vec3 dir = new Vec3(customData.getDouble("dirX"), customData.getDouble("dirY"), customData.getDouble("dirZ")).normalize();
            long seed = customData.getLong("seed");
            int age = summon.getEntityData().get(StaticSummon.AGE);
            int lifetime = summon.getEntityData().get(StaticSummon.LIFETIME);
            if(lifetime <= 0) return;
            float lifeRatio = Mth.clamp((age + partialTick) / (float) lifetime, 0.0F, 1.0F);
            if(lifeRatio > 0.999f) return;
            float radius = PROJECTILE_HIT_LIGHTNING_RADIUS * (1.0f - lifeRatio);
            float alpha = 1.0f - lifeRatio;

            Vec3 entWorldPos = new Vec3(
                Mth.lerp(partialTick, summon.xo, summon.getX()),
                Mth.lerp(partialTick, summon.yo, summon.getY()),
                Mth.lerp(partialTick, summon.zo, summon.getZ())
            );
            VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.entityAdditiveEmissive(RES));
            Matrix4f matrix = poseStack.last().pose();

            Random rand0 = new Random(seed);
            Vec3 start0 = entWorldPos.add(dir.scale(4.0));
            Vec3 end0 = start0.add(dir.scale(PROJECTILE_HIT_LIGHTNING_LENGTH));
            List<Vec3> branch0 = generateBranch(start0, end0, rand0, PROJECTILE_HIT_LIGHTNING_DEPTH, PROJECTILE_HIT_LIGHTNING_JITTER);

            Random rand1 = new Random(seed + 1);
            Vec3 end1 = start0.add(spreadDir(dir, rand1, 0.4F).scale(PROJECTILE_HIT_LIGHTNING_LENGTH * 0.7F));
            List<Vec3> branch1 = generateBranch(start0, end1, rand1, PROJECTILE_HIT_LIGHTNING_DEPTH, PROJECTILE_HIT_LIGHTNING_JITTER);

            Random rand2 = new Random(seed + 2);
            Vec3 start2 = branch0.get(1);
            Vec3 end2 = start2.add(spreadDir(dir, rand2, 0.4F).scale(PROJECTILE_HIT_LIGHTNING_LENGTH * 0.5F));
            List<Vec3> branch2 = generateBranch(start2, end2, rand2, PROJECTILE_HIT_LIGHTNING_DEPTH, PROJECTILE_HIT_LIGHTNING_JITTER);

            for(List<Vec3> branch : List.of(branch0, branch1, branch2)) {
                renderTube(buffer, matrix, entWorldPos, branch, radius * 1.0F, alpha, 1,1,1, true);
                renderTube(buffer, matrix, entWorldPos, branch, radius * 1.25F, alpha * 0.5F, 0.7F,0.15F,0.55F, true);
                renderTube(buffer, matrix, entWorldPos, branch, radius * 0.75F, alpha * 0.2F, 0.5F,0.1F,0.4F, true);
            }
        }

        @Override
        public AABB getBoundingBoxForCulling(StaticSummon summon) {
            double m = PROJECTILE_HIT_LIGHTNING_LENGTH + 8.0;
            Vec3 p = summon.position();
            return new AABB(p.x - m, p.y - m, p.z - m, p.x + m, p.y + m, p.z + m);
        }
    };

    //标记
    public static final IStaticSummonBehavior SUMMON_BEHAVIOR3 = new IStaticSummonBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;

            Vec3 entWorldPos = new Vec3(
                Mth.lerp(partialTick, entity.xo, entity.getX()),
                Mth.lerp(partialTick, entity.yo, entity.getY()),
                Mth.lerp(partialTick, entity.zo, entity.getZ())
            );

            // 公告板：根据相机计算平面基向量
            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            Vec3 toCamera = camera.getPosition().subtract(entWorldPos).normalize();
            Vec3 up = new Vec3(0, 1, 0);
            Vec3 right = toCamera.cross(up).normalize();
            if(right.lengthSqr() < 0.01) right = new Vec3(1, 0, 0);
            Vec3 realUp = right.cross(toCamera).normalize();

            // 十字交叉的4个方向
            Vec3[] dirs = {
                realUp.add(right).normalize(),
                realUp.subtract(right).normalize(),
                realUp.scale(-1.0).add(right).normalize(),
                realUp.scale(-1.0).subtract(right).normalize()
            };

            float age = summon.getEntityData().get(StaticSummon.AGE) + partialTick;
            float lifetime = summon.getEntityData().get(StaticSummon.LIFETIME);
            float progress = age / lifetime;
            float alpha = 1.0f;
            float length = MARK_LENGTH + progress * MARK_LENGTH * 2;
            float radius = MARK_RADIUS + progress * MARK_RADIUS * 2;

            VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.entityAdditiveEmissive(RES));
            Matrix4f matrix = poseStack.last().pose();

            for(Vec3 dir : dirs) {
                List<Vec3> points = new ArrayList<>();
                points.add(entWorldPos);
                points.add(entWorldPos.add(dir.scale(length)));
                renderTube(buffer, matrix, entWorldPos, points, radius * 0.5f, alpha * 0.8f, 1.0f, 1.0f, 1.0f, true);
                renderTube(buffer, matrix, entWorldPos, points, radius, alpha, 0.8f, 0.176f, 0.78f, true);
            }
        }

        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            Entity entity = summon.getOwner();
            if(entity != null) {
                summon.setPos(entity.getBoundingBox().getCenter());
            }
            if(!summon.level().isClientSide()) {
                CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
                int left = summon.getEntityData().get(StaticSummon.LIFETIME) - summon.getEntityData().get(StaticSummon.AGE);
                if(left < 20 && !customData.contains("sound")) {
                    customData.putBoolean("sound", true);
                    summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
                    SoundUtil.playServerSound(summon.level(), ModSounds.DEMON_SWORD_FINAL_STRIKE.get(), summon.position(),16.0f, 1.0f);
                }
            }
        }

        @Override
        public void onDied(StaticSummon summon) {
            if(summon.level() instanceof ServerLevel serverLevel) {
                int age = summon.getEntityData().get(StaticSummon.AGE);
                int lifetime = summon.getEntityData().get(StaticSummon.LIFETIME);
                if(age < lifetime) {
                    summon.discard();
                    return;
                }
                Entity owner = summon.getOwner();
                if(owner != null) {
                    CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
                    if(customData.contains("uuid")) {
                        UUID uuid = customData.getUUID("uuid");
                        Entity entity = serverLevel.getEntity(uuid);
                        if(entity instanceof Player player) {
                            DamageUtil.attack(player, owner, (float) getMarkLightningDamage());
                            StaticSummon newSummon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), serverLevel);
                            newSummon.setOwner(player);
                            Vec3 pos = owner.getBoundingBox().getCenter();
                            newSummon.setPos(pos);
                            newSummon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.DEVILS_DEVASTATION_MARK_LIGHTNING);
                            newSummon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
                            newSummon.getEntityData().set(StaticSummon.LIFETIME, 10);
                            newSummon.getEntityData().set(StaticSummon.GLOW, true);

                            CompoundTag newCustomData = new CompoundTag();
                            newCustomData.putLong("seed", owner.blockPosition().hashCode() * 31L + System.currentTimeMillis());
                            newSummon.getEntityData().set(StaticSummon.CUSTOM_DATA, newCustomData);

                            serverLevel.addFreshEntity(newSummon);
                        }
                    }
                }
                //SoundUtil.playServerSound(summon.level(), ModSounds.DEMON_SWORD_INSANE_IMPACT.get(), summon.position(), 16.0f, 1.0f);
                summon.discard();
            }
        }
    };

    //标记触发后的大型闪电
    public static final IStaticSummonBehavior SUMMON_BEHAVIOR4 = new IStaticSummonBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            if(!customData.contains("seed")) return;
            long seed = customData.getLong("seed");
            int ageInt = summon.getEntityData().get(StaticSummon.AGE);
            int lifetime = summon.getEntityData().get(StaticSummon.LIFETIME);
            if(lifetime <= 0) return;
            float lifeRatio = Mth.clamp((ageInt + partialTick) / (float) lifetime, 0.0F, 1.0F);

            float lengthScale = Math.min(1.0F, lifeRatio * 2.0F);
            float length = MARK_LIGHTNING_LENGTH * lengthScale;
            if(length < 0.5F) return;

            float radius = MARK_LIGHTNING_RADIUS * (1.0F - lifeRatio);
            float alpha = 1.0F - lifeRatio;

            Vec3 entWorldPos = new Vec3(
                Mth.lerp(partialTick, summon.xo, summon.getX()),
                Mth.lerp(partialTick, summon.yo, summon.getY()),
                Mth.lerp(partialTick, summon.zo, summon.getZ())
            );
            VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.entityAdditiveEmissive(RES));
            Matrix4f matrix = poseStack.last().pose();

            Random rand = new Random(seed);
            float baseAngle = rand.nextFloat() * (float)Math.PI * 2;
            Vec3[] dirs = {
                new Vec3(Math.cos(baseAngle), 0, Math.sin(baseAngle)),
                new Vec3(Math.cos(baseAngle + Math.PI / 2), 0, Math.sin(baseAngle + Math.PI / 2)),
                new Vec3(Math.cos(baseAngle + Math.PI), 0, Math.sin(baseAngle + Math.PI)),
                new Vec3(Math.cos(baseAngle + Math.PI * 3 / 2), 0, Math.sin(baseAngle + Math.PI * 3 / 2))
            };

            for(int d = 0; d < 4; d++) {
                Random mainRand = new Random(seed + d);
                Vec3 dir = dirs[d];

                Vec3 start = entWorldPos;
                Vec3 end = start.add(dir.scale(length));
                List<Vec3> mainBranch = generateBranch(start, end, mainRand, MARK_LIGHTNING_DEPTH, MARK_LIGHTNING_JITTER);

                renderTube(buffer, matrix, entWorldPos, mainBranch, radius, alpha, 0.8f, 0.176f, 0.78f, true);
                renderTube(buffer, matrix, entWorldPos, mainBranch, radius * 0.5f, alpha * 0.8f, 1.0f, 1.0f, 1.0f, true);

                for(int s = 0; s < MARK_LIGHTNING_SUB_COUNT; s++) {
                    Random subRand = new Random(seed + d * 10 + s);
                    int idx = subRand.nextInt(mainBranch.size());
                    Vec3 branchPos = mainBranch.get(idx);
                    Vec3 subEnd = branchPos.add(spreadDir(dir, subRand, MARK_LIGHTNING_SUB_SPREAD).scale(length * MARK_LIGHTNING_SUB_LENGTH));
                    List<Vec3> subBranch = generateBranch(branchPos, subEnd, subRand, MARK_LIGHTNING_DEPTH, MARK_LIGHTNING_JITTER);

                    renderTube(buffer, matrix, entWorldPos, subBranch, radius, alpha, 0.8f, 0.176f, 0.78f, true);
                    renderTube(buffer, matrix, entWorldPos, subBranch, radius * 0.5f, alpha * 0.8f, 1.0f, 1.0f, 1.0f, true);
                }
            }
        }
    };

    //生成弹出弹射
    public static void summonStuckProjectile(StaticSummon stuckProjectile) {
        if(stuckProjectile == null || !stuckProjectile.isAlive()) return;
        CompoundTag customData = stuckProjectile.getEntityData().get(StaticSummon.CUSTOM_DATA);
        if(!customData.contains("uuid")) return;
        if(stuckProjectile.level() instanceof ServerLevel serverLevel) {
            UUID uuid = customData.getUUID("uuid");
            Entity entity = serverLevel.getEntity(uuid);
            if(!(entity instanceof Player player)) return;

            Vector3f[] dirs = MathUtil.computeCoordinateSystem(stuckProjectile);
            int rotate = (int) ((Math.random() * 2 - 1) * 90);
            dirs = MathUtil.rotateCoordinateSystem(dirs[0], dirs[2], rotate);

            StaticProjectile projectile = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), serverLevel);
            projectile.setOwner(player);
            Vec3 pos = stuckProjectile.position();
            projectile.setPos(pos);
            projectile.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.DEVILS_DEVASTATION_PROJECTILE2);
            projectile.getEntityData().set(StaticProjectile.RENDER_MODE, "custom");
            projectile.getEntityData().set(StaticProjectile.ORIGIN, pos.toVector3f());
            projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
            projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
            projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);
            projectile.getEntityData().set(StaticProjectile.ITEM, new ItemStack(ModItems.DEVILS_DEVASTATION.get()));
            projectile.getEntityData().set(StaticProjectile.RZP, rotate);
            projectile.getEntityData().set(StaticProjectile.LIFETIME, getStuckProjectileLifetime());
            projectile.getEntityData().set(StaticProjectile.GLOW, true);
            projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*t", getStuckProjectileSpeed()));
            projectile.setDeltaMovement(MathUtil.toVec3(dirs[0]));

            serverLevel.addFreshEntity(projectile);
            stuckProjectile.discard();
        }
    }

    //生成杀戮模式弹射
    public static void summonKilModeProjectile(Player player) {
        ItemStack itemStack = player.getMainHandItem();
        if(!itemStack.is(ModItems.DEVILS_DEVASTATION.get())) return;
        if(player.getCooldowns().isOnCooldown(itemStack.getItem())) return;
        player.getCooldowns().addCooldown(itemStack.getItem(), getKillModeProjectileCycle() + 10);
        if(!player.level().isClientSide()) {
            float radius = 4.0f;
            float w = (float) Math.PI * 1.5f / getKillModeProjectileCycle();
            if(player.getRandom().nextInt(2) == 0) w = -w;
            int rotate = (int) ((Math.random() * 2 - 1) * getKillModeProjectileRotate());
            Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
            dirs = MathUtil.rotateCoordinateSystem(dirs[0], dirs[2], rotate);

            StaticProjectile projectile = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), player.level());
            projectile.setOwner(player);
            Vec3 pos = player.getBoundingBox().getCenter();
            projectile.setPos(pos);
            projectile.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.DEVILS_DEVASTATION_PROJECTILE3);
            projectile.getEntityData().set(StaticProjectile.RENDER_MODE, "custom");
            projectile.getEntityData().set(StaticProjectile.ORIGIN, pos.toVector3f());
            projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
            projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
            projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);
            projectile.getEntityData().set(StaticProjectile.ITEM, new ItemStack(ModItems.DEVILS_DEVASTATION.get()));
            projectile.getEntityData().set(StaticProjectile.LIFETIME, getKillModeProjectileCycle());
            projectile.getEntityData().set(StaticProjectile.GLOW, true);
            projectile.getEntityData().set(StaticProjectile.RZP, rotate);
            projectile.getEntityData().set(StaticProjectile.EXPRESSION_X, w > 0 ? String.format("%.3f*cos(%.3f*t - 0.25 * 3.1415926)", radius, w) : String.format("%.3f*cos(%.3f*t - 0.75 * 3.1415926)", radius, w));
            projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, w > 0 ? String.format("%.3f*sin(%.3f*t - 0.25 * 3.1415926)", radius, w) : String.format("%.3f*sin(%.3f*t - 0.75 * 3.1415926)", radius, w));

            CompoundTag customData = new CompoundTag();
            customData.putFloat("w", w);
            customData.putBoolean("hit", false);
            projectile.getEntityData().set(StaticProjectile.CUSTOM_DATA, customData);

            player.level().addFreshEntity(projectile);
        }

    }

    public static void enterIntoKillMode(Player player) {
        if(!player.level().isClientSide()) return;
        SoundUtil.playClientSound(player, ModSounds.DEMON_SWORD_KILL_MODE.get());

        Vec3 pos = player.getBoundingBox().getCenter();
        Vector3f color = new Vector3f(0.8f, 0.176f, 0.78f);
        int count = 24;
        double radius = 2.0;

        Vec3[] dirs = new Vec3[count];
        for(int i = 0; i < count; i++) {
            double angle = 2.0 * Math.PI * i / count;
            dirs[i] = new Vec3(Math.cos(angle), 0, Math.sin(angle));
        }

        CircleParticleOptions options = new CircleParticleOptions(0.15f, 30, color);
        for(int i = 0; i < count; i++) {
            ParticleUtil.addParticle(
                player.level(), options,
                pos.add(dirs[i].scale(radius)), 0,
                dirs[i].scale(0.2), 0
            );
        }
        for(int i = 0; i < count; i++) {
            ParticleUtil.addParticle(
                player.level(), ModParticles.DEVILS_DEVASTATION_RUNE_PARTICLE2.get(),
                pos.add(dirs[i].scale(radius)), 0,
                dirs[i].scale(0.6), 0
            );
        }
    }

    public static List<Vec3> generateBranch(Vec3 start, Vec3 end, Random rand, int depth, float jitter) {
        List<Vec3> branch = new ArrayList<>();
        branch.add(start);
        subdivide(start, end, depth, jitter, rand, branch);
        branch.add(end);
        return branch;
    }

    public static Vec3 spreadDir(Vec3 dir, Random rand, float maxAngle) {
        float angle = (rand.nextFloat() - 0.5F) * 2.0F * maxAngle;
        Vec3 axisV = new Vec3(rand.nextDouble() - 0.5, rand.nextDouble() - 0.5, rand.nextDouble() - 0.5).normalize();
        if(axisV.lengthSqr() < 0.01D) axisV = new Vec3(1, 0, 0);
        axisV = axisV.cross(dir).normalize();
        if(axisV.lengthSqr() < 0.01D) axisV = new Vec3(0, 0, 1);
        Vector3f d = dir.toVector3f().normalize();
        new Quaternionf().fromAxisAngleRad(axisV.toVector3f().normalize(), angle).transform(d);
        return new Vec3(d.x(), d.y(), d.z());
    }

    private static void subdivide(Vec3 start, Vec3 end, int depth, float jitter, Random rand, List<Vec3> out) {
        if(depth <= 0) return;
        Vec3 mid = start.add(end).scale(0.5);
        Vec3 seg = end.subtract(start);
        Vector3f[] dirs = MathUtil.computeCoordinateSystem(seg.toVector3f(), 0);
        mid = getSurfaceRandomPosInRadius(mid, MathUtil.toVec3(dirs[2]), MathUtil.toVec3(dirs[1]), seg.length() * jitter, rand);
        subdivide(start, mid, depth - 1, jitter, rand, out);
        out.add(mid);
        subdivide(mid, end, depth - 1, jitter, rand, out);
    }

    public static Vec3 getSurfaceRandomPosInRadius(Vec3 pos, Vec3 axisX, Vec3 axisY, double radius, Random random) {
        double s = Math.sqrt(random.nextDouble());
        axisX = axisX.normalize().scale(radius).scale(s);
        axisY = axisY.normalize().scale(radius).scale(s);
        double rad = Math.PI * 2 * random.nextDouble();
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        return new Vec3(
            pos.x + cos * axisX.x + sin * axisY.x,
            pos.y + cos * axisX.y + sin * axisY.y,
            pos.z + cos * axisX.z + sin * axisY.z
        );
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

    public static void renderTubeSegmented(VertexConsumer buffer, Matrix4f matrix, Vec3 entityWorldPos,
                                            List<Vec3> points, float[] radii,
                                            Vector3f[] colors, float alpha,
                                            float uvOffsetU, float uvOffsetV, float uvScaleU, float uvScaleV) {
        int n = points.size();
        if(n < 2 || radii.length < n || colors.length < n) return;

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
            float r0 = radii[i];
            float r1 = radii[i + 1];
            if(r0 <= 0.001F && r1 <= 0.001F) continue;
            Vec3 N = right[i], B = up[i], N1 = right[i + 1], B1 = up[i + 1];
            Vec3 P = points.get(i), P1 = points.get(i + 1);
            Vector3f c0 = colors[i], c1 = colors[i + 1];

            float u0 = uvOffsetU + uvScaleU * ((float)i / (float)(n - 1));
            float u1 = uvOffsetU + uvScaleU * ((float)(i + 1) / (float)(n - 1));

            for(int j = 0;j < RING;j++) {
                float a0 = (float) (2.0 * Math.PI * j / RING);
                float a1 = (float) (2.0 * Math.PI * (j + 1) / RING);

                // 每个面独立映射完整纹理 V=[0,1]
                float v0 = (float) j / (float)RING + uvOffsetV;
                float v1 = (float) (j + 1) / (float)RING + uvOffsetV;

                Vec3 d0 = computeRingDir(N, B, a0);
                Vec3 d1 = computeRingDir(N1, B1, a0);
                Vec3 d0b = computeRingDir(N, B, a1);
                Vec3 d1b = computeRingDir(N1, B1, a1);
                Vec3 v00 = P.add(d0.scale(r0));
                Vec3 v01 = P1.add(d1.scale(r1));
                Vec3 v02 = P1.add(d1b.scale(r1));
                Vec3 v03 = P.add(d0b.scale(r0));
                for(int k = 0;k < 10;k++) {
                    writeVert(buffer, matrix, entityWorldPos, v00, u0, v0, alpha, c0.x(), c0.y(), c0.z());
                    writeVert(buffer, matrix, entityWorldPos, v01, u1, v0, alpha, c1.x(), c1.y(), c1.z());
                    writeVert(buffer, matrix, entityWorldPos, v02, u1, v1, alpha, c1.x(), c1.y(), c1.z());
                    writeVert(buffer, matrix, entityWorldPos, v03, u0, v1, alpha, c0.x(), c0.y(), c0.z());
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

    public static void renderEnergyWave(MultiBufferSource buffer, PoseStack poseStack, Entity entity) {
        Matrix4f matrix = poseStack.last().pose();
        int time = entity.tickCount;

        poseStack.translate(0, 1.4, 0.6);

        float baseRadius = 1.0f;
        float speed = 0.25f;

        Vector3f colorA = new Vector3f(0.729f, 0.396f, 0.345f);
        Vector3f colorB = new Vector3f(0.8f, 0.176f, 0.78f);

        List<Vec3> points = new ArrayList<>();
        float[] outRadius = new float[ENERGY_WAVE_SEG + 1];
        Vector3f[] outColors = new Vector3f[ENERGY_WAVE_SEG + 1];

        for(int i = 0; i <= ENERGY_WAVE_SEG; i++) {
            float t = (float) i / ENERGY_WAVE_SEG;
            float y = t * ENERGY_WAVE_LENGTH;

            float phase = time * speed * 6f - t * (float) Math.PI * 5;
            float wave = (float) Math.sin(phase);

            points.add(new Vec3(0, y, 0));

            float taper = (float) Math.cos(t * Math.PI * 0.5f);
            float pulse = 0.7f + 0.3f * (0.5f + 0.5f * wave);
            outRadius[i] = baseRadius * taper * pulse;

            float blend = 0.5f + 0.5f * (float) Math.sin(time * 0.3f * speed - t * (float) Math.PI * 2);
            outColors[i] = new Vector3f(
                colorA.x + (colorB.x - colorA.x) * blend,
                colorA.y + (colorB.y - colorA.y) * blend,
                colorA.z + (colorB.z - colorA.z) * blend
            );
        }

        float[] inRadius = new float[ENERGY_WAVE_SEG + 1];
        Vector3f[] inColors = new Vector3f[ENERGY_WAVE_SEG + 1];
        for(int i = 0; i <= ENERGY_WAVE_SEG; i++) {
            inRadius[i] = outRadius[i] * 0.1f;
            inColors[i] = new Vector3f(1.0f, 1.0f, 1.0f);
        }
        VertexConsumer consumer2 = buffer.getBuffer(ModRenderTypes.entityAdditiveEmissive(RES));
        renderTubeSegmented(consumer2, matrix, new Vec3(0, 0, 0), points, inRadius, inColors, 1.0f,
                    0, 0, 1.0f, 0.0f);

        float uOffset = time * 0.125f;
        float vOffset = time * 0.125f;
        VertexConsumer consumer = buffer.getBuffer(ModRenderTypes.entityAdditiveEmissive(RES4));
        for(int i = 0;i < 1;i++) {
            renderTubeSegmented(consumer, matrix, new Vec3(0, 0, 0), points, outRadius, outColors, 1.0f,
                    uOffset, 0 + vOffset, 2.0f, 0.0f);
            renderTubeSegmented(consumer, matrix, new Vec3(0, 0, 0), points, outRadius, outColors, 1.0f,
                    uOffset, 0.5f + vOffset, 2.0f, 0.0f);
        }
    }

    public static final IItemWaveBehavior ITEM_WAVE_BEHAVIOR = new IItemWaveBehavior() {
        @Override
        public void onLeftClickAir(PlayerInteractEvent.LeftClickEmpty event) {
            Player player = event.getEntity();
            ItemStack itemStack = player.getMainHandItem();
            if(itemStack.isEmpty()) return;
            if(itemStack.is(ModItems.DEVILS_DEVASTATION.get()) && player.getEffect(ModEffects.KILL_MODE) != null) {
                if(!player.getCooldowns().isOnCooldown(itemStack.getItem())) {
                    SoundUtil.playClientSound(player, ModSounds.DEMON_SWORD_SWING.get());
                    PacketDistributor.sendToServer(new DevilsDevastationLeftClickPayload());
                }
            }
        }

        @Override
        public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
            Player player = event.getEntity();
            ItemStack itemStack = player.getMainHandItem();
            if(itemStack.isEmpty()) return;
            if(itemStack.is(ModItems.DEVILS_DEVASTATION.get()) && player.getEffect(ModEffects.KILL_MODE) != null) {
                if(!player.getCooldowns().isOnCooldown(itemStack.getItem())) {
                    SoundUtil.playClientSound(player, ModSounds.DEMON_SWORD_SWING.get());
                    PacketDistributor.sendToServer(new DevilsDevastationLeftClickPayload());
                }
            }
        }

        @Override
        public void onAttackEntity(AttackEntityEvent event) {
            Player player = event.getEntity();
            ItemStack itemStack = player.getMainHandItem();
            if(itemStack.isEmpty()) return;
            if(itemStack.is(ModItems.DEVILS_DEVASTATION.get()) && player.getEffect(ModEffects.KILL_MODE) != null) {
                if(!player.getCooldowns().isOnCooldown(itemStack.getItem())) {
                    SoundUtil.playClientSound(player, ModSounds.DEMON_SWORD_SWING.get());
                    summonKilModeProjectile(player);
                    event.setCanceled(true);
                }
            }
        }
    };

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(player.getEffect(ModEffects.KILL_MODE) != null) return InteractionResultHolder.pass(stack);
        if(!level.isClientSide()) {
            Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
            StaticProjectile projectile = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), level);
            projectile.setOwner(player);
            Vec3 pos = player.getBoundingBox().getCenter();
            projectile.setPos(pos);
            projectile.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.DEVILS_DEVASTATION_PROJECTILE);
            projectile.getEntityData().set(StaticProjectile.RENDER_MODE, "custom");
            projectile.getEntityData().set(StaticProjectile.ORIGIN, pos.toVector3f());
            projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
            projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
            projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);
            projectile.getEntityData().set(StaticProjectile.ITEM, new ItemStack(ModItems.DEVILS_DEVASTATION.get()));
            projectile.getEntityData().set(StaticProjectile.SCALE_X, 4.0f);
            projectile.getEntityData().set(StaticProjectile.SCALE_Y, 4.0f);
            projectile.getEntityData().set(StaticProjectile.SCALE_Z, 4.0f);
            projectile.getEntityData().set(StaticProjectile.RXP, -90);
            projectile.getEntityData().set(StaticProjectile.RZP, -135);
            projectile.getEntityData().set(StaticProjectile.LIFETIME, getProjectileLifetime());
            projectile.getEntityData().set(StaticProjectile.GLOW, true);
            projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*t", getProjectileSpeed()));
            projectile.setDeltaMovement(MathUtil.toVec3(dirs[0]));
            level.addFreshEntity(projectile);
        }
        SoundUtil.playClientSound(player, ModSounds.DEMON_SWORD_SWING.get());
        player.getCooldowns().addCooldown(stack.getItem(), 10);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}

