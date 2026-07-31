package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.attachment.ModAttachments;
import com.lzxnone.terraria.attachment.PlayerMana;
import com.lzxnone.terraria.event.PlayerManaSyncEventHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ManaEffect extends MobEffect {
    private static final int[] RECOVER_AMOUNTS = {50, 100, 200, 300};

    public ManaEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x00D7FF);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public void applyInstantenousEffect(Entity source, Entity indirectSource, LivingEntity livingEntity, int amplifier, double health) {
        if(!(livingEntity instanceof ServerPlayer player)) return;

        PlayerMana mana = player.getData(ModAttachments.PLAYER_MANA);
        int oldMana = mana.getMana();
        mana.recoverManaImmediately(getRecoverAmount(amplifier));
        ManaSicknessEffect.applyTo(player);
        player.setData(ModAttachments.PLAYER_MANA, mana);
        if(mana.getMana() != oldMana) {
            PlayerManaSyncEventHandler.sync(player);
        }
    }

    public static int getRecoverAmount(int amplifier) {
        int index = Math.clamp(amplifier, 0, RECOVER_AMOUNTS.length - 1);
        return RECOVER_AMOUNTS[index];
    }
}
