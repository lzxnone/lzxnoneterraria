package com.lzxnone.terraria.item.weapon.ranged;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.IStaticProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.weapon.RangedWeapon;
import com.lzxnone.terraria.particle.ModParticles;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.joml.Vector3f;

import java.util.List;

public class SuperStarShooter extends RangedWeapon {
    public static final String ID = "super_star_shooter";
    public static final Vector3f OFFSET = new Vector3f(-0.3f, -0.1f, 1.5f);
    public static final ConfigDouble SPEED = new ConfigDouble("weapon.super_star_shooter.speed", "super_star_shooter_speed", 2.5D, 0.0D, 24.0D);
    public static final ConfigFloat DAMAGE = new ConfigFloat("weapon.super_star_shooter.damage", "super_star_shooter_damage", 9.0f, 0.0f, 8388600.0f);
    public static final ConfigDouble BOLT_SPEED = new ConfigDouble("weapon.super_star_shooter.bolt_speed", "super_star_shooter_bolt_speed", 2.0D, 0.0D, 24.0D);
    public static final ConfigFloat BOLT_DAMAGE = new ConfigFloat("weapon.super_star_shooter.bolt_damage", "super_star_shooter_bolt_damage", 9.0f, 0.0f, 8388600.0f);

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(SPEED, DAMAGE, BOLT_SPEED, BOLT_DAMAGE);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        ID,
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/" + ID + ".png"),
        Component.translatable("item.lzxnoneterraria." + ID),
        CONFIG_DATA
    );

    public SuperStarShooter() {
        super(Tiers.DIAMOND, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            Vec3 motion = summon.getLookAngle().normalize().scale(SPEED.get());
            Vec3 start = summon.position();
            Vec3 end = start.add(motion);

            if(summon.level().isClientSide()) {
                ParticleUtil.addParticle(
                    summon.level(), ModParticles.STAR_PARTICLE.get(),
                    summon.position(), 0.0,
                    new Vec3(0, 0, 0), 0.2
                );
                summon.setPos(end);
                return;
            }

            //碰撞检测
            AABB hitBox = new AABB(start, end).inflate(0.25);
            List<Entity> targets = summon.level().getEntitiesOfClass(
                Entity.class,
                hitBox,
                FilterUtil.createTargetFilter(summon, summon.getOwner())
            );

            for(Entity target : targets) {
                ItemStack sourceStack = summon.getEntityData().get(StaticSummon.STACK_SOURCE);
                if(DamageUtil.rangedAttack(summon, target, sourceStack, DAMAGE.get(), 0.3f)) {
                    target.invulnerableTime = 10;

                    StaticProjectile projectile = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), summon.level());
                    projectile.setOwner(summon.getOwner());
                    Vec3 pos = MathUtil.getRandomPosOnRadius(summon.position(), 4);
                    Vector3f dir = summon.position().subtract(pos).toVector3f();
                    Vector3f[] dirs = MathUtil.computeCoordinateSystem(dir, 0);
                    projectile.setPos(pos);
                    projectile.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.SUPER_STAR_PRISMATIC_BOLT);
                    projectile.getEntityData().set(StaticProjectile.RENDER_MODE, "custom");
                    projectile.getEntityData().set(StaticProjectile.STACK_SOURCE, summon.getEntityData().get(StaticSummon.STACK_SOURCE).copy());
                    projectile.getEntityData().set(StaticProjectile.ORIGIN, MathUtil.toVector3f(pos));
                    projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
                    projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
                    projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);
                    projectile.getEntityData().set(StaticProjectile.GLOW, true);
                    projectile.getEntityData().set(StaticProjectile.LIFETIME, 5);
                    projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format("%.3f*t", BOLT_SPEED.get()));

                    projectile.setDeltaMovement(MathUtil.toVec3(dirs[0]));
                    summon.level().addFreshEntity(projectile);
                }
            }

            //方块检测
            BlockHitResult blockHitResult = CollisionUtil.checkBlockHit(summon, end);
            if(blockHitResult.getType() != HitResult.Type.MISS) {
                SoundUtil.playServerSound(summon.level(), ModSounds.STAR_COLLIDE.get(), summon.position());
                this.onDied(summon);
                return;
            }

            summon.setPos(end);
        }
    };

    public static final IStaticProjectileBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileBehavior() {
        public void onHitEntity(StaticProjectile projectile, EntityHitResult result) {
            if(!projectile.level().isClientSide()) {
                Entity target = result.getEntity();
                if(DamageUtil.rangedAttack(projectile, target, projectile.getEntityData().get(StaticProjectile.STACK_SOURCE), BOLT_DAMAGE.get(), 0.3f)) {
                    target.invulnerableTime = 5;
                }
            }
        }
    };

    @Override
    public boolean canUseAmmo(ItemStack weaponStack, ItemStack ammoStack) {
        return ammoStack.is(ModItems.FALLEN_STAR.get());
    }

    @Override
    protected ResourceLocation getDefaultAmmo(ItemStack weaponStack) {
        return ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "fallen_star");
    }

    @Override
    public int getUseTime(ItemStack weaponStack, LivingEntity entity) {
        return 18;
    }

    @Override
    protected void shoot(Level level, Player player, InteractionHand hand, ItemStack stack) {
        SoundUtil.playClientSound(player, ModSounds.STAR_FALL.get());
        if(!level.isClientSide()) {
            StaticSummon summon = AmmoUtil.createAmmoSummon(level, player, hand, stack, OFFSET);
            summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.FALLEN_STAR_SUPER);
            summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.WHITE_STAR.get()));
            level.addFreshEntity(summon);
        }
    }
}
