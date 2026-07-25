package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.item.weapon.MeleeWeapon;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.beam.SwordBeam;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.IItemWaveBehavior;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.ConfigUtil;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.ParticleUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.List;

public class LightsBane extends MeleeWeapon {
    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";

    public static final String BIG_DAMAGE_PATH = "weapon.lights_bane.big_damage";
    public static final float BIG_DAMAGE_DEFAULT = 4.0f;
    public static final float BIG_DAMAGE_MIN = 0.0f;
    public static final float BIG_DAMAGE_MAX = 8388600.0f;

    public static final String SMALL_DAMAGE_PATH = "weapon.lights_bane.small_damage";
    public static final float SMALL_DAMAGE_DEFAULT = 2.0f;
    public static final float SMALL_DAMAGE_MIN = 0.0f;
    public static final float SMALL_DAMAGE_MAX = 8388600.0f;

    public static final String TARGET_RANGE_PATH = "weapon.lights_bane.target_range";
    public static final double TARGET_RANGE_DEFAULT = 2.0;
    public static final double TARGET_RANGE_MIN = 1.0;
    public static final double TARGET_RANGE_MAX = 64.0;

    public LightsBane() {
        super(Tiers.IRON, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 3, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -1.0, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ));
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigFactory.loadFloatConfig(BIG_DAMAGE_PATH, configText("lights_bane_big_damage"), configTooltip("lights_bane_big_damage"), BIG_DAMAGE_DEFAULT, BIG_DAMAGE_MIN, BIG_DAMAGE_MAX);
            ConfigFactory.loadFloatConfig(SMALL_DAMAGE_PATH, configText("lights_bane_small_damage"), configTooltip("lights_bane_small_damage"), SMALL_DAMAGE_DEFAULT, SMALL_DAMAGE_MIN, SMALL_DAMAGE_MAX);
            ConfigFactory.loadDoubleConfig(TARGET_RANGE_PATH, configText("lights_bane_target_range"), configTooltip("lights_bane_target_range"), TARGET_RANGE_DEFAULT, TARGET_RANGE_MIN, TARGET_RANGE_MAX);
        }
    };

    private static Component configText(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key);
    }

    private static Component configTooltip(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key + ".tooltip");
    }

    public static float getBigDamage() {
        return Math.clamp(ConfigUtil.readFloat(BIG_DAMAGE_PATH, BIG_DAMAGE_DEFAULT), BIG_DAMAGE_MIN, BIG_DAMAGE_MAX);
    }

    public static float getSmallDamage() {
        return Math.clamp(ConfigUtil.readFloat(SMALL_DAMAGE_PATH, SMALL_DAMAGE_DEFAULT), SMALL_DAMAGE_MIN, SMALL_DAMAGE_MAX);
    }

    public static double getTargetRange() {
        return Math.clamp(ConfigUtil.readDouble(TARGET_RANGE_PATH, TARGET_RANGE_DEFAULT), TARGET_RANGE_MIN, TARGET_RANGE_MAX);
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "lights_bane",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/lights_bane.png"),
        Component.translatable("item.lzxnoneterraria.lights_bane"),
        CONFIG_DATA
    );

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.075f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(0.463F, 0.196F, 0.918F),
            new Vector3f(0.408F, 0.373F, 0.494F)
        }
    );

    public static final float SCALE_SMALL = 0.025f;
    public static final float SCALE_BIG = 0.05f;
    public static final int HALF_WIDTH = 85;
    public static final int HALF_HEIGHT = 27;

    public static final ResourceLocation[] RES = {
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/lights_bane_slash0.png"),
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/lights_bane_slash1.png"),
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/lights_bane_slash2.png"),
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/lights_bane_slash3.png"),
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/lights_bane_slash4.png"),
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/lights_bane_slash5.png"),
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/lights_bane_slash6.png"),
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/lights_bane_slash7.png"),
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/lights_bane_slash8.png"),
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/lights_bane_slash9.png"),
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/lights_bane_slash10.png"),
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/lights_bane_slash11.png"),
    };

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick,
                           PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;

            float lifeRatio = (summon.getEntityData().get(StaticSummon.AGE) + partialTick) / summon.getEntityData().get(StaticSummon.LIFETIME);
            if(lifeRatio > 1.0f) return;

            int frame = Math.min((int) (lifeRatio * 12.0f), 11);

            poseStack.pushPose();
            poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());

            VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES[frame]));
            Matrix4f matrix = poseStack.last().pose();

            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            float scale;
            if(customData.contains("big") && customData.getBoolean("big")) scale = SCALE_BIG;
            else scale = SCALE_SMALL;

            poseStack.mulPose(Axis.ZP.rotationDegrees(summon.getEntityData().get(StaticSummon.RZP)));

            consumer.addVertex(matrix, -HALF_WIDTH * scale, -HALF_HEIGHT * scale, 0)
                .setColor(255, 255, 255, 255).setUv(0.0f, 1.0f)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            consumer.addVertex(matrix, HALF_WIDTH * scale, -HALF_HEIGHT * scale, 0)
                .setColor(255, 255, 255, 255).setUv(1.0f, 1.0f)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            consumer.addVertex(matrix, HALF_WIDTH * scale, HALF_HEIGHT * scale, 0)
                .setColor(255, 255, 255, 255).setUv(1.0f, 0.0f)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            consumer.addVertex(matrix, -HALF_WIDTH * scale, HALF_HEIGHT * scale, 0)
                .setColor(255, 255, 255, 255).setUv(0.0f, 0.0f)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);

            poseStack.popPose();
        }

        @Override
        public void tick(StaticSummon summon) {
            //Vec3 center = summon.position().add(summon.getLookAngle().normalize().scale(SwordBeam.DIST));
            this.checkBeforeTick(summon);
            Vec3 pos = summon.position();
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            float scale;
            if(customData.contains("big") && customData.getBoolean("big")) scale = SCALE_BIG;
            else scale = SCALE_SMALL;
            float width = HALF_WIDTH * scale;
            float height = HALF_HEIGHT * scale;

            summon.setBoundingBox(new AABB(
                pos.x - height, pos.y - width, pos.z - height,
                pos.x + height, pos.y + width, pos.z + height
            ));

            if(!summon.level().isClientSide() && summon.getOwner() instanceof Player player) {
                List<Entity> targets = summon.level().getEntitiesOfClass(Entity.class, summon.getBoundingBox(), FilterUtil.createTargetFilter(summon, summon.getOwner()));
                for(Entity target : targets) {
                    float damage = customData.contains("big") && customData.getBoolean("big") ? (float) getBigDamage() : (float) getSmallDamage();
                    if(DamageUtil.normalAttack(summon, target, damage, 1.0f)) {

                    }
                }
            }
        }
    };

    public static final IItemWaveBehavior ITEM_WAVE_BEHAVIOR = new IItemWaveBehavior() {
        @Override
        public void onAttackEntity(AttackEntityEvent event) {
            Player player = event.getEntity();
            ItemStack itemStack = player.getMainHandItem();
            if(itemStack.isEmpty()) return;
            Item item = itemStack.getItem();
            if(item instanceof LightsBane) {
                if(!player.level().isClientSide()) {
                    if(!player.getCooldowns().isOnCooldown(item)) {
                        Entity target = event.getTarget();
                        Vec3 pos = target.position();
                        double targetRange = getTargetRange();
                        AABB searchBox = new AABB(
                            pos.x - targetRange, pos.y - targetRange, pos.z - targetRange,
                            pos.x + targetRange, pos.y + targetRange, pos.z + targetRange
                        );
                        List<Monster> entities = target.level().getEntitiesOfClass(
                            Monster.class,
                            searchBox,
                            FilterUtil.createMonsterFilter(player)
                        );
                        Monster summonTarget = null;
                        entities.sort(Comparator.comparingDouble(e -> e.distanceToSqr(pos)));
                        if(!entities.isEmpty()) summonTarget = entities.getFirst();
                        if(summonTarget != null) {
                            StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), player.level());
                            summon.setOwner(player);
                            summon.setPos(new Vec3(summonTarget.getX(), summonTarget.getY() + summonTarget.getBbHeight(), summonTarget.getZ()));
                            summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.LIGHTS_BANE_SLASH);
                            summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
                            summon.getEntityData().set(StaticSummon.RXP, -90);
                            summon.getEntityData().set(StaticSummon.RZP, -90);
                            summon.getEntityData().set(StaticSummon.GLOW, true);
                            summon.getEntityData().set(StaticSummon.LIFETIME, 10);
                            summon.setNoGravity(true);
                            summon.noPhysics = true;

                            CompoundTag customData = new CompoundTag();
                            customData.putBoolean("big", summon.getRandom().nextInt(5) == 0);
                            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

                            target.level().addFreshEntity(summon);
                            player.getCooldowns().addCooldown(item, 5);
                        }
                    }
                }else {
                    ParticleUtil.addParticles(
                        player.level(), PARTICLE,
                        player.position(), 0.2,
                        new Vec3(0, 0, 0), 0.2,
                        5
                    );
                }
            }
        }
    };
}

