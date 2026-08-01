package com.lzxnone.terraria.item.weapon.summon.whip;

import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.weapon.SummonWeapon;
import com.lzxnone.terraria.utils.CollisionUtil;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.MathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public abstract class Whip extends SummonWeapon {
    private static final int SEGMENTS = 24;
    private static final double HITBOX_INFLATE = 1.0D;
    private static final double SUMMON_OFFSET_X = -1.0D;
    private static final double SUMMON_OFFSET_Y = 1.0D;
    private static final double SUMMON_OFFSET_Z = 0.0D;

    protected Whip(Tier tier, Item.Properties properties) {
        super(tier, properties);
    }

    protected abstract double getRange();

    protected abstract String getRes(StaticSummon summon);

    protected abstract float getDamage();

    protected abstract int getLifetime();

    protected double getHeight() {
        return getRange() * 0.5D;
    }

    protected double getBend() {
        return getRange() * 0.25D;
    }

    protected int getCooldown() {
        return 20;
    }

    protected float getKnockbackScale() {
        return 1.0F;
    }

    protected int getInvulnerableTime() {
        return 20;
    }

    protected int getRotateAngle() {
        return 0;
    }

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            if(summon.isRemoved()) return;

            Entity owner = summon.getOwner();
            if(owner == null) return;

            Vector3f[] dirs = MathUtil.computeCoordinateSystem(owner);
            summon.setPos(new Vec3(
                owner.getX() + dirs[0].x * SUMMON_OFFSET_Z + dirs[1].x * SUMMON_OFFSET_Y + dirs[2].x * SUMMON_OFFSET_X,
                owner.getY() + dirs[0].y * SUMMON_OFFSET_Z + dirs[1].y * SUMMON_OFFSET_Y + dirs[2].y * SUMMON_OFFSET_X,
                owner.getZ() + dirs[0].z * SUMMON_OFFSET_Z + dirs[1].z * SUMMON_OFFSET_Y + dirs[2].z * SUMMON_OFFSET_X
            ));
            hurtTargets(summon);
        }

        @Override
        public AABB getBoundingBoxForCulling(StaticSummon summon) {
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            double range = customData.contains("range") ? customData.getDouble("range") : 8.0D;
            double height = customData.contains("height") ? customData.getDouble("height") : range * 0.5D;
            double bend = customData.contains("bend") ? customData.getDouble("bend") : range * 0.25D;
            double radius = range + height + bend + HITBOX_INFLATE;
            return AABB.ofSize(summon.position(), radius * 2.0D, radius * 2.0D, radius * 2.0D);
        }
    };

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if(!level.isClientSide()) {
            Vector3f[] dirs = MathUtil.computeCoordinateSystem(player);
            StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), level);
            summon.setOwner(player);
            summon.getEntityData().set(StaticSummon.STACK_SOURCE, stack.copy());
            summon.setPos(new Vec3(
                player.getX() + dirs[0].x * SUMMON_OFFSET_Z + dirs[1].x * SUMMON_OFFSET_Y + dirs[2].x * SUMMON_OFFSET_X,
                player.getY() + dirs[0].y * SUMMON_OFFSET_Z + dirs[1].y * SUMMON_OFFSET_Y + dirs[2].y * SUMMON_OFFSET_X,
                player.getZ() + dirs[0].z * SUMMON_OFFSET_Z + dirs[1].z * SUMMON_OFFSET_Y + dirs[2].z * SUMMON_OFFSET_X
            ));
            summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.POSSESSION);
            summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
            summon.getEntityData().set(StaticSummon.LIFETIME, getLifetime());

            int rotateAngle = (int)((player.getRandom().nextFloat() * 2.0F - 1.0F) * getRotateAngle());
            dirs = MathUtil.rotateCoordinateSystem(dirs[0], dirs[2], rotateAngle);
            CompoundTag customData = new CompoundTag();
            customData.putFloat("dirX", dirs[0].x);
            customData.putFloat("dirY", dirs[0].y);
            customData.putFloat("dirZ", dirs[0].z);
            customData.putFloat("upX", dirs[1].x);
            customData.putFloat("upY", dirs[1].y);
            customData.putFloat("upZ", dirs[1].z);
            customData.putFloat("rightX", dirs[2].x);
            customData.putFloat("rightY", dirs[2].y);
            customData.putFloat("rightZ", dirs[2].z);
            customData.putDouble("range", getRange());
            customData.putDouble("height", getHeight());
            customData.putDouble("bend", getBend());
            customData.putBoolean("reverse", summon.getRandom().nextInt(2) == 0);
            customData.putString("res", getRes(summon));
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);

            summon.setNoGravity(true);
            summon.noPhysics = true;

            float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);
            summon.setXRot(xyRot[0]);
            summon.setYRot(xyRot[1]);
            summon.setDeltaMovement(Vec3.ZERO);
            level.addFreshEntity(summon);
        }

        player.getCooldowns().addCooldown(stack.getItem(), getCooldown());
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private static void hurtTargets(StaticSummon summon) {
        if(summon.level().isClientSide()) return;

        ItemStack sourceStack = summon.getEntityData().get(StaticSummon.STACK_SOURCE);
        if(!(sourceStack.getItem() instanceof Whip whip)) return;

        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
        int lifetime = Math.max(1, summon.getEntityData().get(StaticSummon.LIFETIME));
        float progress = Mth.clamp(summon.getEntityData().get(StaticSummon.AGE) / (float)lifetime, 0.0F, 1.0F);

        Vec3 hand = summon.position();
        Vec3 dir = readVec3(customData, "dir", new Vec3(0.0D, 0.0D, 1.0D)).normalize();
        Vec3 up = readVec3(customData, "up", new Vec3(0.0D, 1.0D, 0.0D)).normalize();
        if(dir.lengthSqr() < 1.0E-6D) dir = new Vec3(0.0D, 0.0D, 1.0D);
        if(up.lengthSqr() < 1.0E-6D) up = new Vec3(0.0D, 1.0D, 0.0D);

        double range = customData.contains("range") ? customData.getDouble("range") : whip.getRange();
        double height = customData.contains("height") ? customData.getDouble("height") : whip.getHeight();
        double bend = customData.contains("bend") ? customData.getDouble("bend") : whip.getBend();
        boolean reverse = customData.contains("reverse") && customData.getBoolean("reverse");
        List<Vec3> points = computePoints(hand, dir, up, progress, range, height, bend, reverse, SEGMENTS);

        double radius = range + height + bend + HITBOX_INFLATE;
        AABB searchBox = AABB.ofSize(hand, radius * 2.0D, radius * 2.0D, radius * 2.0D);

        for(Entity target : CollisionUtil.searchEntities(summon.level(), searchBox, summon, summon.getOwner())) {
            if(!intersectsWhip(points, target)) continue;
            if(DamageUtil.summonAttack(summon, target, sourceStack, whip.getDamage(), whip.getKnockbackScale())) {
                target.invulnerableTime = whip.getInvulnerableTime();
            }
        }
    }

    private static boolean intersectsWhip(List<Vec3> points, Entity target) {
        AABB targetBox = target.getBoundingBox().inflate(HITBOX_INFLATE);
        for(int i = 0; i < points.size() - 1; i++) {
            if(targetBox.clip(points.get(i), points.get(i + 1)).isPresent()) return true;
        }
        return false;
    }

    private static List<Vec3> computePoints(Vec3 hand, Vec3 dir, Vec3 up, float progress, double range, double height, double bend, boolean reverse, int segments) {
        float phase = 1.0F - Math.abs(progress * 2.0F - 1.0F);
        float s = easeInOut(phase);
        float tipArc = 4.0F * s * (1.0F - s);
        Vec3 tip = hand
            .add(dir.scale(range * s))
            .add(up.scale(height * tipArc));
        Vec3 chord = tip.subtract(hand);
        double bendAmount = bend * (1.0F - s);
        Vec3 dynamicBendDir = dir.scale(1.0F - s).add(up.scale(-s)).normalize();
        if(dynamicBendDir.lengthSqr() < 1.0E-6D) dynamicBendDir = dir;

        List<Vec3> points = new ArrayList<>(segments + 1);
        for(int i = 0; i <= segments; i++) {
            float t = i / (float)segments;
            float shape = 4.0F * t * (1.0F - t);
            Vec3 base = hand.add(chord.scale(t));
            Vec3 point = base.add(dynamicBendDir.scale(shape * bendAmount));
            boolean reflect = reverse ? progress <= 0.5F : progress > 0.5F;
            points.add(reflect ? reflectAcrossDir(hand, dir, point) : point);
        }
        return points;
    }

    private static Vec3 reflectAcrossDir(Vec3 hand, Vec3 dir, Vec3 point) {
        Vec3 relative = point.subtract(hand);
        Vec3 parallel = dir.scale(relative.dot(dir));
        Vec3 perpendicular = relative.subtract(parallel);
        return hand.add(parallel.subtract(perpendicular));
    }

    private static Vec3 readVec3(CompoundTag tag, String prefix, Vec3 fallback) {
        String x = prefix + "X";
        String y = prefix + "Y";
        String z = prefix + "Z";
        if(!tag.contains(x) || !tag.contains(y) || !tag.contains(z)) return fallback;
        return new Vec3(tag.getDouble(x), tag.getDouble(y), tag.getDouble(z));
    }

    private static float easeInOut(float value) {
        return value < 0.5F ? 2.0F * value * value : 1.0F - (float)Math.pow(-2.0F * value + 2.0F, 2.0D) / 2.0F;
    }
}
