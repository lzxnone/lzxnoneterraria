package com.lzxnone.terraria.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID)
public class FallenStarEventHandler {
    private static final int CHECK_INTERVAL = 20;
    private static final double SPAWN_CHANCE = 0.004D;
    private static final int MIN_SPAWN_RADIUS = 24;
    private static final int MAX_SPAWN_RADIUS = 64;
    private static final int SPAWN_HEIGHT = 48;
    private static final int MAX_ATTEMPTS = 8;
    private static final double FALL_SPEED = -0.65D;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if(!(event.getEntity() instanceof ServerPlayer player)) return;
        if(!(player.level() instanceof ServerLevel serverLevel)) return;
        if(serverLevel.dimension() != Level.OVERWORLD) return;
        if(!serverLevel.isNight()) return;
        if(player.tickCount % CHECK_INTERVAL != 0) return;

        RandomSource random = player.getRandom();
        if(random.nextDouble() >= SPAWN_CHANCE) return;

        Vec3 spawnPos = getSpawnPos(serverLevel, player, random);
        if(spawnPos == null) return;

        ItemEntity fallenStar = new ItemEntity(serverLevel, spawnPos.x, spawnPos.y, spawnPos.z, new ItemStack(ModItems.FALLEN_STAR.get()));
        fallenStar.setDeltaMovement(0.0D, FALL_SPEED, 0.0D);
        fallenStar.setDefaultPickUpDelay();
        serverLevel.addFreshEntity(fallenStar);
        SoundUtil.playServerSound(fallenStar, ModSounds.STAR_FALL.get(), 2.0f, 1.0f);
    }

    private static Vec3 getSpawnPos(ServerLevel level, ServerPlayer player, RandomSource random) {
        for(int i = 0; i < MAX_ATTEMPTS; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0D;
            double radius = Mth.lerp(random.nextDouble(), MIN_SPAWN_RADIUS, MAX_SPAWN_RADIUS);
            int x = Mth.floor(player.getX() + Math.cos(angle) * radius);
            int z = Mth.floor(player.getZ() + Math.sin(angle) * radius);
            int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos surfacePos = new BlockPos(x, surfaceY, z);
            if(!level.hasChunkAt(surfacePos)) continue;
            if(!level.canSeeSky(surfacePos)) continue;

            int y = Math.min(level.getMaxBuildHeight() - 2, surfaceY + SPAWN_HEIGHT);
            return new Vec3(x + 0.5D, y, z + 0.5D);
        }
        return null;
    }
}
