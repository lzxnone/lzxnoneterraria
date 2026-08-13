package com.lzxnone.terraria.item.ammo;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.particle.DustParticleOptions;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.joml.Vector3f;

import java.util.List;

public class ExplodingBullet extends BulletAmmo {
    public static final String ID = "exploding_bullet";

    public static final ConfigFloat BASE_DAMAGE = new ConfigFloat("ammo.exploding_bullet.base_damage", "exploding_bullet_base_damage", 1.5f, 0.0f, 8388600.0f);
    public static final ConfigDouble SPEED = new ConfigDouble("ammo.exploding_bullet.speed", "exploding_bullet_speed", 3.0D, 0.0D, 24.0D);
    public static final ConfigFloat KNOCKBACK_MULTIPLIER = new ConfigFloat("ammo.exploding_bullet.knockback_multiplier", "exploding_bullet_knockback_multiplier", 2.0f, 0.0f, 100.0f);
    public static final ConfigInt DUST_COUNT = new ConfigInt("ammo.exploding_bullet.dust_count", "exploding_bullet_dust_count", 10, 0, 1000);

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(BASE_DAMAGE, SPEED, KNOCKBACK_MULTIPLIER, DUST_COUNT);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        ID,
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/" + ID + ".png"),
        Component.translatable("item.lzxnoneterraria." + ID),
        CONFIG_DATA
    );

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.075f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(1.0F, 0.8F, 0.5F),
            new Vector3f(1.0F, 0.5F, 0.0F),
            new Vector3f(1.0F, 0.9F, 0.0F),
        }
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
                boolean dead = false;
                for(Entity target : targets) {
                    if(DamageUtil.rangedAttack(summon, target, sourceStack, BASE_DAMAGE.get() + damage, knockbackScale * KNOCKBACK_MULTIPLIER.get())) {
                        dead = true;
                        target.invulnerableTime = invulnerableTime;
                        SoundUtil.playServerSound(summon.level(), ModSounds.BOOM.get(), summon.position());

                    }
                }
                if(dead) {
                    summon.setPos(end);
                    ParticleUtil.addParticles(
                        (ServerLevel) summon.level(), ModParticles.EXPLODE_PARTICLE.get(),
                        summon.position(), new Vec3(0, 0, 0),
                        0.2, 1
                    );
                    ParticleUtil.addParticles(
                        (ServerLevel) summon.level(), PARTICLE,
                        summon.position(), new Vec3(0, 0, 0),
                        0.2, DUST_COUNT.get()
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
                SoundUtil.playServerSound(summon.level(), ModSounds.BOOM.get(), summon.position());
                ParticleUtil.addParticles(
                    (ServerLevel) summon.level(), ModParticles.EXPLODE_PARTICLE.get(),
                    summon.position(), new Vec3(0, 0, 0),
                    0.2, 1
                );
                ParticleUtil.addParticles(
                    (ServerLevel) summon.level(), PARTICLE,
                    summon.position(), new Vec3(0, 0, 0),
                    0.2, DUST_COUNT.get()
                );
                this.onDied(summon);
                return;
            }

            summon.setPos(end);
        }
    };
    @Override
    public float getBaseDamage(ItemStack stack) {
        return BASE_DAMAGE.get();
    }

}
