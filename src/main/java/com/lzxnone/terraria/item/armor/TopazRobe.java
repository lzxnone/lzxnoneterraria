package com.lzxnone.terraria.item.armor;

import com.lzxnone.terraria.item.weapon.magic.gem_staff.GemStaff;

public class TopazRobe extends GemRobe {
    public static final int MAX_MANA_BONUS = 40;
    public static final double MANA_COST_MULTIPLIER = 0.93D;
    public static final int SKILL_MASK = GemStaff.SKILL_TOPAZ;

    public TopazRobe() {
        super(ModArmorMaterials.TOPAZ_ROBE, MAX_MANA_BONUS, MANA_COST_MULTIPLIER, SKILL_MASK);
    }
}
