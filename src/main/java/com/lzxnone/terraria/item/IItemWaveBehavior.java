package com.lzxnone.terraria.item;

import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public interface IItemWaveBehavior {
    default void onLeftClickAir(PlayerInteractEvent.LeftClickEmpty event) {}
    default void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {}
    default void onAttackEntity(AttackEntityEvent event) {}
}
