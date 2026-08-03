package com.lzxnone.terraria.item.potion;

import com.lzxnone.terraria.effect.ModEffects;
import com.lzxnone.terraria.effect.SummonEffect;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;

public class SummoningPotion extends Potion {
    public static final int EFFECT_DURATION = 8 * 60 * 20;

    @Override
    protected boolean onDrink(ServerPlayer player, ItemStack stack) {
        player.addEffect(new MobEffectInstance(ModEffects.SUMMON, EFFECT_DURATION, 0));
        return true;
    }

    @Override
    protected String getTooltipKey() {
        return "tooltip.lzxnoneterraria.summoning_potion";
    }

    @Override
    protected Object[] getTooltipArgs() {
        return new Object[]{SummonEffect.getSummonCountPerLevel()};
    }
}
