package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.Config;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.beam.ISwordBeamBehavior;
import com.lzxnone.terraria.entity.beam.SwordBeam;
import com.lzxnone.terraria.entity.beam.SwordBeamBehaviors;
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
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3f;

public class TrueExcalibur extends SwordItem {
    public TrueExcalibur() {
        super(Tiers.DIAMOND, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 7.0f, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -1.0, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ).rarity(Rarity.RARE));
    }

    public static final CompoundTag BEAM_DATA = Util.make(new CompoundTag(), tag -> {
        tag.putString("behavior", "true_excalibur");
        tag.putInt("lifetime", 5);
        tag.putInt("cooldown", 5);
        tag.putFloat("inflate", 2.0f);
        tag.putFloat("color0R", 0.745f);
        tag.putFloat("color0G", 0.620f);
        tag.putFloat("color0B", 0.243f);
        tag.putFloat("color1R", 0.949f);
        tag.putFloat("color1G", 0.863f);
        tag.putFloat("color1B", 0.431f);
        tag.putFloat("color2R", 0.898f);
        tag.putFloat("color2G", 0.725f);
        tag.putFloat("color2B", 0.484f);

        CompoundTag customData = new CompoundTag();
        customData.putInt("hitEntityCount", 0);
        tag.put("customData", customData);
    });

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.075f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(1.0F, 1.0F, 1.0F),
            new Vector3f(1.0F, 0.9F, 0.0F),
            new Vector3f(1.0F, 0.6F, 0.8F)
        }
    );

    public static final ISwordBeamBehavior SWORD_BEAM_BEHAVIOR = new ISwordBeamBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick,
                            PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof SwordBeam beam)) return;
            Entity owner = beam.getOwner();
            if(owner == null) return;

            int age = beam.getEntityData().get(SwordBeam.AGE);
            int lifetime = Math.max(1, beam.getEntityData().get(SwordBeam.LIFETIME));

            float progress = (age + partialTick) / (float) lifetime;
            if(progress > 1.0f) return;

            Vector3f[] dirs = MathUtil.computeCoordinateSystem(beam.getOwner());
            dirs = MathUtil.rotateCoordinateSystem(dirs[0], dirs[2], beam.getEntityData().get(SwordBeam.ROTATE));
            float rotate = beam.getEntityData().get(SwordBeam.ROTATE);

            Vector3f color0 = new Vector3f(0.651f, 0.102f, 0.224f);
            Vector3f color1 = new Vector3f(0.945f, 0.286f, 0.525f);
            Vector3f color2 = new Vector3f(0.945f, 0.286f, 0.525f);
            Vector3f color3 = new Vector3f(1.0f, 1.0f, 1.0f);
            float alpha;

            if(progress <= FADE_IN) {
                alpha = 1 - (FADE_IN - progress) / FADE_IN;
            }else if(progress >= FADE_OUT) {
                alpha = 1 - (progress - FADE_OUT) / (1 - FADE_OUT);
            }else {
                alpha = 1.0f;
            }
            if(beam.getEntityData().get(SwordBeam.RIGHT)) progress = 1.0f - progress;

            float halfWidth = SwordBeam.HALF_WIDTH * SwordBeam.SCALE;
            float halfHeight = SwordBeam.HALF_HEIGHT * SwordBeam.SCALE;

            VertexConsumer vertexConsumer0 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES0));

            //左边
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress - 0.1f * (1.0f - progress)) * 180, SwordBeam.DIST * 1.5f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - (progress - 0.1f * (1.0f - progress))) * 180, rotate);
            poseStack.scale(1.5f, 1.5f, 1.5f);
            renderQuad(poseStack.last().pose(), vertexConsumer0,
                    color0.x(), color0.y(), color0.z(), alpha, halfWidth, halfHeight, 0f, 0, -0.05f);
            poseStack.popPose();

            //右边
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress + 0.1f) * 180, SwordBeam.DIST * 1.5f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - (progress + 0.05f)) * 180, rotate);
            poseStack.scale(1.5f, 1.5f, 1.5f);
            renderQuad(poseStack.last().pose(), vertexConsumer0,
                    color2.x(), color2.y(), color2.z(), alpha, halfWidth, halfHeight, 0f, 0, -0.06f);
            poseStack.popPose();

            //中间
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], progress * 180, SwordBeam.DIST * 1.5f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - progress) * 180, rotate);
            poseStack.scale(1.5f, 1.5f, 1.5f);
            renderQuad(poseStack.last().pose(), vertexConsumer0,
                    color1.x(), color1.y(), color1.z(), alpha, halfWidth, halfHeight, 0f, 0f, -0.04f);
            poseStack.popPose();

            VertexConsumer vertexConsumer1 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES3));

            //三线
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress + 0.05f) * 180, SwordBeam.DIST);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - progress) * 180, rotate);
            poseStack.scale(0.75f, 0.75f, 0.75f);
            renderQuad(poseStack.last().pose(), vertexConsumer1,
                    color3.x(), color3.y(), color3.z(), alpha, halfWidth, halfHeight, 0f, 0f, -0.045f);
            poseStack.popPose();

            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress + 0.05f) * 180, SwordBeam.DIST * 1.2f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - progress) * 180, rotate);
            poseStack.scale(1.125f, 1.125f, 1.125f);
            renderQuad(poseStack.last().pose(), vertexConsumer1,
                    color3.x(), color3.y(), color3.z(), alpha, halfWidth, halfHeight, 0f, 0f, -0.045f);
            poseStack.popPose();

            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], progress * 180, SwordBeam.DIST * 1.725f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - progress) * 180, rotate);
            poseStack.scale(1.425f, 1.425f, 1.425f);
            renderQuad(poseStack.last().pose(), vertexConsumer1,
                    color3.x(), color3.y(), color3.z(), alpha, halfWidth, halfHeight, 0f, 0f, -0.045f);
            poseStack.popPose();

            VertexConsumer vertexConsumer2 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES4));

            //边缘高光
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress + 0.05f) * 180, SwordBeam.DIST * 1.725f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - (progress + 0.05f)) * 180, rotate);
            poseStack.scale(1.5f, 1.5f, 1.5f);
            for(int i = 0;i < 20;i++) {
                renderQuad(poseStack.last().pose(), vertexConsumer2,
                        color1.x(), color1.y(), color1.z(), alpha, halfWidth, halfHeight, 0f, 0f, -0.03f);
            }
            poseStack.popPose();

            VertexConsumer vertexConsumer3 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES5));

            //闪烁
            poseStack.pushPose();
            RenderUtil.applyTranslate(poseStack, dirs[0], dirs[2], (progress + 0.25f) * 180, SwordBeam.DIST * 3.375f);
            RenderUtil.applyRotate(poseStack, dirs[0], dirs[1], (0.5f - (progress + 0.25f)) * 180, rotate);
            poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
            poseStack.scale(1.5f, 1.5f, 1.5f);
            for(int i = 0;i < 20;i++) {
                renderQuad(poseStack.last().pose(), vertexConsumer3,
                        color1.x(), color1.y(), color1.z(), alpha, 32 * SwordBeam.SCALE, 32 * SwordBeam.SCALE, 0f, 0f, -0.03f);
            }
            poseStack.popPose();

            ISwordBeamBehavior.super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
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
                        if(count < Config.trueExcaliburMaxHitCount) {
                            if(DamageUtil.attack(player, target, (float) Config.trueExcaliburDamage)) {
                                target.invulnerableTime = 20;
                                count++;
                                custom_data.putInt("hitEntityCount", count);
                                beam.getEntityData().set(SwordBeam.CUSTOM_DATA, custom_data);
                                ParticleUtil.addParticles(
                                    (ServerLevel) target.level(), ModParticles.EXCALIBUR_HIT_PARTICLE.get(),
                                    new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2.0, target.getZ()), new Vec3(0, 0, 0),
                                    0, 1
                                );
                                ParticleUtil.addParticles(
                                    (ServerLevel) target.level(), ModParticles.TRUE_EXCALIBUR_HIT_PARTICLE.get(),
                                    new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2.0, target.getZ()), new Vec3(0, 0, 0),
                                    0, 1
                                );
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void generate(Entity entity, CompoundTag beamData) {
            beamData.putInt("rotate", (int) (Config.trueExcaliburRotateRange * (Math.random() * 2 - 1)));
            ISwordBeamBehavior.super.generate(entity, beamData);
        }
    };

    public static final IItemWaveBehavior ITEM_WAVE_BEHAVIOR = new IItemWaveBehavior() {
        public void onLeftClickAir(PlayerInteractEvent.LeftClickEmpty event) {
            Player player = event.getEntity();
            ItemStack itemStack = player.getMainHandItem();
            if(itemStack.isEmpty()) return;
            Item item = itemStack.getItem();
            if(item instanceof TrueExcalibur && !player.getCooldowns().isOnCooldown(item)) {
                PacketDistributor.sendToServer(new SwordBeamPayload("true_excalibur", BEAM_DATA));
                SoundUtil.playClientSound(player, ModSounds.WAVE.get());
            }
        }
        public void onAttackEntity(AttackEntityEvent event) {
            Player player = event.getEntity();
            ItemStack itemStack = player.getMainHandItem();
            if(itemStack.isEmpty()) return;
            Item item = itemStack.getItem();
            if(item instanceof TrueExcalibur && !player.getCooldowns().isOnCooldown(item)) {
                if(!player.level().isClientSide()) {
                    SwordBeamBehaviors.getBehavior("true_excalibur").generate(player, BEAM_DATA);
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
            SwordBeamBehaviors.getBehavior("true_excalibur").generate(player, BEAM_DATA);
        }else {
            SoundUtil.playClientSound(player, ModSounds.WAVE.get());
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
