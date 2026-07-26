package com.lzxnone.terraria.network.handler;

import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.entity.summon.StaticSummon;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.item.weapon.SummonWeapon;
import com.lzxnone.terraria.network.payload.ClearSummonPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClearSummonHandler {
    public static void handle(final ClearSummonPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            if(level.isClientSide() || !(level instanceof ServerLevel serverLevel)) return;
            Player player = context.player();
            String id = payload.id();
            boolean clearAll = id.isEmpty();
            List<UUID> summons = SummonWeapon.getSummons(player);
            List<UUID> remainingSummons = new ArrayList<>();

            for(UUID uuid : summons) {
                Entity entity = serverLevel.getEntity(uuid);
                if(entity == null || !entity.isAlive()) continue;

                if(clearAll || (entity instanceof StaticSummon summon && summon.getEntityData().get(StaticSummon.BEHAVIOR).equals(id))) {
                    entity.discard();
                }else {
                    remainingSummons.add(uuid);
                }
            }

            player.setData(ModAttachments.SUMMON_WEAPON_SUMMONS, remainingSummons);
            player.displayClientMessage(
                clearAll
                    ? Component.translatable("message.lzxnoneterraria.clear_all_summons")
                    : Component.translatable("message.lzxnoneterraria.clear_summon", getSummonName(id)),
                true
            );
        });
    };

    private static Component getSummonName(String id) {
        if(StaticSummonBehaviors.TERRAPRISMA.equals(id)) {
            return Component.translatable("item.lzxnoneterraria.terraprisma");
        }
        return Component.literal(id);
    }
}
