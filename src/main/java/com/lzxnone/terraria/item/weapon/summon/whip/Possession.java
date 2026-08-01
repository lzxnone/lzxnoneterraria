package com.lzxnone.terraria.item.weapon.summon.whip;

import com.lzxnone.terraria.entity.summon.StaticSummon;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;

public class Possession extends Whip {
    private static final double RANGE = 32.0D;
    private static final float DAMAGE = 5.0F;
    private static final int ROTATE = 60;

    public Possession() {
        super(Tiers.NETHERITE, new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.RARE));
    }

    @Override
    protected double getRange() {
        return RANGE;
    }

    @Override
    protected String getRes(StaticSummon summon) {
        return "lzxnoneterraria:textures/vfx/possession_projectile" + summon.getRandom().nextInt(3) + ".png";
    }

    @Override
    protected float getDamage() {
        return DAMAGE;
    }

    @Override
    protected int getLifetime() {
        return 10;
    }

    @Override
    protected int getRotateAngle() {
        return ROTATE;
    }

    @Override
    protected int getCooldown() {
        return 3;
    }

    @Override
    protected int getInvulnerableTime() {
        return 12;
    }
}
