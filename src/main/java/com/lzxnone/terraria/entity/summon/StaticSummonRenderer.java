package com.lzxnone.terraria.entity.summon;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class StaticSummonRenderer extends EntityRenderer<Entity>  {
    public StaticSummonRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(Entity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(!(entity instanceof StaticSummon summon)) return;
        if(summon.getEntityData().get(StaticSummon.RENDER_MODE).equals("item") && summon.getEntityData().get(StaticSummon.ITEM) != ItemStack.EMPTY) {
            StaticSummonBehaviors.getBehavior(summon.getEntityData().get(StaticSummon.BEHAVIOR)).renderItem(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        }else if(summon.getEntityData().get(StaticSummon.RENDER_MODE).equals("block") && summon.getEntityData().get(StaticSummon.BLOCK) != Blocks.AIR.defaultBlockState()) {
            StaticSummonBehaviors.getBehavior(summon.getEntityData().get(StaticSummon.BEHAVIOR)).renderBlock(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        }else if(!summon.getEntityData().get(StaticSummon.GLOW)) {
            StaticSummonBehaviors.getBehavior(summon.getEntityData().get(StaticSummon.BEHAVIOR)).render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(Entity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
