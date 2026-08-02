package com.lzxnone.terraria.client.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.attachment.PlayerMana;
import com.lzxnone.terraria.client.config.RenderConfigs;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ManaHudRenderer {
    private static final ResourceLocation MANA_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        LzxnoneTerraria.MODID,
        "textures/ui/mana.png"
    );
    private static final int TEXTURE_WIDTH = 22;
    private static final int TEXTURE_HEIGHT = 24;
    private static final int MANA_PER_STAR = 20;
    private static final int MAX_SEGMENT_COUNT = 20;
    private static final int SEGMENTS_PER_COLUMN = 10;
    private static final int SLOT_WIDTH = 8;
    private static final int SLOT_HEIGHT = 9;
    private static final int SLOT_GAP = 1;
    public static int manaBarX;
    public static int manaBarY = RenderConfigs.MANA_BAR_Y.get();

    public static void render(GuiGraphics guiGraphics, LocalPlayer player) {
        PlayerMana mana = player.getData(ModAttachments.PLAYER_MANA);
        int maxMana = Math.max(0, mana.getMaxMana());
        if(maxMana <= 0) return;

        int segmentCount = Mth.clamp(Mth.ceil(maxMana / (float)MANA_PER_STAR), 0, MAX_SEGMENT_COUNT);
        int currentMana = Mth.clamp(mana.getMana(), 0, maxMana);

        manaBarX = RenderConfigs.MANA_BAR_X.get();
        manaBarY = RenderConfigs.MANA_BAR_Y.get();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        for(int i = 0; i < segmentCount; i++) {
            float fill = Mth.clamp((currentMana - i * MANA_PER_STAR) / (float)MANA_PER_STAR, 0.0F, 1.0F);
            int column = i / SEGMENTS_PER_COLUMN;
            int row = i % SEGMENTS_PER_COLUMN;
            int slotX = manaBarX + column * (SLOT_WIDTH + SLOT_GAP);
            int slotY = manaBarY + row * (SLOT_HEIGHT + SLOT_GAP);
            renderSegment(guiGraphics, slotX, slotY, fill);
        }
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        renderManaText(guiGraphics, currentMana, maxMana, segmentCount);
        RenderSystem.disableBlend();
    }

    private static void renderSegment(GuiGraphics guiGraphics, int slotX, int slotY, float fill) {
        boolean empty = fill <= 0.0F;
        float scale = empty ? 0.5F : 0.5F + fill * 0.5F;
        int width = Math.max(1, Math.round(SLOT_WIDTH * scale));
        int height = Math.max(1, Math.round(SLOT_HEIGHT * scale));
        int x = slotX + (SLOT_WIDTH - width) / 2;
        int y = slotY + (SLOT_HEIGHT - height) / 2;

        if(empty) {
            guiGraphics.setColor(0.0F, 0.0F, 0.0F, 0.8F);
        } else {
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }

        guiGraphics.blit(
            MANA_TEXTURE,
            x,
            y,
            width,
            height,
            0.0F,
            0.0F,
            TEXTURE_WIDTH,
            TEXTURE_HEIGHT,
            TEXTURE_WIDTH,
            TEXTURE_HEIGHT
        );
    }

    private static void renderManaText(GuiGraphics guiGraphics, int currentMana, int maxMana, int segmentCount) {
        Font font = Minecraft.getInstance().font;
        String text = currentMana + "/" + maxMana;
        int textWidth = font.width(text);
        int columnCount = Mth.ceil(segmentCount / (float)SEGMENTS_PER_COLUMN);
        int rowCount = Math.min(segmentCount, SEGMENTS_PER_COLUMN);
        int barWidth = columnCount * SLOT_WIDTH + Math.max(0, columnCount - 1) * SLOT_GAP;
        int barCenterX = manaBarX + barWidth / 2;
        int textX = Mth.clamp(barCenterX - textWidth / 2, 2, guiGraphics.guiWidth() - textWidth - 2);
        int textY = manaBarY + rowCount * SLOT_HEIGHT + Math.max(0, rowCount - 1) * SLOT_GAP + 2;

        guiGraphics.drawString(font, text, textX, textY, 0x66CCFF, true);
    }
}
