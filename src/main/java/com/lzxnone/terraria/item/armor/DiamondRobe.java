package com.lzxnone.terraria.item.armor;

import com.lzxnone.terraria.item.weapon.magic.gem_staff.GemStaff;

public class DiamondRobe extends GemRobe {
    public static final int MAX_MANA_BONUS = 80;
    public static final double MANA_COST_MULTIPLIER = 0.85D;
    public static final int SKILL_MASK = GemStaff.SKILL_DIAMOND;

    public DiamondRobe() {
        super(ModArmorMaterials.DIAMOND_ROBE, MAX_MANA_BONUS, MANA_COST_MULTIPLIER, SKILL_MASK);
    }
}
