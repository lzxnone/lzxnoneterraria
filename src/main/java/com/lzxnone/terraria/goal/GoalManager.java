package com.lzxnone.terraria.goal;

import com.lzxnone.terraria.LzxnoneTerraria;
import net.minecraft.world.entity.PathfinderMob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = LzxnoneTerraria.MODID)
public class GoalManager {
    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if(!event.getLevel().isClientSide() && event.getEntity() instanceof PathfinderMob mob) {
            mob.goalSelector.addGoal(0, new ConfusedEffectGoal(mob));
        }
    }
}
