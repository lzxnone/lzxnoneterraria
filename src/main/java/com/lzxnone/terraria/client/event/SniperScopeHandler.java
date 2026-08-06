package com.lzxnone.terraria.client.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.accessory.AccessoryUtil;
import com.lzxnone.terraria.item.weapon.ranged.gun.Gun;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID, value = Dist.CLIENT)
public class SniperScopeHandler {
    @SubscribeEvent
    public static void onComputeFov(ComputeFovModifierEvent event) {
        ItemStack stack = getActiveSniperStack();
        if(stack.isEmpty()) return;

        double zoom = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getDouble("zoom");
        if(zoom <= 1) return;
        event.setNewFovModifier((float)(event.getNewFovModifier() / zoom));
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.options.hideGui) return;

        ItemStack stack = getActiveSniperStack();
        if(stack.isEmpty()) return;
        double zoom = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getDouble("zoom");
        if(zoom <= 1) return;
        renderSpyglassOverlay(event.getGuiGraphics(), 1.0F);
    }

    private static ItemStack getActiveSniperStack() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if(player == null || player.isSpectator() || !player.isUsingItem()) return ItemStack.EMPTY;

        ItemStack stack = player.getUseItem();
        if(stack.is(ModItems.SNIPER_RIFLE)) return stack;
        if(stack.getItem() instanceof Gun && AccessoryUtil.hasScopeZoom(player)) return stack;
        return ItemStack.EMPTY;
    }

    private static void renderSpyglassOverlay(GuiGraphics guiGraphics, float scopeScale) {
        float minSize = (float)Math.min(guiGraphics.guiWidth(), guiGraphics.guiHeight());
        float scale = Math.min((float)guiGraphics.guiWidth() / minSize, (float)guiGraphics.guiHeight() / minSize) * scopeScale;
        int width = Mth.floor(minSize * scale);
        int height = Mth.floor(minSize * scale);
        int x = (guiGraphics.guiWidth() - width) / 2;
        int y = (guiGraphics.guiHeight() - height) / 2;
        int right = x + width;
        int bottom = y + height;

        RenderSystem.enableBlend();
        guiGraphics.blit(Gui.SPYGLASS_SCOPE_LOCATION, x, y, -90, 0.0F, 0.0F, width, height, width, height);
        RenderSystem.disableBlend();
        guiGraphics.fill(RenderType.guiOverlay(), 0, bottom, guiGraphics.guiWidth(), guiGraphics.guiHeight(), -90, 0xFF000000);
        guiGraphics.fill(RenderType.guiOverlay(), 0, 0, guiGraphics.guiWidth(), y, -90, 0xFF000000);
        guiGraphics.fill(RenderType.guiOverlay(), 0, y, x, bottom, -90, 0xFF000000);
        guiGraphics.fill(RenderType.guiOverlay(), right, y, guiGraphics.guiWidth(), bottom, -90, 0xFF000000);
    }
}
