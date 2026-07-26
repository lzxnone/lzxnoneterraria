package com.lzxnone.terraria.entity.beam;

import com.lzxnone.terraria.entity.ModEntities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public interface ISwordBeamBehavior {
    default void generate(Entity entity, CompoundTag beamData) {
        if(entity == null) return;
        SwordBeam beam = new SwordBeam(ModEntities.SWORD_BEAM.get(), entity.level());
        beam.setOwner(entity);
        if(entity instanceof Player player) beam.getEntityData().set(SwordBeam.STACK_SOURCE, player.getWeaponItem().copy());
        Vec3 pos = new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ());
        beam.setPos(pos);
        if(beamData.contains("behavior")) beam.getEntityData().set(SwordBeam.BEHAVIOR, beamData.getString("behavior"));
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
        if(beamData.contains("behavior") && beamData.contains("cooldown") && entity instanceof Player player) {
            ResourceLocation itemKey = ResourceLocation.fromNamespaceAndPath("lzxnoneterraria", beamData.getString("behavior"));
            Item item = BuiltInRegistries.ITEM.get(itemKey);
            if(item != Items.AIR) player.getCooldowns().addCooldown(item, beamData.getInt("cooldown"));
        }
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
