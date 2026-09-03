package com.lzxnone.terraria.item.armor;

import com.lzxnone.terraria.item.weapon.magic.gem_staff.GemStaff;

public class AmethystRobe extends GemRobe {
    public static final int MAX_MANA_BONUS = 20;
    public static final double MANA_COST_MULTIPLIER = 0.95D;
    public static final int SKILL_MASK = GemStaff.SKILL_AMETHYST;

    public AmethystRobe() {
        super(ModArmorMaterials.AMETHYST_ROBE, MAX_MANA_BONUS, MANA_COST_MULTIPLIER, SKILL_MASK);
    }
}
