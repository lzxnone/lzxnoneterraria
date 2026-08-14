package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.item.weapon.MeleeWeapon;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.IStaticProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class EnchantedSword extends MeleeWeapon {
    public static final String ID = "enchanted_sword";
    public static final ConfigFloat BASE_MELEE_DAMAGE = createBaseMeleeDamageConfig(ID, 4F);
    public static final ConfigFloat BASE_MELEE_ATTACK_SPEED = createBaseMeleeAttackSpeedConfig(ID, -2.4F);
    public static final ConfigFloat DAMAGE = new ConfigFloat(
        "weapon.enchanted_sword.damage",
        "enchanted_sword_damage",
        4.0f,
        0.0f,
        8388600.0f
    );
    public static final ConfigInt LIFETIME = new ConfigInt(
        "weapon.enchanted_sword.lifetime",
        "enchanted_sword_lifetime",
        60,
        1,
        1200
    );
    public static final ConfigDouble SPEED = new ConfigDouble(
        "weapon.enchanted_sword.speed",
        "enchanted_sword_speed",
        1.0,
        0.0,
        10.0
    );
    public EnchantedSword() {
        super(Tiers.IRON, new Item.Properties());
    }

    @Override
    protected float getBaseMeleeDamage(ItemStack stack) {
        return BASE_MELEE_DAMAGE.get();
    }

    @Override
    protected float getBaseMeleeAttackSpeed(ItemStack stack) {
        return BASE_MELEE_ATTACK_SPEED.get();
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                BASE_MELEE_DAMAGE,
                BASE_MELEE_ATTACK_SPEED,
                DAMAGE,
                LIFETIME,
                SPEED
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);

    public static final IStaticProjectileBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileBehavior() {
        @Override
        public void onMoving(StaticProjectile projectile) {
            ParticleUtil.addParticle(
                projectile.level(), ModParticles.COLORFUL_PARTICLE,
                projectile.position(), 0.0,
                new Vec3(0, 0, 0), 0.1
            );
        }
        @Override
        public void onHitEntity(StaticProjectile projectile, EntityHitResult result) {
            if(!projectile.level().isClientSide()) {
                Entity target = result.getEntity();
                Entity owner = projectile.getOwner();
                if(owner == null) return;
                if(!FilterUtil.createTargetFilter(owner).test(target) || !(owner instanceof Player player)) return;
                if(DamageUtil.meleeAttack(projectile, target, projectile.getEntityData().get(StaticProjectile.STACK_SOURCE),(float) DAMAGE.get(), 1.0f, 5)) {
                    ParticleUtil.addParticles(
                        (ServerLevel) projectile.level(), ModParticles.COLORFUL_PARTICLE,
                        projectile.position(), new Vec3(0.2, 0.2, 0.2),
                        0.2, 25
                    );
                    onDied(projectile);
                }
            }
        }
        @Override
        public void onHitBlock(StaticProjectile projectile, BlockHitResult result) {
            if(projectile.level().isClientSide()) return;
            if(!projectile.level().getBlockState(result.getBlockPos()).getCollisionShape(projectile.level(), result.getBlockPos()).isEmpty()) {
                onDied(projectile);
            }
        }
    };

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if(!level.isClientSide()) {
            Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
            StaticProjectile projectile = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), level);
            projectile.setOwner(player);
            Vec3 pos = new Vec3(player.getX(), player.getEyeY() - 0.1, player.getZ());
            projectile.setPos(pos);
            projectile.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.ENCHANTED_SWORD_BEAM);
            projectile.getEntityData().set(StaticProjectile.RENDER_MODE, "item");
            projectile.getEntityData().set(StaticProjectile.STACK_SOURCE, stack.copy());
            projectile.getEntityData().set(StaticProjectile.ORIGIN, MathUtil.toVector3f(pos));
            projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
            projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
            projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);
            projectile.getEntityData().set(StaticProjectile.ITEM, new ItemStack(ModItems.ENCHANTED_SWORD_BEAM.get()));
            projectile.getEntityData().set(StaticProjectile.SCALE_X, 0.5f);
            projectile.getEntityData().set(StaticProjectile.SCALE_Y, 0.5f);
            projectile.getEntityData().set(StaticProjectile.RXP, 90);
            projectile.getEntityData().set(StaticProjectile.RZP, 45);
            projectile.getEntityData().set(StaticProjectile.RYPS, 10);
            projectile.getEntityData().set(StaticProjectile.GLOW, true);
            projectile.getEntityData().set(StaticProjectile.LIFETIME, LIFETIME.get());
            projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*t", SPEED.get()));

            projectile.setDeltaMovement(MathUtil.toVec3(dirs[0]));
            level.addFreshEntity(projectile);
        }else {
            ParticleUtil.addParticles(
                player.level(), ModParticles.COLORFUL_PARTICLE,
                new Vec3(player.getX(), player.getY() + 0.5, player.getZ()), 0.2,
                new Vec3(0, 0, 0), 0.2,
                25
            );
            SoundUtil.playClientSound(player, ModSounds.BEAM.get());
        }

        player.getCooldowns().addCooldown(stack.getItem(), 15);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if(entity instanceof Player player) {
            if(isSelected) {
                boolean current = player.getCooldowns().isOnCooldown(this);
                boolean prev = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                    .copyTag().getBoolean("onCooldown");
                if(prev && !current) {
                    if(level.isClientSide()) {
                        SoundUtil.playClientSound(player, ModSounds.MAX_MANA.get());
                        ParticleUtil.addParticles(
                            player.level(), ModParticles.MAX_MANA_PARTICLE,
                            new Vec3(player.getX(), player.getY() + 1.0, player.getZ()), 0.05,
                            new Vec3(0, 0, 0), 0.05,
                            5
                        );
                    }
                }
                CustomData.update(DataComponents.CUSTOM_DATA, stack,
                    tag -> tag.putBoolean("onCooldown", current));
            }else {
                CustomData.update(DataComponents.CUSTOM_DATA, stack,
                    tag -> tag.putBoolean("onCooldown", false));
            }
        }
    }
}

