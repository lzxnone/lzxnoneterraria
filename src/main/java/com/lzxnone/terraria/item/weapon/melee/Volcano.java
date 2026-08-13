package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.item.weapon.MeleeWeapon;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.IItemWaveBehavior;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.ParticleUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.List;

public class Volcano extends MeleeWeapon {
    public static final String ID = "volcano";
    public static final ConfigFloat BASE_MELEE_DAMAGE = createBaseMeleeDamageConfig(ID, 6F);
    public static final ConfigFloat BASE_MELEE_ATTACK_SPEED = createBaseMeleeAttackSpeedConfig(ID, -2.4F);
    public static final ConfigDouble EXPLOSION_RANGE = new ConfigDouble(
        "weapon.volcano.explosion_range",
        "volcano_explosion_range",
        4.0,
        1.0,
        64.0
    );
    public static final ConfigFloat EXPLOSION_DAMAGE = new ConfigFloat(
        "weapon.volcano.explosion_damage",
        "volcano_explosion_damage",
        6.0f,
        0.0f,
        8388600.0f
    );
    public static final ConfigInt EXPLOSION_MAX_HIT_COUNT = new ConfigInt(
        "weapon.volcano.explosion_max_hit_count",
        "volcano_explosion_max_hit_count",
        2,
        0,
        100
    );
    public static final ConfigInt IGNITE_SECONDS = new ConfigInt(
        "weapon.volcano.ignite_seconds",
        "volcano_ignite_seconds",
        3,
        0,
        60
    );
    public Volcano() {
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
                EXPLOSION_RANGE,
                EXPLOSION_DAMAGE,
                EXPLOSION_MAX_HIT_COUNT,
                IGNITE_SECONDS
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);

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
                                AABB.ofSize(player.getBoundingBox().getCenter(), EXPLOSION_RANGE.get() * 2, EXPLOSION_RANGE.get() * 2, EXPLOSION_RANGE.get() * 2),
                                FilterUtil.createLivingTargetFilter(player)
                            );
                            targets.sort(Comparator.comparingDouble(e -> e.distanceToSqr(target.position())));
                            int hitCount = 0;
                            for(LivingEntity livingEntity : targets) {
                                if(livingEntity.getUUID() == target.getUUID()) continue;
                                if(hitCount >= EXPLOSION_MAX_HIT_COUNT.get()) break;
                                if(DamageUtil.meleeAttack(player, livingEntity, player.getWeaponItem(), (float) EXPLOSION_DAMAGE.get(), 1.0f)) {
                                    livingTarget.igniteForSeconds(IGNITE_SECONDS.get());
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
                        if(player.getRandom().nextInt(2) == 0) livingTarget.igniteForSeconds(IGNITE_SECONDS.get());
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

