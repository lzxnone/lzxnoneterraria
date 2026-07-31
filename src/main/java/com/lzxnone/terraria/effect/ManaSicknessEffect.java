package com.lzxnone.terraria.effect;

import com.lzxnone.terraria.LzxnoneTerraria;
import com.lzxnone.terraria.ui.config.ConfigListItem;
import com.lzxnone.terraria.ui.config.IConfigData;
import com.lzxnone.terraria.ui.config.struct.ConfigFloat;
import com.lzxnone.terraria.ui.config.struct.ConfigInt;
import com.lzxnone.terraria.ui.config.struct.ConfigStruct;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class ManaSicknessEffect extends MobEffect {
    public static final ConfigInt DURATION_PER_USE = new ConfigInt("effect.mana_sickness.duration_per_use", "mana_sickness_duration_per_use", 100, 0, 1200);
    public static final ConfigInt MAX_DURATION = new ConfigInt("effect.mana_sickness.max_duration", "mana_sickness_max_duration", 200, 0, 2400);
    public static final ConfigFloat MAX_DAMAGE_REDUCTION = new ConfigFloat("effect.mana_sickness.max_damage_reduction", "mana_sickness_max_damage_reduction", 0.5F, 0.0F, 1.0F);

    public static final IConfigData CONFIG_DATA = new IConfigData() {
        @Override
        public void onConfigLoad() {
            ConfigStruct.loadAll(DURATION_PER_USE, MAX_DURATION, MAX_DAMAGE_REDUCTION);
        }
    };

    public static final ConfigListItem CONFIG_LIST_ITEM = new ConfigListItem(
        "mana_sickness",
        ResourceLocation.fromNamespaceAndPath(LzxnoneTerraria.MODID, "textures/mob_effect/mana_sickness.png"),
        Component.translatable("effect.lzxnoneterraria.mana_sickness"),
        CONFIG_DATA
    );

    public ManaSicknessEffect() {
        super(MobEffectCategory.HARMFUL, 0x2BC4D8);
    }

    public static void applyTo(LivingEntity entity) {
        int durationPerUse = DURATION_PER_USE.get();
        int maxDuration = MAX_DURATION.get();
        if(durationPerUse <= 0 || maxDuration <= 0) return;

        MobEffectInstance current = entity.getEffect(ModEffects.MANA_SICKNESS);
        int duration = Math.min(maxDuration, durationPerUse);
        if(current != null) {
            duration = Math.min(maxDuration, current.getDuration() + durationPerUse);
        }
        entity.addEffect(new MobEffectInstance(ModEffects.MANA_SICKNESS, duration, 0, false, true, true));
    }

    public static float getMagicDamageMultiplier(LivingEntity entity) {
        MobEffectInstance instance = entity.getEffect(ModEffects.MANA_SICKNESS);
        if(instance == null) return 1.0F;

        int maxDuration = MAX_DURATION.get();
        if(maxDuration <= 0) return 1.0F;

        float durationRatio = Math.min(1.0F, (float) instance.getDuration() / maxDuration);
        float reduction = Math.min(1.0F, MAX_DAMAGE_REDUCTION.get()) * durationRatio;
        return Math.max(0.0F, 1.0F - reduction);
    }
}
