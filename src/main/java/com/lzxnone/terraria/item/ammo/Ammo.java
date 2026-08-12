package com.lzxnone.terraria.item.ammo;

import com.lzxnone.terraria.ModSounds;
import com.lzxnone.terraria.utils.ParticleUtil;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class Ammo extends Item {
    public static final int MAX_STACK_SIZE = 99;

    public Ammo() {
        super(new Item.Properties().stacksTo(MAX_STACK_SIZE));
    }

    public static void playBlockHitEffects(ServerLevel level, BlockHitResult blockHitResult) {
        BlockState hitState = level.getBlockState(blockHitResult.getBlockPos());
        ParticleUtil.addParticles(
            level,
            new BlockParticleOption(ParticleTypes.BLOCK, hitState),
            blockHitResult.getLocation(),
            Vec3.ZERO,
            0.2,
            25
        );
        SoundUtil.playServerSound(level, ModSounds.DIG.get(), blockHitResult.getLocation(), 1.0f, 1.0f);
    }
}
