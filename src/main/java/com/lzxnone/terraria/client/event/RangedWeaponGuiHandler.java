package com.lzxnone.terraria.client.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.ModItemTags;
import com.lzxnone.terraria.item.weapon.RangedWeapon;
import net.minecraft.client.gui.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID, value = Dist.CLIENT)
public class RangedWeaponGuiHandler {
    private static final int VALID_AMMO_COLOR = 0x6600FF00;
    private static final int INVALID_AMMO_COLOR = 0x66FF0000;

    @SubscribeEvent
    public static void onContainerForeground(ContainerScreenEvent.Render.Foreground event) {
        ItemStack carriedStack = event.getContainerScreen().getMenu().getCarried();
        GuiGraphics guiGraphics = event.getGuiGraphics();

        for(Slot slot : event.getContainerScreen().getMenu().slots) {
            if(!slot.isActive()) continue;

            ItemStack weaponStack = slot.getItem();
            if(!(weaponStack.getItem() instanceof RangedWeapon rangedWeapon)) continue;

            renderAmmoDropHint(guiGraphics, rangedWeapon, weaponStack, carriedStack, slot);
            renderSelectedAmmoIcon(guiGraphics, weaponStack, slot.x, slot.y);
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if(player == null || minecraft.options.hideGui || player.isSpectator()) return;

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int centerX = guiGraphics.guiWidth() / 2;
        int hotbarY = guiGraphics.guiHeight() - 19;

        for(int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().items.get(i);
            int slotX = centerX - 90 + i * 20 + 2;
            renderSelectedAmmoIcon(guiGraphics, stack, slotX, hotbarY, player);
        }

        ItemStack offhandStack = player.getOffhandItem();
        if(!offhandStack.isEmpty()) {
            int offhandX = player.getMainArm().getOpposite() == HumanoidArm.LEFT
                    ? centerX - 91 - 26
                    : centerX + 91 + 10;
            renderSelectedAmmoIcon(guiGraphics, offhandStack, offhandX, hotbarY, player);
        }
    }

    private static void renderAmmoDropHint(GuiGraphics guiGraphics, RangedWeapon rangedWeapon,
                                           ItemStack weaponStack, ItemStack carriedStack, Slot slot) {
        if(carriedStack.isEmpty()) return;
        if(!carriedStack.is(ModItemTags.AMMO)) return;

        int color = rangedWeapon.canUseAmmo(weaponStack, carriedStack) ? VALID_AMMO_COLOR : INVALID_AMMO_COLOR;
        guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, color);
    }

    private static void renderSelectedAmmoIcon(GuiGraphics guiGraphics, ItemStack weaponStack, int slotX, int slotY) {
        renderSelectedAmmoIcon(guiGraphics, weaponStack, slotX, slotY, null);
    }

    private static void renderSelectedAmmoIcon(GuiGraphics guiGraphics, ItemStack weaponStack, int slotX, int slotY, Player player) {
        if(!(weaponStack.getItem() instanceof RangedWeapon rangedWeapon)) return;

        ItemStack ammoStack = RangedWeapon.getAmmoStack(weaponStack);
        if(ammoStack.isEmpty()) return;

        int x = slotX + 12;
        int y = slotY + 12;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 200.0F);
        guiGraphics.pose().scale(0.25F, 0.25F, 1.0F);
        guiGraphics.renderItem(ammoStack, 0, 0);
        guiGraphics.pose().popPose();
        guiGraphics.flush();

        if(player != null) {
            renderAmmoCount(guiGraphics, rangedWeapon, weaponStack, player, x, y);
        }
    }

    private static void renderAmmoCount(GuiGraphics guiGraphics, RangedWeapon rangedWeapon,
                                        ItemStack weaponStack, Player player, int iconX, int iconY) {
        int count = rangedWeapon.getAmmoCount(weaponStack, player);
        String text = Integer.toString(count);
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        float scale = 0.5F;
        int textWidth = font.width(text);
        float textX = iconX - textWidth * scale;
        float textY = iconY;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(textX, textY, 500.0F);
        guiGraphics.pose().scale(scale, scale, 1.0F);
        guiGraphics.drawString(font, text, 0, 0, 0xFFFFFF, true);
        guiGraphics.pose().popPose();
        guiGraphics.flush();
    }
}
