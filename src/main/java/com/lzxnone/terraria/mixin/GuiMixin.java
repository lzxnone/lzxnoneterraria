package com.lzxnone.terraria.mixin;

import com.lzxnone.terraria.client.item.ItemNameRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow
    private Minecraft minecraft;

    @Shadow
    private int toolHighlightTimer;

    @Shadow
    private ItemStack lastToolHighlight;

    @Shadow
    public abstract Font getFont();

    @Inject(method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V", at = @At("HEAD"), cancellable = true)
    private void renderCustomSelectedItemName(GuiGraphics guiGraphics, int yShift, CallbackInfo ci) {
        if(this.toolHighlightTimer <= 0 || this.lastToolHighlight.isEmpty()) return;
        if(!ItemNameRenderer.shouldHackHeldItemName(this.lastToolHighlight)) return;

        this.minecraft.getProfiler().push("customSelectedItemName");

        MutableComponent mutablecomponent = Component.empty()
            .append(this.lastToolHighlight.getHoverName())
            .withStyle(this.lastToolHighlight.getRarity().getStyleModifier());
        if(this.lastToolHighlight.has(DataComponents.CUSTOM_NAME)) {
            mutablecomponent.withStyle(ChatFormatting.ITALIC);
        }

        Component highlightTip = this.lastToolHighlight.getHighlightTip(mutablecomponent);
        int alpha = (int)((float)this.toolHighlightTimer * 256.0F / 10.0F);
        if(alpha > 255) {
            alpha = 255;
        }

        if(alpha > 0) {
            if(this.minecraft.gameMode != null) {
                ItemNameRenderer.renderHeldItemName(
                    guiGraphics,
                    this.getFont(),
                    this.lastToolHighlight,
                    highlightTip,
                    yShift,
                    alpha,
                    this.minecraft.gameMode.canHurtPlayer()
                );
            }
        }

        this.minecraft.getProfiler().pop();
        ci.cancel();
    }
}
