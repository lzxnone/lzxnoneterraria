package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.Config;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.TintedVertexConsumer;
import com.lzxnone.terraria.entity.projectile.IStaticProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.particle.ZenithTrailParticleOptions;
import com.lzxnone.terraria.utils.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class Zenith extends SwordItem {
    public Zenith() {
        super(Tiers.NETHERITE, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 20, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -2.4, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ));
    }

    private static ItemStack[] weapons;

    private static ItemStack[] getWeapons() {
        if(weapons == null) {
            weapons = new ItemStack[]{
                new ItemStack(ModItems.COPPER_SHORTSWORD.get()),
                new ItemStack(ModItems.LIGHTS_BANE.get()),
                new ItemStack(ModItems.MURAMASA.get()),
                new ItemStack(ModItems.TERRAGRIM.get()),
                new ItemStack(ModItems.BLOOD_BUTCHERER.get()),
                new ItemStack(ModItems.STARFURY.get()),
                new ItemStack(ModItems.ENCHANTED_SWORD.get()),
                new ItemStack(ModItems.BEE_KEEPER.get()),
                new ItemStack(ModItems.BLADE_OF_GRASS.get()),
                new ItemStack(ModItems.VOLCANO.get()),
                new ItemStack(ModItems.NIGHTS_EDGE.get()),
                new ItemStack(ModItems.TRUE_NIGHTS_EDGE.get()),
                new ItemStack(ModItems.EXCALIBUR.get()),
                new ItemStack(ModItems.TRUE_EXCALIBUR.get()),
                new ItemStack(ModItems.THE_HORSEMANS_BLADE.get()),
                new ItemStack(ModItems.SEEDLER.get()),
                new ItemStack(ModItems.TERRA_BLADE.get()),
                new ItemStack(ModItems.INFLUX_WAVER.get()),
                new ItemStack(ModItems.STAR_WRATH.get()),
                new ItemStack(ModItems.MURAMASA.get()),
                new ItemStack(ModItems.ZENITH.get())
            };
        }
        return weapons;
    }

    public static final Vector3f[] COLORS = {
        new Vector3f(0.922f, 0.651f, 0.529f), // 1. 淺橙色 (#EBA687)
        new Vector3f(0.478f, 0.259f, 0.749f), // 2. 紫色 (#7A42BF)
        new Vector3f(0.220f, 0.306f, 0.824f), // 3. 海軍藍 (#384ED2)
        new Vector3f(0.698f, 1.000f, 0.706f), // 4. 亮薄荷綠 (#B2FFB4)
        new Vector3f(0.929f, 0.110f, 0.141f), // 5. 紅色 (#ED1C24)
        new Vector3f(0.925f, 0.243f, 0.753f), // 6. 粉色 (#EC3EC0)
        new Vector3f(0.357f, 0.620f, 0.910f), // 7. 淺藍色 (#5B9EE8)
        new Vector3f(1.000f, 0.906f, 0.271f), // 8. 黃色 (#FFE745)
        new Vector3f(0.420f, 0.796f, 0.000f), // 9. 綠色 (#6BCB00)
        new Vector3f(0.996f, 0.620f, 0.137f), // 10. 橙色 (#FE9E23)
        new Vector3f(0.702f, 0.212f, 0.788f), // 11. 紫色 (#B336C9)
        new Vector3f(0.702f, 0.212f, 0.788f), // 12. 紫色 (#B336C9)
        new Vector3f(0.925f, 0.784f, 0.075f), // 13. 黃色 (#ECC813)
        new Vector3f(0.925f, 0.784f, 0.075f), // 14. 黃色 (#ECC813)
        new Vector3f(0.988f, 0.373f, 0.016f), // 15. 橙色 (#FC5F04)
        new Vector3f(0.561f, 0.843f, 0.114f), // 16. 綠色 (#8FD71D)
        new Vector3f(0.314f, 0.871f, 0.478f), // 17. 淺綠色 (#50DE7A)
        new Vector3f(0.329f, 0.918f, 0.961f), // 18. 青色 (#54EAF5)
        new Vector3f(0.929f, 0.247f, 0.522f), // 19. 粉色 (#ED3F85)
        new Vector3f(0.996f, 0.761f, 0.980f), // 20. 淺粉色 (#FEC2FA)
        new Vector3f(0.698f, 1.000f, 0.706f)  // 20. 亮薄荷綠 (#B2FFB4)
    };

    public static final ResourceLocation RES0 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/sword_trail.png");
    public static final ResourceLocation RES1 = ResourceLocation.parse("lzxnoneterraria:textures/vfx/beam_sparkle.png");

    public static final IStaticProjectileBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileBehavior() {
        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile proj)) return;
            ItemStack stack = proj.getEntityData().get(StaticProjectile.ITEM);
            if(stack.isEmpty()) return;

            CompoundTag customData = proj.getEntityData().get(StaticProjectile.CUSTOM_DATA);
            if(!customData.contains("a") || !customData.contains("w") || !customData.contains("angle") || !customData.contains("start") || !customData.contains("idx")) return;
            int start = customData.getInt("start");
            if(proj.getEntityData().get(StaticProjectile.AGE) <= start) return;
            int idx = customData.getInt("idx");

            Vec3 dir = MathUtil.toVec3(proj.getEntityData().get(StaticProjectile.DIRECTION)).normalize();
            Vec3 up = MathUtil.toVec3(proj.getEntityData().get(StaticProjectile.UP)).normalize();
            Vec3 right = MathUtil.toVec3(proj.getEntityData().get(StaticProjectile.RIGHT)).normalize();

            Quaternionf rotationDir = new Quaternionf()
                .fromAxisAngleRad(dir.toVector3f(), (float) Math.toRadians(customData.getInt("angle")));
            Quaternionf rotationUp1 = new Quaternionf()
                .fromAxisAngleRad(up.toVector3f(), (float) -(customData.getDouble("w") * (proj.getEntityData().get(StaticProjectile.AGE) - start + partialTick)));
            Quaternionf rotationUp2 = new Quaternionf()
                .fromAxisAngleRad(up.toVector3f(), (float) Math.PI);

            float[] xyRot = MathUtil.computeXYRot(dir.toVector3f(), up.toVector3f());

            poseStack.pushPose();

            //旋转
            poseStack.mulPose(rotationUp1);
            poseStack.mulPose(rotationUp2);
            poseStack.mulPose(rotationDir);
            poseStack.mulPose(Axis.YP.rotationDegrees(-xyRot[1]));
            poseStack.mulPose(Axis.XP.rotationDegrees(xyRot[0]));
            poseStack.mulPose(Axis.XP.rotationDegrees(proj.getEntityData().get(StaticProjectile.RXP)));
            poseStack.mulPose(Axis.YP.rotationDegrees(proj.getEntityData().get(StaticProjectile.RYP)));
            poseStack.mulPose(Axis.ZP.rotationDegrees(proj.getEntityData().get(StaticProjectile.RZP)));

            //缩放
            poseStack.scale(
                proj.getEntityData().get(StaticProjectile.SCALE_X),
                proj.getEntityData().get(StaticProjectile.SCALE_Y),
                proj.getEntityData().get(StaticProjectile.SCALE_Z)
            );

            Minecraft.getInstance().getItemRenderer().renderStatic(
                    stack,
                    ItemDisplayContext.NONE,
                    proj.getEntityData().get(StaticProjectile.GLOW) ? LightTexture.FULL_BRIGHT : packedLight,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    renderType -> {
                        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
                        return new TintedVertexConsumer(vertexConsumer,
                            1.0f,
                            1.0f,
                            1.0f,
                            proj.getEntityData().get(StaticProjectile.COLOR_A));
                    },
                    entity.level(),
                    0
            );

            poseStack.popPose();

            Vec3 currentPos = proj.getPosition(partialTick);

            VertexConsumer vertexConsumer0 = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RES0));
            Matrix4f matrix = poseStack.last().pose();

            Vector3f color = new Vector3f(
                proj.getEntityData().get(StaticProjectile.COLOR_R),
                proj.getEntityData().get(StaticProjectile.COLOR_G),
                proj.getEntityData().get(StaticProjectile.COLOR_B)
            );
            float mainAlpha = (float) Config.zenithTrailAlpha;
            if(idx == 1) mainAlpha = mainAlpha * 0.5f;
            if(idx == 2) mainAlpha = mainAlpha * 0.25f;

            //尾迹
            int quadCount = proj.trailPositions.size() / 2 - 1;
            for(int j = 0;j < 2;j++) {
                for(int i = 1; i < quadCount; i++) {
                    Vec3 currentPoint1 = proj.trailPositions.get(i * 2);
                    Vec3 currentPoint2 = proj.trailPositions.get(i * 2 + 1);
                    Vec3 nextPoint1 = proj.trailPositions.get(i * 2 + 3);
                    Vec3 nextPoint2 = proj.trailPositions.get(i * 2 + 2);

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

                    float radio1 = (float) i / quadCount;
                    float radio2 = (float) (i + 1) / quadCount;

                    vertexConsumer0.addVertex(matrix, (float) x1, (float) y1, (float) z1)
                            .setColor(color.x, color.y, color.z, mainAlpha).setUv(radio1, 0.0f)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                    vertexConsumer0.addVertex(matrix, (float) x2, (float) y2, (float) z2)
                            .setColor(color.x, color.y, color.z, 0).setUv(radio1, 1.0f)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                    vertexConsumer0.addVertex(matrix, (float) x3, (float) y3, (float) z3)
                            .setColor(color.x, color.y, color.z, 0).setUv(radio2, 1.0f)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                    vertexConsumer0.addVertex(matrix, (float) x4, (float) y4, (float) z4)
                            .setColor(color.x, color.y, color.z, mainAlpha).setUv(radio2, 0.0f)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                }
            }

            //闪烁
            if(idx != 0) return;
            float halfWidth = 32 * 0.05f;
            float halfHeight = 32 * 0.05f;

            float radio = (float) (proj.getEntityData().get(StaticProjectile.AGE) - start) / (float) proj.getEntityData().get(StaticProjectile.LIFETIME);
            float alpha;
            if(radio > 0.25f && radio < 0.4f) {
                alpha = (radio - 0.25f) / 0.15f;
            }else if(radio >= 0.4f && radio < 0.6f) {
                alpha = 1.0f;
            }else if(radio >= 0.6f && radio < 0.75f) {
                alpha = 1.0f - (radio - 0.6f) / 0.15f;
            }else {
                alpha = 0;
            }
            if(Math.abs(alpha) < 0.001) return;

            VertexConsumer vertexConsumer1 = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RES1));
            poseStack.pushPose();

            Vector3f tempRight = right.toVector3f();
            double rad = customData.getDouble("w") * (proj.getEntityData().get(StaticProjectile.AGE) - start + partialTick);
            Vector3f swordTipDir = tempRight.rotate(new Quaternionf().fromAxisAngleRad(up.toVector3f(), (float) (Math.PI / 2 - rad))).normalize();
            Vec3 trans = MathUtil.toVec3(swordTipDir).scale(1.25);

            poseStack.translate(trans.x, trans.y, trans.z);
            poseStack.mulPose(new Quaternionf()
                .fromAxisAngleRad(dir.toVector3f(), (float) Math.toRadians(customData.getInt("angle")))
                .rotateY((float) Math.toRadians(-xyRot[1]))
                .rotateX((float) Math.toRadians(-90 + xyRot[0]))
            );

            poseStack.scale(1.0f, 1.0f, 1.0f);
            matrix = poseStack.last().pose();
            for(int i = 0; i < 3; i++) {
                vertexConsumer1.addVertex(matrix, -halfWidth, -halfHeight, 0.01f)
                        .setColor(color.x, color.y, color.z, alpha).setUv(0.0f, 1.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                vertexConsumer1.addVertex(matrix, halfWidth, -halfHeight, 0.01f)
                        .setColor(color.x, color.y, color.z, alpha).setUv(1.0f, 1.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                vertexConsumer1.addVertex(matrix, halfWidth, halfHeight, 0.01f)
                        .setColor(color.x, color.y, color.z, alpha).setUv(1.0f, 0.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
                vertexConsumer1.addVertex(matrix, -halfWidth, halfHeight, 0.01f)
                        .setColor(color.x, color.y, color.z, alpha).setUv(0.0f, 0.0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0f, 1.0f, 0.0f);
            }
            poseStack.popPose();
        }

        @Override
        public void onMoving(StaticProjectile projectile) {
            if(projectile.getOwner() == null) return;

            CompoundTag customData = projectile.getEntityData().get(StaticProjectile.CUSTOM_DATA);
            if(!customData.contains("w") || !customData.contains("angle") || !customData.contains("start")) return;

            int start = customData.getInt("start");
            if(projectile.getEntityData().get(StaticProjectile.AGE) < start) {
                Vec3 currentPos = (projectile.getOwner().getBoundingBox().getCenter()).add(MathUtil.toVec3(projectile.getEntityData().get(StaticProjectile.DIRECTION).normalize()).scale(-2));
                projectile.getEntityData().set(StaticProjectile.ORIGIN, currentPos.toVector3f());
                return;
            }

            float lifeRadio = (float) (projectile.getEntityData().get(StaticProjectile.AGE) - start) / (float) projectile.getEntityData().get(StaticProjectile.LIFETIME);

            Vector3f originalRight = projectile.getEntityData().get(StaticProjectile.RIGHT).normalize();
            Vector3f tempRight = new Vector3f(originalRight);
            Vector3f originalDir = projectile.getEntityData().get(StaticProjectile.DIRECTION).normalize();
            Vector3f tempDir = new Vector3f(originalDir);
            Vector3f up = projectile.getEntityData().get(StaticProjectile.UP).normalize();
            double rad = customData.getDouble("w") * (projectile.getEntityData().get(StaticProjectile.AGE) - start);
            Quaternionf rotation = new Quaternionf()
                .fromAxisAngleRad(up, (float) (Math.PI / 2 - rad));

            Vec3 right = MathUtil.toVec3(tempRight.rotate(rotation)); //剑尖方向
            Vec3 dir = MathUtil.toVec3(tempDir.rotate(rotation)); //剑飞行方向
            if(customData.getDouble("w") < 0) dir = dir.scale(-1);

            projectile.trailPositions.addFirst(projectile.position().add(right.scale(-1.2)));
            projectile.trailPositions.addFirst(projectile.position().add(right.scale(1.5)));
            while(projectile.trailPositions.size() > Config.zenithTrailMaxLength) projectile.trailPositions.removeLast();

            projectile.setBoundingBox(new AABB(
                projectile.getX() - Config.zenithBoundingBoxSize, projectile.getY() - Config.zenithBoundingBoxSize, projectile.getZ() - Config.zenithBoundingBoxSize,
                projectile.getX() + Config.zenithBoundingBoxSize, projectile.getY() + Config.zenithBoundingBoxSize, projectile.getZ() + Config.zenithBoundingBoxSize
            ));

            if(!projectile.level().isClientSide()) {
                List<LivingEntity> targets = projectile.level().getEntitiesOfClass(
                    LivingEntity.class,
                    projectile.getBoundingBox(),
                    FilterUtil.createLivingTargetFilter(projectile, projectile.getOwner())
                );
                for(LivingEntity target : targets) this.onHitEntity(projectile, new EntityHitResult(target, target.position()));
            }else {
                if(lifeRadio > 0.1f && lifeRadio < 0.9f && projectile.getRandom().nextInt(5) == 0) {
                    Vector3f color = new Vector3f(
                        projectile.getEntityData().get(StaticProjectile.COLOR_R),
                        projectile.getEntityData().get(StaticProjectile.COLOR_G),
                        projectile.getEntityData().get(StaticProjectile.COLOR_B)
                    );
                    ZenithTrailParticleOptions options = new ZenithTrailParticleOptions(0.05f, 40, true, color, up, right.toVector3f(), customData.getInt("angle"));
                    Vec3 pos = right.scale(Math.random()).add(projectile.position());
                    Vec3 speed = dir.scale(Math.max(0.05, Math.random() * 0.2));
                    ParticleUtil.addParticle(
                        projectile.level(), options,
                        pos, 0,
                        speed, 0
                    );
                }
            }

            Vec3 currentPos = (projectile.getOwner().getBoundingBox().getCenter()).add(MathUtil.toVec3(originalDir).scale(-2));
            projectile.getEntityData().set(StaticProjectile.ORIGIN, currentPos.toVector3f());
        }

        @Override
        public void onHitEntity(StaticProjectile projectile, EntityHitResult result) {
            if(!projectile.level().isClientSide()) {
                Entity target = result.getEntity();
                if(projectile.getOwner() instanceof Player player && FilterUtil.createTargetFilter(projectile, projectile.getOwner()).test(target)) {
                    if(DamageUtil.attack(player, target, (float) (Config.zenithDamage + Math.random() * Config.zenithDamage))) {
                        target.invulnerableTime = 2;
                    }
                }
            }
        }
    };

    public static void summon(Player player, double deltaDist, boolean isFirst) {
        Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
        int randomAngle = (int) ((Math.random() * 2 - 1) * Config.zenithTrailOffset);
        dirs = MathUtil.rotateCoordinateSystem(dirs[0], dirs[2], randomAngle);

        Vec3 pos = (player.getBoundingBox().getCenter()).add(MathUtil.toVec3(dirs[0]).scale(-2));
        double dist;
        if(Config.zenithDistanceMode) {
            Vec3 targetPos = MathUtil.getCrosshairPos(player, player.level(), Config.zenithMaxRange);
            dist = Math.max(0, Math.min(targetPos.subtract(pos).length() + deltaDist, Config.zenithMaxRange));
        }else {
            dist = deltaDist;
        }
        int cycle = (int) Math.max(10, dist / Config.zenithMaxRange * Config.zenithCycle);
        double a = Math.max(dist / 2, 2);
        double b = Math.min(Math.random() * Config.zenithTrailB + Config.zenithTrailB, a / 2);
        double w = Math.PI * 2 / (double) cycle;
        if(player.getRandom().nextInt(2) == 0) w = -w;

        int randomIndex = isFirst ? getWeapons().length - 1 : player.getRandom().nextInt(getWeapons().length);

        for(int i = 0;i < Config.zenithWeaponCount;i++) {
            StaticProjectile projectile = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), player.level());
            projectile.setOwner(player);

            projectile.setPos(pos);
            projectile.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.ZENITH_PROJECTILE);
            projectile.getEntityData().set(StaticProjectile.RENDER_MODE, "custom");
            projectile.getEntityData().set(StaticProjectile.ITEM, getWeapons()[randomIndex]);
            projectile.getEntityData().set(StaticProjectile.ORIGIN, pos.toVector3f());
            projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
            projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
            projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);
            projectile.getEntityData().set(StaticProjectile.COLOR_R, COLORS[randomIndex].x);
            projectile.getEntityData().set(StaticProjectile.COLOR_G, COLORS[randomIndex].y);
            projectile.getEntityData().set(StaticProjectile.COLOR_B, COLORS[randomIndex].z);
            projectile.getEntityData().set(StaticProjectile.RXP, -90);
            projectile.getEntityData().set(StaticProjectile.RZP, -135);
            projectile.getEntityData().set(StaticProjectile.GLOW, true);

            CompoundTag customData = new CompoundTag();
            customData.putDouble("a", a);
            customData.putDouble("b", b);
            customData.putDouble("w", w);
            customData.putInt("angle", randomAngle);
            if(i == 0) {
                projectile.getEntityData().set(StaticProjectile.COLOR_A, 1.0f);
                projectile.getEntityData().set(StaticProjectile.SCALE_X, (float) Config.zenithScale);
                projectile.getEntityData().set(StaticProjectile.SCALE_Y, (float) Config.zenithScale);
                projectile.getEntityData().set(StaticProjectile.LIFETIME, cycle);
                projectile.getEntityData().set(StaticProjectile.EXPRESSION_X, String.format("%.3f*cos(%.3ft-1.571)", b, w));
                projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*sin(%.3ft-1.571)+%.3f", a, w, a));
                customData.putInt("start", 0);
            }else if(i == 1) {
                projectile.getEntityData().set(StaticProjectile.COLOR_A, 0.75f);
                projectile.getEntityData().set(StaticProjectile.SCALE_X, (float) Config.zenithScale * 0.75f);
                projectile.getEntityData().set(StaticProjectile.SCALE_Y, (float) Config.zenithScale * 0.75f);
                projectile.getEntityData().set(StaticProjectile.LIFETIME, (int) (cycle * 1.25));
                if(w > 0) {
                    projectile.getEntityData().set(StaticProjectile.EXPRESSION_X, String.format("%.3f*cos(%.3ft-3.142)", b, w));
                    projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*sin(%.3ft-3.142)+%.3f", a, w, a));
                }else {
                    projectile.getEntityData().set(StaticProjectile.EXPRESSION_X, String.format("%.3f*cos(%.3ft)", b, w));
                    projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*sin(%.3ft)+%.3f", a, w, a));
                }
                customData.putInt("start", (int) (cycle * 0.25));
            }else if(i == 2) {
                projectile.getEntityData().set(StaticProjectile.COLOR_A, 0.5f);
                projectile.getEntityData().set(StaticProjectile.SCALE_X, (float) Config.zenithScale * 0.5f);
                projectile.getEntityData().set(StaticProjectile.SCALE_Y, (float) Config.zenithScale * 0.5f);
                projectile.getEntityData().set(StaticProjectile.LIFETIME, (int) (cycle * 1.5));
                if(w > 0) {
                    projectile.getEntityData().set(StaticProjectile.EXPRESSION_X, String.format("%.3f*cos(%.3ft-4.713)", b, w));
                    projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*sin(%.3ft-4.713)+%.3f", a, w, a));
                }else {
                    projectile.getEntityData().set(StaticProjectile.EXPRESSION_X, String.format("%.3f*cos(%.3ft+1.571)", b, w));
                    projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*sin(%.3ft+1.571)+%.3f", a, w, a));
                }
                customData.putInt("start", (int) (cycle * 0.5));
            }
            customData.putInt("idx", i);
            projectile.getEntityData().set(StaticProjectile.CUSTOM_DATA, customData);

            float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);
            projectile.setXRot(xyRot[0]);
            projectile.setYRot(xyRot[1]);
            player.level().addFreshEntity(projectile);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if(!level.isClientSide()) {
            int count = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag().getInt("attackCount");
            if(count == 0) {
                CustomData.update(DataComponents.CUSTOM_DATA, stack,
                    tag -> tag.putBoolean("isFirst", true));
            }
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
                    boolean isFirst = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                        .copyTag().getBoolean("isFirst");
                    int count = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                        .copyTag().getInt("attackCount");
                    if(count > 0) {
                        summon(player, deltaDist, isFirst);
                        CustomData.update(DataComponents.CUSTOM_DATA, stack,
                            tag -> tag.putInt("attackCount", count - 1));
                        CustomData.update(DataComponents.CUSTOM_DATA, stack,
                            tag -> tag.putBoolean("isFirst", false));
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
