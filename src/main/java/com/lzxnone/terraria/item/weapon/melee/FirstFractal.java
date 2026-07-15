package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.Config;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.TintedVertexConsumer;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.summon.*;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.particle.ZenithTrailParticleOptions;
import com.lzxnone.terraria.network.SkinFetch;
import com.lzxnone.terraria.utils.*;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
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
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

public class FirstFractal extends SwordItem {
    public FirstFractal() {
        super(Tiers.NETHERITE, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 20.0, AttributeModifier.Operation.ADD_VALUE),
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
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE0.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE1.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE2.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE3.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE4.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE5.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE6.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE7.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE8.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE9.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE10.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE11.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE12.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE13.get()),
                new ItemStack(ModItems.FIRST_FRACTAL_PROJECTILE14.get()),
            };
        }
        return weapons;
    }

    public static final Vector3f[] COLORS = new Vector3f[] {
        new Vector3f(1.0f, 0.0f, 0.0f),     // 红 (Red)
        new Vector3f(1.0f, 0.5f, 0.0f),     // 橙 (Orange)
        new Vector3f(1.0f, 1.0f, 0.0f),     // 黄 (Yellow)
        new Vector3f(0.0f, 1.0f, 0.0f),     // 绿 (Green)
        new Vector3f(0.0f, 1.0f, 1.0f),     // 青 (Cyan)
        new Vector3f(0.0f, 0.0f, 1.0f),     // 蓝 (Blue)
        new Vector3f(0.5f, 0.0f, 1.0f)      // 紫 (Violet)
    };

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        public static final ResourceLocation RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/beam_sparkle.png");
        public static final ResourceLocation RES2 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/first_fractal_star.png");

        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticSummon summon)) return;
            UUID uuid = summon.getEntityData().get(StaticSummon.OWNER).orElse(null);
            if(uuid == null) return;
            ItemStack item = summon.getEntityData().get(StaticSummon.ITEM);
            if(item == ItemStack.EMPTY) return;

            GameProfile profile = SkinFetch.getCachedProfile(uuid);
            PlayerSkin playerSkin = Minecraft.getInstance().getSkinManager().getInsecureSkin(profile);

            boolean isSlim = playerSkin.model() == PlayerSkin.Model.SLIM;
            PlayerModel<LivingEntity> activeModel = isSlim ? StaticSummonRenderer.getSlimModel() : StaticSummonRenderer.getWideModel();
            activeModel.rightArm.resetPose();
            activeModel.leftArm.resetPose();
            activeModel.rightLeg.resetPose();
            activeModel.leftLeg.resetPose();
            activeModel.rightSleeve.resetPose();
            activeModel.leftSleeve.resetPose();
            activeModel.rightPants.resetPose();
            activeModel.leftPants.resetPose();
            activeModel.young = false;

            //确定模型方向
            activeModel.rightArm.xRot = (float) Math.toRadians(-90);
            activeModel.leftArm.xRot = (float) Math.toRadians(-90);
            activeModel.rightArm.yRot = (float) Math.toRadians(-30);
            activeModel.leftArm.yRot = (float) Math.toRadians(30);
            activeModel.rightLeg.xRot = (float) Math.toRadians(-30);
            activeModel.leftLeg.xRot = (float) Math.toRadians(30);

            activeModel.rightSleeve.xRot = activeModel.rightArm.xRot;
            activeModel.rightSleeve.yRot = activeModel.rightArm.yRot;
            activeModel.leftSleeve.xRot = activeModel.leftArm.xRot;
            activeModel.leftSleeve.yRot = activeModel.leftArm.yRot;
            activeModel.rightPants.xRot = activeModel.rightLeg.xRot;
            activeModel.leftPants.xRot = activeModel.leftLeg.xRot;

            Vector3f[] dirs = MathUtil.computeCoordinateSystem(summon.getLookAngle().toVector3f(), 0);
            float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);

            Vector3f color = new Vector3f(
                summon.getEntityData().get(StaticSummon.COLOR_R),
                summon.getEntityData().get(StaticSummon.COLOR_G),
                summon.getEntityData().get(StaticSummon.COLOR_B)
            );

            float radio = (float) (summon.getEntityData().get(StaticSummon.AGE) + partialTick) / (float) summon.getEntityData().get(StaticSummon.LIFETIME);
            float alpha;
            if(radio < 0.25f) {
                alpha = radio / 0.25f;
            }else if(radio > 0.75f) {
                alpha = 1.0f - (radio - 0.75f) / 0.25f;
            }else {
                alpha = 1.0f;
            }

            if(alpha < 0.001f) return;

            //流星
            poseStack.pushPose();
            float starHalfWidth = 45 * 0.06f;
            float starHalfHeight = 17 * 0.06f;
            VertexConsumer starConsumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES2));

            poseStack.translate(
                dirs[0].x * 0 + dirs[1].x * 1.3,
                dirs[0].y * 0 + dirs[1].y * 1.3,
                dirs[0].z * 0 + dirs[1].z * 1.3
            );

            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
            poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));

            poseStack.mulPose(Axis.YP.rotationDegrees(270));

            poseStack.scale(0.75f + alpha * 0.25f, 0.75f + alpha * 0.25f , 0.75f + alpha * 0.25f);
            for(int i = 0; i < 3; i++) {
                starConsumer.addVertex(poseStack.last().pose(), -starHalfWidth, -starHalfHeight, 0f)
                        .setColor(color.x, color.y, color.z, alpha * 0.5f).setUv(0.0f, 1.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                starConsumer.addVertex(poseStack.last().pose(), starHalfWidth, -starHalfHeight, 0f)
                        .setColor(color.x, color.y, color.z, alpha * 0.5f).setUv(1.0f, 1.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                starConsumer.addVertex(poseStack.last().pose(), starHalfWidth, starHalfHeight, 0f)
                        .setColor(color.x, color.y, color.z, alpha * 0.5f).setUv(1.0f, 0.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                starConsumer.addVertex(poseStack.last().pose(), -starHalfWidth, starHalfHeight, 0f)
                        .setColor(color.x, color.y, color.z, alpha * 0.5f).setUv(0.0f, 0.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            }
            poseStack.popPose();

            //闪烁
            poseStack.pushPose();
            float sparkleHalfWidth = 32 * 0.12f;
            float sparkleHalfHeight = 32 * 0.03f;

            VertexConsumer sparkleConsumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES));

            poseStack.translate(
                dirs[0].x * 2 + dirs[1].x * 1.3,
                dirs[0].y * 2 + dirs[1].y * 1.3,
                dirs[0].z * 2 + dirs[1].z * 1.3
            );

            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));

            poseStack.mulPose(Axis.YP.rotationDegrees(270));

            poseStack.scale(alpha, alpha , alpha);

            for(int i = 0; i < 3; i++) {
                sparkleConsumer.addVertex(poseStack.last().pose(), -sparkleHalfWidth, -sparkleHalfHeight, 0.01f)
                        .setColor(color.x, color.y, color.z, alpha).setUv(0.0f, 1.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                sparkleConsumer.addVertex(poseStack.last().pose(), sparkleHalfWidth, -sparkleHalfHeight, 0.01f)
                        .setColor(color.x, color.y, color.z, alpha).setUv(1.0f, 1.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                sparkleConsumer.addVertex(poseStack.last().pose(), sparkleHalfWidth, sparkleHalfHeight, 0.01f)
                        .setColor(color.x, color.y, color.z, alpha).setUv(1.0f, 0.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                sparkleConsumer.addVertex(poseStack.last().pose(), -sparkleHalfWidth, sparkleHalfHeight, 0.01f)
                        .setColor(color.x, color.y, color.z, alpha).setUv(0.0f, 0.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            }
            poseStack.popPose();

            //剑
            poseStack.pushPose();

            poseStack.translate(
                dirs[0].x * 2.2 + dirs[1].x * 1.5,
                dirs[0].y * 2.2 + dirs[1].y * 1.5,
                dirs[0].z * 2.2 + dirs[1].z * 1.5
            );

            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
            poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));

            poseStack.mulPose(Axis.YP.rotationDegrees(270));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-45));

            poseStack.scale(4.0f, 4.0f , 4.0f);

            Minecraft.getInstance().getItemRenderer().renderStatic(
                item,
                ItemDisplayContext.NONE,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                renderType -> {
                    VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
                    return new TintedVertexConsumer(vertexConsumer, 1.0f, 1.0f, 1.0f, alpha);
                },
                summon.level(),
                0
            );

            poseStack.popPose();

            //模型
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
            poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));

            poseStack.mulPose(Axis.YP.rotationDegrees(180));

            poseStack.scale(-1.0F, -1.0F, 1.0F);
            poseStack.translate(0.0F, -1.501F, 0.0F);

            VertexConsumer modelConsumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucent(playerSkin.texture()));

            int packedColor = FastColor.ARGB32.color(Math.min((int)(alpha * 255), 255), 255, 255, 255);
            activeModel.renderToBuffer(poseStack, modelConsumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, packedColor);
            poseStack.popPose();

            //恢复模型
            activeModel.rightArm.resetPose();
            activeModel.leftArm.resetPose();
            activeModel.rightLeg.resetPose();
            activeModel.leftLeg.resetPose();
            activeModel.rightSleeve.resetPose();
            activeModel.leftSleeve.resetPose();
            activeModel.rightPants.resetPose();
            activeModel.leftPants.resetPose();
            activeModel.young = false;
        }

        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            summon.setDeltaMovement(summon.getLookAngle().normalize().scale(Config.firstFractalProjectileSpeed));

            summon.setBoundingBox(new AABB(
                summon.getX() - Config.firstFractalBoundingBoxSize, summon.getY() - Config.firstFractalBoundingBoxSize, summon.getZ() - Config.firstFractalBoundingBoxSize,
                summon.getX() + Config.firstFractalBoundingBoxSize, summon.getY() + Config.firstFractalBoundingBoxSize, summon.getZ() + Config.firstFractalBoundingBoxSize
            ));

            if(!summon.level().isClientSide()) {
                if(summon.getOwner() instanceof Player player) {
                    List<Entity> targets = summon.level().getEntitiesOfClass(
                            Entity.class,
                            summon.getBoundingBox(),
                            FilterUtil.createTargetFilter(summon, summon.getOwner())
                    );
                    for(Entity target : targets) {
                        if(DamageUtil.attack(player, target, (float) Config.firstFractalProjectileDamage)) {
                            target.invulnerableTime = 10;
                        }
                    }
                }
            }else {
                Vector3f color = new Vector3f(
                    summon.getEntityData().get(StaticSummon.COLOR_R),
                    summon.getEntityData().get(StaticSummon.COLOR_G),
                    summon.getEntityData().get(StaticSummon.COLOR_B)
                );
                Vector3f[] dirs = MathUtil.computeCoordinateSystem(summon.getLookAngle().toVector3f(), 0);
                ZenithTrailParticleOptions options = new ZenithTrailParticleOptions(0.05f, 40, true, color, dirs[1], dirs[2], 90);
                Vec3 pos = MathUtil.toVec3(dirs[1]).scale(Math.random() * 2).add(summon.position());
                Vec3 speed = MathUtil.toVec3(dirs[0]).scale(Config.firstFractalProjectileSpeed);
                ParticleUtil.addParticle(
                    summon.level(), options,
                    pos, 0,
                    speed, 0
                );
            }
        }
    };

    public static void summon(Player player, double deltaDist) {
        if(!player.level().isClientSide()) {
            Vec3 targetPos;
            if(Config.firstFractalDistanceMode) {
                Vec3 origin = MathUtil.getCrosshairPos(player, player.level(), Config.firstFractalMaxRange);
                double dist = origin.subtract(player.getEyePosition()).length();
                double maxScale = Config.firstFractalMaxRange - dist;
                double minScale = -dist;
                double scale = Math.min(deltaDist, maxScale);
                if(deltaDist < minScale) scale = -minScale;
                targetPos = origin.add(player.getLookAngle().normalize().scale(scale));
            }else {
                targetPos = player.getEyePosition().add(player.getLookAngle().normalize().scale(Math.min(deltaDist, Config.firstFractalMaxRange)));
            }

            Vec3 summonPos = MathUtil.getRandomPosInRadius(targetPos, Config.firstFractalSpawnRange);
            Vector3f color = COLORS[player.getRandom().nextInt(COLORS.length)];

            StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), player.level());
            summon.setOwner(player);
            summon.setPos(summonPos);
            summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.FIRST_FRACTAL_PROJECTILE);
            summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
            summon.getEntityData().set(StaticSummon.ITEM, getWeapons()[player.getRandom().nextInt(getWeapons().length)]);
            summon.getEntityData().set(StaticSummon.COLOR_R, color.x);
            summon.getEntityData().set(StaticSummon.COLOR_G, color.y);
            summon.getEntityData().set(StaticSummon.COLOR_B, color.z);
            summon.getEntityData().set(StaticSummon.GLOW, true);
            summon.getEntityData().set(StaticSummon.LIFETIME, Config.firstFractalProjectileLifetime);

            Vector3f[] dirs = MathUtil.computeCoordinateSystem(targetPos.subtract(summonPos).toVector3f(), 0);
            float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);
            summon.setXRot(xyRot[0]);
            summon.xRotO = xyRot[0];
            summon.setYRot(xyRot[1]);
            summon.yRotO = xyRot[1];

            summon.setNoGravity(true);
            summon.noPhysics = true;

            player.level().addFreshEntity(summon);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if(!level.isClientSide()) {
            CustomData.update(DataComponents.CUSTOM_DATA, stack,
                    tag -> tag.putInt("attackCount", 3));
        }else {
            SoundUtil.playClientSound(player, ModSounds.WAVE.get());
        }

        player.getCooldowns().addCooldown(stack.getItem(), 3);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if(entity instanceof Player player) {
            if(isSelected) {
                if(player.tickCount % 3 == 0) {
                    double deltaDist = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                        .copyTag().getDouble("deltaDist");
                    int count = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                        .copyTag().getInt("attackCount");
                    if(count > 0) {
                        summon(player, deltaDist);
                        CustomData.update(DataComponents.CUSTOM_DATA, stack,
                            tag -> tag.putInt("attackCount", count - 1));
                    }
                }
            }else {
                int count = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                    .copyTag().getInt("attackCount");
                if(count > 0) {
                    CustomData.update(DataComponents.CUSTOM_DATA, stack,
                        tag -> tag.putInt("attackCount", 0));
                }
            }
        }
    }
}
