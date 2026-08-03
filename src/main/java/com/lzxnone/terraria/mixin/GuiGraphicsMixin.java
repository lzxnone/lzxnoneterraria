package com.lzxnone.terraria.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.lzxnone.terraria.client.item.ItemNameRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
    @Shadow
    private ItemStack tooltipStack;

    @Unique
    private boolean lzxnoneterraria$hackTooltipItemName;
    @Unique
    private int lzxnoneterraria$tooltipTextLineIndex;

    @Inject(method = "renderTooltipInternal", at = @At("HEAD"))
    private void beginCustomTooltipName(
        Font font,
        List<ClientTooltipComponent> components,
        int mouseX,
        int mouseY,
        ClientTooltipPositioner tooltipPositioner,
        CallbackInfo ci
    ) {
        this.lzxnoneterraria$hackTooltipItemName = ItemNameRenderer.shouldHackTooltipItemName(this.tooltipStack);
        this.lzxnoneterraria$tooltipTextLineIndex = 0;
    }

    @WrapOperation(
        method = "renderTooltipInternal",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;renderText(Lnet/minecraft/client/gui/Font;IILorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V"
        )
    )
    private void renderCustomTooltipName(
        ClientTooltipComponent component,
        Font font,
        int mouseX,
        int mouseY,
        Matrix4f matrix,
        MultiBufferSource.BufferSource bufferSource,
        Operation<Void> original
    ) {
        if(this.lzxnoneterraria$hackTooltipItemName && this.lzxnoneterraria$tooltipTextLineIndex == 0) {
            ItemNameRenderer.renderTooltipItemName(
                (GuiGraphics)(Object)this,
                font,
                this.tooltipStack,
                mouseX,
                mouseY
            );
        }else {
            original.call(component, font, mouseX, mouseY, matrix, bufferSource);
        }

        this.lzxnoneterraria$tooltipTextLineIndex++;
    }

    @Inject(method = "renderTooltipInternal", at = @At("RETURN"))
    private void endCustomTooltipName(
        Font font,
        List<ClientTooltipComponent> components,
        int mouseX,
        int mouseY,
        ClientTooltipPositioner tooltipPositioner,
        CallbackInfo ci
    ) {
        this.lzxnoneterraria$hackTooltipItemName = false;
        this.lzxnoneterraria$tooltipTextLineIndex = 0;
    }
}
