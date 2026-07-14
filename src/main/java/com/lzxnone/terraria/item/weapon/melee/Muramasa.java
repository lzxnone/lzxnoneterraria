package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.Config;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.entity.projectile.IStaticProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.IItemWaveBehavior;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.ParticleUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Muramasa extends SwordItem {
    public Muramasa() {
        super(Tiers.IRON, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 4, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -1.5, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ));
    }

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.05f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(0.1F, 0.4F, 1.0F),
            new Vector3f(0.0F, 1.0F, 1.0F)
        }
    );

    public static final IItemWaveBehavior ITEM_WAVE_BEHAVIOR = new IItemWaveBehavior() {
        @Override
        public void onLeftClickAir(PlayerInteractEvent.LeftClickEmpty event) {
            Player player = event.getEntity();
            if(player.level().isClientSide()) {
                ItemStack itemStack = player.getMainHandItem();
                if(itemStack.isEmpty()) return;
                Item item = itemStack.getItem();
                if(item instanceof Muramasa) {
                    ParticleUtil.addParticles(
                        player.level(), PARTICLE,
                        player.getBoundingBox().getCenter(), 0.2,
                        new Vec3(0, 0, 0), 0.2,
                        10
                    );
                }
            }
        }

        @Override
        public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
            Player player = event.getEntity();
            if(player.level().isClientSide()) {
                ItemStack itemStack = player.getMainHandItem();
                if(itemStack.isEmpty()) return;
                Item item = itemStack.getItem();
                if(item instanceof Muramasa) {
                    ParticleUtil.addParticles(
                        player.level(), PARTICLE,
                        player.getBoundingBox().getCenter(), 0.2,
                        new Vec3(0, 0, 0), 0.2,
                        10
                    );
                }
            }
        }

        @Override
        public void onAttackEntity(AttackEntityEvent event) {
            Player player = event.getEntity();
            ItemStack itemStack = player.getMainHandItem();
            if(itemStack.isEmpty()) return;
            Item item = itemStack.getItem();
            if(item instanceof Muramasa) {
                if(!player.level().isClientSide()) {
                    if(!player.getCooldowns().isOnCooldown(item)) {
                        Entity target = event.getTarget();
                        if(target instanceof LivingEntity livingTarget && FilterUtil.createLivingTargetFilter(player).test(livingTarget)) {
                            livingTarget.invulnerableTime = 5;

                            StaticProjectile projectile = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), player.level());
                            projectile.setOwner(player);

                            Vec3 start = MathUtil.getRandomPosInRadius(player.position(), 5);
                            Vec3 end = livingTarget.getBoundingBox().getCenter();
                            Vector3f[] dirs = MathUtil.computeCoordinateSystem(end.subtract(start).toVector3f(), 0);
                            dirs = MathUtil.rotateCoordinateSystem(dirs[0], dirs[2], (int) (360 * Math.random()));

                            projectile.setPos(start);
                            projectile.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.MURAMASA_PROJECTILE);
                            projectile.getEntityData().set(StaticProjectile.RENDER_MODE, "custom");
                            projectile.getEntityData().set(StaticProjectile.ORIGIN, start.toVector3f());
                            projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
                            projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
                            projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);
                            projectile.getEntityData().set(StaticProjectile.LIFETIME, 10);

                            double dist = start.distanceTo(end);
                            double speed = Math.max(0.1, dist / 10);
                            double height = 1.0 + Math.random() * 4.0;

                            projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*t", speed));
                            projectile.getEntityData().set(StaticProjectile.EXPRESSION_Y, String.format("%.3f*(t/10)*(1-t/10)", 4.0 * height));
                            projectile.getEntityData().set(StaticProjectile.GLOW, true);

                            player.level().addFreshEntity(projectile);
                        }
                        player.getCooldowns().addCooldown(item, 7);
                    }
                }else {
                    ParticleUtil.addParticles(
                        player.level(), PARTICLE,
                        player.getBoundingBox().getCenter(), 0.2,
                        new Vec3(0, 0, 0), 0.2,
                        10
                    );
                }
            }
        }
    };

    public static final IStaticProjectileBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileBehavior() {
        public static final ResourceLocation RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/muramasa_trail.png");
        public static final int MAX_LENGTH = 100;

        @Override
        public void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            if(!(entity instanceof StaticProjectile projectile)) return;
            Vec3 currentPos = projectile.getPosition(partialTick);

            VertexConsumer vertexConsumer = bufferSource.getBuffer(ModRenderTypes.entityTranslucentEmissive(RES));
            Matrix4f matrix = poseStack.last().pose();

            int quadCount = projectile.trailPositions.size() / 2 - 1;
            for(int i = 0; i < quadCount;i++) {
                Vec3 currentPoint1 = projectile.trailPositions.get(i * 2);
                Vec3 currentPoint2 = projectile.trailPositions.get(i * 2 + 1);
                Vec3 nextPoint1 = projectile.trailPositions.get(i * 2 + 3);
                Vec3 nextPoint2 = projectile.trailPositions.get(i * 2 + 2);

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

                vertexConsumer.addVertex(matrix, (float)x1, (float)y1, (float)z1)
                    .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(radio1, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                vertexConsumer.addVertex(matrix, (float)x2, (float)y2, (float)z2)
                    .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(radio1, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                vertexConsumer.addVertex(matrix, (float)x3, (float)y3, (float)z3)
                    .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(radio2, 1.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
                vertexConsumer.addVertex(matrix, (float)x4, (float)y4, (float)z4)
                    .setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(radio2, 0.0f)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0, 1, 0);
            }
        }

        @Override
        public void onMoving(StaticProjectile projectile) {
            ParticleUtil.addParticle(
                projectile.level(), PARTICLE,
                projectile.position(), 0,
                new Vec3(0, 0, 0), 0.1
            );
            Vector3f originalRight = projectile.getEntityData().get(StaticProjectile.DIRECTION);
            Vec3 right = MathUtil.toVec3(originalRight).normalize();
            projectile.trailPositions.addFirst(projectile.position().add(right.scale(0.05)));
            projectile.trailPositions.addFirst(projectile.position().add(right.scale(-0.05)));
            while(projectile.trailPositions.size() > MAX_LENGTH) projectile.trailPositions.removeLast();
        }

        @Override
        public void onHitEntity(StaticProjectile projectile, EntityHitResult result) {
            if(!projectile.level().isClientSide()) {
                Entity target = result.getEntity();
                Entity owner = projectile.getOwner();
                if(owner == null) return;
                if(!FilterUtil.createTargetFilter(owner).test(target) || !(owner instanceof Player player)) return;
                if(DamageUtil.attack(player, target, (float) Config.muramasaBeamDamage)) {

                }
            }
        }
    };
}
