package com.lzxnone.terraria.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class StaticProjectileRenderer extends EntityRenderer<Entity> {

    public StaticProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(Entity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(!(entity instanceof StaticProjectile proj)) return;
        if(!proj.isInit) return;
        if(proj.getEntityData().get(StaticProjectile.RENDER_MODE).equals("item") && proj.getEntityData().get(StaticProjectile.ITEM) != ItemStack.EMPTY) {
            StaticProjectileBehaviors.getBehavior(proj.getEntityData().get(StaticProjectile.BEHAVIOR)).renderItem(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        }else if(proj.getEntityData().get(StaticProjectile.RENDER_MODE).equals("block") && proj.getEntityData().get(StaticProjectile.BLOCK) != Blocks.AIR.defaultBlockState()) {
            StaticProjectileBehaviors.getBehavior(proj.getEntityData().get(StaticProjectile.BEHAVIOR)).renderBlock(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        }else if(!proj.getEntityData().get(StaticProjectile.GLOW)) {
            StaticProjectileBehaviors.getBehavior(proj.getEntityData().get(StaticProjectile.BEHAVIOR)).render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(Entity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
