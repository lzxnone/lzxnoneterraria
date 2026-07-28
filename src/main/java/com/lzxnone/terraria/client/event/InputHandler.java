package com.lzxnone.terraria.client.event;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ModKeyBindings;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.weapon.SummonWeapon;
import com.lzxnone.terraria.item.weapon.melee.DevilsDevastation;
import com.lzxnone.terraria.item.weapon.melee.FirstFractal;
import com.lzxnone.terraria.item.weapon.melee.TrueCopperShortsword;
import com.lzxnone.terraria.item.weapon.melee.Zenith;
import com.lzxnone.terraria.item.weapon.ranged.SniperRifle;
import com.lzxnone.terraria.network.payload.ClearSummonPayload;
import com.lzxnone.terraria.network.payload.DevilsDevastationKillModePayload;
import com.lzxnone.terraria.network.payload.FirstFractalScrollPayload;
import com.lzxnone.terraria.network.payload.ToggleSummonFreeTargetingPayload;
import com.lzxnone.terraria.network.payload.ZenithScrollPayload;
import com.lzxnone.terraria.utils.SoundUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
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
            if(player.isUsingItem() && (player.getUseItem().is(ModItems.ZENITH.get()) || player.getUseItem().is(ModItems.TRUE_COPPER_SHORTSWORD.get()))) {
                double scrollDelta = event.getScrollDeltaY();
                if(scrollDelta != 0) {
                    event.setCanceled(true);
                    double deltaDist = mainHand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                            .copyTag().getDouble("deltaDist");

                    double finalDelta = deltaDist + scrollDelta * Zenith.getMouseSpeed();

                    if(Zenith.isDistanceMode()) {
                        if(finalDelta > Zenith.getMaxRange()) finalDelta = Zenith.getMaxRange();
                        else if(finalDelta < -Zenith.getMaxRange()) finalDelta = -Zenith.getMaxRange();
                    }else {
                        if(finalDelta > Zenith.getMaxRange()) finalDelta = Zenith.getMaxRange();
                        else if(finalDelta < 0) finalDelta = 0;
                    }

                    MutableComponent displayText = Component.empty();
                    displayText.append(Component.translatable(Zenith.isDistanceMode() ? "tooltip.lzxnoneterraria.relative_distance" : "tooltip.lzxnoneterraria.absolute_distance").withStyle(ChatFormatting.WHITE))
                         .append(Component.literal(": ").withStyle(ChatFormatting.WHITE))
                         .append(Component.literal(String.format("%.2f", finalDelta)).withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
                    player.displayClientMessage(displayText, true);

                    PacketDistributor.sendToServer(new ZenithScrollPayload(scrollDelta));
                }
            }
        }else if(player.getUseItem().getItem() instanceof SniperRifle) {
            if(player.isUsingItem()) {
                double scrollDelta = event.getScrollDeltaY();
                if(scrollDelta != 0) {
                    event.setCanceled(true);
                    ItemStack useStack = player.getUseItem();
                    double zoom = useStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getDouble("zoom") + scrollDelta * SniperRifle.getZoomScrollSpeed();
                    if(zoom > SniperRifle.MAX_ZOOM) zoom = SniperRifle.MAX_ZOOM;
                    else if(zoom < 1) zoom = 1;
                    double finalZoom = zoom;
                    CustomData.update(DataComponents.CUSTOM_DATA, useStack, tag -> tag.putDouble("zoom", finalZoom));

                    MutableComponent displayText = Component.empty();
                    displayText.append(Component.translatable("tooltip.lzxnoneterraria.zoom").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD))
                        .append(Component.literal(": ").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD))
                        .append(Component.literal(String.format("x%.1f", finalZoom)).withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
                    player.displayClientMessage(displayText, true);
                    SoundUtil.playClientSound(player, SoundEvents.SPYGLASS_USE, 1.0f, 1.0f);
                }
            }
        }else if(mainHand.getItem() instanceof FirstFractal) {
            if(player.isUsingItem() && (player.getUseItem().is(ModItems.FIRST_FRACTAL.get()))) {
                double scrollDelta = event.getScrollDeltaY();
                if(scrollDelta != 0) {
                    event.setCanceled(true);
                    double deltaDist = mainHand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                            .copyTag().getDouble("deltaDist");

                    double finalDelta = deltaDist + scrollDelta * FirstFractal.getScrollSpeed();

                    if(FirstFractal.isDistanceMode()) {
                        if(finalDelta > FirstFractal.getMaxRange()) finalDelta = FirstFractal.getMaxRange();
                        else if(finalDelta < -FirstFractal.getMaxRange()) finalDelta = -FirstFractal.getMaxRange();
                    }else {
                        if(finalDelta > FirstFractal.getMaxRange()) finalDelta = FirstFractal.getMaxRange();
                        else if(finalDelta < 0) finalDelta = 0;
                    }
                    MutableComponent displayText = Component.empty();
                    displayText.append(Component.translatable(FirstFractal.isDistanceMode() ? "tooltip.lzxnoneterraria.relative_distance" : "tooltip.lzxnoneterraria.absolute_distance").withStyle(ChatFormatting.WHITE))
                         .append(Component.literal(": ").withStyle(ChatFormatting.WHITE))
                         .append(Component.literal(String.format("%.2f", finalDelta)).withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
                    player.displayClientMessage(displayText, true);

                    PacketDistributor.sendToServer(new FirstFractalScrollPayload(scrollDelta));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while(ModKeyBindings.KILL_MODE.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            if(mc.player != null) {
                Player player = mc.player;
                ItemStack stack = player.getMainHandItem();
                if(stack.is(ModItems.DEVILS_DEVASTATION.get())) {
                    MobEffectInstance instance = player.getEffect(ModEffects.KILL_MODE_COOLDOWN);
                    MobEffectInstance instance2 = player.getEffect(ModEffects.KILL_MODE);
                    if(instance == null && instance2 == null) {
                        PacketDistributor.sendToServer(new DevilsDevastationKillModePayload());
                        DevilsDevastation.enterIntoKillMode(player);
                    }
                }
            }
        }
        while(ModKeyBindings.CLEAR_SUMMON.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            if(mc.player != null) {
                Player player = mc.player;
                ItemStack stack = player.getMainHandItem();
                if(stack.getItem() instanceof SummonWeapon weapon) {
                    PacketDistributor.sendToServer(new ClearSummonPayload(weapon.getSummonId()));
                }
            }
        }
        while(ModKeyBindings.CLEAR_ALL_SUMMONS.consumeClick()) {
            PacketDistributor.sendToServer(new ClearSummonPayload(""));
        }
        while(ModKeyBindings.TOGGLE_SUMMON_FREE_TARGETING.consumeClick()) {
            PacketDistributor.sendToServer(new ToggleSummonFreeTargetingPayload(true));
        }
    }
}
