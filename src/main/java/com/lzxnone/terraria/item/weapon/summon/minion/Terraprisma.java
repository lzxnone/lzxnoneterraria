package com.lzxnone.terraria.item.weapon.summon.minion;

import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.attachment.PlayerSummon;
import com.lzxnone.terraria.entity.ModEntities;
import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigDouble;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import com.lzxnone.terraria.utils.DamageUtil;
import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.MathUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

public class Terraprisma extends MinionWeapon {
    public static final ConfigDouble TARGET_RANGE = new ConfigDouble(
        "weapon.terraprisma.target_range",
        "terraprisma_target_range",
        64.0D,
        1.0D,
        256.0D
    );
    public static final ConfigDouble FOLLOW_SPEED = new ConfigDouble(
        "weapon.terraprisma.follow_speed",
        "terraprisma_follow_speed",
        4.0D,
        0.0D,
        32.0D
    );
    public static final ConfigDouble DASH_PRE_SPEED = new ConfigDouble(
        "weapon.terraprisma.dash_pre_speed",
        "terraprisma_dash_pre_speed",
        -0.75D,
        -10.0D,
        10.0D
    );
    public static final ConfigDouble DASH_PRE_FRICTION = new ConfigDouble(
        "weapon.terraprisma.dash_pre_friction",
        "terraprisma_dash_pre_friction",
        0.78D,
        0.0D,
        1.0D
    );
    public static final ConfigDouble DASH_SPEED = new ConfigDouble(
        "weapon.terraprisma.dash_speed",
        "terraprisma_dash_speed",
        3.2D,
        0.01D,
        32.0D
    );
    public static final ConfigDouble DASH_FRICTION = new ConfigDouble(
        "weapon.terraprisma.dash_friction",
        "terraprisma_dash_friction",
        0.66D,
        0.0D,
        1.0D
    );
    public static final int DASH_MORE_TIME = 1;

    public static final ConfigDouble ROTATE_SPEED = new ConfigDouble(
        "weapon.terraprisma.rotate_speed",
        "terraprisma_rotate_speed",
        1.0D,
        0.01D,
        32.0D
    );
    public static final ConfigFloat DAMAGE = new ConfigFloat(
        "weapon.terraprisma.damage",
        "terraprisma_damage",
        20.0F,
        0.0F,
        8388600.0F
    );
    public static final float ROTATION_LERP = 0.25f;
    public static final float ROTATION_LERP2 = 0.5f;
    public static final float ROTATION_LERP3 = 1.0f;

    public static final float TRAIL_ALPHA = 1.0f;
    public static final int TRAIL_MAX_LENGTH = 20;
    public static final float AFTERIMAGE_ALPHA = 0.5f;
    public static final int AFTERIMAGE_MAX_LENGTH = 3;
    public static final float AFTERIMAGE_SCALE = 0.92f;

    public static final double HITBOX_INFLATE = 1.0;

    public enum State {
        IDLE,
        FIGHT,
        DASH_PRE,
        DASH,
        ROTATE
    };

    public Terraprisma() {
        super(Tiers.NETHERITE, new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC));
    }

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(
                TARGET_RANGE,
                FOLLOW_SPEED,
                DASH_PRE_SPEED,
                DASH_PRE_FRICTION,
                DASH_SPEED,
                DASH_FRICTION,
                ROTATE_SPEED,
                DAMAGE
            );
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "terraprisma",
        ResourceLocation.fromNamespaceAndPath("lzxnoneterraria", "textures/item/terraprisma.png"),
        Component.translatable("item.lzxnoneterraria.terraprisma"),
        CONFIG_DATA
    );

    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = new IStaticSummonBehavior() {
        @Override
        public void tick(StaticSummon summon) {
            this.checkBeforeTick(summon);
            if(summon.getOwner() != null) {
                double dist = summon.getOwner().position().subtract(summon.position()).length();
                if(dist > MinionWeapon.MAX_RANGE) {
                    summon.setPos(summon.getOwner().position());
                }
            }
            if(summon.getOwner() instanceof Player player && !summon.level().isClientSide()) {
                onState(summon, player);
            }
            if(summon.level().isClientSide()) {
                CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
                int state = customData.contains("state") ? customData.getInt("state") : 0;
                if(state == State.DASH_PRE.ordinal() || state == State.DASH.ordinal()) {
                    int idx = customData.contains("idx") ? customData.getInt("idx") : 0;
                    float[] color = computeCrystalColor(summon.tickCount, idx);
                    summon.afterimageFrames.addFirst(new StaticSummon.AfterimageFrame(
                        summon.position(),
                        summon.getXRot(),
                        summon.getYRot(),
                        color[0],
                        color[1],
                        color[2]
                    ));
                    while(summon.afterimageFrames.size() > AFTERIMAGE_MAX_LENGTH) summon.afterimageFrames.removeLast();
                }else {
                    summon.afterimageFrames.clear();
                }

                if(state == State.ROTATE.ordinal()) {
                    Vector3f[] dirs = MathUtil.computeCoordinateSystem(summon);
                    Vec3 dir = MathUtil.toVec3(dirs[0]);
                    summon.trailPositions.addFirst(summon.position().add(dir.scale(-1)));
                    summon.trailPositions.addFirst(summon.position().add(dir.scale(1)));
                    while(summon.trailPositions.size() > TRAIL_MAX_LENGTH) summon.trailPositions.removeLast();
                }else {
                    summon.trailPositions.clear();
                }
            }
        }
    };

    public static void onState(StaticSummon summon, Player player) {
        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
        int state = customData.contains("state") ? customData.getInt("state") : 0;

        if(state == State.IDLE.ordinal()) {
            storeSummonTarget(summon, findSummonTarget(summon, player, TARGET_RANGE.get()));
            tickFollowOwner(summon);
        }else if(state == State.FIGHT.ordinal()) {
            storeSummonTarget(summon, findSummonTarget(summon, player, TARGET_RANGE.get()));
        }else if(state == State.DASH_PRE.ordinal()) {
            Entity target = getStoredSummonTarget(summon);
            if(target != null) {
                Vec3 distV = target.getBoundingBox().getCenter().subtract(summon.position());
                Vec3 targetDir = distV.normalize();
                float[] targetXYRot = MathUtil.computeXYRot(targetDir.toVector3f());
                if(summon.getDeltaMovement().length() < 0.05) {
                    summon.setXRot(Mth.rotLerp(ROTATION_LERP3, summon.getXRot(), targetXYRot[0]));
                    summon.setYRot(Mth.rotLerp(ROTATION_LERP3, summon.getYRot(), targetXYRot[1]));
                }else {
                    summon.setXRot(Mth.rotLerp(ROTATION_LERP, summon.getXRot(), targetXYRot[0]));
                    summon.setYRot(Mth.rotLerp(ROTATION_LERP, summon.getYRot(), targetXYRot[1]));
                }
            }
            summon.setDeltaMovement(summon.getDeltaMovement().scale(DASH_PRE_FRICTION.get()));
        }else if(state == State.DASH.ordinal()) {
            int dashTime = customData.contains("dashTime") ? customData.getInt("dashTime") : 0;
            customData.putInt("dashTime", dashTime - 1);
            if(dashTime > 0) {
                summon.setDeltaMovement(summon.getLookAngle().normalize().scale(DASH_SPEED.get()));
            }else {
                summon.setDeltaMovement(summon.getDeltaMovement().scale(DASH_FRICTION.get()));
            }
            AABB hitBox = new AABB(summon.position(), summon.position().add(summon.getDeltaMovement().normalize())).inflate(HITBOX_INFLATE);
            List<Entity> targets = summon.level().getEntitiesOfClass(
                Entity.class,
                hitBox,
                FilterUtil.createTargetFilter(summon, player)
            );
            ItemStack sourceStack = summon.getEntityData().get(StaticSummon.STACK_SOURCE);
            for(Entity target : targets) {
                if(DamageUtil.summonAttack(summon, target, sourceStack, customData.contains("damage") ? customData.getFloat("damage") : DAMAGE.get(), 1.0f)) target.invulnerableTime = 15;
            }
        }else if(state == State.ROTATE.ordinal()) {
            int time = customData.contains("rotateTime") ? customData.getInt("rotateTime") : 0;

            Vec3 center = new Vec3(
                customData.getFloat("centerX"),
                customData.getFloat("centerY"),
                customData.getFloat("centerZ")
            );

            Vec3 axisX = new Vec3(
                customData.getFloat("axisX_X"),
                customData.getFloat("axisX_Y"),
                customData.getFloat("axisX_Z")
            ).normalize();

            Vec3 axisY = new Vec3(
                customData.getFloat("axisY_X"),
                customData.getFloat("axisY_Y"),
                customData.getFloat("axisY_Z")
            ).normalize();

            double a = customData.getDouble("a");
            double b = customData.getDouble("b");
            double w = customData.getDouble("w");

            double theta = Math.PI - w * time;

            Vec3 nextPos = center
                .add(axisX.scale(Math.cos(theta) * a))
                .add(axisY.scale(Math.sin(theta) * b));

            summon.setDeltaMovement(nextPos.subtract(summon.position()));

            Vec3 swordDir = nextPos.subtract(center);
            if(swordDir.lengthSqr() > 0.0001) {
                float[] xyRot = MathUtil.computeXYRot(swordDir.toVector3f());
                summon.setXRot(Mth.rotLerp(ROTATION_LERP2, summon.getXRot(), xyRot[0]));
                summon.setYRot(Mth.rotLerp(ROTATION_LERP2, summon.getYRot(), xyRot[1]));
            }

            AABB hitBox = new AABB(summon.position(), nextPos).inflate(HITBOX_INFLATE);
            List<Entity> targets = summon.level().getEntitiesOfClass(
                Entity.class,
                hitBox,
                FilterUtil.createTargetFilter(summon, player)
            );

            ItemStack sourceStack = summon.getEntityData().get(StaticSummon.STACK_SOURCE);
            for(Entity target : targets) {
                if(DamageUtil.summonAttack(summon, target, sourceStack, customData.contains("damage") ? customData.getFloat("damage") : DAMAGE.get(), 1.0f)) target.invulnerableTime = 15;
            }

            customData.putInt("rotateTime", time + 1);
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
        }

        int nextState = getNextState(summon, state);
        if(nextState != state) changeState(summon, state, nextState);
    }

    public static int getNextState(StaticSummon summon, int state) {
        if(state == State.IDLE.ordinal()) {
            Entity target = getStoredSummonTarget(summon);
            if(target != null) return State.FIGHT.ordinal();
            return State.IDLE.ordinal();
        }else if(state == State.FIGHT.ordinal()) {
            Entity target = getStoredSummonTarget(summon);
            if(target != null) {
                Vec3 distV = target.getBoundingBox().getCenter().subtract(summon.position());
                double dist = distV.length();
                if (dist < 16) {
                    if (summon.getRandom().nextInt(2) == 0) return State.DASH_PRE.ordinal();
                    else return State.ROTATE.ordinal();
                } else {
                    return State.DASH_PRE.ordinal();
                }
            }
            return State.IDLE.ordinal();
        }else if(state == State.DASH_PRE.ordinal()) {
            Entity target = getStoredSummonTarget(summon);
            if(target != null) {
                Vec3 targetDir = target.getBoundingBox().getCenter().subtract(summon.position()).normalize();
                double speed = summon.getDeltaMovement().length();
                if(speed < 0.05 && Math.abs(summon.getLookAngle().dot(targetDir) - 1) < 0.001) {
                    return State.DASH.ordinal();
                }
                return State.DASH_PRE.ordinal();
            }
            return State.FIGHT.ordinal();
        }else if(state == State.DASH.ordinal()) {
            double speed = summon.getDeltaMovement().length();
            if(speed < 0.05) {
                return State.FIGHT.ordinal();
            }
            return State.DASH.ordinal();
        }else if(state == State.ROTATE.ordinal()) {
            CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
            int time = customData.contains("rotateTime") ? customData.getInt("rotateTime") : 0;
            int duration = customData.getInt("duration");
            if(time >= duration) {
                return State.FIGHT.ordinal();
            }
            return State.ROTATE.ordinal();
        }
        return State.IDLE.ordinal();
    }

    public static void changeState(StaticSummon summon, int from, int to) {
        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA).copy();
        customData.putInt("state", to);
        if(to == State.DASH_PRE.ordinal()) {
            Entity target = getStoredSummonTarget(summon);
            if(target != null) {
                summon.setDeltaMovement(target.getBoundingBox().getCenter().subtract(summon.position()).normalize().scale(DASH_PRE_SPEED.get()));
            }
        }else if(to == State.DASH.ordinal()) {
            Entity target = getStoredSummonTarget(summon);
            if(target != null) {
                summon.setDeltaMovement(Vec3.ZERO);
                Vec3 distV = target.getBoundingBox().getCenter().subtract(summon.position());
                double dist = distV.length();
                int dashTime = (int) (dist / DASH_SPEED.get()) + DASH_MORE_TIME;
                if(summon.getRandom().nextInt(2) == 0) dashTime++;
                customData.putInt("dashTime", dashTime);
            }
        }else if(to == State.ROTATE.ordinal()) {
            Entity target = getStoredSummonTarget(summon);
            if(target != null) {
                Vec3 start = summon.position();
                Vec3 end = target.getBoundingBox().getCenter();
                Vector3f center = start.add(end).scale(0.5).toVector3f();
                Vec3 rotateDir = end.subtract(start);
                if(rotateDir.lengthSqr() < 1.0E-4D) {
                    rotateDir = summon.getLookAngle();
                }
                if(rotateDir.lengthSqr() < 1.0E-4D && summon.getOwner() != null) {
                    rotateDir = summon.getOwner().getLookAngle();
                }
                if(rotateDir.lengthSqr() < 1.0E-4D) {
                    rotateDir = new Vec3(0.0D, 0.0D, 1.0D);
                }
                Vector3f[] dirs = MathUtil.computeCoordinateSystem(rotateDir.toVector3f(), summon.getYRot());
                int rotationAngle = (int) ((Math.random() * 2 - 1) * 60);
                dirs = MathUtil.rotateCoordinateSystem(dirs[0], dirs[2], rotationAngle);
                double a = end.subtract(start).length() / 2;
                double b = Math.min(Math.random() * 4, a) + 1;

                double h = Math.pow(a - b, 2) / Math.pow(a + b, 2);
                double circumference = Math.PI * (a + b) * (1 + (3 * h) / (10 + Math.sqrt(4 - 3 * h)));
                double range = 0.5 + Math.random() * 0.5;
                double arcLength = circumference * range;
                int duration = (int)Math.ceil(arcLength / ROTATE_SPEED.get());
                duration = Mth.clamp(duration, 8, 18);
                double w = Math.PI * 2 * range / duration;
                if(summon.getRandom().nextInt(2) == 0) w = -w;

                customData.putFloat("axisX_X", dirs[0].x);
                customData.putFloat("axisX_Y", dirs[0].y);
                customData.putFloat("axisX_Z", dirs[0].z);
                customData.putFloat("axisY_X", dirs[2].x);
                customData.putFloat("axisY_Y", dirs[2].y);
                customData.putFloat("axisY_Z", dirs[2].z);
                customData.putFloat("centerX", center.x);
                customData.putFloat("centerY", center.y);
                customData.putFloat("centerZ", center.z);
                customData.putDouble("a", a);
                customData.putDouble("b", b);
                customData.putInt("duration", duration);
                customData.putDouble("w", w);
                customData.putInt("rotateTime", 0);
            }
        }
        summon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
    }

    private static void tickFollowOwner(StaticSummon summon) {
        if(summon.getOwner() == null) return;
        CompoundTag customData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA);
        int idx = customData.contains("idx") ? customData.getInt("idx") : 0;

        Vector3f[] dirs = MathUtil.computeCoordinateSystem(summon.getOwner());

        Vec3 hDir = new Vec3(dirs[0].x, 0, dirs[0].z).normalize();
        if(dirs[0].y > 0.999) {
            hDir = new Vec3(-dirs[1].x, 0, -dirs[1].z);
        }else if(dirs[0].y < -0.999) {
            hDir = new Vec3(dirs[1].x, 0, dirs[1].z);
        }

        //剑的朝向，向下
        Vector3f tipDir = new Vector3f(0, -1, 0);
        Vector3f upDir = hDir.toVector3f();
        Quaternionf rotation = new Quaternionf().fromAxisAngleDeg(dirs[2], (idx + 1) * 10);
        tipDir.rotate(rotation);
        upDir.rotate(rotation);
        float[] targetXYRot = MathUtil.computeXYRot(tipDir, upDir);
        summon.setXRot(Mth.rotLerp(ROTATION_LERP, summon.getXRot(), targetXYRot[0]));
        summon.setYRot(Mth.rotLerp(ROTATION_LERP, summon.getYRot(), targetXYRot[1]));

        //剑的位置，基于水平方向
        Vec3 basePos = summon.getOwner().getBoundingBox().getCenter();
        Vec3 pos = basePos.add(hDir.scale(-(idx + 1) * 0.25 - 1));
        Vec3 dist = pos.subtract(summon.position());
        double followSpeed = FOLLOW_SPEED.get();
        if(dist.length() > followSpeed) {
            summon.setDeltaMovement(dist.normalize().scale(Math.min(followSpeed, dist.length())));
        }else {
            summon.setDeltaMovement(Vec3.ZERO);
            summon.setPos(pos);
        }
    }

    private static float[] computeCrystalColor(float time, int idx) {
        float hue = (time * 0.015F + idx * 0.12F) % 1.0F;
        int rgb = Mth.hsvToRgb(hue, 0.85F, 1.0F);
        float rainbowR = ((rgb >> 16) & 255) / 255.0F;
        float rainbowG = ((rgb >> 8) & 255) / 255.0F;
        float rainbowB = (rgb & 255) / 255.0F;
        float tint = 0.35F;
        float pulse = 0.92F + 0.08F * Mth.sin(time * 0.12F + idx * 0.7F);

        return new float[]{
            (1.0F * (1.0F - tint) + rainbowR * tint) * pulse,
            (1.0F * (1.0F - tint) + rainbowG * tint) * pulse,
            (1.0F * (1.0F - tint) + rainbowB * tint) * pulse
        };
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(stack.is(ModItems.TERRAPRISMA.get()) && level instanceof ServerLevel serverLevel) {
            StaticSummon summon = new StaticSummon(ModEntities.STATIC_SUMMON.get(), level);
            summon.setOwner(player);
            summon.getEntityData().set(StaticSummon.STACK_SOURCE, stack.copy());
            summon.setPos(player.position());
            summon.getEntityData().set(StaticSummon.BEHAVIOR, StaticSummonBehaviors.TERRAPRISMA);
            summon.getEntityData().set(StaticSummon.RENDER_MODE, "custom");
            summon.getEntityData().set(StaticSummon.ITEM, new ItemStack(ModItems.TERRAPRISMA_SUMMON.get()));
            summon.getEntityData().set(StaticSummon.SCALE_X, 2.0f);
            summon.getEntityData().set(StaticSummon.SCALE_Y, 1.0f);
            summon.getEntityData().set(StaticSummon.SCALE_Z, 1.0f);
            summon.getEntityData().set(StaticSummon.LIFETIME, 72000);
            summon.getEntityData().set(StaticSummon.GLOW, true);
            CompoundTag summonData = summon.getEntityData().get(StaticSummon.CUSTOM_DATA).copy();
            summonData.putFloat("damage", DAMAGE.get());
            summon.getEntityData().set(StaticSummon.CUSTOM_DATA, summonData);

            summon.setNoGravity(true);
            summon.noPhysics = true;

            MinionWeapon.addFreshSummon(player, StaticSummonBehaviors.TERRAPRISMA, List.of(summon));

            int idx = 0;
            for(PlayerSummon.SummonSlot slot : player.getData(ModAttachments.PLAYER_SUMMON).getMinionSlots()) {
                if(!slot.getId().equals(StaticSummonBehaviors.TERRAPRISMA)) continue;
                for(UUID uuid : slot.getSummons()) {
                    Entity entity = serverLevel.getEntity(uuid);
                    if(entity instanceof StaticSummon terraSummon) {
                        if(terraSummon.isAlive()) {
                            CompoundTag customData = terraSummon.getEntityData().get(StaticSummon.CUSTOM_DATA).copy();
                            customData.putInt("idx", idx);
                            terraSummon.getEntityData().set(StaticSummon.CUSTOM_DATA, customData);
                            idx++;
                        }
                    }
                }
            }
        }
        SoundUtil.playClientSound(player, ModSounds.SUMMON_TERRAPRISMA.get());
        return InteractionResultHolder.consume(stack);
    }
}
