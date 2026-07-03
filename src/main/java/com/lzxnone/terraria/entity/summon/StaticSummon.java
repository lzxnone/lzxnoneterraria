package com.lzxnone.terraria.entity.summon;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class StaticSummon extends Entity {
    public static final EntityDataAccessor<String> BEHAVIOR =
            SynchedEntityData.defineId(StaticSummon.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> RENDER_MODE =
            SynchedEntityData.defineId(StaticSummon.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<ItemStack> ITEM =
            SynchedEntityData.defineId(StaticSummon.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<BlockState> BLOCK =
            SynchedEntityData.defineId(StaticSummon.class, EntityDataSerializers.BLOCK_STATE);
    public static final EntityDataAccessor<Float> SCALE_X =
            SynchedEntityData.defineId(StaticSummon.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> SCALE_Y =
            SynchedEntityData.defineId(StaticSummon.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> SCALE_Z =
            SynchedEntityData.defineId(StaticSummon.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> RXP =
            SynchedEntityData.defineId(StaticSummon.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> RYP =
            SynchedEntityData.defineId(StaticSummon.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> RZP =
            SynchedEntityData.defineId(StaticSummon.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> RXPS =
            SynchedEntityData.defineId(StaticSummon.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> RYPS =
            SynchedEntityData.defineId(StaticSummon.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> RZPS =
            SynchedEntityData.defineId(StaticSummon.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> COLOR_R =
            SynchedEntityData.defineId(StaticSummon.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> COLOR_G =
            SynchedEntityData.defineId(StaticSummon.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> COLOR_B =
            SynchedEntityData.defineId(StaticSummon.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> COLOR_A =
            SynchedEntityData.defineId(StaticSummon.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> GLOW =
            SynchedEntityData.defineId(StaticSummon.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> AGE =
            SynchedEntityData.defineId(StaticSummon.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LIFETIME =
            SynchedEntityData.defineId(StaticSummon.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Optional<UUID>> OWNER =
            SynchedEntityData.defineId(StaticSummon.class, EntityDataSerializers.OPTIONAL_UUID);
    public static final EntityDataAccessor<CompoundTag> CUSTOM_DATA =
            SynchedEntityData.defineId(StaticSummon.class, EntityDataSerializers.COMPOUND_TAG);

    public LinkedList<Vec3> trailPositions = new LinkedList<>();

    private Entity owner = null;

    public StaticSummon(EntityType<StaticSummon> entityType, Level level) {
        super(entityType, level);
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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(BEHAVIOR, "default");
        builder.define(RENDER_MODE, "custom");
        builder.define(ITEM, ItemStack.EMPTY);
        builder.define(BLOCK, Blocks.AIR.defaultBlockState());
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
        builder.define(LIFETIME, 100);
        builder.define(OWNER, Optional.empty());
        builder.define(CUSTOM_DATA, new CompoundTag());
    }

    @Override
    public void tick() {
        super.tick();
        this.owner = getOwner();
        StaticSummonBehaviors.getBehavior(this.entityData.get(BEHAVIOR)).tick(this);
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("behavior", this.entityData.get(BEHAVIOR));
        tag.putString("renderMode", this.entityData.get(RENDER_MODE));
        ItemStack stack = this.entityData.get(ITEM);
        if(!stack.isEmpty()) tag.put("item", stack.save(this.level().registryAccess()));
        tag.put("block", NbtUtils.writeBlockState(this.entityData.get(BLOCK)));
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
        if(getOwner() != null) tag.putUUID("owner", getOwner().getUUID());
        tag.put("customData", this.entityData.get(CUSTOM_DATA));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
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
        if(tag.contains("owner")) {
            this.entityData.set(OWNER, Optional.of(tag.getUUID("owner")));
            getOwner();
        }
        if(tag.contains("customData")) this.entityData.set(CUSTOM_DATA, tag.getCompound("customData"));
    }

}
