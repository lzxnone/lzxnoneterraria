package com.lzxnone.terraria.client.item;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.ModItems;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.Util;
import net.minecraft.world.item.ItemStack;

public class ItemNameRenderer {
    public static boolean shouldHackHeldItemName(ItemStack stack) {
        return stack.is(ModItems.DEVILS_DEVASTATION.get());
    }

    public static boolean shouldHackTooltipItemName(ItemStack stack) {
        return stack.is(ModItems.DEVILS_DEVASTATION.get());
    }

    public static void renderHeldItemName(
        GuiGraphics guiGraphics,
        Font font,
        ItemStack stack,
        Component name,
        int yShift,
        int alpha,
        boolean canHurtPlayer
    ) {
        String plainName = name.getString();
        int textWidth = font.width(plainName);
        int x = (guiGraphics.guiWidth() - textWidth) / 2;
        int y = guiGraphics.guiHeight() - Math.max(yShift, 59);
        if(!canHurtPlayer) {
            y += 14;
        }

        if(stack.is(ModItems.DEVILS_DEVASTATION.get())) {
            renderDevilsDevastationItemName(guiGraphics, font, plainName, x, y, alpha);
        }
    }

    public static void renderTooltipItemName(
        GuiGraphics guiGraphics,
        Font font,
        ItemStack stack,
        int x,
        int y
    ) {
        if(stack.is(ModItems.DEVILS_DEVASTATION.get())) {
            renderDevilsDevastationItemName(guiGraphics, font, stack.getHoverName().getString(), x, y, 255);
        }
    }

    private static void renderDevilsDevastationItemName(
        GuiGraphics guiGraphics,
        Font font,
        String plainName,
        int x,
        int y,
        int alpha
    ) {
        int textWidth = font.width(plainName);
        FormattedCharSequence plainText = FormattedCharSequence.forward(plainName, Style.EMPTY);

        //外层字体和描边颜色
        int outerTextColor = FastColor.ARGB32.color(0x04, 0xB373F4);
        int outerOutlineColor = FastColor.ARGB32.color(alpha / 2, 0xB373F4);

        //内层字体和描边颜色
        int innerTextColor = FastColor.ARGB32.color(alpha, 0x000000);
        int innerOutlineColor = FastColor.ARGB32.color(alpha, 0xB373F4);

        //外层字体
        float scale = 0.08F;
        double speed = 0.004D;
        double wave = Math.sin(Util.getMillis() * speed);
        float breathScale = 1.0F + (float)((wave + 1.0D) * 0.5D) * scale;

        float centerX = x + textWidth / 2.0F;
        float centerY = y + font.lineHeight / 2.0F;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(centerX, centerY, 0.0F);
        guiGraphics.pose().scale(breathScale, breathScale, 1.0F);
        guiGraphics.pose().translate(-centerX, -centerY, 0.0F);
        font.drawInBatch8xOutline(
            plainText,
            x,
            y,
            outerTextColor,
            outerOutlineColor,
            guiGraphics.pose().last().pose(),
            guiGraphics.bufferSource(),
            15728880
        );
        guiGraphics.pose().popPose();

        //内层字体
        font.drawInBatch8xOutline(
            plainText,
            x,
            y,
            innerTextColor,
            innerOutlineColor,
            guiGraphics.pose().last().pose(),
            guiGraphics.bufferSource(),
            15728880
        );
        guiGraphics.flush();

        //星星流动
        ResourceLocation starTexture = ResourceLocation.fromNamespaceAndPath(
            LzxnoneTerraria.MODID,
            "textures/vfx/devils_name_star.png"
        );
        int starSize = 2;
        int flowHeight = font.lineHeight + starSize * 2;
        float starSpeed = 0.02F;
        float time = Util.getMillis() * starSpeed;
        int charX = x;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, alpha / 255.0F);
        for(int i = 0; i < plainName.length(); i++) {
            String character = plainName.substring(i, i + 1);
            int charWidth = font.width(character);
            if(charWidth <= 0) {
                charX += charWidth;
                continue;
            }

            guiGraphics.enableScissor(charX, y, charX + charWidth, y + font.lineHeight);
            int starCount = charWidth >= 8 ? 2 : 1;
            for(int starIndex = 0; starIndex < starCount; starIndex++) {
                int xRange = Math.max(1, charWidth - starSize);
                int starX = charX + Math.floorMod(i * 5 + starIndex * 3, xRange + 1);
                float offset = i * 7.0F + starIndex * 13.0F;
                int starY = Math.round(y + font.lineHeight + starSize - ((time + offset) % flowHeight));
                guiGraphics.blit(
                    starTexture,
                    starX,
                    starY,
                    starSize,
                    starSize,
                    0.0F,
                    0.0F,
                    8,
                    8,
                    8,
                    8
                );
            }
            guiGraphics.disableScissor();
            charX += charWidth;
        }
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }
}
