package com.lzxnone.terraria.item.ammo;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
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

public class LuminiteBullet extends BulletAmmo {
    public static final String ID = "luminite_bullet";

    public static final ConfigFloat BASE_DAMAGE = new ConfigFloat("ammo.luminite_bullet.base_damage", "luminite_bullet_base_damage", 3.0f, 0.0f, 8388600.0f);
    public static final ConfigDouble SPEED = new ConfigDouble("ammo.luminite_bullet.speed", "luminite_bullet_speed", 3.5D, 0.0D, 24.0D);
    public static final ConfigFloat DAMAGE_DECAY = new ConfigFloat("ammo.luminite_bullet.damage_decay", "luminite_bullet_damage_decay", 0.96f, 0.0f, 1.0f);

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(BASE_DAMAGE, SPEED, DAMAGE_DECAY);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        ID,
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/" + ID + ".png"),
        Component.translatable("item.lzxnoneterraria." + ID),
        CONFIG_DATA
    );

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.025f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(0.0F, 1.0F, 1.0F),
            new Vector3f(0.1F, 0.4F, 1.0F)
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
                int invulnerableTime = customData.contains("invulnerableTime") ? customData.getInt("invulnerableTime") : 10;

                ItemStack sourceStack = summon.getEntityData().get(StaticSummon.STACK_SOURCE);

                for(Entity target : targets) {
                    int hit = customData.getInt("hit");
                    if(DamageUtil.rangedAttack(summon, target, sourceStack, (BASE_DAMAGE.get() + damage) * (float) Math.pow(DAMAGE_DECAY.get(), hit), knockbackScale, invulnerableTime)) {
                        hit++;
                        customData.putInt("hit", hit);
                        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
                        ParticleUtil.addParticles(
                            (ServerLevel) summon.level(), PARTICLE,
                            end, new Vec3(0, 0, 0),
                            0.2, 5
                        );
                    }
                }
            }

            //方块检测
            BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, end);
            if(blockHitResult.getType() != HitResult.Type.MISS) {
                if(summon.level() instanceof ServerLevel serverLevel) Ammo.playBlockHitEffects(serverLevel, blockHitResult);
                summon.setPos(blockHitResult.getLocation());
                ParticleUtil.addParticles(
                    (ServerLevel) summon.level(), PARTICLE,
                    summon.position(), new Vec3(0, 0, 0),
                    0.2, 10
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
