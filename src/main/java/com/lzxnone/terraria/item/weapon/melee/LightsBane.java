package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.weapon.MeleeWeapon;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.beam.SwordBeam;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.IItemWaveBehavior;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.CollisionUtil;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.ParticleUtil;
import com.lzxnone.terraria.utils.SearchUtil;
import com.mojang.math.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import org.joml.Vector3f;

import java.util.List;

public class LightsBane extends MeleeWeapon {
    public static final String ID = "lights_bane";
    public static final ConfigFloat BASE_MELEE_DAMAGE = createBaseMeleeDamageConfig(ID, 3F);
    public static final ConfigFloat BASE_MELEE_ATTACK_SPEED = createBaseMeleeAttackSpeedConfig(ID, -1.0F);
    public static final ConfigFloat BIG_DAMAGE = new ConfigFloat(
        "weapon.lights_bane.big_damage",
        "lights_bane_big_damage",
        4.0f,
        0.0f,
        8388600.0f
    );
    public static final ConfigFloat SMALL_DAMAGE = new ConfigFloat(
        "weapon.lights_bane.small_damage",
        "lights_bane_small_damage",
        2.0f,
        0.0f,
        8388600.0f
    );
    public static final ConfigDouble TARGET_RANGE = new ConfigDouble(
        "weapon.lights_bane.target_range",
        "lights_bane_target_range",
        2.0,
        1.0,
        64.0
    );
    public LightsBane() {
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
                BIG_DAMAGE,
                SMALL_DAMAGE,
                TARGET_RANGE
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.075f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(0.463F, 0.196F, 0.918F),
            new Vector3f(0.408F, 0.373F, 0.494F)
        }
    );

    public static final float SCALE_SMALL = 0.025f;
    public static final float SCALE_BIG = 0.05f;
    public static final int HALF_WIDTH = 85;
    public static final int HALF_HEIGHT = 27;

    public static final ResourceLocation[] RES = {
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/lights_bane_slash0.png"),
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/lights_bane_slash1.png"),
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/lights_bane_slash2.png"),
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/lights_bane_slash3.png"),
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/lights_bane_slash4.png"),
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/lights_bane_slash5.png"),
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/lights_bane_slash6.png"),
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/lights_bane_slash7.png"),
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/lights_bane_slash8.png"),
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/lights_bane_slash9.png"),
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/lights_bane_slash10.png"),
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/vfx/lights_bane_slash11.png"),
    };

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {

        @Override
        public void tick(StaticSummon summon) {
            //Vec3 center = summon.position().add(summon.getLookAngle().normalize().scale(SwordBeam.DIST));
            this.checkBeforeTick(summon);
            Vec3 pos = summon.position();
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            float scale;
            if(customData.contains("big") && customData.getBoolean("big")) scale = SCALE_BIG;
            else scale = SCALE_SMALL;
            float width = HALF_WIDTH * scale;
            float height = HALF_HEIGHT * scale;

            summon.setBoundingBox(new AABB(
                pos.x - height, pos.y - width, pos.z - height,
                pos.x + height, pos.y + width, pos.z + height
            ));

            if(!summon.level().isClientSide() && summon.getOwner() instanceof Player player) {
                List<Entity> targets = summon.level().getEntitiesOfClass(Entity.class, summon.getBoundingBox(), FilterUtil.createTargetFilter(summon, summon.getOwner()));
                for(Entity target : targets) {
                    float damage = customData.contains("big") && customData.getBoolean("big") ? (float) BIG_DAMAGE.get() : (float) SMALL_DAMAGE.get();
                    if(DamageUtil.meleeAttack(summon, target, summon.getEntityData().get(StaticSummon.STACK_SOURCE), damage, 1.0f, -1)) {

                    }
                }
            }
        }
    };

    public static final IItemWaveBehavior ITEM_WAVE_BEHAVIOR = new IItemWaveBehavior() {
        @Override
        public void onAttackEntity(AttackEntityEvent event) {
            Player player = event.getEntity();
            ItemStack itemStack = player.getMainHandItem();
            if(itemStack.isEmpty()) return;
            Item item = itemStack.getItem();
            if(item instanceof LightsBane) {
                if(!player.level().isClientSide()) {
                    if(!player.getCooldowns().isOnCooldown(item)) {
                        Entity target = event.getTarget();
                        Vec3 pos = target.position();
                        double targetRange = TARGET_RANGE.get();
                        AABB searchBox = new AABB(
                            pos.x - targetRange, pos.y - targetRange, pos.z - targetRange,
                            pos.x + targetRange, pos.y + targetRange, pos.z + targetRange
                        );
                        List<Entity> entities = SearchUtil.searchNearestEnemies(player, player, searchBox, 1);
                        Entity summonTarget = null;
                        if(!entities.isEmpty()) summonTarget = entities.getFirst();
                        if(summonTarget != null) {
                            StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), player.level());
                            summon.setOwner(player);
                            summon.getEntityData().set(StaticSummon.STACK_SOURCE, player.getWeaponItem().copy());
                            summon.setPos(new Vec3(summonTarget.getX(), summonTarget.getY() + summonTarget.getBbHeight(), summonTarget.getZ()));
                            summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.LIGHTS_BANE_SLASH);
                            summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
                            summon.getEntityData().set(StaticSummon.RXP, -90);
                            summon.getEntityData().set(StaticSummon.RZP, -90);
                            summon.getEntityData().set(StaticSummon.GLOW, true);
                            summon.getEntityData().set(StaticSummon.LIFETIME, 10);
                            summon.setNoGravity(true);
                            summon.noPhysics = true;

                            CompoundTag customData = new CompoundTag();
                            customData.putBoolean("big", summon.getRandom().nextInt(5) == 0);
                            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

                            target.level().addFreshEntity(summon);
                            player.getCooldowns().addCooldown(item, 5);
                        }
                    }
                }else {
                    ParticleUtil.addParticles(
                        player.level(), PARTICLE,
                        player.position(), 0.2,
                        new Vec3(0, 0, 0), 0.2,
                        5
                    );
                }
            }
        }
    };
}
