package com.lzxnone.terraria.block;

import com.lzxnone.terraria.LzxnoneTerraria;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LzxnoneTerraria.MODID);

    public static final DeferredBlock<Block> VISION = BLOCKS.register("vision", () -> new Block(
        BlockBehaviour.Properties.of()
            .strength(1.0F)
            .noOcclusion()
            .sound(SoundType.GLASS)
    ));

    public static final DeferredBlock<Block> STARDUST_DRAGON_HEAD = registerModelBlock("stardust_dragon_head");
    public static final DeferredBlock<Block> STARDUST_DRAGON_BODY_A = registerModelBlock("stardust_dragon_body_a");
    public static final DeferredBlock<Block> STARDUST_DRAGON_BODY_B = registerModelBlock("stardust_dragon_body_b");
    public static final DeferredBlock<Block> STARDUST_DRAGON_TAIL = registerModelBlock("stardust_dragon_tail");

    private static DeferredBlock<Block> registerModelBlock(String name) {
        return BLOCKS.register(name, () -> new Block(
            BlockBehaviour.Properties.of()
                .strength(1.0F)
                .noOcclusion()
                .sound(SoundType.GLASS)
        ));
    }
}
