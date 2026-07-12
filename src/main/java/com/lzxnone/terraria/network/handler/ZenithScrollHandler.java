package com.lzxnone.terraria.network.handler;

import com.lzxnone.terraria.Config;
import com.lzxnone.terraria.item.weapon.melee.TrueCopperShortsword;
import com.lzxnone.terraria.item.weapon.melee.Zenith;
import com.lzxnone.terraria.network.payload.ZenithScrollPayload;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ZenithScrollHandler {
    public static void handle(final ZenithScrollPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            if(level.isClientSide()) return;
            Player player = context.player();
            ItemStack weapon = player.getMainHandItem();
            if(weapon.getItem() instanceof Zenith || weapon.getItem() instanceof TrueCopperShortsword) {
                double scrollDelta = payload.scrollDelta();
                double deltaDist = weapon.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                        .copyTag().getDouble("deltaDist");
                double finalDelta = deltaDist + scrollDelta * Config.zenithMouseSpeed;

                if(Config.zenithDistanceMode) {

                    if(finalDelta > Config.zenithMaxRange) finalDelta = Config.zenithMaxRange;
                    else if(finalDelta < -Config.zenithMaxRange) finalDelta = -Config.zenithMaxRange;
                }else {
                    if(finalDelta > Config.zenithMaxRange) finalDelta = Config.zenithMaxRange;
                    else if(finalDelta < 0) finalDelta = 0;
                }

                double finalDelta1 = finalDelta;
                CustomData.update(DataComponents.CUSTOM_DATA, weapon,
                    tag -> tag.putDouble("deltaDist", finalDelta1));
            }
        });
    }
}
