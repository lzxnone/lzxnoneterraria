package com.lzxnone.terraria.network.handler;

import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.weapon.melee.DevilsDevastation;
import com.lzxnone.terraria.network.payload.DevilsDevastationKillModePayload;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class DevilsDevastationKillModeHandler {
    public static void handle(final DevilsDevastationKillModePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            if (level.isClientSide()) return;
            Player player = context.player();
            ItemStack stack = player.getMainHandItem();
            if(stack.is(ModItems.DEVILS_DEVASTATION.get())) {
                MobEffectInstance instance = player.getEffect(ModEffects.KILL_MODE_COOLDOWN);
                MobEffectInstance instance2 = player.getEffect(ModEffects.KILL_MODE);
                if(instance == null && instance2 == null) {
                    MobEffectInstance effectInstance = new MobEffectInstance(ModEffects.KILL_MODE, DevilsDevastation.getKillModeTime(), 0);
                    player.addEffect(effectInstance);
                    player.setData(ModAttachments.DEVILS_DEVASTATION_HIT_COUNT, 0);
                }
            }
        });
    };
}
