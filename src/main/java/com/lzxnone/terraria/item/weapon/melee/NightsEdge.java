package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.Config;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.beam.ISwordBeamBehavior;
import com.lzxnone.terraria.entity.beam.SwordBeam;
import com.lzxnone.terraria.entity.beam.SwordBeamBehaviors;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.IItemWaveBehavior;
import com.lzxnone.terraria.network.payload.SwordBeamPayload;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.utils.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class NightsEdge extends SwordItem {
    public NightsEdge() {
        super(Tiers.IRON, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 6.0, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -1.5, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ).rarity(Rarity.UNCOMMON));
    }

    public static final CompoundTag BEAM_DATA = Util.make(new CompoundTag(), tag -> {
        tag.putString("behavior", "nights_edge");
        tag.putInt("lifetime", 7);
        tag.putInt("cooldown", 7);
        tag.putFloat("color0R", 0.165f);
        tag.putFloat("color0G", 0.098f);
        tag.putFloat("color0B", 0.247f);
        tag.putFloat("color1R", 0.278f);
        tag.putFloat("color1G", 0.141f);
        tag.putFloat("color1B", 0.588f);
        tag.putFloat("color2R", 0.325f);
        tag.putFloat("color2G", 0.212f);
        tag.putFloat("color2B", 0.553f);

        CompoundTag customData = new CompoundTag();
        customData.putInt("hitEntityCount", 0);
        tag.put("customData", customData);
    });

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.075f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(0.463F, 0.196F, 0.918F),
            new Vector3f(0.408F, 0.373F, 0.494F)
        }
    );

    public static final ISwordBeamBehavior SWORD_BEAM_BEHAVIOR = new ISwordBeamBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick,
                           PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if (!(entity instanceof SwordBeam beam)) return;
            Entity owner = beam.getOwner();
            if (owner == null) return;

            int age = beam.getEntityData().get(SwordBeam.AGE);
            int lifetime = Math.max(1, beam.getEntityData().get(SwordBeam.LIFETIME));

            float progress = (age + partialTick) / (float) lifetime;
            if (progress > 1.0f) return;

            Vector3f[] dirs = MathUtil.computeCoordinateSystem(beam.getOwner());
            dirs = MathUtil.rotateCoordinateSystem(dirs[0], dirs[2], beam.getEntityData().get(SwordBeam.ROTATE));
            float rotate = beam.getEntityData().get(SwordBeam.ROTATE);

            Vector3f color0 = entity.getEntityData().get(SwordBeam.COLOR0);
            Vector3f color1 = entity.getEntityData().get(SwordBeam.COLOR1);
            Vector3f color2 = entity.getEntityData().get(SwordBeam.COLOR2);
            Vector3f color3 = entity.getEntityData().get(SwordBeam.COLOR3);
            float alpha;

            if (progress <= FADE_IN) {
                alpha = 1 - (FADE_IN - progress) / FADE_IN;
            } else if (progress >= FADE_OUT) {
                alpha = 1 - (progress - FADE_OUT) / (1 - FADE_OUT);
            } else {
                alpha = 1.0f;
            }
            if (beam.getEntityData().get(SwordBeam.RIGHT)) progress = 1.0f - progress;

            float halfWidth = SwordBeam.HALF_WIDTH * SwordBeam.SCALE;
            float halfHeight = SwordBeam.HALF_HEIGHT * SwordBeam.SCALE;

            VertexConsumer vertexConsumer0 = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RES0));

            //左边
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress - 0.1f * (1.0f - progress)) * 180, SwordBeam.DIST);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - (progress - 0.1f * (1.0f - progress))) * 180, rotate);
            renderQuad(poseStack.last().pose(), vertexConsumer0,
                    color0.x(), color0.y(), color0.z(), alpha, halfWidth, halfHeight, 0f, 0, -0.01f);
            poseStack.popPose();

            //右边
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress + 0.1f) * 180, SwordBeam.DIST);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - (progress + 0.05f)) * 180, rotate);
            renderQuad(poseStack.last().pose(), vertexConsumer0,
                    color2.x(), color2.y(), color2.z(), alpha, halfWidth, halfHeight, 0f, 0, -0.02f);
            poseStack.popPose();

            //中间
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], progress * 180, SwordBeam.DIST);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - progress) * 180, rotate);
            renderQuad(poseStack.last().pose(), vertexConsumer0,
                    color1.x(), color1.y(), color1.z(), alpha, halfWidth, halfHeight, 0f, 0f, 0f);
            poseStack.popPose();

            VertexConsumer vertexConsumer1 = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RES3));

            //外线
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], progress * 180, SwordBeam.DIST * 1.15f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - progress) * 180, rotate);
            poseStack.scale(0.95f, 0.95f, 0.95f);
            renderQuad(poseStack.last().pose(), vertexConsumer1,
                    color3.x(), color3.y(), color3.z(), alpha, halfWidth, halfHeight, 0f, 0f, 0.02f);
            poseStack.popPose();

            VertexConsumer vertexConsumer2 = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RES5));

            //闪烁
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress + 0.25f) * 180, SwordBeam.DIST * 2.25f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - progress - 0.25f) * 180, rotate);
            poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
            poseStack.scale(1.0f, 1.0f, 1.0f);
            for (int i = 0; i < 20; i++) {
                renderQuad(poseStack.last().pose(), vertexConsumer2,
                        color1.x(), color1.y(), color1.z(), alpha, 32 * SwordBeam.SCALE, 32 * SwordBeam.SCALE, 0f, 0f, 0.03f);
            }
            poseStack.popPose();
        }

        @Override
        public void onMoving(SwordBeam beam) {
            if(beam.currentPosition == null) return;
            ParticleUtil.addParticle(
                beam.level(), PARTICLE,
                beam.currentPosition, 0.2,
                new Vec3(0, 0, 0), 0.2
            );
        }

        @Override
        public void onHitEntity(SwordBeam beam, EntityHitResult result) {
            if(!beam.level().isClientSide()) {
                Entity target = result.getEntity();
                if(beam.getOwner() instanceof Player player && FilterUtil.createTargetFilter(player).test(target)) {
                    CompoundTag custom_data = beam.getEntityData().get(SwordBeam.CUSTOM_DATA);
                    if(custom_data.contains("hitEntityCount")) {
                        int count = custom_data.getInt("hitEntityCount");
                        if(count < Config.nightsEdgeMaxHitCount) {
                            if(DamageUtil.attack(player, target, (float) Config.nightsEdgeDamage)) {
                                ParticleUtil.addParticles(
                                    (ServerLevel) target.level(), ModParticles.NIGHTS_EDGE_HIT_PARTICLE.get(),
                                    new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2.0, target.getZ()), new Vec3(0, 0, 0),
                                    0, 1
                                );
                                target.invulnerableTime = 20;
                                count++;
                                custom_data.putInt("hitEntityCount", count);
                                beam.getEntityData().set(SwordBeam.CUSTOM_DATA, custom_data);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void generate(Entity entity, CompoundTag beamData) {
            int randomAngle = (int) (Config.nightsEdgeRotateRange * (Math.random() * 2 - 1));
            beamData.putInt("rotate", randomAngle);
            ISwordBeamBehavior.super.generate(entity, beamData);
            if(entity instanceof Player player) summon(player, randomAngle);
        }
    };

    public static final Vector3f COLOR0 = new Vector3f(0.278f, 0.141f, 0.588f);

    public static final ResourceLocation RES0 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/terra_beam0.png");
    public static final ResourceLocation RES1 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/terra_beam3.png");
    public static final ResourceLocation RES2 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/beam_sparkle.png");

    public static final float FADE_IN = 0.33f;
    public static final float FADE_OUT = 0.67f;

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick,
                           PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;

            float halfWidth = SwordBeam.HALF_WIDTH * SwordBeam.SCALE;
            float halfHeight = SwordBeam.HALF_HEIGHT * SwordBeam.SCALE;

            float progress = (summon.getEntityData().get(StaticSummon.AGE) + partialTick) / (float) summon.getEntityData().get(StaticSummon.LIFETIME);

            float alpha;
            if(progress <= FADE_IN) {
                alpha = 1 - (FADE_IN - progress) / FADE_IN;
            }else if (progress >= FADE_OUT) {
                alpha = 1 - (progress - FADE_OUT) / (1 - FADE_OUT);
            }else {
                alpha = 1.0f;
            }

            if(alpha < 0.01f) return;

            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);

            Vector3f dir = new Vector3f();
            if (customData.contains("dirX")) dir.x = customData.getFloat("dirX");
            if (customData.contains("dirY")) dir.y = customData.getFloat("dirY");
            if (customData.contains("dirZ")) dir.z = customData.getFloat("dirZ");
            Vector3f up = new Vector3f();
            if (customData.contains("upX")) up.x = customData.getFloat("upX");
            if (customData.contains("upY")) up.y = customData.getFloat("upY");
            if (customData.contains("upZ")) up.z = customData.getFloat("upZ");
            Vector3f right = new Vector3f();
            if (customData.contains("rightX")) right.x = customData.getFloat("rightX");
            if (customData.contains("rightY")) right.y = customData.getFloat("rightY");
            if (customData.contains("rightZ")) right.z = customData.getFloat("rightZ");

            Vector3f[] dirs = new Vector3f[]{dir, up, right};
            double rotate = summon.getEntityData().get(StaticSummon.RZP);
            double rotateSpeed = summon.getEntityData().get(StaticSummon.RZPS);
            float rotateAngle = (float) ((summon.getEntityData().get(StaticSummon.AGE) + partialTick) * rotateSpeed);

            Quaternionf rotation = new Quaternionf().fromAxisAngleRad(
                up, (float) Math.toRadians(-rotateAngle)
            );
            poseStack.mulPose(rotation);

            VertexConsumer vertexConsumer0 = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RES0));

            //外层(亮)
            poseStack.pushPose();
            poseStack.mulPose(rotation);
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 90, SwordBeam.DIST);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], 0, rotate);
            //poseStack.scale(1.0f + progress, 1.0f + progress, 1.0f + progress);
            RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer0,
                    COLOR0.x(), COLOR0.y(), COLOR0.z(), alpha, halfWidth, halfHeight, 0f, 0f, 0f);
            poseStack.popPose();

            VertexConsumer vertexConsumer1 = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RES1));

            //外线
            poseStack.pushPose();
            poseStack.mulPose(rotation);
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 90, SwordBeam.DIST * 1.15f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], 0, rotate);
            //poseStack.scale(0.95f * (1 + progress), 0.95f * (1 + progress), 0.95f * (1 + progress));
            RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer1,
                    1.0f, 1.0f, 1.0f, alpha, halfWidth, halfHeight, 0f, 0f, 0.02f);
            poseStack.popPose();

            VertexConsumer vertexConsumer2 = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RES2));

            //闪烁
            poseStack.pushPose();
            poseStack.mulPose(rotation);
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], 135, SwordBeam.DIST * 2.25f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], 0, rotate);
            //poseStack.scale(1.0f + progress, 1.0f + progress, 1.0f + progress);
            RenderUtil.renderQuad(poseStack.last().pose(), vertexConsumer2,
                    COLOR0.x(), COLOR0.y(), COLOR0.z(), alpha, 32 * SwordBeam.SCALE, 32 * SwordBeam.SCALE, 0f, 0f, 0.04f);
            poseStack.popPose();

        }

        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);

            //碰撞箱计算
            Vec3 center = summon.position().add(summon.getLookAngle().normalize().scale(SwordBeam.DIST));
            if(customData.contains("extX") && customData.contains("extY") && customData.contains("extZ")) {
                summon.setBoundingBox(new AABB(
                    center.x - customData.getFloat("extX"), center.y - customData.getFloat("extY"), center.z - customData.getFloat("extZ"),
                    center.x + customData.getFloat("extX"), center.y + customData.getFloat("extY"), center.z + customData.getFloat("extZ")
                ));
            }

            //碰撞计算
            if(!summon.level().isClientSide() && customData.contains("hitCount") && summon.getOwner() instanceof Player player) {
                int count = customData.getInt("hitCount");
                if(count < Config.nightsEdgeProjectileMaxHitCount) {
                    List<Entity> targets = summon.level().getEntitiesOfClass(Entity.class, summon.getBoundingBox(), FilterUtil.createTargetFilter(summon, summon.getOwner()));
                    for(Entity target : targets) {
                        if(count >= Config.nightsEdgeProjectileMaxHitCount) break;
                        if(DamageUtil.attack(player, target, (float) Config.nightsEdgeProjectileDamage)) {
                            count++;
                            ParticleUtil.addParticles(
                                (ServerLevel) target.level(), ModParticles.NIGHTS_EDGE_HIT_PARTICLE.get(),
                                new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2.0, target.getZ()), new Vec3(0, 0, 0),
                                0, 1
                            );
                            target.invulnerableTime = 20;
                        }
                    }
                    if(!targets.isEmpty()) customData.putInt("hitCount", count);
                }
                if(count >= Config.nightsEdgeProjectileMaxHitCount) this.onDied(summon);
            }

            ClipContext context = new ClipContext(
                summon.position(),
                summon.position().add(summon.getDeltaMovement()),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                summon
            );
            BlockHitResult blockHit = summon.level().clip(context);
            if(blockHit.getType() != HitResult.Type.MISS) {

            }

            //粒子
            if(summon.level().isClientSide()) {
                ParticleUtil.addParticle(
                    summon.level(), PARTICLE,
                    summon.position(), 2.5,
                    new Vec3(0, 0, 0), 0.5
                );
            }
        }
    };

    public static void summon(Player player, int randomAngle) {
        StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), player.level());
        summon.setOwner(player);
        Vec3 pos = new Vec3(player.getX(), player.getEyeY() - 0.1, player.getZ());
        summon.setPos(pos);

        if(!Config.nightsEdgeProjectileAlignToBlade) randomAngle = (int) (Config.nightsEdgeProjectileRotateRange * (Math.random() * 2 - 1));

        Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
        dirs = MathUtil.rotateCoordinateSystem(dirs[0], dirs[2], randomAngle);
        float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);
        summon.setXRot(xyRot[0]);
        summon.xRotO = xyRot[0];
        summon.setYRot(xyRot[1]);
        summon.yRotO = xyRot[1];

        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.NIGHTS_EDGE_BEAM);
        summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
        summon.getEntityData().set(StaticSummon.LIFETIME, Config.nightsEdgeProjectileLifetime);
        summon.getEntityData().set(StaticSummon.RZP, randomAngle);
        summon.getEntityData().set(StaticSummon.RZPS, Config.nightsEdgeProjectileRotationSpeed);
        summon.getEntityData().set(StaticSummon.GLOW, true);
        summon.setNoGravity(true);
        summon.noPhysics = true;

        CompoundTag customData = new CompoundTag();

        Quaternionf rotation = new Quaternionf()
            .fromAxisAngleRad(dirs[0], (float) Math.toRadians(Math.abs(dirs[0].y) > 0.999 ? 0 : randomAngle))
            .rotateY((float) Math.toRadians(-xyRot[1]))
            .rotateX((float) Math.toRadians(xyRot[0]))
            .rotateX((float) Math.toRadians(-90.0))
            .rotateZ((float) Math.toRadians(-90.0));

        Vector3f axisX = new Vector3f(1, 0, 0).rotate(rotation);
        Vector3f axisY = new Vector3f(0, 1, 0).rotate(rotation);
        Vector3f axisZ = new Vector3f(0, 0, 1).rotate(rotation);

        float extX = SwordBeam.HALF_WIDTH * SwordBeam.SCALE * Math.abs(axisX.x()) + SwordBeam.HALF_HEIGHT * SwordBeam.SCALE * Math.abs(axisY.x()) + SwordBeam.HALF_THICKNESS * SwordBeam.SCALE * Math.abs(axisZ.x());
        float extY = SwordBeam.HALF_WIDTH * SwordBeam.SCALE * Math.abs(axisX.y()) + SwordBeam.HALF_HEIGHT * SwordBeam.SCALE * Math.abs(axisY.y()) + SwordBeam.HALF_THICKNESS * SwordBeam.SCALE * Math.abs(axisZ.y());
        float extZ = SwordBeam.HALF_WIDTH * SwordBeam.SCALE * Math.abs(axisX.z()) + SwordBeam.HALF_HEIGHT * SwordBeam.SCALE * Math.abs(axisY.z()) + SwordBeam.HALF_THICKNESS * SwordBeam.SCALE * Math.abs(axisZ.z());

        customData.putInt("hitCount", 0);
        customData.putFloat("extX", extX);
        customData.putFloat("extY", extY);
        customData.putFloat("extZ", extZ);
        customData.putFloat("dirX", dirs[0].x);
        customData.putFloat("dirY", dirs[0].y);
        customData.putFloat("dirZ", dirs[0].z);
        customData.putFloat("upX", dirs[1].x);
        customData.putFloat("upY", dirs[1].y);
        customData.putFloat("upZ", dirs[1].z);
        customData.putFloat("rightX", dirs[2].x);
        customData.putFloat("rightY", dirs[2].y);
        customData.putFloat("rightZ", dirs[2].z);
        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

        summon.setDeltaMovement(player.getLookAngle().normalize().scale(Config.nightsEdgeProjectileSpeed));

        player.level().addFreshEntity(summon);
    }

    public static final IItemWaveBehavior ITEM_WAVE_BEHAVIOR = new IItemWaveBehavior() {
        public void onLeftClickAir(PlayerInteractEvent.LeftClickEmpty event) {
            Player player = event.getEntity();
            ItemStack itemStack = player.getMainHandItem();
            if(itemStack.isEmpty()) return;
            Item item = itemStack.getItem();
            if(item instanceof NightsEdge && !player.getCooldowns().isOnCooldown(item)) {
                PacketDistributor.sendToServer(new SwordBeamPayload("nights_edge", BEAM_DATA));
                SoundUtil.playClientSound(player, ModSounds.WAVE.get());
            }
        }
        public void onAttackEntity(AttackEntityEvent event) {
            Player player = event.getEntity();
            ItemStack itemStack = player.getMainHandItem();
            if(itemStack.isEmpty()) return;
            Item item = itemStack.getItem();
            if(item instanceof NightsEdge && !player.getCooldowns().isOnCooldown(item)) {
                if(!player.level().isClientSide()) {
                    SwordBeamBehaviors.getBehavior("nights_edge").generate(player, BEAM_DATA);
                }else {
                    SoundUtil.playClientSound(player, ModSounds.WAVE.get());
                }
            }
            event.setCanceled(true);
        }
    };

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(!level.isClientSide()) {
            SwordBeamBehaviors.getBehavior("nights_edge").generate(player, BEAM_DATA);
        }else {
            SoundUtil.playClientSound(player, ModSounds.WAVE.get());
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

}
