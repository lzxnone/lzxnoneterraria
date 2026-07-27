package com.lzxnone.terraria.item.ammo;

import com.lzxnone.terraria.entity.summon.IStaticSummonBehavior;

public class PartyBullet extends BasicBulletAmmo {
    public static final float BASE_DAMAGE = 0.8f;
    public static final double SPEED = 3.0D;
    public static final IStaticSummonBehavior SUMMON_BEHAVIOR = createSummonBehavior(BASE_DAMAGE, SPEED);

}
