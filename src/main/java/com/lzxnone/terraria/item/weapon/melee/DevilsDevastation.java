package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.Config;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.TintedVertexConsumer;
import com.lzxnone.terraria.entity.projectile.IStaticProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.particle.CircleParticleOptions;
import com.lzxnone.terraria.particle.DustParticleOptions;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.units.qual.C;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class DevilsDevastation extends SwordItem {
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

    public static final ResourceLocation RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/devils_devastation_lightning.png");
    public static final ResourceLocation RES2 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/circular_smear_smokey.png");
    public static final ResourceLocation RES3 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/circular_smear_fire3.png");

    public static final float PROJECTILE_MOVING_RADIUS = 0.25F;

    public static final float PROJECTILE_HIT_RADIUS = 1.0F;
    public static final float PROJECTILE_HIT_LENGTH = 32F;
    public static final int PROJECTILE_HIT_DEPTH = 1;
    public static final float PROJECTILE_HIT_JITTER = 0.45F;
    public static final int PROJECTILE_HIT_RING = 8;

    //右键生成的弹射
    public static final IStaticProjectileBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile projectile)) return;
            renderItem(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
            VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES));
            Vec3 entWorldPos = new Vec3(
                Mth.lerp(partialTick, projectile.xo, projectile.getX()),
                Mth.lerp(partialTick, projectile.yo, projectile.getY()),
                Mth.lerp(partialTick, projectile.zo, projectile.getZ())
            );
            Matrix4f matrix = poseStack.last().pose();
            renderTube(buffer, matrix, entWorldPos, projectile.trailPositions, PROJECTILE_MOVING_RADIUS * 1.0F, 1.0f, 1,1,1);
            renderTube(buffer, matrix, entWorldPos, projectile.trailPositions, PROJECTILE_MOVING_RADIUS * 1.25F, 1.0f * 0.5F, 0.7F,0.15F,0.55F);
            renderTube(buffer, matrix, entWorldPos, projectile.trailPositions, PROJECTILE_MOVING_RADIUS * 1.5F, 1.0f * 0.2F, 0.5F,0.1F,0.4F);
            renderTube(buffer, matrix, entWorldPos, projectile.trailPositions2, PROJECTILE_MOVING_RADIUS * 1.0F, 1.0f, 1,1,1);
            renderTube(buffer, matrix, entWorldPos, projectile.trailPositions2, PROJECTILE_MOVING_RADIUS * 1.25F, 1.0f * 0.5F, 0.7F,0.15F,0.55F);
            renderTube(buffer, matrix, entWorldPos, projectile.trailPositions2, PROJECTILE_MOVING_RADIUS * 1.5F, 1.0f * 0.2F, 0.5F,0.1F,0.4F);
        }

        @Override
        public void onMoving(StaticProjectile projectile) {
            Vec3 right = MathUtil.toVec3(projectile.getEntityData().get(StaticProjectile.RIGHT));
            projectile.trailPositions.addFirst(projectile.position().add(right.scale(PROJECTILE_MOVING_RADIUS / 2 * 1.5f)));
            projectile.trailPositions2.addFirst(projectile.position().add(right.scale(-PROJECTILE_MOVING_RADIUS / 2 * 1.5f)));
            while(projectile.trailPositions.size() > 10) projectile.trailPositions.removeLast();
            while(projectile.trailPositions2.size() > 10) projectile.trailPositions2.removeLast();
            if(projectile.level().isClientSide()) {
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
                if(DamageUtil.attack(player, target, 20.0f)) {
                    StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), projectile.level());
                    summon.setOwner(target);
                    Vec3 pos = projectile.position();
                    summon.setPos(pos);
                    summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.DEVILS_DEVASTATION_PROJECTILE);
                    summon.getEntityData().set(StaticSummon.RENDER_MODE, "item");
                    summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.DEVILS_DEVASTATION.get()));
                    summon.getEntityData().set(StaticSummon.SCALE_X, 4.0f);
                    summon.getEntityData().set(StaticSummon.SCALE_Y, 4.0f);
                    summon.getEntityData().set(StaticSummon.SCALE_Z, 4.0f);
                    summon.getEntityData().set(StaticSummon.RXP, -90);
                    summon.getEntityData().set(StaticSummon.RZP, -135);
                    summon.getEntityData().set(StaticSummon.LIFETIME, 200);
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

                    target.invulnerableTime = 5;
                    List<UUID> uuids = new ArrayList<>(target.getData(ModAttachments.STUCK_DEVILS_DEVASTATION_PROJECTILE));
                    if(uuids.size() >= 5) {
                        Entity stuckProjectile = ((ServerLevel) projectile.level()).getEntity(uuids.getFirst());
                        if(stuckProjectile != null && stuckProjectile.isAlive() && stuckProjectile instanceof StaticSummon stuckStaticProjectile) summonStuckProjectile(stuckStaticProjectile);
                        uuids.removeFirst();
                    }
                    uuids.add(summon.getUUID());
                    target.setData(ModAttachments.STUCK_DEVILS_DEVASTATION_PROJECTILE, uuids);

                    for(int i = 0;i < 20;i++) {
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
            //旋转
            RenderUtil.applyRotate(poseStack, dir, up, age * 30 + 60, rotate);

            //缩放
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
            //旋转
            RenderUtil.applyRotate(poseStack, dir, up, age * 30 + 180, rotate);

            //缩放
            poseStack.scale(scaleX, scaleY, scaleZ);

            VertexConsumer buffer0 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES2));
            RenderUtil.renderQuad(poseStack.last().pose(), buffer0, 0.729f, 0.396f, 0.345f, 1.0f, 78, 78, 0, 0, 0);
            poseStack.popPose();

            poseStack.pushPose();
            //旋转
            RenderUtil.applyRotate(poseStack, dir, up, age * 30, rotate);

            //缩放
            poseStack.scale(scaleX, scaleY, scaleZ);

            VertexConsumer buffer1 = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES3));
            RenderUtil.renderQuad(poseStack.last().pose(), buffer1, 0.8f, 0.176f, 0.78f, 1.0f, 78, 78, 0, 0, 0);
            poseStack.popPose();

        }

        @Override
        public void onMoving(StaticProjectile projectile) {
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

                float[] xyRot = MathUtil.computeXYRot(dir, up);
                Quaternionf rotation = new Quaternionf().fromAxisAngleRad(up, (float) Math.toRadians(age * 30)).rotateY((float) Math.toRadians(-xyRot[1])).rotateX((float) Math.toRadians(xyRot[0]));
                Vector3f pos = new Vector3f(0, 0, 4);
                pos.rotate(rotation);

                IronSparkParticleOptions ironSparkParticleOptions;
                if(projectile.getRandom().nextInt(2) == 0) {
                    ironSparkParticleOptions = new IronSparkParticleOptions(
                            1.0f, 40, 4.0f, new Vector3f(0.729f, 0.396f, 0.345f), dir.mul(0.2f), right
                    );
                }else {
                    ironSparkParticleOptions = new IronSparkParticleOptions(
                            1.0f, 40, 4.0f, new Vector3f(0.8f, 0.176f, 0.78f), dir.mul(0.2f), right
                    );
                }
                ParticleUtil.addParticle(
                    projectile.level(), ironSparkParticleOptions,
                    MathUtil.toVec3(pos), 0.5,
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
                if(DamageUtil.attack(player, target, 20.0f)) {
                    target.invulnerableTime = 5;
                    onDied(projectile);
                }
            }
        }

        @Override
        public void onHitBlock(StaticProjectile projectile, BlockHitResult result) {}
    };

    //弹出弹射然后生成新的旋转弹射
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
            projectile.getEntityData().set(StaticProjectile.LIFETIME, 40);
            projectile.getEntityData().set(StaticProjectile.GLOW, true);
            projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*t", 3.0));
            projectile.setDeltaMovement(MathUtil.toVec3(dirs[0]));

            serverLevel.addFreshEntity(projectile);
            stuckProjectile.discard();
        }
    }

    //依附在实体上的剑
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

    //弹射击中实体产生的闪电
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
            float radius = PROJECTILE_HIT_RADIUS * (1.0f - lifeRatio);
            float alpha = 1.0f - lifeRatio;

            Vec3 entWorldPos = new Vec3(
                Mth.lerp(partialTick, summon.xo, summon.getX()),
                Mth.lerp(partialTick, summon.yo, summon.getY()),
                Mth.lerp(partialTick, summon.zo, summon.getZ())
            );
            VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES));
            Matrix4f matrix = poseStack.last().pose();

            Random rand0 = new Random(seed);
            Vec3 start0 = entWorldPos.add(dir.scale(4.0));
            Vec3 end0 = start0.add(dir.scale(PROJECTILE_HIT_LENGTH));
            List<Vec3> branch0 = generateBranch(start0, end0, rand0);

            Random rand1 = new Random(seed + 1);
            Vec3 end1 = start0.add(spreadDir(dir, rand1, 0.4F).scale(PROJECTILE_HIT_LENGTH * 0.7F));
            List<Vec3> branch1 = generateBranch(start0, end1, rand1);

            Random rand2 = new Random(seed + 2);
            Vec3 start2 = branch0.get(1);
            Vec3 end2 = start2.add(spreadDir(dir, rand2, 0.4F).scale(PROJECTILE_HIT_LENGTH * 0.5F));
            List<Vec3> branch2 = generateBranch(start2, end2, rand2);


            for(List<Vec3> branch : new List[]{branch0, branch1, branch2}) {
                renderTube(buffer, matrix, entWorldPos, branch, radius * 1.0F, alpha, 1,1,1);
                renderTube(buffer, matrix, entWorldPos, branch, radius * 1.25F, alpha * 0.5F, 0.7F,0.15F,0.55F);
                renderTube(buffer, matrix, entWorldPos, branch, radius * 0.75F, alpha * 0.2F, 0.5F,0.1F,0.4F);
            }
        }

        @Override
        public AABB getBoundingBoxForCulling(StaticSummon summon) {
            double m = PROJECTILE_HIT_LENGTH + 8.0;
            Vec3 p = summon.position();
            return new AABB(p.x - m, p.y - m, p.z - m, p.x + m, p.y + m, p.z + m);
        }
    };

    public static List<Vec3> generateBranch(Vec3 start, Vec3 end, Random rand) {
        List<Vec3> branch = new ArrayList<>();
        branch.add(start);
        subdivide(start, end, PROJECTILE_HIT_DEPTH, rand, branch);
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
    private static void subdivide(Vec3 start, Vec3 end, int depth, Random rand, List<Vec3> out) {
        if(depth <= 0) return;
        Vec3 mid = start.add(end).scale(0.5);
        Vec3 seg = end.subtract(start);
        Vector3f[] dirs = MathUtil.computeCoordinateSystem(seg.toVector3f(), 0);
        mid = getSurfaceRandomPosInRadius(mid, MathUtil.toVec3(dirs[2]), MathUtil.toVec3(dirs[1]), seg.length() * PROJECTILE_HIT_JITTER, rand);
        subdivide(start, mid, depth - 1, rand, out);
        out.add(mid);
        subdivide(mid, end, depth - 1, rand, out);
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
                                    List<Vec3> points, float radius, float alpha, float colorR, float colorG, float colorB) {
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
            float r0 = radius * (1.0F - (float) i / (float) (n - 1));
            float r1 = radius * (1.0F - (float) (i + 1) / (float) (n - 1));
            if(r0 <= 0.001F && r1 <= 0.001F) continue;
            Vec3 N = right[i], B = up[i], N1 = right[i + 1], B1 = up[i + 1];
            Vec3 P = points.get(i), P1 = points.get(i + 1);
            for(int j = 0;j < PROJECTILE_HIT_RING;j++) {
                float a0 = (float) (2.0 * Math.PI * j / PROJECTILE_HIT_RING);
                float a1 = (float) (2.0 * Math.PI * (j + 1) / PROJECTILE_HIT_RING);
                Vec3 d0 = computeRingDir(N, B, a0);
                Vec3 d1 = computeRingDir(N1, B1, a0);
                Vec3 d0b = computeRingDir(N, B, a1);
                Vec3 d1b = computeRingDir(N1, B1, a1);
                Vec3 v0 = P.add(d0.scale(r0));
                Vec3 v1 = P1.add(d1.scale(r1));
                Vec3 v2 = P1.add(d1b.scale(r1));
                Vec3 v3 = P.add(d0b.scale(r0));
                for(int k = 0;k < 3;k++) {
                    writeVert(buffer, matrix, entityWorldPos, v0, alpha, colorR, colorG, colorB);
                    writeVert(buffer, matrix, entityWorldPos, v1, alpha, colorR, colorG, colorB);
                    writeVert(buffer, matrix, entityWorldPos, v2, alpha, colorR, colorG, colorB);
                    writeVert(buffer, matrix, entityWorldPos, v3, alpha, colorR, colorG, colorB);
                }
            }
        }
    }

    public static Vec3 computeRingDir(Vec3 n, Vec3 b, float ang) {
        return n.scale(Math.cos(ang)).add(b.scale(Math.sin(ang)));
    }

    public static void writeVert(VertexConsumer buffer, Matrix4f matrix, Vec3 entityWorldPos,
                                  Vec3 worldPos, float alpha, float colorR, float colorG, float colorB) {
        double lx = worldPos.x - entityWorldPos.x;
        double ly = worldPos.y - entityWorldPos.y;
        double lz = worldPos.z - entityWorldPos.z;

        buffer.addVertex(matrix, (float)lx, (float)ly, (float)lz)
            .setColor(colorR, colorG, colorB, alpha)
            .setUv(0.5F, 0.5F)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT)
            .setNormal(0.0F, 1.0F, 0.0F);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
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
            projectile.getEntityData().set(StaticProjectile.LIFETIME, 40);
            projectile.getEntityData().set(StaticProjectile.GLOW, true);
            projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*t", 3.0));
            projectile.setDeltaMovement(MathUtil.toVec3(dirs[0]));
            level.addFreshEntity(projectile);
        }
        SoundUtil.playClientSound(player, ModSounds.DEMON_SWORD_SWING.get());
        player.getCooldowns().addCooldown(stack.getItem(), 10);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}