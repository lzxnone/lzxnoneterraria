package com.lzxnone.terraria.mixin;

import com.lzxnone.terraria.item.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class TerragrimRenderMixin {
    @Shadow
    private void renderPlayerArm(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                                 float equippedProgress, float swingProgress, HumanoidArm side) {}

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void renderHand(AbstractClientPlayer player, float partialTicks, float pitch,
                            InteractionHand hand, float swingProgress, ItemStack stack,
                            float equippedProgress, PoseStack poseStack,
                            MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {

        if(!player.isScoping()) {
            if(stack.is(ModItems.TERRAGRIM.get())) {
                if(player.isUsingItem()) {
                    ItemStack usingStack = player.getUseItem();
                    if(usingStack.is(ModItems.TERRAGRIM.get())) {
                        boolean isMainHand = hand == InteractionHand.MAIN_HAND;
                        HumanoidArm humanoidarm = isMainHand ? player.getMainArm() : player.getMainArm().getOpposite();

                        poseStack.pushPose();

                        if(!player.isInvisible()) {
                            this.renderPlayerArm(poseStack, buffer, combinedLight, equippedProgress, swingProgress, humanoidarm);
                        }

                        poseStack.popPose();
                        ci.cancel();
                    }
                }
            }
        }
    }

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void renderTerragrim(LivingEntity entity, ItemStack itemStack, ItemDisplayContext displayContext,
                              boolean leftHand, PoseStack poseStack, MultiBufferSource buffer,
                              int seed, CallbackInfo ci) {

        if(itemStack.is(ModItems.TERRAGRIM.get()) && entity instanceof Player player) {
            if(player.isUsingItem()) {
                ItemStack usingStack = player.getUseItem();
                if(usingStack.is(ModItems.TERRAGRIM.get())) {
                    ci.cancel();
                }
            }
        }
    }
}
