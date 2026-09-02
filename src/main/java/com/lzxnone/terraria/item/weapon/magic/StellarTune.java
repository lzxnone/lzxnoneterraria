package com.lzxnone.terraria.item.weapon.magic;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.IStaticProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.weapon.MagicWeapon;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.particle.StarlightParticleOptions;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.AmmoUtil;
import com.lzxnone.terraria.utils.CollisionUtil;
import com.lzxnone.terraria.utils.ParticleUtil;
import com.lzxnone.terraria.utils.SearchUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Locale;

public class StellarTune extends MagicWeapon {
    public static final ConfigFloat DAMAGE = new ConfigFloat("weapon.stellar_tune.damage", "stellar_tune_damage", 9.0F, 0.0F, 8388600.0F);
    public static final ConfigDouble MANA_CONSUME = new ConfigDouble("weapon.stellar_tune.mana_consume", "stellar_tune_mana_consume", 12.0D, 0.0D, 10000.0D);
    public static final ConfigDouble TARGET_RANGE = new ConfigDouble("weapon.stellar_tune.target_range", "stellar_tune_target_range", 32.0D, 0.0D, 128.0D);
    public static final ConfigInt ARRIVAL_TIME = new ConfigInt("weapon.stellar_tune.arrival_time", "stellar_tune_arrival_time", 10, 1, 72000);
    public static final Vector3f OFFSET = new Vector3f(-0.3F, -0.1F, 1.5F);
    public static final StarlightParticleOptions PARTICLE = new StarlightParticleOptions(
        1.0f,
        0.35f,
        40,
        new Vector3f[]{
            new Vector3f(1.00f, 0.55f, 0.76f), // 粉红
            new Vector3f(0.56f, 0.76f, 1.00f), // 粉蓝
            new Vector3f(1.00f, 0.87f, 0.54f)  // 粉黄
        }
    );

    private static final double WAVE_AMPLITUDE = 2.0D;
    private static final double HITBOX_INFLATE = 1.0D;

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DAMAGE, MANA_CONSUME, TARGET_RANGE, ARRIVAL_TIME);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "stellar_tune",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/stellar_tune.png"),
        Component.translatable("item.lzxnoneterraria.stellar_tune"),
        CONFIG_DATA
    );

    public StellarTune() {
        super(Tiers.DIAMOND, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    public static final IStaticProjectileBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileBehavior() {
        @Override
        public void onMoving(StaticProjectile projectile) {
            if(projectile.level().isClientSide()) {
                ParticleUtil.addParticle(projectile.level(), ModParticles.STAR_PARTICLE.get(), projectile.position(), 0.25D, Vec3.ZERO, 0.2D);
                return;
            }
            Vec3 start = projectile.prevPos == null ? projectile.position() : projectile.prevPos;
            EntityHitResult result = CollisionUtil.checkEntityHit(projectile, start, projectile.position(), HITBOX_INFLATE);
            if(result != null) onHitEntity(projectile, result);
        }

        @Override
        public void onHitEntity(StaticProjectile projectile, EntityHitResult result) {
            if(projectile.level().isClientSide()) return;
            DamageUtil.magicAttack(projectile, result.getEntity(), projectile.getEntityData().get(StaticProjectile.STACK_SOURCE), DAMAGE.get(), 0.4F, 10);
            onDied(projectile);
        }

        @Override
        public void onDied(StaticProjectile projectile) {
            if(projectile.level() instanceof ServerLevel serverLevel) {
                ParticleUtil.addParticles(serverLevel, ModParticles.STAR_PARTICLE.get(), projectile.position(), new Vec3(0.5D, 0.5D, 0.5D), 0.25D, 25);
                int count = 4 + projectile.getRandom().nextInt(5);
                ParticleUtil.addParticles(
                    serverLevel,
                    PARTICLE,
                    projectile.position(),
                    new Vec3(0.35D, 0.35D, 0.35D),
                    0.16D,
                    count
                );
            }
            projectile.discard();
        }
    };

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
    public int getUseTime(ItemStack weaponStack, LivingEntity entity) {
        return computeUseTime(12, weaponStack, entity);
    }

    @Override
    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        if(level.isClientSide()) return;

        double targetRange = TARGET_RANGE.get();
        Vector3f[] playerDirs = MathUtil.computeCoordinateSystem(player);
        Vector3f offset = new Vector3f(
            hand == (player.getMainArm() == HumanoidArm.RIGHT ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND) ? -OFFSET.x : OFFSET.x,
            OFFSET.y,
            OFFSET.z
        );
        Vec3 eyePos = player.getEyePosition();
        Vec3 start = new Vec3(
            eyePos.x + playerDirs[0].x * offset.z + playerDirs[1].x * offset.y + playerDirs[2].x * offset.x,
            eyePos.y + playerDirs[0].y * offset.z + playerDirs[1].y * offset.y + playerDirs[2].y * offset.x,
            eyePos.z + playerDirs[0].z * offset.z + playerDirs[1].z * offset.y + playerDirs[2].z * offset.x
        );
        Vec3 targetPos = null;
        double bestDistanceSqr = Double.MAX_VALUE;
        AABB searchBox = AABB.ofSize(player.getEyePosition(), targetRange * 2.0D, targetRange * 2.0D, targetRange * 2.0D);
        for(Entity target : SearchUtil.searchEnemies(player, player, searchBox)) {
            Vec3 candidatePos = target.getBoundingBox().getCenter();
            double distanceSqr = candidatePos.distanceToSqr(start);
            if(distanceSqr > targetRange * targetRange || distanceSqr >= bestDistanceSqr) continue;
            if(!hasLineOfSight(player, candidatePos)) continue;
            bestDistanceSqr = distanceSqr;
            targetPos = candidatePos;
        }
        if(targetPos == null) {
            targetPos = MathUtil.getCrosshairPos(player, level, targetRange, true, true);
        }

        Vec3 dir = targetPos.subtract(start);
        double distance = dir.length();
        if(distance < 0.001D) {
            dir = player.getLookAngle().normalize();
            distance = 0.001D;
        }

        int soundIndex = targetRange <= 0.0D ? 0 : (int) Math.floor(distance / Math.max(0.001D, targetRange / 6.0D));
        soundIndex = Math.max(0, Math.min(5, soundIndex));
        switch(soundIndex) {
            case 0 -> player.playNotifySound(ModSounds.STAR_TUNE0.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
            case 1 -> player.playNotifySound(ModSounds.STAR_TUNE1.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
            case 2 -> player.playNotifySound(ModSounds.STAR_TUNE2.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
            case 3 -> player.playNotifySound(ModSounds.STAR_TUNE3.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
            case 4 -> player.playNotifySound(ModSounds.STAR_TUNE4.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
            default -> player.playNotifySound(ModSounds.STAR_TUNE5.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
        }

        int arrivalTime = Math.max(1, ARRIVAL_TIME.get());
        double speed = distance / arrivalTime;
        Vector3f[] dirs = MathUtil.computeCoordinateSystem(dir.toVector3f(), player.getYRot());
        dirs = MathUtil.rotateCoordinateSystem(dirs[0], dirs[2], (int) ((Math.random() * 2 - 1) * 90));

        StaticProjectile projectile = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), level);
        projectile.setOwner(player);
        projectile.setPos(start);
        projectile.getEntityData().set(StaticProjectile.STACK_SOURCE, stack.copy());
        projectile.getEntityData().set(StaticProjectile.ITEM, new ItemStack(ModItems.FALLEN_STAR.get()));
        projectile.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.STELLAR_TUNE_PROJECTILE);
        projectile.getEntityData().set(StaticProjectile.RENDER_MODE, "custom");
        projectile.getEntityData().set(StaticProjectile.ORIGIN, start.toVector3f());
        projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
        projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
        projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);
        projectile.getEntityData().set(StaticProjectile.LIFETIME, arrivalTime);
        projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format(Locale.ROOT, "%.5f*t", speed));
        projectile.getEntityData().set(StaticProjectile.EXPRESSION_Y, String.format(Locale.ROOT, "%.5f*sin(%.5f*t)", WAVE_AMPLITUDE, Math.PI * 2.0D / arrivalTime));
        projectile.getEntityData().set(StaticProjectile.GLOW, true);
        level.addFreshEntity(projectile);
    }

    private static boolean hasLineOfSight(Player player, Vec3 targetPos) {
        return player.level().clip(new ClipContext(
            player.getEyePosition(),
            targetPos,
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            player
        )).getType() == HitResult.Type.MISS;
    }
}
