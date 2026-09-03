package com.lzxnone.terraria.item.armor;

import com.lzxnone.terraria.item.weapon.magic.gem_staff.GemStaff;

public class RubyRobe extends GemRobe {
    public static final int MAX_MANA_BONUS = 60;
    public static final double MANA_COST_MULTIPLIER = 0.87D;
    public static final int SKILL_MASK = GemStaff.SKILL_RUBY;

    public RubyRobe() {
        super(ModArmorMaterials.RUBY_ROBE, MAX_MANA_BONUS, MANA_COST_MULTIPLIER, SKILL_MASK);
    }
}
