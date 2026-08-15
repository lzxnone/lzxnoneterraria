package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.item.weapon.MeleeWeapon;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.*;
import net.minecraft.core.Direction;
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
import net.minecraft.world.phys.*;
import org.joml.Vector3f;

import java.util.List;
import net.minecraft.world.item.enchantment.Enchantments;

public class Meowmere extends MeleeWeapon {
    public static final String ID = "meowmere";
    public static final ConfigFloat BASE_MELEE_DAMAGE = createBaseMeleeDamageConfig(ID, 19F);
    public static final ConfigFloat BASE_MELEE_ATTACK_SPEED = createBaseMeleeAttackSpeedConfig(ID, -2.4F);
    public static final ConfigFloat DAMAGE = new ConfigFloat(
        "weapon.meowmere.damage",
        "meowmere_damage",
        20.0f,
        0.0f,
        8388600.0f
    );
    public static final ConfigDouble GRAVITY = new ConfigDouble(
        "weapon.meowmere.gravity",
        "meowmere_gravity",
        0.025,
        0.0,
        1.0
    );
    public static final ConfigDouble INITIAL_SPEED = new ConfigDouble(
        "weapon.meowmere.initial_speed",
        "meowmere_initial_speed",
        1.5,
        0.0,
        10.0
    );
    public Meowmere() {
        super(Tiers.DIAMOND, new Item.Properties().rarity(Rarity.RARE));
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

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                BASE_MELEE_DAMAGE,
                BASE_MELEE_ATTACK_SPEED,
                DAMAGE,
                GRAVITY,
                INITIAL_SPEED
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = createConfigListItem(ID, CONFIG_DATA);

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.05f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(1.00f, 0.50f, 0.50f), // 淡红 (柔粉红)
            new Vector3f(1.00f, 0.75f, 0.50f), // 淡橙 (奶油橘)
            new Vector3f(1.00f, 1.00f, 0.50f), // 淡黄 (米黄色)
            new Vector3f(0.50f, 1.00f, 0.50f), // 淡绿 (薄荷绿)
            new Vector3f(0.50f, 1.00f, 1.00f), // 淡青 (冰蓝色)
            new Vector3f(0.50f, 0.50f, 1.00f), // 淡蓝 (天蓝色)
            new Vector3f(0.75f, 0.50f, 1.00f)  // 淡紫 (薰衣草)
        }
    );

    public static final DustParticleOptions PARTICLE2 = new DustParticleOptions(
        0.2f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(1.00f, 0.50f, 0.50f), // 淡红 (柔粉红)
            new Vector3f(1.00f, 0.75f, 0.50f), // 淡橙 (奶油橘)
            new Vector3f(1.00f, 1.00f, 0.50f), // 淡黄 (米黄色)
            new Vector3f(0.50f, 1.00f, 0.50f), // 淡绿 (薄荷绿)
            new Vector3f(0.50f, 1.00f, 1.00f), // 淡青 (冰蓝色)
            new Vector3f(0.50f, 0.50f, 1.00f), // 淡蓝 (天蓝色)
            new Vector3f(0.75f, 0.50f, 1.00f)  // 淡紫 (薰衣草)
        }
    );

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        public static final ResourceLocation RES = ResourceLocation.parse("lzxnoneterraria:textures/vfx/rainbow.png");
        public static final int MAX_LENGTH = 100;


        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            Vec3 motion = summon.getDeltaMovement();
            motion = motion.add(0, -GRAVITY.get(), 0);
            Vec3 up = MathUtil.toVec3(MathUtil.computeCoordinateSystem(motion.toVector3f(), 0)[1]).normalize();

            summon.trailPositions.addFirst(summon.position().add(up.scale(0.5)));
            summon.trailPositions.addFirst(summon.position().add(up.scale(-0.5)));
            while(summon.trailPositions.size() > MAX_LENGTH) summon.trailPositions.removeLast();

            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);

            EntityHitResult entityHitResult = CollisionUtil.checkEntityHit(summon, summon.position().add(motion), 1.0);
            if(entityHitResult != null) {
                Entity target = entityHitResult.getEntity();
                if(summon.getOwner() instanceof Player player) {
                    if(DamageUtil.meleeAttack(summon, target, summon.getEntityData().get(StaticSummon.STACK_SOURCE), (float) DAMAGE.get(), 1.0f, 10)) {
                        if(customData.contains("hitEntity") && customData.getInt("hitEntity") < 4) {
                            customData.putInt("hitEntity", customData.getInt("hitEntity") + 1);
                            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
                        }else {
                            onDied(summon);
                            return;
                        }
                    }
                }
                return;
            }

            BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, summon.position().add(motion));
            if(blockHitResult.getType() != HitResult.Type.MISS) {
                if(customData.contains("hitBlock")) {
                    int hitBlock = customData.getInt("hitBlock");

                    int soundIndex = summon.getRandom().nextInt(2);
                    if(soundIndex == 0) SoundUtil.playServerSound(summon.level(), ModSounds.CAT.get(), summon.position());
                    else SoundUtil.playServerSound(summon.level(), ModSounds.CAT2.get(), summon.position());

                    if(summon.level().isClientSide()) {
                        Vec3 dir = summon.getDeltaMovement();
                        Vector3f[] dirs = MathUtil.computeCoordinateSystem(dir.toVector3f(), 0);
                        int count = (hitBlock + 1) * 15;
                        double deltaAngle = 360.0 / count;
                        for(int i = 0;i < count;i++) {
                            double angle = i * deltaAngle;
                            double cos = Math.cos(Math.toRadians(angle));
                            double sin = Math.sin(Math.toRadians(angle));
                            Vec3 speed = new Vec3(
                                dirs[1].x * cos + dirs[2].x * sin,
                                dirs[1].y * cos + dirs[2].y * sin,
                                dirs[1].z * cos + dirs[2].z * sin
                            );
                            ParticleUtil.addParticle(
                                summon.level(), PARTICLE2,
                                summon.position(), 0.0,
                                speed.scale(0.05 * (hitBlock + 1)), 0.0
                            );
                        }
                    }else {
                        if(summon.getOwner() instanceof Player player) {
                            List<LivingEntity> hitEntities = summon.level().getEntitiesOfClass(
                                LivingEntity.class,
                                new AABB(
                                    summon.getX() - 0.25 * (hitBlock + 1), summon.getY() - 0.25 * (hitBlock + 1), summon.getZ() - 0.25 * (hitBlock + 1),
                                    summon.getX() + 0.25 * (hitBlock + 1), summon.getY() + 0.25 * (hitBlock + 1), summon.getZ() + 0.25 * (hitBlock + 1)
                                ),
                                FilterUtil.createLivingTargetFilter(summon.getOwner())
                            );
                            for(LivingEntity livingEntity : hitEntities) livingEntity.hurt(summon.damageSources().playerAttack(player), (float) DAMAGE.get());
                        }
                    }

                    if(hitBlock < 4) {
                        customData.putInt("hitBlock", hitBlock + 1);
                        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

                        Direction face = blockHitResult.getDirection();

                        double mx = motion.x;
                        double my = motion.y;
                        double mz = motion.z;

                        switch(face.getAxis()) {
                            case X -> mx = -mx;
                            case Y -> my = -my;
                            case Z -> mz = -mz;
                        }

                        if(face == Direction.UP && Math.abs(my) < 0.1) {
                            my = 0;
                        }

                        motion = new Vec3(mx, my, mz);

                        Vec3 hitVec = blockHitResult.getLocation();
                        summon.setPos(
                            hitVec.x + face.getStepX() * 0.05,
                            hitVec.y + face.getStepY() * 0.05,
                            hitVec.z + face.getStepZ() * 0.05
                        );
                    }else {
                        onDied(summon);
                        return;
                    }
                }else {
                    onDied(summon);
                    return;
                }
            }
            summon.setDeltaMovement(motion);
        }

        @Override
        public AABB getBoundingBoxForCulling(StaticSummon summon) {
            if(summon.trailPositions.isEmpty()) return summon.getBoundingBox();
            return new AABB(summon.position(), summon.trailPositions.getLast());
        }

        @Override
        public void onDied(StaticSummon summon) {
            if(!summon.level().isClientSide()) {
                for(int i = 0;i < summon.trailPositions.size();i += 2) {
                    Vec3 pos1 = summon.trailPositions.get(i);
                    Vec3 pos2 = summon.trailPositions.get(i + 1);
                    Vec3 mid = new Vec3(
                        (pos1.x + pos2.x) / 2,
                        (pos1.y + pos2.y) / 2,
                        (pos1.z + pos2.z) / 2
                    );
                    ParticleUtil.addParticles(
                        (ServerLevel) summon.level(), PARTICLE,
                        mid, new Vec3(0, 0, 0), 0, 1
                    );
                }
                summon.discard();
            }
        }
    };

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if(!level.isClientSide()) {
            StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), level);
            summon.setOwner(player);
            summon.getEntityData().set(StaticSummon.STACK_SOURCE, player.getWeaponItem().copy());
            Vec3 pos = player.getBoundingBox().getCenter();
            summon.setPos(pos);
            summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.MEOWMERE_PROJECTILE);
            summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
            summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.MEOWMERE_PROJECTILE.get()));
            summon.getEntityData().set(StaticSummon.RYP, -90);
            summon.getEntityData().set(StaticSummon.RZP, -90);
            summon.getEntityData().set(StaticSummon.LIFETIME, 200);
            summon.getEntityData().set(StaticSummon.GLOW, true);

            CompoundTag customData = new CompoundTag();
            customData.putInt("hitBlock", 0);
            customData.putInt("hitEntity", 0);
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

            summon.setNoGravity(true);
            summon.noPhysics = true;

            Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
            float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);
            summon.setXRot(xyRot[0]);
            summon.setYRot(xyRot[1]);
            summon.setDeltaMovement(player.getLookAngle().normalize().scale(INITIAL_SPEED.get()));
            level.addFreshEntity(summon);
        }else {
            ParticleUtil.addParticles(
                player.level(), PARTICLE,
                player.position(), 2.0,
                new Vec3(0, 0, 0), 0.1,
                25
            );
        }

        player.getCooldowns().addCooldown(stack.getItem(), 3);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}

