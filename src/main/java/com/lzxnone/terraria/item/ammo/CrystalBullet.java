package com.lzxnone.terraria.item.ammo;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.IStaticProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.*;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.*;

import org.joml.Vector3f;

import java.util.List;

public class CrystalBullet extends BulletAmmo {
    public static final String ID = "crystal_bullet";

    public static final ConfigFloat BASE_DAMAGE = new ConfigFloat("ammo.crystal_bullet.base_damage", "crystal_bullet_base_damage", 1.2f, 0.0f, 8388600.0f);
    public static final ConfigDouble SPEED = new ConfigDouble("ammo.crystal_bullet.speed", "crystal_bullet_speed", 3.0D, 0.0D, 24.0D);
    public static final ConfigFloat FRAGMENT_DAMAGE_MULTIPLIER = new ConfigFloat("ammo.crystal_bullet.fragment_damage_multiplier", "crystal_bullet_fragment_damage_multiplier", 0.5f, 0.0f, 100.0f);
    public static final ConfigDouble FRAGMENT_SPEED = new ConfigDouble("ammo.crystal_bullet.fragment_speed", "crystal_bullet_fragment_speed", 1.0, 0.0D, 24.0D);
    public static final ConfigInt FRAGMENT_TIME = new ConfigInt("ammo.crystal_bullet.fragment_time", "crystal_bullet_fragment_time", 60, 0, 72000);
    public static final ConfigInt FRAGMENT_COUNT = new ConfigInt("ammo.crystal_bullet.fragment_count", "crystal_bullet_fragment_count", 3, 0, 100);
    public static final ConfigInt PARTICLE_COUNT = new ConfigInt("ammo.crystal_bullet.particle_count", "crystal_bullet_particle_count", 5, 0, 1000);

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(BASE_DAMAGE, SPEED, FRAGMENT_DAMAGE_MULTIPLIER, FRAGMENT_SPEED, FRAGMENT_TIME, FRAGMENT_COUNT, PARTICLE_COUNT);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        ID,
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/" + ID + ".png"),
        Component.translatable("item.lzxnoneterraria." + ID),
        CONFIG_DATA
    );

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            Vec3 motion = summon.getLookAngle().normalize().scale(SPEED.get());
            Vec3 start = summon.position();
            Vec3 end = start.add(motion);

            if(summon.level().isClientSide()) {
                summon.setPos(end);
                return;
            }

            //碰撞检测
            AABB hitBox = new AABB(start, end).inflate(0.25);
            List<Entity> targets = summon.level().getEntitiesOfClass(
                Entity.class,
                hitBox,
                FilterUtil.createTargetFilter(summon, summon.getOwner())
            );
            if(!targets.isEmpty()) {
                CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
                float damage = customData.contains("damage") ? customData.getFloat("damage") : 0;
                float knockbackScale = customData.contains("knockbackScale") ? customData.getFloat("knockbackScale") : 1.0f;
                int invulnerableTime = customData.contains("invulnerableTime") ? customData.getInt("invulnerableTime") : 20;

                ItemStack sourceStack = summon.getEntityData().get(StaticSummon.STACK_SOURCE);
                Entity target = targets.getFirst();
                if(DamageUtil.rangedAttack(summon, target, sourceStack, BASE_DAMAGE.get() + damage, knockbackScale)) {
                    target.invulnerableTime = invulnerableTime;
                    summon.setPos(end);
                    for(int i = 0;i < FRAGMENT_COUNT.get();i++) {
                        summonProjectile(summon, (BASE_DAMAGE.get() + damage) * FRAGMENT_DAMAGE_MULTIPLIER.get());
                    }
                    ParticleUtil.addParticles(
                        (ServerLevel) summon.level(), ModParticles.CRYSTAL_FRAGMENT_PARTICLE.get(),
                        summon.position(), new Vec3(0.2, 0.2, 0.2),
                        0.2, PARTICLE_COUNT.get()
                    );
                    this.onDied(summon);
                    return;
                }
            }

            //方块检测
            BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, end);
            if(blockHitResult.getType() != HitResult.Type.MISS) {
                if(summon.level() instanceof ServerLevel serverLevel) Ammo.playBlockHitEffects(serverLevel, blockHitResult);
                summon.setPos(blockHitResult.getLocation());
                CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
                float damage = customData.contains("damage") ? customData.getFloat("damage") : 0;
                for(int i = 0;i < FRAGMENT_COUNT.get();i++) {
                    summonProjectile(summon, (BASE_DAMAGE.get() + damage) * FRAGMENT_DAMAGE_MULTIPLIER.get());
                }
                ParticleUtil.addParticles(
                    (ServerLevel) summon.level(), ModParticles.CRYSTAL_FRAGMENT_PARTICLE.get(),
                    summon.position(), new Vec3(0.2, 0.2, 0.2),
                    0.2, PARTICLE_COUNT.get()
                    );
                this.onDied(summon);
                return;
            }

            summon.setPos(end);
        }
    };

    public static final IStaticProjectileBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileBehavior() {
        @Override
        public void onHitEntity(StaticProjectile projectile, EntityHitResult result) {
            if(!projectile.level().isClientSide()) {
                Entity target = result.getEntity();
                Entity owner = projectile.getOwner();
                if(owner == null) return;
                if(!FilterUtil.createTargetFilter(owner).test(target) || !(owner instanceof Player player)) return;
                CompoundTag customData = projectile.getEntityData().get(StaticProjectile.CUSTOM_DATA);
                float damage = customData.contains("damage") ? customData.getFloat("damage") : 0;
                if(DamageUtil.rangedAttack(projectile, target, projectile.getEntityData().get(StaticProjectile.STACK_SOURCE), damage, 1.0f)) {
                    target.invulnerableTime = 5;
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

    public static void summonProjectile(StaticSummon summon, float damage) {
        if(summon.level().isClientSide()) return;
        if(summon.getOwner() == null) return;
        Vec3 targetPos = MathUtil.getRandomPosOnRadius(summon.position(), 1);
        Vector3f[] dirs = MathUtil.computeCoordinateSystem(targetPos.subtract(summon.position()).toVector3f(), 0);

        StaticProjectile projectile = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), summon.level());
        projectile.setOwner(summon.getOwner());
        projectile.getEntityData().set(StaticProjectile.STACK_SOURCE, summon.getEntityData().get(StaticSummon.STACK_SOURCE).copy());
        projectile.setPos(summon.position());
        projectile.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.CRYSTAL_FRAGMENT);
        projectile.getEntityData().set(StaticProjectile.RENDER_MODE, "item");
        projectile.getEntityData().set(StaticProjectile.ORIGIN, summon.position().toVector3f());
        projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
        projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
        projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);
        projectile.getEntityData().set(StaticProjectile.ITEM, new ItemStack(ModItems.CRYSTAL_FRAGMENT.get()));
        projectile.getEntityData().set(StaticProjectile.SCALE_X, 0.5f);
        projectile.getEntityData().set(StaticProjectile.SCALE_Y, 0.5f);
        projectile.getEntityData().set(StaticProjectile.SCALE_Z, 0.5f);
        projectile.getEntityData().set(StaticProjectile.RXPS, 10);
        projectile.getEntityData().set(StaticProjectile.LIFETIME, FRAGMENT_TIME.get());
        projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*t", FRAGMENT_SPEED.get()));

        CompoundTag customData = new CompoundTag();
        customData.putFloat("damage", damage);
        projectile.getEntityData().set(StaticProjectile.CUSTOM_DATA, customData);

        projectile.setDeltaMovement(MathUtil.toVec3(dirs[0]));
        summon.level().addFreshEntity(projectile);
    }
}
