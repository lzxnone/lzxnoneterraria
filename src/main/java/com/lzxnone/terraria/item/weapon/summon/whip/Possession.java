package com.lzxnone.terraria.item.weapon.summon.whip;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.attachment.TargetMarks;
import com.lzxnone.terraria.block.ModBlocks;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.projectile.IStaticProjectileBehavior;
import com.lzxnone.terraria.entity.projectile.StaticProjectile;
import com.lzxnone.terraria.entity.projectile.StaticProjectileBehaviors;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.weapon.summon.minion.MinionWeapon;
import com.lzxnone.terraria.item.weapon.summon.sentry.SentryWeapon;
import com.lzxnone.terraria.particle.DustParticleOptions;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.ParticleUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.joml.Vector3f;

import java.util.Locale;

public class Possession extends Whip {
    private static final ConfigDouble RANGE = new ConfigDouble("weapon.possession.range", "possession_range", 32.0D, 1.0D, 256.0D);
    private static final ConfigFloat DAMAGE = new ConfigFloat("weapon.possession.damage", "possession_damage", 10.0F, 0.0F, 8388600.0F);
    private static final ConfigInt ROTATE = new ConfigInt("weapon.possession.rotate", "possession_rotate", 60, 0, 360);
    private static final ConfigDouble BEAM_SPEED = new ConfigDouble("weapon.possession.beam_speed", "possession_beam_speed", 1.5D, 0.01D, 32.0D);
    private static final double BEAM_HEIGHT = 4.0D;
    private static final ConfigFloat BEAM_DAMAGE = new ConfigFloat("weapon.possession.beam_damage", "possession_beam_damage", 10.0F, 0.0F, 8388600.0F);
    private static final ConfigInt PROPHETIC_MARK_DURATION = new ConfigInt("weapon.possession.prophetic_mark_duration", "possession_prophetic_mark_duration", 80, 1, 72000);
    public static final ConfigInt MAX_PROPHETIC_MARK_STACKS = new ConfigInt("weapon.possession.max_prophetic_mark_stacks", "possession_max_prophetic_mark_stacks", 3, 0, 1024);

    public Possession() {
        super(Tiers.NETHERITE, new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC));
    }

    @Override
    protected double getRange() {
        return RANGE.get();
    }

    @Override
    protected String getTailRes(StaticSummon summon) {
        return "lzxnoneterraria:textures/vfx/possession_projectile_tail.png";
    }

    @Override
    protected String getBodyRes(StaticSummon summon) {
        return "lzxnoneterraria:textures/vfx/possession_projectile_body.png";
    }

    @Override
    protected String getHeadRes(StaticSummon summon) {
        return "lzxnoneterraria:textures/vfx/possession_projectile_head" + summon.getRandom().nextInt(3) + ".png";
    }

    @Override
    protected float getTailRatio() {
        return 0.05F;
    }

    @Override
    protected float getHeadRatio() {
        return 0.075F;
    }

    @Override
    protected float getBodyUnitRatio() {
        return 0.15F;
    }

    @Override
    protected float getDamage() {
        return DAMAGE.get();
    }

    @Override
    protected int getLifetime() {
        return 10;
    }

    @Override
    protected int getRotateAngle() {
        return ROTATE.get();
    }

    @Override
    protected int getUseTime() {
        return 12;
    }

    @Override
    protected int getInvulnerableTime() {
        return 10;
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                RANGE,
                DAMAGE,
                ROTATE,
                BEAM_SPEED,
                BEAM_DAMAGE,
                PROPHETIC_MARK_DURATION,
                MAX_PROPHETIC_MARK_STACKS
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "possession",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/item/possession.png"),
        Component.translatable("item.lzxnoneterraria.possession"),
        CONFIG_DATA
    );

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(
        0.1f, 0.5f, 40, true, new Vector3f[]{
            new Vector3f(0.0F, 1.0F, 1.0F),
            new Vector3f(0.1F, 0.4F, 1.0F)
        }
    );

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            int age = summon.getEntityData().get(StaticSummon.AGE);
            int lifetime = summon.getEntityData().get(StaticSummon.LIFETIME);
            float progress = (float) age / lifetime;
            if(progress > 0.5f) {
                summon.getEntityData().set(StaticSummon.COLOR_A, Math.max(0, 1.0f - (progress - 0.5f) / 0.5f));
            }
            if(progress > 0.25f) {
                CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
                Vec3 move = new Vec3(
                        customData.contains("moveX") ? customData.getDouble("moveX") : 0,
                        customData.contains("moveY") ? customData.getDouble("moveY") : 0,
                        customData.contains("moveZ") ? customData.getDouble("moveZ") : 0
                );
                summon.setPos(summon.position().add(move.normalize().scale(0.25)));
            }
        }
    };

    public static final IStaticProjectileBehavior PROJECTILE_BEHAVIOR = new IStaticProjectileBehavior() {
        @Override
        public void onMoving(StaticProjectile projectile) {
            Vector3f[] dirs = MathUtil.computeCoordinateSystem(projectile.getDeltaMovement().toVector3f(), 0);
            Vec3 right = MathUtil.toVec3(dirs[2]);
            projectile.trailPositions.addFirst(projectile.position().add(right.scale(0.1)));
            projectile.trailPositions.addFirst(projectile.position().add(right.scale(-0.1)));
            while(projectile.trailPositions.size() > 10) projectile.trailPositions.removeLast();
        }

        @Override
        public void onHitEntity(StaticProjectile projectile, EntityHitResult result) {
            if(!projectile.level().isClientSide()) {
                Entity target = result.getEntity();
                DamageUtil.meleeAttack(projectile, target, projectile.getEntityData().get(StaticProjectile.STACK_SOURCE), BEAM_DAMAGE.get(), 0, 10);
            }
        }
    };

    @Override
    protected void onHitTarget(StaticSummon summon, Entity target) {
        if(summon.level() instanceof ServerLevel serverLevel) {
            TargetMarks marks = target.getData(ModAttachments.TARGET_MARKS).copy();
            marks.getMarks().put(
                TargetMarks.PROPHETIC,
                new TargetMarks.Mark(
                    serverLevel.getGameTime(),
                    PROPHETIC_MARK_DURATION.get(),
                    0,
                    summon.getOwner() == null ? null : summon.getOwner().getUUID(),
                    summon.getEntityData().get(StaticSummon.STACK_SOURCE)
                )
            );
            target.setData(ModAttachments.TARGET_MARKS, marks);

            ParticleUtil.addParticles(
                serverLevel, PARTICLE,
                target.getBoundingBox().getCenter(), new Vec3(0, 0, 0),
                0.1, 5
            );

            // 生成 POSSESSION_PRISMATIC_BOLT 视觉特效
            StaticProjectile projectile = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), summon.level());
            projectile.setOwner(summon.getOwner());
            Vec3 pos = MathUtil.getRandomPosOnRadius(target.getBoundingBox().getCenter(), 1);
            Vector3f dir = target.getBoundingBox().getCenter().subtract(pos).toVector3f();
            Vector3f[] dirs = MathUtil.computeCoordinateSystem(dir, 0);
            projectile.setPos(pos);
            projectile.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.POSSESSION_PRISMATIC_BOLT);
            projectile.getEntityData().set(StaticProjectile.RENDER_MODE, "custom");
            projectile.getEntityData().set(StaticProjectile.STACK_SOURCE, summon.getEntityData().get(StaticSummon.STACK_SOURCE).copy());
            projectile.getEntityData().set(StaticProjectile.ORIGIN, MathUtil.toVector3f(pos));
            projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
            projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
            projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);
            projectile.getEntityData().set(StaticProjectile.GLOW, true);
            projectile.getEntityData().set(StaticProjectile.LIFETIME, 4);
            projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, "0.25*t");

            projectile.setDeltaMovement(MathUtil.toVec3(dirs[0]));
            summon.level().addFreshEntity(projectile);
        }
    }

    public static void markEvent(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        if(!(target.level() instanceof ServerLevel serverLevel)) return;

        TargetMarks marks = target.getData(ModAttachments.TARGET_MARKS);
        TargetMarks.Mark mark = marks.getMarks().get(TargetMarks.PROPHETIC);
        if(mark == null) return;

        if(serverLevel.getGameTime() - mark.getStartTime() >= mark.getDuration()) {
            TargetMarks copiedMarks = marks.copy();
            copiedMarks.getMarks().remove(TargetMarks.PROPHETIC);
            target.setData(ModAttachments.TARGET_MARKS, copiedMarks);
            return;
        }
        if(mark.getStacks() >= MAX_PROPHETIC_MARK_STACKS.get()) return;

        Entity damageEntity = event.getSource().getDirectEntity();
        if(!(damageEntity instanceof StaticSummon summon)) return;

        ItemStack sourceStack = summon.getEntityData().get(StaticSummon.STACK_SOURCE);
        if(sourceStack.isEmpty() || !(sourceStack.getItem() instanceof MinionWeapon || sourceStack.getItem() instanceof SentryWeapon)) return;

        Entity owner = serverLevel.getEntity(mark.getOwner());
        ItemStack markStack = mark.getStack();
        if(owner == null || markStack.isEmpty()) return;

        createVision(summon.position(), target, markStack, owner);

        TargetMarks copiedMarks = marks.copy();
        TargetMarks.Mark copiedMark = mark.copy();
        copiedMark.setStacks(copiedMark.getStacks() + 1);
        copiedMarks.getMarks().put(TargetMarks.PROPHETIC, copiedMark);
        target.setData(ModAttachments.TARGET_MARKS, copiedMarks);
    }

    public static void createVision(Vec3 origin, Entity target, ItemStack stack, Entity owner) {
        Vec3 summonPos = MathUtil.getRandomPosOnRadius(origin, 8);
        Vector3f[] dirs = MathUtil.computeCoordinateSystem(target.position().subtract(summonPos).toVector3f(), 0);
        float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);

        StaticSummon visionSummon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), target.level());
        visionSummon.setOwner(owner);
        visionSummon.setPos(summonPos);
        visionSummon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.POSSESSION_VISION);
        visionSummon.getEntityData().set(StaticSummon.RENDER_MODE, "block");
        visionSummon.getEntityData().set(StaticSummon.BLOCK, ModBlocks.VISION.get().defaultBlockState());
        visionSummon.getEntityData().set(StaticSummon.LIFETIME, 40);
        visionSummon.getEntityData().set(StaticSummon.GLOW, true);
        visionSummon.setNoGravity(true);
        visionSummon.noPhysics = true;
        visionSummon.setXRot(xyRot[0]);
        visionSummon.setYRot(xyRot[1]);
        Vec3 randomPos = MathUtil.getRandomPosInRadius(visionSummon.position(), 1);
        Vec3 randomDir = randomPos.subtract(visionSummon.position()).normalize();
        CompoundTag customData = new CompoundTag();
        customData.putDouble("moveX", randomDir.x);
        customData.putDouble("moveY", randomDir.y);
        customData.putDouble("moveZ", randomDir.z);
        visionSummon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
        target.level().addFreshEntity(visionSummon);

        double dist = summonPos.subtract(target.getBoundingBox().getCenter()).length();
        int lifetime = Math.max(1, (int)Math.ceil(dist / BEAM_SPEED.get()));
        double speed = dist / lifetime;
        double height = Math.min(BEAM_HEIGHT, dist * 0.15D);

        StaticProjectile projectile = new StaticProjectile(ModEntities.STATIC_PROJECTILE.get(), target.level());
        projectile.setOwner(owner);
        projectile.setPos(summonPos);
        projectile.getEntityData().set(StaticProjectile.STACK_SOURCE, stack.copy());
        projectile.getEntityData().set(StaticProjectile.BEHAVIOR, StaticProjectileBehaviors.POSSESSION_PROJECTILE);
        projectile.getEntityData().set(StaticProjectile.RENDER_MODE, "custom");
        projectile.getEntityData().set(StaticProjectile.ORIGIN, summonPos.toVector3f());
        projectile.getEntityData().set(StaticProjectile.DIRECTION, dirs[0]);
        projectile.getEntityData().set(StaticProjectile.UP, dirs[1]);
        projectile.getEntityData().set(StaticProjectile.RIGHT, dirs[2]);
        projectile.getEntityData().set(StaticProjectile.LIFETIME, lifetime);
        projectile.getEntityData().set(StaticProjectile.EXPRESSION_Z, String.format(Locale.ROOT, "%.5f*t", speed));
        projectile.getEntityData().set(
            StaticProjectile.EXPRESSION_Y,
            String.format(Locale.ROOT, "%.5f*4*(t/%.5f)*(1-t/%.5f)", height, (double)lifetime, (double)lifetime)
        );
        projectile.getEntityData().set(StaticProjectile.GLOW, true);
        target.level().addFreshEntity(projectile);
    }
}
