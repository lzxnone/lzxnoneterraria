package com.lzxnone.terraria.event_handler;

import com.lzxnone.terraria.Config;
import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.weapon.melee.FirstFractal;
import com.lzxnone.terraria.item.weapon.melee.TrueCopperShortsword;
import com.lzxnone.terraria.item.weapon.melee.Zenith;
import com.lzxnone.terraria.network.payload.FirstFractalScrollPayload;
import com.lzxnone.terraria.network.payload.ZenithScrollPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID, value = Dist.CLIENT)
public class InputHandler {
    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if(player == null) return;

        ItemStack mainHand = player.getMainHandItem();
        if(mainHand.getItem() instanceof Zenith || mainHand.getItem() instanceof TrueCopperShortsword) {
            int count = mainHand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                    .copyTag().getInt("attackCount");
            double scrollDelta = event.getScrollDeltaY();
            if(scrollDelta != 0 && count > 0) {
                event.setCanceled(true);
                double deltaDist = mainHand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                        .copyTag().getDouble("deltaDist");

                double finalDelta = deltaDist + scrollDelta * Config.zenithMouseSpeed;

                if(Config.zenithDistanceMode) {
                    if(finalDelta > Config.zenithMaxRange) finalDelta = Config.zenithMaxRange;
                    else if(finalDelta < -Config.zenithMaxRange) finalDelta = -Config.zenithMaxRange;
                }else {
                    if(finalDelta > Config.zenithMaxRange) finalDelta = Config.zenithMaxRange;
                    else if(finalDelta < 0) finalDelta = 0;
                }

                MutableComponent displayText = Component.empty();
                displayText.append(Component.literal(String.format("%s", Config.zenithDistanceMode ? "相对距离: " : "绝对距离: ")).withStyle(ChatFormatting.WHITE))
                     .append(Component.literal(String.format("%.1f", finalDelta)).withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
                player.displayClientMessage(displayText, true);

                PacketDistributor.sendToServer(new ZenithScrollPayload(scrollDelta));
            }
        }else if(mainHand.getItem() instanceof FirstFractal) {
            int count = mainHand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                    .copyTag().getInt("attackCount");
            double scrollDelta = event.getScrollDeltaY();
            if(scrollDelta != 0 && count > 0) {
                event.setCanceled(true);
                double deltaDist = mainHand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                        .copyTag().getDouble("deltaDist");

                double finalDelta = deltaDist + scrollDelta * Config.firstFractalScrollSpeed;

                if(Config.firstFractalDistanceMode) {
                    if(finalDelta > Config.firstFractalMaxRange) finalDelta = Config.firstFractalMaxRange;
                    else if(finalDelta < -Config.firstFractalMaxRange) finalDelta = -Config.firstFractalMaxRange;
                }else {
                    if(finalDelta > Config.firstFractalMaxRange) finalDelta = Config.firstFractalMaxRange;
                    else if(finalDelta < 0) finalDelta = 0;
                }
                MutableComponent displayText = Component.empty();
                displayText.append(Component.literal(String.format("%s", Config.firstFractalDistanceMode ? "相对距离: " : "绝对距离: ")).withStyle(ChatFormatting.WHITE))
                     .append(Component.literal(String.format("%.1f", finalDelta)).withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
                player.displayClientMessage(displayText, true);

                PacketDistributor.sendToServer(new FirstFractalScrollPayload(scrollDelta));
            }
        }
    }

    @SubscribeEvent
    public static void onMovementInputUpdate(MovementInputUpdateEvent event) {
        Player player = event.getEntity();
        if(player.isUsingItem() && (player.getUseItem().is(ModItems.TERRAGRIM.get()) || player.getUseItem().is(ModItems.MACE.get()))) {
            Input input = event.getInput();
            input.forwardImpulse *= 5.0F;
            input.leftImpulse *= 5.0F;
        }
    }
}
