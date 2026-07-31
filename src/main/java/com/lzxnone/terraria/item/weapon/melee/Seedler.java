package com.lzxnone.terraria.item.weapon.melee;

import com.lzxnone.terraria.item.weapon.MeleeWeapon;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.IStaticProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.*;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.List;

public class Seedler extends MeleeWeapon {
    public static final ConfigDouble NUT_GRAVITY = new ConfigDouble(
        "weapon.seedler.nut_gravity",
        "seedler_nut_gravity",
        0.075,
        0.0,
        1.0
    );
    public static final ConfigDouble NUT_BOUNCE = new ConfigDouble(
        "weapon.seedler.nut_bounce",
        "seedler_nut_bounce",
        0.5,
        0.0,
        1.0
    );
    public static final ConfigDouble NUT_INITIAL_SPEED = new ConfigDouble(
        "weapon.seedler.nut_initial_speed",
        "seedler_nut_initial_speed",
        0.75,
        0.0,
        10.0
    );
    public static final ConfigFloat THORN_DAMAGE = new ConfigFloat(
        "weapon.seedler.thorn_damage",
        "seedler_thorn_damage",
        2.0f,
        0.0f,
        8388600.0f
    );
    public static final ConfigInt THORN_LIFETIME = new ConfigInt(
        "weapon.seedler.thorn_lifetime",
        "seedler_thorn_lifetime",
        30,
        1,
        1200
    );
    public static final ConfigDouble THORN_SPEED = new ConfigDouble(
        "weapon.seedler.thorn_speed",
        "seedler_thorn_speed",
        2.0,
        0.0,
        10.0
    );
    public Seedler() {
        super(Tiers.DIAMOND, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 7, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -2.4, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ).rarity(Rarity.UNCOMMON));
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                NUT_GRAVITY,
                NUT_BOUNCE,
                NUT_INITIAL_SPEED,
                THORN_DAMAGE,
                THORN_LIFETIME,
                THORN_SPEED
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "seedler",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/seedler.png"),
        Component.translatable("item.lzxnoneterraria.seedler"),
        CONFIG_DATA
    );

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.025f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(0.7F, 0.7F, 0.7F),
        }
    );

    public static final DustParticleOptions PARTICLE2 = new DustParticleOptions(
        0.05f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(1.0F, 0.8F, 0.5F),
            new Vector3f(1.0F, 0.5F, 0.0F),
            new Vector3f(1.0F, 0.9F, 0.0F),
        }
    );

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            Vec3 motion = summon.getDeltaMovement();
            motion = motion.add(0, -NUT_GRAVITY.get(), 0);

            EntityHitResult entityHitResult = CollisionUtil.checkEntityHit(summon, summon.position().add(motion), 1.0);
            if(entityHitResult != null) {
                Entity target = entityHitResult.getEntity();
                if(summon.getOwner() instanceof Player player) {
                    if(DamageUtil.meleeAttack(summon, target, summon.getEntityData().get(StaticSummon.STACK_SOURCE), (float) THORN_DAMAGE.get(), 1.0f)) {
                        target.invulnerableTime = 2;
                    }
                }
                onDied(summon);
                return;
            }

            BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, summon.position().add(motion));
            if(blockHitResult.getType() != HitResult.Type.MISS) {
                Direction face = blockHitResult.getDirection();

                double mx = motion.x;
                double my = motion.y;
                double mz = motion.z;

                switch(face.getAxis()) {
                    case X -> mx = -mx * NUT_BOUNCE.get();
                    case Y -> my = -my * NUT_BOUNCE.get();
                    case Z -> mz = -mz * NUT_BOUNCE.get();
                }

                if(face == Direction.UP && Math.abs(my) < 0.1) {
                    my = 0;
                    mx *= NUT_BOUNCE.get();
                    mz *= NUT_BOUNCE.get();
                }

                motion = new Vec3(mx, my, mz);

                Vec3 hitVec = blockHitResult.getLocation();
                summon.setPos(
                    hitVec.x + face.getStepX() * 0.05,
                    hitVec.y + face.getStepY() * 0.05,
                    hitVec.z + face.getStepZ() * 0.05
                );
            }

            summon.setDeltaMovement(motion);
        }

        @Override
        public void onDied(StaticSummon summon) {
            if(!summon.level().isClientSide()) {
                List<Entity> targets = CollisionUtil.searchEnemies(
                    summon.level(),
                    AABB.ofSize(summon.position(), 64, 64, 64),
                    summon,
                    summon.getOwner()
                );
                targets.sort(Comparator.comparingDouble(e -> e.distanceToSqr(summon.position())));

                int count = summon.getRandom().nextInt(4) + 4;
                for(int i = 0; i < count; i++) {
                    Vec3 tPos = i < targets.size() ? new Vec3(targets.get(i).getX(), targets.get(i).getEyeY(), targets.get(i).getZ()) : MathUtil.getRandomPosInRadius(summon.position(), 4);
                    Vec3 dir = new Vec3(tPos.x - summon.getX(), tPos.y - summon.getY(), tPos.z - summon.getZ()).normalize();

                    Vector3f[] dirs = MathUtil.computeCoordinateSystem(dir.toVector3f(), 0);

                    StaticProjectile proj = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), summon.level());
                    proj.setOwner(summon.getOwner());
                    proj.getEntityData().set(StaticProjectile.STACK_SOURCE, summon.getEntityData().get(StaticSummon.STACK_SOURCE).copy());
                    proj.setPos(summon.position());
                    proj.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.SEEDLER_THORN);
                    proj.getEntityData().set(StaticProjectile.RENDER_MODE, "item");
                    proj.getEntityData().set(StaticProjectile.ORIGIN, summon.position().toVector3f());
                    proj.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
                    proj.getEntityData().set(StaticProjectile.UP, dirs[1]);
                    proj.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);
                    proj.getEntityData().set(StaticProjectile.ITEM, new ItemStack(ModItems.SEEDLER_THORN.get()));
                    proj.getEntityData().set(StaticProjectile.SCALE_X, 0.5f);
                    proj.getEntityData().set(StaticProjectile.SCALE_Y, 0.5f);
                    proj.getEntityData().set(StaticProjectile.RXP, 90);
                    proj.getEntityData().set(StaticProjectile.LIFETIME, THORN_LIFETIME.get());
                    proj.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*t", THORN_SPEED.get()));

                    proj.setDeltaMovement(MathUtil.toVec3(dirs[0]));
                    summon.level().addFreshEntity(proj);
                }

                ParticleUtil.addParticles(
                    (ServerLevel) summon.level(), ModParticles.EXPLODE_PARTICLE.get(),
                    summon.position(), new Vec3(0.2, 0.2, 0.2),
                    0.05, 1
                );
                ParticleUtil.addParticles(
                    (ServerLevel) summon.level(), PARTICLE2,
                    summon.position(), new Vec3(0.2, 0.2, 0.2),
                    0.2, 25
                );
                ParticleUtil.addParticles(
                    (ServerLevel) summon.level(), PARTICLE,
                    summon.position(), new Vec3(0.2, 0.2, 0.2),
                    0.2, 25
                );
                SoundUtil.playServerSound(summon.level(), ModSounds.BOOM.get(), summon.position());

                summon.discard();
            }
        }
    };

    public static final IStaticProjectileBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileBehavior() {
        @Override
        public void onMoving(StaticProjectile projectile) {
            ParticleUtil.addParticle(
                projectile.level(), ModParticles.SEEDLER_THORN_PARTICLE.get(),
                projectile.position(), 0,
                new Vec3(0, 0, 0), 0
            );
        }
        @Override
        public void onHitEntity(StaticProjectile projectile, EntityHitResult result) {
            if(!projectile.level().isClientSide()) {
                Entity target = result.getEntity();
                Entity owner = projectile.getOwner();
                if(owner == null) return;
                if(!FilterUtil.createTargetFilter(owner).test(target) || !(owner instanceof Player player)) return;
                if(DamageUtil.meleeAttack(projectile, target, projectile.getEntityData().get(StaticProjectile.STACK_SOURCE), (float) THORN_DAMAGE.get(), 1.0f)) {
                    target.invulnerableTime = 2;
                    onDied(projectile);
                }
            }
        }
        @Override
        public void onHitBlock(StaticProjectile projectile, BlockHitResult result) {
            if(projectile.level().isClientSide()) return;
            if(!projectile.level().getBlockState(result.getBlockPos()).getCollisionShape(projectile.level(), result.getBlockPos()).isEmpty()) {
                onDied(projectile);
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
            Vec3 pos = new Vec3(player.getX(), player.getEyeY() - 0.1, player.getZ());
            summon.setPos(pos);
            summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.SEEDLER_NUT);
            summon.getEntityData().set(StaticSummon.RENDER_MODE, "item");
            summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.SEEDLER_NUT.get()));
            summon.getEntityData().set(StaticSummon.SCALE_X, 0.5f);
            summon.getEntityData().set(StaticSummon.SCALE_Y, 0.5f);
            summon.getEntityData().set(StaticSummon.RXPS, 10);
            summon.getEntityData().set(StaticSummon.LIFETIME, (int) (7 + Math.random() * 36));

            summon.setNoGravity(true);
            summon.noPhysics = true;

            Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
            float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);
            summon.setXRot(xyRot[0]);
            summon.setYRot(xyRot[1]);
            summon.setDeltaMovement(player.getLookAngle().normalize().scale(NUT_INITIAL_SPEED.get()));
            level.addFreshEntity(summon);
        }

        player.getCooldowns().addCooldown(stack.getItem(), 10);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}

