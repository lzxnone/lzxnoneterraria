package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.IStaticProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.ConfigUtil;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.utils.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.joml.Vector3f;

public class StarWrath extends SwordItem {
    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";

    public static final String DAMAGE_PATH = "weapon.star_wrath.damage";
    public static final double DAMAGE_DEFAULT = 16.0;
    public static final double DAMAGE_MIN = 0.0;
    public static final double DAMAGE_MAX = 2147483647.0;

    public static final String SPEED_PATH = "weapon.star_wrath.speed";
    public static final double SPEED_DEFAULT = 2.0;
    public static final double SPEED_MIN = 0.0;
    public static final double SPEED_MAX = 10.0;

    public static final String MAX_RANGE_PATH = "weapon.star_wrath.max_range";
    public static final double MAX_RANGE_DEFAULT = 48.0;
    public static final double MAX_RANGE_MIN = 1.0;
    public static final double MAX_RANGE_MAX = 1024.0;

    public StarWrath() {
        super(Tiers.DIAMOND, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 15, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -2.4, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ).rarity(Rarity.RARE));
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigFactory.loadDoubleConfig(DAMAGE_PATH, configText("star_wrath_damage"), configTooltip("star_wrath_damage"), DAMAGE_DEFAULT, DAMAGE_MIN, DAMAGE_MAX);
            ConfigFactory.loadDoubleConfig(SPEED_PATH, configText("star_wrath_speed"), configTooltip("star_wrath_speed"), SPEED_DEFAULT, SPEED_MIN, SPEED_MAX);
            ConfigFactory.loadDoubleConfig(MAX_RANGE_PATH, configText("star_wrath_max_range"), configTooltip("star_wrath_max_range"), MAX_RANGE_DEFAULT, MAX_RANGE_MIN, MAX_RANGE_MAX);
        }
    };

    private static Component configText(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key);
    }

    private static Component configTooltip(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key + ".tooltip");
    }

    public static double getDamage() {
        return Math.clamp(ConfigUtil.readDouble(DAMAGE_PATH, DAMAGE_DEFAULT), DAMAGE_MIN, DAMAGE_MAX);
    }

    public static double getSpeed() {
        return Math.clamp(ConfigUtil.readDouble(SPEED_PATH, SPEED_DEFAULT), SPEED_MIN, SPEED_MAX);
    }

    public static double getMaxRange() {
        return Math.clamp(ConfigUtil.readDouble(MAX_RANGE_PATH, MAX_RANGE_DEFAULT), MAX_RANGE_MIN, MAX_RANGE_MAX);
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "star_wrath",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/star_wrath.png"),
        Component.translatable("item.lzxnoneterraria.star_wrath"),
        CONFIG_DATA
    );

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.05f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(1.0F, 0.8F, 0.5F),
            new Vector3f(1.0F, 0.5F, 0.0F),
            new Vector3f(1.0F, 0.9F, 0.0F)
        }
    );

    public static final DustParticleOptions PARTICLE2 = new DustParticleOptions(
        0.1f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(1.0F, 0.0F, 1.0F),
            new Vector3f(1.0F, 0.6F, 0.8F),
            new Vector3f(0.7F, 0.7F, 0.7F)
        }
    );

    public static final IStaticProjectileBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileBehavior() {
        @Override
        public void onMoving(StaticProjectile projectile) {
            if(projectile.tickCount % 3 == 0) {
                ParticleUtil.addParticle(
                    projectile.level(), ModParticles.STAR_PARTICLE.get(),
                    projectile.position(), 1.0,
                    new Vec3(0, 0, 0), 0.1
                );
            }
            ParticleUtil.addParticles(
                projectile.level(), PARTICLE,
                projectile.position(), 1.0,
                new Vec3(0, 0, 0), 0.1,
                3
            );
        }
        @Override
        public void onHitEntity(StaticProjectile projectile, EntityHitResult result) {
            if(!projectile.level().isClientSide()) {
                Entity target = result.getEntity();
                Entity owner = projectile.getOwner();
                if(!FilterUtil.createTargetFilter(owner).test(target) || !(owner instanceof Player player)) return;
                if(DamageUtil.attack(player, target, (float) getDamage())) {
                    target.invulnerableTime = 2;
                }
            }
        }
        @Override
        public void onHitBlock(StaticProjectile projectile, BlockHitResult result) {
            CompoundTag customData = projectile.getEntityData().get(StaticProjectile.CUSTOM_DATA);
            if(!customData.contains("targetLifetime")) return;

            int targetLifetime = customData.getInt("targetLifetime");
            int age = projectile.getEntityData().get(StaticProjectile.AGE);

            if(!projectile.level().isClientSide()) {
                if(age < targetLifetime) {
                    EntityHitResult entityHit = CollisionUtil.checkEntityHit(projectile, 3.0);
                    if(entityHit != null) {
                        this.onHitEntity(projectile, entityHit);
                    }
                }else {
                    if(!projectile.level().getBlockState(result.getBlockPos()).getCollisionShape(projectile.level(), result.getBlockPos()).isEmpty()) {
                        onDied(projectile);
                    }
                }
            }
        }

        @Override
        public void onDied(StaticProjectile projectile) {
            if(!projectile.level().isClientSide()) {
                ParticleUtil.addParticles(
                    (ServerLevel) projectile.level(), ModParticles.STAR_PARTICLE.get(),
                    projectile.position(), new Vec3(0.5, 0.5, 0.5),
                    0.2, 10
                );
                ParticleUtil.addParticles(
                    (ServerLevel) projectile.level(), PARTICLE,
                    projectile.position(), new Vec3(0.5, 0.5, 0.5),
                    0.2, 10
                );
                ParticleUtil.addParticles(
                    (ServerLevel) projectile.level(), PARTICLE2,
                    projectile.position(), new Vec3(0.5, 0.5, 0.5),
                    0.2, 30
                );
                SoundUtil.playServerSound(projectile.level(), ModSounds.STAR_COLLIDE.get(), projectile.position());
                projectile.discard();
            }
        }
    };

    public static final double HEIGHT = 20.0;
    public static final double DELTA_HEIGHT = 10.0;
    public static final double SPAWN_OFFSET = 10.0;
    public static final double HIT_OFFSET = 1.5;
    public static final int SPAWN_COUNT = 3;

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if(!level.isClientSide()) {
            for(int i = 0;i < SPAWN_COUNT;i++) {
                Vec3 targetPos = MathUtil.getCrosshairPos(player, level, getMaxRange());

                Vec3 spawnPos = new Vec3(
                    targetPos.x + SPAWN_OFFSET * (Math.random() * 2 - 1),
                    targetPos.y + HEIGHT + i * DELTA_HEIGHT,
                    targetPos.z + SPAWN_OFFSET * (Math.random() * 2 - 1)
                );

                Vec3 hitPos = new Vec3(
                    targetPos.x + HIT_OFFSET * (Math.random() * 2 - 1),
                    targetPos.y,
                    targetPos.z + HIT_OFFSET * (Math.random() * 2 - 1)
                );

                Vector3f[] dirs = MathUtil.computeCoordinateSystem(new Vec3(hitPos.x - spawnPos.x, hitPos.y - spawnPos.y, hitPos.z - spawnPos.z).toVector3f(), 0);

                StaticProjectile projectile = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), level);
                projectile.setOwner(player);
                projectile.setPos(spawnPos);
                projectile.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.STAR_WRATH_STAR);
                projectile.getEntityData().set(StaticProjectile.RENDER_MODE, "item");
                projectile.getEntityData().set(StaticProjectile.ORIGIN, MathUtil.toVector3f(spawnPos));
                projectile.getEntityData().set(StaticProjectile.ITEM, new ItemStack(ModItems.STAR_WRATH_STAR.get()));
                projectile.getEntityData().set(StaticProjectile.LIFETIME, 300);
                projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
                projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
                projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);
                projectile.getEntityData().set(StaticProjectile.SCALE_X, 2f);
                projectile.getEntityData().set(StaticProjectile.SCALE_Y, 1f);
                projectile.getEntityData().set(StaticProjectile.GLOW, true);
                projectile.getEntityData().set(StaticProjectile.RXP, 90);
                projectile.getEntityData().set(StaticProjectile.RZP, 90);
                projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*t", getSpeed()));

                CompoundTag customData = new CompoundTag();
                customData.putInt("targetLifetime", (int) Math.round(targetPos.distanceTo(spawnPos) / getSpeed()));
                projectile.getEntityData().set(StaticProjectile.CUSTOM_DATA, customData);

                projectile.setDeltaMovement(MathUtil.toVec3(dirs[0]));
                level.addFreshEntity(projectile);

                CustomData.update(DataComponents.CUSTOM_DATA, itemstack,
                    tag -> tag.putInt("playSoundCount", 3 + player.level().random.nextInt(3)));
            }
        }else {
            ParticleUtil.addParticles(
                player.level(), ModParticles.STAR_PARTICLE.get(),
                new Vec3(player.getX(), player.getY() + 0.5, player.getZ()), 0.2,
                new Vec3(0, 0, 0), 0.2,
                10
            );
            ParticleUtil.addParticles(
                player.level(), PARTICLE,
                new Vec3(player.getX(), player.getY() + 0.5, player.getZ()), 0.2,
                new Vec3(0, 0, 0), 0.2,
                10
            );
            SoundUtil.playClientSound(player, ModSounds.WAVE2.get());
        }

        player.getCooldowns().addCooldown(this, 3);
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if(entity instanceof Player player) {
            if(isSelected) {
                if(player.tickCount % 4 == 0) {
                    int count = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                        .copyTag().getInt("playSoundCount");
                    if(count > 0) {
                        SoundUtil.playClientSound(player, ModSounds.STAR_FALL.get());
                        CustomData.update(DataComponents.CUSTOM_DATA, stack,
                            tag -> tag.putInt("playSoundCount", count - 1));
                    }
                }
            }else {
                int count = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                    .copyTag().getInt("playSoundCount");
                if(count > 0) {
                    CustomData.update(DataComponents.CUSTOM_DATA, stack,
                        tag -> tag.putInt("playSoundCount", 0));
                }
            }
        }
    }
}
