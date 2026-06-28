package com.lzxnone.terraria.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.joml.Vector3f;


public class TextureProjectile extends Projectile {
    public static final EntityDataAccessor<String> BEHAVIOR =
            SynchedEntityData.defineId(TextureProjectile.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<ItemStack> ITEM =
            SynchedEntityData.defineId(TextureProjectile.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<Vector3f> ORIGIN =
            SynchedEntityData.defineId(TextureProjectile.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Vector3f> DIRECTION =
            SynchedEntityData.defineId(TextureProjectile.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Vector3f> UP =
            SynchedEntityData.defineId(TextureProjectile.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Vector3f> RIGHT =
            SynchedEntityData.defineId(TextureProjectile.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Float> SCALE_X =
            SynchedEntityData.defineId(TextureProjectile.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> SCALE_Y =
            SynchedEntityData.defineId(TextureProjectile.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> RXP =
            SynchedEntityData.defineId(TextureProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> RYP =
            SynchedEntityData.defineId(TextureProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> RZP =
            SynchedEntityData.defineId(TextureProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> RXPS =
            SynchedEntityData.defineId(TextureProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> RYPS =
            SynchedEntityData.defineId(TextureProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> RZPS =
            SynchedEntityData.defineId(TextureProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> GLOW =
            SynchedEntityData.defineId(TextureProjectile.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> AGE =
            SynchedEntityData.defineId(TextureProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LIFETIME =
            SynchedEntityData.defineId(TextureProjectile.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<String> EXPRESSION_X =
            SynchedEntityData.defineId(TextureProjectile.class, EntityDataSerializers.STRING); //右
    public static final EntityDataAccessor<String> EXPRESSION_Y =
            SynchedEntityData.defineId(TextureProjectile.class, EntityDataSerializers.STRING); //上
    public static final EntityDataAccessor<String> EXPRESSION_Z =
            SynchedEntityData.defineId(TextureProjectile.class, EntityDataSerializers.STRING); //前

    public static final EntityDataAccessor<CompoundTag> CUSTOM_DATA =
            SynchedEntityData.defineId(TextureProjectile.class, EntityDataSerializers.COMPOUND_TAG);


    public boolean isInit = false;

    public Expression exprX;
    public Expression exprY;
    public Expression exprZ;

    public Vec3 prevPos;
    public Vec3 prevDeltaMovement;

    public boolean positionOverridden = false;

    public TextureProjectile(EntityType<TextureProjectile> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(BEHAVIOR, "default");
        builder.define(ITEM, ItemStack.EMPTY);
        builder.define(ORIGIN, new Vector3f(0, 0, 0));
        builder.define(DIRECTION, new Vector3f(0, 0, 1));
        builder.define(UP, new Vector3f(0, 1, 0));
        builder.define(RIGHT, new Vector3f(1, 0, 0));
        builder.define(SCALE_X, 1.0f);
        builder.define(SCALE_Y, 1.0f);
        builder.define(RXP, 0);
        builder.define(RYP, 0);
        builder.define(RZP, 0);
        builder.define(RXPS, 0);
        builder.define(RYPS, 0);
        builder.define(RZPS, 0);
        builder.define(GLOW, false);
        builder.define(AGE, 0);
        builder.define(LIFETIME, 60);
        builder.define(EXPRESSION_X, "0");
        builder.define(EXPRESSION_Y, "0");
        builder.define(EXPRESSION_Z, "0");
        builder.define(CUSTOM_DATA, new CompoundTag());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if(!isInit) return;
        if(key.equals(TextureProjectile.EXPRESSION_Y) && exprY != null) {
            this.exprY = new ExpressionBuilder(this.entityData.get(TextureProjectile.EXPRESSION_Y)).variables("t").build();
        }else if (key.equals(TextureProjectile.EXPRESSION_Z) && exprZ != null) {
            this.exprZ = new ExpressionBuilder(this.entityData.get(TextureProjectile.EXPRESSION_Z)).variables("t").build();
        }else if (key.equals(TextureProjectile.EXPRESSION_X) && exprX != null) {
            this.exprX = new ExpressionBuilder(this.entityData.get(TextureProjectile.EXPRESSION_X)).variables("t").build();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(!isInit) {
            this.exprX = new ExpressionBuilder(this.entityData.get(EXPRESSION_X)).variables("t").build();
            this.exprY = new ExpressionBuilder(this.entityData.get(EXPRESSION_Y)).variables("t").build();
            this.exprZ = new ExpressionBuilder(this.entityData.get(EXPRESSION_Z)).variables("t").build();
            isInit = true;
        }

        this.prevPos = this.position();
        this.prevDeltaMovement = this.getDeltaMovement();
        double valX =  this.exprX.setVariable("t", this.entityData.get(AGE)).evaluate();
        double valY =  this.exprY.setVariable("t", this.entityData.get(AGE)).evaluate();
        double valZ =  this.exprZ.setVariable("t", this.entityData.get(AGE)).evaluate();
        Vector3f origin = this.entityData.get(ORIGIN);
        Vector3f right = this.entityData.get(RIGHT);
        Vector3f up = this.entityData.get(UP);
        Vector3f dir = this.entityData.get(DIRECTION);
        double newX = origin.x + (valX * right.x) + (valY * up.x) + (valZ * dir.x);
        double newY = origin.y + (valX * right.y) + (valY * up.y) + (valZ * dir.y);
        double newZ = origin.z + (valX * right.z) + (valY * up.z) + (valZ * dir.z);

        Vec3 nextPos = new Vec3(newX, newY, newZ);
        this.setDeltaMovement(nextPos.subtract(this.position()));

        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if(hitResult.getType() != HitResult.Type.MISS) {
            this.onHit(hitResult);
        }

        if(!this.positionOverridden) {
            this.setPos(nextPos);
        }
        this.positionOverridden = false;
        ProjectileBehaviors.getBehavior(this.entityData.get(BEHAVIOR)).onMoving(this);

        if(this.entityData.get(AGE) > this.entityData.get(LIFETIME)) {
            ProjectileBehaviors.getBehavior(this.entityData.get(BEHAVIOR)).onDied(this);
        }
        this.entityData.set(AGE, this.entityData.get(AGE) + 1);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("behavior", this.entityData.get(BEHAVIOR));
        ItemStack stack = this.entityData.get(ITEM);
        if(!stack.isEmpty()) tag.put("item", stack.save(this.level().registryAccess()));
        tag.putFloat("originX", this.entityData.get(ORIGIN).x);
        tag.putFloat("originY", this.entityData.get(ORIGIN).y);
        tag.putFloat("originZ", this.entityData.get(ORIGIN).z);
        tag.putFloat("dirX", this.entityData.get(DIRECTION).x);
        tag.putFloat("dirY", this.entityData.get(DIRECTION).y);
        tag.putFloat("dirZ", this.entityData.get(DIRECTION).z);
        tag.putFloat("upX", this.entityData.get(UP).x);
        tag.putFloat("upY", this.entityData.get(UP).y);
        tag.putFloat("upZ", this.entityData.get(UP).z);
        tag.putFloat("rightX", this.entityData.get(RIGHT).x);
        tag.putFloat("rightY", this.entityData.get(RIGHT).y);
        tag.putFloat("rightZ", this.entityData.get(RIGHT).z);
        tag.putFloat("scaleX", this.entityData.get(SCALE_X));
        tag.putFloat("scaleY", this.entityData.get(SCALE_Y));
        tag.putInt("rxp", this.entityData.get(RXP));
        tag.putInt("ryp", this.entityData.get(RYP));
        tag.putInt("rzp", this.entityData.get(RZP));
        tag.putInt("rxps", this.entityData.get(RXPS));
        tag.putInt("ryps", this.entityData.get(RYPS));
        tag.putInt("rzps", this.entityData.get(RZPS));
        tag.putBoolean("glow", this.entityData.get(GLOW));
        tag.putInt("age", this.entityData.get(AGE));
        tag.putInt("lifetime", this.entityData.get(LIFETIME));
        tag.putString("expressionX", this.entityData.get(EXPRESSION_X));
        tag.putString("expressionY", this.entityData.get(EXPRESSION_Y));
        tag.putString("expressionZ", this.entityData.get(EXPRESSION_Z));
        tag.put("customData", this.entityData.get(CUSTOM_DATA));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if(tag.contains("behavior")) this.entityData.set(BEHAVIOR, tag.getString("behavior"));
        if(tag.contains("item")) {
            ItemStack.parse(this.level().registryAccess(), tag.getCompound("item"))
                    .ifPresent(stack -> this.entityData.set(ITEM, stack));
        }
        Vector3f origin = new Vector3f();
        if(tag.contains("originX")) origin.x = tag.getFloat("originX");
        if(tag.contains("originY")) origin.y = tag.getFloat("originY");
        if(tag.contains("originZ")) origin.z = tag.getFloat("originZ");
        this.entityData.set(ORIGIN, origin);
        Vector3f dir = new Vector3f();
        if(tag.contains("dirX")) dir.x = tag.getFloat("dirX");
        if(tag.contains("dirY")) dir.y = tag.getFloat("dirY");
        if(tag.contains("dirZ")) dir.z = tag.getFloat("dirZ");
        this.entityData.set(DIRECTION, dir);
        Vector3f up = this.entityData.get(UP);
        tag.putFloat("upX", up.x);
        tag.putFloat("upY", up.y);
        tag.putFloat("upZ", up.z);
        this.entityData.set(UP, up);
        Vector3f right = this.entityData.get(RIGHT);
        tag.putFloat("rightX", right.x);
        tag.putFloat("rightY", right.y);
        tag.putFloat("rightZ", right.z);
        this.entityData.set(RIGHT, right);
        if(tag.contains("scaleX")) this.entityData.set(SCALE_X, tag.getFloat("scaleX"));
        if(tag.contains("scaleY")) this.entityData.set(SCALE_Y, tag.getFloat("scaleY"));
        if(tag.contains("rxp")) this.entityData.set(RXP, tag.getInt("rxp"));
        if(tag.contains("ryp")) this.entityData.set(RYP, tag.getInt("ryp"));
        if(tag.contains("rzp")) this.entityData.set(RZP, tag.getInt("rzp"));
        if(tag.contains("rxps")) this.entityData.set(RXPS, tag.getInt("rxps"));
        if(tag.contains("ryps")) this.entityData.set(RYPS, tag.getInt("ryps"));
        if(tag.contains("rzps")) this.entityData.set(RZPS, tag.getInt("rzps"));
        if(tag.contains("glow")) this.entityData.set(GLOW, tag.getBoolean("glow"));
        if(tag.contains("age")) this.entityData.set(AGE, tag.getInt("age"));
        if(tag.contains("lifetime")) this.entityData.set(LIFETIME, tag.getInt("lifetime"));
        if(tag.contains("expressionX")) this.entityData.set(EXPRESSION_X, tag.getString("expressionX"));
        if(tag.contains("expressionY")) this.entityData.set(EXPRESSION_Y, tag.getString("expressionY"));
        if(tag.contains("expressionZ")) this.entityData.set(EXPRESSION_Z, tag.getString("expressionZ"));
        if(tag.contains("customData")) this.entityData.set(CUSTOM_DATA, tag.getCompound("customData"));
    }

    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements) {}

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        if(owner != null && target.getUUID().equals(owner.getUUID())) return;
        ProjectileBehaviors.getBehavior(this.entityData.get(BEHAVIOR)).onHitEntity(this, result);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        ProjectileBehaviors.getBehavior(this.entityData.get(BEHAVIOR)).onHitBlock(this, result);
    }
}
