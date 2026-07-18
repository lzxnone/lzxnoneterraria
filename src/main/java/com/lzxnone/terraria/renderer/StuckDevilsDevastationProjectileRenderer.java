package com.lzxnone.terraria.renderer;

import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.item.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.StuckInBodyLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
/*
public class StuckDevilsDevastationProjectileRenderer<T extends LivingEntity, M extends EntityModel<T>> extends StuckInBodyLayer<T, M> {
    private final ItemRenderer itemRenderer;
    private final ItemStack stack = new ItemStack(ModItems.DEVILS_DEVASTATION.get()); // 替换成你自己的剑

    public StuckDevilsDevastationProjectileRenderer(EntityRendererProvider.Context context, LivingEntityRenderer<T, M> renderer) {
        super(renderer);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    protected int numStuck(T entity) {
        return entity.getData(ModAttachments.STUCK_DEVILS_DEVASTATION_PROJECTILE).size();
    }

    @Override
    protected void renderStuckItem(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                                   Entity entity, float x, float y, float z, float partialTick) {
        poseStack.pushPose();

        // 计算法线长度
        float f = Mth.sqrt(x * x + z * z);

        // 偏转角度，对齐生物体表法线（模仿原版箭矢计算）
        poseStack.mulPose(Axis.YP.rotationDegrees((float)(Math.atan2(x, z) * (180F / Math.PI))));
        poseStack.mulPose(Axis.XP.rotationDegrees((float)(Math.atan2(y, f) * (180F / Math.PI))));

        // 调整物品的倾斜度，确保是剑尖插进身体，而不是平躺
        // 原版物品在 FIXED 模式下是直立的，旋转 90 度并倾斜 45 度通常能指向 Z 轴正/负方向
        poseStack.mulPose(Axis.XP.rotationDegrees(-45.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));

        // 平移，让剑尖没入模型内部
        poseStack.translate(0.0D, 0.0D, -0.3D);

        // 缩小比例，让插在身上的剑显得自然
        poseStack.scale(0.8F, 0.8F, 0.8F);

        // 调用 ItemRenderer 直接渲染物品模型
        this.itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED,
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), 0);

        poseStack.popPose();
    }
}*/
