package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.ConfigUtil;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.utils.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public class Meowmere extends SwordItem {
    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";

    public static final String DAMAGE_PATH = "weapon.meowmere.damage";
    public static final float DAMAGE_DEFAULT = 20.0f;
    public static final float DAMAGE_MIN = 0.0f;
    public static final float DAMAGE_MAX = 8388600.0f;

    public static final String GRAVITY_PATH = "weapon.meowmere.gravity";
    public static final double GRAVITY_DEFAULT = 0.025;
    public static final double GRAVITY_MIN = 0.0;
    public static final double GRAVITY_MAX = 1.0;

    public static final String INITIAL_SPEED_PATH = "weapon.meowmere.initial_speed";
    public static final double INITIAL_SPEED_DEFAULT = 1.5;
    public static final double INITIAL_SPEED_MIN = 0.0;
    public static final double INITIAL_SPEED_MAX = 10.0;

    public Meowmere() {
        super(Tiers.DIAMOND, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 19, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -2.4, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ).rarity(Rarity.RARE));
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigFactory.loadFloatConfig(DAMAGE_PATH, configText("meowmere_damage"), configTooltip("meowmere_damage"), DAMAGE_DEFAULT, DAMAGE_MIN, DAMAGE_MAX);
            ConfigFactory.loadDoubleConfig(GRAVITY_PATH, configText("meowmere_gravity"), configTooltip("meowmere_gravity"), GRAVITY_DEFAULT, GRAVITY_MIN, GRAVITY_MAX);
            ConfigFactory.loadDoubleConfig(INITIAL_SPEED_PATH, configText("meowmere_initial_speed"), configTooltip("meowmere_initial_speed"), INITIAL_SPEED_DEFAULT, INITIAL_SPEED_MIN, INITIAL_SPEED_MAX);
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

    public static double getGravity() {
        return Math.clamp(ConfigUtil.readDouble(GRAVITY_PATH, GRAVITY_DEFAULT), GRAVITY_MIN, GRAVITY_MAX);
    }

    public static double getInitialSpeed() {
        return Math.clamp(ConfigUtil.readDouble(INITIAL_SPEED_PATH, INITIAL_SPEED_DEFAULT), INITIAL_SPEED_MIN, INITIAL_SPEED_MAX);
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "meowmere",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/meowmere.png"),
        Component.translatable("item.lzxnoneterraria.meowmere"),
        CONFIG_DATA
    );

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.05f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(1.00f, 0.50f, 0.50f), // 淡红 (柔粉红)
            new Vector3f(1.00f, 0.75f, 0.50f), // 淡橙 (奶油橘)
            new Vector3f(1.00f, 1.00f, 0.50f), // 淡黄 (米黄色)
            new Vector3f(0.50f, 1.00f, 0.50f), // 淡绿 (薄荷绿)
            new Vector3f(0.50f, 1.00f, 1.00f), // 淡青 (冰蓝色)
            new Vector3f(0.50f, 0.50f, 1.00f), // 淡蓝 (天蓝色)
            new Vector3f(0.75f, 0.50f, 1.00f)  // 淡紫 (薰衣草)
        }
    );

    public static final DustParticleOptions PARTICLE2 = new DustParticleOptions(
        0.2f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(1.00f, 0.50f, 0.50f), // 淡红 (柔粉红)
            new Vector3f(1.00f, 0.75f, 0.50f), // 淡橙 (奶油橘)
            new Vector3f(1.00f, 1.00f, 0.50f), // 淡黄 (米黄色)
            new Vector3f(0.50f, 1.00f, 0.50f), // 淡绿 (薄荷绿)
            new Vector3f(0.50f, 1.00f, 1.00f), // 淡青 (冰蓝色)
            new Vector3f(0.50f, 0.50f, 1.00f), // 淡蓝 (天蓝色)
            new Vector3f(0.75f, 0.50f, 1.00f)  // 淡紫 (薰衣草)
        }
    );

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        public static final ResourceLocation RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/rainbow.png");
        public static final int MAX_LENGTH = 100;

        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;

            this.renderItem(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

            Vec3 currentPos = summon.getPosition(partialTick);

            //VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RES));
            VertexConsumer vertexConsumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES));
            Matrix4f matrix = poseStack.last().pose();

            int quadCount = summon.trailPositions.size() / 2 - 1;
            for(int i = 0; i < quadCount;i++) {
                Vec3 currentPoint1 = summon.trailPositions.get(i * 2);
                Vec3 currentPoint2 = summon.trailPositions.get(i * 2 + 1);
                Vec3 nextPoint1 = summon.trailPositions.get(i * 2 + 3);
                Vec3 nextPoint2 = summon.trailPositions.get(i * 2 + 2);

                double x1 = currentPoint1.x - currentPos.x;
                double y1 = currentPoint1.y - currentPos.y;
                double z1 = currentPoint1.z - currentPos.z;

                double x2 = currentPoint2.x - currentPos.x;
                double y2 = currentPoint2.y - currentPos.y;
                double z2 = currentPoint2.z - currentPos.z;

                double x3 = nextPoint1.x - currentPos.x;
                double y3 = nextPoint1.y - currentPos.y;
                double z3 = nextPoint1.z - currentPos.z;

                double x4 = nextPoint2.x - currentPos.x;
                double y4 = nextPoint2.y - currentPos.y;
                double z4 = nextPoint2.z - currentPos.z;

                float alpha1 = 1.0F - (float) i / quadCount;
                float alpha2 = 1.0F - (float) (i + 1) / quadCount;

                vertexConsumer.addVertex(matrix, (float)x1, (float)y1, (float)z1)
                    .setColor(1.0f, 1.0f, 1.0f, alpha1).setUv(0.0f, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                vertexConsumer.addVertex(matrix, (float)x2, (float)y2, (float)z2)
                    .setColor(1.0f, 1.0f, 1.0f, alpha1).setUv(0.0f, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                vertexConsumer.addVertex(matrix, (float)x3, (float)y3, (float)z3)
                    .setColor(1.0f, 1.0f, 1.0f, alpha2).setUv(1.0f, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                vertexConsumer.addVertex(matrix, (float)x4, (float)y4, (float)z4)
                    .setColor(1.0f, 1.0f, 1.0f, alpha2).setUv(1.0f, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
            }
        }

        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            Vec3 motion = summon.getDeltaMovement();
            motion = motion.add(0, -getGravity(), 0);
            Vec3 up = MathUtil.toVec3(MathUtil.computeCoordinateSystem(motion.toVector3f(), 0)[1]).normalize();

            summon.trailPositions.addFirst(summon.position().add(up.scale(0.5)));
            summon.trailPositions.addFirst(summon.position().add(up.scale(-0.5)));
            while(summon.trailPositions.size() > MAX_LENGTH) summon.trailPositions.removeLast();

            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);

            EntityHitResult entityHitResult = CollisionUtil.checkEntityHit(summon, summon.position().add(motion));
            if(entityHitResult != null) {
                Entity target = entityHitResult.getEntity();
                if(summon.getOwner() instanceof Player player) {
                    if(DamageUtil.attack(player, target, (float) getDamage())) {
                        if(customData.contains("hitEntity") && customData.getInt("hitEntity") < 4) {
                            customData.putInt("hitEntity", customData.getInt("hitEntity") + 1);
                            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
                        }else {
                            onDied(summon);
                            return;
                        }
                        target.invulnerableTime = 2;
                    }
                }
                return;
            }

            BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, summon.position().add(motion));
            if(blockHitResult.getType() != HitResult.Type.MISS) {
                if(customData.contains("hitBlock")) {
                    int hitBlock = customData.getInt("hitBlock");

                    int soundIndex = summon.getRandom().nextInt(2);
                    if(soundIndex == 0) SoundUtil.playServerSound(summon.level(), ModSounds.CAT.get(), summon.position());
                    else SoundUtil.playServerSound(summon.level(), ModSounds.CAT2.get(), summon.position());

                    if(summon.level().isClientSide()) {
                        Vec3 dir = summon.getDeltaMovement();
                        Vector3f[] dirs = MathUtil.computeCoordinateSystem(dir.toVector3f(), 0);
                        int count = (hitBlock + 1) * 15;
                        double deltaAngle = 360.0 / count;
                        for(int i = 0;i < count;i++) {
                            double angle = i * deltaAngle;
                            double cos = Math.cos(Math.toRadians(angle));
                            double sin = Math.sin(Math.toRadians(angle));
                            Vec3 speed = new Vec3(
                                dirs[1].x * cos + dirs[2].x * sin,
                                dirs[1].y * cos + dirs[2].y * sin,
                                dirs[1].z * cos + dirs[2].z * sin
                            );
                            ParticleUtil.addParticle(
                                summon.level(), PARTICLE2,
                                summon.position(), 0.0,
                                speed.scale(0.05 * (hitBlock + 1)), 0.0
                            );
                        }
                    }else {
                        if(summon.getOwner() instanceof Player player) {
                            List<LivingEntity> hitEntities = summon.level().getEntitiesOfClass(
                                LivingEntity.class,
                                new AABB(
                                    summon.getX() - 0.25 * (hitBlock + 1), summon.getY() - 0.25 * (hitBlock + 1), summon.getZ() - 0.25 * (hitBlock + 1),
                                    summon.getX() + 0.25 * (hitBlock + 1), summon.getY() + 0.25 * (hitBlock + 1), summon.getZ() + 0.25 * (hitBlock + 1)
                                ),
                                FilterUtil.createLivingTargetFilter(summon.getOwner())
                            );
                            for(LivingEntity livingEntity : hitEntities) livingEntity.hurt(summon.damageSources().playerAttack(player), (float) getDamage());
                        }
                    }

                    if(hitBlock < 4) {
                        customData.putInt("hitBlock", hitBlock + 1);
                        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

                        Direction face = blockHitResult.getDirection();

                        double mx = motion.x;
                        double my = motion.y;
                        double mz = motion.z;

                        switch(face.getAxis()) {
                            case X -> mx = -mx;
                            case Y -> my = -my;
                            case Z -> mz = -mz;
                        }

                        if(face == Direction.UP && Math.abs(my) < 0.1) {
                            my = 0;
                        }

                        motion = new Vec3(mx, my, mz);

                        Vec3 hitVec = blockHitResult.getLocation();
                        summon.setPos(
                            hitVec.x + face.getStepX() * 0.05,
                            hitVec.y + face.getStepY() * 0.05,
                            hitVec.z + face.getStepZ() * 0.05
                        );
                    }else {
                        onDied(summon);
                        return;
                    }
                }else {
                    onDied(summon);
                    return;
                }
            }
            summon.setDeltaMovement(motion);
        }

        @Override
        public AABB getBoundingBoxForCulling(StaticSummon summon) {
            if(summon.trailPositions.isEmpty()) return summon.getBoundingBox();
            return new AABB(summon.position(), summon.trailPositions.getLast());
        }

        @Override
        public void onDied(StaticSummon summon) {
            if(!summon.level().isClientSide()) {
                for(int i = 0;i < summon.trailPositions.size();i += 2) {
                    Vec3 pos1 = summon.trailPositions.get(i);
                    Vec3 pos2 = summon.trailPositions.get(i + 1);
                    Vec3 mid = new Vec3(
                        (pos1.x + pos2.x) / 2,
                        (pos1.y + pos2.y) / 2,
                        (pos1.z + pos2.z) / 2
                    );
                    ParticleUtil.addParticles(
                        (ServerLevel) summon.level(), PARTICLE,
                        mid, new Vec3(0, 0, 0), 0, 1
                    );
                }
                summon.discard();
            }
        }
    };

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if(!level.isClientSide()) {
            StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), level);
            summon.setOwner(player);
            Vec3 pos = player.getBoundingBox().getCenter();
            summon.setPos(pos);
            summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.MEOWMERE_PROJECTILE);
            summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
            summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.MEOWMERE_PROJECTILE.get()));
            summon.getEntityData().set(StaticSummon.RYP, -90);
            summon.getEntityData().set(StaticSummon.RZP, -90);
            summon.getEntityData().set(StaticSummon.LIFETIME, 200);
            summon.getEntityData().set(StaticSummon.GLOW, true);

            CompoundTag customData = new CompoundTag();
            customData.putInt("hitBlock", 0);
            customData.putInt("hitEntity", 0);
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

            summon.setNoGravity(true);
            summon.noPhysics = true;

            Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
            float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);
            summon.setXRot(xyRot[0]);
            summon.setYRot(xyRot[1]);
            summon.setDeltaMovement(player.getLookAngle().normalize().scale(getInitialSpeed()));
            level.addFreshEntity(summon);
        }else {
            ParticleUtil.addParticles(
                player.level(), PARTICLE,
                player.position(), 2.0,
                new Vec3(0, 0, 0), 0.1,
                25
            );
        }

        player.getCooldowns().addCooldown(stack.getItem(), 3);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}

