package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.IProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.ProjectileBehaviors;
import com.lzxnone.terraria.entity.projectile.TextureProjectile;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.utils.MathUtil;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class EnchantedSword extends SwordItem {

    public EnchantedSword() {
        super(Tiers.IRON, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 4,
                        AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -2.4,
                        AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ));
    }



    public static final IProjectileBehavior PROJECTILE_BEHAVIOR = new IProjectileBehavior() {
        @Override
        public void onMoving(TextureProjectile projectile) {
            projectile.level().addParticle(
                ModParticles.COLORFUL_PARTICLE,
                projectile.getX(), projectile.getY(), projectile.getZ(),
                (Math.random() * 2 - 1) * 0.1, (Math.random() * 2 - 1) * 0.1, (Math.random() * 2 - 1) * 0.1
            );
        }
        @Override
        public void onHitEntity(TextureProjectile projectile, EntityHitResult result) {
            if(!projectile.level().isClientSide()) {
                if(result.getEntity() instanceof LivingEntity target) {
                    if(target.hurt(projectile.damageSources().thrown(projectile, projectile.getOwner()), 5.5f)) {
                        ((ServerLevel) projectile.level()).sendParticles(
                            ModParticles.COLORFUL_PARTICLE,
                            projectile.getX(), projectile.getY(), projectile.getZ(),
                            25,
                            0.2, 0.2, 0.2,
                            0.2
                        );
                        projectile.discard();
                    }
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

            Vector3f[] dirs = MathUtil.computeProjectileDir(
                MathUtil.toVector3f(lookVec)
            );
            TextureProjectile projectile = new TextureProjectile(ModEntities.TEXTURE_PROJECTILE.get(), level);
            projectile.setOwner(player);
            Vec3 pos = new Vec3(player.getX(), player.getEyeY() - 0.1, player.getZ());
            projectile.setPos(pos);
            projectile.getEntityData().set(TextureProjectile.BEHAVIOR, ProjectileBehaviors.ENCHANTED_SWORD_BEAM);
            projectile.getEntityData().set(TextureProjectile.ORIGIN, MathUtil.toVector3f(pos));
            projectile.getEntityData().set(TextureProjectile.DIRECTION, dirs[0]);
            projectile.getEntityData().set(TextureProjectile.UP, dirs[1]);
            projectile.getEntityData().set(TextureProjectile.RIGHT, dirs[2]);
            projectile.getEntityData().set(TextureProjectile.ITEM, new ItemStack(ModItems.ENCHANTED_SWORD_BEAM.get()));
            projectile.getEntityData().set(TextureProjectile.SCALE_X, 0.5f);
            projectile.getEntityData().set(TextureProjectile.SCALE_Y, 0.5f);
            projectile.getEntityData().set(TextureProjectile.RXP, 90);
            projectile.getEntityData().set(TextureProjectile.RZP, 45);
            projectile.getEntityData().set(TextureProjectile.RYPS, 10);
            projectile.getEntityData().set(TextureProjectile.GLOW, true);
            projectile.getEntityData().set(TextureProjectile.EXPRESSION_Z, "t");

            projectile.setDeltaMovement(MathUtil.toVec3(dirs[0]));
            level.addFreshEntity(projectile);

            ((ServerLevel) player.level()).sendParticles(
                ModParticles.COLORFUL_PARTICLE,
                player.getX(), player.getY() + 0.5, player.getZ(),
                25,
                0.2, 0.2, 0.2,
                0.2
            );
            player.level().playSound(null, player, ModSounds.BEAM.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
        }

        player.getCooldowns().addCooldown(stack.getItem(), 15);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
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
