package com.lzxnone.terraria.item.weapon.magic;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.weapon.MagicWeapon;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.CollisionUtil;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.SearchUtil;
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
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class NebulaBlaze extends MagicWeapon {
    public static final Vector3f OFFSET = new Vector3f(-0.25F, -0.1F, 1.2F);
    public static final double HITBOX_INFLATE = 0.35D;
    public static final String TARGET_UUID_KEY = "target";

    public static final ConfigFloat DAMAGE = new ConfigFloat("weapon.nebula_blaze.damage", "nebula_blaze_damage", 10.0F, 0.0F, 8388600.0F);
    public static final ConfigFloat EXPLOSIVE_DAMAGE = new ConfigFloat("weapon.nebula_blaze.explosive_damage", "nebula_blaze_explosive_damage", 30.0F, 0.0F, 8388600.0F);
    public static final ConfigDouble SPEED = new ConfigDouble("weapon.nebula_blaze.speed", "nebula_blaze_speed", 1.0D, 0.0D, 24.0D);
    public static final ConfigDouble EXPLOSIVE_SPEED = new ConfigDouble("weapon.nebula_blaze.explosive_speed", "nebula_blaze_explosive_speed", 2.0D, 0.0D, 24.0D);
    public static final ConfigDouble TARGET_RANGE = new ConfigDouble("weapon.nebula_blaze.target_range", "nebula_blaze_target_range", 8.0D, 0.0D, 128.0D);
    public static final ConfigDouble EXPLOSIVE_TARGET_RANGE = new ConfigDouble("weapon.nebula_blaze.explosive_target_range", "nebula_blaze_explosive_target_range", 16.0D, 0.0D, 128.0D);
    public static final ConfigDouble EXPLOSION_RANGE = new ConfigDouble("weapon.nebula_blaze.explosion_range", "nebula_blaze_explosion_range", 3.0D, 0.0D, 64.0D);
    public static final ConfigDouble EXPLOSIVE_CHANCE = new ConfigDouble("weapon.nebula_blaze.explosive_chance", "nebula_blaze_explosive_chance", 0.2D, 0.0D, 1.0D);
    public static final ConfigDouble MANA_CONSUME = new ConfigDouble("weapon.nebula_blaze.mana_consume", "nebula_blaze_mana_consume", 12.0D, 0.0D, 10000.0D);

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                DAMAGE,
                EXPLOSIVE_DAMAGE,
                SPEED,
                EXPLOSIVE_SPEED,
                TARGET_RANGE,
                EXPLOSIVE_TARGET_RANGE,
                EXPLOSION_RANGE,
                EXPLOSIVE_CHANCE,
                MANA_CONSUME
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "nebula_blaze",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/nebula_blaze.png"),
        Component.translatable("item.lzxnoneterraria.nebula_blaze"),
        CONFIG_DATA
    );

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            checkBeforeTick(summon);
            tickProjectile(summon, SPEED.get(), TARGET_RANGE.get(), DAMAGE.get());
        }

        @Override
        public void onDied(StaticSummon summon) {
            explode(summon, DAMAGE.get());
        }
    };

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR2 = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            checkBeforeTick(summon);
            tickProjectile(summon, EXPLOSIVE_SPEED.get(), EXPLOSIVE_TARGET_RANGE.get(), EXPLOSIVE_DAMAGE.get());
        }

        @Override
        public void onDied(StaticSummon summon) {
            explode(summon, EXPLOSIVE_DAMAGE.get());
        }
    };

    public NebulaBlaze() {
        super(Tiers.DIAMOND, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public int getUseTime(ItemStack weaponStack, LivingEntity entity) {
        return 12;
    }

    @Override
    public float getTooltipDamage(ItemStack weaponStack, LivingEntity entity) {
        float damage = entity instanceof Player player ? DamageUtil.applyPlayerDamageEffects(player, DAMAGE.get()) : DAMAGE.get();
        return applyMagicDamageBonus(weaponStack, entity, damage);
    }

    @Override
    protected double getManaConsumeRate(ItemStack stack, LivingEntity entity) {
        return MANA_CONSUME.get();
    }

    @Override
    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        if(level.isClientSide()) return;

        player.playNotifySound(ModSounds.MAGIC_SHOOT.get(), SoundSource.PLAYERS, 4.0F, 1.0F);

        Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
        Vector3f offset = new Vector3f(
            hand == InteractionHand.OFF_HAND ? -OFFSET.x : OFFSET.x,
            OFFSET.y,
            OFFSET.z
        );
        Vec3 eyePos = player.getEyePosition();
        Vec3 pos = new Vec3(
            eyePos.x + dirs[0].x * offset.z + dirs[1].x * offset.y + dirs[2].x * offset.x,
            eyePos.y + dirs[0].y * offset.z + dirs[1].y * offset.y + dirs[2].y * offset.x,
            eyePos.z + dirs[0].z * offset.z + dirs[1].z * offset.y + dirs[2].z * offset.x
        );
        Vec3 direction = MathUtil.toVec3(dirs[0]).normalize();

        boolean explosive = player.getRandom().nextDouble() < EXPLOSIVE_CHANCE.get();
        StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), level);
        summon.setOwner(player);
        summon.setPos(pos);
        summon.setDeltaMovement(direction.scale(explosive ? EXPLOSIVE_SPEED.get() : SPEED.get()));
        summon.getEntityData().set(StaticSummon.STACK_SOURCE, stack.copy());
        summon.getEntityData().set(StaticSummon.BEHAVIOR, explosive ? StaticSummonBehaviors.NEBULA_BLAZE_EXPLOSIVE : StaticSummonBehaviors.NEBULA_BLAZE);
        summon.getEntityData().set(StaticSummon.LIFETIME, 100);
        summon.getEntityData().set(StaticSummon.GLOW, true);
        summon.setNoGravity(true);
        summon.noPhysics = true;

        float[] xyRot = MathUtil.computeXYRot(direction.toVector3f());
        summon.setXRot(xyRot[0]);
        summon.setYRot(xyRot[1]);
        summon.xRotO = xyRot[0];
        summon.yRotO = xyRot[1];
        level.addFreshEntity(summon);
    }

    private static void tickProjectile(StaticSummon summon, double speed, double targetRange, float damage) {
        Vec3 direction = summon.getDeltaMovement();
        if(direction.lengthSqr() < 1.0E-7D) direction = summon.getLookAngle();
        if(direction.lengthSqr() < 1.0E-7D) direction = new Vec3(0.0D, 0.0D, 1.0D);

        if(summon.level() instanceof ServerLevel serverLevel) {
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            Entity target = null;
            if(customData.contains(TARGET_UUID_KEY)) {
                target = serverLevel.getEntity(customData.getUUID(TARGET_UUID_KEY));
                if(target == null || !target.isAlive()) {
                    target = null;
                    customData.remove(TARGET_UUID_KEY);
                }
            }
            if(target == null) {
                List<Entity> targets = SearchUtil.searchNearestEnemies(summon, summon.getOwner(), summon.getBoundingBox().inflate(targetRange), 1);
                target = targets.isEmpty() ? null : targets.getFirst();
                if(target != null) customData.putUUID(TARGET_UUID_KEY, target.getUUID());
            }
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

            if(target != null) {
                Vec3 targetDir = target.getBoundingBox().getCenter().subtract(summon.position());
                if(targetDir.lengthSqr() > 1.0E-7D) direction = targetDir.normalize();
            }
        }

        Vec3 motion = direction.normalize().scale(speed);
        Vec3 start = summon.position();
        Vec3 end = start.add(motion);
        summon.setDeltaMovement(motion);

        float[] xyRot = MathUtil.computeXYRot(direction.toVector3f());
        summon.setXRot(xyRot[0]);
        summon.setYRot(xyRot[1]);
        summon.xRotO = xyRot[0];
        summon.yRotO = xyRot[1];

        if(summon.level().isClientSide()) return;

        AABB hitBox = new AABB(start, end).inflate(HITBOX_INFLATE);
        List<Entity> hitTargets = summon.level().getEntitiesOfClass(
            Entity.class,
            hitBox,
            FilterUtil.createTargetFilter(summon, summon.getOwner())
        );
        if(!hitTargets.isEmpty()) {
            explode(summon, damage);
            return;
        }

        BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, end);
        if(blockHitResult.getType() != HitResult.Type.MISS) {
            summon.setPos(blockHitResult.getLocation());
            summon.setDeltaMovement(Vec3.ZERO);
            explode(summon, damage);
        }
    }

    private static void explode(StaticSummon summon, float damage) {
        if(!(summon.level() instanceof ServerLevel serverLevel)) return;

        Vec3 pos = summon.position();
        double explosionRange = EXPLOSION_RANGE.get();
        if(explosionRange > 0.0D) {
            ItemStack sourceStack = summon.getEntityData().get(StaticSummon.STACK_SOURCE);
            for(Entity target : serverLevel.getEntitiesOfClass(
                Entity.class,
                AABB.ofSize(pos, explosionRange * 2.0D, explosionRange * 2.0D, explosionRange * 2.0D),
                FilterUtil.createTargetFilter(summon, summon.getOwner())
            )) {
                if(target.distanceToSqr(pos) > explosionRange * explosionRange) continue;
                if(DamageUtil.magicAttack(summon, target, sourceStack, damage, 0.4F, 5)) {
                }
            }
        }

        summon.discard();
    }
}
