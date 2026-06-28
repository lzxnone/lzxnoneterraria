package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.IProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.ProjectileBehaviors;
import com.lzxnone.terraria.entity.projectile.TextureProjectile;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.utils.MathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class Seedler extends SwordItem {
    public Seedler() {
        super(Tiers.DIAMOND, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 8, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -2.4, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ));
    }

    public static final double GRAVITY = 0.1; //重力
    public static final double NORMAL_DAMPING = 0.6;
    public static final double TANGENT_DAMPING = 0.9;

    public static final IProjectileBehavior NUT_PROJECTILE_BEHAVIOR = new IProjectileBehavior() {
        @Override
        public void onHitBlock(TextureProjectile projectile, BlockHitResult result) {
            if(projectile.level().isClientSide()) return;

            int age = projectile.getEntityData().get(TextureProjectile.AGE);
            int currentLifetime = projectile.getEntityData().get(TextureProjectile.LIFETIME);
            int remainingLifetime = currentLifetime - age;
            if(remainingLifetime <= 0) {
                this.onDied(projectile);
                return;
            }

            CompoundTag customData = projectile.getEntityData().get(TextureProjectile.CUSTOM_DATA);
            double oldVy = customData.getDouble("vy");
            double vz = customData.getDouble("vz");
            double vy = oldVy - GRAVITY * age;

            Vector3f dir = projectile.getEntityData().get(TextureProjectile.DIRECTION);
            Vector3f up = projectile.getEntityData().get(TextureProjectile.UP);

            Vec3 velocity = new Vec3(
                vy * up.x() + vz * dir.x(),
                vy * up.y() + vz * dir.y(),
                vy * up.z() + vz * dir.z()
            );

            Vec3 normal = Vec3.atLowerCornerOf(result.getDirection().getNormal());
            Vec3 reflected = velocity.subtract(normal.scale(2 * velocity.dot(normal)));
            double normalMag = reflected.dot(normal);
            Vec3 tangentDir = reflected.subtract(normal.scale(normalMag));
            double tangentMag = tangentDir.length();
            Vec3 finalVelocity = normal.scale(normalMag * NORMAL_DAMPING);
            if(tangentMag > 0.0001) {
                finalVelocity = finalVelocity.add(tangentDir.normalize().scale(tangentMag * TANGENT_DAMPING));
            }

            double newVz = Math.sqrt(finalVelocity.x * finalVelocity.x + finalVelocity.z * finalVelocity.z);
            double newVy = finalVelocity.y;

            boolean isBounced = false;

            //竖直弹射时
            if(newVy < 0.1 && newVy > 0.0001) {
                newVy = 0;
                String newExprY = String.format(Locale.US, "-%.4f*t*t", GRAVITY * 0.5);
                projectile.getEntityData().set(TextureProjectile.EXPRESSION_Y, newExprY);
                projectile.exprY = new ExpressionBuilder(newExprY).variables("t").build();
            }else if(newVy >= 0.1) {
                String newExprY = String.format(Locale.US, "%.3f*t - %.4f*t*t", newVy, GRAVITY * 0.5);
                projectile.getEntityData().set(TextureProjectile.EXPRESSION_Y, newExprY);
                projectile.exprY = new ExpressionBuilder(newExprY).variables("t").build();
            }
            if(Math.abs(normal.y) >= 0.999 && newVy >= 0.1) isBounced = true;


            //水平弹射时
            if(newVz < 0.1 && newVz > 0.0001) {
                newVz = 0;
                projectile.getEntityData().set(TextureProjectile.EXPRESSION_Z, "0");
                projectile.exprZ = new ExpressionBuilder("0").variables("t").build();
                Vec3 reflectedH = new Vec3(reflected.x, 0, reflected.z).normalize();
                Vector3f[] newDirs = MathUtil.computeProjectileDir(MathUtil.toVector3f(reflectedH));
                projectile.getEntityData().set(TextureProjectile.DIRECTION, newDirs[0]);
                projectile.getEntityData().set(TextureProjectile.RIGHT, newDirs[2]);
            }else if(newVz >= 0.1) {
                String newExprZ = String.format(Locale.US, "%.3f*t", newVz);
                projectile.getEntityData().set(TextureProjectile.EXPRESSION_Z, newExprZ);
                projectile.exprZ = new ExpressionBuilder(newExprZ).variables("t").build();
                Vec3 reflectedH = new Vec3(reflected.x, 0, reflected.z).normalize();
                Vector3f[] newDirs = MathUtil.computeProjectileDir(MathUtil.toVector3f(reflectedH));
                projectile.getEntityData().set(TextureProjectile.DIRECTION, newDirs[0]);
                projectile.getEntityData().set(TextureProjectile.RIGHT, newDirs[2]);
            }
            if((Math.abs(normal.x) >= 0.999 || Math.abs(normal.z) >= 0.999) && newVz >= 0.1) isBounced = true;

            Vec3 hitPos = result.getLocation();
            Vec3 offsetPos = hitPos.add(normal.scale(isBounced ? 0.1 : 0));

            projectile.getEntityData().set(TextureProjectile.ORIGIN, MathUtil.toVector3f(offsetPos));
            projectile.getEntityData().set(TextureProjectile.AGE, 0);
            projectile.getEntityData().set(TextureProjectile.LIFETIME, remainingLifetime);

            customData.putDouble("vz", newVz);
            customData.putDouble("vy", newVy);
            projectile.getEntityData().set(TextureProjectile.CUSTOM_DATA, customData);

            projectile.setPos(offsetPos);
            projectile.positionOverridden = true;
        }

        @Override
        public void onDied(TextureProjectile projectile) {
            if(!projectile.level().isClientSide()) {
                Vec3 pos = projectile.position();
                Level level = projectile.level();

                ((ServerLevel) level).sendParticles(
                    ModParticles.EXPLODE_PARTICLE.get(),
                    pos.x, pos.y, pos.z, 1, 0.2, 0.2, 0.2, 0.2
                );
                ((ServerLevel) level).sendParticles(
                    ModParticles.EXPLODE_FLAME_PARTICLE,
                    pos.x, pos.y, pos.z, 10, 0.2, 0.2, 0.2, 0.2
                );
                ((ServerLevel) level).sendParticles(
                    ModParticles.SMOKE_PARTICLE,
                    pos.x, pos.y, pos.z, 10, 0.2, 0.2, 0.2, 0.2
                );

                level.playSound(null, pos.x, pos.y, pos.z,
                    ModSounds.BOOM.get(), SoundSource.PLAYERS, 4.0F, 1.0F);

                Entity owner = projectile.getOwner();
                List<LivingEntity> targets = level.getEntitiesOfClass(
                    LivingEntity.class,
                    AABB.ofSize(pos, 64, 64, 64),
                    e -> e.isAlive() && e != owner && !e.isSpectator()
                );
                targets.sort(Comparator.comparingDouble(e -> e.distanceToSqr(pos)));

                int count = projectile.getRandom().nextInt(4) + 4;
                for(int i = 0; i < count; i++) {
                    Vec3 dir;
                    if(i < targets.size()) {
                        Vec3 tPos = targets.get(i).getEyePosition();
                        dir = new Vec3(tPos.x - pos.x, tPos.y - pos.y, tPos.z - pos.z).normalize();
                    }else {
                        float yaw = projectile.getRandom().nextFloat() * (float)Math.PI * 2;
                        float pitch = (projectile.getRandom().nextFloat() - 0.5f) * 0.5f;
                        dir = new Vec3(-Math.sin(yaw) * Math.cos(pitch), Math.sin(pitch),
                                       Math.cos(yaw) * Math.cos(pitch));
                    }

                    Vector3f[] dirs = MathUtil.computeProjectileDir(MathUtil.toVector3f(dir));

                    TextureProjectile proj = new TextureProjectile(ModEntities.TEXTURE_PROJECTILE.get(), level);
                    proj.setOwner(owner);
                    proj.setPos(pos);
                    proj.getEntityData().set(TextureProjectile.BEHAVIOR, ProjectileBehaviors.SEEDLER_THORN);
                    proj.getEntityData().set(TextureProjectile.ORIGIN, MathUtil.toVector3f(pos));
                    proj.getEntityData().set(TextureProjectile.DIRECTION, dirs[0]);
                    proj.getEntityData().set(TextureProjectile.UP, dirs[1]);
                    proj.getEntityData().set(TextureProjectile.RIGHT, dirs[2]);
                    proj.getEntityData().set(TextureProjectile.ITEM, new ItemStack(ModItems.SEEDLER_THORN.get()));
                    proj.getEntityData().set(TextureProjectile.SCALE_X, 0.5f);
                    proj.getEntityData().set(TextureProjectile.SCALE_Y, 0.5f);
                    proj.getEntityData().set(TextureProjectile.RXP, 90);
                    proj.getEntityData().set(TextureProjectile.LIFETIME, 30);
                    proj.getEntityData().set(TextureProjectile.EXPRESSION_Z, "t*2");

                    proj.setDeltaMovement(MathUtil.toVec3(dirs[0]));
                    level.addFreshEntity(proj);
                }
                projectile.discard();
            }
        }

        @Override
        public void onHitEntity(TextureProjectile projectile, EntityHitResult result) {
            if(!projectile.level().isClientSide()) {
                if(result.getEntity() instanceof LivingEntity target) {
                    this.onDied(projectile);
                }
            }
        }
    };

    public static final IProjectileBehavior THORN_PROJECTILE_BEHAVIOR = new IProjectileBehavior() {
        @Override
        public void onMoving(TextureProjectile projectile) {
            projectile.level().addParticle(
                ModParticles.SEEDLER_THORN_PARTICLE.get(),
                projectile.getX(), projectile.getY(), projectile.getZ(),
                0, 0, 0
            );
        }
        @Override
        public void onHitEntity(TextureProjectile projectile, EntityHitResult result) {
            if(!projectile.level().isClientSide()) {
                if(result.getEntity() instanceof LivingEntity target) {
                    if(target.hurt(projectile.damageSources().thrown(projectile, projectile.getOwner()), 7f)) projectile.discard();
                }
            }
        }
        @Override
        public void onHitBlock(TextureProjectile projectile, BlockHitResult result) {
            if(projectile.level().isClientSide()) return;
            if(!projectile.level().getBlockState(result.getBlockPos()).getCollisionShape(projectile.level(), result.getBlockPos()).isEmpty()) {
                projectile.discard();
            }
        }
    };

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if(!level.isClientSide()) {
            Vec3 lookVec = player.getLookAngle();

            float yaw = player.getYRot();
            Vec3 horizontalDir = new Vec3(
                -Math.sin(yaw * Math.PI / 180),
                0,
                Math.cos(yaw * Math.PI / 180)
            );
            Vector3f[] dirs = MathUtil.computeProjectileDir(
                MathUtil.toVector3f(horizontalDir)
            );

            double vz = Math.sqrt(lookVec.x * lookVec.x + lookVec.z * lookVec.z) * 1.5;
            double vy = lookVec.y * 1.5;
            String exprZ = String.format(Locale.US, "%.3f*t", vz);
            String exprY = String.format(Locale.US, "%.3f*t - %.4f*t*t", vy, GRAVITY * 0.5);

            CompoundTag customData = new CompoundTag();
            customData.putDouble("vz", vz);
            customData.putDouble("vy", vy);

            TextureProjectile projectile = new TextureProjectile(ModEntities.TEXTURE_PROJECTILE.get(), level);
            projectile.setOwner(player);
            Vec3 pos = new Vec3(player.getX(), player.getEyeY() - 0.1, player.getZ());
            projectile.setPos(pos);
            projectile.getEntityData().set(TextureProjectile.BEHAVIOR, ProjectileBehaviors.SEEDLER_NUT);
            projectile.getEntityData().set(TextureProjectile.ORIGIN, MathUtil.toVector3f(pos));
            projectile.getEntityData().set(TextureProjectile.DIRECTION, dirs[0]);
            projectile.getEntityData().set(TextureProjectile.UP, dirs[1]);
            projectile.getEntityData().set(TextureProjectile.RIGHT, dirs[2]);
            projectile.getEntityData().set(TextureProjectile.ITEM, new ItemStack(ModItems.SEEDLER_NUT.get()));
            projectile.getEntityData().set(TextureProjectile.SCALE_X, 0.5f);
            projectile.getEntityData().set(TextureProjectile.SCALE_Y, 0.5f);
            projectile.getEntityData().set(TextureProjectile.RXPS, 10);
            projectile.getEntityData().set(TextureProjectile.LIFETIME, (int) (7 + Math.random() * 36));
            projectile.getEntityData().set(TextureProjectile.EXPRESSION_Z, exprZ);
            projectile.getEntityData().set(TextureProjectile.EXPRESSION_Y, exprY);
            projectile.getEntityData().set(TextureProjectile.CUSTOM_DATA, customData);

            projectile.setDeltaMovement(MathUtil.toVec3(dirs[0]));
            level.addFreshEntity(projectile);
        }

        player.getCooldowns().addCooldown(stack.getItem(), 10);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
