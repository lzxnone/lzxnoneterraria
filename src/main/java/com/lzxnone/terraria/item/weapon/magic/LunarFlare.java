package com.lzxnone.terraria.item.weapon.magic;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.weapon.MagicWeapon;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.CollisionUtil;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.ParticleUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class LunarFlare extends MagicWeapon {
    public static final double HITBOX_INFLATE = 0.75D;
    public static final double SPAWN_HEIGHT = 20.0D;
    public static final double DELTA_HEIGHT = 10.0D;
    public static final double SPAWN_OFFSET = 10.0D;
    public static final double HIT_OFFSET = 1.5D;

    public static final ConfigFloat DAMAGE = new ConfigFloat("weapon.lunar_flare.damage", "lunar_flare_damage", 5.0F, 0.0F, 8388600.0F);
    public static final ConfigDouble RANGE = new ConfigDouble("weapon.lunar_flare.range", "lunar_flare_range", 64.0D, 0.0D, 512.0D);
    public static final ConfigDouble SPEED = new ConfigDouble("weapon.lunar_flare.speed", "lunar_flare_speed", 2.5D, 0.0D, 10.0D);
    public static final ConfigDouble EXPLOSION_RANGE = new ConfigDouble("weapon.lunar_flare.explosion_range", "lunar_flare_explosion_range", 3.0D, 0.0D, 64.0D);
    public static final ConfigDouble MANA_CONSUME = new ConfigDouble("weapon.lunar_flare.mana_consume", "lunar_flare_mana_consume", 9.0D, 0.0D, 10000.0D);

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DAMAGE, RANGE, SPEED, EXPLOSION_RANGE, MANA_CONSUME);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "lunar_flare",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/lunar_flare.png"),
        Component.translatable("item.lzxnoneterraria.lunar_flare"),
        CONFIG_DATA
    );

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.04F, 0.35F, 24, true, new Vector3f[]{
            new Vector3f(0.15F, 0.45F, 1.0F),
            new Vector3f(0.0F, 0.9F, 1.0F)
        }
    );

    public static final DustParticleOptions PARTICLE2 = new DustParticleOptions(
        0.12F, 0.5F, 44, true, new Vector3f[]{
            new Vector3f(0.05F, 0.25F, 1.0F),
            new Vector3f(0.0F, 0.8F, 1.0F),
            new Vector3f(0.45F, 1.0F, 1.0F)
        }
    );

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            checkBeforeTick(summon);

            Vec3 motion = summon.getDeltaMovement();
            if(motion.lengthSqr() < 1.0E-7D) {
                onDied(summon);
                return;
            }

            Vec3 start = summon.position();
            Vec3 nextPos = start.add(motion);

            float[] xyRot = MathUtil.computeXYRot(motion.toVector3f());
            summon.setXRot(xyRot[0]);
            summon.setYRot(xyRot[1]);

            if(summon.level().isClientSide()) {
                double length = motion.length();
                int count = Math.max(4, (int) Math.ceil(length / 0.25D));
                for(int i = 0; i <= count; i++) {
                    double ratio = i / (double) count;
                    Vec3 pos = start.add(motion.scale(ratio));
                    ParticleUtil.addParticle(
                        summon.level(), PARTICLE,
                        pos, 0.02D,
                        new Vec3(0.0D, 0.0D, 0.0D), 0.01D
                    );
                }
                return;
            }

            EntityHitResult entityHitResult = CollisionUtil.checkEntityHit(summon, nextPos, HITBOX_INFLATE);
            if(entityHitResult != null) {
                onDied(summon);
                return;
            }

            BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, nextPos);
            if(blockHitResult.getType() != HitResult.Type.MISS) {
                CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
                int targetLifetime = customData.getInt("targetLifetime");
                int age = summon.getEntityData().get(StaticSummon.AGE);
                if(age >= targetLifetime && !summon.level().getBlockState(blockHitResult.getBlockPos()).getCollisionShape(summon.level(), blockHitResult.getBlockPos()).isEmpty()) {
                    onDied(summon);
                    return;
                }
            }
        }

        @Override
        public AABB getBoundingBoxForCulling(StaticSummon summon) {
            return summon.getBoundingBox().inflate(8.0D);
        }

        @Override
        public void onDied(StaticSummon summon) {
            if(!(summon.level() instanceof ServerLevel serverLevel)) return;

            Vec3 pos = summon.position();
            ParticleUtil.addParticles(
                serverLevel, ModParticles.LUNAR_FLARE_EXPLOSION_PARTICLE.get(),
                pos, new Vec3(0.0D, 0.0D, 0.0D),
                0.0D, 1
            );
            ParticleUtil.addParticles(
                serverLevel, PARTICLE2,
                pos, new Vec3(0.7D, 0.7D, 0.7D),
                0.35D, 45
            );
            ParticleUtil.addParticles(
                serverLevel, PARTICLE,
                pos, new Vec3(0.45D, 0.45D, 0.45D),
                0.25D, 25
            );

            double explosionRange = EXPLOSION_RANGE.get();
            if(explosionRange > 0.0D) {
                ItemStack sourceStack = summon.getEntityData().get(StaticSummon.STACK_SOURCE);
                for(Entity target : serverLevel.getEntitiesOfClass(
                    Entity.class,
                    AABB.ofSize(pos, explosionRange * 2.0D, explosionRange * 2.0D, explosionRange * 2.0D),
                    FilterUtil.createTargetFilter(summon, summon.getOwner())
                )) {
                    if(target.distanceToSqr(pos) > explosionRange * explosionRange) continue;
                    if(DamageUtil.magicAttack(summon, target, sourceStack, DAMAGE.get(), 0.4F, 10)) {
                    }
                }
            }

            summon.discard();
        }
    };

    public LunarFlare() {
        super(Tiers.DIAMOND, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public float getTooltipDamage(ItemStack weaponStack, LivingEntity entity) {
        float damage = entity instanceof Player player ? DamageUtil.applyPlayerDamageEffects(player, DAMAGE.get()) : DAMAGE.get();
        return applyMagicDamageBonus(weaponStack, entity, damage);
    }

    @Override
    public int getUseTime(ItemStack weaponStack, LivingEntity entity) {
        return 10;
    }

    @Override
    protected double getManaConsumeRate(ItemStack stack, LivingEntity entity) {
        return MANA_CONSUME.get();
    }

    @Override
    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        if(level.isClientSide()) return;

        player.playNotifySound(ModSounds.MAGIC_SHOOT.get(), SoundSource.PLAYERS, 4.0F, 1.0F);

        int count = 2 + player.getRandom().nextInt(3);
        for(int i = 0; i < count; i++) {
            Vec3 target = MathUtil.getCrosshairPos(player, level, RANGE.get());

            Vec3 spawnPos = new Vec3(
                target.x + SPAWN_OFFSET * (player.getRandom().nextDouble() * 2.0D - 1.0D),
                target.y + SPAWN_HEIGHT + i * DELTA_HEIGHT,
                target.z + SPAWN_OFFSET * (player.getRandom().nextDouble() * 2.0D - 1.0D)
            );

            Vec3 hitPos = new Vec3(
                target.x + HIT_OFFSET * (player.getRandom().nextDouble() * 2.0D - 1.0D),
                target.y,
                target.z + HIT_OFFSET * (player.getRandom().nextDouble() * 2.0D - 1.0D)
            );

            Vec3 toTarget = hitPos.subtract(spawnPos);
            if(toTarget.lengthSqr() < 1.0E-7D) continue;

            StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), level);
            summon.setOwner(player);
            summon.setPos(spawnPos);
            summon.setDeltaMovement(toTarget.normalize().scale(SPEED.get()));
            summon.getEntityData().set(StaticSummon.STACK_SOURCE, stack.copy());
            summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.LUNAR_FLARE);
            summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
            summon.getEntityData().set(StaticSummon.LIFETIME, 1200);
            summon.getEntityData().set(StaticSummon.GLOW, true);
            summon.setNoGravity(true);
            summon.noPhysics = true;

            CompoundTag customData = new CompoundTag();
            customData.putInt("targetLifetime", (int) Math.round(target.distanceTo(spawnPos) / SPEED.get()));
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

            float[] xyRot = MathUtil.computeXYRot(summon.getDeltaMovement().toVector3f());
            summon.setXRot(xyRot[0]);
            summon.setYRot(xyRot[1]);
            summon.xRotO = xyRot[0];
            summon.yRotO = xyRot[1];

            level.addFreshEntity(summon);
        }
    }
}
