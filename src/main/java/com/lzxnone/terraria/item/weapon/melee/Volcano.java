package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.item.weapon.MeleeWeapon;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.IItemWaveBehavior;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.ui.config.ConfigFactory;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.ConfigUtil;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.ParticleUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.List;

public class Volcano extends MeleeWeapon {
    private static final String CONFIG_TRANSLATION_PREFIX = "lzxnoneterraria.configuration.";

    public static final String EXPLOSION_RANGE_PATH = "weapon.volcano.explosion_range";
    public static final double EXPLOSION_RANGE_DEFAULT = 4.0;
    public static final double EXPLOSION_RANGE_MIN = 1.0;
    public static final double EXPLOSION_RANGE_MAX = 64.0;

    public static final String EXPLOSION_DAMAGE_PATH = "weapon.volcano.explosion_damage";
    public static final float EXPLOSION_DAMAGE_DEFAULT = 6.0f;
    public static final float EXPLOSION_DAMAGE_MIN = 0.0f;
    public static final float EXPLOSION_DAMAGE_MAX = 8388600.0f;

    public static final String EXPLOSION_MAX_HIT_COUNT_PATH = "weapon.volcano.explosion_max_hit_count";
    public static final int EXPLOSION_MAX_HIT_COUNT_DEFAULT = 2;
    public static final int EXPLOSION_MAX_HIT_COUNT_MIN = 0;
    public static final int EXPLOSION_MAX_HIT_COUNT_MAX = 100;

    public static final String IGNITE_SECONDS_PATH = "weapon.volcano.ignite_seconds";
    public static final int IGNITE_SECONDS_DEFAULT = 3;
    public static final int IGNITE_SECONDS_MIN = 0;
    public static final int IGNITE_SECONDS_MAX = 60;

    public Volcano() {
        super(Tiers.IRON, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 6, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -2.4, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ));
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigFactory.loadDoubleConfig(EXPLOSION_RANGE_PATH, configText("volcano_explosion_range"), configTooltip("volcano_explosion_range"), EXPLOSION_RANGE_DEFAULT, EXPLOSION_RANGE_MIN, EXPLOSION_RANGE_MAX);
            ConfigFactory.loadFloatConfig(EXPLOSION_DAMAGE_PATH, configText("volcano_explosion_damage"), configTooltip("volcano_explosion_damage"), EXPLOSION_DAMAGE_DEFAULT, EXPLOSION_DAMAGE_MIN, EXPLOSION_DAMAGE_MAX);
            ConfigFactory.loadIntConfig(EXPLOSION_MAX_HIT_COUNT_PATH, configText("volcano_explosion_max_hit_count"), configTooltip("volcano_explosion_max_hit_count"), EXPLOSION_MAX_HIT_COUNT_DEFAULT, EXPLOSION_MAX_HIT_COUNT_MIN, EXPLOSION_MAX_HIT_COUNT_MAX);
            ConfigFactory.loadIntConfig(IGNITE_SECONDS_PATH, configText("volcano_ignite_seconds"), configTooltip("volcano_ignite_seconds"), IGNITE_SECONDS_DEFAULT, IGNITE_SECONDS_MIN, IGNITE_SECONDS_MAX);
        }
    };

    private static Component configText(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key);
    }

    private static Component configTooltip(String key) {
        return Component.translatable(CONFIG_TRANSLATION_PREFIX + key + ".tooltip");
    }

    public static double getExplosionRange() {
        return Math.clamp(ConfigUtil.readDouble(EXPLOSION_RANGE_PATH, EXPLOSION_RANGE_DEFAULT), EXPLOSION_RANGE_MIN, EXPLOSION_RANGE_MAX);
    }

    public static float getExplosionDamage() {
        return Math.clamp(ConfigUtil.readFloat(EXPLOSION_DAMAGE_PATH, EXPLOSION_DAMAGE_DEFAULT), EXPLOSION_DAMAGE_MIN, EXPLOSION_DAMAGE_MAX);
    }

    public static int getExplosionMaxHitCount() {
        return Math.clamp(ConfigUtil.readInt(EXPLOSION_MAX_HIT_COUNT_PATH, EXPLOSION_MAX_HIT_COUNT_DEFAULT), EXPLOSION_MAX_HIT_COUNT_MIN, EXPLOSION_MAX_HIT_COUNT_MAX);
    }

    public static int getIgniteSeconds() {
        return Math.clamp(ConfigUtil.readInt(IGNITE_SECONDS_PATH, IGNITE_SECONDS_DEFAULT), IGNITE_SECONDS_MIN, IGNITE_SECONDS_MAX);
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "volcano",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/volcano.png"),
        Component.translatable("item.lzxnoneterraria.volcano"),
        CONFIG_DATA
    );

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.075f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(1.0F, 0.8F, 0.5F),
            new Vector3f(1.0F, 0.5F, 0.0F),
            new Vector3f(1.0F, 0.9F, 0.0F),
        }
    );

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {};

    public static final IItemWaveBehavior ITEM_WAVE_BEHAVIOR = new IItemWaveBehavior() {
        @Override
        public void onLeftClickAir(PlayerInteractEvent.LeftClickEmpty event) {
            Player player = event.getEntity();
            if(player.level().isClientSide()) {
                ItemStack itemStack = player.getMainHandItem();
                if(itemStack.isEmpty()) return;
                Item item = itemStack.getItem();
                if(item instanceof Volcano) {
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
                if(item instanceof Volcano) {
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
            if(item instanceof Volcano) {
                if(!player.level().isClientSide()) {
                    Entity target = event.getTarget();
                    if(target instanceof LivingEntity livingTarget && FilterUtil.createLivingTargetFilter(player).test(livingTarget)) {
                        if(!player.getCooldowns().isOnCooldown(item)) {
                            List<LivingEntity> targets = player.level().getEntitiesOfClass(
                                LivingEntity.class,
                                AABB.ofSize(player.getBoundingBox().getCenter(), getExplosionRange() * 2, getExplosionRange() * 2, getExplosionRange() * 2),
                                FilterUtil.createLivingTargetFilter(player)
                            );
                            targets.sort(Comparator.comparingDouble(e -> e.distanceToSqr(target.position())));
                            int hitCount = 0;
                            for(LivingEntity livingEntity : targets) {
                                if(livingEntity.getUUID() == target.getUUID()) continue;
                                if(hitCount >= getExplosionMaxHitCount()) break;
                                if(DamageUtil.meleeAttack(player, livingEntity, player.getWeaponItem(), (float) getExplosionDamage(), 1.0f)) {
                                    livingTarget.igniteForSeconds(getIgniteSeconds());
                                    hitCount++;
                                }
                            }

                            StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), livingTarget.level());
                            summon.setOwner(player);
                            summon.getEntityData().set(StaticSummon.STACK_SOURCE, player.getWeaponItem().copy());
                            Vec3 pos = livingTarget.position();
                            summon.setPos(pos);
                            summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.VOLCANO_LIGHT);
                            summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
                            summon.getEntityData().set(StaticSummon.LIFETIME, 10);
                            summon.setNoGravity(true);
                            summon.noPhysics = true;
                            summon.level().addFreshEntity(summon);

                            int randomSound = player.getRandom().nextInt(3);
                            if(randomSound == 0) {
                                SoundUtil.playServerSound(target.level(), ModSounds.EXPLOSIVE_TRAP_EXPLODE0.get(), target.position());
                            }else if(randomSound == 1) {
                                SoundUtil.playServerSound(target.level(), ModSounds.EXPLOSIVE_TRAP_EXPLODE1.get(), target.position());
                            }else {
                                SoundUtil.playServerSound(target.level(), ModSounds.EXPLOSIVE_TRAP_EXPLODE2.get(), target.position());
                            }
                            ParticleUtil.addParticles(
                                (ServerLevel) target.level(), ModParticles.SOLAR_EXPLOSION_PARTICLE.get(),
                                new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2.0, target.getZ()), new Vec3(0, 0, 0),
                                0, 1
                            );
                            ParticleUtil.addParticles(
                                (ServerLevel) target.level(), ModParticles.EXPLODE_PARTICLE.get(),
                                new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2.0, target.getZ()), new Vec3(0, 0, 0),
                                0.2, (int) (30 + Math.random() * 30)
                            );
                            ParticleUtil.addParticles(
                                (ServerLevel) target.level(), PARTICLE,
                                new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2.0, target.getZ()), new Vec3(0, 0, 0),
                                0.4, 100
                            );
                            player.getCooldowns().addCooldown(item, 10);
                        }
                        if(player.getRandom().nextInt(2) == 0) livingTarget.igniteForSeconds(getIgniteSeconds());
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
}

