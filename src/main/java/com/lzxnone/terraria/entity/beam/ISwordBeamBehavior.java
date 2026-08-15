package com.lzxnone.terraria.entity.beam;

import com.lzxnone.terraria.entity.ModEntities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public interface ISwordBeamBehavior {
    private static String behaviorOf(ItemStack stack) {
        return stack.isEmpty() ? "" : BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
    }

    default void generate(Entity entity, CompoundTag beamData) {
        if(entity == null) return;
        SwordBeam beam = new SwordBeam(ModEntities.SWORD_BEAM.get(), entity.level());
        beam.setOwner(entity);
        Vec3 pos = new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ());
        beam.setPos(pos);
        if(beamData.contains("behavior")) {
            String behavior = beamData.getString("behavior");
            beam.getEntityData().set(SwordBeam.BEHAVIOR, behavior);
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("lzxnoneterraria", behavior));
            if(item != Items.AIR) beam.getEntityData().set(SwordBeam.STACK_SOURCE, new ItemStack(item));
            //记录使用手（主手优先，映射为物理右手）：武器在主手槽 → 右手 = 主手设置是 RIGHT；仅副手槽 → 右手 = 主手设置是 LEFT（左撇子）
            boolean rightHand = true;
            if(entity instanceof Player player) {
                ItemStack mainStack = player.getMainHandItem();
                ItemStack offStack = player.getOffhandItem();
                boolean mainHolds = behaviorOf(mainStack).equals(behavior);
                boolean offHolds = behaviorOf(offStack).equals(behavior);
                if(mainHolds) {
                    rightHand = player.getMainArm() == HumanoidArm.RIGHT;
                }else if(offHolds) {
                    rightHand = player.getMainArm() == HumanoidArm.LEFT;
                }else {
                    rightHand = player.getMainArm() == HumanoidArm.RIGHT;
                }
            }
            beam.getEntityData().set(SwordBeam.RIGHT_HAND, rightHand);
        }
        if(beamData.contains("rotate"))  beam.getEntityData().set(SwordBeam.ROTATE, beamData.getInt("rotate"));
        if(beamData.contains("right"))  beam.getEntityData().set(SwordBeam.RIGHT, beamData.getBoolean("right"));
        if(beamData.contains("inflate"))  beam.getEntityData().set(SwordBeam.INFLATE, beamData.getFloat("inflate"));
        if(beamData.contains("color0R") && beamData.contains("color0G") && beamData.contains("color0B")) {
            beam.getEntityData().set(SwordBeam.COLOR0, new Vector3f(beamData.getFloat("color0R"), beamData.getFloat("color0G"), beamData.getFloat("color0B")));
        }
        if(beamData.contains("color1R") && beamData.contains("color1G") && beamData.contains("color1B")) {
            beam.getEntityData().set(SwordBeam.COLOR1, new Vector3f(beamData.getFloat("color1R"), beamData.getFloat("color1G"), beamData.getFloat("color1B")));
        }
        if(beamData.contains("color2R") && beamData.contains("color2G") && beamData.contains("color2B")) {
            beam.getEntityData().set(SwordBeam.COLOR2, new Vector3f(beamData.getFloat("color2R"), beamData.getFloat("color2G"), beamData.getFloat("color2B")));
        }
        if(beamData.contains("color3R") && beamData.contains("color3G") && beamData.contains("color3B")) {
            beam.getEntityData().set(SwordBeam.COLOR3, new Vector3f(beamData.getFloat("color3R"), beamData.getFloat("color3G"), beamData.getFloat("color3B")));
        }
        if(beamData.contains("age")) beam.getEntityData().set(SwordBeam.AGE, beamData.getInt("age"));
        if(beamData.contains("lifetime")) beam.getEntityData().set(SwordBeam.LIFETIME, beamData.getInt("lifetime"));
        if(beamData.contains("customData")) beam.getEntityData().set(SwordBeam.CUSTOM_DATA, beamData.getCompound("customData").copy());
        entity.level().addFreshEntity(beam);
    }

    default void onMoving(SwordBeam beam) {}
    default void onHitEntity(SwordBeam beam, EntityHitResult result) {}
    default void onHitBlock(SwordBeam beam, BlockHitResult result) {}
    default void onDied(SwordBeam beam) {
        if(!beam.level().isClientSide()) {
            beam.discard();
        }
    }
}
