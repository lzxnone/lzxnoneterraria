package com.lzxnone.terraria.client.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.item.ModItems;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID, value = Dist.CLIENT)
public class GuiRenderHandler {
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if(player == null || minecraft.options.hideGui || player.isSpectator()) return;

        ManaHudRenderer.render(event.getGuiGraphics(), player);

        ItemStack stack = player.getMainHandItem();
        if(stack.is(ModItems.DEVILS_DEVASTATION.get()) && player.getEffect(ModEffects.KILL_MODE) != null) {
            Component text = Component.translatable("tooltip.lzxnoneterraria.devils_devastation_kill_mode");
            Font font = minecraft.font;
            String plainText = text.getString();
            int yShift = Math.max(minecraft.gui.leftHeight, minecraft.gui.rightHeight) + (68 - 59);
            int actionbarY = event.getGuiGraphics().guiHeight() - Math.max(yShift, 68);
            int x = (event.getGuiGraphics().guiWidth() - font.width(plainText)) / 2;
            int y = actionbarY + 8;

            long shakeTime = Util.getMillis() % 1500L;
            if(shakeTime < 500L) {
                float progress = shakeTime / 500.0F;
                float strength = 1.0F - progress;
                x += Math.round(Math.sin(shakeTime * 0.12D) * 4.0D * strength);
                y += Math.round(Math.sin(shakeTime * 0.27D) * 2.0D * strength);
            }

            event.getGuiGraphics().drawString(font, text, x, y, 0xB373F4, true);
        }
    }
}
