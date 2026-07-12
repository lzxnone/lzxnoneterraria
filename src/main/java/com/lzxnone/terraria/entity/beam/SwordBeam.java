package com.lzxnone.terraria.entity.beam;

import com.lzxnone.terraria.utils.FilterUtil;
import com.lzxnone.terraria.utils.MathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SwordBeam extends Entity {
    public static final float HALF_WIDTH = 30.0f;
    public static final float HALF_HEIGHT = 72.0f;
    public static final float HALF_THICKNESS = 0.25f;
    public static final float SCALE = 0.05f;
    public static final float DIST = HALF_WIDTH * SCALE;

    public static final EntityDataAccessor<String> BEHAVIOR =
            SynchedEntityData.defineId(SwordBeam.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> ROTATE =
            SynchedEntityData.defineId(SwordBeam.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Vector3f> COLOR0 =
            SynchedEntityData.defineId(SwordBeam.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Vector3f> COLOR1 =
            SynchedEntityData.defineId(SwordBeam.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Vector3f> COLOR2 =
            SynchedEntityData.defineId(SwordBeam.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Vector3f> COLOR3 =
            SynchedEntityData.defineId(SwordBeam.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Boolean> RIGHT =
            SynchedEntityData.defineId(SwordBeam.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> INFLATE =
            SynchedEntityData.defineId(SwordBeam.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> AGE =
            SynchedEntityData.defineId(SwordBeam.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LIFETIME =
            SynchedEntityData.defineId(SwordBeam.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Optional<UUID>> OWNER =
            SynchedEntityData.defineId(SwordBeam.class, EntityDataSerializers.OPTIONAL_UUID);
    public static final EntityDataAccessor<CompoundTag> CUSTOM_DATA =
            SynchedEntityData.defineId(SwordBeam.class, EntityDataSerializers.COMPOUND_TAG);

    private Entity owner = null;

    public Vec3 prevPosition = null;
    public Vec3 currentPosition = null;
    public Vector3f[] dirs = null;

    public SwordBeam(EntityType<Entity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    public void setOwner(Entity entity) {
        if(entity == null) return;
        this.entityData.set(OWNER, Optional.of(entity.getUUID()));
        this.owner = entity;
    }

    public Entity getOwner() {
        if(owner == null) {
            if(this.entityData.get(OWNER).orElse(null) != null) {
                UUID uuid = this.entityData.get(OWNER).orElse(null);
                if(!this.level().isClientSide()) {
                    setOwner(((ServerLevel) this.level()).getEntity(uuid));
                }else {
                    for(Entity entity : this.level().getEntities(this, this.getBoundingBox().inflate(64.0))) {
                        if(entity.getUUID().equals(uuid)) {
                            setOwner(entity);
                        }
                    }
                }
            }
        }
        return owner;
    }

    @Override
    public void tick() {
        super.tick();

        if(!this.level().isClientSide()) {
            if(this.getEntityData().get(AGE) >= this.getEntityData().get(LIFETIME)) {
                SwordBeamBehaviors.getBehavior(this.entityData.get(BEHAVIOR)).onDied(this);
                return;
            }
            this.getEntityData().set(AGE, this.getEntityData().get(AGE) + 1);
        }

        if(this.getOwner() != null) {
            this.setPos(this.getOwner().getX(), this.getOwner().getY() + this.getOwner().getBbHeight() * 0.5, this.getOwner().getZ());
        }else {
            SwordBeamBehaviors.getBehavior(this.entityData.get(BEHAVIOR)).onDied(this);
            return;
        }

        SwordBeamBehaviors.getBehavior(this.entityData.get(BEHAVIOR)).onMoving(this);
        this.checkCollision();
    }

    public void checkCollision() {
        Entity owner = getOwner();
        float progress = (float) this.entityData.get(AGE) / (float) this.entityData.get(LIFETIME);
        if(progress > 1.0f) return;
        if(this.entityData.get(RIGHT)) progress = 1.0f - progress;

        this.dirs = MathUtil.computeCoordinateSystem(owner);
        this.dirs = MathUtil.rotateCoordinateSystem(dirs[0], dirs[2], this.entityData.get(ROTATE));
        Vector3f dir = dirs[0];
        Vector3f up = dirs[1];
        Vector3f right = dirs[2];

        float cos = (float) Math.cos(progress * Math.PI);
        float sin = (float) Math.sin(progress * Math.PI);
        Vector3f current = new Vector3f(
            cos * right.x  + sin * dir.x,
            cos * right.y  + sin * dir.y,
            cos * right.z  + sin * dir.z
        );

        double centerX = this.getX() + current.x() * DIST;
        double centerY = this.getY() + current.y() * DIST;
        double centerZ = this.getZ() + current.z() * DIST;

        this.currentPosition = new Vec3(centerX, centerY, centerZ);

        float[] xyRot = MathUtil.computeXYRot(dirs[0], dirs[1]);

        float angle = (float) ((0.5 - progress) * Math.PI);
        Quaternionf rotation = new Quaternionf()
            .fromAxisAngleRad(dir, (float) Math.toRadians(Math.abs(dirs[0].y) > 0.999 ? 0 : this.entityData.get(ROTATE)))
            .rotateY((float) Math.toRadians(-xyRot[1]))
            .rotateX((float) Math.toRadians(xyRot[0]))
            .rotateX((float) Math.toRadians(-90.0))
            .rotateZ((float) Math.toRadians(-90.0));
        Quaternionf rotation2 = new Quaternionf()
            .fromAxisAngleRad(up, angle);

        Vector3f axisX = new Vector3f(1, 0, 0).rotate(rotation).rotate(rotation2);
        Vector3f axisY = new Vector3f(0, 1, 0).rotate(rotation).rotate(rotation2);
        Vector3f axisZ = new Vector3f(0, 0, 1).rotate(rotation).rotate(rotation2);

        float extX = HALF_WIDTH * SCALE * Math.abs(axisX.x()) + HALF_HEIGHT * SCALE * Math.abs(axisY.x()) + HALF_THICKNESS * SCALE * Math.abs(axisZ.x());
        float extY = HALF_WIDTH * SCALE * Math.abs(axisX.y()) + HALF_HEIGHT * SCALE * Math.abs(axisY.y()) + HALF_THICKNESS * SCALE * Math.abs(axisZ.y());
        float extZ = HALF_WIDTH * SCALE * Math.abs(axisX.z()) + HALF_HEIGHT * SCALE * Math.abs(axisY.z()) + HALF_THICKNESS * SCALE * Math.abs(axisZ.z());

        AABB hitBox = new AABB(
            centerX - extX, centerY - extY, centerZ - extZ,
            centerX + extX, centerY + extY, centerZ + extZ
        ).inflate(this.getEntityData().get(SwordBeam.INFLATE));
        this.setBoundingBox(hitBox);

        List<Entity> targets = this.level().getEntities(this, hitBox, FilterUtil.createTargetFilter(this, this.getOwner()));

        for(Entity target : targets) SwordBeamBehaviors.getBehavior(this.entityData.get(BEHAVIOR)).onHitEntity(this, new EntityHitResult(target, currentPosition));

        if(this.prevPosition == null) return;

        ClipContext context = new ClipContext(
            this.prevPosition,
            this.currentPosition,
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            this
        );

        BlockHitResult blockHit = this.level().clip(context);

        if(blockHit.getType() != HitResult.Type.MISS) {
            SwordBeamBehaviors.getBehavior(this.entityData.get(BEHAVIOR)).onHitBlock(this, blockHit);
        }

        this.prevPosition = this.currentPosition;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(BEHAVIOR, "default");
        builder.define(ROTATE, 0);
        builder.define(RIGHT, false);
        builder.define(INFLATE, 0.0f);
        builder.define(COLOR0, new Vector3f(1.0f, 1.0f, 1.0f));
        builder.define(COLOR1, new Vector3f(1.0f, 1.0f, 1.0f));
        builder.define(COLOR2, new Vector3f(1.0f, 1.0f, 1.0f));
        builder.define(COLOR3, new Vector3f(1.0f, 1.0f, 1.0f));
        builder.define(AGE, 0);
        builder.define(LIFETIME, 10);
        builder.define(OWNER, Optional.empty());
        builder.define(CUSTOM_DATA, new CompoundTag());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("behavior", this.entityData.get(BEHAVIOR));
        tag.putInt("rotate", this.entityData.get(ROTATE));
        tag.putBoolean("right", this.entityData.get(RIGHT));
        tag.putFloat("inflate", this.entityData.get(INFLATE));
        Vector3f c0 = this.entityData.get(COLOR0);
        tag.putFloat("color0R", c0.x);
        tag.putFloat("color0G", c0.y);
        tag.putFloat("color0B", c0.z);
        Vector3f c1 = this.entityData.get(COLOR1);
        tag.putFloat("color1R", c1.x);
        tag.putFloat("color1G", c1.y);
        tag.putFloat("color1B", c1.z);
        Vector3f c2 = this.entityData.get(COLOR2);
        tag.putFloat("color2R", c2.x);
        tag.putFloat("color2G", c2.y);
        tag.putFloat("color2B", c2.z);
        Vector3f c3 = this.entityData.get(COLOR3);
        tag.putFloat("color3R", c3.x);
        tag.putFloat("color3G", c3.y);
        tag.putFloat("color3B", c3.z);
        tag.putInt("age", this.entityData.get(AGE));
        tag.putInt("lifetime", this.entityData.get(LIFETIME));
        if(getOwner() != null) tag.putUUID("owner", getOwner().getUUID());
        tag.put("customData", this.entityData.get(CUSTOM_DATA));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if(tag.contains("behavior")) this.entityData.set(BEHAVIOR, tag.getString("behavior"));
        if(tag.contains("rotate")) this.entityData.set(ROTATE, tag.getInt("rotate"));
        if(tag.contains("right")) this.entityData.set(RIGHT, tag.getBoolean("right"));
        if(tag.contains("inflate")) this.entityData.set(INFLATE, tag.getFloat("inflate"));
        if(tag.contains("color0R")) {
            this.entityData.set(COLOR0, new Vector3f(
                tag.getFloat("color0R"), tag.getFloat("color0G"), tag.getFloat("color0B")
            ));
        }
        if(tag.contains("color1R")) {
            this.entityData.set(COLOR1, new Vector3f(
                tag.getFloat("color1R"), tag.getFloat("color1G"), tag.getFloat("color1B")
            ));
        }
        if(tag.contains("color2R")) {
            this.entityData.set(COLOR2, new Vector3f(
                tag.getFloat("color2R"), tag.getFloat("color2G"), tag.getFloat("color2B")
            ));
        }
        if(tag.contains("color3R")) {
            this.entityData.set(COLOR3, new Vector3f(
                tag.getFloat("color3R"), tag.getFloat("color3G"), tag.getFloat("color3B")
            ));
        }
        if(tag.contains("age")) this.entityData.set(AGE, tag.getInt("age"));
        if(tag.contains("lifetime")) this.entityData.set(LIFETIME, tag.getInt("lifetime"));
        if(tag.contains("owner")) {
            this.entityData.set(OWNER, Optional.of(tag.getUUID("owner")));
            getOwner();
        }
        if(tag.contains("customData")) this.entityData.set(CUSTOM_DATA, tag.getCompound("customData"));
    }
}
