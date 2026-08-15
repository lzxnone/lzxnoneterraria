package com.lzxnone.terraria.network.handler;

import com.lzxnone.terraria.attachment.PlayerSummon;
import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.entity.summon.StaticSummonBehaviors;
import com.lzxnone.terraria.network.PlayerSummonSync;
import com.lzxnone.terraria.network.payload.ClearSummonPayload;
import net.minecraft.ChatFormatting;
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
            List<PlayerSummon.SummonSlot> slots = player.getData(ModAttachments.PLAYER_SUMMON).getMinionSlots();
            List<PlayerSummon.SummonSlot> remainingSlots = new ArrayList<>();

            for(PlayerSummon.SummonSlot slot : slots) {
                boolean clearSlot = clearAll || slot.getId().equals(id);

                if(clearSlot) {
                    for(UUID uuid : slot.getSummons()) {
                        Entity entity = serverLevel.getEntity(uuid);
                        if(entity != null) entity.discard();
                    }
                }else {
                    List<UUID> aliveSlot = slot.getSummons().stream()
                        .filter(uuid -> {
                            Entity entity = serverLevel.getEntity(uuid);
                            return entity != null && entity.isAlive();
                        })
                        .toList();
                    if(!aliveSlot.isEmpty()) remainingSlots.add(new PlayerSummon.SummonSlot(slot.getId(), aliveSlot));
                }
            }

            PlayerSummon summonData = player.getData(ModAttachments.PLAYER_SUMMON);
            summonData.setMinionSlots(remainingSlots);
            PlayerSummonSync.setAndSync(player, summonData);
            player.displayClientMessage(
                getClearMessage(clearAll, id),
                true
            );
        });
    };

    private static Component getClearMessage(boolean clearAll, String id) {
        if(clearAll) {
            return Component.translatable("message.lzxnoneterraria.clear_all_summons")
                .withStyle(ChatFormatting.GREEN);
        }
        return Component.translatable("message.lzxnoneterraria.clear_summon", getSummonName(id).copy().withStyle(ChatFormatting.RED))
            .withStyle(ChatFormatting.GREEN);
    }

    private static Component getSummonName(String id) {
        if(StaticSummonBehaviors.TERRAPRISMA.equals(id)) {
            return Component.translatable("item.lzxnoneterraria.terraprisma");
        }
        if(StaticSummonBehaviors.STARDUST_DRAGON_STAFF.equals(id)) {
            return Component.translatable("item.lzxnoneterraria.stardust_dragon_staff");
        }
        if(StaticSummonBehaviors.STARDUST_CELL_STAFF.equals(id)) {
            return Component.translatable("item.lzxnoneterraria.stardust_cell_staff");
        }
        return Component.literal(id);
    }
}
