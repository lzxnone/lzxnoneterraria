package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.Config;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.IStaticProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.utils.CollisionUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.ParticleUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class InfluxWaver extends SwordItem {
    public InfluxWaver() {
        super(Tiers.DIAMOND, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 11, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -2.4, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ));
    }

    public static final float FADE_OUT = 0.1f;
    public static final float FRICTION = 0.1f;

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);

            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            if(!customData.contains("idx")) return;
            int idx = customData.getInt("idx");

            boolean checkBlock = true;
            boolean checkEntity = true;

            //粒子
            if(summon.level().isClientSide()) {
                ParticleUtil.addParticle(
                    summon.level(), ModParticles.INFLUX_WAVER_DROP_PARTICLE.get(),
                    summon.position(), 0.0,
                    new Vec3(0, idx == 0 ? -0.1 : 0, 0), 0.00
                );
            }

            //运动逻辑
            if(customData.contains("dead")) {
                if(customData.getBoolean("dead")) {
                    Vec3 currentMovement = summon.getDeltaMovement();
                    double speed = currentMovement.length();

                    if(speed <= FRICTION) {
                        summon.setDeltaMovement(Vec3.ZERO);
                    }else {
                        Vec3 newMovement = currentMovement.scale((speed - FRICTION) / speed);
                        summon.setDeltaMovement(newMovement);
                    }
                }
                float alpha = summon.getEntityData().get(StaticSummon.COLOR_A) - FADE_OUT;
                summon.getEntityData().set(StaticSummon.COLOR_A, alpha);
                if(alpha < 0.001f) {
                    this.onDied(summon);
                    return;
                }
                checkBlock = false;
                checkEntity = false;
            }else {
                if(idx == 0) {
                    summon.setDeltaMovement(summon.getLookAngle().normalize().scale(Config.influxWaverSpeed));
                }else {
                    checkBlock = false;
                    if(customData.contains("target")) {
                        Entity target = summon.level().getEntity(customData.getInt("target"));
                        if(target == null || !target.isAlive()) {
                            summon.setDeltaMovement(summon.getLookAngle().normalize().scale(Config.influxWaverSpeed));
                            checkEntity = false;
                        }else {
                            Vec3 targetPos = new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ());
                            summon.setDeltaMovement(targetPos.subtract(summon.position()).normalize().scale(Config.influxWaverSpeed));
                        }
                    }else {
                        summon.setDeltaMovement(summon.getLookAngle().normalize().scale(Config.influxWaverSpeed));
                        checkEntity = false;
                    }
                }
            }

            if(idx > 0) {
                float[] xyRot = MathUtil.computeXYRot(MathUtil.toVector3f(summon.getDeltaMovement()));
                summon.setXRot(xyRot[0]);
                summon.xRotO = xyRot[0];
                summon.setYRot(xyRot[1]);
                summon.yRotO = xyRot[1];
            }

            //碰撞逻辑
            if(!summon.level().isClientSide()) {
                Vec3 motion = summon.getDeltaMovement();
                if(checkBlock) {
                    BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, summon.position().add(motion));
                    if(blockHitResult.getType() != HitResult.Type.MISS) {
                        customData.putBoolean("dead", true);
                        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
                        return;
                    }
                }
                if(checkEntity) {
                    EntityHitResult entityHitResult = CollisionUtil.checkEntityHit(summon, summon.position().add(motion));
                    if(entityHitResult != null) {
                        Entity hitTarget = entityHitResult.getEntity();
                        Entity target = customData.contains("target") ? summon.level().getEntity(customData.getInt("target")) : null;
                        if(target != null && !hitTarget.getUUID().equals(target.getUUID())) return;
                        if(hitTarget instanceof LivingEntity livingEntity && summon.getOwner() instanceof Player player && FilterUtil.createLivingTargetFilter(player).test(livingEntity)) {
                            if(livingEntity.hurt(summon.damageSources().playerAttack(player), (float) Config.influxWaverDamage)) {
                                livingEntity.invulnerableTime = 2;
                            }
                            customData.putBoolean("dead", false);
                            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
                            return;
                        }
                    }
                }
            }
        }

        @Override
        public void onDied(StaticSummon summon) {
            if(!summon.level().isClientSide()) {
                CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
                if(customData.contains("dead") && !customData.getBoolean("dead") && customData.contains("idx") && customData.getInt("idx") < Config.influxWaverMaxCount) {
                    if(summon.getOwner() instanceof Player player) {
                        Entity target = InfluxWaver.search(player, summon.position());
                        if(target != null) {
                            Vec3 targetPos = new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ());
                            Vec3 summonPos = MathUtil.getRandomPosOnRadius(targetPos, Config.influxWaverSpawnRange);
                            InfluxWaver.summon(player, summonPos, targetPos.subtract(summonPos).normalize(), target, customData.getInt("idx") + 1);
                        }
                    }
                }
                summon.discard();
            }
        }
    };

    public static void summon(Player player, Vec3 pos, Vec3 dir, Entity target, int idx) {
        if(player == null) return;
        StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), player.level());
        summon.setOwner(player);
        summon.setPos(pos);

        Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
        float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);
        summon.setXRot(xyRot[0]);
        summon.xRotO = xyRot[0];
        summon.setYRot(xyRot[1]);
        summon.yRotO = xyRot[1];

        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.INFLUX_WAVER_BEAM);
        summon.getEntityData().set(StaticSummon.RENDER_MODE, "item");
        summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.INFLUX_WAVER_BEAM.get()));
        summon.getEntityData().set(StaticSummon.SCALE_X, 1.25f);
        summon.getEntityData().set(StaticSummon.SCALE_Y, 1.25f);
        summon.getEntityData().set(StaticSummon.RXP, -90);
        summon.getEntityData().set(StaticSummon.RZP, -135);
        summon.getEntityData().set(StaticSummon.GLOW, true);
        summon.getEntityData().set(StaticSummon.LIFETIME, 300);
        summon.setNoGravity(true);
        summon.noPhysics = true;

        CompoundTag customData = new CompoundTag();
        if(target != null) customData.putInt("target", target.getId());
        customData.putInt("idx", idx);
        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

        player.level().addFreshEntity(summon);
    }

    public static Entity search(Player player, Vec3 pos) {
        AABB searchBox = new AABB(
            pos.x - Config.influxWaverTargetRange, pos.y - Config.influxWaverTargetRange, pos.z - Config.influxWaverTargetRange,
            pos.x + Config.influxWaverTargetRange, pos.y + Config.influxWaverTargetRange, pos.z + Config.influxWaverTargetRange
        );
        List<Monster> entities = player.level().getEntitiesOfClass(
            Monster.class,
            searchBox,
            FilterUtil.createMonsterFilter(player)
        );
        Monster target = null;
        entities.sort(Comparator.comparingDouble(e -> e.distanceToSqr(pos)));
        if(!entities.isEmpty()) target = entities.getFirst();
        return target;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if(!level.isClientSide()) {
            summon(player, new Vec3(player.getX(), player.getEyeY() - 0.1, player.getZ()), player.getLookAngle(), null, 0);
        }else {
            ParticleUtil.addParticles(
                level, ModParticles.INFLUX_WAVER_DROP_PARTICLE.get(),
                player.position(), 1.0,
                new Vec3(0, 0, 0), 0,
                25
            );
        }

        player.getCooldowns().addCooldown(stack.getItem(), 8);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
