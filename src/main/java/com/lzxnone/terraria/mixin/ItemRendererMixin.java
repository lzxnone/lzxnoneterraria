package com.lzxnone.terraria.mixin;

import com.lzxnone.terraria.client.item.melee.ClientDevilsDevastation;
import com.lzxnone.terraria.item.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderEffect(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand,
                                PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight,
                                int combinedOverlay, BakedModel model, CallbackInfo ci) {
        // 仅在手持视角下处理（第一人称或第三人称手持），忽略 GUI、掉落物、展示框等
        boolean isFirstPerson = displayContext.firstPerson();
        boolean isThirdPerson = displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND ||
                                displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
        if(!isFirstPerson && !isThirdPerson) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if(player == null || mc.level == null) return;

        if(itemStack.is(ModItems.DEVILS_DEVASTATION.get())) {
            ClientDevilsDevastation.renderEnergyParticles(poseStack, displayContext, player);
        }
    }
}
