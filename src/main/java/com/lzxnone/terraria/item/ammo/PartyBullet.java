package com.lzxnone.terraria.item.ammo;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.particle.ModParticles;
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

import java.util.List;

public class PartyBullet extends BulletAmmo {
    public static final String ID = "party_bullet";

    public static final ConfigFloat BASE_DAMAGE = new ConfigFloat("ammo.party_bullet.base_damage", "party_bullet_base_damage", 1.5f, 0.0f, 8388600.0f);
    public static final ConfigDouble SPEED = new ConfigDouble("ammo.party_bullet.speed", "party_bullet_speed", 3.0D, 0.0D, 24.0D);

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(BASE_DAMAGE, SPEED);
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
                if(DamageUtil.rangedAttack(summon, target, sourceStack, BASE_DAMAGE.get() + damage, knockbackScale, invulnerableTime)) {
                    summon.setPos(end);
                    ParticleUtil.addParticles(
                        (ServerLevel) summon.level(), ModParticles.PARTY_PARTICLE.get(),
                        summon.position(), new Vec3(0, 0, 0),
                        0.2, 5
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
                ParticleUtil.addParticles(
                    (ServerLevel) summon.level(), ModParticles.PARTY_PARTICLE.get(),
                    summon.position(), new Vec3(0, 0, 0),
                    0.2, 5
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
