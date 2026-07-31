package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class IchorEffect extends MobEffect {
    public static final ConfigFloat DAMAGE_BONUS_PER_LEVEL = new ConfigFloat("effect.ichor.damage_bonus_per_level", "ichor_damage_bonus_per_level", 0.15f, 0.0f, 10.0f);

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DAMAGE_BONUS_PER_LEVEL);
        }
    };


    public static float getDamageBonusPerLevel() {
        return DAMAGE_BONUS_PER_LEVEL.get();
    }

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "ichor",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/mob_effect/ichor.png"),
        Component.translatable("effect.lzxnoneterraria.ichor"),
        CONFIG_DATA
    );

    public IchorEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFFF00);
    }
}
