package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.item.weapon.MeleeWeapon;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3f;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.item.ModItems;

import java.util.List;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jspecify.annotations.NonNull;

public class TheHorsemansBlade extends MeleeWeapon {
    public static final String ID = "the_horsemans_blade";
    public static final ConfigFloat BASE_MELEE_DAMAGE = createBaseMeleeDamageConfig(ID, 14F);
    public static final ConfigFloat BASE_MELEE_ATTACK_SPEED = createBaseMeleeAttackSpeedConfig(ID, -2.4F);
    public static final ConfigInt BLADE_ROTATE_RANGE = new ConfigInt(
        "weapon.the_horsemans_blade.blade_rotate_range",
        "horsemans_blade_rotate_range",
        45,
        0,
        90
    );
    public static final ConfigInt BLADE_MAX_HIT_COUNT = new ConfigInt(
        "weapon.the_horsemans_blade.blade_max_hit_count",
        "horsemans_blade_max_hit_count",
        3,
        0,
        100
    );
    public static final ConfigFloat BLADE_DAMAGE = new ConfigFloat(
        "weapon.the_horsemans_blade.blade_damage",
        "horsemans_blade_damage",
        15.0f,
        0.0f,
        8388600.0f
    );
    public static final ConfigDouble PUMPKIN_SPAWN_RANGE = new ConfigDouble(
        "weapon.the_horsemans_blade.pumpkin_spawn_range",
        "horsemans_pumpkin_spawn_range",
        16.0,
        1.0,
        64.0
    );
    public static final ConfigDouble PUMPKIN_SPEED = new ConfigDouble(
        "weapon.the_horsemans_blade.pumpkin_speed",
        "horsemans_pumpkin_speed",
        0.5,
        0.0,
        10.0
    );
    public static final ConfigFloat PUMPKIN_DAMAGE = new ConfigFloat(
        "weapon.the_horsemans_blade.pumpkin_damage",
        "horsemans_pumpkin_damage",
        15.0f,
        0.0f,
        8388600.0f
    );
    public static final ConfigDouble PUMPKIN_MAX_TARGET_RANGE = new ConfigDouble(
        "weapon.the_horsemans_blade.pumpkin_max_target_range",
        "horsemans_pumpkin_max_target_range",
        32.0,
        1.0,
        64.0
    );
    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                BASE_MELEE_DAMAGE,
                BASE_MELEE_ATTACK_SPEED,
                BLADE_ROTATE_RANGE,
                BLADE_MAX_HIT_COUNT,
                BLADE_DAMAGE,
                PUMPKIN_SPAWN_RANGE,
                PUMPKIN_SPEED,
                PUMPKIN_DAMAGE,
                PUMPKIN_MAX_TARGET_RANGE
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);

    public TheHorsemansBlade() {
        super(Tiers.DIAMOND, new Item.Properties().rarity(Rarity.RARE));
    }

    @Override
    public float getTooltipDamage(ItemStack weaponStack, LivingEntity entity) {
        float damage = BLADE_DAMAGE.get();
        //锋利附魔
        int sharpnessLevel = getEnchantmentLevel(entity, weaponStack, Enchantments.SHARPNESS);
        if(sharpnessLevel > 0) {
            damage += 1.0F + Math.max(0, sharpnessLevel - 1) * 0.5F;
        }
        //药水
        if(entity instanceof Player player) damage = DamageUtil.applyPlayerDamageEffects(player, damage);
        //近战加成
        return MeleeWeapon.applyMeleeDamageBonus(weaponStack, entity, damage);
    }

    @Override
    protected float getBaseMeleeDamage(ItemStack stack) {
        return BASE_MELEE_DAMAGE.get();
    }

    @Override
    protected float getBaseMeleeAttackSpeed(ItemStack stack) {
        return BASE_MELEE_ATTACK_SPEED.get();
    }

    public static final CompoundTag BEAM_DATA = Util.make(new CompoundTag(), tag -> {
        tag.putString("behavior", "the_horsemans_blade");
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
                        if(count < BLADE_MAX_HIT_COUNT.get()) {
                            if(DamageUtil.meleeAttack(beam, target, beam.getEntityData().get(SwordBeam.STACK_SOURCE), (float) BLADE_DAMAGE.get(), 1.0f, 10)) {
                                count++;
                                custom_data.putInt("hitEntityCount", count);
                                beam.getEntityData().set(SwordBeam.CUSTOM_DATA, custom_data);

                                StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), beam.level());
                                Entity entity = beam.getOwner();
                                if(entity == null) return;
                                summon.setOwner(entity);
                                summon.getEntityData().set(StaticSummon.STACK_SOURCE, beam.getEntityData().get(SwordBeam.STACK_SOURCE).copy());
                                Vec3 pos = MathUtil.getRandomPosInRadius(entity.position(), PUMPKIN_SPAWN_RANGE.get());
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
            beamData.putInt("rotate", (int) (BLADE_ROTATE_RANGE.get() * (Math.random() * 2 - 1)));
            beamData.putInt("lifetime", Math.max(1, getUseTime() / 3));
            ISwordBeamBehavior.super.generate(entity, beamData);
            if(entity instanceof Player player) {
                player.getCooldowns().addCooldown(ModItems.THE_HORSEMANS_BLADE.get(), Math.max(1, getUseTime() / 3));
            }
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
        if(player.getCooldowns().isOnCooldown(stack.getItem()) || !tryShoot(level, player, hand, stack)) return InteractionResultHolder.fail(stack);
        player.startUsingItem(hand);
        player.getCooldowns().addCooldown(stack.getItem(), Math.max(1, getUseTime() / 3));
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int count) {
        if(!(livingEntity instanceof Player player)) return;
        if(!shouldShootThisTick(stack, livingEntity, count)) return;

        if(!tryShoot(level, player, player.getUsedItemHand(), stack)) {
            player.stopUsingItem();
        }
    }

    public boolean shouldShootThisTick(ItemStack weaponStack, LivingEntity entity, int remainingUseTicks) {
        int useTime = Math.max(1, getUseTime());
        int elapsedMinecraftTicks = getUseDuration(weaponStack, entity) - remainingUseTicks;
        if(elapsedMinecraftTicks <= 0) return false;

        int currentShot = elapsedMinecraftTicks * 3 / useTime;
        int previousShot = (elapsedMinecraftTicks - 1) * 3 / useTime;
        return currentShot > previousShot;
    }

    public boolean tryShoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        shoot(level, player, hand, stack);
        return true;
    }

    public void shoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        SoundUtil.playClientSound(player, ModSounds.WAVE.get());
        int randomAngle = (int) (BLADE_ROTATE_RANGE.get() * (Math.random() * 2 - 1));
        CompoundTag beamData = BEAM_DATA.copy();
        beamData.putInt("rotate", randomAngle);
        beamData.putInt("lifetime", Math.max(1, getUseTime() / 3));
        SwordBeamBehaviors.getBehavior(SwordBeamBehaviors.DEFAULT).generate(player, beamData);
    }

    public static int getUseTime() {
        return 26;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public @NonNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);

            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            Entity target = null;

            if(customData.contains("target")) {
                Entity entity = summon.level().getEntity(customData.getInt("target"));
                if(entity != null && entity.isAlive()) {
                    target = entity;
                }
            }

            if(!summon.level().isClientSide()) {
                if(target == null) {
                    List<Entity> targets = SearchUtil.searchNearestEnemies(summon, summon.getOwner(), summon.getBoundingBox().inflate(PUMPKIN_MAX_TARGET_RANGE.get()), 1);
                    target = targets.isEmpty() ? null : targets.getFirst();
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

                summon.setDeltaMovement(dir.scale(PUMPKIN_SPEED.get()));

                float[] xyRot = MathUtil.computeXYRot(MathUtil.toVector3f(dir));
                summon.setXRot(xyRot[0]);
                summon.setYRot(xyRot[1]);

                if(selfPos.distanceToSqr(targetPos) < 2.0D) {
                    if(summon.getOwner() instanceof Player player && FilterUtil.createTargetFilter(player).test(target) && DamageUtil.meleeAttack(summon, target, summon.getEntityData().get(StaticSummon.STACK_SOURCE), (float) PUMPKIN_DAMAGE.get(), 1.0f, 10)) onDied(summon);
                }
            }else {
                Vec3 dir = summon.getLookAngle().normalize();
                summon.setDeltaMovement(dir.scale(PUMPKIN_SPEED.get()));
            }

            if(!summon.level().isClientSide()) {
                Vec3 pos = summon.position();
                AABB box = new AABB(
                    pos.x - 0.25, pos.y - 0.25, pos.z - 0.25,
                    pos.x + 0.25, pos.y + 0.25, pos.z + 0.25
                );
                summon.setBoundingBox(box);

                List<Entity> hitEntities = SearchUtil.searchEnemies(summon, summon.getOwner(), summon.getBoundingBox().inflate(0.2D));

                if(!hitEntities.isEmpty()) {
                    for(Entity hitEntity : hitEntities) {
                        if(target != null && hitEntity.getUUID() == target.getUUID()) continue;
                        if(summon.getOwner() instanceof Player player) {
                            if(DamageUtil.meleeAttack(summon, hitEntity, summon.getEntityData().get(StaticSummon.STACK_SOURCE), (float) PUMPKIN_DAMAGE.get(), 1.0f, 10)) {
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
