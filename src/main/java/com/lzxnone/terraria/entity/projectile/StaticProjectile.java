package com.lzxnone.terraria.entity.projectile;

import com.lzxnone.terraria.entity.summon.StaticSummon;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.joml.Vector3f;


public class StaticProjectile extends Projectile {
    public static final EntityDataAccessor<String> BEHAVIOR =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> RENDER_MODE =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<ItemStack> ITEM =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<BlockState> BLOCK =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.BLOCK_STATE);
    public static final EntityDataAccessor<Vector3f> ORIGIN =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Vector3f> DIRECTION =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Vector3f> UP =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Vector3f> RIGHT =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Float> SCALE_X =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> SCALE_Y =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> SCALE_Z =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> RXP =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> RYP =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> RZP =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> RXPS =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> RYPS =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> RZPS =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> COLOR_R =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> COLOR_G =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> COLOR_B =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> COLOR_A =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> GLOW =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> AGE =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LIFETIME =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<String> EXPRESSION_X =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.STRING); //右
    public static final EntityDataAccessor<String> EXPRESSION_Y =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.STRING); //上
    public static final EntityDataAccessor<String> EXPRESSION_Z =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.STRING); //前

    public static final EntityDataAccessor<CompoundTag> CUSTOM_DATA =
            SynchedEntityData.defineId(StaticProjectile.class, EntityDataSerializers.COMPOUND_TAG);

    public boolean isInit = false;

    public Expression exprX;
    public Expression exprY;
    public Expression exprZ;

    public Vec3 prevPos;
    public Vec3 prevDeltaMovement;

    public boolean positionOverridden = false;

    public StaticProjectile(EntityType<StaticProjectile> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(BEHAVIOR, "default");
        builder.define(RENDER_MODE, "custom");
        builder.define(ITEM, ItemStack.EMPTY);
        builder.define(BLOCK, Blocks.AIR.defaultBlockState());
        builder.define(ORIGIN, new Vector3f(0, 0, 0));
        builder.define(DIRECTION, new Vector3f(0, 0, 1));
        builder.define(UP, new Vector3f(0, 1, 0));
        builder.define(RIGHT, new Vector3f(1, 0, 0));
        builder.define(SCALE_X, 1.0f);
        builder.define(SCALE_Y, 1.0f);
        builder.define(SCALE_Z, 1.0f);
        builder.define(RXP, 0);
        builder.define(RYP, 0);
        builder.define(RZP, 0);
        builder.define(RXPS, 0);
        builder.define(RYPS, 0);
        builder.define(RZPS, 0);
        builder.define(COLOR_R, 1.0f);
        builder.define(COLOR_G, 1.0f);
        builder.define(COLOR_B, 1.0f);
        builder.define(COLOR_A, 1.0f);
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
        if(key.equals(StaticProjectile.EXPRESSION_Y) && exprY != null) {
            this.exprY = new ExpressionBuilder(this.entityData.get(StaticProjectile.EXPRESSION_Y)).variables("t").build();
        }else if (key.equals(StaticProjectile.EXPRESSION_Z) && exprZ != null) {
            this.exprZ = new ExpressionBuilder(this.entityData.get(StaticProjectile.EXPRESSION_Z)).variables("t").build();
        }else if (key.equals(StaticProjectile.EXPRESSION_X) && exprX != null) {
            this.exprX = new ExpressionBuilder(this.entityData.get(StaticProjectile.EXPRESSION_X)).variables("t").build();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.isInit) {
            this.exprX = new ExpressionBuilder(this.getEntityData().get(StaticProjectile.EXPRESSION_X)).variables("t").build();
            this.exprY = new ExpressionBuilder(this.getEntityData().get(StaticProjectile.EXPRESSION_Y)).variables("t").build();
            this.exprZ = new ExpressionBuilder(this.getEntityData().get(StaticProjectile.EXPRESSION_Z)).variables("t").build();
            this.isInit = true;
        }

        this.prevPos = this.position();
        this.prevDeltaMovement = this.getDeltaMovement();
        double valX =  this.exprX.setVariable("t", this.getEntityData().get(StaticProjectile.AGE)).evaluate();
        double valY =  this.exprY.setVariable("t", this.getEntityData().get(StaticProjectile.AGE)).evaluate();
        double valZ =  this.exprZ.setVariable("t", this.getEntityData().get(StaticProjectile.AGE)).evaluate();
        Vector3f origin = this.getEntityData().get(StaticProjectile.ORIGIN);
        Vector3f right = this.getEntityData().get(StaticProjectile.RIGHT);
        Vector3f up = this.getEntityData().get(StaticProjectile.UP);
        Vector3f dir = this.getEntityData().get(StaticProjectile.DIRECTION);
        double newX = origin.x + (valX * right.x) + (valY * up.x) + (valZ * dir.x);
        double newY = origin.y + (valX * right.y) + (valY * up.y) + (valZ * dir.y);
        double newZ = origin.z + (valX * right.z) + (valY * up.z) + (valZ * dir.z);

        Vec3 nextPos = new Vec3(newX, newY, newZ);
        this.setDeltaMovement(nextPos.subtract(this.position()));

        if(!this.positionOverridden) {
            this.setPos(nextPos);
        }
        this.positionOverridden = false;

        StaticProjectileBehaviors.getBehavior(this.entityData.get(BEHAVIOR)).onMoving(this);

        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if(hitResult.getType() != HitResult.Type.MISS) {
            this.onHit(hitResult);
        }

        if(this.entityData.get(AGE) > this.entityData.get(LIFETIME)) {
            StaticProjectileBehaviors.getBehavior(this.entityData.get(BEHAVIOR)).onDied(this);
        }
        this.entityData.set(AGE, this.entityData.get(AGE) + 1);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("behavior", this.entityData.get(BEHAVIOR));
        tag.putString("renderMode", this.entityData.get(RENDER_MODE));
        ItemStack stack = this.entityData.get(ITEM);
        if(!stack.isEmpty()) tag.put("item", stack.save(this.level().registryAccess()));
        tag.put("block", NbtUtils.writeBlockState(this.entityData.get(BLOCK)));
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
        tag.putFloat("scaleZ", this.entityData.get(SCALE_Z));
        tag.putInt("rxp", this.entityData.get(RXP));
        tag.putInt("ryp", this.entityData.get(RYP));
        tag.putInt("rzp", this.entityData.get(RZP));
        tag.putInt("rxps", this.entityData.get(RXPS));
        tag.putInt("ryps", this.entityData.get(RYPS));
        tag.putInt("rzps", this.entityData.get(RZPS));
        tag.putFloat("colorR", this.entityData.get(COLOR_R));
        tag.putFloat("colorG", this.entityData.get(COLOR_G));
        tag.putFloat("colorB", this.entityData.get(COLOR_B));
        tag.putFloat("colorA", this.entityData.get(COLOR_A));
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
        if(tag.contains("renderMode")) this.entityData.set(RENDER_MODE, tag.getString("renderMode"));
        if(tag.contains("item")) {
            ItemStack.parse(this.level().registryAccess(), tag.getCompound("item"))
                    .ifPresent(stack -> this.entityData.set(ITEM, stack));
        }
        if(tag.contains("block")) {
            BlockState blockState = NbtUtils.readBlockState(
                this.level().holderLookup(Registries.BLOCK),
                tag.getCompound("block")
            );
            this.entityData.set(BLOCK, blockState);
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
        if(tag.contains("upX")) up.x = tag.getFloat("upX");
        if(tag.contains("upY")) up.y = tag.getFloat("upY");
        if(tag.contains("upZ")) up.z = tag.getFloat("upZ");
        this.entityData.set(UP, up);
        Vector3f right = this.entityData.get(RIGHT);
        if(tag.contains("rightX")) right.x = tag.getFloat("rightX");
        if(tag.contains("rightY")) right.y = tag.getFloat("rightY");
        if(tag.contains("rightZ")) right.z = tag.getFloat("rightZ");
        this.entityData.set(RIGHT, right);
        if(tag.contains("scaleX")) this.entityData.set(SCALE_X, tag.getFloat("scaleX"));
        if(tag.contains("scaleY")) this.entityData.set(SCALE_Y, tag.getFloat("scaleY"));
        if(tag.contains("scaleZ")) this.entityData.set(SCALE_Z, tag.getFloat("scaleZ"));
        if(tag.contains("rxp")) this.entityData.set(RXP, tag.getInt("rxp"));
        if(tag.contains("ryp")) this.entityData.set(RYP, tag.getInt("ryp"));
        if(tag.contains("rzp")) this.entityData.set(RZP, tag.getInt("rzp"));
        if(tag.contains("rxps")) this.entityData.set(RXPS, tag.getInt("rxps"));
        if(tag.contains("ryps")) this.entityData.set(RYPS, tag.getInt("ryps"));
        if(tag.contains("rzps")) this.entityData.set(RZPS, tag.getInt("rzps"));
        if(tag.contains("colorR")) this.entityData.set(COLOR_R, tag.getFloat("colorR"));
        if(tag.contains("colorG")) this.entityData.set(COLOR_G, tag.getFloat("colorG"));
        if(tag.contains("colorB")) this.entityData.set(COLOR_B, tag.getFloat("colorB"));
        if(tag.contains("colorA")) this.entityData.set(COLOR_A, tag.getFloat("colorA"));
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
        StaticProjectileBehaviors.getBehavior(this.entityData.get(BEHAVIOR)).onHitEntity(this, result);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        StaticProjectileBehaviors.getBehavior(this.entityData.get(BEHAVIOR)).onHitBlock(this, result);
    }
}
