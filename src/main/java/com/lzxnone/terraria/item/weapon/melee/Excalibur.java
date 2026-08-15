package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.item.weapon.MeleeWeapon;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.beam.ISwordBeamBehavior;
import com.lzxnone.terraria.entity.beam.SwordBeam;
import com.lzxnone.terraria.entity.beam.SwordBeamBehaviors;
import com.lzxnone.terraria.item.IItemWaveBehavior;
import com.lzxnone.terraria.network.payload.SwordBeamPayload;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.ParticleUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3f;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.item.ModItems;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jspecify.annotations.NonNull;

public class Excalibur extends MeleeWeapon {
    public static final String ID = "excalibur";
    public static final ConfigFloat BASE_MELEE_DAMAGE = createBaseMeleeDamageConfig(ID, 7.0F);
    public static final ConfigFloat BASE_MELEE_ATTACK_SPEED = createBaseMeleeAttackSpeedConfig(ID, -1.0F);
    public static final ConfigInt ROTATE_RANGE = new ConfigInt(
        "weapon.excalibur.rotate_range",
        "excalibur_rotate_range",
        45,
        0,
        90
    );
    public static final ConfigInt MAX_HIT_COUNT = new ConfigInt(
        "weapon.excalibur.max_hit_count",
        "excalibur_max_hit_count",
        3,
        0,
        100
    );
    public static final ConfigFloat DAMAGE = new ConfigFloat(
        "weapon.excalibur.damage",
        "excalibur_damage",
        8.0f,
        0.0f,
        8388600.0f
    );
    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                BASE_MELEE_DAMAGE,
                BASE_MELEE_ATTACK_SPEED,
                ROTATE_RANGE,
                MAX_HIT_COUNT,
                DAMAGE
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);

    public Excalibur() {
        super(Tiers.IRON, new Item.Properties().rarity(Rarity.UNCOMMON));
    }

    @Override
    public float getTooltipDamage(ItemStack weaponStack, LivingEntity entity) {
        float damage = DAMAGE.get();
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
        tag.putString("behavior", "excalibur");
        tag.putFloat("color0R", 0.745f);
        tag.putFloat("color0G", 0.620f);
        tag.putFloat("color0B", 0.243f);
        tag.putFloat("color1R", 0.949f);
        tag.putFloat("color1G", 0.863f);
        tag.putFloat("color1B", 0.431f);
        tag.putFloat("color2R", 0.898f);
        tag.putFloat("color2G", 0.725f);
        tag.putFloat("color2B", 0.484f);

        CompoundTag customData = new CompoundTag();
        customData.putInt("hitEntityCount", 0);
        tag.put("customData", customData);
    });

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.075f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(1.0F, 1.0F, 1.0F),
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
                        if(count < MAX_HIT_COUNT.get()) {
                            if(DamageUtil.meleeAttack(beam, target, beam.getEntityData().get(SwordBeam.STACK_SOURCE), (float) DAMAGE.get(), 1.0f, 10)) {
                                count++;
                                custom_data.putInt("hitEntityCount", count);
                                beam.getEntityData().set(SwordBeam.CUSTOM_DATA, custom_data);
                                ParticleUtil.addParticles(
                                    (ServerLevel) target.level(), ModParticles.EXCALIBUR_HIT_PARTICLE.get(),
                                    new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2.0, target.getZ()), new Vec3(0, 0, 0),
                                    0, 1
                                );
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void generate(Entity entity, CompoundTag beamData) {
            beamData.putInt("rotate", (int) (ROTATE_RANGE.get() * (Math.random() * 2 - 1)));
            beamData.putInt("lifetime", Math.max(1, getUseTime() / 3));
            ISwordBeamBehavior.super.generate(entity, beamData);
            if(entity instanceof Player player) {
                player.getCooldowns().addCooldown(ModItems.EXCALIBUR.get(), Math.max(1, getUseTime() / 3));
            }
        }
    };

    public static final IItemWaveBehavior ITEM_WAVE_BEHAVIOR = new IItemWaveBehavior() {
        public void onLeftClickAir(PlayerInteractEvent.LeftClickEmpty event) {
            Player player = event.getEntity();
            ItemStack itemStack = player.getMainHandItem();
            if(itemStack.isEmpty()) return;
            Item item = itemStack.getItem();
            if(item instanceof Excalibur && !player.getCooldowns().isOnCooldown(item)) {
                PacketDistributor.sendToServer(new SwordBeamPayload("excalibur", BEAM_DATA));
                SoundUtil.playClientSound(player, ModSounds.WAVE.get());
            }
        }
        public void onAttackEntity(AttackEntityEvent event) {
            Player player = event.getEntity();
            ItemStack itemStack = player.getMainHandItem();
            if(itemStack.isEmpty()) return;
            Item item = itemStack.getItem();
            if(item instanceof Excalibur && !player.getCooldowns().isOnCooldown(item)) {
                if(!player.level().isClientSide()) {
                    SwordBeamBehaviors.getBehavior("excalibur").generate(player, BEAM_DATA);
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
        int randomAngle = (int) (ROTATE_RANGE.get() * (Math.random() * 2 - 1));
        CompoundTag beamData = BEAM_DATA.copy();
        beamData.putInt("rotate", randomAngle);
        beamData.putInt("lifetime", Math.max(1, getUseTime() / 3));
        SwordBeamBehaviors.getBehavior(SwordBeamBehaviors.DEFAULT).generate(player, beamData);
    }

    public static int getUseTime() {
        return 20;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public @NonNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }
}

