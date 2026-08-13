package com.lzxnone.terraria.item.weapon.magic;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.enchantment.ModEnchantmentConfigs;
import com.lzxnone.terraria.enchantment.ModEnchantments;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.weapon.MagicWeapon;
import com.lzxnone.terraria.particle.CircleParticleOptions;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.CollisionUtil;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.ParticleUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class BubbleGun extends MagicWeapon {
    public static final Vector3f OFFSET = new Vector3f(-0.25F, -0.1F, 1.2F);
    public static final double SPRINT_SPREAD_MULTIPLIER = 2.0D;
    public static final double WALK_SPREAD_MULTIPLIER = 1.5D;
    public static final double CROUCH_SPREAD_MULTIPLIER = 0.5D;
    public static final double HITBOX_INFLATE = 0.35D;
    public static final double BREAK_SPEED = 0.05D;
    public static final float SPREAD = 10.0F;

    public static final ConfigFloat DAMAGE = new ConfigFloat("weapon.bubble_gun.damage", "bubble_gun_damage", 10.0F, 0.0F, 8388600.0F);
    public static final ConfigDouble SPEED = new ConfigDouble("weapon.bubble_gun.speed", "bubble_gun_speed", 1.2D, 0.0D, 10.0D);
    public static final ConfigDouble DRAG = new ConfigDouble("weapon.bubble_gun.drag", "bubble_gun_drag", 0.94D, 0.0D, 1.0D);
    public static final ConfigDouble MANA_CONSUME = new ConfigDouble("weapon.bubble_gun.mana_consume", "bubble_gun_mana_consume", 5.0D, 0.0D, 10000.0D);

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DAMAGE, SPEED, DRAG, MANA_CONSUME);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "bubble_gun",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/bubble_gun.png"),
        Component.translatable("item.lzxnoneterraria.bubble_gun"),
        CONFIG_DATA
    );

    public static final CircleParticleOptions PARTICLE = new CircleParticleOptions(
        0.04F, 16, new Vector3f(0.0F, 0.12F, 0.65F)
    );

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            checkBeforeTick(summon);

            Vec3 motion = summon.getDeltaMovement().scale(DRAG.get());
            if(motion.lengthSqr() <= BREAK_SPEED * BREAK_SPEED) {
                onDied(summon);
                return;
            }

            Vec3 nextPos = summon.position().add(motion);
            summon.setDeltaMovement(motion);

            if(summon.level().isClientSide()) return;

            EntityHitResult entityHitResult = CollisionUtil.checkEntityHit(summon, nextPos, HITBOX_INFLATE);
            if(entityHitResult != null) {
                if(DamageUtil.magicAttack(summon, entityHitResult.getEntity(), summon.getEntityData().get(StaticSummon.STACK_SOURCE), DAMAGE.get(), 0.2F)) {
                    entityHitResult.getEntity().invulnerableTime = 5;
                }
                onDied(summon);
                return;
            }

            BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, nextPos);
            if(blockHitResult.getType() != HitResult.Type.MISS) {
                onDied(summon);
            }
        }

        @Override
        public void onDied(StaticSummon summon) {
            if(!(summon.level() instanceof ServerLevel serverLevel)) return;

            ParticleUtil.addParticles(
                serverLevel, PARTICLE,
                summon.position(), new Vec3(0.18D, 0.18D, 0.18D),
                0.12D, 12
            );
            SoundUtil.playServerSound(serverLevel, ModSounds.BUBBLE_BREAK.get(), summon.position());
            summon.discard();
        }
    };

    public BubbleGun() {
        super(Tiers.DIAMOND, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public int getUseTime(ItemStack weaponStack, LivingEntity entity) {
        return 6;
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

        player.playNotifySound(ModSounds.BUBBLE.get(), SoundSource.PLAYERS, 2.0F, 1.0F);

        Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
        Vec3 forward = MathUtil.toVec3(dirs[0]).normalize();
        Vec3 up = MathUtil.toVec3(dirs[1]).normalize();
        Vec3 right = MathUtil.toVec3(dirs[2]).normalize();

        double spread = SPREAD;
        if(player.isSprinting()) {
            spread *= SPRINT_SPREAD_MULTIPLIER;
        }else if(player.getDeltaMovement().horizontalDistanceSqr() > 1.0E-4D) {
            spread *= WALK_SPREAD_MULTIPLIER;
        }
        if(player.isShiftKeyDown()) spread *= CROUCH_SPREAD_MULTIPLIER;

        int steadyBreathLevel = getEnchantmentLevel(player, stack, ModEnchantments.STEADY_BREATH);
        if(steadyBreathLevel > 0) {
            spread *= Math.pow(ModEnchantmentConfigs.getSteadyBreathSpreadMultiplier(), steadyBreathLevel);
        }

        double yaw = Math.toRadians((player.getRandom().nextFloat() * 2.0F - 1.0F) * spread);
        double pitch = Math.toRadians((player.getRandom().nextFloat() * 2.0F - 1.0F) * spread);
        Vec3 direction = forward
            .add(right.scale(Math.tan(yaw)))
            .add(up.scale(Math.tan(pitch)))
            .normalize();

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

        StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), level);
        summon.setOwner(player);
        summon.setPos(pos);
        summon.setDeltaMovement(direction.scale(SPEED.get()));
        summon.getEntityData().set(StaticSummon.STACK_SOURCE, stack.copy());
        summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.BUBBLE);
        summon.getEntityData().set(StaticSummon.RENDER_MODE, "item");
        summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.BUBBLE_GUN_PROJECTILE.get()));
        summon.getEntityData().set(StaticSummon.LIFETIME, 1200);
        summon.getEntityData().set(StaticSummon.SCALE_X, 0.5F);
        summon.getEntityData().set(StaticSummon.SCALE_Y, 0.5F);
        summon.getEntityData().set(StaticSummon.SCALE_Z, 0.5F);
        summon.setNoGravity(true);
        summon.noPhysics = true;

        float[] xyRot = MathUtil.computeXYRot(direction.toVector3f());
        summon.setXRot(xyRot[0]);
        summon.setYRot(xyRot[1]);
        summon.xRotO = xyRot[0];
        summon.yRotO = xyRot[1];
        level.addFreshEntity(summon);
    }
}
