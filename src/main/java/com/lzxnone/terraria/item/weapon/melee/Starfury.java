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
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.joml.Vector3f;

public class Starfury extends SwordItem {
    public Starfury() {
        super(Tiers.IRON, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 5, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -2.4, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ));
    }

    public static final IProjectileBehavior PROJECTILE_BEHAVIOR = new IProjectileBehavior() {
        @Override
        public void onMoving(TextureProjectile projectile) {
            projectile.level().addParticle(
                ModParticles.STAR_PARTICLE.get(),
                projectile.getX(), projectile.getY(), projectile.getZ(),
                (Math.random() * 2 - 1) * 0.1, (Math.random() * 2 - 1) * 0.1, (Math.random() * 2 - 1) * 0.1
            );
        }
        @Override
        public void onHitEntity(TextureProjectile projectile, EntityHitResult result) {
            if(!projectile.level().isClientSide()) {
                if(result.getEntity() instanceof LivingEntity target) {
                    target.hurt(projectile.damageSources().thrown(projectile, projectile.getOwner()), 8f);
                }
            }
        }
        @Override
        public void onHitBlock(TextureProjectile projectile, BlockHitResult result) {
            if(projectile.level().isClientSide()) return;

            CompoundTag customData = projectile.getEntityData().get(TextureProjectile.CUSTOM_DATA);
            if(!customData.contains("targetLifetime")) return;

            int targetLifetime = customData.getInt("targetLifetime");
            int age = projectile.getEntityData().get(TextureProjectile.AGE);
            if(age < targetLifetime + 1) {
                EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                    projectile.level(),
                    projectile,
                    projectile.position(),
                    projectile.position().add(projectile.getDeltaMovement()),
                    projectile.getBoundingBox()
                        .expandTowards(projectile.getDeltaMovement())
                        .inflate(1.0),
                    entity -> !entity.isSpectator() && entity.isPickable() && entity != projectile.getOwner()
                );
                if(entityHit != null) {
                    this.onHitEntity(projectile, entityHit);
                }
                return;
            }

            if(!projectile.level().getBlockState(result.getBlockPos()).getCollisionShape(projectile.level(), result.getBlockPos()).isEmpty()) {
                ((ServerLevel) projectile.level()).sendParticles(
                    ModParticles.STAR_PARTICLE.get(),
                    projectile.getX(), projectile.getY(), projectile.getZ(),
                    25,
                    0.2, 0.2, 0.2,
                    0.2
                );
                projectile.level().playSound(null, projectile, ModSounds.STAR_COLLIDE.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
                projectile.discard();
            }
        }
        @Override
        public void onDied(TextureProjectile projectile) {
            if(!projectile.level().isClientSide()) {
                projectile.level().playSound(null, projectile, ModSounds.STAR_COLLIDE.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
                projectile.discard();
            }
        }
    };

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if(!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            Vec3 targetPos = getTargetPosition(player, serverLevel, 24.0D);
            double spawnHeight = 20.0D;
            double slantOffset = 2.0D;

            Vec3 spawnPos = new Vec3(
                targetPos.x + slantOffset * (Math.random() * 2 - 1),
                targetPos.y + spawnHeight,
                targetPos.z + slantOffset * (Math.random() * 2 - 1)
            );
            Vector3f[] dirs = MathUtil.computeProjectileDir(
                MathUtil.toVector3f(new Vec3(targetPos.x - spawnPos.x, targetPos.y - spawnPos.y, targetPos.z - spawnPos.z))
            );

            TextureProjectile projectile = new TextureProjectile(ModEntities.TEXTURE_PROJECTILE.get(), level);
            projectile.setOwner(player);
            projectile.setPos(spawnPos);
            projectile.getEntityData().set(TextureProjectile.BEHAVIOR, ProjectileBehaviors.STARFURY_STAR);
            projectile.getEntityData().set(TextureProjectile.ORIGIN, MathUtil.toVector3f(spawnPos));
            projectile.getEntityData().set(TextureProjectile.ITEM, new ItemStack(ModItems.STARFURY_STAR.get()));
            projectile.getEntityData().set(TextureProjectile.LIFETIME, 300);
            projectile.getEntityData().set(TextureProjectile.DIRECTION, dirs[0]);
            projectile.getEntityData().set(TextureProjectile.UP, dirs[1]);
            projectile.getEntityData().set(TextureProjectile.RIGHT, dirs[2]);
            projectile.getEntityData().set(TextureProjectile.SCALE_X, 1f);
            projectile.getEntityData().set(TextureProjectile.SCALE_Y, 2f);
            projectile.getEntityData().set(TextureProjectile.GLOW, true);
            projectile.getEntityData().set(TextureProjectile.RXP, 90);
            projectile.getEntityData().set(TextureProjectile.RZP, 90);
            projectile.getEntityData().set(TextureProjectile.EXPRESSION_Z, "t*2");

            CompoundTag customData = new CompoundTag();
            customData.putInt("targetLifetime", (int) Math.floor(targetPos.distanceTo(spawnPos) / 2.0D));
            projectile.getEntityData().set(TextureProjectile.CUSTOM_DATA, customData);

            projectile.setDeltaMovement(MathUtil.toVec3(dirs[0]));
            level.addFreshEntity(projectile);

            ((ServerLevel) player.level()).sendParticles(
                ModParticles.STAR_PARTICLE.get(),
                player.getX(), player.getY() + 0.5, player.getZ(),
                25,
                0.2, 0.2, 0.2,
                0.2
            );
            player.level().playSound(null, player, ModSounds.STAR_FALL.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
        }

        player.getCooldowns().addCooldown(this, 13);
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    private Vec3 getTargetPosition(Player player, Level level, double maxRange) {
        Vec3 eyePos = player.getEyePosition(1.0F);
        Vec3 lookVec = player.getLookAngle();
        Vec3 maxRangeEnd = eyePos.add(lookVec.scale(maxRange));

        BlockHitResult blockHit = level.clip(new ClipContext(
            eyePos, maxRangeEnd,
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            player
        ));

        Vec3 finalTarget = maxRangeEnd;
        if(blockHit.getType() != HitResult.Type.MISS) {
            finalTarget = blockHit.getLocation();
        }

        AABB searchBox = player.getBoundingBox().expandTowards(lookVec.scale(maxRange)).inflate(1.0D);
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
            level, player, eyePos, finalTarget, searchBox,
            entity -> !entity.isSpectator() && entity.isPickable() && entity != player
        );

        if(entityHit != null) {
            finalTarget = entityHit.getLocation();
        }

        return finalTarget;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if(isSelected && entity instanceof Player player) {
            boolean currentlyOnCooldown = player.getCooldowns().isOnCooldown(this);
            boolean wasOnCooldown = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                    .copyTag().getBoolean("WasOnCooldown");

            if(wasOnCooldown && !currentlyOnCooldown) {
                if(level.isClientSide()) {
                    player.playSound(ModSounds.MAX_MANA.get(), 4.0F, 1.0F);
                }else {
                    ((ServerLevel) player.level()).sendParticles(
                        ModParticles.MAX_MANA_PARTICLE,
                        player.getX(), player.getY() + 0.5, player.getZ(),
                        5,
                        0.2, 0.2, 0.2,
                        0.2
                    );
                }
            }

            CustomData.update(DataComponents.CUSTOM_DATA, stack,
                tag -> tag.putBoolean("WasOnCooldown", currentlyOnCooldown));
        }
    }
}
