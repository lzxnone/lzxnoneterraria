package com.lzxnone.terraria.item.weapon.melee;

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
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.utils.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.List;

public class Seedler extends SwordItem {
    public Seedler() {
        super(Tiers.DIAMOND, new Item.Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_damage"), 8, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "base_attack_speed"), -2.4, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        ));
    }

    public static final double GRAVITY = 0.075; //重力
    public static final double BOUNCINESS = 0.5;
    public static final double VZ = 0.75;

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            Vec3 motion = summon.getDeltaMovement();
            motion = motion.add(0, -GRAVITY, 0);

            EntityHitResult entityHitResult = CollisionUtil.checkEntityHit(summon, summon.position().add(motion));
            if(entityHitResult != null) {
                Entity target = entityHitResult.getEntity();
                if(summon.getOwner() instanceof Player player && target instanceof LivingEntity livingEntity) livingEntity.hurt(summon.damageSources().playerAttack(player), 7.0f);
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
                    case X -> mx = -mx * BOUNCINESS;
                    case Y -> my = -my * BOUNCINESS;
                    case Z -> mz = -mz * BOUNCINESS;
                }

                if(face == Direction.UP && Math.abs(my) < 0.1) {
                    my = 0;
                    mx *= BOUNCINESS;
                    mz *= BOUNCINESS;
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
                List<Monster> targets = summon.level().getEntitiesOfClass(
                    Monster.class,
                    AABB.ofSize(summon.position(), 64, 64, 64),
                    FilterUtil.createMonsterFilter(summon.getOwner())
                );
                targets.sort(Comparator.comparingDouble(e -> e.distanceToSqr(summon.position())));

                int count = summon.getRandom().nextInt(4) + 4;
                for(int i = 0; i < count; i++) {
                    Vec3 tPos = i < targets.size() ? new Vec3(targets.get(i).getX(), targets.get(i).getEyeY(), targets.get(i).getZ()) : MathUtil.getRandomPosInRadius(summon.position(), 4);
                    Vec3 dir = new Vec3(tPos.x - summon.getX(), tPos.y - summon.getY(), tPos.z - summon.getZ()).normalize();

                    Vector3f[] dirs = MathUtil.computeDir(MathUtil.toVector3f(dir));

                    StaticProjectile proj = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), summon.level());
                    proj.setOwner(summon.getOwner());
                    proj.setPos(summon.position());
                    proj.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.SEEDLER_THORN);
                    proj.getEntityData().set(StaticProjectile.RENDER_MODE, "item");
                    proj.getEntityData().set(StaticProjectile.ORIGIN, MathUtil.toVector3f(summon.position()));
                    proj.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
                    proj.getEntityData().set(StaticProjectile.UP, dirs[1]);
                    proj.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);
                    proj.getEntityData().set(StaticProjectile.ITEM, new ItemStack(ModItems.SEEDLER_THORN.get()));
                    proj.getEntityData().set(StaticProjectile.SCALE_X, 0.5f);
                    proj.getEntityData().set(StaticProjectile.SCALE_Y, 0.5f);
                    proj.getEntityData().set(StaticProjectile.RXP, 90);
                    proj.getEntityData().set(StaticProjectile.LIFETIME, 30);
                    proj.getEntityData().set(StaticProjectile.EXPRESSION_Z, "t*2");

                    proj.setDeltaMovement(MathUtil.toVec3(dirs[0]));
                    summon.level().addFreshEntity(proj);
                }

                ParticleUtil.addParticles(
                    (ServerLevel) summon.level(), ModParticles.EXPLODE_PARTICLE.get(),
                    summon.position(), new Vec3(0.2, 0.2, 0.2),
                    0.05, 1
                );
                ParticleUtil.addParticles(
                    (ServerLevel) summon.level(), ModParticles.EXPLODE_FLAME_PARTICLE,
                    summon.position(), new Vec3(0.2, 0.2, 0.2),
                    0.2, 25
                );
                ParticleUtil.addParticles(
                    (ServerLevel) summon.level(), ModParticles.SMOKE_PARTICLE,
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
                if(result.getEntity() instanceof LivingEntity target) {
                    Entity owner = projectile.getOwner();
                    if(owner == null) return;
                    if(!FilterUtil.createLivingTargetFilter(owner).test(target) || !(owner instanceof Player player)) return;
                    if(target.hurt(projectile.damageSources().playerAttack(player), 7f)) onDied(projectile);
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

            float[] xyRot = MathUtil.computeXYRot(MathUtil.toVector3f(player.getLookAngle()));
            summon.setXRot(xyRot[0]);
            summon.setYRot(xyRot[1]);
            summon.setDeltaMovement(player.getLookAngle().normalize().scale(VZ));
            level.addFreshEntity(summon);
        }

        player.getCooldowns().addCooldown(stack.getItem(), 10);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
