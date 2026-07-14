package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.Config;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.beam.ISwordBeamBehavior;
import com.lzxnone.terraria.entity.beam.SwordBeam;
import com.lzxnone.terraria.entity.beam.SwordBeamBehaviors;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.IItemWaveBehavior;
import com.lzxnone.terraria.network.payload.SwordBeamPayload;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.utils.*;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.List;

public class TheHorsemansBlade extends SwordItem {
    public TheHorsemansBlade() {
        super(Tiers.DIAMOND, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 14, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -2.4, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ).rarity(Rarity.RARE));
    }

    public static final CompoundTag BEAM_DATA = Util.make(new CompoundTag(), tag -> {
        tag.putString("behavior", "the_horsemans_blade");
        tag.putInt("cooldown", 10);
        tag.putFloat("color0R", 0.741f);
        tag.putFloat("color0G", 0.133f);
        tag.putFloat("color0B", 0.133f);
        tag.putFloat("color1R", 0.937f);
        tag.putFloat("color1G", 0.408f);
        tag.putFloat("color1B", 0.149f);
        tag.putFloat("color2R", 0.765f);
        tag.putFloat("color2G", 0.471f);
        tag.putFloat("color2B", 0.212f);

        CompoundTag customData = new CompoundTag();
        customData.putInt("hitEntityCount", 0);
        tag.put("customData", customData);
    });

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.075f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(1.0F, 0.8F, 0.5F),
            new Vector3f(1.0F, 0.5F, 0.0F),
            new Vector3f(1.0F, 0.9F, 0.0F)
        }
    );

    public static final ISwordBeamBehavior SWORD_BEAM_BEHAVIOR = new ISwordBeamBehavior() {
        @Override
        public void onMoving(SwordBeam beam) {
            if(beam.currentPosition == null) return;
            ParticleUtil.addParticle(
                beam.level(), PARTICLE,
                beam.currentPosition, 0.2,
                new Vec3(0, 0, 0), 0.2
            );
        }

        @Override
        public void onHitEntity(SwordBeam beam, EntityHitResult result) {
            if(!beam.level().isClientSide()) {
                Entity target = result.getEntity();
                if(beam.getOwner() instanceof Player player && FilterUtil.createTargetFilter(player).test(target)) {
                    CompoundTag custom_data = beam.getEntityData().get(SwordBeam.CUSTOM_DATA);
                    if(custom_data.contains("hitEntityCount")) {
                        int count = custom_data.getInt("hitEntityCount");
                        if(count < Config.horsemansBladeMaxHitCount) {
                            if(DamageUtil.attack(player, target, (float) Config.horsemansBladeDamage)) {
                                target.invulnerableTime = 20;
                                count++;
                                custom_data.putInt("hitEntityCount", count);
                                beam.getEntityData().set(SwordBeam.CUSTOM_DATA, custom_data);

                                StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), beam.level());
                                Entity entity = beam.getOwner();
                                if(entity == null) return;
                                summon.setOwner(entity);
                                Vec3 pos = MathUtil.getRandomPosInRadius(entity.position(), Config.horsemansPumpkinSpawnRange);
                                summon.setPos(pos);

                                float[] xyRot = MathUtil.computeXYRot(MathUtil.toVector3f(target.position().subtract(pos)));
                                summon.setXRot(xyRot[0]);
                                summon.xRotO = xyRot[0];
                                summon.setYRot(xyRot[1]);
                                summon.yRotO = xyRot[1];

                                summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.THE_HORSEMANS_BLADE_JACK);
                                summon.getEntityData().set(StaticSummon.RENDER_MODE, "block");
                                summon.getEntityData().set(StaticSummon.BLOCK, Blocks.JACK_O_LANTERN.defaultBlockState());
                                summon.getEntityData().set(StaticSummon.LIFETIME, 300);
                                summon.getEntityData().set(StaticSummon.SCALE_X, 0.5f);
                                summon.getEntityData().set(StaticSummon.SCALE_Y, 0.5f);
                                summon.getEntityData().set(StaticSummon.SCALE_Z, 0.5f);
                                summon.getEntityData().set(StaticSummon.RYP, 180);
                                summon.getEntityData().set(StaticSummon.GLOW, true);
                                summon.setNoGravity(true);
                                summon.noPhysics = true;

                                CompoundTag customData = new CompoundTag();
                                customData.putInt("target", target.getId());
                                summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

                                beam.level().addFreshEntity(summon);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void generate(Entity entity, CompoundTag beamData) {
            beamData.putInt("rotate", (int) (Config.horsemansBladeRotateRange * (Math.random() * 2 - 1)));
            //beamData.putBoolean("right", entity.getRandom().nextInt(2) == 0);
            ISwordBeamBehavior.super.generate(entity, beamData);
        }
    };

    public static final IItemWaveBehavior ITEM_WAVE_BEHAVIOR = new IItemWaveBehavior() {
        public void onLeftClickAir(PlayerInteractEvent.LeftClickEmpty event) {
            Player player = event.getEntity();
            ItemStack itemStack = player.getMainHandItem();
            if(itemStack.isEmpty()) return;
            Item item = itemStack.getItem();
            if(item instanceof TheHorsemansBlade && !player.getCooldowns().isOnCooldown(item)) {
                PacketDistributor.sendToServer(new SwordBeamPayload("the_horsemans_blade", BEAM_DATA));
                SoundUtil.playClientSound(player, ModSounds.WAVE.get());
            }
        }
        public void onAttackEntity(AttackEntityEvent event) {
            Player player = event.getEntity();
            ItemStack itemStack = player.getMainHandItem();
            if(itemStack.isEmpty()) return;
            Item item = itemStack.getItem();
            if(item instanceof TheHorsemansBlade && !player.getCooldowns().isOnCooldown(item)) {
                if(!player.level().isClientSide()) {
                    SwordBeamBehaviors.getBehavior("the_horsemans_blade").generate(player, BEAM_DATA);
                }else {
                    SoundUtil.playClientSound(player, ModSounds.WAVE.get());
                }
            }
            event.setCanceled(true);
        }
    };

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(!level.isClientSide()) {
            SwordBeamBehaviors.getBehavior("the_horsemans_blade").generate(player, BEAM_DATA);
        }else {
            SoundUtil.playClientSound(player, ModSounds.WAVE.get());
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);

            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            LivingEntity target = null;

            if(customData.contains("target")) {
                Entity entity = summon.level().getEntity(customData.getInt("target"));
                if(entity instanceof LivingEntity le && le.isAlive()) {
                    target = le;
                }
            }

            if(!summon.level().isClientSide()) {
                if(target == null) {
                    List<Monster> targets = summon.level().getEntitiesOfClass(
                        Monster.class,
                        AABB.ofSize(summon.position(), Config.horsemansPumpkinMaxTargetRange * 2, Config.horsemansPumpkinMaxTargetRange * 2, Config.horsemansPumpkinMaxTargetRange * 2),
                        FilterUtil.createMonsterFilter(summon.getOwner())
                    );
                    targets.sort(Comparator.comparingDouble(e -> e.distanceToSqr(summon.position())));
                    if(!targets.isEmpty()) target = targets.getFirst();
                    if(target != null) {
                        customData.putInt("target", target.getId());
                        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
                    }
                }
            }

            if(target != null) {
                Vec3 targetPos = target.position().add(0, target.getBbHeight() / 2.0D, 0);
                Vec3 selfPos = summon.position();
                Vec3 dir = targetPos.subtract(selfPos).normalize();

                summon.setDeltaMovement(dir.scale(Config.horsemansPumpkinSpeed));

                float[] xyRot = MathUtil.computeXYRot(MathUtil.toVector3f(dir));
                summon.setXRot(xyRot[0]);
                summon.setYRot(xyRot[1]);

                if(selfPos.distanceToSqr(targetPos) < 2.0D) {
                    if(summon.getOwner() instanceof Player player && FilterUtil.createLivingTargetFilter(player).test(target) && DamageUtil.attack(player, target, (float) Config.horsemansPumpkinDamage)) onDied(summon);
                }
            }else {
                Vec3 dir = summon.getLookAngle().normalize();
                summon.setDeltaMovement(dir.scale(Config.horsemansPumpkinSpeed));
            }

            if(!summon.level().isClientSide()) {
                Vec3 pos = summon.position();
                AABB box = new AABB(
                    pos.x - 0.25, pos.y - 0.25, pos.z - 0.25,
                    pos.x + 0.25, pos.y + 0.25, pos.z + 0.25
                );
                summon.setBoundingBox(box);

                List<Monster> hitEntities = summon.level().getEntitiesOfClass(
                    Monster.class,
                    summon.getBoundingBox().inflate(0.2D),
                    FilterUtil.createMonsterFilter(summon.getOwner())
                );

                if(!hitEntities.isEmpty()) {
                    for(Entity hitEntity : hitEntities) {
                        if(target != null && hitEntity.getUUID() == target.getUUID()) continue;
                        if(summon.getOwner() instanceof Player player) {
                            if(DamageUtil.attack(player, target, (float) Config.horsemansPumpkinDamage)) {
                                hitEntity.invulnerableTime = 2;
                            }
                        }
                    }
                }
            }else {
                ParticleUtil.addParticles(
                    summon.level(), PARTICLE,
                    summon.position(), 0.1,
                    new Vec3(0, 0, 0), 0.1,
                    2
                );
            }
        }

        @Override
        public void onDied(StaticSummon summon) {
            if(!summon.level().isClientSide()) {
                ParticleUtil.addParticles(
                    (ServerLevel) summon.level(), PARTICLE,
                    summon.position(), new Vec3(0.1, 0.1, 0.1),
                    0.1, 50
                );
                summon.discard();
            }
        }
    };
}
