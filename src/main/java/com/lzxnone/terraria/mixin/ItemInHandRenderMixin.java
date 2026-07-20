package com.lzxnone.terraria.mixin;

import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.entity.ModRenderTypes;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.weapon.melee.DevilsDevastation;
import com.lzxnone.terraria.item.weapon.melee.Mace;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRenderMixin {
    @Shadow
    private void renderPlayerArm(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                                 float equippedProgress, float swingProgress, HumanoidArm side) {}

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void renderHand(AbstractClientPlayer player, float partialTicks, float pitch,
                                         InteractionHand hand, float swingProgress, ItemStack stack,
                                         float equippedProgress, PoseStack poseStack,
                                         MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {
        if(!player.isScoping()) {
            if(stack.is(ModItems.ZENITH.get())
                || stack.is(ModItems.TRUE_COPPER_SHORTSWORD.get())
                || stack.is(ModItems.FIRST_FRACTAL.get())
                || stack.is(ModItems.TERRAGRIM.get())) {
                if(player.isUsingItem()) {
                    ItemStack usingStack = player.getUseItem();
                    if(usingStack.is(stack.getItem())) {
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
            }else if(stack.is(ModItems.MACE.get())) {
                int state = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                    .copyTag().getInt("state");
                if(state != Mace.State.IDLE.ordinal()) {
                    boolean isMainHand = hand == InteractionHand.MAIN_HAND;
                    HumanoidArm humanoidarm = isMainHand ? player.getMainArm() : player.getMainArm().getOpposite();

                    poseStack.pushPose();

                    if(!player.isInvisible()) {
                        this.renderPlayerArm(poseStack, buffer, combinedLight, equippedProgress, swingProgress, humanoidarm);
                    }

                    poseStack.popPose();
                    ci.cancel();
                }
            }else if(stack.is(ModItems.DEVILS_DEVASTATION.get())) {
                MobEffectInstance effectInstance = player.getEffect(ModEffects.KILL_MODE);
                if(effectInstance != null && player.getCooldowns().isOnCooldown(stack.getItem())) {
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void renderItem(LivingEntity entity, ItemStack itemStack, ItemDisplayContext displayContext,
                                         boolean leftHand, PoseStack poseStack, MultiBufferSource buffer,
                                         int seed, CallbackInfo ci) {

        if((itemStack.is(ModItems.ZENITH.get())
            || itemStack.is(ModItems.TRUE_COPPER_SHORTSWORD.get())
            || itemStack.is(ModItems.FIRST_FRACTAL.get())
            || itemStack.is(ModItems.TERRAGRIM.get())) && entity instanceof Player player) {
            if(player.isUsingItem()) {
                ItemStack usingStack = player.getUseItem();
                if(usingStack.is(itemStack.getItem())) {
                    ci.cancel();
                }
            }
        }else if(itemStack.is(ModItems.MACE.get())) {
            int state = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                    .copyTag().getInt("state");
            if(state != Mace.State.IDLE.ordinal()) {
                ci.cancel();
            }
        }else if(itemStack.is(ModItems.DEVILS_DEVASTATION.get()) && entity instanceof Player player) {
                MobEffectInstance effectInstance = player.getEffect(ModEffects.KILL_MODE);
                if(effectInstance != null && player.getCooldowns().isOnCooldown(itemStack.getItem())) {
                    ci.cancel();
                }
            }
    }

    @Inject(method = "renderItem", at = @At("RETURN"))
    private void renderItemPost(LivingEntity entity, ItemStack itemStack, ItemDisplayContext displayContext,
                                boolean leftHand, PoseStack poseStack, MultiBufferSource buffer,
                                int seed, CallbackInfo ci) {
        if(itemStack.is(ModItems.DEVILS_DEVASTATION.get()) && entity instanceof Player player) {
            if(entity.getEffect(ModEffects.KILL_MODE) != null && !player.getCooldowns().isOnCooldown(itemStack.getItem())) {
                poseStack.pushPose();
                DevilsDevastation.renderEnergyWave(buffer, poseStack, player);
                poseStack.popPose();
            }
        }
    }
}
