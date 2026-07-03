package com.lzxnone.terraria.entity.beam;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class SwordBeamRenderer extends EntityRenderer<Entity> {
    public SwordBeamRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(Entity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(Entity entity) {
        return ResourceLocation.parse("lzxnoneterraria:textures/item/terra_beam.png");
    }
}
